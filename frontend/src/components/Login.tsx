import { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { authApi } from '../services/api';
import '../styles/Auth.css';

export const Login = () => {
  const [isLogin, setIsLogin] = useState(true);
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      if (isLogin) {
        const user = await authApi.login(username, password);
        login(user);
      } else {
        if (password !== confirmPassword) {
          setError('Passwords do not match');
          setLoading(false);
          return;
        }
        await authApi.register(username, password);
        setError('');
        setIsLogin(true);
        setPassword('');
        setConfirmPassword('');
        alert('Registration successful! Please login.');
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'An error occurred');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <div className="auth-header">
          <h1>🔐 PassVault</h1>
          <p>Secure Password Manager</p>
        </div>

        <div className="auth-tabs">
          <button
            className={isLogin ? 'tab active' : 'tab'}
            onClick={() => setIsLogin(true)}
          >
            Login
          </button>
          <button
            className={!isLogin ? 'tab active' : 'tab'}
            onClick={() => setIsLogin(false)}
          >
            Register
          </button>
        </div>

        <form onSubmit={handleSubmit} className="auth-form">
          <div className="form-group">
            <label>Username</label>
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
              minLength={3}
              maxLength={50}
              placeholder="Enter your username"
            />
          </div>

          <div className="form-group">
            <label>Master Password</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              minLength={8}
              placeholder="Enter your master password"
            />
          </div>

          {!isLogin && (
            <div className="form-group">
              <label>Confirm Password</label>
              <input
                type="password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                required
                minLength={8}
                placeholder="Confirm your master password"
              />
            </div>
          )}

          {error && <div className="error-message">{error}</div>}

          <button type="submit" className="btn-primary" disabled={loading}>
            {loading ? 'Please wait...' : isLogin ? 'Login' : 'Register'}
          </button>
        </form>
      </div>
    </div>
  );
};
