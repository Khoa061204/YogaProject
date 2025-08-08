import database from '@react-native-firebase/database';
import { YogaCourse, Schedule } from '../types';

export const getYogaCourses = async (): Promise<YogaCourse[]> => {
  try {
    const snapshot = await database()
      .ref('courses')
      .orderByChild('isActive')
      .equalTo(true)
      .once('value');
    
    const courses: YogaCourse[] = [];
    snapshot.forEach((child) => {
      courses.push({
        id: parseInt(child.key || '0'),
        ...child.val()
      });
    });
    
    return courses;
  } catch (error) {
    console.error('Error fetching courses:', error);
    return [];
  }
};

export const getSchedulesForCourse = async (courseId: number): Promise<Schedule[]> => {
  try {
    const snapshot = await database()
      .ref('schedules')
      .orderByChild('yogaCourseId')
      .equalTo(courseId)
      .once('value');
    
    const schedules: Schedule[] = [];
    snapshot.forEach((child) => {
      schedules.push({
        id: parseInt(child.key || '0'),
        ...child.val()
      });
    });
    
    return schedules;
  } catch (error) {
    console.error('Error fetching schedules:', error);
    return [];
  }
};

export const searchCourses = async (
  dayOfWeek?: string,
  timeRange?: { start: string; end: string }
): Promise<YogaCourse[]> => {
  try {
    let ref = database().ref('courses');
    
    if (dayOfWeek) {
      ref = ref.orderByChild('dayOfWeek').equalTo(dayOfWeek);
    }
    
    const snapshot = await ref.once('value');
    const courses: YogaCourse[] = [];
    
    snapshot.forEach((child) => {
      const course = {
        id: parseInt(child.key || '0'),
        ...child.val()
      } as YogaCourse;
      
      if (timeRange) {
        if (course.time >= timeRange.start && course.time <= timeRange.end) {
          courses.push(course);
        }
      } else {
        courses.push(course);
      }
    });
    
    return courses;
  } catch (error) {
    console.error('Error searching courses:', error);
    return [];
  }
};