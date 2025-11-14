# PassVault - Quick Start Guide

## What's New

Your PassVault application now has a modern web interface! You can use either:
- **Web UI** - Modern React frontend
- **CLI** - Original command-line interface

## Prerequisites

1. **PostgreSQL** - Make sure it's running
   ```bash
   # Check if PostgreSQL is running
   pg_isready

   # Create database if needed
   createdb passvault
   ```

2. **Node.js & npm** - For the web frontend
   ```bash
   node --version  # Should be 18+
   npm --version
   ```

## Starting the Application

### Option 1: Web Interface (Default)

#### Step 1: Start the Backend
```bash
# From project root
./mvnw spring-boot:run
```

Wait for the message:
```
PASSWORD VAULT WEB MODE
Access at: http://localhost:8080
```

#### Step 2: Start the Frontend
```bash
# In a new terminal
cd frontend
npm install  # First time only
npm run dev
```

#### Step 3: Access the App
Open your browser to: **http://localhost:5173**

### Option 2: CLI Mode

Update `src/main/resources/application.properties`:
```properties
passvault.mode=cli
```

Then run:
```bash
./mvnw spring-boot:run
```

## Using the Web Interface

### First Time Setup
1. Click "Register" tab
2. Create username (3-50 characters)
3. Create master password (8+ characters)
4. Click "Register"

### Login
1. Enter your username
2. Enter your master password
3. Click "Login"

### Managing Passwords

**Add Password**
- Click "Add Password" button
- Fill in service name and username
- Enter password or click "Generate"
- Enter your master password
- Click "Add Password"

**View Password**
- Click the eye icon on any password card
- Enter your master password when prompted
- Password will be decrypted and displayed
- Click copy icon to copy to clipboard

**Generate Password**
- Click "Generate Password" button
- Adjust length with slider (8-32 characters)
- Toggle "Include Symbols" checkbox
- Click "Generate"
- Click "Copy" to copy to clipboard

**Search Passwords**
- Type in the search box at the top
- Results filter in real-time

**Delete Password**
- Click "Delete" on any password card
- Confirm deletion

## Architecture

### Backend (Spring Boot)
- **Port**: 8080
- **API**: REST endpoints at `/api/*`
- **Database**: PostgreSQL
- **Security**:
  - AES-256-GCM encryption
  - BCrypt password hashing
  - CORS enabled for localhost

### Frontend (React + Vite)
- **Port**: 5173
- **Framework**: React 18 with TypeScript
- **Build Tool**: Vite
- **API Proxy**: Requests to `/api/*` proxied to backend

### API Endpoints

```
POST /api/auth/login       - Login user
POST /api/auth/register    - Register new user
GET  /api/passwords/:id    - Get all passwords
POST /api/passwords/:id    - Add new password
POST /api/passwords/:id/decrypt - Decrypt password
DELETE /api/passwords/:id  - Delete password
GET  /api/passwords/:id/search?q= - Search passwords
```

## Security Features

1. **Master Password** - Never stored, only used for encryption/decryption
2. **AES-256-GCM** - Military-grade encryption for passwords
3. **PBKDF2** - 100,000 iterations for key derivation
4. **BCrypt** - Secure master password hashing (12 rounds)
5. **CORS** - Configured for local development only
6. **Input Validation** - SQL injection and XSS prevention

## Troubleshooting

### Backend won't start
```bash
# Check if PostgreSQL is running
pg_isready

# Check if port 8080 is available
lsof -i :8080

# Check application.properties has correct DB credentials
cat src/main/resources/application.properties
```

### Frontend won't start
```bash
# Check if port 5173 is available
lsof -i :5173

# Reinstall dependencies
cd frontend
rm -rf node_modules package-lock.json
npm install
```

### API calls fail
1. Make sure backend is running on port 8080
2. Check browser console for CORS errors
3. Verify vite.config.ts has proxy configured

### Can't login
1. Verify user was created (check database)
2. Ensure correct master password
3. Check backend logs for errors

## Development

### Frontend Development
```bash
cd frontend
npm run dev      # Start dev server
npm run build    # Build for production
npm run preview  # Preview production build
```

### Backend Development
```bash
./mvnw clean install     # Build project
./mvnw spring-boot:run   # Run application
./mvnw test              # Run tests
```

## Production Deployment

1. Build frontend:
   ```bash
   cd frontend
   npm run build
   # Output in frontend/dist/
   ```

2. Configure for production:
   - Update CORS settings in WebConfig.java
   - Enable HTTPS
   - Use production database credentials
   - Set secure environment variables

3. Deploy backend:
   ```bash
   ./mvnw clean package
   java -jar target/passVault-0.0.1-SNAPSHOT.jar
   ```

## File Structure

```
project/
├── src/main/java/              # Backend Java code
│   └── com/ishan/passvault/
│       ├── cli/                # CLI interface
│       ├── config/             # Configuration
│       ├── controller/         # REST controllers
│       ├── model/              # Entity models
│       ├── repository/         # Data access
│       ├── service/            # Business logic
│       └── util/               # Utilities
├── frontend/                   # Frontend React app
│   ├── src/
│   │   ├── components/         # React components
│   │   ├── context/            # Auth context
│   │   ├── services/           # API services
│   │   ├── styles/             # CSS files
│   │   └── types/              # TypeScript types
│   └── package.json
└── README.md
```

## Support

For issues or questions:
1. Check the logs (backend console output)
2. Check browser console (frontend errors)
3. Verify database connection
4. Review this guide

## Next Steps

Consider adding:
- Password strength meter
- Export/import functionality
- Password categories/tags
- Two-factor authentication
- Password expiration reminders
- Audit log
- Mobile app
