# Spring Boot Java Event-driven architecture

This is a sample project for how you can set up a
[multi-project Gradle build](https://docs.gradle.org/current/userguide/multi_project_builds.html)
using [Spring Boot](https://github.com/spring-projects/spring-boot),
[Java](https://www.java.com)
and [Event-driven architecture](https://en.wikipedia.org/wiki/Event-driven_architecture).
You can copy or fork this project to quickly set up a
new project with the same event-driven architecture.

## Event-driven architecture

[wikipedia.org](https://en.wikipedia.org/wiki/Event-driven_architecture) -
[aws.amazon.com](https://aws.amazon.com/event-driven-architecture/) -
[microservices.io](https://microservices.io/patterns/index.html)

Instead of the traditional request/response paradigm we achieve with REST APIs,
event-driven architecture allows us to use an asynchronous publish/consume pattern.
This is a popular way to structure decoupled microservices.

An event is when state has been updated, and it is relevant for other services.
This could for example be **ORDER_CREATED** or **PAYMENT_FAILED**.
Producers are responsible for publishing events and are unaware of which services 
consume them or how they process the events. Producers will simply just publish the event and move on.
Consumers are responsible for consuming events and can then process 
them which will likely lead to new events being published from the same service.

### Benefits

- **Decoupling**: Producers and consumers do not need to communicate directly so there is no waiting for a response. 
- **Scalability**: Consumers process events independently, allowing high scalability.
- **Performance**: Events can be processed in parallel by multiple consumers.

## Project structure

This sample project consists of two independently runnable microservices named **order** and **payment**.
Each service has a database and is responsible for its own domain.
Both services will consume and publish relevant events.
The order service uses 
[Spring Boot Web MVC](https://github.com/spring-projects/spring-boot)
to expose a REST API which has an endpoint for creating a new order.
Sending a request to this endpoint will initiate the microservices.

The **order** service will create an order and publish an **OrderCreatedEvent**.
This event is then consumed by the **payment** service.
For demonstration purposes, the **payment** service will then randomly
decide whether the payment completed or failed.
Below you can see a table presenting the two possible flows.

| API (sync)         | Input events     | Service | Action           | Output events    |
|--------------------|------------------|---------|------------------|------------------|
| POST /order/create |                  | Order   | Create order     | OrderCreated     |
|                    | OrderCreated     | Payment | Complete payment | PaymentCompleted |
|                    | PaymentCompleted | Order   | Complete order   | OrderCompleted   |
| -                  |                  |         |                  |                  |
| POST /order/create |                  | Order   | Create order     | OrderCreated     |
|                    | OrderCreated     | Payment | Fail payment     | PaymentFailed    |
|                    | PaymentFailed    | Order   | Cancel order     | OrderCancelled   |

## Patterns

### Transactional outbox
[microservices.io](https://microservices.io/patterns/data/transactional-outbox.html)

The problem is that if we save to the database and publish an event at the same time,
we cannot guarantee that the database transaction has been committed before the event is published
and consumed somewhere else. In that case, it could for example cause an issue where 
the **order** service consumes another event before the order has ever been saved to the database.
This can lead to data inconsistency.

This is solved by using the **transactional outbox** pattern in the two microservices.
We must ensure that an event is not published before the state has been saved to the database. 
When an order is created, the **order** service will save the order to the database,
but it will also save an **OrderCreatedEvent** to the outbox table.
Then a separate scheduled task will poll the outbox table and publish the event.
This ensures the database transaction has been committed before the event is published.

### Database per service
[microservices.io](https://microservices.io/patterns/data/database-per-service.html)

Each microservice has its own database. This is a common pattern in microservices

## Setup

LocalStack

## Usage

Clone the project to your local machine, go to the root directory and use
these two commands in separate terminals.
```
./gradlew order:bootRun
```
```
./gradlew payment:bootRun
```
You can also use IntelliJ IDEA to easily run the two services at once.

### Swagger Documentation
Once the system is running, navigate to http://localhost:8080/
to view the Swagger documentation for the **order** service.

## Technology

- [LocalStack](https://github.com/localstack/localstack) - For testing Amazon Web Services SQS and SNS locally
- [JDK21](https://openjdk.org/projects/jdk/21/) - Latest JDK with long-term support
- [Gradle](https://github.com/gradle/gradle) - Used for compilation, building, testing and dependency management
- [Spring Boot Web MVC](https://github.com/spring-projects/spring-boot) - For creating REST APIs
- [Springdoc](https://github.com/springdoc/springdoc-openapi) - Provides Swagger documentation for REST APIs
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/reference/index.html) - Repository support for JPA
- [H2database](https://github.com/h2database/h2database) - Provides an in-memory database for simple local testing
- [Liquibase](https://github.com/liquibase/liquibase) - Used to manage database schema changelogs

## Gradle best practices
[kotlinlang.org](https://kotlinlang.org/docs/gradle-best-practices.html)

### Preface
This project uses Java but the linked article above is generally meant 
for Kotlin projects. However, I still think that the recommended best 
practices for Gradle are relevant for a Java project as well. 
The recommendations can be useful for all sorts of Gradle projects.

### ✅ Use Kotlin DSL
This project uses Kotlin DSL instead of the traditional Groovy DSL by
using **build.gradle.kts** files instead of **build.gradle** files.
This gives us the benefits of strict typing which lets IDEs provide
better support for refactoring and auto-completion.

### ✅ Use a version catalog

This project uses a version catalog
[local.versions.toml](gradle/local.versions.toml)
which allows us to centralize dependency management.
We can define versions, libraries, bundles and plugins here.
This enables us to use Gradle dependencies consistently across the entire project.

Dependencies can then be implemented in a specific **build.gradle.kts** file as such:
```kotlin
implementation(local.spring.boot.starter)
```

The Kotlinlang article says to name the version catalog **libs.versions.toml**
but for this project it has been named **local.versions.toml**. The reason
for this is that we can create a shared common version catalog which can
be used across Gradle projects. Imagine that you are working on multiple
similar Gradle projects with different purposes, but each project has some
specific dependencies but also some dependencies in common. The dependencies
that are common across projects could be placed in the shared version catalog
while specific dependencies are placed in the local version catalog.

### ✅ Use local build cache

This project uses a local
[build cache](https://docs.gradle.org/current/userguide/build_cache.html)
for Gradle which is a way to increase build performance because it will
re-use outputs produced by previous builds. It will store build outputs
locally and allow subsequent builds to fetch these outputs from the cache
when it knows that the inputs have not changed.
This means we can save time building

Gradle build cache is disabled by default so it has been enabled for this
project by updating the root [gradle.properties](gradle.properties) file:
```properties
org.gradle.caching=true
```

This is enough to enable the local build cache
and by default, this will use a directory in the Gradle User Home
to store build cache artifacts.

### ✅ Use configuration cache

This project uses
[Gradle configuration cache](https://docs.gradle.org/current/userguide/configuration_cache.html)
and this will improve build performance by caching the result of the
configuration phase and reusing this for subsequent builds. This means
that Gradle tasks can be executed faster if nothing has been changed
that affects the build configuration. If you update a **build.gradle.kts**
file, the build configuration has been affected.

This is not enabled by default, so it is enabled by defining this in
the root [gradle.properties](gradle.properties) file:
```properties
org.gradle.configuration-cache=true
org.gradle.configuration-cache.parallel=true
```

### ✅ Use modularization

This project uses modularization to create a
[multi-project Gradle build](https://docs.gradle.org/current/userguide/multi_project_builds.html).
The benefit here is that we optimize build performance and structure our
entire project in a meaningful way. This is more scalable as it is easier
to grow a large project when you structure the code like this.

```
root
│─ build.gradle.kts
│─ settings.gradle.kts
│─ apps
│   └─ order
│       └─ build.gradle.kts
│   └─ payment
│       └─ build.gradle.kts
│─ modules
│   ├─ consumer
│   │   └─ build.gradle.kts
│   ├─ event
│   │   └─ build.gradle.kts
│   ├─ jackson
│   │   └─ build.gradle.kts
│   ├─ model
│   │   └─ build.gradle.kts
│   ├─ outbox
│   │   └─ build.gradle.kts
│   └─ producer
│       └─ build.gradle.kts
```

This also allows us to specifically decide which Gradle dependencies will be used
for which subproject. Each subproject should only use exactly the dependencies
that they need.

Subprojects located under [apps](apps) are runnable, so this means we can
run the **order** project to spin up a service. We can add more
subprojects under [apps](apps) to create additional runnable microservices.

Subprojects located under [modules](modules) are not independently runnable.
The subprojects are used to structure code into various layers. The **model**
subproject is the most inner layer and contains domain model classes and this
subproject knows nothing about any of the other subprojects. The purpose of
the **persistence** subproject is to manage the code responsible for
interacting with the database. We can add more non-runnable subprojects
under [modules](modules) if necessary. This could for example
be a third-party integration.

---

#### Subproject with other subproject as dependency

The subprojects in this repository may use other subprojects as dependencies.

In our root [settings.gradle.kts](settings.gradle.kts) we have added:
```kotlin
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
```
Which allows us to add a subproject as a dependency in another subproject:

```kotlin
dependencies {
    implementation(projects.model)
}
```

This essentially allows us to define this structure:

```
order  
│─ consumer  
│─ event  
│─ jackson  
│─ model  
│─ outbox  
└─ producer

payment  
│─ consumer  
│─ event  
│─ jackson  
│─ model  
│─ outbox  
└─ producer

consumer  
└─ event

outbox  
└─ event

event, jackson, model and producer has no dependencies
```

## Meta

This project has been created with the sample code structure from:
[thorlauridsen/spring-boot-java-sample](https://github.com/thorlauridsen/spring-boot-java-sample).
