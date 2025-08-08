import React, { useState, useEffect, useCallback } from 'react';
import { StyleSheet, View, RefreshControl, ScrollView } from 'react-native';
import { Text, ActivityIndicator, Button, Portal, Dialog } from 'react-native-paper';
import { BookingItem } from '../../components';
import { bookingService, BookingWithDetails } from '../../services/bookings';
import { createMaterialTopTabNavigator } from '@react-navigation/material-top-tabs';

const Tab = createMaterialTopTabNavigator();

interface BookingListProps {
  bookings: BookingWithDetails[];
  onCancelBooking: (booking: BookingWithDetails) => void;
  refreshing: boolean;
  onRefresh: () => void;
}

const BookingList: React.FC<BookingListProps> = ({
  bookings,
  onCancelBooking,
  refreshing,
  onRefresh,
}) => {
  if (bookings.length === 0) {
    return (
      <View style={styles.emptyContainer}>
        <Text>No bookings found</Text>
      </View>
    );
  }

  return (
    <ScrollView
      contentContainerStyle={styles.listContent}
      refreshControl={
        <RefreshControl refreshing={refreshing} onRefresh={onRefresh} />
      }
    >
      {bookings.map((booking) => (
        <BookingItem
          key={booking.id}
          booking={booking}
          schedule={booking.schedule}
          course={booking.course}
          onCancel={() => onCancelBooking(booking)}
        />
      ))}
    </ScrollView>
  );
};

const UpcomingBookings: React.FC<BookingListProps> = (props) => {
  return <BookingList {...props} />;
};

const PastBookings: React.FC<BookingListProps> = (props) => {
  return <BookingList {...props} />;
};

export const BookingHistoryScreen: React.FC = () => {
  const [upcomingBookings, setUpcomingBookings] = useState<BookingWithDetails[]>([]);
  const [pastBookings, setPastBookings] = useState<BookingWithDetails[]>([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState('');
  const [selectedBooking, setSelectedBooking] = useState<BookingWithDetails | null>(null);
  const [showCancelDialog, setShowCancelDialog] = useState(false);
  const [cancelling, setCancelling] = useState(false);

  const loadBookings = async () => {
    try {
      const [upcoming, past] = await Promise.all([
        bookingService.getUpcomingBookings(),
        bookingService.getPastBookings(),
      ]);
      setUpcomingBookings(upcoming);
      setPastBookings(past);
      setError('');
    } catch (err) {
      setError('Failed to load bookings');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadBookings();
  }, []);

  const onRefresh = useCallback(async () => {
    setRefreshing(true);
    await loadBookings();
    setRefreshing(false);
  }, []);

  const handleCancelBooking = (booking: BookingWithDetails) => {
    setSelectedBooking(booking);
    setShowCancelDialog(true);
  };

  const confirmCancelBooking = async () => {
    if (!selectedBooking) return;

    setCancelling(true);
    try {
      await bookingService.cancelBooking(
        selectedBooking.id,
        selectedBooking.schedule.id
      );
      await loadBookings();
    } catch (err) {
      setError('Failed to cancel booking');
    } finally {
      setCancelling(false);
      setShowCancelDialog(false);
      setSelectedBooking(null);
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
    <>
      <Tab.Navigator
        screenOptions={{
          tabBarLabelStyle: styles.tabLabel,
          tabBarIndicatorStyle: styles.tabIndicator,
        }}
      >
        <Tab.Screen name="Upcoming" options={{ title: 'Upcoming' }}>
          {() => (
            <UpcomingBookings
              bookings={upcomingBookings}
              onCancelBooking={handleCancelBooking}
              refreshing={refreshing}
              onRefresh={onRefresh}
            />
          )}
        </Tab.Screen>
        <Tab.Screen name="Past" options={{ title: 'Past' }}>
          {() => (
            <PastBookings
              bookings={pastBookings}
              onCancelBooking={handleCancelBooking}
              refreshing={refreshing}
              onRefresh={onRefresh}
            />
          )}
        </Tab.Screen>
      </Tab.Navigator>

      <Portal>
        <Dialog
          visible={showCancelDialog}
          onDismiss={() => setShowCancelDialog(false)}
        >
          <Dialog.Title>Cancel Booking</Dialog.Title>
          <Dialog.Content>
            <Text>
              Are you sure you want to cancel this booking? This action cannot be
              undone.
            </Text>
            {error ? <Text style={styles.errorText}>{error}</Text> : null}
          </Dialog.Content>
          <Dialog.Actions>
            <Button onPress={() => setShowCancelDialog(false)}>No</Button>
            <Button
              mode="contained"
              onPress={confirmCancelBooking}
              loading={cancelling}
              disabled={cancelling}
            >
              Yes, Cancel
            </Button>
          </Dialog.Actions>
        </Dialog>
      </Portal>
    </>
  );
};

const styles = StyleSheet.create({
  centerContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  listContent: {
    flexGrow: 1,
    padding: 16,
  },
  emptyContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 16,
  },
  errorText: {
    color: '#B00020',
    marginTop: 8,
  },
  tabLabel: {
    textTransform: 'none',
    fontWeight: 'bold',
  },
  tabIndicator: {
    backgroundColor: '#6200ee',
  },
});