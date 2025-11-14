import axios from 'axios';
import type { User, PasswordEntry, AddPasswordRequest } from '../types';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const authApi = {
  login: async (username: string, password: string): Promise<User> => {
    const response = await api.post('/auth/login', { username, password });
    return response.data;
  },

  register: async (username: string, password: string): Promise<void> => {
    await api.post('/auth/register', { username, password });
  },
};

export const passwordApi = {
  getAll: async (userId: number): Promise<PasswordEntry[]> => {
    const response = await api.get(`/passwords/${userId}`);
    return response.data;
  },

  add: async (userId: number, data: AddPasswordRequest): Promise<PasswordEntry> => {
    const response = await api.post(`/passwords/${userId}`, data);
    return response.data;
  },

  getDecrypted: async (userId: number, serviceName: string, username: string, masterPassword: string): Promise<string> => {
    const response = await api.post(`/passwords/${userId}/decrypt`, {
      serviceName,
      username,
      masterPassword,
    });
    return response.data;
  },

  delete: async (userId: number, serviceName: string, username: string): Promise<void> => {
    await api.delete(`/passwords/${userId}`, {
      data: { serviceName, username },
    });
  },

  search: async (userId: number, searchTerm: string): Promise<PasswordEntry[]> => {
    const response = await api.get(`/passwords/${userId}/search`, {
      params: { q: searchTerm },
    });
    return response.data;
  },
};

export const generatePassword = (length: number = 16, includeSymbols: boolean = true): string => {
  const uppercase = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
  const lowercase = 'abcdefghijklmnopqrstuvwxyz';
  const numbers = '0123456789';
  const symbols = '!@#$%^&*()_+-=[]{}|;:,.<>?';

  let charset = uppercase + lowercase + numbers;
  if (includeSymbols) {
    charset += symbols;
  }

  let password = '';
  password += uppercase[Math.floor(Math.random() * uppercase.length)];
  password += lowercase[Math.floor(Math.random() * lowercase.length)];
  password += numbers[Math.floor(Math.random() * numbers.length)];
  if (includeSymbols) {
    password += symbols[Math.floor(Math.random() * symbols.length)];
  }

  for (let i = password.length; i < length; i++) {
    password += charset[Math.floor(Math.random() * charset.length)];
  }

  return password.split('').sort(() => Math.random() - 0.5).join('');
};
