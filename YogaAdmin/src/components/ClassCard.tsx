import React from 'react';
import { StyleSheet, View } from 'react-native';
import { Card, Title, Paragraph, Button, Chip } from 'react-native-paper';
import { YogaCourse } from '../types';

interface ClassCardProps {
  course: YogaCourse;
  onAddToCart?: () => void;
  onViewDetails?: () => void;
}

export const ClassCard: React.FC<ClassCardProps> = ({
  course,
  onAddToCart,
  onViewDetails,
}) => {
  return (
    <Card style={styles.card}>
      <Card.Content>
        <Title>{course.type}</Title>
        <View style={styles.detailsRow}>
          <Paragraph>{`${course.dayOfWeek} at ${course.time}`}</Paragraph>
          <Chip icon="clock">{`${course.duration} mins`}</Chip>
        </View>
        {course.difficulty && (
          <Chip icon="stairs" style={styles.chip}>{course.difficulty}</Chip>
        )}
        <Paragraph style={styles.price}>£{course.price.toFixed(2)}</Paragraph>
        {course.description && (
          <Paragraph style={styles.description}>{course.description}</Paragraph>
        )}
      </Card.Content>
      <Card.Actions style={styles.actions}>
        <Button onPress={onViewDetails}>View Details</Button>
        <Button mode="contained" onPress={onAddToCart}>
          Add to Cart
        </Button>
      </Card.Actions>
    </Card>
  );
};

const styles = StyleSheet.create({
  card: {
    marginHorizontal: 16,
    marginVertical: 8,
    elevation: 4,
  },
  detailsRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginVertical: 8,
  },
  chip: {
    marginVertical: 4,
    alignSelf: 'flex-start',
  },
  price: {
    fontSize: 18,
    fontWeight: 'bold',
    marginVertical: 8,
  },
  description: {
    marginTop: 8,
    color: '#666',
  },
  actions: {
    justifyContent: 'flex-end',
    paddingHorizontal: 16,
  },
});