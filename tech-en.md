# Technical Architecture - WhereAreTheyV2

## Overview
WhereAreTheyV2 is an Android location-sharing application that enables users to share their real-time locations with friends. The application combines native Android development with Baidu Maps integration and secure data handling through encryption.

## Architecture Diagram
```
┌─────────────────────────────────────────────────────────────┐
│                    Android Application                      │
├─────────────────────────────────────────────────────────────┤
│  ┌───────────────┐  ┌───────────────┐  ┌─────────────────┐ │
│  │   UI Layer    │  │ Business Logic│  │   Data Layer    │ │
│  │               │  │               │  │                 │ │
│  │ - Activities  │  │ - AsyncTasks  │  │ - DAOs          │ │
│  │ - Fragments   │  │ - Listeners   │  │ - Models        │ │
│  │ - Baidu Maps  │  │ - Utilities   │  │ - Encryption    │ │
│  └───────────────┘  └───────────────┘  └─────────────────┘ │
├─────────────────────────────────────────────────────────────┤
│                    Data Storage Layer                       │
├─────────────────────────────────────────────────────────────┤
│                      MariaDB Database                       │
└─────────────────────────────────────────────────────────────┘
```

## Frontend Architecture

### Technology Stack
- **Language**: Java
- **Framework**: Native Android SDK
- **UI Components**: Android Support Library (AppCompatActivity, Fragments, etc.)
- **Map Service**: Baidu Maps SDK
- **Minimum SDK**: API Level 16 (Android 4.1)
- **Target SDK**: API Level 27 (Android 8.1)

### Main Components

#### Activities
1. **MainActivity**: Splash screen with delayed navigation to login
2. **LoginActivity**: User authentication and registration entry point
3. **TabGroupActivity**: Main application interface with tab navigation
4. **Supporting Activities**: 
   - RegisterActivity
   - ForgetPwdActivity
   - ChangePwdActivity
   - SettingActivity
   - DisplayFriendsActivity
   - FriendsInfoActivity
   - TalkingRoomActivity

#### Fragments
1. **MessageFragment**: Displays messages between users
2. **LocationFragment**: Core location sharing functionality with Baidu Maps integration
3. **MyInfoFragment**: User profile management

#### UI Features
- Tab-based navigation system
- Map visualization of friend locations
- Custom map markers for different user states (online/offline)
- Interactive map elements with click handlers
- Custom dialogs for user interactions

## Backend Architecture

### Data Storage
- **Database**: MariaDB
- **Connection Method**: JDBC
- **Driver**: mariadb-java-client
- **Connection Management**: Custom DBConnection class with connection pooling concepts

### Data Access Layer
- **UserDao**: Handles all user-related database operations
  - User authentication
  - User registration
  - Password management
  - Profile updates
- **Models**:
  - User: User account information
  - Friends: Friend relationships
  - MessageIO: Inter-user messaging
  - NowLocation: Real-time location data

### Security Layer
- **Encryption**: AES encryption for sensitive data
- **AesUtil**: Custom encryption utility class
- **Data Protection**:
  - Passwords encrypted before storage
  - Sensitive database credentials encrypted in application code
  - End-to-end encryption for location data transmission

## Core Functionality Flow

### User Authentication Flow
1. User enters credentials in LoginActivity
2. LoginTask executes asynchronously
3. UserDao validates credentials against database
4. Upon success, user data is decrypted and stored in MainApplication
5. User is navigated to TabGroupActivity

### Location Sharing Flow
1. LocationFragment initializes Baidu Maps and location services
2. Location updates are captured via BDLocationListener
3. SendLocationTask periodically transmits encrypted location data
4. GetFriendsLocationsTask retrieves friends' locations
5. Locations are displayed on map with custom markers

### Messaging Flow
1. User initiates message via map marker click or direct interface
2. SendMsgTask transmits message to database
3. Messages are retrieved and displayed in MessageFragment

## Data Models

### User
- userId: Unique identifier
- password: AES encrypted password
- userName: Display name
- pwdProtectId: Password recovery question ID
- pwdProtectA: AES encrypted password recovery answer
- userType: User role/type

### NowLocation
- userId: Associated user
- latitude/longitude: GPS coordinates
- locationDesc: Human-readable location description
- time: Timestamp of location update

### MessageIO
- senderId: Message originator
- receiverId: Message recipient
- content: Message content
- messageType: Type of message

### Friends
- userId: Primary user
- friendId: Friend's user ID
- relationship status data

## Security Implementation

### Data Encryption
- AES 256-bit encryption for sensitive data
- Custom encryption provider for Android 7.0+ compatibility
- Encryption keys stored in application code (needs improvement for production)

### Database Security
- Encrypted database connection credentials
- Prepared statements to prevent SQL injection
- Connection management to prevent resource leaks

### Android Permissions
- Location access (fine and coarse)
- Network state monitoring
- Storage read/write permissions
- Phone state access

## Asynchronous Processing

### AsyncTask Implementation
- **LoginTask**: Handles user authentication
- **SendLocationTask**: Transmits location data
- **GetFriendsLocationsTask**: Retrieves friends' locations
- **ShowFriendsTask**: Fetches friend lists
- **SendMsgTask**: Handles message transmission

### Thread Management
- Handler/Runnable for periodic location updates
- Proper AsyncTask lifecycle management
- Resource cleanup in Fragment lifecycle methods

## Third-Party Integrations

### Baidu Maps SDK
- Location services via LocationClient
- Map rendering with BaiduMap and MapView
- Custom markers and overlays
- Location listeners for real-time updates

### Database Driver
- MariaDB Java Client for JDBC connectivity

## Project Structure

```
app/
├── src/main/java/com/gjf/wherearethey_v2/
│   ├── bean/           # Data models
│   ├── databaseoperation/
│   │   ├── dao/        # Data access objects
│   │   └── dbconnection/ # Database connection management
│   ├── encrypt/        # Encryption utilities
│   ├── fragment/       # UI fragments
│   ├── task/           # AsyncTask implementations
│   ├── util/           # Utility classes
│   ├── MainActivity.java
│   ├── LoginActivity.java
│   ├── MainApplication.java
│   └── TabGroupActivity.java
├── src/main/res/       # Resources (layouts, drawables, etc.)
└── libs/               # External libraries (JAR files)
```

## Performance Considerations

### Memory Management
- Singleton pattern for MainApplication
- Proper resource cleanup in lifecycle methods
- Efficient map rendering with overlay management

### Network Efficiency
- Connection reuse patterns
- Asynchronous operations to prevent UI blocking
- Periodic updates rather than continuous polling

### Battery Optimization
- Configurable location update frequency
- GPS optimization through Baidu Maps SDK
- Foreground service considerations for location tracking

## Future Enhancement Opportunities

1. **Modernization**: Migrate from AsyncTask to modern concurrency (Coroutines/RxJava)
2. **Architecture**: Implement MVVM or MVP pattern for better separation of concerns
3. **Security**: Improve key storage mechanisms for production use
4. **Networking**: Replace direct JDBC with REST API layer
5. **UI**: Modernize interface with Material Design components