## Task Management API Documentation

This document provides details about the REST API endpoints available in the Task Management application.

## Table of Contents

1. [Overview](#overview)
2. [Base URL](#base-url)
3. [Authentication](#authentication)
4. [Task Endpoints](#task-endpoints)
5. [User Endpoints](#user-endpoints)
6. [Task Status Endpoints](#task-status-endpoints)
7. [Task Category Endpoints](#task-category-endpoints)
8. [API Status](#api-status)
9. [Error Handling](#error-handling)
10. [Postman Collection](#postman-collection)
11. [cURL Examples](#curl-examples)

## Overview

The Task Management API provides a RESTful interface for managing tasks, users, task statuses, and task categories. It follows standard HTTP methods and uses JSON for data exchange.

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

- **URL**: `/api/auth/register`
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

- **URL**: `/api/auth/login`
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

- `/` (root endpoint)
- `/api/auth/**` (authentication endpoints)

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
- **URL Parameters**: `userId=[integer]` - User ID
- **Example**: `/tasks/user/1`
- **Response**: Array of task objects

### Create Task

Creates a new task.

- **URL**: `/tasks`
- **Method**: `POST`
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
- **URL Parameters**: `id=[integer]` - Task ID
- **Example**: `/tasks/1`
- **Response**: HTTP 204 (No Content)

### Search Tasks

Searches tasks by title (case-insensitive).

- **URL**: `/tasks/search`
- **Method**: `GET`
- **Query Parameters**: `keyword=[string]` - Search term
- **Example**: `/tasks/search?keyword=implement`
- **Response**: Array of matching task objects

### Get Overdue Tasks

Retrieves all overdue tasks for a specific user.

- **URL**: `/tasks/overdue/{userId}`
- **Method**: `GET`
- **URL Parameters**: `userId=[integer]` - User ID
- **Example**: `/tasks/overdue/1`
- **Response**: Array of overdue task objects

## User Endpoints

### Get All Users

Retrieves a list of all users.

- **URL**: `/users`
- **Method**: `GET`
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
- **URL Parameters**: `id=[integer]` - User ID
- **Example**: `/users/1`
- **Response**: User object

### Get User by Username

Retrieves a specific user by their username.

- **URL**: `/users/username/{username}`
- **Method**: `GET`
- **URL Parameters**: `username=[string]` - Username
- **Example**: `/users/username/johndoe`
- **Response**: User object

### Create User

Creates a new user.

- **URL**: `/users`
- **Method**: `POST`
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
- **URL Parameters**: `id=[integer]` - Status ID
- **Example**: `/task-statuses/1`
- **Response**: Task status object

### Get Task Status by Name

Retrieves a specific task status by its name.

- **URL**: `/task-statuses/name/{name}`
- **Method**: `GET`
- **URL Parameters**: `name=[string]` - Status name
- **Example**: `/task-statuses/name/To%20Do`
- **Response**: Task status object

### Create Task Status

Creates a new task status.

- **URL**: `/task-statuses`
- **Method**: `POST`
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
- **URL Parameters**: `id=[integer]` - Status ID
- **Example**: `/task-statuses/5`
- **Response**: HTTP 204 (No Content)

## Task Category Endpoints

### Get All Task Categories

Retrieves a list of all task categories.

- **URL**: `/task-categories`
- **Method**: `GET`
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
- **URL Parameters**: `id=[integer]` - Category ID
- **Example**: `/task-categories/1`
- **Response**: Task category object

### Get Task Category by Name

Retrieves a specific task category by its name.

- **URL**: `/task-categories/name/{name}`
- **Method**: `GET`
- **URL Parameters**: `name=[string]` - Category name
- **Example**: `/task-categories/name/Feature`
- **Response**: Task category object

### Create Task Category

Creates a new task category.

- **URL**: `/task-categories`
- **Method**: `POST`
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
- **URL Parameters**: `id=[integer]` - Category ID
- **Example**: `/task-categories/6`
- **Response**: HTTP 204 (No Content)

## API Status

### Get API Status

Checks if the API is operational.

- **URL**: `/status`
- **Method**: `GET`
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
      "name": "Tasks",
      "item": [
        {
          "name": "Get All Tasks",
          "request": {
            "method": "GET",
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
            "url": {
              "raw": "{{baseUrl}}/users/1",
              "host": ["{{baseUrl}}"],
              "path": ["users", "1"]
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
            "url": {
              "raw": "{{baseUrl}}/task-statuses/1",
              "host": ["{{baseUrl}}"],
              "path": ["task-statuses", "1"]
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
            "url": {
              "raw": "{{baseUrl}}/task-categories/1",
              "host": ["{{baseUrl}}"],
              "path": ["task-categories", "1"]
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

Here are cURL commands for common API operations:

### Tasks

**Get All Tasks**

```bash
curl -X GET http://localhost:8080/api/tasks
```

**Get Task by ID**

```bash
curl -X GET http://localhost:8080/api/tasks/1
```

**Create Task**

```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
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
curl -X PUT http://localhost:8080/api/tasks/1 \
  -H "Content-Type: application/json" \
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
curl -X DELETE http://localhost:8080/api/tasks/1
```

**Search Tasks**

```bash
curl -X GET "http://localhost:8080/api/tasks/search?keyword=implement"
```

### Users

**Get All Users**

```bash
curl -X GET http://localhost:8080/api/users
```

**Get User by ID**

```bash
curl -X GET http://localhost:8080/api/users/1
```

**Create User**

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
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
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
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
curl -X DELETE http://localhost:8080/api/users/1
```

### Task Statuses

**Get All Task Statuses**

```bash
curl -X GET http://localhost:8080/api/task-statuses
```

**Create Task Status**

```bash
curl -X POST http://localhost:8080/api/task-statuses \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Blocked",
    "description": "Tasks that are blocked by dependencies",
    "color": "#e74c3c"
  }'
```

### Task Categories

**Get All Task Categories**

```bash
curl -X GET http://localhost:8080/api/task-categories
```

**Create Task Category**

```bash
curl -X POST http://localhost:8080/api/task-categories \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Infrastructure",
    "description": "Infrastructure related tasks",
    "color": "#8e44ad"
  }'
```

### API Status

**Get API Status**

```bash
curl -X GET http://localhost:8080/api/status
```
