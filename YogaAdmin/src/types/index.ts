export interface YogaCourse {
  id: number;
  dayOfWeek: string;
  time: string;
  price: number;
  capacity: number;
  duration: number;
  type: string;
  description?: string;
  isActive: boolean;
  difficulty?: string;
  equipment?: string;
}

export interface Schedule {
  id: number;
  date: string;
  teacher: string;
  comments?: string;
  yogaCourseId: number;
  currentEnrollment: number;
  isCancelled: boolean;
}

export interface CartItem {
  schedule: Schedule;
  course: YogaCourse;
}

export interface Booking {
  id: string;
  userId: string;
  userEmail: string;
  scheduleId: number;
  bookingDate: string;
}