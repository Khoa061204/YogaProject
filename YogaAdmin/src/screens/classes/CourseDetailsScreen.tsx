import React, { useState, useEffect } from 'react';
import { StyleSheet, View, ScrollView } from 'react-native';
import { Card, Title, Paragraph, Chip, Button, ActivityIndicator, Text } from 'react-native-paper';
import { getYogaCourses, getSchedulesForCourse } from '../../services/classes';
import { YogaCourse, Schedule } from '../../types';

interface CourseDetailsScreenProps {
  route: { params: { courseId: number } };
  navigation: any;
}

export const CourseDetailsScreen: React.FC<CourseDetailsScreenProps> = ({
  route,
  navigation,
}) => {
  const { courseId } = route.params;
  const [course, setCourse] = useState<YogaCourse | null>(null);
  const [schedules, setSchedules] = useState<Schedule[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    loadCourseDetails();
  }, [courseId]);

  const loadCourseDetails = async () => {
    try {
      const courses = await getYogaCourses();
      const foundCourse = courses.find(c => c.id === courseId);
      
      if (!foundCourse) {
        setError('Course not found');
        setLoading(false);
        return;
      }

      setCourse(foundCourse);
      
      const courseSchedules = await getSchedulesForCourse(courseId);
      setSchedules(courseSchedules.filter(s => !s.isCancelled));
      
      setError('');
    } catch (err) {
      setError('Failed to load course details');
    } finally {
      setLoading(false);
    }
  };

  const handleAddToCart = (schedule: Schedule) => {
    if (course) {
      navigation.navigate('Cart', {
        scheduleId: schedule.id,
        courseId: course.id,
      });
    }
  };

  if (loading) {
    return (
      <View style={styles.centerContainer}>
        <ActivityIndicator size="large" />
      </View>
    );
  }

  if (error || !course) {
    return (
      <View style={styles.centerContainer}>
        <Text style={styles.errorText}>{error || 'Course not found'}</Text>
      </View>
    );
  }

  return (
    <ScrollView style={styles.container}>
      <Card style={styles.card}>
        <Card.Content>
          <Title>{course.type}</Title>
          
          <View style={styles.detailsRow}>
            <Chip icon="calendar">{course.dayOfWeek}</Chip>
            <Chip icon="clock">{course.time}</Chip>
          </View>

          <View style={styles.detailsContainer}>
            <Paragraph style={styles.detail}>
              Duration: {course.duration} minutes
            </Paragraph>
            <Paragraph style={styles.detail}>
              Capacity: {course.capacity} people
            </Paragraph>
            <Paragraph style={styles.price}>
              £{course.price.toFixed(2)} per class
            </Paragraph>
          </View>

          {course.difficulty && (
            <Chip icon="stairs" style={styles.chip}>
              {course.difficulty}
            </Chip>
          )}

          {course.equipment && (
            <View style={styles.section}>
              <Title style={styles.sectionTitle}>Required Equipment</Title>
              <Paragraph>{course.equipment}</Paragraph>
            </View>
          )}

          {course.description && (
            <View style={styles.section}>
              <Title style={styles.sectionTitle}>Description</Title>
              <Paragraph>{course.description}</Paragraph>
            </View>
          )}
        </Card.Content>
      </Card>

      <View style={styles.section}>
        <Title style={styles.sectionTitle}>Available Classes</Title>
        {schedules.length > 0 ? (
          schedules.map((schedule) => (
            <Card key={schedule.id} style={styles.scheduleCard}>
              <Card.Content>
                <Paragraph>Date: {schedule.date}</Paragraph>
                <Paragraph>Teacher: {schedule.teacher}</Paragraph>
                <Paragraph>
                  Available Spots: {course.capacity - schedule.currentEnrollment}
                </Paragraph>
                {schedule.comments && (
                  <Paragraph style={styles.comments}>
                    Note: {schedule.comments}
                  </Paragraph>
                )}
              </Card.Content>
              <Card.Actions>
                <Button
                  mode="contained"
                  onPress={() => handleAddToCart(schedule)}
                  disabled={schedule.currentEnrollment >= course.capacity}
                >
                  {schedule.currentEnrollment >= course.capacity
                    ? 'Class Full'
                    : 'Add to Cart'}
                </Button>
              </Card.Actions>
            </Card>
          ))
        ) : (
          <Text style={styles.noSchedules}>
            No upcoming classes available
          </Text>
        )}
      </View>
    </ScrollView>
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
  card: {
    margin: 16,
    elevation: 4,
  },
  detailsRow: {
    flexDirection: 'row',
    gap: 8,
    marginVertical: 12,
  },
  detailsContainer: {
    marginVertical: 12,
  },
  detail: {
    marginVertical: 4,
  },
  price: {
    fontSize: 18,
    fontWeight: 'bold',
    marginTop: 8,
  },
  chip: {
    alignSelf: 'flex-start',
    marginVertical: 4,
  },
  section: {
    marginHorizontal: 16,
    marginVertical: 8,
  },
  sectionTitle: {
    fontSize: 20,
    marginBottom: 8,
  },
  scheduleCard: {
    marginVertical: 8,
  },
  comments: {
    fontStyle: 'italic',
    marginTop: 8,
  },
  noSchedules: {
    textAlign: 'center',
    marginVertical: 16,
    color: '#666',
  },
  errorText: {
    color: '#B00020',
    textAlign: 'center',
  },
});