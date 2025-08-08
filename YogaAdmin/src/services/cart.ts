import AsyncStorage from '@react-native-async-storage/async-storage';
import { CartItem, YogaCourse, Schedule } from '../types';

const CART_STORAGE_KEY = '@yoga_cart';

export interface CartService {
  getCart: () => Promise<CartItem[]>;
  addToCart: (course: YogaCourse, schedule: Schedule) => Promise<void>;
  removeFromCart: (scheduleId: number) => Promise<void>;
  clearCart: () => Promise<void>;
  isInCart: (scheduleId: number) => Promise<boolean>;
}

export const cartService: CartService = {
  getCart: async () => {
    try {
      const cartData = await AsyncStorage.getItem(CART_STORAGE_KEY);
      return cartData ? JSON.parse(cartData) : [];
    } catch (error) {
      console.error('Error getting cart:', error);
      return [];
    }
  },

  addToCart: async (course: YogaCourse, schedule: Schedule) => {
    try {
      const currentCart = await cartService.getCart();
      
      // Check if schedule is already in cart
      const exists = currentCart.some(item => item.schedule.id === schedule.id);
      if (exists) {
        throw new Error('This class is already in your cart');
      }

      // Check if schedule conflicts with any item in cart
      const hasConflict = currentCart.some(item => {
        return item.schedule.date === schedule.date && 
               item.schedule.time === course.time;
      });
      if (hasConflict) {
        throw new Error('You have another class scheduled at this time');
      }

      const newCart = [...currentCart, { course, schedule }];
      await AsyncStorage.setItem(CART_STORAGE_KEY, JSON.stringify(newCart));
    } catch (error) {
      throw error;
    }
  },

  removeFromCart: async (scheduleId: number) => {
    try {
      const currentCart = await cartService.getCart();
      const newCart = currentCart.filter(item => item.schedule.id !== scheduleId);
      await AsyncStorage.setItem(CART_STORAGE_KEY, JSON.stringify(newCart));
    } catch (error) {
      console.error('Error removing from cart:', error);
    }
  },

  clearCart: async () => {
    try {
      await AsyncStorage.removeItem(CART_STORAGE_KEY);
    } catch (error) {
      console.error('Error clearing cart:', error);
    }
  },

  isInCart: async (scheduleId: number) => {
    try {
      const currentCart = await cartService.getCart();
      return currentCart.some(item => item.schedule.id === scheduleId);
    } catch (error) {
      console.error('Error checking cart:', error);
      return false;
    }
  },
};