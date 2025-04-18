# TeamsLKGhostApp Backend

## Overview

The TeamsLKGhostApp backend is a robust and scalable Spring Boot application designed to serve as the core of a collaborative team management platform. It provides RESTful APIs for managing users, teams, channels, messages, events, and file attachments. The backend is built with a modular architecture to ensure maintainability, scalability, and ease of integration with the frontend

## Application Components

Before diving into the details, let me present the main loosely coupled components of this application:

1. **Frontend**: The frontend is available at [mmConnTeams\_\_FRNTND](https://github.com/SabinGhost19/mmConnTeams__FRNTND.git).
2. **WebSocket Service**: A Node.js service for WebSocket communication, available at [mmConnTeams_WebScktService](https://github.com/SabinGhost19/mmConnTeams_WebScktService.git).
3. **Mail Sender Microservice**: A service for securing the creation of an admin account, available at [mail-sender-microS](https://github.com/SabinGhost19/mail-sender-microS.git).
4. **Spring Boot Backend**: This backend, for which the clone, build, and run steps are enumerated here, including instructions for instantiating the associated database.

For each service and component listed above, refer to the dedicated repository for details on cloning, building, running, and integrating it into your local environment.

### Order of Execution

To run the application, follow this order:

1. **Mail Sender Microservice** (PHP)
2. **WebSocket Service** (Node.js)
3. **PostgreSQL Database** (or PostgreSQL container)
4. **Spring Boot Backend**
5. **Frontend** (Express.js)

## Key Features

### User Management

- **Registration**: Users can register using the `/api/auth/register` endpoint. Passwords are hashed using BCrypt before being stored in the database.
- **Login**: The `/api/auth/login` endpoint authenticates users and generates JWT tokens.
- **Logout**: The `/api/auth/logout` endpoint invalidates the user's session and sets their status to offline.
- **Profile Management**: Users can update their profiles and manage notification preferences.

### Team and Channel Management

- **Teams**: Users can create and join teams. The `TeamController` provides endpoints for managing teams and their members.
- **Channels**: Teams can have multiple channels for focused discussions. The `ChannelController` handles channel-related operations.

### Messaging System

- **Real-Time Messaging**: Messages are sent and received in real-time using WebSocket endpoints.
- **Reactions**: Users can react to messages using the `/messages/{messageId}/reactions` endpoint.
- **Read Status**: The `/channels/{channelId}/read` endpoint marks messages as read.

### Event Management

- **Event Creation**: Users can create events with attendees using the `/events-add` endpoint.
- **Event Retrieval**: The `/events` endpoint retrieves all events for a user.
- **Event Updates**: Events can be updated or deleted using the respective endpoints.

### File Management

- **Azure Blob Storage**: Files are securely uploaded, stored, and retrieved using Azure Blob Storage.
- **Blob Storage Configuration**: The `BlobStorageConfig` class manages the connection to Azure Blob Storage.
- **Endpoints**: The `FileController` provides endpoints for uploading, downloading, and deleting files.

### Security

- **Authentication**: JSON Web Tokens (JWT) are used for secure authentication.
- **Authorization**: Role-based access control ensures that users can only access resources they are permitted to.

### Real-Time Features

- **WebSocket**: Used for real-time messaging and notifications.
- **Event Listeners**: Handle WebSocket events to ensure seamless communication.

## Architecture

The application follows a layered architecture:

1. **Controller Layer**: Handles HTTP requests and responses.
2. **Service Layer**: Contains business logic.
3. **Repository Layer**: Manages database interactions.
4. **Model Layer**: Defines the data structures used across the application.

## Database

- **Relational Database**: The application uses PostgreSQL for data storage.
- **JPA Entities**: Models are mapped to database tables using JPA annotations.
- **Custom Queries**: Complex queries are implemented using JPQL and native SQL queries.

## Workflow and Features

### User Management

- **Registration**: Users register via the `/api/auth/register` endpoint. Passwords are hashed using BCrypt.
- **Login**: The `/api/auth/login` endpoint authenticates users and generates JWT tokens.
- **Logout**: The `/api/auth/logout` endpoint invalidates the user's session and sets their status to offline.

### Team and Channel Management

- **Teams**: Users can create and join teams. The `TeamController` provides endpoints for managing teams and their members.
- **Channels**: Teams can have multiple channels for focused discussions. The `ChannelController` handles channel-related operations.

### Messaging System

- **Real-Time Messaging**: Messages are sent and received in real-time using WebSocket endpoints.
- **Reactions**: Users can react to messages using the `/messages/{messageId}/reactions` endpoint.
- **Read Status**: The `/channels/{channelId}/read` endpoint marks messages as read.

### Event Management

- **Event Creation**: Users can create events with attendees using the `/events-add` endpoint.
- **Event Retrieval**: The `/events` endpoint retrieves all events for a user.
- **Event Updates**: Events can be updated or deleted using the respective endpoints.

### File Management

- **Azure Blob Storage**: Files are securely uploaded, stored, and retrieved using Azure Blob Storage.
- **Blob Storage Configuration**: The `BlobStorageConfig` class manages the connection to Azure Blob Storage.
- **Endpoints**: The `FileController` provides endpoints for uploading, downloading, and deleting files.

## Why We Chose This Theme and Technologies

### Theme Selection

This project was chosen as a personal challenge because it seemed like an interesting topic to explore. The goal was to integrate various technologies and protocols while keeping the architecture loosely coupled. This approach allows for easier deployments and the creation of manifests for testing the application in a Kubernetes cluster environment.

### Technology Stack

1. **Spring Boot**: Chosen for its simplicity, scalability, and extensive ecosystem. It allows rapid development of robust backend services
2. **PostgreSQL**: A powerful, open-source relational database that ensures data integrity and supports complex queries
3. **Docker**: Used for containerization to ensure consistent environments across development, testing, and production
4. **WebSocket**: Enables real-time communication for features like messaging and notifications
5. **Azure Blob Storage**: Provides secure and scalable file storage for attachments and other resources
6. **JWT (JSON Web Tokens)**: Ensures secure authentication and authorization

## Pending Features and Challenges

### Admin-Level Deletions

- **Current Status**: Admin-level deletions of exposed resources are partially implemented.
- **Challenges**:
  - Testing is incomplete for all resource types.
  - Persistence management for deletions is complex due to the use of transactional methods.
  - Additional time is required to ensure complete functionality and reliability.

### Private Chat Functionality

- **Current Status**: Chat management currently supports only shared chats within team channels.
- **Challenges**:
  - Private chat functionality is not yet implemented.
  - Users can only initialize WebSocket connections for team channels where both the current user and the desired user are members.
  - Requires additional endpoints and a new table in the database to support private chats.

### Next Steps

- **Admin-Level Deletions**:
  - Complete testing for all resource types.
  - Refactor persistence logic to ensure transactional integrity.
- **Private Chat Functionality**:
  - Design and implement new database schema for private chats.
  - Add endpoints to support private chat creation and management.
  - Update WebSocket configuration to handle private chat connections.

## How to Run

### Prerequisites

- Java 17 or higher
- Maven 3.8 or higher
- A relational database (e.g., PostgreSQL)

### Steps

1. Clone the repository:
   ```bash
   git clone https://github.com/SabinGhost19/mmConnTeams__BKND.git
   ```
2. Navigate to the project directory:
   ```bash
   cd TeamsLKGhostApp
   ```
3. Configure the database in `src/main/resources/application.properties`.
4. Build the project:
   ```bash
   ./mvnw clean install
   ```
5. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

## How to Run the Database (my favorite option)

### Prerequisites

- Docker installed on your system
- PostgreSQL image available in Docker

### Steps

**Create a New PostgreSQL Container and Restore the Database**
If you prefer to create a new PostgreSQL container, follow these steps:

```bash
docker run -d --name postgres_new -e POSTGRES_PASSWORD=<your_password> -p 5432:5432 postgres
docker cp postgre-dump/backup.dump postgres_new:/tmp/
docker exec postgres_new pg_restore -U postgres -d postgres -v /tmp/backup.dump
```

### Notes

- This dump file includes adequate inserts for testing purposes
- The database schema (tables and relationships) is automatically created by the Spring Boot backend using JPA when the application is run. The dump file is optional and primarily for testing with pre-populated data
