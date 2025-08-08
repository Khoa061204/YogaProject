import React from 'react';
import { Provider as PaperProvider } from 'react-native-paper';
import { LoginScreen } from './src/screens/auth/LoginScreen';

function App(): React.JSX.Element {
  return (
    <PaperProvider>
      <LoginScreen />
    </PaperProvider>
  );
}

export default App;