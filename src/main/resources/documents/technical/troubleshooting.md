# Troubleshooting Guide

## Common Issues and Solutions

### Authentication Issues

#### Problem: "Unauthorized" or 401 Error

**Symptoms:**
- All API requests return 401 Unauthorized
- Error message: "Invalid API key"
- Headers show Authorization header

**Solutions:**

1. **Verify API Key Format**
    - Should start with `sk_live_` or `sk_test_`
    - Check for trailing/leading spaces
    - Keys are case-sensitive

2. **Check Authentication Method**
```bash
   # Method 1: Authorization Header (preferred)
   curl -H "Authorization: Bearer YOUR_API_KEY" https://api.example.com/v1/users
   
   # Method 2: Query Parameter
   curl https://api.example.com/v1/users?api_key=YOUR_API_KEY
```

3. **Regenerate API Key**
    - Log in to app.example.com
    - Go to Settings → API Keys
    - Delete the old key
    - Click "Generate New Key"
    - Copy immediately (won't be shown again)

4. **Check Key Permissions**
    - Some keys have restricted scopes
    - Verify key has permission for the endpoint
    - Try with a full-access key

5. **Environment Variables**
```bash
   # Set in shell
   export API_KEY="sk_live_xxxxx"
   
   # Verify it's set
   echo $API_KEY
   
   # Use in curl
   curl -H "Authorization: Bearer $API_KEY" https://api.example.com/v1/users
```

#### Problem: "Forbidden" or 403 Error

**Symptoms:**
- API key is valid but you can't access a resource
- Error: "Don't have permission to access this resource"

**Solutions:**

1. **Check Resource Ownership**
    - Ensure you own the resource or are a team member
    - Resource owner can share access

2. **Check Role Permissions**
    - Admin: Full access
    - Editor: Can modify resources
    - Viewer: Read-only access

3. **Team Access**
    - Ask the project owner to add you
    - You need explicit membership to access shared resources

### Connection Issues

#### Problem: Connection Timeout

**Symptoms:**
- Requests hang for 30+ seconds then fail
- "Connection timeout" error
- Can't reach API at all

**Solutions:**

1. **Check Internet Connection**
```bash
   # Test basic connectivity
   ping api.example.com
   
   # Check DNS resolution
   nslookup api.example.com
```

2. **Check Firewall/Proxy**
    - API uses standard HTTPS (port 443)
    - No special firewall rules needed
    - If behind corporate proxy, configure it

3. **Try Different Network**
    - Try from different WiFi or mobile hotspot
    - Rule out local network issues

4. **Increase Timeout Value**
```java
   RequestConfig config = RequestConfig.custom()
       .setConnectionTimeout(15000)  // 15 seconds
       .setSocketTimeout(20000)       // 20 seconds
       .build();
```

5. **Check API Status**
    - Visit https://status.example.com
    - Check for ongoing incidents
    - Subscribe to status updates

#### Problem: Connection Refused

**Symptoms:**
- Error: "Connection refused"
- "Unable to connect to api.example.com"
- Happens immediately, not after timeout

**Solutions:**

1. **Verify Correct URL**
```bash
   # Production (correct)
   https://api.example.com/v1
   
   # Staging
   https://staging-api.example.com/v1
   
   # Development (for local testing)
   http://localhost:8000/v1
```

2. **Check DNS Resolution**
```bash
   nslookup api.example.com
   # Should show IP addresses
```

3. **Test with Simple Tool**
```bash
   # Test basic connectivity
   curl -I https://api.example.com/v1
   
   # Should return HTTP response headers
```

### Request Format Issues

#### Problem: "Invalid JSON" Error

**Symptoms:**
- Error: "Invalid JSON in request body"
- 400 Bad Request status
- Same request sometimes works, sometimes doesn't

**Solutions:**

1. **Validate JSON Syntax**
```bash
   # Use jq to validate
   echo '{"email":"user@example.com"}' | jq empty
   
   # Or use online validator
   # https://jsonlint.com
```

2. **Check UTF-8 Encoding**
```java
   // Ensure UTF-8 encoding
   String json = new String(data, StandardCharsets.UTF_8);
```

3. **Escape Special Characters**
```json
   // Wrong: unescaped quotes
   {"name": "John "The King" Doe"}
   
   // Correct: escaped quotes
   {"name": "John \"The King\" Doe"}
```

4. **Content-Type Header**
```bash
   # MUST include this header for JSON
   curl -H "Content-Type: application/json" \
        -d '{"email":"user@example.com"}' \
        https://api.example.com/v1/users
```

5. **Use JSON Library**
```java
   // Don't build JSON strings manually
   // Use a library instead
   ObjectMapper mapper = new ObjectMapper();
   String json = mapper.writeValueAsString(user);
```

#### Problem: Missing Required Fields

**Symptoms:**
- Error: "Field 'email' is required"
- 400 Bad Request with validation errors
- Different fields have different requirements

**Solutions:**

1. **Check API Documentation**
    - Review the endpoint documentation
    - Identify all required fields

2. **Required Fields by Endpoint**
    - POST /users: email, name
    - POST /projects: name, owner_id
    - PATCH /users: at least one field to update

3. **Example with All Required Fields**
```json
   {
     "email": "user@example.com",
     "name": "John Doe",
     "plan": "professional"
   }
```

#### Problem: Invalid Field Values

**Symptoms:**
- Error: "Invalid email format"
- Error: "Plan must be one of: starter, professional, enterprise"
- Field validation fails

**Solutions:**

1. **Email Validation**
    Valid: user@example.com
    Invalid: user@
    Invalid: @example.com
    Invalid: user@example (missing .com)
2. **Plan Values (Enum)**
   Valid: starter, professional, enterprise
   Invalid: pro, prof, prof+ (these don't exist)
3. **Numeric Ranges**
   limit: 1-100 (for pagination)
   offset: 0 or positive numbers
### Rate Limiting

#### Problem: 429 Too Many Requests

**Symptoms:**
- Sudden API failures after many successful requests
- Error: "Too many requests"
- 429 status code
- X-RateLimit-Remaining header shows 0

**Solutions:**

1. **Check Rate Limit Headers**
```bash
   curl -i https://api.example.com/v1/users
   
   # Look for headers:
   # X-RateLimit-Limit: 1000
   # X-RateLimit-Remaining: 0
   # X-RateLimit-Reset: 1234567890
```

2. **Implement Exponential Backoff**
```java
   int retries = 0;
   long delayMs = 1000;
   
   while (retries < 5) {
       try {
           return api.makeRequest(request);
       } catch (RateLimitException e) {
           retries++;
           if (retries >= 5) throw e;
           
           System.out.println("Rate limited. Waiting " + delayMs + "ms");
           Thread.sleep(delayMs);
           delayMs *= 2;  // Double the delay each time
       }
   }
```

3. **Batch Operations**
    - Instead of 100 individual requests, use batch endpoint
    - Reduces API calls significantly

4. **Cache Results**
```java
   // Cache user data for 5 minutes
   Cache<String, User> cache = CacheBuilder.newBuilder()
       .expireAfterWrite(5, TimeUnit.MINUTES)
       .build();
```

5. **Upgrade Your Plan**
    - Free: 1,000 requests/hour
    - Professional: 10,000 requests/hour
    - Enterprise: Custom limits

6. **Monitor Usage**
    - Check your dashboard for current usage
    - Set up alerts when approaching limits
    - Plan for peak usage times

### Response/Data Issues

#### Problem: Empty Response or Null Fields

**Symptoms:**
- Response status is 200 but data is empty
- Expected field is null
- Data seems incomplete

**Solutions:**

1. **Check What You're Requesting**
```bash
   # Getting list endpoint
   GET /users
   
   # Getting single item
   GET /users/{id}
   
   # These are different! List doesn't include all details.
```

2. **Verify Object Exists**
```bash
   # First verify the user exists
   curl https://api.example.com/v1/users/user_123
   
   # If 404, the object doesn't exist
```

3. **Check for Soft Deletes**
    - Deleted objects might still return with status: "deleted"
    - Filter by status in your code

4. **Pagination**
```bash
   # Make sure you're checking all pages
   GET /users?limit=20&offset=0
   GET /users?limit=20&offset=20
   GET /users?limit=20&offset=40
```

#### Problem: Unexpected Data Type

**Symptoms:**
- Expected string, got number
- Expected object, got null
- Type conversion errors

**Solutions:**

1. **Check API Response Type**
    - ID fields are strings (e.g., "user_123")
    - Timestamps are ISO 8601 strings
    - Counts are numbers

2. **Type Safe Parsing**
```java
   // Use Jackson for safe parsing
   ObjectMapper mapper = new ObjectMapper();
   User user = mapper.readValue(jsonString, User.class);
```

3. **Handle Nullable Fields**
```java
   User user = mapper.readValue(jsonString, User.class);
   if (user.getDescription() != null) {
       System.out.println(user.getDescription());
   }
```

### Performance Issues

#### Problem: Slow API Responses

**Symptoms:**
- Requests take 5+ seconds to complete
- Previously fast, now slow
- Intermittent slowness

**Solutions:**

1. **Profile the Request**
```bash
   # See breakdown of request time
   curl -w "Connect: %{time_connect}s\nTransfer: %{time_starttransfer}s\nTotal: %{time_total}s\n" \
        https://api.example.com/v1/users
```

2. **Check Query Complexity**
    - Requesting large lists without pagination?
    - Requesting lists with complex filters?
    - Use pagination to limit result size

3. **Optimize Requests**
```bash
   # Bad: Getting all users
   GET /users
   
   # Good: Getting with pagination
   GET /users?limit=50&offset=0
   
   # Better: Getting with filtering
   GET /users?status=active&limit=50
```

4. **Check API Status**
    - Visit https://status.example.com
    - Slow API might indicate server issues

5. **Implement Caching**
    - Cache user data for 5 minutes
    - Cache project lists for 10 minutes
    - Reduces API calls and improves responsiveness

### Debugging Tips

#### Enable Detailed Logging
```java
// Add to your configuration
System.setProperty("sun.net.http.allowRestrictedHeaders", "true");

// Log all HTTP requests/responses
logging.level.org.apache.http = DEBUG
```

#### Use a Tool to Inspect Requests
```bash
# Postman: GUI tool for testing APIs
# https://www.postman.com

# curl: Command line tool
curl -v -H "Authorization: Bearer KEY" https://api.example.com/v1/users

# Charles Proxy: HTTP debugging proxy
# https://www.charlesproxy.com
```

#### Common Debugging Checklist

- [ ] API key is valid and not expired
- [ ] Using correct URL (production vs staging)
- [ ] HTTP method is correct (POST vs GET vs PATCH)
- [ ] Headers include Content-Type for JSON requests
- [ ] Request body is valid JSON
- [ ] All required fields are present
- [ ] Field values are in correct format
- [ ] Not exceeding rate limits
- [ ] API status is normal (check status page)

## Getting Help

If issues persist:

1. **Check Documentation**
    - https://docs.example.com
    - https://api.example.com/v1/docs

2. **Search Issues**
    - GitHub Issues: https://github.com/example/api/issues
    - Community: https://community.example.com

3. **Contact Support**
    - Email: support@example.com
    - Chat: https://support.example.com/chat
    - Phone: Available for Enterprise customers

4. **Include in Support Request**
    - Error message (exact text)
    - Request ID (from error response)
    - Steps to reproduce
    - Your API version
    - Relevant code snippet