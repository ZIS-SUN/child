# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a kindergarten management system (幼儿园信息管理系统) with a Spring Boot backend and Vue 3 frontend. The system serves three user types: principals/administrators (PRINCIPAL), teachers (TEACHER), and parents (PARENT), plus a public-facing website.

## Architecture

### Backend (Spring Boot + MyBatis Plus)

**Technology Stack:**
- Spring Boot 2.7.18 (Java 1.8)
- Spring Security + JWT authentication
- MyBatis Plus 3.5.3.1 for database access
- MySQL database
- Knife4j (Swagger) for API documentation

**Package Structure:**
```
com.kindergarten/
├── config/          # Configuration classes (Security, MyBatis, Web, Knife4j)
├── controller/      # REST API endpoints (Admin*, Parent*, Public*)
├── service/         # Business logic layer
├── mapper/          # MyBatis mappers
├── entity/          # Database entities
├── dto/             # Data Transfer Objects
├── vo/              # Value Objects for responses
├── filter/          # Security filters
├── util/            # Utility classes (JwtUtil, etc.)
└── common/          # Common classes and enums
```

**Key Entities:**
- SysUser, ParentInfo, TeacherInfo - User management
- ChildInfo, ClassInfo - Children and class information
- Attendance, LeaveRequest, ChildHealth - Daily operations
- Notice, Album, AlbumPhoto - Content management
- WeeklyMenu, Enrollment - Additional features

**Controllers follow naming pattern:**
- `Admin*Controller` - Admin/teacher management endpoints (requires PRINCIPAL/TEACHER role)
- `Parent*Controller` - Parent portal endpoints (requires PARENT role)
- `PublicController` - Public website endpoints (no authentication)

### Frontend (Vue 3 + Element Plus + Vite)

**Technology Stack:**
- Vue 3.5.22 with Composition API (`<script setup>`)
- Element Plus 2.11.5 for UI components
- Vue Router 4.6.3 for routing
- Pinia 3.0.3 for state management
- Axios 1.12.2 for HTTP requests
- Vite 7.1.7 for build tooling

**Project Structure:**
```
frontend/src/
├── api/             # API service modules (auth.js, admin.js, parent.js, public.js)
├── store/           # Pinia stores (user.js)
├── router/          # Vue Router configuration
├── views/           # Page components
│   ├── admin/      # Admin management pages
│   ├── parent/     # Parent portal pages
│   └── public/     # Public website pages
├── utils/           # Utilities (request.js, export.js)
└── main.js          # Application entry point
```

**Routing Structure:**
- `/public/*` - Public website (home, notices, albums, enrollment, teachers, menu)
- `/parent/*` - Parent portal (requires PARENT authentication)
- `/admin/*` - Admin dashboard (requires PRINCIPAL/TEACHER authentication)
- `/login`, `/register` - Authentication pages

**API Request Configuration:**
- Base URL: `http://localhost:8080/api`
- JWT token stored in localStorage, automatically added to request headers
- Centralized error handling with ElMessage notifications
- 401 responses redirect to login

## Development Commands

### Backend
```bash
# Navigate to backend directory
cd backend

# Compile project (requires Maven)
mvn clean compile

# Run tests
mvn test

# Package application
mvn clean package

# Run application
mvn spring-boot:run
# Or run the JAR directly
java -jar target/kindergarten-management-1.0.0.jar

# Application will start on http://localhost:8080/api
# API documentation: http://localhost:8080/api/doc.html
```

### Frontend
```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Start development server (http://localhost:5173)
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview
```

## Database Configuration

**Database:** MySQL with name `ultrathink`
**Connection:** Configured in `backend/src/main/resources/application.yml`
- Default: `jdbc:mysql://localhost:3306/ultrathink`
- Username: `root`
- Password: `1234` (update for your environment)

**MyBatis Plus Configuration:**
- Auto-increment ID generation
- Logic delete support (deleted field: 0=active, 1=deleted)
- Automatic camel case mapping
- Mapper XMLs located in `classpath*:/mapper/**/*.xml`

## Authentication & Security

**JWT Configuration:**
- Secret key configured in `application.yml` (jwt.secret)
- Token expiration: 24 hours (86400000ms)
- Header: `Authorization: Bearer <token>`

**User Roles:**
- `PRINCIPAL` - Full system access
- `TEACHER` - Teacher management functions
- `PARENT` - Parent portal access

**Route Guards:**
- Frontend router checks `meta.requiresAuth` and validates JWT token
- Backend uses Spring Security with JWT filter
- Unauthorized requests return 401 and redirect to login

## File Upload

**Configuration:**
- Upload path: `/Users/zishen/Desktop/项目1/backend/uploads/` (update for your environment)
- Max file size: 5MB
- Allowed types: jpg, jpeg, png, webp, gif
- File upload handled by `FileUploadController`

## Key Integration Points

**Parent-Child Relationship:**
- Parents link to children via `ParentChildRelation` entity
- Children belong to classes via `ClassInfo`

**Teacher-Class Assignment:**
- Teachers assigned to classes via `TeacherClassRelation` entity

**Notice System:**
- Notices created by admins/teachers
- Read tracking via `NoticeRead` entity

**Attendance & Leave:**
- Daily attendance tracked in `Attendance` entity
- Leave requests managed via `LeaveRequest` with approval workflow

**Health Monitoring:**
- General health records in `ChildHealth`
- Daily temperature checks in `TemperatureRecord`

## Logging

- Backend logging configured for `com.kindergarten` at DEBUG level
- Spring Security logging at DEBUG level
- Console pattern: `%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{50} - %msg%n`

## Code Conventions

**Backend:**
- Use Lombok annotations (@Data, @Builder, @Slf4j)
- REST responses follow common format (likely in common package)
- Service layer contains business logic, controllers are thin
- Validation annotations on DTOs

**Frontend:**
- Vue 3 Composition API with `<script setup>` syntax
- Element Plus components for UI consistency
- Centralized API calls in `/api` modules
- Pinia stores for shared state
- Async/await for API calls

## Important Notes

- Backend context path is `/api` - all endpoints are prefixed
- Frontend development server proxies API calls to backend
- Images and uploads stored in backend/uploads directory
- Database schema likely initialized via SQL scripts or MyBatis Plus auto-generation
- Knife4j provides interactive API documentation at `/api/doc.html`
