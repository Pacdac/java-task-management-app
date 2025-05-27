# Task Management API Documentation

This document provides details about the REST API endpoints available in the Task Management application.

## Table of Contents

1. [Overview](#overview)
2. [Base URL](#base-url)
3. [Authentication](#authentication)
4. [Task Endpoints](#task-endpoints)
5. [User Endpoints](#user-endpoints)
6. [Task Status Endpoints](#task-status-endpoints)
7. [Task Category Endpoints](#task-category-endpoints)
8. [Task Priority Endpoints](#task-priority-endpoints)
9. [API Status](#api-status)
10. [Error Handling](#error-handling)
11. [Postman Collection](#postman-collection)
12. [cURL Examples](#curl-examples)

## Overview

The Task Management API provides a RESTful interface for managing tasks, users, task statuses, task categories, and task priorities. It follows standard HTTP methods and uses JSON for data exchange.

## Base URL

All API endpoints are relative to the base URL:

```
http://localhost:8080/api
```

## Authentication

The API uses JWT (JSON Web Token) based authentication. To access protected endpoints, you need to:

1. Register a user or use an existing account
2. Authenticate with your credentials to receive a JWT token
3. Include the token in the Authorization header for subsequent requests

### Authentication Endpoints

#### Registration

To use the API, first register a user account.

- **URL**: `/auth/register`
- **Method**: `POST`
- **Request Body**: Registration data including credentials
- **Example Request**:

```json
{
  "username": "marksmith",
  "email": "mark@example.com",
  "password": "securepassword123",
  "firstName": "Mark",
  "lastName": "Smith"
}
```

- **Response**: Created user object (without password)
- **Notes**:
  - Passwords are automatically hashed and stored securely
  - Users are assigned the `USER` role by default

#### Login

Authenticates a user and returns a JWT token.

- **URL**: `/auth/login`
- **Method**: `POST`
- **Request Body**: Authentication credentials
- **Example Request**:

```json
{
  "username": "johndoe",
  "password": "password123"
}
```

- **Response**: JWT token
- **Example Response**:

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huZG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE1MTYyMzkwMjJ9.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
}
```

### Using JWT Tokens

After receiving a token, include it in the `Authorization` header for all subsequent API requests:

```
Authorization: Bearer <your_jwt_token>
```

#### JWT Token Details

- **Expiration**: Tokens expire after 24 hours, after which you'll need to authenticate again
- **Token Format**: Standard JWT with header, payload, and signature
- **Claims**: Contains user's username and roles as claims
- **Security**: Tokens are signed with HMAC-SHA256 to prevent tampering

#### Protected Endpoints

All API endpoints except for the following require a valid JWT token:

- `/status` (API status endpoint)
- `/auth/**` (authentication endpoints)

#### Error Responses

When authentication fails, you may receive the following responses:

- **401 Unauthorized**: Invalid or expired token
- **403 Forbidden**: Valid token but insufficient permissions for the requested resource

### Security Roles

Users have different roles which determine their access privileges:

- `USER`: Standard user with access to basic task operations
- `ADMIN`: Administrator with access to all operations including admin-specific endpoints

### Authentication Examples

#### cURL Examples

**Login and extract token:**

```bash
# Login and get token
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username": "johndoe", "password": "password123"}' | jq -r '.token'
```

**Using the token to access a protected endpoint:**

```bash
# Set token as variable (replace with your actual token)
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

# Access protected endpoint
curl -X GET "http://localhost:8080/api/tasks" \
  -H "Authorization: Bearer $TOKEN"
```

#### Postman Setup

1. **Create a Login Request**:

   - Create a POST request to `/api/auth/login`
   - Set body to raw JSON with username and password
   - Add a Test script to extract and store the token:

   ```javascript
   let responseJson = pm.response.json();
   if (responseJson.token) {
     pm.environment.set("jwtToken", responseJson.token);
   }
   ```

2. **Use Token in Subsequent Requests**:
   - In your request headers, add:
   ```
   Authorization: Bearer {{jwtToken}}
   ```
   - This uses the environment variable set from the login request

## Task Endpoints

### Get All Tasks

Retrieves a list of all tasks.

- **URL**: `/tasks`
- **Method**: `GET`
- **Authorization**: Requires `USER` or `ADMIN` role
- **Response**: Array of task objects
- **Example Response**:

```json
[
  {
    "id": 1,
    "title": "Implement frontend components",
    "description": "Create Angular components for the task management app",
    "dueDate": "2025-06-01",
    "priority": 2,
    "createdAt": "2025-05-21T10:00:00Z",
    "updatedAt": "2025-05-21T10:00:00Z",
    "userId": 1,
    "username": "johndoe",
    "statusId": 1,
    "statusName": "To Do",
    "categoryId": 1,
    "categoryName": "Feature"
  },
  {
    "id": 2,
    "title": "Fix navigation issue",
    "description": "Fix the navigation menu not displaying correctly on mobile",
    "dueDate": "2025-05-25",
    "priority": 3,
    "createdAt": "2025-05-20T14:30:00Z",
    "updatedAt": "2025-05-20T14:30:00Z",
    "userId": 2,
    "username": "janedoe",
    "statusId": 2,
    "statusName": "In Progress",
    "categoryId": 2,
    "categoryName": "Bug"
  }
]
```

### Get Task by ID

Retrieves a specific task by its ID.

- **URL**: `/tasks/{id}`
- **Method**: `GET`
- **Authorization**: Requires `USER` or `ADMIN` role
- **URL Parameters**: `id=[integer]` - Task ID
- **Example**: `/tasks/1`
- **Response**: Task object
- **Example Response**:

```json
{
  "id": 1,
  "title": "Implement frontend components",
  "description": "Create Angular components for the task management app",
  "dueDate": "2025-06-01",
  "priority": 2,
  "createdAt": "2025-05-21T10:00:00Z",
  "updatedAt": "2025-05-21T10:00:00Z",
  "userId": 1,
  "username": "johndoe",
  "statusId": 1,
  "statusName": "To Do",
  "categoryId": 1,
  "categoryName": "Feature"
}
```

### Get Tasks by User ID

Retrieves all tasks assigned to a specific user.

- **URL**: `/tasks/user/{userId}`
- **Method**: `GET`
- **Authorization**: Requires `USER` or `ADMIN` role
- **URL Parameters**: `userId=[integer]` - User ID
- **Example**: `/tasks/user/1`
- **Response**: Array of task objects

### Create Task

Creates a new task.

- **URL**: `/tasks`
- **Method**: `POST`
- **Authorization**: Requires `USER` or `ADMIN` role
- **Request Body**: Task data
- **Example Request**:

```json
{
  "title": "Implement authorization",
  "description": "Implement JWT authorization for the API",
  "dueDate": "2025-06-15",
  "priority": 1,
  "userId": 1,
  "statusId": 1,
  "categoryId": 1
}
```

- **Response**: Created task object with ID

### Update Task

Updates an existing task.

- **URL**: `/tasks/{id}`
- **Method**: `PUT`
- **Authorization**: Requires `USER` or `ADMIN` role
- **URL Parameters**: `id=[integer]` - Task ID
- **Request Body**: Updated task data
- **Example**: `/tasks/1`
- **Example Request**:

```json
{
  "title": "Implement JWT authorization",
  "description": "Implement JWT authorization for the API with refresh tokens",
  "dueDate": "2025-06-20",
  "priority": 1,
  "userId": 1,
  "statusId": 2,
  "categoryId": 1
}
```

- **Response**: Updated task object

### Delete Task

Deletes a task.

- **URL**: `/tasks/{id}`
- **Method**: `DELETE`
- **Authorization**: Requires `USER` or `ADMIN` role
- **URL Parameters**: `id=[integer]` - Task ID
- **Example**: `/tasks/1`
- **Response**: HTTP 204 (No Content)

### Search Tasks

Searches tasks by title (case-insensitive).

- **URL**: `/tasks/search`
- **Method**: `GET`
- **Authorization**: Requires `USER` or `ADMIN` role
- **Query Parameters**: `keyword=[string]` - Search term
- **Example**: `/tasks/search?keyword=implement`
- **Response**: Array of matching task objects

### Get Overdue Tasks

Retrieves all overdue tasks for a specific user.

- **URL**: `/tasks/overdue/{userId}`
- **Method**: `GET`
- **Authorization**: Requires `USER` or `ADMIN` role
- **URL Parameters**: `userId=[integer]` - User ID
- **Example**: `/tasks/overdue/1`
- **Response**: Array of overdue task objects

## User Endpoints

### Get All Users

Retrieves a list of all users.

- **URL**: `/users`
- **Method**: `GET`
- **Authorization**: Requires `USER` or `ADMIN` role
- **Response**: Array of user objects
- **Example Response**:

```json
[
  {
    "id": 1,
    "username": "johndoe",
    "email": "john@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "createdAt": "2025-05-01T09:00:00Z",
    "updatedAt": "2025-05-01T09:00:00Z"
  },
  {
    "id": 2,
    "username": "janedoe",
    "email": "jane@example.com",
    "firstName": "Jane",
    "lastName": "Doe",
    "createdAt": "2025-05-02T10:30:00Z",
    "updatedAt": "2025-05-02T10:30:00Z"
  }
]
```

### Get User by ID

Retrieves a specific user by their ID.

- **URL**: `/users/{id}`
- **Method**: `GET`
- **Authorization**: Requires `USER` or `ADMIN` role
- **URL Parameters**: `id=[integer]` - User ID
- **Example**: `/users/1`
- **Response**: User object

### Get User by Username

Retrieves a specific user by their username.

- **URL**: `/users/username/{username}`
- **Method**: `GET`
- **Authorization**: Requires `USER` or `ADMIN` role
- **URL Parameters**: `username=[string]` - Username
- **Example**: `/users/username/johndoe`
- **Response**: User object

### Create User

Creates a new user.

- **URL**: `/users`
- **Method**: `POST`
- **Authorization**: Requires `USER` or `ADMIN` role
- **Request Body**: User data
- **Example Request**:

```json
{
  "username": "marksmith",
  "email": "mark@example.com",
  "password": "securepassword123",
  "firstName": "Mark",
  "lastName": "Smith"
}
```

- **Response**: Created user object (without password)

### Update User

Updates an existing user.

- **URL**: `/users/{id}`
- **Method**: `PUT`
- **URL Parameters**: `id=[integer]` - User ID
- **Request Body**: Updated user data
- **Example**: `/users/1`
- **Example Request**:

```json
{
  "username": "johndoe",
  "email": "john.doe@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "password": "newpassword123" // Optional - only include to change password
}
```

- **Response**: Updated user object (without password)

### Delete User

Deletes a user.

- **URL**: `/users/{id}`
- **Method**: `DELETE`
- **URL Parameters**: `id=[integer]` - User ID
- **Example**: `/users/1`
- **Response**: HTTP 204 (No Content)

## Task Status Endpoints

### Get All Task Statuses

Retrieves a list of all task statuses.

- **URL**: `/task-statuses`
- **Method**: `GET`
- **Authorization**: Requires `USER` or `ADMIN` role
- **Response**: Array of task status objects
- **Example Response**:

```json
[
  {
    "id": 1,
    "name": "To Do",
    "description": "Tasks that need to be started",
    "color": "#3498db"
  },
  {
    "id": 2,
    "name": "In Progress",
    "description": "Tasks currently being worked on",
    "color": "#f39c12"
  },
  {
    "id": 3,
    "name": "Review",
    "description": "Tasks that need review",
    "color": "#9b59b6"
  },
  {
    "id": 4,
    "name": "Done",
    "description": "Completed tasks",
    "color": "#2ecc71"
  }
]
```

### Get Task Status by ID

Retrieves a specific task status by its ID.

- **URL**: `/task-statuses/{id}`
- **Method**: `GET`
- **Authorization**: Requires `USER` or `ADMIN` role
- **URL Parameters**: `id=[integer]` - Status ID
- **Example**: `/task-statuses/1`
- **Response**: Task status object

### Get Task Status by Name

Retrieves a specific task status by its name.

- **URL**: `/task-statuses/name/{name}`
- **Method**: `GET`
- **Authorization**: Requires `USER` or `ADMIN` role
- **URL Parameters**: `name=[string]` - Status name
- **Example**: `/task-statuses/name/To%20Do`
- **Response**: Task status object

### Create Task Status

Creates a new task status.

- **URL**: `/task-statuses`
- **Method**: `POST`
- **Authorization**: Requires `ADMIN` role
- **Request Body**: Task status data
- **Example Request**:

```json
{
  "name": "Blocked",
  "description": "Tasks that are blocked by dependencies",
  "color": "#e74c3c"
}
```

- **Response**: Created task status object

### Update Task Status

Updates an existing task status.

- **URL**: `/task-statuses/{id}`
- **Method**: `PUT`
- **Authorization**: Requires `ADMIN` role
- **URL Parameters**: `id=[integer]` - Status ID
- **Request Body**: Updated task status data
- **Example**: `/task-statuses/5`
- **Example Request**:

```json
{
  "name": "Blocked",
  "description": "Tasks that are blocked by external dependencies",
  "color": "#c0392b"
}
```

- **Response**: Updated task status object

### Delete Task Status

Deletes a task status.

- **URL**: `/task-statuses/{id}`
- **Method**: `DELETE`
- **Authorization**: Requires `ADMIN` role
- **URL Parameters**: `id=[integer]` - Status ID
- **Example**: `/task-statuses/5`
- **Response**: HTTP 204 (No Content)

## Task Category Endpoints

### Get All Task Categories

Retrieves a list of all task categories.

- **URL**: `/task-categories`
- **Method**: `GET`
- **Authorization**: Requires `USER` or `ADMIN` role
- **Response**: Array of task category objects
- **Example Response**:

```json
[
  {
    "id": 1,
    "name": "Feature",
    "description": "New features or functionality",
    "color": "#e74c3c"
  },
  {
    "id": 2,
    "name": "Bug",
    "description": "Issues that need to be fixed",
    "color": "#e67e22"
  },
  {
    "id": 3,
    "name": "Documentation",
    "description": "Documentation related tasks",
    "color": "#1abc9c"
  }
]
```

### Get Task Category by ID

Retrieves a specific task category by its ID.

- **URL**: `/task-categories/{id}`
- **Method**: `GET`
- **Authorization**: Requires `USER` or `ADMIN` role
- **URL Parameters**: `id=[integer]` - Category ID
- **Example**: `/task-categories/1`
- **Response**: Task category object

### Get Task Category by Name

Retrieves a specific task category by its name.

- **URL**: `/task-categories/name/{name}`
- **Method**: `GET`
- **Authorization**: Requires `USER` or `ADMIN` role
- **URL Parameters**: `name=[string]` - Category name
- **Example**: `/task-categories/name/Feature`
- **Response**: Task category object

### Create Task Category

Creates a new task category.

- **URL**: `/task-categories`
- **Method**: `POST`
- **Authorization**: Requires `ADMIN` role
- **Request Body**: Task category data
- **Example Request**:

```json
{
  "name": "Infrastructure",
  "description": "Infrastructure related tasks",
  "color": "#8e44ad"
}
```

- **Response**: Created task category object

### Update Task Category

Updates an existing task category.

- **URL**: `/task-categories/{id}`
- **Method**: `PUT`
- **Authorization**: Requires `ADMIN` role
- **URL Parameters**: `id=[integer]` - Category ID
- **Request Body**: Updated task category data
- **Example**: `/task-categories/6`
- **Example Request**:

```json
{
  "name": "Infrastructure",
  "description": "Tasks related to infrastructure and deployment",
  "color": "#8e44ad"
}
```

- **Response**: Updated task category object

### Delete Task Category

Deletes a task category.

- **URL**: `/task-categories/{id}`
- **Method**: `DELETE`
- **Authorization**: Requires `ADMIN` role
- **URL Parameters**: `id=[integer]` - Category ID
- **Example**: `/task-categories/6`
- **Response**: HTTP 204 (No Content)

## Task Priority Endpoints

### Get All Task Priorities

Retrieves a list of all task priorities.

- **URL**: `/task-priorities`
- **Method**: `GET`
- **Authorization**: Requires `USER` or `ADMIN` role
- **Response**: Array of task priority objects
- **Example Response**:

```json
[
  {
    "id": 1,
    "name": "Low",
    "description": "Low priority tasks",
    "value": 1,
    "color": "#3498db"
  },
  {
    "id": 2,
    "name": "Medium",
    "description": "Medium priority tasks",
    "value": 2,
    "color": "#f39c12"
  },
  {
    "id": 3,
    "name": "High",
    "description": "High priority tasks",
    "value": 3,
    "color": "#e74c3c"
  }
]
```

### Get Task Priority by ID

Retrieves a specific task priority by its ID.

- **URL**: `/task-priorities/{id}`
- **Method**: `GET`
- **Authorization**: Requires `USER` or `ADMIN` role
- **URL Parameters**: `id=[integer]` - Priority ID
- **Example**: `/task-priorities/1`
- **Response**: Task priority object

### Get Task Priority by Name

Retrieves a specific task priority by its name.

- **URL**: `/task-priorities/name/{name}`
- **Method**: `GET`
- **Authorization**: Requires `USER` or `ADMIN` role
- **URL Parameters**: `name=[string]` - Priority name
- **Example**: `/task-priorities/name/High`
- **Response**: Task priority object

### Get Task Priority by Value

Retrieves a specific task priority by its value.

- **URL**: `/task-priorities/value/{value}`
- **Method**: `GET`
- **Authorization**: Requires `USER` or `ADMIN` role
- **URL Parameters**: `value=[integer]` - Priority value
- **Example**: `/task-priorities/value/3`
- **Response**: Task priority object

### Create Task Priority

Creates a new task priority.

- **URL**: `/task-priorities`
- **Method**: `POST`
- **Authorization**: Requires `ADMIN` role
- **Request Body**: Task priority data
- **Example Request**:

```json
{
  "name": "Critical",
  "description": "Critical priority tasks that need immediate attention",
  "value": 4,
  "color": "#c0392b"
}
```

- **Response**: Created task priority object

### Update Task Priority

Updates an existing task priority.

- **URL**: `/task-priorities/{id}`
- **Method**: `PUT`
- **Authorization**: Requires `ADMIN` role
- **URL Parameters**: `id=[integer]` - Priority ID
- **Request Body**: Updated task priority data
- **Example**: `/task-priorities/4`
- **Example Request**:

```json
{
  "name": "Critical",
  "description": "Critical priority tasks that need immediate attention",
  "value": 4,
  "color": "#c0392b"
}
```

- **Response**: Updated task priority object

### Delete Task Priority

Deletes a task priority.

- **URL**: `/task-priorities/{id}`
- **Method**: `DELETE`
- **Authorization**: Requires `ADMIN` role
- **URL Parameters**: `id=[integer]` - Priority ID
- **Example**: `/task-priorities/4`
- **Response**: HTTP 204 (No Content)

### Get API Status

Checks if the API is operational.

- **URL**: `/status`
- **Method**: `GET`
- **Authorization**: Public (no authentication required)
- **Response**: Status information
- **Example Response**:

```json
{
  "status": "operational",
  "version": "1.0.0",
  "timestamp": "1621612345678"
}
```

## Error Handling

The API uses standard HTTP status codes to indicate the success or failure of requests:

- **200 OK**: The request was successful
- **201 Created**: The resource was successfully created
- **204 No Content**: The request was successful but there is no representation to return
- **400 Bad Request**: The request could not be understood or was missing required parameters
- **404 Not Found**: The requested resource could not be found
- **500 Internal Server Error**: An error occurred on the server

### Error Response Format

```json
{
  "status": 404,
  "message": "Task not found with id: 999",
  "timestamp": "2025-05-21T15:30:45.123Z",
  "path": "/api/tasks/999"
}
```

### Validation Error Response Format

```json
{
  "status": 400,
  "message": "Validation failed",
  "timestamp": "2025-05-21T15:35:12.456Z",
  "errors": {
    "title": "Title is required",
    "priority": "Priority must be between 1 and 5"
  }
}
```

## Postman Collection

You can import the following Postman collection to test the API endpoints:

```json
{
  "info": {
    "_postman_id": "e2b87f3e-5d13-4f4a-8e5c-34b9a7d21f53",
    "name": "Task Management API",
    "description": "Collection for Task Management API endpoints",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Authentication",
      "item": [
        {
          "name": "Register",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"username\": \"johndoe\",\n  \"email\": \"john@example.com\",\n  \"password\": \"password123\",\n  \"firstName\": \"John\",\n  \"lastName\": \"Doe\"\n}"
            },
            "url": {
              "raw": "{{baseUrl}}/auth/register",
              "host": ["{{baseUrl}}"],
              "path": ["auth", "register"]
            }
          }
        },
        {
          "name": "Login",
          "event": [
            {
              "listen": "test",
              "script": {
                "exec": [
                  "let responseJson = pm.response.json();",
                  "if (responseJson.token) {",
                  "  pm.environment.set(\"jwtToken\", responseJson.token);",
                  "}"
                ],
                "type": "text/javascript"
              }
            }
          ],
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"username\": \"johndoe\",\n  \"password\": \"password123\"\n}"
            },
            "url": {
              "raw": "{{baseUrl}}/auth/login",
              "host": ["{{baseUrl}}"],
              "path": ["auth", "login"]
            }
          }
        }
      ]
    },
    {
      "name": "Tasks",
      "item": [
        {
          "name": "Get All Tasks",
          "request": {
            "method": "GET",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{jwtToken}}"
              }
            ],
            "url": {
              "raw": "{{baseUrl}}/tasks",
              "host": ["{{baseUrl}}"],
              "path": ["tasks"]
            }
          }
        },
        {
          "name": "Get Task by ID",
          "request": {
            "method": "GET",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{jwtToken}}"
              }
            ],
            "url": {
              "raw": "{{baseUrl}}/tasks/1",
              "host": ["{{baseUrl}}"],
              "path": ["tasks", "1"]
            }
          }
        },
        {
          "name": "Get Tasks by User ID",
          "request": {
            "method": "GET",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{jwtToken}}"
              }
            ],
            "url": {
              "raw": "{{baseUrl}}/tasks/user/1",
              "host": ["{{baseUrl}}"],
              "path": ["tasks", "user", "1"]
            }
          }
        },
        {
          "name": "Create Task",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              },
              {
                "key": "Authorization",
                "value": "Bearer {{jwtToken}}"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"title\": \"Implement authorization\",\n  \"description\": \"Implement JWT authorization for the API\",\n  \"dueDate\": \"2025-06-15\",\n  \"priority\": 1,\n  \"userId\": 1,\n  \"statusId\": 1,\n  \"categoryId\": 1\n}"
            },
            "url": {
              "raw": "{{baseUrl}}/tasks",
              "host": ["{{baseUrl}}"],
              "path": ["tasks"]
            }
          }
        },
        {
          "name": "Update Task",
          "request": {
            "method": "PUT",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              },
              {
                "key": "Authorization",
                "value": "Bearer {{jwtToken}}"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"title\": \"Implement JWT authorization\",\n  \"description\": \"Implement JWT authorization for the API with refresh tokens\",\n  \"dueDate\": \"2025-06-20\",\n  \"priority\": 1,\n  \"userId\": 1,\n  \"statusId\": 2,\n  \"categoryId\": 1\n}"
            },
            "url": {
              "raw": "{{baseUrl}}/tasks/1",
              "host": ["{{baseUrl}}"],
              "path": ["tasks", "1"]
            }
          }
        },
        {
          "name": "Delete Task",
          "request": {
            "method": "DELETE",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{jwtToken}}"
              }
            ],
            "url": {
              "raw": "{{baseUrl}}/tasks/1",
              "host": ["{{baseUrl}}"],
              "path": ["tasks", "1"]
            }
          }
        },
        {
          "name": "Search Tasks",
          "request": {
            "method": "GET",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{jwtToken}}"
              }
            ],
            "url": {
              "raw": "{{baseUrl}}/tasks/search?keyword=implement",
              "host": ["{{baseUrl}}"],
              "path": ["tasks", "search"],
              "query": [
                {
                  "key": "keyword",
                  "value": "implement"
                }
              ]
            }
          }
        },
        {
          "name": "Get Overdue Tasks",
          "request": {
            "method": "GET",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{jwtToken}}"
              }
            ],
            "url": {
              "raw": "{{baseUrl}}/tasks/overdue/1",
              "host": ["{{baseUrl}}"],
              "path": ["tasks", "overdue", "1"]
            }
          }
        }
      ]
    },
    {
      "name": "Users",
      "item": [
        {
          "name": "Get All Users",
          "request": {
            "method": "GET",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{jwtToken}}"
              }
            ],
            "url": {
              "raw": "{{baseUrl}}/users",
              "host": ["{{baseUrl}}"],
              "path": ["users"]
            }
          }
        },
        {
          "name": "Get User by ID",
          "request": {
            "method": "GET",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{jwtToken}}"
              }
            ],
            "url": {
              "raw": "{{baseUrl}}/users/1",
              "host": ["{{baseUrl}}"],
              "path": ["users", "1"]
            }
          }
        },
        {
          "name": "Get User by Username",
          "request": {
            "method": "GET",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{jwtToken}}"
              }
            ],
            "url": {
              "raw": "{{baseUrl}}/users/username/johndoe",
              "host": ["{{baseUrl}}"],
              "path": ["users", "username", "johndoe"]
            }
          }
        },
        {
          "name": "Create User",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              },
              {
                "key": "Authorization",
                "value": "Bearer {{jwtToken}}"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"username\": \"marksmith\",\n  \"email\": \"mark@example.com\",\n  \"password\": \"securepassword123\",\n  \"firstName\": \"Mark\",\n  \"lastName\": \"Smith\"\n}"
            },
            "url": {
              "raw": "{{baseUrl}}/users",
              "host": ["{{baseUrl}}"],
              "path": ["users"]
            }
          }
        },
        {
          "name": "Update User",
          "request": {
            "method": "PUT",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              },
              {
                "key": "Authorization",
                "value": "Bearer {{jwtToken}}"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"username\": \"johndoe\",\n  \"email\": \"john.doe@example.com\",\n  \"firstName\": \"John\",\n  \"lastName\": \"Doe\",\n  \"password\": \"newpassword123\"\n}"
            },
            "url": {
              "raw": "{{baseUrl}}/users/1",
              "host": ["{{baseUrl}}"],
              "path": ["users", "1"]
            }
          }
        },
        {
          "name": "Delete User",
          "request": {
            "method": "DELETE",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{jwtToken}}"
              }
            ],
            "url": {
              "raw": "{{baseUrl}}/users/1",
              "host": ["{{baseUrl}}"],
              "path": ["users", "1"]
            }
          }
        }
      ]
    },
    {
      "name": "Task Statuses",
      "item": [
        {
          "name": "Get All Task Statuses",
          "request": {
            "method": "GET",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{jwtToken}}"
              }
            ],
            "url": {
              "raw": "{{baseUrl}}/task-statuses",
              "host": ["{{baseUrl}}"],
              "path": ["task-statuses"]
            }
          }
        },
        {
          "name": "Get Task Status by ID",
          "request": {
            "method": "GET",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{jwtToken}}"
              }
            ],
            "url": {
              "raw": "{{baseUrl}}/task-statuses/1",
              "host": ["{{baseUrl}}"],
              "path": ["task-statuses", "1"]
            }
          }
        },
        {
          "name": "Get Task Status by Name",
          "request": {
            "method": "GET",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{jwtToken}}"
              }
            ],
            "url": {
              "raw": "{{baseUrl}}/task-statuses/name/To%20Do",
              "host": ["{{baseUrl}}"],
              "path": ["task-statuses", "name", "To%20Do"]
            }
          }
        },
        {
          "name": "Create Task Status",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              },
              {
                "key": "Authorization",
                "value": "Bearer {{jwtToken}}"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"name\": \"Blocked\",\n  \"description\": \"Tasks that are blocked by dependencies\",\n  \"color\": \"#e74c3c\"\n}"
            },
            "url": {
              "raw": "{{baseUrl}}/task-statuses",
              "host": ["{{baseUrl}}"],
              "path": ["task-statuses"]
            }
          }
        },
        {
          "name": "Update Task Status",
          "request": {
            "method": "PUT",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              },
              {
                "key": "Authorization",
                "value": "Bearer {{jwtToken}}"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"name\": \"Blocked\",\n  \"description\": \"Tasks that are blocked by external dependencies\",\n  \"color\": \"#c0392b\"\n}"
            },
            "url": {
              "raw": "{{baseUrl}}/task-statuses/5",
              "host": ["{{baseUrl}}"],
              "path": ["task-statuses", "5"]
            }
          }
        },
        {
          "name": "Delete Task Status",
          "request": {
            "method": "DELETE",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{jwtToken}}"
              }
            ],
            "url": {
              "raw": "{{baseUrl}}/task-statuses/5",
              "host": ["{{baseUrl}}"],
              "path": ["task-statuses", "5"]
            }
          }
        }
      ]
    },
    {
      "name": "Task Categories",
      "item": [
        {
          "name": "Get All Task Categories",
          "request": {
            "method": "GET",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{jwtToken}}"
              }
            ],
            "url": {
              "raw": "{{baseUrl}}/task-categories",
              "host": ["{{baseUrl}}"],
              "path": ["task-categories"]
            }
          }
        },
        {
          "name": "Get Task Category by ID",
          "request": {
            "method": "GET",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{jwtToken}}"
              }
            ],
            "url": {
              "raw": "{{baseUrl}}/task-categories/1",
              "host": ["{{baseUrl}}"],
              "path": ["task-categories", "1"]
            }
          }
        },
        {
          "name": "Get Task Category by Name",
          "request": {
            "method": "GET",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{jwtToken}}"
              }
            ],
            "url": {
              "raw": "{{baseUrl}}/task-categories/name/Feature",
              "host": ["{{baseUrl}}"],
              "path": ["task-categories", "name", "Feature"]
            }
          }
        },
        {
          "name": "Create Task Category",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              },
              {
                "key": "Authorization",
                "value": "Bearer {{jwtToken}}"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"name\": \"Infrastructure\",\n  \"description\": \"Infrastructure related tasks\",\n  \"color\": \"#8e44ad\"\n}"
            },
            "url": {
              "raw": "{{baseUrl}}/task-categories",
              "host": ["{{baseUrl}}"],
              "path": ["task-categories"]
            }
          }
        },
        {
          "name": "Update Task Category",
          "request": {
            "method": "PUT",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              },
              {
                "key": "Authorization",
                "value": "Bearer {{jwtToken}}"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"name\": \"Infrastructure\",\n  \"description\": \"Tasks related to infrastructure and deployment\",\n  \"color\": \"#8e44ad\"\n}"
            },
            "url": {
              "raw": "{{baseUrl}}/task-categories/6",
              "host": ["{{baseUrl}}"],
              "path": ["task-categories", "6"]
            }
          }
        },
        {
          "name": "Delete Task Category",
          "request": {
            "method": "DELETE",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{jwtToken}}"
              }
            ],
            "url": {
              "raw": "{{baseUrl}}/task-categories/6",
              "host": ["{{baseUrl}}"],
              "path": ["task-categories", "6"]
            }
          }
        }
      ]
    },
    {
      "name": "Task Priorities",
      "item": [
        {
          "name": "Get All Task Priorities",
          "request": {
            "method": "GET",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{jwtToken}}"
              }
            ],
            "url": {
              "raw": "{{baseUrl}}/task-priorities",
              "host": ["{{baseUrl}}"],
              "path": ["task-priorities"]
            }
          }
        },
        {
          "name": "Get Task Priority by ID",
          "request": {
            "method": "GET",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{jwtToken}}"
              }
            ],
            "url": {
              "raw": "{{baseUrl}}/task-priorities/1",
              "host": ["{{baseUrl}}"],
              "path": ["task-priorities", "1"]
            }
          }
        },
        {
          "name": "Get Task Priority by Name",
          "request": {
            "method": "GET",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{jwtToken}}"
              }
            ],
            "url": {
              "raw": "{{baseUrl}}/task-priorities/name/High",
              "host": ["{{baseUrl}}"],
              "path": ["task-priorities", "name", "High"]
            }
          }
        },
        {
          "name": "Get Task Priority by Value",
          "request": {
            "method": "GET",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{jwtToken}}"
              }
            ],
            "url": {
              "raw": "{{baseUrl}}/task-priorities/value/3",
              "host": ["{{baseUrl}}"],
              "path": ["task-priorities", "value", "3"]
            }
          }
        },
        {
          "name": "Create Task Priority",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              },
              {
                "key": "Authorization",
                "value": "Bearer {{jwtToken}}"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"name\": \"Critical\",\n  \"description\": \"Critical priority tasks that need immediate attention\",\n  \"value\": 4,\n  \"color\": \"#c0392b\"\n}"
            },
            "url": {
              "raw": "{{baseUrl}}/task-priorities",
              "host": ["{{baseUrl}}"],
              "path": ["task-priorities"]
            }
          }
        },
        {
          "name": "Update Task Priority",
          "request": {
            "method": "PUT",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              },
              {
                "key": "Authorization",
                "value": "Bearer {{jwtToken}}"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"name\": \"Critical\",\n  \"description\": \"Critical priority tasks that need immediate attention\",\n  \"value\": 4,\n  \"color\": \"#c0392b\"\n}"
            },
            "url": {
              "raw": "{{baseUrl}}/task-priorities/4",
              "host": ["{{baseUrl}}"],
              "path": ["task-priorities", "4"]
            }
          }
        },
        {
          "name": "Delete Task Priority",
          "request": {
            "method": "DELETE",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{jwtToken}}"
              }
            ],
            "url": {
              "raw": "{{baseUrl}}/task-priorities/4",
              "host": ["{{baseUrl}}"],
              "path": ["task-priorities", "4"]
            }
          }
        }
      ]
    },
    {
      "name": "API Status",
      "item": [
        {
          "name": "Get API Status",
          "request": {
            "method": "GET",
            "url": {
              "raw": "{{baseUrl}}/status",
              "host": ["{{baseUrl}}"],
              "path": ["status"]
            }
          }
        }
      ]
    }
  ],
  "variable": [
    {
      "key": "baseUrl",
      "value": "http://localhost:8080/api",
      "type": "string"
    }
  ]
}
```

### How to Import the Postman Collection

1. Open Postman
2. Click on the "Import" button in the top left corner
3. Select "Raw text"
4. Paste the JSON above
5. Click "Import"
6. The collection will be added to your workspace

## cURL Examples

Here are cURL commands for common API operations with authentication:

### Authentication

**Login and extract token:**

```bash
# Login and get token
TOKEN=$(curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username": "johndoe", "password": "password123"}' | jq -r '.token')

# Save token for later use
echo $TOKEN
```

### Tasks

**Get All Tasks**

```bash
# Using saved token
curl -X GET "http://localhost:8080/api/tasks" \
  -H "Authorization: Bearer $TOKEN"
```

**Get Task by ID**

```bash
# Using saved token
curl -X GET "http://localhost:8080/api/tasks/1" \
  -H "Authorization: Bearer $TOKEN"
```

**Create Task**

```bash
# Using saved token
curl -X POST "http://localhost:8080/api/tasks" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "title": "Implement authorization",
    "description": "Implement JWT authorization for the API",
    "dueDate": "2025-06-15",
    "priority": 1,
    "userId": 1,
    "statusId": 1,
    "categoryId": 1
  }'
```

**Update Task**

```bash
# Using saved token
curl -X PUT "http://localhost:8080/api/tasks/1" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "title": "Implement JWT authorization",
    "description": "Implement JWT authorization for the API with refresh tokens",
    "dueDate": "2025-06-20",
    "priority": 1,
    "userId": 1,
    "statusId": 2,
    "categoryId": 1
  }'
```

**Delete Task**

```bash
# Using saved token
curl -X DELETE "http://localhost:8080/api/tasks/1" \
  -H "Authorization: Bearer $TOKEN"
```

**Search Tasks**

```bash
# Using saved token
curl -X GET "http://localhost:8080/api/tasks/search?keyword=implement" \
  -H "Authorization: Bearer $TOKEN"
```

**Get Overdue Tasks**

```bash
# Using saved token
curl -X GET "http://localhost:8080/api/tasks/overdue/1" \
  -H "Authorization: Bearer $TOKEN"
```

### Users

**Get All Users**

```bash
# Using saved token
curl -X GET "http://localhost:8080/api/users" \
  -H "Authorization: Bearer $TOKEN"
```

**Get User by ID**

```bash
# Using saved token
curl -X GET "http://localhost:8080/api/users/1" \
  -H "Authorization: Bearer $TOKEN"
```

**Get User by Username**

```bash
# Using saved token
curl -X GET "http://localhost:8080/api/users/username/johndoe" \
  -H "Authorization: Bearer $TOKEN"
```

**Create User**

```bash
# Using saved token (requires ADMIN role)
curl -X POST "http://localhost:8080/api/users" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "username": "marksmith",
    "email": "mark@example.com",
    "password": "securepassword123",
    "firstName": "Mark",
    "lastName": "Smith"
  }'
```

**Update User**

```bash
# Using saved token
curl -X PUT "http://localhost:8080/api/users/1" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "username": "johndoe",
    "email": "john.doe@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "password": "newpassword123"
  }'
```

**Delete User**

```bash
# Using saved token (requires ADMIN role)
curl -X DELETE "http://localhost:8080/api/users/1" \
  -H "Authorization: Bearer $TOKEN"
```

### Task Statuses

**Get All Task Statuses**

```bash
# Using saved token
curl -X GET "http://localhost:8080/api/task-statuses" \
  -H "Authorization: Bearer $TOKEN"
```

**Get Task Status by ID**

```bash
# Using saved token
curl -X GET "http://localhost:8080/api/task-statuses/1" \
  -H "Authorization: Bearer $TOKEN"
```

**Get Task Status by Name**

```bash
# Using saved token
curl -X GET "http://localhost:8080/api/task-statuses/name/To%20Do" \
  -H "Authorization: Bearer $TOKEN"
```

**Create Task Status**

```bash
# Using saved token (requires ADMIN role)
curl -X POST "http://localhost:8080/api/task-statuses" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Blocked",
    "description": "Tasks that are blocked by dependencies",
    "color": "#e74c3c"
  }'
```

**Update Task Status**

```bash
# Using saved token (requires ADMIN role)
curl -X PUT "http://localhost:8080/api/task-statuses/5" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Blocked",
    "description": "Tasks that are blocked by external dependencies",
    "color": "#c0392b"
  }'
```

**Delete Task Status**

```bash
# Using saved token (requires ADMIN role)
curl -X DELETE "http://localhost:8080/api/task-statuses/5" \
  -H "Authorization: Bearer $TOKEN"
```

### Task Categories

**Get All Task Categories**

```bash
# Using saved token
curl -X GET "http://localhost:8080/api/task-categories" \
  -H "Authorization: Bearer $TOKEN"
```

**Get Task Category by ID**

```bash
# Using saved token
curl -X GET "http://localhost:8080/api/task-categories/1" \
  -H "Authorization: Bearer $TOKEN"
```

**Get Task Category by Name**

```bash
# Using saved token
curl -X GET "http://localhost:8080/api/task-categories/name/Feature" \
  -H "Authorization: Bearer $TOKEN"
```

**Create Task Category**

```bash
# Using saved token (requires ADMIN role)
curl -X POST "http://localhost:8080/api/task-categories" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Infrastructure",
    "description": "Infrastructure related tasks",
    "color": "#8e44ad"
  }'
```

**Update Task Category**

```bash
# Using saved token (requires ADMIN role)
curl -X PUT "http://localhost:8080/api/task-categories/6" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Infrastructure",
    "description": "Tasks related to infrastructure and deployment",
    "color": "#8e44ad"
  }'
```

**Delete Task Category**

```bash
# Using saved token (requires ADMIN role)
curl -X DELETE "http://localhost:8080/api/task-categories/6" \
  -H "Authorization: Bearer $TOKEN"
```

### Task Priorities

**Get All Task Priorities**

```bash
# Using saved token
curl -X GET "http://localhost:8080/api/task-priorities" \
  -H "Authorization: Bearer $TOKEN"
```

**Get Task Priority by ID**

```bash
# Using saved token
curl -X GET "http://localhost:8080/api/task-priorities/1" \
  -H "Authorization: Bearer $TOKEN"
```

**Get Task Priority by Name**

```bash
# Using saved token
curl -X GET "http://localhost:8080/api/task-priorities/name/High" \
  -H "Authorization: Bearer $TOKEN"
```

**Get Task Priority by Value**

```bash
# Using saved token
curl -X GET "http://localhost:8080/api/task-priorities/value/3" \
  -H "Authorization: Bearer $TOKEN"
```

**Create Task Priority**

```bash
# Using saved token (requires ADMIN role)
curl -X POST "http://localhost:8080/api/task-priorities" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Critical",
    "description": "Critical priority tasks that need immediate attention",
    "value": 4,
    "color": "#c0392b"
  }'
```

**Update Task Priority**

```bash
# Using saved token (requires ADMIN role)
curl -X PUT "http://localhost:8080/api/task-priorities/4" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Critical",
    "description": "Critical priority tasks that need immediate attention",
    "value": 4,
    "color": "#c0392b"
  }'
```

**Delete Task Priority**

```bash
# Using saved token (requires ADMIN role)
curl -X DELETE "http://localhost:8080/api/task-priorities/4" \
  -H "Authorization: Bearer $TOKEN"
```

### API Status

**Get API Status**

```bash
# No token needed (public endpoint)
curl -X GET "http://localhost:8080/api/status"
```
