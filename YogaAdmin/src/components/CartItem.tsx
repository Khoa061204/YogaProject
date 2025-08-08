import React from 'react';
import { StyleSheet, View } from 'react-native';
import { Card, Title, Paragraph, Button, IconButton } from 'react-native-paper';
import { CartItem as CartItemType } from '../types';

interface CartItemProps {
  item: CartItemType;
  onRemove: () => void;
}

export const CartItem: React.FC<CartItemProps> = ({ item, onRemove }) => {
  return (
    <Card style={styles.card}>
      <Card.Content>
        <View style={styles.header}>
          <Title>{item.course.type}</Title>
          <IconButton
            icon="close"
            size={20}
            onPress={onRemove}
          />
        </View>
        <Paragraph>{`Date: ${item.schedule.date}`}</Paragraph>
        <Paragraph>{`Teacher: ${item.schedule.teacher}`}</Paragraph>
        <Paragraph style={styles.price}>
          £{item.course.price.toFixed(2)}
        </Paragraph>
        {item.schedule.comments && (
          <Paragraph style={styles.comments}>
            Note: {item.schedule.comments}
          </Paragraph>
        )}
      </Card.Content>
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
  },
  price: {
    fontSize: 16,
    fontWeight: 'bold',
    marginTop: 8,
  },
  comments: {
    marginTop: 8,
    fontStyle: 'italic',
    color: '#666',
  },
});