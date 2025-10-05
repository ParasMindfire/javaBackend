## 1)in 02-spring-core-demo > 01-constructor-injection how autowired worked 


### 1️⃣ Your `DemoController` constructor

```java
@Autowired
public DemoController(Coach theCoach) {
    this.myCoach = theCoach;
}
```

* The `@Autowired` on the constructor tells Spring:

  > “Hey Spring, when creating `DemoController`, find a bean of type `Coach` and pass it as `theCoach`.”

---

### 2️⃣ The `CricketCoach` bean

```java
@Component
public class CricketCoach implements Coach {
    public String getDailyWorkout() {
        return "Practise fast  for 15mins";
    }
}
```

* `@Component` tells Spring:

  > “Make an instance of `CricketCoach` and register it in the **ApplicationContext** as a bean.”
* `CricketCoach` **implements `Coach`**, so it is a candidate whenever Spring needs a `Coach` bean.

---

### 3️⃣ How Spring injects the dependency

1. Spring sees `DemoController` is a bean (because it’s annotated with `@RestController`, which is a specialization of `@Component`).
2. Spring notices the constructor has `@Autowired` and the parameter type is `Coach`.
3. Spring searches the **ApplicationContext** for a bean of type `Coach`.
4. It finds `CricketCoach` (since it’s a `@Component` implementing `Coach`).
5. Spring calls the constructor:

   ```java
   new DemoController(cricketCoachInstance);
   ```

   and assigns it to `myCoach`.

---

### 4️⃣ Result

Now, whenever you call:

```java
@GetMapping({"/dialyworkout"})
public String getDailyWorkout() {
    return this.myCoach.getDailyWorkout();
}
```

* Spring uses the injected `CricketCoach` instance.
* The endpoint `/dialyworkout` returns:

```
Practise fast for 15mins
```

---

💡 **Key points:**

* Spring injects **by type** (`Coach` → `CricketCoach`).
* If multiple beans implement `Coach`, you’d need `@Qualifier` to specify which one.
* Constructor injection (like here) is **preferred** because it ensures the dependency is always provided and makes the class easier to test.

---




## 2) what does lazy do in 02-spring-core-demo > 06-lazy-initialisation

Ah! Now we are in **Spring Lazy Initialization** territory. Let’s break it down clearly using your code.

---

### 1️⃣ What `@Lazy` does

```java
@Component
@Lazy
public class BaseballCoach implements Coach { ... }
```

* Normally, Spring creates **all singleton beans at startup** when the application context is initialized.

  * That means, constructors of all `@Component` beans are called immediately.
  * You would see `System.out.println` for all beans when the app starts.

* **`@Lazy` changes this behavior**:

  * The bean **will not be created at startup**.
  * Spring will create it **only when it is first requested** (i.e., injected somewhere or explicitly retrieved from the context).

---

### 2️⃣ How it works in your example

You have multiple beans implementing `Coach`:

| Bean            | Annotation            | Output at startup                     |
| --------------- | --------------------- | ------------------------------------- |
| `CricketCoach`  | `@Component`          | Prints: `Constructor of CricketCoach` |
| `TennisCoach`   | `@Component @Primary` | Prints: `Constructor of TennisCoach`  |
| `TrackCoach`    | `@Component`          | Prints: `Constructor of track coach`  |
| `BaseballCoach` | `@Component @Lazy`    | **Does not print yet**                |

* **At startup**, Spring will **skip creating `BaseballCoach`**.
* When you inject it somewhere, like:

```java
@Autowired
private BaseballCoach baseballCoach;
```

* Only then will Spring call its constructor and you will see:

```
Constructor of baseballCOach
```

---

### 3️⃣ Key points about `@Lazy`

1. Works **only for singleton beans** (by default).
2. Can be used at:

   * Class level (`@Component @Lazy`) → delays bean creation
   * Injection point (`@Lazy @Autowired`) → delays dependency injection
3. Useful when:

   * Bean initialization is expensive (heavy computation, external API calls).
   * You don’t always need the bean in your app.

---

### 4️⃣ Real-world analogy

Think of it like **lazy-loading a video**:

* Normally, Spring **loads all videos at once** at startup.
* `@Lazy` says:

  > “Don’t load this video until the user actually clicks play.”

---

So in your code, **`BaseballCoach` will only be created when someone actually asks for it**, while all other coaches are created at startup.

---


## 3)what is bean scoping and what does @Scope("prototype") do in 02-spring-core-demo > 07-bean-scoping

Ah! This is a classic **Spring bean scope and dependency injection example**. Let me explain everything clearly.

---

### 1️⃣ What your code is doing

#### a) `DemoController`

```java
@Autowired
public void setCoach(@Qualifier("cricketCoach") Coach theCoach, 
                     @Qualifier("cricketCoach") Coach theAnotherCoach) {
    System.out.println("Inside demo controller");
    this.myCoach = theCoach;
    this.anotherCoach = theAnotherCoach;
}
```

* This is **setter injection** using `@Autowired`.
* `@Qualifier("cricketCoach")` tells Spring:

  > “Inject the bean with name `cricketCoach` here.”
* You are injecting **two beans of type `Coach`**, both pointing to `cricketCoach`.

#### b) `CricketCoach`

```java
@Component
@Scope("prototype")
public class CricketCoach implements Coach { ... }
```

* `@Component` → Spring will manage this class as a bean.
* `@Scope("prototype")` → tells Spring **do not reuse the same instance**.

  * Every time Spring is asked for this bean, it **creates a new instance**.

---

### 2️⃣ Difference between singleton and prototype

| Scope     | Behavior                                         | Your code outcome                                           |
| --------- | ------------------------------------------------ | ----------------------------------------------------------- |
| singleton | Only **one instance** per Spring container.      | `myCoach` and `anotherCoach` would point to **same object** |
| prototype | **New instance every time** a bean is requested. | `myCoach` and `anotherCoach` are **different objects**      |

---

### 3️⃣ What happens in your controller

1. Spring sees the setter injection with two `@Qualifier("cricketCoach")`.
2. Because `CricketCoach` is **prototype scoped**:

   * `theCoach` → new `CricketCoach` object
   * `theAnotherCoach` → another new `CricketCoach` object
3. When you call `/check` endpoint:

```java
boolean var10000 = this.myCoach == this.anotherCoach;
return "check if both beans are mapping to same pointer :" + var10000;
```

* It prints:

```
check if both beans are mapping to same pointer :false
```

* ✅ This confirms that **prototype scope creates separate instances**.

4. When you call `/dailyworkout` endpoint:

```java
return this.myCoach.getDailyWorkout();
```

* Returns:

```
Practise cricket fast for 15mins !!!
```

---

### 4️⃣ Prototype Scope Summary

* Annotation: `@Scope("prototype")`
* Meaning: **Spring does not cache the bean**; creates a new object **every time** it is injected or retrieved from the context.
* Contrast with **singleton** (default scope):

  * Only one instance per container.
  * Every injection points to the same object.

---

### 5️⃣ Visual analogy

* **Singleton** → like a single shared TV in a house; everyone watches the same TV.
* **Prototype** → like printing a new copy of a book every time someone requests it; each person gets their own copy.

---


## 4)Difference between @Configuration + @Bean and @Component

Perfect 👍 Let’s build a **side-by-side example** so you can clearly see the difference between `@Component` and `@Bean`.

---

### ✅ Using `@Component`

```java
// File: SwimCoach.java
package com.paras.springCore.common;

import org.springframework.stereotype.Component;

@Component("aquatic")   // Bean will be registered as "aquatic"
public class SwimCoach implements Coach {
   @Override
   public String getDailyWorkout() {
      return "Swim 1000 meters as a warm up!";
   }
}
```

* Very simple: just mark it with `@Component`.
* Spring auto-detects it via **component scanning**.
* No extra configuration required.

---

### ✅ Using `@Configuration + @Bean`

```java
// File: SportConfig.java
package com.paras.springCore.config;

import com.paras.springCore.common.SwimCoach;
import com.paras.springCore.common.Coach;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SportConfig {

   @Bean("aquatic")   // Bean will also be registered as "aquatic"
   public Coach swimCoach() {
      SwimCoach coach = new SwimCoach();
      // Custom setup logic here
      System.out.println("Customizing SwimCoach bean before returning...");
      return coach;
   }

   @Bean("fastSwimCoach")
   public Coach fastSwimCoach() {
      return new SwimCoach();   // different bean with same class
   }
}
```

* Here you **manually create** the bean with custom logic.
* You can even register **multiple beans** from the same class (`aquatic`, `fastSwimCoach`).
* Useful when you **can’t annotate the class** (third-party libraries).

---

### ✅ Comparing the two

| Feature                     | `@Component`       | `@Bean`                     |
| --------------------------- | ------------------ | --------------------------- |
| Auto-detection              | Yes (via scanning) | No (you define it manually) |
| Third-party classes         | ❌ Not possible     | ✅ Possible                  |
| Custom creation logic       | ❌ Limited          | ✅ Full control              |
| Multiple beans of same type | ❌ Difficult        | ✅ Easy                      |
| Simplicity                  | ✅ Very simple      | ❌ More verbose              |

---

### ✅ Injection Example

```java
@RestController
public class DemoController {

   @Autowired
   @Qualifier("aquatic")
   private Coach swimCoach;   // Could come from @Component OR @Bean

   @Autowired
   @Qualifier("fastSwimCoach")
   private Coach anotherSwimCoach;  // Only possible with @Bean
}
```

---

👉 So, **use `@Component` when it’s your own class** and no special config is needed.
👉 Use **`@Bean` when you need custom creation logic or want to register third-party classes**.

---


## 5) Why is DAO needed ?

Great question 🚀 Let’s break down **DAO (Data Access Object)** in simple terms with reasoning on *why it is necessary*.

---

### 1️⃣ What is DAO?

* **DAO = Data Access Object**
* It is a **design pattern** used to **separate database access logic** from the rest of the application.
* DAO provides an **abstraction layer** between the business logic and the database (JDBC, JPA, Hibernate, etc.).

👉 In simple words:
DAO acts like a **middleman** between your application and the database.

---

### 2️⃣ Without DAO (Bad Design)

Suppose you have a `StudentService` that directly talks to the database:

```java
public class StudentService {

    public Student getStudent(int id) {
        Connection conn = DriverManager.getConnection(...);
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM students WHERE id=?");
        ...
        // fetch student from DB
    }
}
```

❌ Problems:

* Database code is **mixed with business logic**.
* Harder to **maintain** (if you change DB or queries, you must change service code).
* **Not reusable** (every service might need to duplicate DB logic).
* **Harder to test** (unit testing business logic requires a real database).

---

### 3️⃣ With DAO (Good Design)

You create a **StudentDAO** whose only responsibility is DB interaction:

```java
public interface StudentDAO {
    Student getStudent(int id);
    void saveStudent(Student student);
}
```

```java
public class StudentDAOImpl implements StudentDAO {
    @Override
    public Student getStudent(int id) {
        // all DB-related code here
        return new Student(id, "Paras");
    }

    @Override
    public void saveStudent(Student student) {
        // insert into database
    }
}
```

Now your **Service Layer** uses DAO:

```java
public class StudentService {
    private StudentDAO studentDAO;

    public StudentService(StudentDAO studentDAO) {
        this.studentDAO = studentDAO;
    }

    public Student fetchStudent(int id) {
        return studentDAO.getStudent(id); // business logic separate
    }
}
```

✅ Benefits:

* **Separation of concerns** → DB logic is isolated in DAO.
* **Reusability** → DAO can be used by multiple services.
* **Flexibility** → Easy to switch from MySQL → MongoDB → JPA → Hibernate without changing business logic.
* **Testability** → You can mock DAO for testing without hitting a real database.

---

### 4️⃣ Why is DAO necessary?

* To keep **business logic clean** and independent of database operations.
* To make code **maintainable** and **scalable**.
* To support **different databases/ORMs** easily.
* To make **unit testing** possible without requiring a real database.

---

### 5️⃣ Analogy

Think of DAO like a **waiter in a restaurant**:

* The waiter (DAO) takes your order (business request) and communicates with the kitchen (database).
* You (business logic) don’t directly talk to the kitchen (database).
* If the kitchen changes (MySQL → PostgreSQL), you don’t care — the waiter adapts.

---

👉 **In Spring**, we often use:

* `JdbcTemplate` with DAO classes.
* Or JPA repositories (`Spring Data JPA`) which are like pre-built DAOs.

---

Got it 👍 Here’s your set of questions written in a neat **README.md format**:  

---


## 6a) Database Connection & Table Creation
- How does the database get connected through `application.properties`?  
- Does that configuration allow the `EntityManager` to automatically create tables in the database with the given fields from the entity classes?

---

## 6b) Multiple Entities & Persist Behavior
- In `StudentDAOimpl` we have:
  ```java
  @Autowired 
  public StudentDAOimpl(EntityManager entityManager) {
      this.entityManager = entityManager;
  }
  ```
  What happens if there are multiple entities like `Student`, `Teacher`, etc.?  
- How does:
  ```java
  entityManager.persist(theStudent);
  ```
  know to save the data specifically in the **student** table and not in a different table?

---

## 6c) `find` vs `createQuery`
- Why in some cases do we use:
  ```java
  return entityManager.find(Student.class, id);
  ```
  while in other cases we write:
  ```java
  TypedQuery<Student> query = 
      entityManager.createQuery("FROM Student ORDER BY id DESC", Student.class);
  ```
- What is the difference between using `find` and writing custom queries with `TypedQuery`?

---

## 6d) Is the code in repo 03-springboot-hibernate-jpa-crud > StudentDAOimpl raw JPA ?


Nice set of code — good job getting this far. I’ll answer your numbered doubts one-by-one, explain the why, and give a few small suggestions / best-practices you can copy into your project.

---

### 1) Did Hibernate create the `student` table automatically?

**Yes (if `spring.jpa.hibernate.ddl-auto=update` is active and your app actually started and connected to the DB).**

* `@Entity` on `Student` + Spring Boot + Hibernate (the JPA provider) → Hibernate inspects your entity and maps it to a table.
* `@Table(name="student")` tells Hibernate the DB table name explicitly. If you didn’t supply `@Table`, Hibernate would default the table name (usually the entity class name).
* `spring.jpa.hibernate.ddl-auto=update` tells Hibernate to update the DB schema to match your entities.

  * `create` = drop & create each start (dangerous for prod)
  * `update` = alter tables to match entities (safe-ish for development)
  * `validate` = only check, don’t change DB
  * `none` = do nothing

So when your app starts, Hibernate created/updated the `student` table according to the `Student` fields and annotations.

---

Great follow-up 👍 Let’s go step by step.

---

### **2) About `@Autowired EntityManager` and multiple entities**

```java
private EntityManager entityManager;

@Autowired
public StudentDAOimpl(EntityManager entityManager) {
    this.entityManager = entityManager;
}
```

* **`EntityManager` is not per-entity**.
  It is a **general-purpose object** provided by JPA that can manage **all entities in your persistence context** (all the entities annotated with `@Entity` in your project).

* When you autowire `EntityManager`, Spring Boot creates a **single `EntityManagerFactory`** (based on your `spring.datasource.*` config).
  From this factory, it provides a single `EntityManager` bean by default (unless you configure multiple datasources).

✅ So, even if you have multiple entities (`Student`, `Teacher`, `Course`, etc.), **you still get the same `EntityManager` instance**.
It doesn’t care which entity—it will handle all of them.
When you do:

```java
entityManager.persist(student);
```

JPA looks at the **runtime type of the object** (here `Student`), and because your class is annotated with `@Entity` + `@Table(name="student")`, it knows which table (`student`) to use.

👉 In short: `EntityManager` is shared, and the mapping to the correct table is decided by the entity class annotation (`@Entity`, `@Table`), not by the `EntityManager`.

---

### **3) Default vs Custom Queries**

Yes, exactly ✅

* `EntityManager` has **basic built-in operations** for **single-entity actions**:

  * `persist(entity)` → INSERT new record
  * `find(EntityClass.class, id)` → SELECT by primary key
  * `merge(entity)` → UPDATE existing record
  * `remove(entity)` → DELETE

These are universal operations, and JPA knows how to generate SQL based on the entity metadata (`@Entity`, `@Column`, etc.).

* But when you want **custom queries** (like `findByLastName`, `findAllSorted`, `deleteAll`), you have to write **JPQL** or **native SQL**:

```java
TypedQuery<Student> query =
    entityManager.createQuery("FROM Student WHERE lastName = :lname", Student.class);
```

Because JPA can’t guess these custom requirements—it only knows about basic persistence operations.

---

✅ **Answer in short:**

1. `@Autowired EntityManager` → Always injects the one tied to your datasource (manages all entities).
2. The actual entity-to-table mapping comes from the entity class annotations (`@Entity`, `@Table`).
3. `find/persist/merge/remove` = default CRUD operations.
   For anything else (filters, ordering, joins) → you must write JPQL.

---

Would you like me to also show you **how this changes when you have multiple datasources** (e.g., 2 different DBs, each with its own `EntityManager`)? That’s the scenario where Spring might get confused.

### 4) Is this raw JPA?

**Yes — this is JPA (Jakarta Persistence API) code using `EntityManager`.** Hibernate is the JPA implementation under the hood (as suggested by your `spring.jpa.hibernate.*` properties).

* You are using *programmatic JPA* (EntityManager + JPQL). This is perfectly valid JPA.
* **Contrast:** Spring Data JPA (`JpaRepository`) is a higher-level abstraction that reduces boilerplate (you define interfaces and Spring provides implementations).

  * Your DAO is a classic JPA DAO implementation (not Spring Data).

---





## 6e) does studentDao get created before this and passed as an argument ?? how is this working ?

```java
@Bean public CommandLineRunner commandLineRunner(StudentDAO studentDAO){ 
   return _ -> { 
      createMultipleStudent(studentDAO); 
   }; 
}

```

Great question 🚀 You’re diving into one of the **core Spring Boot mechanisms**: dependency injection + bean lifecycle. Let’s break it down step by step.

---

### Code in Question

```java
@Bean
public CommandLineRunner commandLineRunner(StudentDAO studentDAO) {
    return _ -> {
        createMultipleStudent(studentDAO);
        // other methods...
    };
}
```

### 🔹 Does `StudentDAO` get created before this?

✅ Yes.
Here’s the order:

1. Spring Boot starts and scans your project for `@Component`, `@Repository`, `@Service`, and `@Bean` definitions.
2. It finds `StudentDAOimpl` (since you annotated it with `@Repository`) and creates a `StudentDAOimpl` bean.

   * This bean needs an `EntityManager`.
   * Spring Boot auto-configures an `EntityManager` based on `application.properties` (using DataSource + Hibernate).
   * The `EntityManager` is injected into `StudentDAOimpl` via constructor.
3. Now the `StudentDAO` bean is ready.
4. Spring sees your `@Bean public CommandLineRunner commandLineRunner(StudentDAO studentDAO)` method.

   * Since a `StudentDAO` bean already exists, it injects that bean here.
5. After the application context is loaded, Spring executes all `CommandLineRunner` beans.

   * That’s when your code (`createMultipleStudent(studentDAO)`) actually runs.

---

### 🔹 Why does this work?

Because Spring Boot’s **IoC container (ApplicationContext)** resolves dependencies **before running any `CommandLineRunner` beans**.

Think of it like this:

* First → Spring wires up **all beans** (like `StudentDAO`, `EntityManager`, etc.)
* Then → It passes those beans into your method arguments.
* Finally → After context startup, the runner executes your logic.

---

✅ So yes, your assumption is right:
👉 `StudentDAO` is created **before** this method executes, and it is **passed automatically** as an argument by Spring.

---


## 7)Do i need to create custom exception everytime like in 04-springboot-rest-crud > 01-springboot-rest-crud ?

---

### 🔹 Do you need separate exception classes?

👉 **Not always.**
It depends on **how specific you want your exception handling to be**.

---

### ✅ Option 1: Use a few custom exceptions (best for clarity)

For example:

* `StudentNotFoundException` → thrown when a student with given ID doesn’t exist.
* `CourseNotFoundException` → for missing course.
* `ValidationException` → for invalid inputs.

This gives you **fine-grained control** in your `@ExceptionHandler`, and your error messages are clearer.

```java
@ExceptionHandler
public ResponseEntity<ErrorResponse> handleException(StudentNotFoundException exc) {
    return buildErrorResponse(exc, HttpStatus.NOT_FOUND);
}
```

---

### ✅ Option 2: Use one generic exception (best for simplicity)

Instead of multiple exceptions, you could create **one custom exception** like `ResourceNotFoundException` and reuse it for students, courses, etc.

```java
throw new ResourceNotFoundException("Student not found with id - " + id);
```

And handle it in one place:

```java
@ExceptionHandler
public ResponseEntity<ErrorResponse> handleException(ResourceNotFoundException exc) {
    return buildErrorResponse(exc, HttpStatus.NOT_FOUND);
}
```

---

### ✅ Option 3: Rely on built-in exceptions

Spring and JPA already throw exceptions like:

* `EntityNotFoundException`
* `DataIntegrityViolationException`
* `MethodArgumentNotValidException` (for validations)

You can catch these in your **global exception handler** without making custom classes at all.

---

### 🔹 Best Practice in Real Projects

* Use **a few custom exceptions** where it makes sense (`NotFoundException`, `ValidationException`).
* Reuse them across multiple entities (instead of making `StudentNotFoundException`, `CourseNotFoundException`, etc., just have one `ResourceNotFoundException`).
* Create **specialized ones** only if they need different handling logic.

---

So, short answer to your question:
👉 **No, you don’t need separate exception classes for each entity.**
You can:

* use a **generic one** (e.g., `ResourceNotFoundException`),
* or keep a few specific ones depending on your project complexity.

---



## 8) what does @ControllerAdvice do in 02-spring-global-exceptionHandler is it for writing clean codes 

---

### 🔹 What is `@ControllerAdvice`?

* `@ControllerAdvice` is a **Spring annotation** used to handle exceptions **globally** across all controllers.
* Instead of writing `@ExceptionHandler` inside every controller, you write them **once** inside a `@ControllerAdvice` class.
* Think of it as a **"global error handler"** for your REST API.

---

### 🔹 How does it work here?

```java
@ControllerAdvice
public class StudentRestExceptionHandler {
    
    // Handles StudentNotFoundException only
    @ExceptionHandler
    public ResponseEntity<StudentErrorResponse> handleException(StudentNotFoundException exc) {
        StudentErrorResponse error = new StudentErrorResponse();
        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.setMessage(exc.getMessage());
        error.setTimeStamp(System.currentTimeMillis());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // Handles any other Exception (generic fallback)
    @ExceptionHandler
    public ResponseEntity<StudentErrorResponse> handleException(Exception exc) {
        StudentErrorResponse error = new StudentErrorResponse();
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setMessage(exc.getMessage());
        error.setTimeStamp(System.currentTimeMillis());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
```

* If your controller throws `StudentNotFoundException`, the **first handler** runs.
* If it throws any other unhandled exception, the **second handler** runs as a **fallback**.

---

### 🔹 Why separate file (`StudentRestExceptionHandler`)?

👉 **Clean code & separation of concerns**:

* Your controller should focus only on **business logic** (like fetching/updating students).
* Your exception handler should focus only on **error responses**.
* With `@ControllerAdvice`, you can reuse this handler for **all controllers** in your project without duplicating code.

---

### 🔹 Quick Analogy

Think of `@ControllerAdvice` as a **global customer service desk** in a company:

* If a problem happens in **any department (controller)**, instead of each department handling complaints differently, everything is forwarded to the **customer service desk (ControllerAdvice)** which gives a **standardized response**.

---



## 9)Why did we add ObjectMapper in 04-springboot-rest-crud > 03-spring-boot-restCrudEmployee ??
---

### 🔹 Why convert `Employee` to JSON with `ObjectMapper` in PATCH?

* **Incoming request JSON:**

```json
{
    "firstName": "John",
    "email": "john@example.com"
}
```

* Spring Boot already **deserializes this JSON** into your method parameter `Map<String,Object> patchPayload`. ✅

  * So you **do have the patch as a Java Map**.

* `tempEmployee` is the **existing Employee object** from the database.

* Now the goal of PATCH: **update only the fields present in the patch** without touching the others.

---

### 🔹 Why not just update `tempEmployee` directly?

You could do it manually:

```java
if(patchPayload.containsKey("firstName")) {
    tempEmployee.setFirstName((String) patchPayload.get("firstName"));
}
if(patchPayload.containsKey("email")) {
    tempEmployee.setEmail((String) patchPayload.get("email"));
}
```

* Works, but if `Employee` has **many fields**, this becomes **boilerplate and error-prone**.

---

### 🔹 How `ObjectMapper` helps

```java
ObjectNode employeeNode = objectMapper.convertValue(tempEmployee, ObjectNode.class);
ObjectNode patchNode = objectMapper.convertValue(patchPayload, ObjectNode.class);
employeeNode.setAll(patchNode);
return objectMapper.convertValue(employeeNode, Employee.class);
```

* Converts the existing Employee → JSON tree (`ObjectNode`)
* Converts the patch Map → JSON tree
* Merges them automatically (`setAll`)
* Converts back to Employee object

✅ **Result:** All fields in the patch override the existing ones, and all other fields remain unchanged.

---

### 🔹 Key point

* **Incoming request is JSON** → Spring converts it into a **Map** (`@RequestBody Map<String,Object>`).
* **Why convert Employee to JSON?** → To **merge the incoming patch with existing object dynamically**, without manually checking each field.

So, yes, the request is JSON, but your **existing Employee object is Java**, and you need a way to combine the two. `ObjectMapper` makes that clean.

---


## 10)Explain the use DemoSecurityConfig and in memory storage in 05-springboot-rest-security 
---

### 🧩 1️⃣ When you used `InMemoryUserDetailsManager`

```java
@Bean
public InMemoryUserDetailsManager userDetailsManager(){
    UserDetails paras = User.builder()
        .username("paras")
        .password("{noop}test123")
        .roles("EMPLOYEE","MANAGER","ADMIN")
        .build();

    return new InMemoryUserDetailsManager(paras);
}
```

👉 Here, everything (users and roles) is **stored in memory** — no database involved.
You directly define which users exist and which roles they have.

When you did this:

* Spring automatically knew: “paras has roles EMPLOYEE, MANAGER, ADMIN”
* So in your `SecurityFilterChain`, when you wrote:

  ```java
  .requestMatchers(HttpMethod.GET, "/api/employees").hasRole("EMPLOYEE")
  ```

  it worked fine because Spring could read the roles from memory.

---

### 🧩 2️⃣ What happens when you switch to `JdbcUserDetailsManager`

Now, instead of storing users in memory, Spring fetches user data from a **database**.

```java
@Bean
public UserDetailsManager userDetailsManager(DataSource dataSource){
    JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);

    jdbcUserDetailsManager.setUsersByUsernameQuery(
        "select user_id, pw, active from members where user_id=?");

    jdbcUserDetailsManager.setAuthoritiesByUsernameQuery(
        "select user_id, role from roles where user_id=?");

    return jdbcUserDetailsManager;
}
```

Here’s the **flow** 👇

### ➤ Step 1: User logs in with username & password

Spring Security calls `JdbcUserDetailsManager.loadUserByUsername(username)`

### ➤ Step 2: It runs your SQL queries:

* First query:

  ```sql
  select user_id, pw, active from members where user_id=?
  ```

  → gets username, password, and whether the account is active.

* Second query:

  ```sql
  select user_id, role from roles where user_id=?
  ```

  → gets all the roles (authorities) that belong to this user.

### ➤ Step 3: Spring builds a `UserDetails` object

It now knows:

* username
* password
* active status
* roles (like ROLE_EMPLOYEE, ROLE_MANAGER, etc.)

---

### 🧩 3️⃣ Why your filter chain uses `.hasRole()` even if `Employee` table has no `role` field

Excellent observation 💡
Your `Employee` table (`employee_directory.employee`) is your **business data** — not for authentication.

The **roles** are not supposed to be inside your `employee` table.
They are typically stored separately in **authentication tables**, like:

```sql
CREATE TABLE members (
  user_id VARCHAR(50) NOT NULL PRIMARY KEY,
  pw VARCHAR(68) NOT NULL,
  active BOOLEAN NOT NULL
);

CREATE TABLE roles (
  user_id VARCHAR(50) NOT NULL,
  role VARCHAR(50) NOT NULL,
  FOREIGN KEY (user_id) REFERENCES members(user_id)
);
```

So:

* `members` = stores login credentials
* `roles` = stores role mappings (like EMPLOYEE, ADMIN, etc.)
* `employee` = stores actual company data (unrelated to login security)

---

### 🧩 4️⃣ What happens in the Filter Chain

When a request comes:

1. Spring Security checks the HTTP request.
2. The user’s username is extracted from the authentication token (HTTP Basic or JWT, etc.)
3. It checks in memory / DB what roles the user has.
4. Then, it compares with your filter rules:

   ```java
   .requestMatchers(HttpMethod.GET, "/api/employees").hasRole("EMPLOYEE")
   ```

   If user has `ROLE_EMPLOYEE` → ✅ allowed.
   If not → ❌ 403 Forbidden.

---

### 🧩 5️⃣ Why you need the role tables

Since you’re using database authentication now, you **must have** those two tables:

* `members` (for login info)
* `roles` (for authorization info)

Otherwise, Spring won’t be able to know what roles your user has — and all your `hasRole()` checks will fail.

---

### 🧩 6️⃣ TL;DR — the difference between both approaches

| Approach                     | Where users are stored | Where roles come from     | Good for     | Example                  |
| ---------------------------- | ---------------------- | ------------------------- | ------------ | ------------------------ |
| `InMemoryUserDetailsManager` | Hardcoded in Java code | Hardcoded roles in code   | Simple demos | Small POCs               |
| `JdbcUserDetailsManager`     | Database tables        | Database tables (`roles`) | Real apps    | Enterprise login systems |

---


## 11)Tell me abou the application properties of 05-springboot-rest-security

---

### ⚙️ 1️⃣ Database Configuration — for JDBC / JPA

These first three lines tell Spring **how to connect to your MySQL database**:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/employee_directory
spring.datasource.username=springstudent
spring.datasource.password=springstudent
```

### 👉 What happens here:

* **`spring.datasource.url`**
  → The **JDBC connection URL** to your database.
  `jdbc:mysql://localhost:3306/employee_directory` means:

  * Use MySQL driver (`jdbc:mysql`)
  * Connect to the database running on your **local system** (`localhost`)
  * Use the port **3306** (default MySQL port)
  * Access the database **employee_directory**

* **`spring.datasource.username`**
  → The **username** used to log in to your MySQL database.
  Here it’s `springstudent`.

* **`spring.datasource.password`**
  → The **password** for that user.
  Here it’s also `springstudent`.

✅ These three properties allow:

* JPA / Hibernate (for your `Employee` entity)
* and your `JdbcUserDetailsManager`
  to connect to the **same database**.

---

### 🔐 2️⃣ Spring Security Default Login Configuration

These two lines are **completely separate** — they configure **default in-memory users** for Spring Security.

```properties
spring.security.user.name=paras
spring.security.user.password=Paras@123
```

### 👉 What happens here:

If you **don’t define your own UserDetailsService** (like `InMemoryUserDetailsManager` or `JdbcUserDetailsManager`),
Spring Boot **automatically creates a default user** using these properties.

That means:

* Username = `paras`
* Password = `Paras@123`
* Role = `USER` (by default)

So when you start the app and access a secured endpoint, Spring will ask for login.
You can enter these credentials to log in successfully.

---

### 🧩 3️⃣ Which One Takes Effect in Your Code

Right now, in your setup 👇

```java
@Bean
public UserDetailsManager userDetailsManager(DataSource dataSource){
    JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
    ...
    return jdbcUserDetailsManager;
}
```

You have **explicitly defined** your own `UserDetailsService` (`JdbcUserDetailsManager`).

➡️ Because of this,
the default Spring Boot user (`spring.security.user.name` and `spring.security.user.password`) is **ignored**.

Spring only uses those two properties **if** you don’t define your own login setup.

---

### ✅ TL;DR Summary

| Property                        | Purpose                          | Used by                             | Active in your project?          |
| ------------------------------- | -------------------------------- | ----------------------------------- | -------------------------------- |
| `spring.datasource.url`         | Database connection URL          | JDBC / JPA / JdbcUserDetailsManager | ✅ Yes                            |
| `spring.datasource.username`    | DB username                      | JDBC / JPA / JdbcUserDetailsManager | ✅ Yes                            |
| `spring.datasource.password`    | DB password                      | JDBC / JPA / JdbcUserDetailsManager | ✅ Yes                            |
| `spring.security.user.name`     | Default in-memory login username | Spring Security (auto config)       | ❌ Ignored (because you use JDBC) |
| `spring.security.user.password` | Default in-memory login password | Spring Security (auto config)       | ❌ Ignored (because you use JDBC) |

---



## 12) In 06-springboot-spring-mvc > 01-thymeleafDemo-helloWorld

1. When I hit `/showForm`, it shows `helloworld-form`.
   → But what happens next — does it show something in `helloworld`?

2. In `helloworld-form.html`, what is `theData`?

3. When I hit submit, the form goes to `processFormVersionThree`.
   → From where does `@RequestParam("studentName")` get its value?

4. Does the value come from the form’s `name="studentName"` attribute?

5. Is it assigned to the variable `theName` automatically?

6. What exactly is `Model` in the method parameters, and how does it work?

---

### 🧭 1. Flow Overview

You have three main files working together:

| Layer      | File                        | Purpose                                                  |
| ---------- | --------------------------- | -------------------------------------------------------- |
| Controller | `HelloWorldController.java` | Handles requests, processes data, passes results to view |
| View 1     | `helloworld-form.html`      | Input form for user                                      |
| View 2     | `helloworld.html`           | Displays output message                                  |

---

### 🧩 Step-by-step flow

### 🧱 Step 1: When you hit `/showForm`

```java
@RequestMapping("/showForm")
public String showForm(){
    return "helloworld-form";
}
```

✅ This method returns `"helloworld-form"`, so Spring looks for a file named
`helloworld-form.html` in your **templates** folder.
That’s the HTML form that will be shown.

---

### 🧠 Step 2: Inside `helloworld-form.html`

```html
<form th:action="@{/processFormVersionThree}" method="POST">
    <input type="text" name="studentName" placeholder="Whats Your name ?">
    <input type="submit">
</form>
```

➡️ When you press **Submit**, the browser sends an HTTP POST request to
`/processFormVersionThree`.

➡️ It sends the form data as key-value pairs:

```
studentName = whatever_you_typed
```

---

### 🧩 Step 3: How the controller gets that data

In your controller:

```java
@PostMapping("/processFormVersionThree")
public String processFormVersionThree(@RequestParam("studentName") String theName, Model model)
```

Here’s what happens:

1. `@RequestParam("studentName")` looks for a form field or URL parameter named `studentName`.

   * ✅ It matches the `<input name="studentName">` in your form.
   * So the text you typed in the form will be extracted and assigned to the Java variable `theName`.

   Example:

   ```
   You typed "Paras" → theName = "Paras"
   ```

2. You then modify it:

   ```java
   theName = theName.toUpperCase();
   String result = "Yo ! " + theName;
   ```

3. Then you add it to the model:

   ```java
   model.addAttribute("message", result);
   ```

---

### 🎨 Step 4: What is `Model`?

`Model` is an interface provided by Spring to **transfer data** from your **controller** → **view (HTML)**.

You can think of it like a small data bag or map.

When you write:

```java
model.addAttribute("message", result);
```

✅ It puts `message` into that bag.
✅ Thymeleaf can then access it using `${message}`.

So when your method returns:

```java
return "helloworld";
```

→ Spring renders `helloworld.html`
→ Thymeleaf replaces `${message}` with your dynamic data.

---

### 🖥 Step 5: In `helloworld.html`

```html
Student Name : <span th:text="${param.studentName}" /><br>
The Message : <span th:text="${message}"/>
```

* `${param.studentName}` → refers to the **request parameter** directly (from the form or URL).

  * So if you submitted `studentName=Paras`, it prints **Paras**.

* `${message}` → refers to the **model attribute** you set in the controller.

  * It prints **Yo ! PARAS**.

---

### 🔹 Step 6: What about `theData`?

In your `helloworld-form.html`, you wrote:

```html
<p th:text="'time on the server is ' + ${theData}" class="funny"/>
```

➡️ `${theData}` would come from the controller **if you had added it** like this:

```java
model.addAttribute("theData", new Date());
```

But in your current code, you never set it.
So, `theData` will be **null** and nothing will display.

---

### ✅ Summary

| Concept                                          | Meaning                                                                |
| ------------------------------------------------ | ---------------------------------------------------------------------- |
| `@RequestMapping("/showForm")`                   | Opens the form view                                                    |
| `<form th:action="@{/processFormVersionThree}">` | Submits to controller endpoint                                         |
| `@RequestParam("studentName")`                   | Binds form field `name="studentName"` to Java variable                 |
| `Model`                                          | Used to pass data from controller → HTML view                          |
| `${message}`                                     | Accesses model attribute in Thymeleaf                                  |
| `${param.studentName}`                           | Accesses form parameter directly                                       |
| `${theData}`                                     | Not used yet; needs `model.addAttribute("theData", ...)` in controller |

---


## 13) In 06-springboot-spring-mvc > 02-validationDemo tell the overall flow

### High-level overview

* **GET /** → `CustomerController.showForm()` — returns `customer-form.html` with a new `Customer` backing object.
* User fills the form and **POSTs** to `/processForm`.
* Spring **binds** the request params to a `Customer` object (uses `WebDataBinder`).
* `@InitBinder` runs and registers `StringTrimmerEditor`.
* After binding, **validation** runs because the controller method has `@Valid`.
* Any binding or validation errors are captured in `BindingResult`.
* Controller checks `BindingResult.hasErrors()` and either re-renders the form (with error messages) or proceeds to `customer-confirmation.html`.

---

### Detailed timeline (what happens internally)

#### 1) Browser GET request — show form

* Request: `GET /`
* Controller:

  ```java
  @GetMapping("/")
  public String showForm(Model theModel){
      theModel.addAttribute("customer", new Customer());
      return "customer-form";
  }
  ```
* Effect:

  * Adds an empty `Customer` object to the model under the name `"customer"`.
  * Thymeleaf template `customer-form.html` is rendered.
  * Because the template uses `th:object="${customer}"` + `th:field="*{...}"`, Thymeleaf:

    * Generates input `name` attributes that match `Customer` property names (e.g. `name="lastName"`).
    * Pre-populates fields from the `Customer` instance (empty now).

---

#### 2) Form HTML (what matters for binding)

Your form:

```html
<form th:action="@{/processForm}" th:object="${customer}" method="POST">
    First Name: <input type="text" th:field="*{firstName}" />
    Last Name (*): <input type="text" th:field="*{lastName}" />
    Free Passes (*): <input type="text" th:field="*{freePasses}" />
    Postal Code (*): <input type="text" th:field="*{postalCode}" />
    Course Code (*): <input type="text" th:field="*{courseCode}" />
    <input type="submit" value="Submit" />
</form>
```

* `th:field="*{freePasses}"` ⇒ input `name="freePasses"`. On submit the browser sends `freePasses=<string>`.
* `th:object="${customer}"` tells Thymeleaf the form is bound to `Customer`.

---

### 3) POST request arrives — request processing order

When user submits:

1. **DispatcherServlet** receives POST `/processForm`.

2. Spring finds the handler method `processForm(...)`.

3. **Before binding**, Spring calls any `@InitBinder` methods for that controller.

   Your `@InitBinder`:

   ```java
   @InitBinder
   public void initBinder(WebDataBinder dataBinder){
       StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
       dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
   }
   ```

   * This registers a **String editor** that trims leading/trailing whitespace.
   * The `true` flag means: if, after trimming, the String is empty (`""`), convert it to `null`.

4. Spring creates a target `Customer` object and starts **data binding**:

   * Binds each request parameter to the corresponding `Customer` property.
   * Uses registered editors/converters:

     * `StringTrimmerEditor` is applied to **String** fields (e.g. `firstName`, `lastName`, `postalCode`, `courseCode`) — blanks become `null`.
     * For `freePasses` (an `Integer`), Spring tries to convert the incoming String to an `Integer`.

       * If the user left it blank: since `freePasses` is *not a String field*, the `StringTrimmerEditor` **won’t** run for it. Spring will attempt String→Integer conversion and likely create a **type mismatch** error if empty (unless a Number editor allowing empty is registered).

5. **Validation** phase (because controller parameter has `@Valid`):

   * Spring invokes the configured JSR-380 (jakarta.validation) validator.
   * All constraint annotations on `Customer` fields are evaluated.
   * Constraint violations are turned into errors added to `BindingResult`.

6. Controller method is invoked with arguments:

   ```java
   public String processForm(@Valid @ModelAttribute("customer") Customer theCustomer,
                             BindingResult theBindingResult) { ... }
   ```

   * **Important**: `BindingResult` **must** immediately follow the validated model attribute parameter. Otherwise Spring will throw an exception.

---

### Validation and binding details per field

Customer class snapshot with constraints:

```java
private String firstName;

@NotNull(message="is required")
@Size(min=1, message="is required")
private String lastName;

@NotNull(message="is required")
@Min(value = 0, message = "must be ≥ 0")
@Max(value = 10, message = "must be ≤ 10")
private Integer freePasses;

@Pattern(regexp = "^[a-zA-Z0-9]{5}$", message = "must be exactly 5 letters/digits")
private String postalCode;

@CourceCode(value="PARAS", message="Must Start with Paras")
private String courseCode;
```

* `lastName`:

  * You used both `@NotNull` and `@Size(min=1)`.
  * Because `StringTrimmerEditor(true)` converts blank strings to `null`, `@NotNull` will detect blanks.
  * Note: `@Size` by itself does **not** run on `null` values — that's why `@NotNull` is also present.

* `freePasses` (Integer):

  * If user enters a non-integer (or leaves blank), **binding** will produce a type-mismatch error before annotation validation (BindingResult will contain that field error).
  * `@NotNull`, `@Min`, `@Max` are validation constraints — they check the Integer after successful conversion.
  * If you want blank numeric inputs to become `null` without a type error, register a `CustomNumberEditor(Integer.class, true)` in `@InitBinder` (the `true` allows empty → null). Right now, blank numeric inputs are likely to cause conversion errors.

* `postalCode`:

  * `@Pattern` enforces exactly 5 alphanumeric chars. If null, `@Pattern` generally ignores `null` (so use `@NotNull` if you want it required).

* `courseCode`:

  * Custom annotation `@CourceCode(value="PARAS", message="Must Start with Paras")`.
  * The validator class (referenced in the annotation) should implement `ConstraintValidator<CourceCode, String>` and check `value == null ? true : value.startsWith(prefix)`. (Returning `true` for `null` lets `@NotNull` handle emptiness if needed.)

---

### What happens in the controller method

```java
@PostMapping("/processForm")
public String processForm(@Valid @ModelAttribute("customer") Customer theCustomer,
                          BindingResult theBindingResult) {

    if (theBindingResult.hasErrors()) {
        System.out.println("binding " + theBindingResult);
        return "customer-form";
    }
    return "customer-confirmation";
}
```

* If `hasErrors()` is `true`, the method returns the original form view (`customer-form`) — **and** the model includes:

  * The `customer` object (with the user-entered values that successfully bound).
  * The `BindingResult` containing field errors.
* Thymeleaf can read the `BindingResult` to render error messages inline.

---

### How Thymeleaf shows errors

In your form you used this pattern:

```html
<span th:if="${#fields.hasErrors('lastName')}" th:errors="*{lastName}" class="error"></span>
```

* `#fields.hasErrors('lastName')` checks if the `BindingResult` has errors for that field.
* `th:errors="*{lastName}"` renders the validation message(s) from the `BindingResult`.
* Messages come from:

  * the `message` property in the constraint annotation (e.g., `message="is required"`), or
  * from message bundles (`messages.properties`) when you use message keys.

---

### Custom validator (`@CourceCode`) — how it typically works

(You didn’t show the validator implementation; typical structure:)

```java
public class CourseCodeConstraintValidator implements ConstraintValidator<CourceCode, String> {

    private String prefix;

    @Override
    public void initialize(CourceCode constraintAnnotation) {
        this.prefix = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(String code, ConstraintValidatorContext context) {
        // return true for null so @NotNull can handle requiredness separately
        if (code == null) {
            return true;
        }
        return code.startsWith(prefix);
    }
}
```

* The validator reads the annotation’s `value` (prefix) in `initialize`.
* `isValid` returns `true` for `null` so you can compose `@NotNull + @CourceCode` if you need both presence and format checks.

---

### Gotchas & recommendations (practical)

* **`StringTrimmerEditor` only applies to `String` fields.** For numeric fields like `Integer freePasses`, if the user submits blank, Spring will attempt to convert `""` to `Integer` and produce a type mismatch. If you want to accept empty numeric fields and convert them to `null`, register a `CustomNumberEditor` or `NumberFormat` with `allowEmpty=true` in `@InitBinder`.
* **Order of parameters:** `BindingResult` must immediately follow the `@Valid @ModelAttribute(...)` parameter.
* **`@Size` on its own does not fire on `null`.** Use `@NotNull` (or `@NotBlank` for Strings) when you want to require the field.
* **Naming consistency:** your custom annotation is `CourceCode` (typo). Consider renaming to `CourseCode` for clarity; ensure the validator class name in `@Constraint(validatedBy=...)` matches the real class.
* **Custom messages:** put them in `messages.properties` or use the `message` attribute on annotations. Example key in `messages.properties`:
  `CourceCode.message=Must start with PARAS` (if you use message keys instead of literal messages).

---

### Example error scenarios (what the user sees)

1. User leaves **lastName** blank:

   * `StringTrimmerEditor` converts `""` → `null`.
   * `@NotNull` triggers: message `"is required"`.
   * Form returns, and `<span th:errors="*{lastName}">` shows `is required`.

2. User enters `freePasses = 15`:

   * `@Max(10)` triggers: message `"must be ≤ 10"`.

3. User enters `postalCode = "abc"`:

   * `@Pattern` triggers: message `"must be exactly 5 letters/digits"`.

4. User enters `courseCode = "XYZ"` while annotation expects prefix `"PARAS"`:

   * Custom validator triggers and produces `"Must Start with Paras"`.

---

### TL;DR (short summary)

* `@InitBinder` registers editors (your `StringTrimmerEditor`) that run **before** data binding.
* `th:object` + `th:field` in Thymeleaf map form fields to `Customer` properties.
* `@Valid` triggers JSR-380 validation after binding; validation errors + binding errors go into `BindingResult`.
* `BindingResult` must be directly after the `@ModelAttribute` param and is used by Thymeleaf to display errors.
* Blank strings for String fields become `null` because of `StringTrimmerEditor(true)`, which helps `@NotNull` to catch blanks reliably.
* For numeric fields, consider adding a number editor if you want empty → null behavior.

---



