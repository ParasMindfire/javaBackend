Content-Type, Authorization`
# Spring Framework & Spring Boot Complete Guide

A comprehensive guide covering Spring Framework, Spring Boot, JPA/Hibernate, Spring Security, and AOP concepts with practical examples.

## Table of Contents

- [Spring Framework Core](#spring-framework-core)
- [Spring Boot](#spring-boot)
- [JPA and Hibernate](#jpa-and-hibernate)
- [Spring Security](#spring-security)
- [Aspect-Oriented Programming (AOP)](#aspect-oriented-programming-aop)

---

# Spring Framework Core

## 1. Why Spring?

**Inversion of Control (IoC) & Dependency Injection (DI)** let you write modular, testable code: components declare what they need, the framework provides them.

**Basically:**
- Spring Container = IoC Container
- Two main types of Spring Containers:
  - **BeanFactory** → Basic container, lazy-loading, lightweight
  - **ApplicationContext** → Extends BeanFactory → has all features of BeanFactory + additional enterprise features, Advanced container, eager-loading, event propagation, i18n support, etc.

**Key Benefits:**
- **Loose coupling** so swapping implementations (or faking for tests) is easy
- **Lots of infra out of the box** (web, data, security) while you focus on business logic
- **Modern Spring Boot** gives quick bootstrapping and minimal configuration

## 2. Bean

### Definition
A Bean is simply a Java object that is created, initialized, managed, and destroyed by the Spring IoC container instead of you manually doing `new`.

- By default, beans are singletons (one instance shared across the app)
- You declare them with `@Component`, `@Service`, `@Repository`, `@Controller`, or `@Bean`

### Example

```java
@Component
public class PacmanGame implements Game {
    public String play() { return "Playing Pacman!"; }
}
```

Here, `PacmanGame` is a bean managed by Spring.

### Analogy
Think of a bean as a dish cooked by a chef (Spring container). Instead of cooking yourself (`new`), you just order it and the chef (Spring) serves it hot, whenever you need it.

## 3. ApplicationContext

### Definition
The ApplicationContext is the Spring IoC container implementation that:
- Holds all beans
- Knows how to wire them together
- Provides configuration, lifecycle management, and dependency injection

When you start a Spring Boot app, it creates an ApplicationContext behind the scenes.

### Example

```java
@SpringBootApplication
public class GamingApplication {
    public static void main(String[] args) {
        ApplicationContext context =
            SpringApplication.run(GamingApplication.class, args);

        Game game = context.getBean(Game.class);
        System.out.println(game.play());
    }
}
```

### Analogy
Imagine a warehouse (ApplicationContext) where all the prepared dishes (Beans) are stored. Whenever you need something, you ask the warehouse manager (Spring) to hand it to you.

## 4. Component Scan

### Definition
Spring automatically scans the classpath for classes annotated with `@Component`, `@Service`, `@Controller`, or `@Repository` and registers them as beans.

- Triggered by `@SpringBootApplication` or `@ComponentScan`
- It means you don't need to manually list all beans

### Example

```java
@SpringBootApplication // includes @ComponentScan by default
public class GamingApplication { ... }
```

If `PacmanGame` is in the same package (or subpackage), Spring automatically discovers and registers it.

### Analogy
Think of component scan as a radar system — it sweeps through your package and automatically picks up any class marked with `@Component` (or stereotype annotations).

## 5. Dependency Injection (DI)

### Definition
Instead of a class creating its own dependencies using `new`, Spring injects them when the object is created.

### Types of DI

#### Constructor Injection (Preferred)

Dependencies are provided via the class constructor.

**Preferred because:**
- Makes dependencies mandatory (object cannot be created without them)
- Easier to test (use constructor in tests)
- Works well with final fields (immutability)

```java
import org.springframework.stereotype.Component;

interface Game {
    String play();
}

@Component
class PacmanGame implements Game {
    public String play() { return "Playing Pacman!"; }
}

@Component
class GameRunner {
    private final Game game;

    // Constructor Injection
    public GameRunner(Game game) {
        this.game = game;
    }

    public void run() {
        System.out.println(game.play());
    }
}
```

**Usage:** Spring will automatically detect `PacmanGame` and inject it into `GameRunner` when creating the bean.

| Pros | Cons | Best Use |
|------|------|----------|
| Safe, immutable, easy to test | More code if many dependencies | Always prefer |

#### Setter Injection

Dependencies are provided via a setter method. Good when the dependency is optional or can change after object creation. But object can be created without dependency → less safe.

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class ChessGame implements Game {
    public String play() { return "Playing Chess!"; }
}

@Component
class GameRunner {
    private Game game;

    // Setter Injection
    @Autowired
    public void setGame(Game game) {
        this.game = game;
    }

    public void run() {
        System.out.println(game.play());
    }
}
```

**Usage:** Spring creates `GameRunner`, then calls `setGame()` to inject the `ChessGame` bean.

**The Problem with Setter Injection:**

When you use setter injection, Spring can create the object first, and then later call the setter to provide the dependency.

That means:
- At the time the object is constructed, the dependency is null
- If you try to use the dependency before Spring sets it, you'll get a NullPointerException (NPE)

**Example: Setter Injection with Missing Dependency**

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

interface Game {
    String play();
}

@Component
class ChessGame implements Game {
    public String play() { return "Playing Chess!"; }
}

@Component
class GameRunner {
    private Game game;

    // Setter Injection
    @Autowired(required = false)  // dependency is optional
    public void setGame(Game game) {
        this.game = game;
    }

    public void run() {
        if (game == null) {
            System.out.println("No game injected!");
        } else {
            System.out.println(game.play());
        }
    }
}
```

**Case 1: ChessGame Bean Present**
Spring finds `ChessGame`, calls `setGame()`, and everything works:
```
Playing Chess!
```

**Case 2: ChessGame Bean Removed**
If you comment out `@Component` on `ChessGame` (so Spring can't find it), what happens?
- Spring still creates `GameRunner`
- But since no Game bean exists and we marked `required=false`, Spring skips calling `setGame`
- So `game` remains null

Output:
```
No game injected!
```

| Pros | Cons | Best Use |
|------|------|----------|
| Flexible, supports optional deps | Can create object without dependency | Optional dependencies |

#### Field Injection (Not Recommended)

Dependencies are injected directly into fields with `@Autowired`. Simple, fewer lines of code.

**But:**
- Harder to test (no constructor to pass mocks)
- Violates dependency inversion principle because class hides its dependencies

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class TetrisGame implements Game {
    public String play() { return "Playing Tetris!"; }
}

@Component
class GameRunner {
    @Autowired
    private Game game;  // Direct field injection

    public void run() {
        System.out.println(game.play());
    }
}
```

**Usage:** Spring automatically sets `game` after object creation.

| Pros | Cons | Best Use |
|------|------|----------|
| Quick, fewer lines | Hard to test, hidden deps, immutability issues | Prototypes, small demos only |

## 6. Rule of Thumb in Modern Spring Boot

- Use **Constructor Injection** almost always
- Use **Setter Injection** if dependency is optional
- Avoid **Field Injection** (except for quick demos)

## 7. Autowiring

### Definition
Spring automatically injects dependencies into a bean without needing `new`. You can use:
- `@Autowired` on a constructor/field/setter
- Or omit it for constructor injection (Spring Boot does it automatically if there's only one constructor)

### Analogy
Autowiring is like plugging in a charger. You don't solder wires yourself — you just let the socket provide the electricity.

## 8. @Configuration / @Bean

### Definition
While `@Component` marks classes for auto-discovery, sometimes you need manual configuration — like when working with external libraries.

- `@Configuration` marks a class as a source of bean definitions
- `@Bean` defines a bean explicitly inside that config

### Example

```java
@Configuration
public class GameConfig {
    @Bean
    public Game tetrisGame() {
        return new TetrisGame(); // custom bean not discovered by component scan
    }
}
```

Now `tetrisGame` becomes available as a Spring bean.

### Analogy
Think of `@Bean` as a custom order at a restaurant. The chef (Spring) usually cooks from the menu (components), but if you want something special (external library class), you place a manual order via `@Bean`.

### Spring Stereotype Annotations

Spring automatically detects and registers beans if you annotate them with these stereotype annotations:

| Annotation | Where to use | Meaning |
|------------|--------------|---------|
| `@Component` | Generic component class | Basic building block (default) |
| `@Service` | Business logic layer | Marks a class as a service |
| `@Repository` | DAO layer (database access) | Adds persistence exception translation |
| `@Controller` | MVC web controller | Handles web requests (returns views) |
| `@RestController` | REST API controller | `@Controller` + `@ResponseBody` |

## 9. Tight vs Loose Coupling

### Tight Coupling Example (Bad)

#### Why Tight Coupling is Bad

Tight coupling means that classes are hardwired to specific implementations.

**Example:**

```java
class GameRunner {
    MarioGame mario;

    public GameRunner() {
        this.mario = new MarioGame(); // Hardcoded dependency
    }

    public void run() {
        mario.up();
        mario.down();
    }
}
```

Here, `GameRunner` can only run `MarioGame`. If you want to play Pacman, you'd need to edit `GameRunner` and recompile.

**Problems:**
- Breaks Open/Closed Principle (OCP – open for extension, closed for modification)
- Code becomes hard to test because you cannot inject mocks easily
- Maintenance becomes harder as more dependencies are added

### Loose Coupling (Good)

Loose coupling means the class depends on abstractions (interfaces) instead of specific implementations.

**Example:**

```java
interface GamingConsole {
    void up();
    void down();
}

class MarioGame implements GamingConsole {
    public void up() { System.out.println("Jump"); }
    public void down() { System.out.println("Go down into a hole"); }
}

class PacmanGame implements GamingConsole {
    public void up() { System.out.println("Move up"); }
    public void down() { System.out.println("Move down"); }
}

class GameRunner {
    private GamingConsole game;

    public GameRunner(GamingConsole game) { // Constructor Injection
        this.game = game;
    }

    public void run() {
        game.up();
        game.down();
    }
}
```

Now `GameRunner` does not care which game it is — only that it's a `GamingConsole`.

**If both MarioGame and PacmanGame exist, Spring may get confused — you can solve it by:**

**Marking one as `@Primary`**

```java
@Component
@Primary
class MarioGame implements GamingConsole { ... }
```

**Or by using `@Qualifier`**

```java
@Autowired
public GameRunner(@Qualifier("pacmanGame") GamingConsole game) { ... }
```

**Summary:**
- Tight coupling is bad because it reduces flexibility and increases maintenance cost
- Loose coupling is good because you can swap implementations easily, test better, and let Spring manage dependencies

## 10. Maven

Maven is a build automation and dependency management tool for Java projects.

Instead of manually downloading JARs (Java ARchive, A JAR file is basically a compressed archive like a ZIP file that bundles together) (Spring, Hibernate, MySQL driver, etc.), you just declare them in `pom.xml` and Maven fetches them automatically from Maven Central Repository.

It also helps in building, testing, and packaging your application into a JAR or WAR file.

## 11. pom.xml

**Full form** → Project Object Model

This is the config file for Maven.

**Defines:**
- Project metadata (groupId, artifactId, version)
- Dependencies (libraries your app needs)
- Plugins (extra tools like Spring Boot plugin)
- Build settings

## 12. Gradle

Another build and dependency management tool (more modern than Maven).

- Instead of XML, Gradle uses scripts (`build.gradle`) written in Groovy or Kotlin DSL
- Faster builds than Maven (supports incremental builds and caching)
- Flexible and often used in Android + modern Java projects

## 13. WAR

The full form of WAR is **Web Application Archive**

### What is a WAR?

A WAR file is a package used to deploy Java web applications.

Just like a JAR (Java ARchive) bundles normal Java apps, a WAR bundles web apps.

**It contains:**
- Servlets, JSPs, HTML, CSS, JS
- Compiled `.class` files
- Libraries (`.jar` files)

## 14. Servlet

**Servlet = Server + Applet** (A small Java program that runs inside a web browser that is client-side)

A Java class that runs on a web server and handles HTTP requests & responses.

The Servlet container (like Tomcat) manages the servlet. Its lifecycle has 3 main methods:
- `init()` → runs once when servlet is first loaded
- `service()` → runs for every request (handles GET/POST/etc)
- `destroy()` → runs once when servlet is removed or server shuts down

## 15. Design Game Runner to Run Games (Mario, SuperContra, Pacman etc) in an Iterative Approach

### Iteration 1: Tightly Coupled Java Code

- GameRunner class
- Game classes: Mario, SuperContra, Pacman etc

```java
// Game classes
class Mario {
    public void up() { System.out.println("Mario jumps!"); }
}

class SuperContra {
    public void up() { System.out.println("SuperContra jumps!"); }
}

class Pacman {
    public void up() { System.out.println("Pacman moves up!"); }
}

// GameRunner - tightly coupled
class GameRunner {
    private Mario mario;

    public GameRunner() {
        this.mario = new Mario(); // Hard-coded dependency
    }

    public void run() {
        mario.up();
    }
}

// Main
public class Iteration1 {
    public static void main(String[] args) {
        GameRunner runner = new GameRunner();
        runner.run();
    }
}
```

### Iteration 2: Loose Coupling - Interfaces

- GameRunner class
- GamingConsole interface
- Game classes: Mario, SuperContra, Pacman etc

```java
// GamingConsole interface
interface GamingConsole {
    void up();
}

// Game classes implement interface
class Mario implements GamingConsole {
    public void up() { System.out.println("Mario jumps!"); }
}

class SuperContra implements GamingConsole {
    public void up() { System.out.println("SuperContra jumps!"); }
}

class Pacman implements GamingConsole {
    public void up() { System.out.println("Pacman moves up!"); }
}

// GameRunner depends on interface, not implementation
class GameRunner {
    private GamingConsole game;

    public GameRunner(GamingConsole game) {
        this.game = game; // Inject via constructor
    }

    public void run() {
        game.up();
    }
}

// Main
public class Iteration2 {
    public static void main(String[] args) {
        GamingConsole game = new Pacman(); // easily swap game
        GameRunner runner = new GameRunner(game);
        runner.run();
    }
}
```

### Iteration 3: Loose Coupling - Spring Level 1

- Spring Beans
- Spring framework will manage objects and wiring

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

// Interface
interface GamingConsole {
    void up();
}

// Game implementations
@Component
class Mario implements GamingConsole {
    public void up() { System.out.println("Mario jumps!"); }
}

@Component
@Primary
class Pacman implements GamingConsole {
    public void up() { System.out.println("Pacman moves up!"); }
}

// GameRunner
@Component
class GameRunner {
    private GamingConsole game;

    // Constructor injection (recommended)
    @Autowired
    public GameRunner(GamingConsole game) {
        this.game = game;
    }

    public void run() {
        game.up();
    }
}

// Main
public class Iteration4 {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        GameRunner runner = context.getBean(GameRunner.class);
        runner.run();
    }
}
```

You can swap games just by marking another `@Component` with `@Primary` or using `@Qualifier`.

## 16. Difference between JAVA Bean, Spring Bean and POJO

### JavaBean

A JavaBean is a standard Java class that follows certain conventions.

**Features / Rules of JavaBean:**
- Private properties (fields)
- Public getter and setter methods for accessing properties
- A no-argument constructor (default constructor)

### Spring Bean

A Spring Bean is any object that is managed by the Spring IoC container.

**Features / Rules of Spring Bean:**
- Created, configured, and managed by Spring container
- Can have constructor or setter-based dependency injection
- Can have custom lifecycle methods (init-method, destroy-method) managed by Spring

### POJO

A POJO = Plain Old Java Object

It is a simple Java class without special restrictions or framework dependencies.

**Characteristics:**
- Private fields (encapsulation)
- Public getters and setters
- No special inheritance required
- No constraints
- Any Java Object is a POJO!

### Comparison Table

| Feature | Java Bean | Spring Bean |
|---------|-----------|-------------|
| Definition | Standard Java class following conventions | Object managed by Spring container |
| Creation | Created manually via `new` | Created by Spring IoC container |
| Lifecycle Management | None (you manage object) | Fully managed by Spring |
| Dependency Injection | Not supported | Supported (constructor/setter/field) |

## 17. Difference between Autowiring and Dependency Injection

| Feature | Dependency Injection (DI) | Autowiring |
|---------|---------------------------|------------|
| Definition | Pattern to provide dependencies to objects | Spring feature to automatically implement DI |
| Manual vs Automatic | Can be manual (XML, constructor, setter) | Automatic by Spring |
| Scope | Concept / design pattern | Mechanism in Spring |
| Explicit/Implicit | Can be explicit (setter/constructor) | Implicit (Spring finds bean automatically) |
| Example | Passing Game object to GameRunner via constructor | Using `@Autowired` to inject Game automatically |

## 18. Difference between Bean Factory, Application Context and Spring Context

### a) Bean Factory

**Definition:** The BeanFactory is the core IoC container in Spring. It is the simplest container that provides basic dependency injection (DI).

**Key Points:**
- Lazy loading → beans are created only when requested
- Basic functionality → just instantiation, configuration, and DI
- Does not provide advanced features like event handling or internationalization

**Common implementation:**

```java
BeanFactory factory = new XmlBeanFactory(new FileSystemResource("beans.xml"));
MyBean bean = (MyBean) factory.getBean("myBean");
```

**Use case:** Lightweight container for resource-constrained environments.

### b) ApplicationContext

**Definition:** ApplicationContext is a full-featured container in Spring that extends BeanFactory and provides enterprise-level features.

**Key Points:**
- Eager loading → beans are created at startup
- Supports all features of BeanFactory +:
  - Event propagation (ApplicationEvent)
  - Internationalization (i18n)
  - Resource loading (files, classpath)
  - Integration with AOP
- Recommended for modern Spring applications

**Common implementations:**
- ClassPathXmlApplicationContext
- FileSystemXmlApplicationContext
- AnnotationConfigApplicationContext

**Example:**

```java
ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
GameRunner runner = context.getBean(GameRunner.class);
runner.run();
```

### c) Spring Context

**Definition:** Spring Context is another name for ApplicationContext. In practical terms, when people say "Spring Context," they mean ApplicationContext, the container that manages Spring beans and DI.

**Key Points:**
- Provides access to beans
- Manages bean lifecycle
- Provides additional features like events, i18n, resource loading

### Differences Table

| Feature | BeanFactory | ApplicationContext / Spring Context |
|---------|-------------|-------------------------------------|
| Type | Basic IoC container | Advanced IoC container |
| Bean Loading | Lazy (on demand) | Eager (at startup) |
| Features | Basic DI | DI + Events + i18n + Resource loading + AOP |
| Recommended for | Lightweight, legacy apps | Modern Spring applications |
| Typical Implementations | XmlBeanFactory | ClassPathXmlApplicationContext, AnnotationConfigApplicationContext |

## 19. How Can I List All Beans Managed by Spring Framework?

```java
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ListBeansExample {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        // Get all bean names
        String[] beanNames = context.getBeanDefinitionNames();

        // Print bean names
        for (String name : beanNames) {
            System.out.println(name);
        }
    }
}
```

## 20. Spring is Managing Objects and Performing Auto-wiring. BUT Aren't We Writing the Code to Create Objects? How Do We Get Spring to Create Objects for Us?

We don't manually create objects with `new` in Spring. Instead, we mark classes as beans (using `@Component`, `@Bean`, or XML config), and the Spring container automatically creates and manages these objects and injects dependencies via autowiring. This enables loose coupling and lifecycle management.

## 21. What is Internationalization (i18n)?

Internationalization is the process of designing an application so that it can support multiple languages and regions without changing the code.

Often abbreviated as **i18n** → 18 letters between i and n.

## 22. When to Use Qualifier, Primary and Both

### @Primary

**Use case:** When multiple beans of the same type exist, and you want one default bean to be injected automatically.

Spring will prefer the bean marked with `@Primary` if no `@Qualifier` is specified.

### @Qualifier

**Use case:** When multiple beans of the same type exist, and you want to inject a specific bean explicitly.

### Using Both @Primary + @Qualifier

**Scenario:** You can mark a bean as primary and still use `@Qualifier` elsewhere for specific injection.

**Important:** `@Qualifier` always wins over `@Primary`.

If a bean is marked `@Primary` and another bean is selected using `@Qualifier`, Spring will inject the bean specified by `@Qualifier`, ignoring the primary.

## 23. Difference between @Component and @Bean

| Feature | @Component | @Bean |
|---------|------------|-------|
| Applied on | Class | Method inside @Configuration class |
| Purpose | Auto-detect via component scan | Explicitly define how to create bean |
| Use Case | Your own classes | Third-party classes or custom creation logic |
| Requires @Configuration? | No | Yes, must be inside @Configuration |
| Ease of use | Very easy. Just add an annotation. | You write all the code. |
| Who creates beans? | Spring Framework | You write bean creation code |

## 24. What Will Happen If We Don't Have Spring

### Real-world Applications vs GameRunner Example

**GameRunner App:** Very simple, only a few classes like GameRunner, Mario, Pacman.

**Real-world apps:**
- Multiple layers: Web, Business, Data/Repository, etc.
- Thousands of classes and dependencies

**Example:**
- BusinessService depends on DataRepository
- Controller depends on BusinessService

Manually creating objects with `new` and wiring them becomes complex and error-prone. You would have to manually create all objects in the correct order and pass dependencies:

```java
DataRepository repo = new DataRepository();
BusinessService service = new BusinessService(repo);
Controller controller = new Controller(service);
```

With thousands of classes, this is unmanageable.

**With Spring:**
```
Controller -> BusinessCalculationService -> DataRepository
Spring container creates all objects and injects dependencies
You just focus on methods like calculate(), saveData()
```

## 25. Difference between Lazy and Eager Initialization

### Eager Initialization

**Definition:** Spring creates the bean as soon as the container starts. It is Default behavior for singleton beans. Very Frequently used.

**Pros:**
- Bean is ready when needed
- Early detection of configuration errors

**Cons:**
- Slower startup if there are many beans
- Resources are used even if the bean is never needed

### Lazy Initialization

**Definition:** Spring creates the bean only when it is first requested. Use `@Lazy` annotation or XML configuration. Rarely Used.

**Pros:**
- Faster startup
- Resources used only when needed

**Cons:**
- Bean creation happens at runtime → possible runtime errors if misconfigured

## 26. Bean Scope

Bean scope defines how Spring creates and shares beans. It controls the lifecycle and number of instances of a bean in the Spring container.

### a) Singleton Scope (Default)

**Definition:** Only one instance of the bean is created per Spring IoC container.

**Usage:** Default scope.

**Characteristics:**
- Same instance shared across the application
- Bean is eagerly initialized by default (unless `@Lazy` is used)
- Good for stateless beans

```java
@Component
@Scope("singleton")
public class SingletonBean { }
```

**Example:**

```java
SingletonBean b1 = context.getBean(SingletonBean.class);
SingletonBean b2 = context.getBean(SingletonBean.class);

System.out.println(b1 == b2); // true, same instance
```

### b) Prototype Scope

**Definition:** A new instance is created every time the bean is requested.

**Characteristics:**
- Not shared, multiple instances exist
- Spring only manages creation and dependency injection, not the full lifecycle (no destruction callback)
- Useful for stateful beans or beans that need unique instances

```java
@Component
@Scope("prototype")
public class PrototypeBean { }
```

**Example:**

```java
PrototypeBean b1 = context.getBean(PrototypeBean.class);
PrototypeBean b2 = context.getBean(PrototypeBean.class);

System.out.println(b1 == b2); // false, different instances
```

## 27. Difference between Spring Singleton and Java Singleton

- **Spring Singleton:** One object instance per Spring IoC container
- **Java Singleton (GOF):** One object instance per JVM

## 28. Specializations of @Component

- **@Service** - Indicates that an annotated class has business logic
- **@Controller** - Indicates that an annotated class is a "Controller" (e.g. a web controller). Used to define controllers in your web applications and REST API
- **@Repository** - Indicates that an annotated class is used to retrieve and/or manipulate data in a database

## 29. Post Construct and Pre Destroy

- **@PostConstruct** Identifies the method that will be executed after dependency injection is done to perform any initialization
- **@PreDestroy** Identifies the method that will receive the callback notification to signal that the instance is in the process of being removed by the container. Typically used to release resources that it has been holding.

## 30. Why is Spring Ecosystem Popular?

- **Loose Coupling:** Spring manages creation and wiring of beans and dependencies
  - Makes it easy to build loosely coupled applications
  - Make writing unit tests easy! (Spring Unit Testing)
- **Reduced Boilerplate Code:** Focus on Business Logic
  - Example: No need for exception handling in each method!
  - All Checked Exceptions are converted to Runtime or Unchecked Exceptions
- **Architectural Flexibility:** Spring Modules and Projects
  - You can pick and choose which ones to use (You DON'T need to use all of them!)
- **Evolution with Time:** Microservices and Cloud
  - Spring Boot, Spring Cloud etc

---

# Spring Boot

## 1. Why Spring Boot

Spring Framework is very powerful, but setting it up is painful.

**You need to:**
- Write long XML configurations (or Java @Configuration classes)
- Configure DispatcherServlet for MVC apps
- Add dependencies manually (JPA, Hibernate, JDBC, Tomcat, Jackson, etc.)
- Manage version conflicts between libraries
- Deploy apps manually on a Tomcat server (WAR file)

So, even for a simple "Hello World REST API", you'd spend hours setting things up before writing actual code. 

Spring Boot was introduced to make Spring development faster and easier. It removes boilerplate configuration and provides a ready-to-use environment.

## 2. Key Features of Spring Boot

### a) Auto-Configuration

Spring Boot automatically configures beans based on the dependencies in your project.

**Example:**
If `spring-boot-starter-web` is on the classpath, Spring Boot automatically configures:
- Embedded Tomcat server
- DispatcherServlet
- JSON support via Jackson

You don't have to configure these manually.

### b) Starter Dependencies

Boot provides starter packs — pre-defined sets of dependencies.

**Examples:**
- `spring-boot-starter-web` → Web + Tomcat + Jackson
- `spring-boot-starter-data-jpa` → Hibernate + JPA + HikariCP
- `spring-boot-starter-security` → Spring Security dependencies

No need to manually search for compatible versions.

### c) Embedded Servers

Spring Boot ships with embedded web servers like Tomcat, Jetty, Undertow. You run your app using `main()` — no WAR deployment needed.

Just run:
```bash
mvn spring-boot:run
```

**Normally, in traditional Java web apps (without Spring Boot):**
- You build a WAR file
- Deploy it manually on an external server (Tomcat, Jetty, WebSphere, etc.)
- Start the server separately
- So your app is dependent on an external web server to run

**With Spring Boot (Embedded Server):**
Spring Boot includes a web server inside your application JAR.

**Common embedded servers:**
- Tomcat (default)
- Jetty
- Undertow

That means your application is self-contained: You don't need to install Tomcat separately.

### d) Production-Ready Features

Spring Boot Actuator provides monitoring endpoints out-of-the-box:
- `/actuator/health` – Health check
- `/actuator/metrics` – App metrics
- `/actuator/env` – Environment details

Makes it easy to monitor apps in production.

### e) Spring Initializer

Boot provides Spring Initializer: A web tool to quickly generate a Spring Boot project with dependencies. You pick what you need → it gives you a ready-to-run project.

No need to start from scratch.

## 3. Difference between Starter Dependency and Maven

### Maven

- Fetches and manages dependencies
- Builds and packages your project
- Maven = Amazon delivery service. You tell it what products (dependencies) to get, it fetches them.

### Spring Boot Starters

- Provide pre-curated dependency lists (so you don't need to manually declare dozens of JARs)
- Ensure compatible versions (via Spring Boot BOM)
- Reduce boilerplate in pom.xml
- Spring Boot Starter = Ready-made shopping list. Instead of writing down 20 individual items, you just say "Give me a Spring Web Starter Pack" and it includes everything needed.

**Maven (or Gradle) still downloads and manages dependencies. Spring Boot just makes it easier by providing starter packs (predefined dependency lists with tested versions) so you don't have to manually add and manage each dependency yourself.**

## 4. Goals of Spring Boot

### Goals of Spring Boot

**a) Simplify Spring Development**
- Remove complex XML/Java configuration

**b) Auto-Configuration**
- Configure beans automatically based on dependencies in the classpath

**c) Opinionated Defaults**
- Provide sensible defaults for project setup (e.g., port 8080, JSON via Jackson, H2 database for dev)

**d) Production Ready**
- Built-in monitoring (Actuator), metrics, and health checks

**e) Easy Deployment**
- Ships with embedded servers (Tomcat, Jetty, Undertow). Run apps as standalone JARs

**f) Rapid Development**
- Spring Initializr + starter dependencies = quick project bootstrapping

## 5. How Does Spring Boot Work?

Spring Boot works by combining:

### a) Starter Dependencies
Instead of adding 10 different dependencies, you add one starter (`spring-boot-starter-web`) which pulls in all required libraries.

### b) Auto-Configuration
Boot looks at the classpath and configures beans automatically.

**Example:**
- If `spring-boot-starter-data-jpa` is present → Boot configures Hibernate + DataSource
- If `spring-boot-starter-web` is present → Boot configures Tomcat + DispatcherServlet

### c) Embedded Server
Spring Boot apps come with Tomcat/Jetty embedded. Run apps with `java -jar app.jar`, no external server needed.

### d) Convention over Configuration
Sensible defaults (port 8080, JSON serialization, etc.). You only override settings in `application.properties` or `application.yml`.

### e) @SpringBootApplication
A meta-annotation that combines:
- `@Configuration` → declares beans
- `@EnableAutoConfiguration` → enables auto-config
- `@ComponentScan` → scans packages for beans

## 6. Compare Spring vs Spring MVC vs Spring Boot

| Feature | Spring (Core) | Spring MVC | Spring Boot |
|---------|---------------|------------|-------------|
| Purpose | Foundation: IoC, DI, Bean Management | Web framework (MVC pattern) | Rapid development with auto-config & starters |
| Setup | Manual XML/Java config | Requires config for DispatcherServlet, View | Minimal setup (auto-config, Initializr) |
| Dependencies | Add manually, manage versions | Add manually (Spring MVC, Jackson, etc.) | Starter dependencies (e.g., spring-boot-starter-web) |
| Server | External (Tomcat, Jetty) | External (Tomcat, Jetty) | Embedded server (Tomcat default) |
| Configuration Style | XML or Java | Java + XML | Convention over configuration |
| Focus | Core features (IoC, DI) | Web apps (controllers, views, REST) | Full-stack apps (web, data, cloud, monitoring) |
| Deployment | Build WAR, deploy manually | Build WAR, deploy manually | Run as standalone JAR |
| Production Features | None | None | Built-in Actuator, metrics, health checks |

## 7. Problem with Traditional Method and How to Overcome Using Spring Boot

### Traditional Method Problems

In a plain Spring app (or Spring MVC app), you have to configure everything yourself:
- **Component Scan** → tell Spring where to find beans with `@Component`, `@Service`, etc.
- **DispatcherServlet** → central servlet that handles web requests
- **Data Sources** → define database connection (URL, username, password, pool)
- **JSON Conversion** → add Jackson library and configure converters to handle Object ↔ JSON
- **ViewResolvers / Error Pages** → define how errors are displayed

This makes projects tedious and verbose.

### What Spring Boot Does: Auto-Configuration

Spring Boot says: "Instead of making you configure everything, I'll look at what's in your project, and automatically set it up with sensible defaults."

**How it works:**

**a) Checks Classpath**
- If you added `spring-boot-starter-web`, Boot knows you want to build a web app
- It sees libraries like `spring-webmvc` and `jackson-databind` → so it configures a DispatcherServlet and JSON converters automatically

**b) Checks Existing Config**
- If you already defined a DataSource, Boot won't create another
- If you don't, Boot creates one (H2 in-memory DB for dev)

**c) Loads AutoConfiguration Classes**
These are pre-built classes inside Spring Boot JARs that configure things for you.

**Example:**
- `DispatcherServletAutoConfiguration` → registers DispatcherServlet
- `ErrorMvcAutoConfiguration` → sets up default error pages
- `JacksonHttpMessageConvertersConfiguration` → sets up JSON conversion
- `EmbeddedWebServerFactoryCustomizerAutoConfiguration` → sets up Tomcat (default), or Jetty/Undertow if you include them

## 8. Questions

**Who is launching the Spring Context?**
**Who is triggering the component scan?**
**Who is enabling auto configuration?**

**Answer: @SpringBootApplication**

## 9. @SpringBootConfiguration

Special type of `@Configuration`. Marks this class as the source of bean definitions. Equivalent to telling Spring: "Hey, you can start building the ApplicationContext from here."

## 10. @EnableAutoConfiguration

This is the magic of Spring Boot. Tells Spring Boot: "Look at the classpath, figure out what libraries are present (Tomcat, JPA, Jackson, etc.), and automatically configure beans for them if not already configured."

## 11. @ComponentScan

Triggers the component scanning mechanism. By default, it scans the package of the main application class and all its subpackages. Finds classes annotated with `@Component`, `@Service`, `@Repository`, `@Controller`, etc. Registers them as beans in the ApplicationContext.

## 12. Managing App Configuration Using Profiles

Applications have different environments: Dev, QA, Stage, Prod. Different environments need different configuration, Different Databases, Different Web Services.

We can provide different configuration for different environments using **Profiles:** Environment specific configuration

```java
@Configuration
public class DataSourceConfig {

    @Bean
    @Profile("dev")
    public DataSource devDataSource() {
        return new HikariDataSource("jdbc:h2:mem:devdb");
    }

    @Bean
    @Profile("prod")
    public DataSource prodDataSource() {
        return new HikariDataSource("jdbc:mysql://prod-server/mydb");
    }
}
```

And we can define externalized configuration for application using **ConfigurationProperties:** Define externalized configuration

```properties
app.datasource.url=jdbc:h2:mem:devdb
app.datasource.username=devuser
```

or

```properties
app.datasource.url=jdbc:mysql://prod-server/mydb
app.datasource.username=produser
```

## 13. What is Spring Boot Actuator?

It's a module in Spring Boot that gives you monitoring & management endpoints for your application. Instead of writing your own health checks, metrics collectors, or bean inspectors, Actuator gives them out-of-the-box.

In production, you don't just need your app to run; you also need to:
- Check if it's healthy (DB connections working? service alive?)
- Monitor performance metrics (CPU, memory, HTTP requests)
- Debug which endpoints are mapped (what REST APIs exist)
- Track beans and dependencies Spring has wired

**Common Endpoints:**

- `/actuator/beans` → Lists all beans managed by Spring (helps debug wiring issues)
- `/actuator/health` → Shows app health (UP/DOWN). Can include DB, disk space, message queues
- `/actuator/metrics` → Shows app performance metrics (JVM memory, CPU, HTTP requests, GC, etc.)
- `/actuator/mappings` → Shows all request mappings (which controllers handle which URLs)
- `/actuator/env` → Environment properties
- `/actuator/loggers` → View/change log levels at runtime
- `/actuator/threaddump` → Debug thread state

**Note:** Only `/health` and `/info` are public. Other endpoints require security (Spring Security).

---

# JPA and Hibernate

## 1. World Before JPA — JDBC & Spring JDBC

### Raw JDBC (java.sql)

You write SQL strings, Connection, PreparedStatement, ResultSet handling. 

**Problems:**
- Boilerplate: open/close resources, handle SQLExceptions, map rows to objects manually
- Hard to maintain, easy to leak resources, verbose for CRUD + relationships

### Spring JDBC (JdbcTemplate)

Removes most boilerplate (resource management, exception translation). You still write SQL and manually map rows to objects (RowMapper) or use BeanPropertyRowMapper.

Good for simple queries or high-performance, SQL-centric code — but still not an ORM.

### Why JPA?

JPA = Java Persistence API (a standard/spec), not an implementation. 

**Goals / Benefits:**
- **Object–Relational Mapping (ORM):** map Java objects to DB tables and back automatically
- **Less boilerplate:** CRUD, dirty checking, cascading saves, relationships are handled
- **Persistence context** (first-level cache) and lifecycle management — performance and consistency benefits
- **JPQL:** object-oriented query language (queries against entities, not tables)
- **Portability:** same API across providers (Hibernate, EclipseLink, etc.)

**When JPA helps:** apps with domain objects and relational mapping, focusing on business logic rather than SQL plumbing.

### Why Hibernate?

Hibernate is the most widely used JPA provider (implementation). 

**Adds features beyond JPA spec:**
- HQL (Hibernate Query Language) and Criteria API (older Hibernate-specific API)
- Stronger caching support (2nd-level cache providers), batch fetching optimizations, advanced mappings
- Maturity, community, documentation, performance tuning options

### JPA vs Hibernate

- **JPA** = spec/interface you program against
- **Hibernate** = concrete library that implements JPA + extra features

You should code to JPA interfaces (EntityManager / javax.persistence annotations) so you remain portable, but you can use Hibernate features if needed.

## 2. @Entity

Marks a POJO as a JPA-managed entity (persistent). Must have a no-arg constructor (can be protected). Optional: `@Table(name="...")` to set DB table name.

```java
@Entity
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String username;

  // getters/setters
}
```

**`@Column` attributes you commonly use:** name, nullable, length, unique, insertable, updatable, columnDefinition.

- `@Temporal(TemporalType.DATE|TIME|TIMESTAMP)` for Date (older JPA)
- `@Lob` for large text/BLOB
- Enums: `@Enumerated(EnumType.STRING)` (recommended) or ORDINAL

## 3. @Id

`@Id` marks PK. Strategies for generating keys via `@GeneratedValue`:
- **AUTO** — provider picks strategy
- **IDENTITY** — DB auto-increment
- **SEQUENCE** — DB sequence (preferred for performance in many DBs)
- **TABLE** — table-based (rare)

## 4. Relationships & Mappings (the Big One)

### General Rules

**a) Always decide owning side (persists FK) vs inverse side (mappedBy)**

**b) Use `mappedBy` on the inverse side to avoid duplicate FK management**

When you create a bidirectional relationship (e.g., Customer ↔ Order), two entities point to each other. Without `mappedBy`, Hibernate thinks both sides are "owners" → it may try to manage the foreign key twice or even create an extra join table, which is wrong.

**Example without `mappedBy`:**

```java
@Entity
class Customer {
    @OneToMany // no mappedBy
    private List<Order> orders;
}
```

Hibernate might:
- Create an unnecessary join table `customer_orders`
- Or get confused about who manages the `customer_id` FK

**Fix:**
Use `mappedBy = "customer"` in Customer, so Hibernate knows: Order is the owning side (has the FK). Customer is the inverse side (just a mirror).

```java
@Entity
class Customer {
    @OneToMany(mappedBy = "customer") // inverse side
    private List<Order> orders;
}
```

**Result:** Only one FK (`customer_id` in orders table). No duplication.

**c) Prefer LAZY fetching for collections**

When you load a Customer, should Hibernate immediately load all Orders?
- **EAGER** (default for `@ManyToOne`) → loads related data immediately
- **LAZY** (default for `@OneToMany`) → loads data only when needed

**Why prefer LAZY for collections?**

Customers might have thousands of Orders. If you load 1 Customer, and Hibernate fetches all Orders eagerly, it's a performance disaster.

```java
@Entity
class Customer {
    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private List<Order> orders;
}
```

### @ManyToOne (Most Common)

Many children → One parent.
Owning side = child.
FK stored in child table.

```java
@Entity
class Order {
  @Id @GeneratedValue Long id;

  @ManyToOne
  @JoinColumn(name = "customer_id") // FK
  private Customer customer;
}
```

**Example:** Many orders belong to one customer.

### @OneToMany

One parent → Many children.
Usually inverse side with `mappedBy`. Works with `@ManyToOne` on child.

```java
@Entity
class Customer {
  @Id @GeneratedValue Long id;

  @OneToMany(mappedBy = "customer",
             cascade = CascadeType.ALL,
             orphanRemoval = true)
  private List<Order> orders = new ArrayList<>();
}
```

**Example:** A customer has many orders.

**Best practice:** Always pair with `@ManyToOne`.

### @OneToOne

One entity → One entity. Owning side has the FK.

```java
@Entity
class User {
  @Id @GeneratedValue Long id;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "profile_id") // FK
  private Profile profile;
}
```

**Example:** One user has exactly one profile.

### @ManyToMany

Many ↔ Many relationship.
Join table used.

```java
@Entity
class Student {
  @Id @GeneratedValue Long id;

  @ManyToMany
  @JoinTable(
    name = "student_course",
    joinColumns = @JoinColumn(name="student_id"),
    inverseJoinColumns = @JoinColumn(name="course_id")
  )
  private Set<Course> courses = new HashSet<>();
}
```

**Example:** Students can enroll in many courses; courses can have many students.

## 5. Cascade Types in JPA

### a) CascadeType.PERSIST

When you save (persist) a parent entity, the child entities are also automatically saved. 

**Example:** If you create a Customer with Orders and persist the Customer, you don't need to separately persist the Orders. 

**Use case:** When children always come with the parent at creation.

### b) CascadeType.MERGE

When you update (merge) a parent entity, the children are also updated. 

**Example:** You update a Customer's name and also update an Order's status. If you merge the Customer, the updated Order will also be merged automatically. 

**Use case:** Ensures parent and children stay in sync during updates.

### c) CascadeType.REMOVE

When you delete a parent entity, the children are also deleted. 

**Example:** If you delete a Customer, all their Orders will be deleted from the DB.

**Be very careful** — sometimes you don't want to lose child data. (e.g., If an Order must be kept for auditing, don't use REMOVE.)

### d) CascadeType.REFRESH

If the parent entity is refreshed (reloaded from the DB), children are also refreshed. 

**Example:** You reload a Customer from the DB; their Orders list is also reloaded.

**Use case:** Rare, but useful when you need to discard local changes and re-sync with DB.

### e) CascadeType.DETACH

When the parent is detached from the persistence context (no longer tracked), children are also detached. 

**Example:** You detach a Customer → Hibernate stops tracking the Customer and its Orders. 

**Use case:** Rare, often in advanced scenarios (like clearing cache).

### f) CascadeType.ALL

Shortcut for ALL of the above. Persist, Merge, Remove, Refresh, Detach → all applied to children when done on the parent. Convenient, but risky — especially with REMOVE.

## 6. @Embeddable & @Embedded

Think of them as a way to reuse a value object (like an Address, Name, Money, etc.) inside an entity.

**Example:**

```java
@Embeddable
class Address {
    private String street;
    private String city;
    private String zipcode;
}

@Entity
class User {
    @Id @GeneratedValue
    private Long id;

    private String name;

    @Embedded   // tells Hibernate: put Address fields in User's table
    private Address address;
}
```

**What happens?** There is no separate table for Address. Instead, User table will have columns like:
```
id | name | street | city | zipcode
```

Address is stored inside User → embedded.

## 7. @MappedSuperclass

This is about reusing mappings across entities.

**Example:**

```java
@MappedSuperclass
abstract class BaseEntity {
    @Id @GeneratedValue
    private Long id;

    private Instant createdAt;
}

@Entity
class User extends BaseEntity {
    private String name;
}

@Entity
class Product extends BaseEntity {
    private String title;
}
```

**What happens?** BaseEntity is not an entity itself → no `base_entity` table created. But its fields (`id`, `createdAt`) get mapped into the child tables.

So:
- User table → id, createdAt, name
- Product table → id, createdAt, title

**Use case:** When multiple entities share common fields (like id, createdAt, updatedAt), but you don't want a separate parent table.

## 8. Difference Between @Embeddable and @MappedSuperclass

| Feature | @Embeddable + @Embedded | @MappedSuperclass |
|---------|-------------------------|-------------------|
| Purpose | Reuse value-type objects inside entity | Reuse entity attributes across entities |
| Table | No separate table | No separate table |
| Example | Address inside User | BaseEntity (id, timestamps) extended by entities |
| Usage | Composition ("has-a" relationship) | Inheritance ("is-a" relationship) |
| Annotation on class | @Embeddable | @MappedSuperclass |

**Summary:**
- `@Embeddable` is like putting an object inside another object (composition)
- `@MappedSuperclass` is like inheriting properties from a parent class (inheritance)

## 9. Persistence Context

Think of it as a container (in-memory map) where JPA keeps track of all entities you are currently working with. It is **first-level cache** → means:

If you fetch an entity by ID twice in the same transaction, JPA will return the same object from the cache, not run another SQL. Helps reduce database hits.

**Example:**

```java
User u1 = em.find(User.class, 1L); // SQL runs, loads user
User u2 = em.find(User.class, 1L); // No SQL, returned from cache
System.out.println(u1 == u2); // true (same object from persistence context)
```

## 10. EntityManager

Main JPA interface to interact with the persistence context.

**It handles:**
- Finding entities (find, getReference)
- Persisting new entities (persist)
- Updating entities (merge)
- Removing entities (remove)
- Running queries (createQuery)

Think of it as the "remote control" for the persistence context.

## 11. EntityManagerFactory

A heavy-weight object that creates EntityManagers. Usually one per database in your app.

In Spring Boot, you don't worry about it — Spring manages it for you.

### How Spring Helps

Normally in plain JPA you'd write:

```java
EntityManagerFactory emf = Persistence.createEntityManagerFactory("myPU");
EntityManager em = emf.createEntityManager();
```

But with Spring:
You just write:

```java
@PersistenceContext
private EntityManager em;
```

or even simpler → use Spring Data JPA (JpaRepository) which injects EntityManager behind the scenes.

### Summary

- **Persistence Context** = memory area (1st level cache)
- **EntityManager** = API to talk to persistence context
- **EntityManagerFactory** = factory to create EntityManagers
- **Spring Boot** auto-manages all of this for you

## 12. Entity Lifecycle States

Every entity in JPA can be in one of these states:

### a) Transient

Newly created object, not known to JPA/persistence context, not in DB. 

**Example:** 
```java
User user = new User("Paras"); // transient
```

If app ends now → it's lost.

### b) Managed (Persistent)

Once you call `em.persist(user)` (inside a transaction), it becomes managed. JPA tracks it in the Persistence Context. Any changes are automatically updated in DB at commit (dirty checking).

**Example:**

```java
em.persist(user); // now managed
user.setName("Paras Singh"); // change tracked
```

No need to call update manually → JPA does it.

### c) Detached

Entity was managed before, but persistence context is closed (transaction ended, session closed, or you manually detach). Now JPA does not track changes anymore.

**Example:**

```java
em.detach(user); // manually detached
user.setName("New Name"); // will NOT be updated in DB
```

### d) Removed

Entity is marked for deletion using `em.remove(entity)`. SQL DELETE will be executed at commit.

**Example:**

```java
em.remove(user); // scheduled for deletion
```

**Flow:** Transient → Managed → Detached/Removed

## 13. Transactions in JPA

JPA requires transactions for all write operations (insert, update, delete). For read-only, some databases allow queries without transactions, but best practice is still to use them. In Spring, you typically mark service methods with `@Transactional`.

**Example:**

```java
@Service
public class UserService {

  @Transactional
  public void saveUser(User user) {
      em.persist(user); // transaction opened and committed automatically
  }
}
```

## 14. Difference between JDBC, Spring JDBC, JPA, and Spring Data JPA

| Feature | JDBC | Spring JDBC | JPA | Spring Data JPA |
|---------|------|-------------|-----|-----------------|
| Definition | Standard Java API to connect and execute SQL on databases | Spring abstraction over JDBC to reduce boilerplate | Java specification for ORM (Object-Relational Mapping) | Spring framework extension of JPA for repositories and easier data access |
| Level | Low-level | Low to mid-level | High-level | High-level, abstraction over JPA |
| Boilerplate Code | High: need to handle connections, statements, result sets manually | Reduced: JdbcTemplate handles resource management and exceptions | Minimal: maps entities to DB tables, ORM handles SQL generation | Very low: auto-generates queries CRUD methods via interfaces |
| Object Mapping | Manual mapping from ResultSet to objects | Partial support: RowMapper / BeanPropertyRowMapper | Automatic mapping: entities mapped to tables using annotations | Full automatic mapping using JPA entities and Spring Data repository methods |
| Query Language | SQL only | SQL only | JPQL (object-oriented) + SQL | JPQL + derived queries, method names, and @Query |
| Transaction Management | Manual or via JDBC APIs | Can use Spring @Transactional | Integrated with JPA EntityManager | Integrated with JPA + Spring @Transactional |
| Performance | Fast, direct access | Slight overhead due to Spring wrapper | Slightly slower than JDBC due to ORM abstraction | Similar to JPA, slight overhead for repository abstraction |
| Use Case | Simple or legacy apps, raw SQL needed | Apps needing simplified JDBC with Spring features | ORM-based apps, entity relationships, object-oriented DB handling | Modern Spring Boot apps, rapid CRUD development, minimal boilerplate |

## 15. Create COURSE Table: Use Spring JDBC, JPA/Hibernate, and Spring Data JPA to Play with COURSE Table

### a) Spring JDBC Example

```java
public class Course {
    private Long id;
    private String name;
    private String author;
    // getters and setters
}
```

```java
@Repository
public class CourseJdbcRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void insert(Course c) {
        String sql = "INSERT INTO COURSE(name, author) VALUES (?, ?)";
        jdbcTemplate.update(sql, c.getName(), c.getAuthor());
    }

    public List<Course> findAll() {
        return jdbcTemplate.query(
            "SELECT * FROM COURSE",
            (rs, rowNum) -> {
                Course c = new Course();
                c.setId(rs.getLong("id"));
                c.setName(rs.getString("name"));
                c.setAuthor(rs.getString("author"));
                return c;
            });
    }
}
```

### b) JPA + Hibernate Example

```java
@Entity
@Table(name="COURSE")
public class Course {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String author;
    // getters and setters
}
```

```java
@Repository
public class CourseJpaRepository {
    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void insert(Course c) {
        em.persist(c);
    }

    public List<Course> findAll() {
        return em.createQuery("SELECT c FROM Course c", Course.class)
                 .getResultList();
    }
}
```

### c) Spring Data JPA Example

```java
@Repository
public interface CourseSpringDataRepository extends JpaRepository<Course, Long> {
    // no need to write implementation
    List<Course> findByAuthor(String author);
}
```

---

# Spring Security

## 1. System Components: Resources & Identities

Before diving into Spring Security, understand the basics of any secure system:

### Resources
Things you want to protect:
- REST APIs
- Web Applications
- Databases
- Cloud resources (S3 buckets, compute instances, etc.)

### Identities
The users or systems accessing these resources:
- Humans (users)
- Systems (service accounts, microservices)

### Key Actions

- **Authentication** → Are you who you claim to be?
- **Authorization** → Can you do what you are trying to do?

## 2. Security Principle Breakdown

**A chain is only as strong as its weakest link.** No matter how strong your architecture is, one flaw can compromise everything.

**Example:**
Strong password encryption but weak API token validation → attacker can still exploit it. Always look at the whole system.

## 3. 6 Principles of Building Secure Systems

### a) Trust Nothing

Never trust input from users or external systems. Always validate every request and every piece of data.

Spring Security helps by: Filtering requests, CSRF protection, Input validation

### b) Assign Least Privileges

Give only necessary permissions, no more. **Principle of Minimum Privilege:**
- Database → read-only for reporting user
- App → user can only update their own profile

Spring Security uses roles and authorities to implement this.

### c) Have Complete Mediation

Every access request must be checked against permissions. 

**Analogy:** Medieval fort → everyone passes through one main gate, and guards check access. 

All requests go through the Security Filter Chain. No bypass allowed.

### d) Defense in Depth

Multiple layers of security → even if one fails, others protect the system.

**Layers:**
- Transport → HTTPS
- Network → firewalls
- Infrastructure → server hardening
- Application → authentication & authorization

**Example:**
Even if someone gets DB credentials, the firewall and encrypted communication protect the data.

### e) Economy of Mechanism

Keep security simple. Complex systems are harder to secure and maintain.

**Example:**
- Don't reinvent encryption → use standard libraries like Spring Security Crypto
- Avoid overly complex role hierarchies if possible

### f) Openness of Design

Security should not rely on secrecy of design (security through obscurity is weak). Open designs allow: Experts to review. Bugs to be found and fixed.

Spring Security is open-source → widely used, tested, and reviewed.

## Summary Table

| Concept/Principle | What It Means | Example / Spring Security |
|-------------------|---------------|---------------------------|
| Authentication | Verify identity | Login with password, JWT |
| Authorization | Check permissions | @PreAuthorize("hasRole('ADMIN')") |
| Trust Nothing | Validate all input | CSRF protection, input validation |
| Least Privilege | Minimal access | User can only read their data |
| Complete Mediation | Check all requests | Security filter chain |
| Defense in Depth | Multiple layers | HTTPS + firewall + app auth |
| Economy of Mechanism | Keep it simple | Use standard libraries |
| Openness of Design | Not security through obscurity | Open-source frameworks like Spring Security |

## 4. How Spring MVC Works

- Client sends request → DispatcherServlet receives
- HandlerMapping → Finds correct controller
- Controller → Executes logic, returns Model + View
- ViewResolver → Resolves view name to actual file
- Response → Rendered HTML/JSON sent back

## 5. How Does Spring Security Work?

Spring security intercepts all requests. Follows following security principle (Have Complete Mediation).

- Client sends request (`/welcome`)
- Spring Security Filter Chain intercepts request before it reaches DispatcherServlet
  - Checks Authentication (are you logged in? token valid?)
  - Checks Authorization (do you have permission to access this URL?)
- If allowed → Request goes to DispatcherServlet → Controller
- Controller processes, returns response
- Response also passes through Security filters before going back

## 6. Spring Security Components in MVC

### a) Security Filter Chain

- Core of Spring Security
- A set of filters applied to every request:
  - UsernamePasswordAuthenticationFilter → handles login
  - BasicAuthenticationFilter → handles HTTP Basic auth
  - JwtAuthenticationFilter → handles JWT tokens (if configured)
  - AuthorizationFilter → checks if user has permission

Think of it as a security checkpoint with multiple guards.

### b) Authentication

Validates identity of the user.

**Options:**
- Form login (username/password)
- HTTP Basic Auth
- OAuth2 / JWT
- Custom authentication

If success → SecurityContext holds the Authentication object (user details, roles).
If fail → Request blocked, error 401 Unauthorized.

### c) Authorization

Checks permissions.

**Works at:**

**URL level (request mappings)**

```java
http.authorizeHttpRequests()
    .requestMatchers("/admin/**").hasRole("ADMIN")
    .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
    .anyRequest().authenticated();
```

**Method level**

```java
@PreAuthorize("hasRole('ADMIN')")
public void deleteUser(Long id) { ... }
```

### d) SecurityContext

After authentication, Spring stores the user's details in SecurityContextHolder. Any part of the app (controller, service) can fetch current user:

```java
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
String username = auth.getName();
```

### e) Integration with Spring MVC

Controller methods can use security annotations:

```java
@GetMapping("/profile")
public String profile(Authentication authentication, Model model) {
    model.addAttribute("username", authentication.getName());
    return "profile";
}
```

REST example:

```java
@RestController
public class AdminController {

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/dashboard")
    public String dashboard() {
        return "Admin Dashboard";
    }
}
```

## 7. CORS (Cross-Origin Resource Sharing)

By default, browsers block AJAX requests from a different domain (a security measure called Same-Origin Policy). 

**Same-origin** → frontend and backend have the same protocol, domain, and port. 

**Cross-origin** → different domains. 

**Example:**
- Frontend: http://myfrontend.com
- Backend API: http://api.mybackend.com

If frontend tries to call backend API directly, browser blocks it unless backend explicitly allows it.

CORS allows servers to specify which domains are allowed to make requests. Handled via special HTTP headers:
- `Access-Control-Allow-Origin: https://myfrontend.com`
- `Access-Control-Allow-Methods: GET, POST, PUT, DELETE`
- `Access-Control-Allow-Headers: Content-

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