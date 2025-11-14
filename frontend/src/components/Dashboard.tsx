import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { passwordApi, generatePassword } from '../services/api';
import type { PasswordEntryWithDecrypted } from '../types';
import { AddPasswordModal } from './AddPasswordModal';
import { PasswordCard } from './PasswordCard';
import '../styles/Dashboard.css';

export const Dashboard = () => {
  const { user, logout } = useAuth();
  const [passwords, setPasswords] = useState<PasswordEntryWithDecrypted[]>([]);
  const [filteredPasswords, setFilteredPasswords] = useState<PasswordEntryWithDecrypted[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [showAddModal, setShowAddModal] = useState(false);
  const [showGenerator, setShowGenerator] = useState(false);
  const [generatedPassword, setGeneratedPassword] = useState('');
  const [passwordLength, setPasswordLength] = useState(16);
  const [includeSymbols, setIncludeSymbols] = useState(true);

  useEffect(() => {
    loadPasswords();
  }, []);

  useEffect(() => {
    if (searchTerm) {
      const filtered = passwords.filter(
        (p) =>
          p.serviceName.toLowerCase().includes(searchTerm.toLowerCase()) ||
          p.username.toLowerCase().includes(searchTerm.toLowerCase()) ||
          p.category?.toLowerCase().includes(searchTerm.toLowerCase())
      );
      setFilteredPasswords(filtered);
    } else {
      setFilteredPasswords(passwords);
    }
  }, [searchTerm, passwords]);

  const loadPasswords = async () => {
    if (!user) return;
    try {
      setLoading(true);
      const data = await passwordApi.getAll(user.id);
      setPasswords(data);
      setFilteredPasswords(data);
    } catch (error) {
      console.error('Failed to load passwords:', error);
    } finally {
      setLoading(false);
    }
  };

  const handlePasswordAdded = () => {
    loadPasswords();
    setShowAddModal(false);
  };

  const handlePasswordDeleted = () => {
    loadPasswords();
  };

  const handleGeneratePassword = () => {
    const pwd = generatePassword(passwordLength, includeSymbols);
    setGeneratedPassword(pwd);
  };

  const handleCopyGenerated = () => {
    navigator.clipboard.writeText(generatedPassword);
    alert('Password copied to clipboard!');
  };

  if (loading) {
    return (
      <div className="dashboard-container">
        <div className="loading">Loading your vault...</div>
      </div>
    );
  }

  return (
    <div className="dashboard-container">
      <header className="dashboard-header">
        <div className="header-left">
          <h1>🔐 PassVault</h1>
          <span className="username">Welcome, {user?.username}</span>
        </div>
        <button onClick={logout} className="btn-secondary">
          Logout
        </button>
      </header>

      <div className="dashboard-content">
        <div className="toolbar">
          <div className="search-box">
            <input
              type="text"
              placeholder="Search passwords..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>
          <div className="toolbar-actions">
            <button onClick={() => setShowGenerator(!showGenerator)} className="btn-secondary">
              Generate Password
            </button>
            <button onClick={() => setShowAddModal(true)} className="btn-primary">
              Add Password
            </button>
          </div>
        </div>

        {showGenerator && (
          <div className="generator-panel">
            <h3>Password Generator</h3>
            <div className="generator-controls">
              <div className="form-group">
                <label>Length: {passwordLength}</label>
                <input
                  type="range"
                  min="8"
                  max="32"
                  value={passwordLength}
                  onChange={(e) => setPasswordLength(Number(e.target.value))}
                />
              </div>
              <div className="form-group">
                <label>
                  <input
                    type="checkbox"
                    checked={includeSymbols}
                    onChange={(e) => setIncludeSymbols(e.target.checked)}
                  />
                  Include Symbols
                </label>
              </div>
              <button onClick={handleGeneratePassword} className="btn-primary">
                Generate
              </button>
            </div>
            {generatedPassword && (
              <div className="generated-password">
                <code>{generatedPassword}</code>
                <button onClick={handleCopyGenerated} className="btn-secondary">
                  Copy
                </button>
              </div>
            )}
          </div>
        )}

        <div className="passwords-stats">
          <div className="stat-card">
            <span className="stat-number">{passwords.length}</span>
            <span className="stat-label">Total Passwords</span>
          </div>
          <div className="stat-card">
            <span className="stat-number">{filteredPasswords.length}</span>
            <span className="stat-label">Showing</span>
          </div>
        </div>

        {filteredPasswords.length === 0 ? (
          <div className="empty-state">
            <p>No passwords found</p>
            <button onClick={() => setShowAddModal(true)} className="btn-primary">
              Add Your First Password
            </button>
          </div>
        ) : (
          <div className="passwords-grid">
            {filteredPasswords.map((password) => (
              <PasswordCard
                key={password.id}
                password={password}
                onDeleted={handlePasswordDeleted}
              />
            ))}
          </div>
        )}
      </div>

      {showAddModal && (
        <AddPasswordModal
          onClose={() => setShowAddModal(false)}
          onSuccess={handlePasswordAdded}
        />
      )}
    </div>
  );
};
