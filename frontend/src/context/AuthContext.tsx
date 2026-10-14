import { createContext, useContext, useState, type ReactNode } from 'react';
import { login as loginRequest } from '../api/auth';

interface AuthContextValue {
  username: string | null;
  isAuthenticated: boolean;
  login: (username: string, password: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [username, setUsername] = useState<string | null>(localStorage.getItem('username'));

  async function login(usernameInput: string, password: string) {
    const response = await loginRequest({ username: usernameInput, password });
    localStorage.setItem('token', response.token);
    localStorage.setItem('username', response.username);
    setUsername(response.username);
  }

  function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    setUsername(null);
  }

  return (
    <AuthContext.Provider value={{ username, isAuthenticated: username !== null, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth(): AuthContextValue {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
