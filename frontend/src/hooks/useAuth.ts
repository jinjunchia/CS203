import { useContext } from 'react';
import { AuthContext } from './../app/(context)/AuthContext';

const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within a UserProvider');
  }
  return context;
};

export default useAuth;
