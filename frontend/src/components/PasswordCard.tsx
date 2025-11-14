import { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { passwordApi } from '../services/api';
import type { PasswordEntryWithDecrypted } from '../types';
import '../styles/PasswordCard.css';

interface PasswordCardProps {
  password: PasswordEntryWithDecrypted;
  onDeleted: () => void;
}

export const PasswordCard = ({ password, onDeleted }: PasswordCardProps) => {
  const { user } = useAuth();
  const [showPassword, setShowPassword] = useState(false);
  const [decryptedPassword, setDecryptedPassword] = useState('');
  const [masterPassword, setMasterPassword] = useState('');
  const [showMasterPrompt, setShowMasterPrompt] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleViewPassword = async () => {
    if (showPassword) {
      setShowPassword(false);
      setDecryptedPassword('');
      return;
    }

    if (decryptedPassword) {
      setShowPassword(true);
      return;
    }

    setShowMasterPrompt(true);
  };

  const handleDecrypt = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!user) return;

    setError('');
    setLoading(true);

    try {
      const pwd = await passwordApi.getDecrypted(
        user.id,
        password.serviceName,
        password.username,
        masterPassword
      );
      setDecryptedPassword(pwd);
      setShowPassword(true);
      setShowMasterPrompt(false);
      setMasterPassword('');
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to decrypt password');
    } finally {
      setLoading(false);
    }
  };

  const handleCopy = () => {
    if (decryptedPassword) {
      navigator.clipboard.writeText(decryptedPassword);
      alert('Password copied to clipboard!');
    }
  };

  const handleDelete = async () => {
    if (!user) return;
    if (!confirm(`Delete password for ${password.serviceName}?`)) return;

    try {
      await passwordApi.delete(user.id, password.serviceName, password.username);
      onDeleted();
    } catch (err) {
      alert('Failed to delete password');
    }
  };

  return (
    <div className="password-card">
      <div className="card-header">
        <h3>{password.serviceName}</h3>
        {password.category && <span className="category-badge">{password.category}</span>}
      </div>

      <div className="card-body">
        <div className="card-field">
          <span className="field-label">Username:</span>
          <span className="field-value">{password.username}</span>
        </div>

        <div className="card-field">
          <span className="field-label">Password:</span>
          <div className="password-field">
            <span className="field-value">
              {showPassword ? decryptedPassword : '••••••••'}
            </span>
            <button onClick={handleViewPassword} className="btn-icon" title="View password">
              {showPassword ? '🙈' : '👁️'}
            </button>
            {decryptedPassword && (
              <button onClick={handleCopy} className="btn-icon" title="Copy password">
                📋
              </button>
            )}
          </div>
        </div>

        {password.notes && (
          <div className="card-field">
            <span className="field-label">Notes:</span>
            <span className="field-value notes">{password.notes}</span>
          </div>
        )}

        <div className="card-meta">
          <span>Updated: {new Date(password.updatedAt).toLocaleDateString()}</span>
        </div>
      </div>

      <div className="card-actions">
        <button onClick={handleDelete} className="btn-danger">
          Delete
        </button>
      </div>

      {showMasterPrompt && (
        <div className="modal-overlay" onClick={() => setShowMasterPrompt(false)}>
          <div className="modal-content small" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3>Enter Master Password</h3>
              <button className="close-btn" onClick={() => setShowMasterPrompt(false)}>
                ×
              </button>
            </div>
            <form onSubmit={handleDecrypt} className="modal-form">
              <div className="form-group">
                <input
                  type="password"
                  value={masterPassword}
                  onChange={(e) => setMasterPassword(e.target.value)}
                  required
                  placeholder="Master password"
                  autoFocus
                />
              </div>
              {error && <div className="error-message">{error}</div>}
              <div className="modal-actions">
                <button
                  type="button"
                  onClick={() => setShowMasterPrompt(false)}
                  className="btn-secondary"
                >
                  Cancel
                </button>
                <button type="submit" className="btn-primary" disabled={loading}>
                  {loading ? 'Decrypting...' : 'Decrypt'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
