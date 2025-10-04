Content-Type, Authorization`

## 8. CSRF (Cross-Site Request Forgery)

CSRF tricks a logged-in user's browser into making an unwanted request to your app.

**Example:**
- You log in to bank.com (session cookie stored in browser)
- You visit a malicious site evil.com
- That site makes a hidden POST request to bank.com/transfer?amount=1000&to=hacker
- Browser sends your bank.com session cookie automatically, so server thinks it's a legitimate request from you
- Money transferred → attacker wins

### Solution: CSRF Tokens

The server issues a CSRF token (a random secret value). Every state-changing request (POST, PUT, DELETE, PATCH) must include this token. Since attacker's site cannot read the token, they can't forge requests. 

In Spring Security: CSRF protection is enabled by default for non-GET methods (POST, PUT, DELETE). Spring Security adds a hidden input field with CSRF token in forms.

## 9. How Can You Protect from CSRF?

### a) Synchronizer Token Pattern

- Server generates a unique, random token for each session (or even each request)
- Token is embedded in forms or AJAX responses
- Client must send back this token in every state-changing request (POST/PUT/DELETE)
- Since an attacker's site cannot read the token (due to Same-Origin Policy), they cannot forge valid requests

#### Example Flow

**User logs in** → server stores session + generates CSRF token.

HTML form includes a hidden field:

```html
<form action="/transfer" method="POST">
    <input type="hidden" name="_csrf" value="A1B2C3D4E5F6" />
    <input type="text" name="amount">
    <button type="submit">Transfer</button>
</form>
```

So now your browser has:
- a) Session cookie (JSESSIONID=12345)
- b) CSRF token (XYZ123SECRET)

**You submit the form** → token sent. Your browser sends:

```
POST /transfer
Cookie: JSESSIONID=12345
_csrf=XYZ123SECRET
```

Server checks:
- Does the cookie match a logged-in user?
- Does the CSRF token match the one it issued?
- If yes → allow the request

**Now when Attacker tries from evil.com**

Suppose attacker hosts this on evil.com:

```html
<form action="https://bank.com/transfer" method="POST">
   <input type="hidden" name="amount" value="1000">
</form>
<script>document.forms[0].submit();</script>
```

Browser sends:
```
POST /transfer
Cookie: JSESSIONID=12345   (because you're logged in to bank.com)
_csrf=???                  (NO TOKEN!)
```

The attacker cannot guess or read your XYZ123SECRET token, because of the Same-Origin Policy.

**Same-Origin Policy (why attacker can't read token)**

Same-Origin Policy is a browser rule:
- A webpage (like evil.com) cannot read data from another domain (bank.com)
- JavaScript running on evil.com cannot peek into:
  - Responses from bank.com
  - Cookies for bank.com
  - Hidden form fields or tokens from bank.com

So even if evil.com tricks your browser into sending a request, it can't know the CSRF token. Without the correct token, the request fails.

### b) SameSite Cookie Attribute

Cookies can be marked with SameSite flag → tells browsers when to send cookies with requests.

**SameSite=Strict**
→ Cookies sent only for requests from same site.
→ If a malicious site tries to POST to your app, browser will not send the session cookie.

**SameSite=Lax (default in modern browsers)**
→ Cookies sent for normal navigation (like clicking a link), but not for hidden forms/images.

**SameSite=None; Secure**
→ Cookies allowed in cross-site requests, but must use HTTPS.

When server sets cookie:
```
Set-Cookie: JSESSIONID=12345; HttpOnly; Secure; SameSite=Strict
```

If user is logged in and visits evil.com, and that site tries to send:

```html
<form action="https://bank.com/transfer" method="POST">
   <input type="hidden" name="amount" value="1000">
</form>
```

Browser will not send JSESSIONID cookie to bank.com because request originated from another site. Attack prevented at the browser level.

**In Spring Boot**

You can enforce this in application.properties:

```properties
server.servlet.session.cookie.same-site=strict
```

## 10. Storing User Credentials

### a) In-Memory Authentication

**What it is:** You hardcode usernames, passwords, and roles directly in the Spring config.

**Use Case:**
- Small apps
- Prototyping
- Unit/integration testing

**Pros:** Easy to set up, no external dependency.

**Cons:** Not secure, not scalable (every time you add a user, you must change code & restart app).

### b) Database (JDBC/JPA) Authentication

**What it is:** User credentials are stored in your DB (e.g., MySQL, PostgreSQL).

**Use Case:**
- Real production apps with many users
- Flexible because you can define custom schemas

**Pros:** Centralized, persistent, can be managed easily.

**Cons:** You must ensure passwords are hashed & secured.

### c) LDAP (Lightweight Directory Access Protocol)

**What it is:** A protocol to access directory services (like Microsoft Active Directory, OpenLDAP).

**Use Case:**
- Large organizations with centralized identity management
- Enterprises with thousands of employees

**Pros:** Centralized management, integrates with enterprise systems.

**Cons:** Setup & maintenance overhead.

## 11. Encoding vs Hashing vs Encryption

### a) Encoding: Transform Data - One Form to Another

- Does NOT use a key or password
- Is reversible
- Typically NOT used for securing data
- **Use cases:** Compression, Streaming
- **Example:** Base 64, Wav, MP3

### b) Hashing: Convert Data into a Hash (a String)

- One-way process
- NOT reversible
- You CANNOT get the original data back!
- **Use cases:** Validate integrity of data
- **Example:** bcrypt, scrypt

Hashing is a one-way function:
```
password123  --->  hash (irreversible, unique-looking value)
```

For example (with bcrypt):
```
password123  --->  $2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36Zl9kQvR6kXI6nW9jKqM4i
```

This means you never need to store the actual password. You only store its hash.

**How Authentication Works with Hashing:**
- User signs up → you hash their password and store the hash
  - "password123" → bcrypt("password123") → store hash in DB
- Next time they log in, they enter password123
- Spring Security (or your code) hashes this new input
- It then compares the new hash with the one stored in DB
- If they match → login success
- If not → authentication failed

### c) Encryption: Encoding Data Using a Key or Password

- You need the key or password to decrypt
- **Example:** RSA

## 12. What Does JWT Contain

### a) Header

Describes how the JWT is secured.

**Example:**

```json
{
  "typ": "JWT",             // Type of token
  "alg": "HS512"            // Algorithm used for signing
}
```

Here, HS512 = HMAC with SHA-512 (hashing algorithm).
This tells whoever is validating the token: "expect HS512 algorithm."

### b) Payload (Claims)

This contains information about the user and the token. It has two types of attributes:

**Standard Claims (registered claims defined by JWT spec)**
- `iss` → Issuer (who created the token)
- `sub` → Subject (who the token refers to, usually user id/username)
- `aud` → Audience (who should use this token, e.g., your app/service)
- `exp` → Expiration time (when token becomes invalid)
- `iat` → Issued at (when the token was created)

**Custom Claims (you define these)**

**Example:**
```json
{
  "role": "ADMIN",
  "email": "paras@example.com",
  "youratt1": "customValue"
}
```

### c) Signature

Includes a Secret

## 13. Symmetric Key Encryption

**Definition:** Same key is used for encryption and decryption.

Think of it like a shared secret (like a locker key).

**Example:**

```
Message: "Hello Paras"
Key: 12345

Encrypt("Hello Paras", 12345) → "a94fJk*7#..."
Decrypt("a94fJk*7#...", 12345) → "Hello Paras"
```

**Pros:**
- Very fast
- Simple to implement

**Cons:**
- Both parties must share the same key → key distribution problem
- If key leaks, security is broken

**Use Case:**
- Database encryption

## 14. Asymmetric Key Encryption

**Definition:** Uses a key pair:
- Public Key (shared with everyone)
- Private Key (kept secret by the owner)

Message encrypted with one key can only be decrypted with the other.

**Example:**

```
Public Key: 🔓
Private Key: 🔐

Encrypt("Hello Paras", Public Key) → "xf72$k2#..."
Decrypt("xf72$k2#...", Private Key) → "Hello Paras"
```

**Pros:**
- Solves key distribution problem (you can safely share public key)
- Enables digital signatures (proves authenticity)

**Cons:**
- Slower than symmetric encryption (100–1000x)
- Computationally heavy

**Use Case:**
- SSL/TLS (HTTPS handshake)
- JWT (when using RS256/RS512) → signed with private key, verified with public key
- SSH keys
- Digital certificates

## 15. Understanding Spring Security Authentication

When a request hits your app, it passes through the Spring Security Filter Chain. At the heart of it is the authentication process.

### AuthenticationManager

Think of it as the orchestrator. It doesn't authenticate by itself — instead, it delegates the work to one (or more) AuthenticationProviders. It tries providers in order until one succeeds.

### AuthenticationProvider

Responsible for doing actual authentication (based on a specific mechanism).

**Examples:**
- DaoAuthenticationProvider → username + password (via DB)
- JwtAuthenticationProvider → JWT validation
- LdapAuthenticationProvider → LDAP/AD integration

If authentication is successful → returns a fully populated Authentication object.

### UserDetailsService (My Code)

A core interface to fetch user data (from DB, LDAP, etc). Commonly used by DaoAuthenticationProvider. Loads: username, password (hashed), roles/authorities.

```java
public interface UserDetailsService {
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
```

After authentication succeeds, Spring Security needs to remember who the user is for the rest of the request (and session if enabled). This is done with these objects:

### Flow of Storage

```
SecurityContextHolder
    ↓
SecurityContext
    ↓
Authentication
    ↓
Principal (UserDetails) + GrantedAuthorities
```

- **SecurityContextHolder** → a wrapper (ThreadLocal) that stores the current user's security info
- **SecurityContext** → contains the Authentication object
- **Authentication** → holds details about the authenticated user
- **Principal** → the actual user object (often a UserDetails implementation)
- **GrantedAuthority** → roles/permissions the user has (ROLE_USER, ROLE_ADMIN, SCOPE_READ)

### Example in Action

1. User logs in with username/password → request passes through filter
2. AuthenticationManager delegates to DaoAuthenticationProvider
3. Provider calls UserDetailsService.loadUserByUsername()
4. Password is checked using PasswordEncoder (like bcrypt)
5. If valid → builds UsernamePasswordAuthenticationToken with:
   - Principal (user info)
   - GrantedAuthorities (roles)
6. Marks as authenticated
7. Stored in SecurityContextHolder

Now, anywhere in your app, you can get the authenticated user:

```java
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
String username = auth.getName();
Collection<? extends GrantedAuthority> roles = auth.getAuthorities();
```

### Summary

- **AuthenticationManager** → coordinator
- **AuthenticationProvider** → does actual authentication
- **UserDetailsService** → loads user info (DB, LDAP, etc.)

**Result** → stored in SecurityContextHolder → SecurityContext → Authentication → GrantedAuthorities

### Flow Diagram

```
User submits login form
       ↓
UsernamePasswordAuthenticationToken created
       ↓
AuthenticationManager
       ↓
AuthenticationProvider (DaoAuthenticationProvider)
       ↓
Calls UserDetailsService.loadUserByUsername(username)
       ↓
Fetch user info from DB/LDAP/In-memory
       ↓
Check password (PasswordEncoder)
       ↓
If valid, create Authentication object
       ↓
Store in SecurityContextHolder
```

---

# Aspect-Oriented Programming (AOP)

## 1. The Problem – Cross-Cutting Concerns

When you build applications, you usually follow a layered architecture:
- **Web Layer** → handles HTTP requests/responses, JSON/XML conversion
- **Business Layer** → core business logic
- **Data Layer** → persistence logic (DB access, repositories)

Now, suppose you want to add:
- Logging (track method execution, parameters, etc.)
- Security (check if user has permission)
- Performance Monitoring (how long a method takes)
- Transactions (begin/commit/rollback DB transactions)

These are not tied to a single layer. They apply across all layers. These are called **Cross-Cutting Concerns** because they "cut across" multiple layers.

Normally, you would add logging or security inside every method:

**Problem:**
- Code repetition (logging/security everywhere)
- Code tangling (business logic mixed with logging/security)
- Hard to maintain (change logging style → update all methods)

AOP allows you to separate cross-cutting concerns into reusable modules called aspects. Instead of writing logging/security code inside each method, you declare it in a separate class (Aspect) and let AOP automatically apply it to the target methods.

## 2. Key Concepts in AOP

### Aspect

A module that encapsulates cross-cutting concern (e.g., LoggingAspect, SecurityAspect).

### Join Point

A specific point in the execution of the program, like:
- A method call
- An exception being thrown
- A field being accessed

### Pointcut

A predicate (expression) that matches join points. For example:

```java
execution(* com.myapp.service.*.*(..))
```

This matches execution of all methods in service package.

### Advice

The actual code you want to run at a join point. 

**Types of advice:**
- **Before** → run before method execution
- **AfterReturning** → run after method returns successfully
- **AfterThrowing** → run if method throws exception
- **After (finally)** → always run after method (success/failure)
- **Around** → wrap around method execution (control execution, measure performance)

### Weaving

The process of applying aspects to target objects. Done by Spring AOP at runtime (using proxies).

## 3. Example of AOP

```java
@Aspect
@Component
public class LoggingAspect {

    // Pointcut: all methods inside service package
    @Pointcut("execution(* com.myapp.service.*.*(..))")
    public void serviceMethods() {}

    // Advice: runs before method execution
    @Before("serviceMethods()")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("Executing: " + joinPoint.getSignature());
    }

    // Advice: runs after method execution
    @AfterReturning(value = "serviceMethods()", returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {
        System.out.println("Completed: " + joinPoint.getSignature() + " with result = " + result);
    }

    // Around Advice (performance monitoring)
    @Around("serviceMethods()")
    public Object measureTime(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = pjp.proceed(); // execute actual method
        long timeTaken = System.currentTimeMillis() - start;
        System.out.println("Time taken by " + pjp.getSignature() + ": " + timeTaken + "ms");
        return result;
    }
}
```

## 4. Pointcut Syntax

```java
execution(* com.myapp.service.UserService.*(..))   // all methods in UserService
execution(* com.myapp.service.*.*(..))             // all methods in service package
execution(public * *(..))                          // all public methods
execution(* *(String, ..))                         // methods with first param String
execution(* save*(..))                             // methods starting with save
execution(* com.myapp..*(..))                      // methods in package & subpackages
```

---

## End of Documentation

This README provides a comprehensive overview of Spring Framework, Spring Boot, JPA/Hibernate, Spring Security, and AOP. Each section includes definitions, examples, use cases, and best practices to help you build enterprise Java applications effectively.