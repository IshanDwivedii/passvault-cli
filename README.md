# 🔐 PassVault CLI - Secure Password Manager

A secure, command-line password vault built with Java Spring Boot, featuring military-grade encryption, secure password input, and comprehensive security measures.

## 🛡️ Security Features

### Encryption & Security
- **AES-256-GCM Encryption**: Military-grade encryption for all stored passwords
- **PBKDF2 Key Derivation**: 100,000 iterations for secure key generation
- **Secure Random Generation**: Cryptographically secure salt and IV generation
- **Memory Protection**: Automatic clearing of sensitive data from memory
- **Password Masking**: Secure password input with character masking
- **BCrypt Hashing**: Secure master password hashing with 12 rounds


## 🚀 Quick Start

### Prerequisites
- Java 21 or higher
- Maven 3.6+
- PostgreSQL 12+
- Git

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/passvault-cli.git
   cd passvault-cli
   ```

2. **Set up PostgreSQL database**
   ```bash
   # Create database
   createdb passvault
   
   # Create user (optional)
   createuser -P passvault_user
   ```

3. **Configure environment variables**
   ```bash
   # Create .env file
   cp .env.example .env
   
   # Edit with your database credentials
   nano .env
   ```

4. **Build and run**
   ```bash
   mvn clean install
   java -jar target/passVault-0.0.1-SNAPSHOT.jar
   ```

## 📖 Usage Guide

### First Time Setup

1. **Launch the application**
   ```bash
   java -jar passVault-0.0.1-SNAPSHOT.jar
   ```

2. **Register a new account**
   ```
   =================================
    SECURE PASSWORD VAULT CLI
   =================================
   
   1. Login
   2. Register
   3. Exit
   Choose option: 2
   
   Choose username: myuser
   Choose master password: ********
   Confirm master password: ********
   
   ✓ Registration successful! You can now login.
   ```

3. **Login and start managing passwords**
   ```
   Username: myuser
   Master Password: ********
   
   ✓ Login successful! Welcome myuser
   ```

### Main Menu Operations

```
=================================
    PASSWORD VAULT - MAIN MENU
=================================
Logged in as: myuser

1. Add Password Entry
2. View Password Entry
3. List All Services
4. Update Password Entry
5. Delete Password Entry
6. Generate Secure Password
7. Password Security Audit
8. Change Master Password
9. Logout
0. Exit Application
```

#### 1. Adding Passwords
```
--- ADD PASSWORD ENTRY ---
Service/Website name: github.com
Username/Email: myuser@example.com
Password (or press Enter to generate): ********
Notes (optional): Personal GitHub account
Enter master password: ********

✓ Password entry added successfully!
```

#### 2. Viewing Passwords
```
--- VIEW PASSWORD ENTRY ---
Service/Website name: github.com
Enter master password: ********

--- PASSWORD DETAILS ---
Service: github.com
Username: myuser@example.com
Password: MySecurePassword123!
Notes: Personal GitHub account
Created: 2024-01-15
Updated: 2024-01-15
```

#### 3. Generating Secure Passwords
```
Generate password? (y/n): y
Generated password: K9#mN2$pL8@vX5&qR
Use this password? (y/n): y
```

## 🔧 Configuration

### Environment Variables

Create a `.env` file in the project root:

```env
# Database Configuration
DB_HOST=localhost
DB_PORT=5432
DB_NAME=passvault
DB_USERNAME=passvault_user
DB_PASSWORD=your_secure_password

# Security Configuration
ENCRYPTION_ITERATIONS=100000
BCRYPT_ROUNDS=12
SESSION_TIMEOUT=1800

# Application Configuration
APP_NAME=PassVault CLI
APP_VERSION=1.0.0
LOG_LEVEL=INFO
```

### Application Properties

Key configuration options in `application.properties`:

```properties
# Database
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# Security
spring.jpa.properties.hibernate.connection.provider_disables_autocommit=true
spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true

# Performance
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
```

## 🧪 Testing

### Running Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=EncryptionServiceTest

# Run with coverage
mvn test jacoco:report
```

### Security Testing
```bash
# Run security tests
mvn test -Dtest=SecurityTestSuite

# Run SQL injection tests
mvn test -Dtest=SQLInjectionTest
```

## 🏗️ Architecture

### Project Structure
```
src/
├── main/
│   ├── java/com/ishan/passvault/
│   │   ├── cli/                 # Command-line interface
│   │   ├── config/              # Configuration classes
│   │   ├── model/               # Entity models
│   │   ├── repository/          # Data access layer
│   │   ├── service/             # Business logic
│   │   └── util/                # Utility classes
│   └── resources/
│       └── application.properties
└── test/
    └── java/com/ishan/passvault/
        ├── security/            # Security tests
        └── service/             # Service tests
```

### Key Components

- **PasswordVaultCLI**: Main CLI interface and user interaction
- **EncryptionService**: AES-256-GCM encryption with PBKDF2 key derivation
- **AuthenticationService**: User authentication and session management
- **PasswordService**: Password CRUD operations with encryption
- **SecurePasswordInput**: Secure password input with masking
- **InputValidator**: Input validation and sanitization

## 🔒 Security Considerations

### Production Deployment

1. **Database Security**
   - Use strong, unique database passwords
   - Enable SSL/TLS for database connections
   - Restrict database access to application server only
   - Regular database backups with encryption

2. **Application Security**
   - Run application with minimal privileges
   - Use HTTPS for any web interfaces
   - Implement rate limiting for login attempts
   - Regular security updates and patches

3. **Infrastructure Security**
   - Secure server configuration
   - Firewall rules to restrict access
   - Regular security audits
   - Monitoring and alerting for suspicious activity

### Security Best Practices

- **Master Password**: Use a strong, unique master password
- **Regular Updates**: Keep the application updated
- **Backup**: Regularly backup your password database
- **Access Control**: Limit physical access to the system
- **Monitoring**: Monitor for suspicious login attempts

## 🐛 Troubleshooting

### Common Issues

1. **Database Connection Failed**
   ```
   Error: Could not connect to database
   Solution: Check database credentials and network connectivity
   ```

2. **Encryption Error**
   ```
   Error: Bad padding during decryption
   Solution: Verify master password is correct
   ```

3. **Memory Issues**
   ```
   Error: OutOfMemoryError
   Solution: Increase JVM heap size: -Xmx2g
   ```

### Debug Mode
```bash
# Enable debug logging
java -Dlogging.level.com.ishan.passvault=DEBUG -jar passVault-0.0.1-SNAPSHOT.jar
```

## 📊 Performance

### Benchmarks
- **Encryption**: ~50ms per password entry
- **Decryption**: ~30ms per password entry
- **Key Derivation**: ~200ms (one-time during login)
- **Database Operations**: ~10-50ms depending on data size

### Optimization Tips
- Use connection pooling (already configured)
- Implement caching for frequently accessed data
- Optimize database indexes
- Monitor memory usage

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Setup
```bash
# Clone and setup
git clone https://github.com/yourusername/passvault-cli.git
cd passvault-cli

# Install dependencies
mvn install

# Run in development mode
mvn spring-boot:run
```

## ⚠️ Disclaimer

This software is provided "as is" without warranty. Users are responsible for:
- Securing their master passwords
- Regular backups of their password data
- Understanding the security implications
- Compliance with local laws and regulations

## 🆘 Support

- **Issues**: [GitHub Issues](https://github.com/yourusername/passvault-cli/issues)
- **Security**: [Security Policy](SECURITY.md)
- **Documentation**: [Wiki](https://github.com/yourusername/passvault-cli/wiki)

## 🔄 Changelog

### Version 1.0.0 (Current)
- Initial release
- AES-256-GCM encryption
- PBKDF2 key derivation
- Secure password input
- Comprehensive input validation
- SQL injection protection
- XSS prevention

---

