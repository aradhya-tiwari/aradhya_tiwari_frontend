# Capstone Vehicle Rental System

Capstone Vehicle Rental System is a full-stack vehicle booking application with role-based access for normal users and adminis. It allows users to sign up, log in, browse vehicles, create bookings, cancel bookings, and rate completed or active rentals. Administrators can manage vehicles and view all bookings across the platform.

## Overview

The project is split into two main parts:

- `backend`: Spring Boot REST API with JWT-based authentication, PostgreSQL persistence, validation, logging, and unit tests
- `frontend`: HTML, CSS, and JavaScript pages that consume the backend API

The application follows a layered backend structure:

- Controller: handles HTTP requests and responses
- Service: contains business logic
- Repository: handles database access through Spring Data JPA

## Core Features

- User signup and login
- JWT-based authentication
- Role-based authorization for `USER` and `ADMIN`
- Vehicle listing for authenticated users
- Vehicle add, update, and delete for admins
- Booking creation with overlap protection
- User booking history
- Booking cancellation
- Booking rating from `0` to `5`
- Admin view of all bookings
- SLF4J logging with Logback
- Unit tests with JUnit and Mockito

## Tech Stack

- Frontend: HTML, TailwindCSS, JavaScript
- Backend: Java 17, Spring Boot 4.0.5
- Database: PostgreSQL
- ORM: Spring Data JPA, Hibernate
- Security: Spring Security, JWT
- Build Tool: Maven Wrapper
- Testing: JUnit 5, Mockito
- Logging: SLF4J with Logback

## Project Structure

- `backend/`: Spring Boot API, database access, security, business logic, and backend tests
- `frontend/`: static HTML pages and JavaScript files for login, booking, vehicle browsing, and admin actions
- `resources/`: supporting documents such as schema draft, SRS notes, and ER diagram
- `readme.md`: This document as main documentation of the project

## Roles and Access

### User

- Sign up and log in
- View available vehicles
- Create bookings
- View personal bookings
- Cancel eligible bookings (only current user's and which has not been canceled)
- Rate active or completed bookings

### Admin

- All authenticated user capabilities
- Add vehicles
- Update vehicles
- Delete vehicles if they do not have active-related bookings
- View all bookings

## Backend Architecture

### Main Packages

- `controller`: REST endpoints for users, vehicles, and bookings
- `service`: business rules such as login validation, booking overlap checks, status updates, and deletion rules
- `repository`: Spring Data JPA repositories
- `entity`: JPA entities for users, vehicles, and bookings
- `dto`: request and response DTOs
- `config`: Spring Security, JWT authentication filter, and CORS setup
- `component`: reusable helpers such as JWT generation, password encoding, and auth cookie handling

### Important Backend Classes

- `UserController`, `VehicleController`, `BookingController`
- `UserService`, `VehicleService`, `BookingService`
- `JwtAuthenticationFilter`
- `SecurityConfig`
- `JwtComponent`

## Authentication and Security

Authentication is handled using JWT.

Current frontend behavior:

- after login or signup, the backend returns a JWT token
- the frontend stores the token in `localStorage`
- the frontend also sends requests with `credentials: include`
- `ApiService` includes the JWT as `Authorization: Bearer <token>`

Backend security rules:

- `/api/users/signup` and `/api/users/login` are public
- `/api/bookings/**` requires authentication
- `GET /api/vehicles/**` requires authentication
- `POST /api/vehicles/**`, `PUT /api/vehicles/**`, and `DELETE /api/vehicles/**` require admin role

## Database Design

The system revolves around three main entities:

### User

- `userId`
- `fullName`
- `email`
- `passwordHash`
- `role`
- `isActive`
- `createdAt`
- `updatedAt`

### Vehicle

- `vehicleId`
- `vehicleName`
- `vehicleType`
- `registrationNumber`
- `pricePerDay`
- `availabilityStatus`
- `basicDetails`
- `imgUrl`
- `isActive`
- `createdAt`
- `updatedAt`

### Booking

- `bookingId`
- `user`
- `vehicle`
- `bookingDate`
- `startDate`
- `endDate`
- `status`
- `rating`
- `createdAt`
- `updatedAt`

Booking statuses used in the backend:

- `PENDING`
- `CONFIRMED`
- `ACTIVE`
- `COMPLETED`
- `CANCELLED`

Reference files:

- schema draft: [resources/schema.sql](C:\Users\aradh\Desktop\nucleusTeq\Repo\nucles-teq-assignement\Capstone_VechicleRentalSystem\resources\schema.sql)
- requirements: [resources/srs.md](C:\Users\aradh\Desktop\nucleusTeq\Repo\nucles-teq-assignement\Capstone_VechicleRentalSystem\resources\srs.md)

## Booking Logic

The booking flow is one of the most important parts of the project.

When a user creates a booking:

1. the request dates are validated
2. the user is identified from the JWT
3. the selected vehicle is loaded from the database
4. the service checks that the vehicle is active
5. the service checks for overlapping bookings with statuses `PENDING`, `CONFIRMED`, or `ACTIVE`
6. if no conflict exists, the booking is stored as `CONFIRMED`

### Overlap Rule

A vehicle cannot be booked for dates that collide with another active-like booking.

Equivalent logic:

- existing booking start date <= requested end date
- existing booking end date >= requested start date

### Automatic Status Update

Booking status is updated dynamically when bookings are read:

- before start date -> `CONFIRMED`
- between start and end date -> `ACTIVE`
- after end date -> `COMPLETED`
- `CANCELLED` and `COMPLETED` are not recalculated

## API Summary

### User API

`POST /api/users/signup`

- creates a new account
- request body:

```json
{
  "fullName": "Aradhya ...",
  "email": "sample@gmail.com",
  "password": "1234567890",
  "role": "USER"
}
```

`POST /api/users/login`

- logs in an existing user
- request body:

```json
{
  "email": "sample@gmail.com",
  "password": "1234567890"
}
```

### Vehicle API

`GET /api/vehicles`

- returns all vehicles for authenticated users

`GET /api/vehicles/{vehicleId}`

- returns one vehicle by id

`POST /api/vehicles`

- admin only
- creates a vehicle

`PUT /api/vehicles/{vehicleId}`

- admin only
- updates a vehicle

`DELETE /api/vehicles/{vehicleId}`

- admin only
- deletes a vehicle only if it has no `PENDING`, `CONFIRMED`, or `ACTIVE` bookings

Example vehicle payload:

```json
{
  "vehicleName": "Honda City",
  "vehicleType": "CAR",
  "registrationNumber": "MP12AB1234",
  "pricePerDay": 2500,
  "availabilityStatus": true,
  "basicDetails": "Automatic transmission, petrol, AC",
  "imgUrl": "https://example.com/car.png", // does not exists 
  "isActive": true
}
```

### Booking API

`POST /api/bookings`

- creates a booking for the logged-in user

```json
{
  "vehicleId": 1,
  "startDate": "2026-05-10",
  "endDate": "2026-05-12"
}
```

`GET /api/bookings/my`

- returns bookings of the current user

`PUT /api/bookings/{bookingId}/cancel`

- cancels the logged-in user's own booking

`GET /api/bookings/all`

- admin only
- returns all bookings

`GET /api/bookings/vehicle/{vehicleId}`

- returns all bookings for a specific vehicle

`PUT /api/bookings/{bookingId}/rating`

- updates a rating for the current user's booking

```json
{
  "rating": 5
}
```

## Frontend Pages

### `index.html`

- login and signup entry page

### `app.html`

- landing page after login
- shows logged-in user name and role

### `vehicles.html`

- lists vehicles for normal users
- includes search and filtering
- supports booking a selected vehicle

### `bookings.html`

- shows the logged-in user's bookings
- supports cancellation
- supports rating updates

### `admin.html`

- admin dashboard for vehicle management

### `admin-bookings.html`

- admin page for viewing all bookings

## Frontend JavaScript Modules

- `authService.js`: handles login, signup, token storage, role lookup, and logout
- `apiService.js`: handles authenticated GET, POST, PUT, DELETE requests
- `index.js`: login and signup form behavior
- `app.js`: dashboard logic after login
- `vehicles.js`: vehicle browsing and booking modal
- `bookings.js`: user booking listing, cancellation, and ratings
- `admin.js`: admin vehicle CRUD operations
- `admin-bookings.js`: admin booking listing

## Configuration

Backend configuration is currently in [backend/src/main/resources/application.properties](C:\Users\aradh\Desktop\nucleusTeq\Repo\nucles-teq-assignement\Capstone_VechicleRentalSystem\backend\src\main\resources\application.properties).

Current defaults:

- database: PostgreSQL
- database URL: `jdbc:postgresql://localhost:5432/capstone_vrs`
- username: `abcd`
- password: `vrs12345`
- JWT expiration: `86400000` milliseconds = 60 * 60 * 24 (1 day)

## Testing

The backend contains unit tests for:

- services
- components
- controllers

Current test stack:

- JUnit 5
- Mockito

## Logging

The backend now uses SLF4J with Logback.

Logging is configured in:

- [backend/src/main/resources/logback-spring.xml](C:\Users\aradh\Desktop\nucleusTeq\Repo\nucles-teq-assignement\Capstone_VechicleRentalSystem\backend\src\main\resources\logback-spring.xml)

Logging covers:

- authentication flow
- vehicle operations
- booking operations
- service-level validation failures
- automatic booking status changes

## Validation and Error Handling

Examples of validation already implemented:

- required request fields
- only future-or-present booking start date
- end date cannot be before start date
- duplicate email rejection
- duplicate registration number rejection
- rating must be between `0` and `5`
- users can cancel or rate only their own bookings

Typical error response format:

```json
{
  "message": "Vehicle not found"
}
```

## Known Notes and Limitations

- the root and backend readme files were originally empty; this file is the main project documentation
- the frontend stores JWT in `localStorage`, which is simple for learning but not ideal for production security, I have made arrangements to simply switch to http-only cookie when needed.
- some frontend files contain hard-coded API URLs pointing to `http://localhost:5500`
- there are a few naming inconsistencies in older resource files compared with the current JPA entities
- one backend unit test currently has an assertion/data capitalization mismatch in `VehicleServiceTest`
- the repository name contains a typo: `Vechicle` instead of `Vehicle`


## Authoring Notes

Supporting project resources:

- ER diagram: `resources/er-diagram.excalidraw`, to run it I use excalidraw extension
- requirements summary: `resources/srs.md`
- SQL draft: `resources/schema.sql` 