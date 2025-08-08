import React from 'react';
import { View, StyleSheet } from 'react-native';
import { TextInput, Button, Title } from 'react-native-paper';

export const LoginScreen = () => {
  return (
    <View style={styles.container}>
      <Title style={styles.title}>Welcome to Yoga Classes</Title>
      <TextInput
        label="Email"
        mode="outlined"
        style={styles.input}
      />
      <TextInput
        label="Password"
        mode="outlined"
        secureTextEntry
        style={styles.input}
      />
      <Button mode="contained" style={styles.button}>
        Login
      </Button>
      <Button mode="text" style={styles.button}>
        Create Account
      </Button>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 16,
    justifyContent: 'center',
  },
  title: {
    fontSize: 24,
    marginBottom: 24,
    textAlign: 'center',
  },
  input: {
    marginBottom: 16,
  },
  button: {
    marginTop: 8,
  },
});