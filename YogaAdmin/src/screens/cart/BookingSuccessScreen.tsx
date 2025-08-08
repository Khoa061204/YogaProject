import React from 'react';
import { StyleSheet, View } from 'react-native';
import { Text, Button } from 'react-native-paper';
import Icon from 'react-native-vector-icons/MaterialCommunityIcons';

interface BookingSuccessScreenProps {
  navigation: any;
}

export const BookingSuccessScreen: React.FC<BookingSuccessScreenProps> = ({
  navigation,
}) => {
  return (
    <View style={styles.container}>
      <Icon name="check-circle" size={80} color="#4CAF50" />
      <Text style={styles.title}>Booking Successful!</Text>
      <Text style={styles.message}>
        Your classes have been booked successfully. You can view your bookings in
        the Booking History section.
      </Text>
      <View style={styles.buttonContainer}>
        <Button
          mode="contained"
          onPress={() => navigation.navigate('BookingHistory')}
          style={styles.button}
        >
          View Bookings
        </Button>
        <Button
          mode="outlined"
          onPress={() => navigation.navigate('Classes')}
          style={styles.button}
        >
          Browse More Classes
        </Button>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 16,
    backgroundColor: '#fff',
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginTop: 16,
    marginBottom: 8,
  },
  message: {
    textAlign: 'center',
    marginBottom: 24,
    color: '#666',
  },
  buttonContainer: {
    width: '100%',
    maxWidth: 300,
  },
  button: {
    marginTop: 8,
  },
});