# PassVault Web Interface - Features Overview

## User Interface Components

### 1. Login/Registration Page
- Clean, modern design with gradient background
- Tabbed interface for Login/Register
- Form validation
- Secure password input
- Error messaging
- Responsive design

### 2. Dashboard
**Header**
- Application logo and title
- Welcome message with username
- Logout button

**Toolbar**
- Search bar for filtering passwords
- Generate Password button
- Add Password button

**Password Generator Panel** (toggleable)
- Length slider (8-32 characters)
- Include symbols checkbox
- Generate button
- Copy to clipboard functionality

**Statistics Cards**
- Total passwords count
- Currently displayed count

**Password Grid**
- Responsive grid layout
- Password cards with:
  - Service name
  - Category badge
  - Username
  - Masked password with view toggle
  - Copy password button
  - Notes section
  - Last updated date
  - Delete button

### 3. Add Password Modal
- Service/Website name input
- Username/Email input
- Password input with generate button
- Category input
- Notes textarea
- Master password verification
- Form validation
- Cancel/Submit actions

### 4. Password Card
- Hover effects
- View password with master password prompt
- Decryption modal
- Copy to clipboard
- Delete confirmation
- Visual feedback

## Technical Features

### Frontend
- **React 18** - Modern React with hooks
- **TypeScript** - Type safety
- **Vite** - Fast build tool
- **Axios** - HTTP client
- **Context API** - State management
- **CSS Modules** - Scoped styling
- **Responsive Design** - Mobile-friendly

### Backend
- **REST API** - JSON endpoints
- **CORS** - Cross-origin enabled
- **Spring Boot 3** - Modern Java framework
- **JPA/Hibernate** - Database ORM
- **PostgreSQL** - Relational database

### Security
- **No storage** of master passwords
- **Client-side** password masking
- **Server-side** encryption/decryption
- **AES-256-GCM** encryption
- **PBKDF2** key derivation
- **BCrypt** for auth
- **Input validation** on both sides

## User Experience

### Workflow
1. **Register** → Create account with username and master password
2. **Login** → Authenticate with credentials
3. **Add Passwords** → Store encrypted passwords
4. **View Passwords** → Decrypt with master password
5. **Search/Filter** → Find passwords quickly
6. **Generate** → Create strong passwords
7. **Manage** → Update or delete entries

### Design Principles
- **Clean & Modern** - Professional appearance
- **Intuitive** - Easy to navigate
- **Secure** - Security-first approach
- **Responsive** - Works on all devices
- **Fast** - Quick load times
- **Accessible** - Clear labels and feedback

## Color Scheme
- **Primary**: Blue/Purple gradient (#667eea to #764ba2)
- **Background**: Light gray (#f3f4f6)
- **Text**: Dark gray (#374151)
- **Success**: Green tones
- **Error**: Red tones
- **Neutral**: White cards with shadows

## Animations & Effects
- Smooth transitions (0.2s)
- Hover effects on buttons and cards
- Card elevation on hover
- Loading states
- Modal slide-in animations
- Button press effects

## Browser Compatibility
- Chrome/Edge (latest)
- Firefox (latest)
- Safari (latest)
- Mobile browsers

## Performance
- **Fast load** - Vite optimization
- **Code splitting** - React lazy loading
- **API caching** - Smart data fetching
- **Minimal rerenders** - React optimization
