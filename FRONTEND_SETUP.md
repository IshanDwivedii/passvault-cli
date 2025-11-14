# PassVault Web Frontend

Modern, secure web interface for PassVault password manager.

## Prerequisites

- Node.js 18+ and npm
- PostgreSQL database running
- Spring Boot backend running

## Quick Start

### 1. Start the Backend

```bash
# Make sure PostgreSQL is running and database 'passvault' exists
# From project root directory
./mvnw spring-boot:run
```

The backend will start on `http://localhost:8080`

### 2. Start the Frontend

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies (first time only)
npm install

# Start development server
npm run dev
```

The frontend will start on `http://localhost:5173`

### 3. Access the Application

Open your browser and navigate to `http://localhost:5173`

## Features

- Secure user registration and login
- Add, view, and manage password entries
- Password generator with customizable settings
- Search and filter passwords
- Modern, responsive UI
- Password masking and secure decryption
- Real-time password strength analysis

## Architecture

- **Frontend**: React 18 + TypeScript + Vite
- **Backend**: Spring Boot 3 + PostgreSQL
- **Security**: AES-256-GCM encryption, BCrypt password hashing
- **API**: RESTful API with JSON communication

## Development

### Frontend Structure

```
frontend/
├── src/
│   ├── components/     # React components
│   ├── context/        # Auth context
│   ├── services/       # API services
│   ├── styles/         # CSS stylesheets
│   └── types/          # TypeScript types
├── public/
└── package.json
```

### Available Scripts

```bash
npm run dev      # Start development server
npm run build    # Build for production
npm run preview  # Preview production build
```

## Security Notes

- Master passwords are never stored
- All password data is encrypted with AES-256-GCM
- HTTPS recommended for production
- CORS configured for local development only

## Switching to CLI Mode

To run in CLI mode instead of web mode, update `application.properties`:

```properties
passvault.mode=cli
```

Then run the backend with `./mvnw spring-boot:run`
