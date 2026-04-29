## 3.1 Functional Requirements

### 1. User Authentication
- The system shall allow users to log in.
- The system shall validate user credentials before granting access.

### 2. Vehicle Management
- The system shall allow users to view available vehicles.

Each vehicle shall include:
- Name
- Type (Car/Bike)
- Availability status
- Basic details

The system shall allow Admin users to:
- Add new vehicles
- Update vehicle details
- Remove vehicles

### 3. Booking System
- The system shall allow users to book a vehicle based on availability.

The system shall ensure:
- A vehicle cannot be booked if it is already reserved for the selected duration.

The system shall allow users to:
- View their booking history.

The system shall store:
- Booking date
- Rental duration
- Vehicle details

### 4. Availability Management
- The system shall maintain the availability status of each vehicle.
- Availability shall be updated when a booking is created.

## 4. System Architecture

### 4.1 Technology Stack
- Frontend: HTML, CSS, JavaScript (Optional)
- Backend: Spring Boot (Java 17)
- Database: PostgreSQL
- Build Tool: Maven
- Testing: JUnit, Mockito
- Logging: SLF4J with Logback

## 5. Development Process Requirements

### Version Control
- The system shall use Git for version control.
- Commit history shall reflect incremental development.

### Database Design
- An Entity Relationship Diagram (ERD) shall be created before implementation.

The database shall include entities such as:
- Users
- Vehicles
- Bookings

### Code Quality
The system shall follow:
- Layered architecture (Controller -> Service -> Repository)
- Separation of concerns
- Clean coding practices

## Validation and Exception Handling
- The system shall validate all input data.
- Invalid inputs shall return appropriate error responses.
- Centralized exception handling shall be implemented.

## Logging
- The system shall log application flow and errors.

## Testing
- Unit tests shall be implemented for core business logic.
- The system shall achieve at least 70% code coverage.
