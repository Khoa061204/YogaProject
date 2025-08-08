import React from 'react';
import { StyleSheet, View } from 'react-native';
import { Card, Title, Paragraph, Button, Badge } from 'react-native-paper';
import { Booking, Schedule, YogaCourse } from '../types';

interface BookingItemProps {
  booking: Booking;
  schedule: Schedule;
  course: YogaCourse;
  onCancel?: () => void;
}

export const BookingItem: React.FC<BookingItemProps> = ({
  booking,
  schedule,
  course,
  onCancel,
}) => {
  const isPast = new Date(schedule.date) < new Date();

  return (
    <Card style={styles.card}>
      <Card.Content>
        <View style={styles.header}>
          <Title>{course.type}</Title>
          {schedule.isCancelled && (
            <Badge size={24} style={styles.cancelledBadge}>
              Cancelled
            </Badge>
          )}
        </View>
        <Paragraph>{`Date: ${schedule.date}`}</Paragraph>
        <Paragraph>{`Teacher: ${schedule.teacher}`}</Paragraph>
        <Paragraph>{`Booked on: ${new Date(booking.bookingDate).toLocaleDateString()}`}</Paragraph>
        {schedule.comments && (
          <Paragraph style={styles.comments}>
            Note: {schedule.comments}
          </Paragraph>
        )}
      </Card.Content>
      {!isPast && !schedule.isCancelled && onCancel && (
        <Card.Actions style={styles.actions}>
          <Button mode="outlined" onPress={onCancel} textColor="#f44336">
            Cancel Booking
          </Button>
        </Card.Actions>
      )}
    </Card>
  );
};

const styles = StyleSheet.create({
  card: {
    marginHorizontal: 16,
    marginVertical: 8,
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 8,
  },
  cancelledBadge: {
    backgroundColor: '#f44336',
  },
  comments: {
    marginTop: 8,
    fontStyle: 'italic',
    color: '#666',
  },
  actions: {
    justifyContent: 'flex-end',
    paddingHorizontal: 16,
  },
});