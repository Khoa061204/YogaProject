import { initializeApp } from '@react-native-firebase/app';
import auth from '@react-native-firebase/auth';
import database from '@react-native-firebase/database';

const firebaseConfig = {
  apiKey: "AIzaSyA69lqJWspnmUfvxpZR3iOrRwJiqPtpUp8",
  authDomain: "yogaadmin-cb81e.firebaseapp.com",
  databaseURL: "https://yogaadmin-cb81e-default-rtdb.asia-southeast1.firebasedatabase.app",
  projectId: "yogaadmin-cb81e",
  storageBucket: "yogaadmin-cb81e.firebasestorage.app",
  appId: "1:968665838490:android:164c3ff29ef748d330c023",
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);

export { app, auth, database };