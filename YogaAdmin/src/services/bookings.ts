import database from '@react-native-firebase/database';
import auth from '@react-native-firebase/auth';
import { Booking, Schedule, YogaCourse } from '../types';

export interface BookingWithDetails extends Booking {
  schedule: Schedule;
  course: YogaCourse;
}

export const bookingService = {
  getUserBookings: async (): Promise<BookingWithDetails[]> => {
    try {
      const user = auth().currentUser;
      if (!user) throw new Error('User not authenticated');

      // Get all bookings for the user
      const bookingsSnapshot = await database()
        .ref('bookings')
        .orderByChild('userId')
        .equalTo(user.uid)
        .once('value');

      const bookings: BookingWithDetails[] = [];
      
      // Process each booking
      for (const child of Object.entries(bookingsSnapshot.val() || {})) {
        const [bookingId, bookingData] = child;
        const booking = bookingData as Booking;

        // Get schedule details
        const scheduleSnapshot = await database()
          .ref('schedules')
          .child(booking.scheduleId.toString())
          .once('value');
        const schedule = scheduleSnapshot.val() as Schedule;
        if (!schedule) continue;

        // Get course details
        const courseSnapshot = await database()
          .ref('courses')
          .child(schedule.yogaCourseId.toString())
          .once('value');
        const course = courseSnapshot.val() as YogaCourse;
        if (!course) continue;

        bookings.push({
          ...booking,
          id: bookingId,
          schedule,
          course,
        });
      }

      // Sort by date, most recent first
      return bookings.sort((a, b) => {
        const dateA = new Date(a.schedule.date).getTime();
        const dateB = new Date(b.schedule.date).getTime();
        return dateB - dateA;
      });

    } catch (error) {
      console.error('Error fetching bookings:', error);
      throw error;
    }
  },

  cancelBooking: async (bookingId: string, scheduleId: number): Promise<void> => {
    try {
      const user = auth().currentUser;
      if (!user) throw new Error('User not authenticated');

      // Get current enrollment count
      const scheduleRef = database().ref('schedules').child(scheduleId.toString());
      const scheduleSnapshot = await scheduleRef.once('value');
      const schedule = scheduleSnapshot.val();
      
      if (!schedule) throw new Error('Schedule not found');

      // Update enrollment count and mark booking as cancelled
      await Promise.all([
        scheduleRef.update({
          currentEnrollment: Math.max(0, schedule.currentEnrollment - 1)
        }),
        database()
          .ref('bookings')
          .child(bookingId)
          .update({ cancelled: true, cancelledAt: new Date().toISOString() })
      ]);

    } catch (error) {
      console.error('Error cancelling booking:', error);
      throw error;
    }
  },

  getUpcomingBookings: async (): Promise<BookingWithDetails[]> => {
    const bookings = await bookingService.getUserBookings();
    const now = new Date();
    
    return bookings.filter(booking => {
      const classDate = new Date(booking.schedule.date);
      return classDate >= now && !booking.schedule.isCancelled;
    });
  },

  getPastBookings: async (): Promise<BookingWithDetails[]> => {
    const bookings = await bookingService.getUserBookings();
    const now = new Date();
    
    return bookings.filter(booking => {
      const classDate = new Date(booking.schedule.date);
      return classDate < now || booking.schedule.isCancelled;
    });
  }
};