import { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { passwordApi, generatePassword } from '../services/api';
import '../styles/Modal.css';

interface AddPasswordModalProps {
  onClose: () => void;
  onSuccess: () => void;
}

export const AddPasswordModal = ({ onClose, onSuccess }: AddPasswordModalProps) => {
  const { user } = useAuth();
  const [serviceName, setServiceName] = useState('');
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [notes, setNotes] = useState('');
  const [category, setCategory] = useState('');
  const [masterPassword, setMasterPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleGenerate = () => {
    const generated = generatePassword(16, true);
    setPassword(generated);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!user) return;

    setError('');
    setLoading(true);

    try {
      await passwordApi.add(user.id, {
        serviceName,
        username,
        password,
        notes,
        category,
        masterPassword,
      });
      onSuccess();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to add password');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2>Add New Password</h2>
          <button className="close-btn" onClick={onClose}>
            ×
          </button>
        </div>

        <form onSubmit={handleSubmit} className="modal-form">
          <div className="form-group">
            <label>Service/Website *</label>
            <input
              type="text"
              value={serviceName}
              onChange={(e) => setServiceName(e.target.value)}
              required
              placeholder="e.g., GitHub, Gmail"
            />
          </div>

          <div className="form-group">
            <label>Username/Email *</label>
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
              placeholder="your@email.com"
            />
          </div>

          <div className="form-group">
            <label>Password *</label>
            <div className="password-input-group">
              <input
                type="text"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                placeholder="Enter or generate password"
              />
              <button type="button" onClick={handleGenerate} className="btn-secondary">
                Generate
              </button>
            </div>
          </div>

          <div className="form-group">
            <label>Category</label>
            <input
              type="text"
              value={category}
              onChange={(e) => setCategory(e.target.value)}
              placeholder="e.g., Social, Work, Banking"
            />
          </div>

          <div className="form-group">
            <label>Notes</label>
            <textarea
              value={notes}
              onChange={(e) => setNotes(e.target.value)}
              placeholder="Optional notes"
              rows={3}
            />
          </div>

          <div className="form-group">
            <label>Master Password *</label>
            <input
              type="password"
              value={masterPassword}
              onChange={(e) => setMasterPassword(e.target.value)}
              required
              placeholder="Enter your master password"
            />
          </div>

          {error && <div className="error-message">{error}</div>}

          <div className="modal-actions">
            <button type="button" onClick={onClose} className="btn-secondary">
              Cancel
            </button>
            <button type="submit" className="btn-primary" disabled={loading}>
              {loading ? 'Adding...' : 'Add Password'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
