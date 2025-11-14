export interface User {
  id: number;
  username: string;
  createdAt: string;
  lastLogin?: string;
}

export interface PasswordEntry {
  id: number;
  serviceName: string;
  username: string;
  encryptedPassword: string;
  notes?: string;
  category?: string;
  createdAt: string;
  updatedAt: string;
  lastAccessed?: string;
}

export interface PasswordEntryWithDecrypted extends PasswordEntry {
  decryptedPassword?: string;
}

export interface AuthResponse {
  user: User;
  token?: string;
}

export interface AddPasswordRequest {
  serviceName: string;
  username: string;
  password: string;
  notes?: string;
  category?: string;
  masterPassword: string;
}

export interface UpdatePasswordRequest extends AddPasswordRequest {
  newPassword?: string;
}
