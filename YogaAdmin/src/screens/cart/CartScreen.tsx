import React, { useState, useEffect, useCallback } from 'react';
import { StyleSheet, View, FlatList } from 'react-native';
import { Text, Button, Portal, Dialog, ActivityIndicator } from 'react-native-paper';
import { CartItem as CartItemComponent } from '../../components';
import { cartService } from '../../services/cart';
import { CartItem } from '../../types';
import auth from '@react-native-firebase/auth';
import database from '@react-native-firebase/database';

interface CartScreenProps {
  navigation: any;
}

export const CartScreen: React.FC<CartScreenProps> = ({ navigation }) => {
  const [cartItems, setCartItems] = useState<CartItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [processing, setProcessing] = useState(false);
  const [showConfirmDialog, setShowConfirmDialog] = useState(false);
  const [error, setError] = useState('');

  const loadCart = async () => {
    try {
      const items = await cartService.getCart();
      setCartItems(items);
      setError('');
    } catch (err) {
      setError('Failed to load cart');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadCart();
  }, []);

  const handleRemoveItem = async (scheduleId: number) => {
    await cartService.removeFromCart(scheduleId);
    loadCart();
  };

  const calculateTotal = useCallback(() => {
    return cartItems.reduce((sum, item) => sum + item.course.price, 0);
  }, [cartItems]);

  const handleCheckout = async () => {
    if (!auth().currentUser) {
      navigation.navigate('Login');
      return;
    }
    setShowConfirmDialog(true);
  };

  const processBookings = async () => {
    setProcessing(true);
    setError('');

    try {
      const user = auth().currentUser;
      if (!user) throw new Error('User not authenticated');

      const bookingsRef = database().ref('bookings');
      const schedulesRef = database().ref('schedules');

      // Process each booking in sequence
      for (const item of cartItems) {
        // Check if class is still available
        const scheduleSnapshot = await schedulesRef
          .child(item.schedule.id.toString())
          .once('value');
        
        const schedule = scheduleSnapshot.val();
        if (!schedule) {
          throw new Error(`Class ${item.course.type} is no longer available`);
        }

        if (schedule.currentEnrollment >= item.course.capacity) {
          throw new Error(`Class ${item.course.type} is now full`);
        }

        // Create booking
        const bookingRef = await bookingsRef.push({
          userId: user.uid,
          userEmail: user.email,
          scheduleId: item.schedule.id,
          courseId: item.course.id,
          bookingDate: new Date().toISOString(),
        });

        // Update enrollment count
        await schedulesRef
          .child(item.schedule.id.toString())
          .update({
            currentEnrollment: schedule.currentEnrollment + 1,
          });
      }

      // Clear cart after successful booking
      await cartService.clearCart();
      setCartItems([]);
      navigation.navigate('BookingSuccess');

    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to process booking');
    } finally {
      setProcessing(false);
      setShowConfirmDialog(false);
    }
  };

  if (loading) {
    return (
      <View style={styles.centerContainer}>
        <ActivityIndicator size="large" />
      </View>
    );
  }

  return (
    <View style={styles.container}>
      {cartItems.length > 0 ? (
        <>
          <FlatList
            data={cartItems}
            renderItem={({ item }) => (
              <CartItemComponent
                item={item}
                onRemove={() => handleRemoveItem(item.schedule.id)}
              />
            )}
            keyExtractor={(item) => item.schedule.id.toString()}
            contentContainerStyle={styles.listContent}
          />

          <View style={styles.footer}>
            <Text style={styles.total}>
              Total: £{calculateTotal().toFixed(2)}
            </Text>
            <Button
              mode="contained"
              onPress={handleCheckout}
              disabled={processing}
              style={styles.checkoutButton}
            >
              {processing ? 'Processing...' : 'Checkout'}
            </Button>
          </View>
        </>
      ) : (
        <View style={styles.emptyContainer}>
          <Text>Your cart is empty</Text>
          <Button
            mode="contained"
            onPress={() => navigation.navigate('Classes')}
            style={styles.browseButton}
          >
            Browse Classes
          </Button>
        </View>
      )}

      <Portal>
        <Dialog
          visible={showConfirmDialog}
          onDismiss={() => setShowConfirmDialog(false)}
        >
          <Dialog.Title>Confirm Booking</Dialog.Title>
          <Dialog.Content>
            <Text>
              You are about to book {cartItems.length} class{cartItems.length > 1 ? 'es' : ''}.
              Total amount: £{calculateTotal().toFixed(2)}
            </Text>
            {error ? <Text style={styles.errorText}>{error}</Text> : null}
          </Dialog.Content>
          <Dialog.Actions>
            <Button onPress={() => setShowConfirmDialog(false)}>Cancel</Button>
            <Button
              mode="contained"
              onPress={processBookings}
              disabled={processing}
            >
              Confirm
            </Button>
          </Dialog.Actions>
        </Dialog>
      </Portal>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  centerContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  listContent: {
    paddingVertical: 8,
  },
  footer: {
    backgroundColor: '#fff',
    padding: 16,
    elevation: 8,
  },
  total: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 8,
  },
  checkoutButton: {
    marginTop: 8,
  },
  emptyContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 16,
  },
  browseButton: {
    marginTop: 16,
  },
  errorText: {
    color: '#B00020',
    marginTop: 8,
  },
});