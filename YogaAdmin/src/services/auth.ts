import auth from '@react-native-firebase/auth';

export interface AuthError {
  code: string;
  message: string;
}

export const signIn = async (email: string, password: string) => {
  try {
    const result = await auth().signInWithEmailAndPassword(email, password);
    return { user: result.user, error: null };
  } catch (error) {
    return { user: null, error: error as AuthError };
  }
};

export const signUp = async (email: string, password: string) => {
  try {
    const result = await auth().createUserWithEmailAndPassword(email, password);
    return { user: result.user, error: null };
  } catch (error) {
    return { user: null, error: error as AuthError };
  }
};

export const signOut = async () => {
  try {
    await auth().signOut();
    return { error: null };
  } catch (error) {
    return { error: error as AuthError };
  }
};

export const resetPassword = async (email: string) => {
  try {
    await auth().sendPasswordResetEmail(email);
    return { error: null };
  } catch (error) {
    return { error: error as AuthError };
  }
};

export const useAuthStateListener = (callback: (user: any) => void) => {
  auth().onAuthStateChanged(callback);
};