# Task Manager API

A robust REST API for task management built with Spring Boot. This application provides comprehensive task management capabilities with secure user authentication.

## 🚀 Features

### Current Features
- User Authentication with JWT
- Task Management (CRUD operations)
- Secure API endpoints
- Global Exception Handling
- Docker Support
- Database Integration (H2 In-Memory)

### Planned Features
- Unit and Integration Testing
- API Documentation (Swagger/OpenAPI)
- Advanced Task Features (Categories, Priority, Due Dates)
- Enhanced Security Features
- Caching Implementation
- Performance Optimization
- Audit Logging

## 🛠 Technology Stack

- **Framework:** Spring Boot
- **Language:** Java
- **Build Tool:** Maven
- **Database:** SQL Database with JPA/Hibernate
- **Security:** Spring Security with JWT
- **Containerization:** Docker

## 📋 Prerequisites

- Java 17 or higher
- Maven
- Docker (optional)
- Your favorite IDE (VS Code, IntelliJ IDEA, etc.)

## 🚀 Getting Started

1. **Clone the repository**
   ```bash
   git clone [your-repository-url]
   ```

2. **Navigate to the project directory**
   ```bash
   cd taskmanager
   ```

3. **Build the project**
   ```bash
   mvn clean install
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

The application will start running at `http://localhost:8080`

## 🔒 API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login user

### Tasks
- `GET /api/tasks` - Get all tasks
- `GET /api/tasks/{id}` - Get task by ID
- `POST /api/tasks` - Create new task
- `PUT /api/tasks/{id}` - Update task
- `DELETE /api/tasks/{id}` - Delete task

## 🔐 Security

- JWT based authentication
- Password encryption
- Protected endpoints
- Role-based access control (coming soon)

## 🧪 Testing (Coming Soon)

- Unit Tests
- Integration Tests
- API Tests
- Test Coverage Reports

## 📚 Documentation (Coming Soon)

- API Documentation with Swagger/OpenAPI
- Detailed API endpoints documentation
- Request/Response examples

## 🤝 Contributing

This is a personal project for learning and demonstration purposes. However, feedback and suggestions are welcome!

## 👤 Author

[Your Name]
- LinkedIn: Putri Khaireen Jasmin
- GitHub: itskhaireen

---
*This project is continuously being enhanced with new features and improvements.* 