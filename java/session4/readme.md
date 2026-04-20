# TODO Application Enhancement - Session 4

This module implements a Spring Boot Todo application with focus on:
- clear and maintainable code
- logging in controller and service layers
- unit testing with JUnit and Mockito
- simulated external notification integration

## Tech Stack
- Java 17 (project target)
- Spring Boot 3.2.4
- Maven
- JUnit 5
- Mockito

## Assignment Coverage

### 1. Logging (Mandatory)
SLF4J logging is implemented in:
- Controller layer: `TodoController`
- Service layer: `TodoService`
- Simulated external service: `NotificationServiceClient`

What is logged:
- incoming API requests
- successful operations
- key business events such as TODO creation and notification dispatch

### 2. Unit Testing (JUnit + Mockito)
Unit tests are written for:
- service logic in `TodoServiceTest`
- controller endpoints in `TodoControllerTest`

Test approach:
- JUnit 5 for test structure and assertions
- Mockito for mocking dependencies (`TodoRepository`, `NotificationServiceClient`, `TodoService`)
- positive and negative scenario coverage (found/not found, valid/invalid transition, validation failure)

### 3. Simulated Another Service
A dummy service class `NotificationServiceClient` is added to simulate external integration.

When a TODO is created in `TodoService.createTodo`, this client is invoked:
- Example message: "Notification sent for new Todo"

### 4. README Documentation
This README documents implementation and the testing process requested in the assignment.

## Testing Process Followed

### Step 1: Run unit tests
From this folder (`java/session4`):

```bash
./mvnw.cmd test
```

Expected result:
- all tests pass
- no test failures or errors
Which was achieved in this case.

### Step 2: Run full build verification

```bash
./mvnw.cmd clean install
```

Expected result:
- project compiles
- tests execute successfully
- build completes with `BUILD SUCCESS`

### Step 3: Verify code coverage target (85%+)
Use JaCoCo directly from Maven command line:

```bash
./mvnw.cmd org.jacoco:jacoco-maven-plugin:0.8.12:prepare-agent test org.jacoco:jacoco-maven-plugin:0.8.12:report
```

Coverage report location:
- `target/site/jacoco/index.html`

Acceptance criterion:
- line coverage should be at least 85%

## Input Scenarios Tested

Service layer scenarios:
- create TODO with valid input
- create TODO with null status (defaults to `PENDING`)
- fetch all TODOs
- fetch TODO by ID (exists)
- fetch TODO by ID (not found)
- update TODO with valid status transition
- update TODO with invalid status transition
- delete TODO (exists)
- delete TODO (not found)

Controller layer scenarios:
- create TODO endpoint success
- create TODO validation failure (short title)
- get all TODOs
- get TODO by ID
- update TODO
- delete TODO

## Code Quality Notes
- descriptive class and method names are used across controller, service, and test layers
- tests are designed to be isolated and deterministic via mocking
- service logic is kept focused to maintain readability and maintainability
