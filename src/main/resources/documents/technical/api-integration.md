# API Integration Guide

## Getting Started with Our API

Our API provides a comprehensive interface for integrating with our platform. This guide covers authentication, core concepts, and best practices.

## Base URLs

- **Production**: `https://api.example.com/v1`
- **Staging**: `https://staging-api.example.com/v1`
- **Development**: `http://localhost:8000/v1`

## Authentication

All API requests require authentication using an API key. There are two ways to authenticate:

### Bearer Token (Recommended)

Include your API key in the Authorization header:
```bash
curl -H "Authorization: Bearer YOUR_API_KEY" https://api.example.com/v1/users
```

### API Key Parameter

Alternatively, pass your API key as a query parameter:
```bash
curl https://api.example.com/v1/users?api_key=YOUR_API_KEY
```

## Getting Your API Key

1. Log in to your account at https://app.example.com
2. Navigate to Settings → API Keys
3. Click "Generate New Key"
4. Copy the key and store it securely
5. Keys are active immediately

**Note**: Keep your API key confidential. Treat it like a password.

## Rate Limiting

The API enforces rate limiting to ensure fair usage:

- **Free Tier**: 1,000 requests per hour
- **Professional**: 10,000 requests per hour
- **Enterprise**: Custom limits

### Rate Limit Headers

Every response includes rate limit information:
X-RateLimit-Limit: 1000
X-RateLimit-Remaining: 995
X-RateLimit-Reset: 1234567890

When you exceed the limit, you'll receive a `429 Too Many Requests` response.

### Implementing Backoff

When receiving 429 responses, implement exponential backoff:
```java
int retries = 0;
int maxRetries = 5;
long backoffMs = 1000;

while (retries < maxRetries) {
    try {
        response = api.makeRequest(request);
        break;
    } catch (RateLimitException e) {
        retries++;
        Thread.sleep(backoffMs);
        backoffMs *= 2;
    }
}
```

## Core Endpoints

### Users

#### Create User
```http
POST /users
Content-Type: application/json

{
  "email": "user@example.com",
  "name": "John Doe",
  "plan": "professional"
}
```

**Response** (201):
```json
{
  "id": "user_123abc",
  "email": "user@example.com",
  "name": "John Doe",
  "plan": "professional",
  "created_at": "2024-01-01T10:00:00Z",
  "api_key": "sk_live_xxxxx"
}
```

#### Get User
```http
GET /users/{id}
```

**Response** (200):
```json
{
  "id": "user_123abc",
  "email": "user@example.com",
  "name": "John Doe",
  "plan": "professional",
  "created_at": "2024-01-01T10:00:00Z",
  "api_calls_this_month": 45230,
  "status": "active"
}
```

#### List Users
```http
GET /users?limit=10&offset=0
```

**Response** (200):
```json
{
  "data": [
    {
      "id": "user_123abc",
      "email": "user@example.com",
      "plan": "professional"
    }
  ],
  "total": 150,
  "limit": 10,
  "offset": 0
}
```

#### Update User
```http
PATCH /users/{id}
Content-Type: application/json

{
  "name": "Jane Doe",
  "plan": "enterprise"
}
```

**Response** (200):
```json
{
  "id": "user_123abc",
  "email": "user@example.com",
  "name": "Jane Doe",
  "plan": "enterprise"
}
```

#### Delete User
```http
DELETE /users/{id}
```

**Response** (204 No Content)

### Projects

#### Create Project
```http
POST /projects
Content-Type: application/json

{
  "name": "My Project",
  "description": "A test project",
  "owner_id": "user_123abc"
}
```

**Response** (201):
```json
{
  "id": "proj_456def",
  "name": "My Project",
  "description": "A test project",
  "owner_id": "user_123abc",
  "created_at": "2024-01-01T10:00:00Z",
  "status": "active"
}
```

#### Get Project
```http
GET /projects/{id}
```

**Response** (200):
```json
{
  "id": "proj_456def",
  "name": "My Project",
  "owner_id": "user_123abc",
  "members": 3,
  "status": "active",
  "created_at": "2024-01-01T10:00:00Z"
}
```

### Resources

#### Upload Resource
```http
POST /projects/{project_id}/resources
Content-Type: multipart/form-data

file=@document.pdf
name=document
type=pdf
```

**Response** (201):
```json
{
  "id": "res_789ghi",
  "project_id": "proj_456def",
  "name": "document",
  "type": "pdf",
  "size_bytes": 245678,
  "url": "https://storage.example.com/res_789ghi.pdf",
  "created_at": "2024-01-01T10:05:00Z"
}
```

#### List Resources
```http
GET /projects/{project_id}/resources?limit=20
```

**Response** (200):
```json
{
  "data": [
    {
      "id": "res_789ghi",
      "name": "document",
      "type": "pdf",
      "size_bytes": 245678
    }
  ],
  "total": 15,
  "limit": 20
}
```

## Error Handling

All errors follow a consistent format:
```json
{
  "error": {
    "code": "INVALID_REQUEST",
    "message": "The request body is invalid",
    "details": {
      "field": "email",
      "reason": "Must be a valid email address"
    },
    "request_id": "req_xyz123"
  }
}
```

### Common Error Codes

| Code | Status | Description |
|------|--------|-------------|
| INVALID_REQUEST | 400 | Request format is invalid |
| UNAUTHORIZED | 401 | Missing or invalid API key |
| FORBIDDEN | 403 | Don't have permission |
| NOT_FOUND | 404 | Resource not found |
| CONFLICT | 409 | Resource already exists |
| RATE_LIMITED | 429 | Too many requests |
| SERVER_ERROR | 500 | Server error (retry with backoff) |
| SERVICE_UNAVAILABLE | 503 | Service temporarily down |

## Pagination

Endpoints that return lists support pagination:
```http
GET /users?limit=50&offset=100
```

Parameters:
- `limit`: Number of items per page (default: 20, max: 100)
- `offset`: Number of items to skip (default: 0)

## Versioning

The current API version is v1. We support previous versions for 12 months after releasing a new version.

To use a different version:
```bash
curl https://api.example.com/v2/users
```

## Webhooks

Webhooks allow you to receive notifications about events:

### Setting Up Webhooks
```http
POST /webhooks
Content-Type: application/json

{
  "url": "https://yourapp.com/webhook",
  "events": ["user.created", "user.deleted", "resource.uploaded"]
}
```

### Webhook Events

- `user.created` - User account created
- `user.updated` - User information changed
- `user.deleted` - User account deleted
- `project.created` - New project created
- `resource.uploaded` - File uploaded
- `resource.deleted` - File deleted

### Webhook Payload
```json
{
  "id": "evt_123",
  "event": "user.created",
  "timestamp": "2024-01-01T10:00:00Z",
  "data": {
    "id": "user_123abc",
    "email": "user@example.com",
    "name": "John Doe"
  }
}
```

## Best Practices

### 1. Use Connection Pooling
```java
HttpClientBuilder builder = HttpClients.custom()
    .setMaxConnTotal(100)
    .setMaxConnPerRoute(10);
HttpClient httpClient = builder.build();
```

### 2. Implement Timeouts
```java
RequestConfig requestConfig = RequestConfig.custom()
    .setConnectionTimeout(5000)
    .setSocketTimeout(10000)
    .build();
```

### 3. Cache Responses Appropriately

- Cache user data for 5 minutes
- Cache project data for 10 minutes
- Don't cache user-specific data longer than their session

### 4. Handle Errors Gracefully
```java
try {
    response = api.makeRequest(request);
} catch (RateLimitException e) {
    // Implement backoff
} catch (ServerException e) {
    // Retry with exponential backoff
} catch (ClientException e) {
    // Don't retry, fix the request
}
```

### 5. Use Batch Operations When Possible

Instead of making 100 individual requests, use batch endpoints:
```http
POST /batch
Content-Type: application/json

{
  "requests": [
    {
      "method": "POST",
      "path": "/users",
      "body": { "email": "user1@example.com" }
    },
    {
      "method": "POST",
      "path": "/users",
      "body": { "email": "user2@example.com" }
    }
  ]
}
```

## Code Examples

### Python
```python
import requests

API_KEY = "sk_live_xxxxx"
headers = {"Authorization": f"Bearer {API_KEY}"}

# Create user
response = requests.post(
    "https://api.example.com/v1/users",
    json={"email": "user@example.com", "name": "John Doe"},
    headers=headers
)
user = response.json()
print(f"Created user: {user['id']}")

# Get user
response = requests.get(
    f"https://api.example.com/v1/users/{user['id']}",
    headers=headers
)
user_data = response.json()
print(f"User: {user_data}")
```

### JavaScript
```javascript
const API_KEY = "sk_live_xxxxx";

async function createUser(email, name) {
    const response = await fetch("https://api.example.com/v1/users", {
        method: "POST",
        headers: {
            "Authorization": `Bearer ${API_KEY}`,
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ email, name })
    });
    
    if (!response.ok) {
        throw new Error(`API error: ${response.status}`);
    }
    
    return response.json();
}

createUser("user@example.com", "John Doe")
    .then(user => console.log(`Created user: ${user.id}`))
    .catch(error => console.error(error));
```

### Java
```java
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiExample {
    private static final String API_KEY = "sk_live_xxxxx";
    private static final String BASE_URL = "https://api.example.com/v1";
    
    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        
        String json = "{\"email\": \"user@example.com\", \"name\": \"John Doe\"}";
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/users"))
            .header("Authorization", "Bearer " + API_KEY)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();
        
        HttpResponse<String> response = client.send(request, 
            HttpResponse.BodyHandlers.ofString());
        
        System.out.println("Status: " + response.statusCode());
        System.out.println("Body: " + response.body());
    }
}
```

## Support

For API support:
- Documentation: https://docs.example.com
- Status: https://status.example.com
- Email: api-support@example.com
- Slack Community: https://community.example.com/slack