import React, { useState } from 'react';
import { StyleSheet, View, KeyboardAvoidingView, Platform } from 'react-native';
import { TextInput, Button, Title, HelperText, Paragraph } from 'react-native-paper';
import { resetPassword } from '../../services/auth';

interface ForgotPasswordScreenProps {
  navigation: any;
}

export const ForgotPasswordScreen: React.FC<ForgotPasswordScreenProps> = ({ navigation }) => {
  const [email, setEmail] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);

  const handleResetPassword = async () => {
    if (!email) {
      setError('Please enter your email address');
      return;
    }

    setLoading(true);
    setError('');
    setSuccess(false);

    const { error: resetError } = await resetPassword(email);
    
    if (resetError) {
      setError(resetError.message);
      setLoading(false);
      return;
    }

    setSuccess(true);
    setLoading(false);
  };

  return (
    <KeyboardAvoidingView
      behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
      style={styles.container}
    >
      <View style={styles.content}>
        <Title style={styles.title}>Reset Password</Title>
        
        <Paragraph style={styles.description}>
          Enter your email address and we'll send you instructions to reset your password.
        </Paragraph>

        <TextInput
          label="Email"
          value={email}
          onChangeText={setEmail}
          autoCapitalize="none"
          keyboardType="email-address"
          mode="outlined"
          style={styles.input}
        />

        {error ? (
          <HelperText type="error" visible={!!error}>
            {error}
          </HelperText>
        ) : null}

        {success ? (
          <HelperText type="info" visible={true}>
            Password reset instructions have been sent to your email.
          </HelperText>
        ) : null}

        <Button
          mode="contained"
          onPress={handleResetPassword}
          loading={loading}
          disabled={loading}
          style={styles.button}
        >
          Reset Password
        </Button>

        <Button
          mode="text"
          onPress={() => navigation.goBack()}
          style={styles.textButton}
        >
          Back to Login
        </Button>
      </View>
    </KeyboardAvoidingView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
  },
  content: {
    flex: 1,
    padding: 16,
    justifyContent: 'center',
  },
  title: {
    fontSize: 24,
    marginBottom: 16,
    textAlign: 'center',
  },
  description: {
    textAlign: 'center',
    marginBottom: 24,
    color: '#666',
  },
  input: {
    marginBottom: 16,
  },
  button: {
    marginTop: 8,
  },
  textButton: {
    marginTop: 8,
  },
});