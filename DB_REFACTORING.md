# Database Module Refactoring Documentation

## Overview
This document describes the refactoring of the database module to support interface-based calls with runtime switching capability. The refactoring enables the application to switch between different data access implementations (e.g., JDBC, REST API) at runtime through configuration.

## Architecture

### Interface Layer
All Data Access Objects (DAOs) now implement corresponding interfaces:
- `IUserDao` - User data access interface
- `INowLocationDao` - Location data access interface
- `IMessageIODao` - Message data access interface
- `IFriendsDao` - Friends data access interface

### Factory Pattern
A `DaoFactory` class has been introduced to manage the creation and switching of DAO implementations:
- Provides singleton access to DAO instances
- Supports runtime switching between implementations
- Configuration stored in SharedPreferences
- Default implementation is JDBC for backward compatibility

### Implementation Layer
Current implementations include:
- JDBC-based DAOs (existing implementation)
- Future REST API-based DAOs (to be implemented)

## Key Changes

### 1. Interface Creation
Each existing DAO class now implements a corresponding interface, defining the contract for data access operations.

### 2. Factory Implementation
The `DaoFactory` class provides:
- Static methods to get DAO instances
- Runtime switching capability through `setDefaultImplType()`
- Configuration persistence in SharedPreferences
- Backward compatibility with existing JDBC implementation

### 3. Task Class Updates
Asynchronous task classes now use the factory to obtain DAO instances:
- `LoginTask` updated to use `IUserDao`
- `SendLocationTask` updated to use `INowLocationDao`
- `SendMsgTask` updated to use `IMessageIODao`
- Other task classes can be updated similarly

## Configuration

### Runtime Switching
To switch between implementations at runtime:
```java
// Switch to JDBC implementation
DaoFactory.setDefaultImplType(DaoFactory.IMPL_TYPE_JDBC);

// Switch to REST API implementation (when available)
DaoFactory.setDefaultImplType(DaoFactory.IMPL_TYPE_REST);
```

### Default Configuration
The application reads the implementation type from SharedPreferences:
- Key: `db_impl_type`
- Default value: `jdbc`

## Benefits

1. **Flexibility**: Easy switching between data access implementations
2. **Maintainability**: Clear separation of interface and implementation
3. **Extensibility**: Simple to add new implementations (e.g., REST API)
4. **Backward Compatibility**: Existing JDBC implementation remains the default
5. **Testability**: Interfaces make unit testing easier with mocks

## Future Enhancements

1. **REST API Implementation**: Create REST-based implementations of all DAO interfaces
2. **Additional Data Sources**: Support for other databases or cloud services
3. **Caching Layer**: Add caching mechanisms for improved performance
4. **Connection Pooling**: Enhanced connection management for high-load scenarios

## Usage Examples

### Getting a DAO Instance
```java
// Get default implementation
IUserDao userDao = DaoFactory.getUserDao();

// Get specific implementation
IUserDao userDao = DaoFactory.getUserDao(DaoFactory.IMPL_TYPE_JDBC);
```

### Setting the Default Implementation
```java
// Set and persist the default implementation
DaoFactory.setDefaultImplType(DaoFactory.IMPL_TYPE_JDBC);
```

## Migration Guide

1. Update task classes to use interfaces instead of concrete classes
2. Obtain DAO instances through `DaoFactory` instead of direct singleton access
3. Test with existing JDBC implementation to ensure compatibility
4. Prepare for future REST API implementation by following the interface contracts