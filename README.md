# Spring Cloud Java Event-driven architecture

This is a sample project for how you can set up a
[multi-project Gradle build](https://docs.gradle.org/current/userguide/multi_project_builds.html)
using [Spring Cloud AWS](https://github.com/awspring/spring-cloud-aws),
[Java](https://www.java.com)
and [Event-driven architecture](https://en.wikipedia.org/wiki/Event-driven_architecture).
You can copy or fork this project to quickly set up a
new project with the same event-driven architecture.

## Event-driven architecture

[aws.amazon.com](https://aws.amazon.com/event-driven-architecture/) -
[microservices.io](https://microservices.io/patterns/index.html) - 
[wikipedia.org](https://en.wikipedia.org/wiki/Event-driven_architecture)

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
Services can evolve independently without the risk of introducing system-wide issues.
- **Scalability**: Microservices are loosely coupled so it is easier to scale without affecting other parts of the system.
- **Flexibility**: It is easier to add new functionality without affecting other services.
You can even use different technologies for separate microservices.
- **Performance**: Events can be processed in parallel by multiple consumers.

### Obstacles

- **At-least-once delivery**: Events can be delivered more than once, so consumer methods must be idempotent.
- **Ordering**: Event order is not guaranteed, so consumers must be able to handle events out of order.
However, you can set up strict ordering such as FIFO but this may reduce performance.
- **Schema evolution**: Events can change over time, so backward compatibility must be ensured.

## Project structure

This sample project consists of two independently runnable microservices named **order** and **payment**.
Each service has a database and is responsible for its own domain.
Both services will consume and publish relevant events.
The **order** service uses 
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

### Choreography-based saga
[aws.amazon.com](https://docs.aws.amazon.com/prescriptive-guidance/latest/cloud-design-patterns/saga-choreography.html) - 
[microservices.io](https://microservices.io/patterns/data/saga.html)

The **order** and **payment** service are part of a **choreography-based saga**.
Each service is responsible for its own domain, and they communicate through events.
Once an event is published, the producer does not know how another service might 
consume and process it. Services simply react to consumed events and publish new events.
An important thing about this pattern is that every action needs a compensating action 
if something fails. For example, if the **payment** service fails to complete a payment, 
it must publish a **PaymentFailedEvent**. The **order** service will then consume 
this event and cancel the order. This is a way to ensure eventual consistency.

If we had a central orchestrator, it would be called an **orchestration-based saga**.

### Transactional outbox
[aws.amazon.com](https://docs.aws.amazon.com/prescriptive-guidance/latest/cloud-design-patterns/transactional-outbox.html) -
[microservices.io](https://microservices.io/patterns/data/transactional-outbox.html)

A common issue with event-driven architecture is ensuring that state has been saved
to the database before an event is published. If we try to save an entity to the database
and publish an event at the same time, it can lead to issues as the database transaction
may not have been committed before the event is published.

Example issue:
1. **Order** service tries to save an order to the database.
   - Transaction has not been committed yet.
2. **Order** service publishes an **OrderCreatedEvent**.
3. **Payment** service consumes the **OrderCreatedEvent**.
4. **Payment** service publishes a **PaymentCompletedEvent**.
5. **Order** service consumes the **PaymentCompletedEvent**.
6. **Order** service tries to complete the order.
   - Transaction has still not been committed yet.
   - Order cannot be completed because it does not exist in the database.

This is solved by using the **transactional outbox** pattern in the two microservices.
We must ensure that an event is not published before the state has been saved to the database. 
When an order is created, the **order** service will save the order to the database,
but it will also save an **OrderCreatedEvent** to the outbox table.
Then a separate scheduled task will poll the outbox table and publish the event.
This ensures the database transaction has been committed before the event is published.
Essentially, an event can never be published before a specific database
transaction has been committed.

### Idempotency and deduplication
[microservices.io](https://microservices.io/patterns/communication-style/idempotent-consumer.html)

Considering that many messaging services guarantee **at-least-once delivery**, 
we must ensure that our services can gracefully handle duplicate events.
Ideally, our methods should always be idempotent which means that calling
a method multiple times with the same input always produces the same output.
If your methods are idempotent, then you can safely process the same event multiple times.

Sometimes it is not possible to make a method idempotent.
In that case, we can for example use some type of deduplication mechanism to
gracefully handle duplicate events. This project showcases an example of how
you can implement deduplication. 

In the **deduplication** subproject, you can find 
[ProcessedEventRepo](modules/deduplication/src/main/java/com/github/thorlauridsen/deduplication/ProcessedEventRepo.java) 
and [ProcessedEventEntity](modules/deduplication/src/main/java/com/github/thorlauridsen/deduplication/ProcessedEventEntity.java).
These are used to store processed events in the database.
When a service consumes an event, it will first check if the event has already been processed.
If the event has already been processed, then the service will not process the event again.
If the event has not been processed, then the service will process the event and mark 
the event as processed.

### Database per service
[aws.amazon.com](https://docs.aws.amazon.com/prescriptive-guidance/latest/modernization-data-persistence/database-per-service.html) -
[microservices.io](https://microservices.io/patterns/data/database-per-service.html)

Each service has its own database which is a common pattern in event-driven architecture.
This allows each service to have its own database schema related to its own domain.
A benefit here is that multiple services do not need to rely and depend on the same 
shared database schema. This allows for more scalability and independence.
A specific service could even use a completely different database technology than another service.

## Usage

### Docker Compose
To run the system with [Docker Compose](https://docs.docker.com/compose/),
clone the project to your local machine, go to the root directory and use:
```
docker-compose up -d
```
This will launch the entire project with 
[LocalStack](https://github.com/localstack/localstack), 
[PostgreSQL](https://www.postgresql.org/) 
and the two microservices.

### Gradle

For this project I have decided to create an independent SQS queue and SNS topic for each event.
You can use
[LocalStack](https://github.com/localstack/localstack)
to run AWS services locally through
[Docker](https://www.docker.com/).
If you wish to run the microservices in this project,
you must first start LocalStack and create the queues and topics.

Once you have set up **localstack** and **awslocal**, open a terminal and use:
```
localstack start -d
```

Then you can create the queues and topics with the following commands:
```
awslocal sqs create-queue --queue-name order-created-queue
awslocal sns create-topic --name order-created-topic
awslocal sqs get-queue-attributes --queue-url http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/order-created-queue --attribute-name QueueArn
awslocal sns subscribe --topic-arn arn:aws:sns:us-east-1:000000000000:order-created-topic --protocol sqs --notification-endpoint arn:aws:sqs:us-east-1:000000000000:order-created-queue
awslocal sqs create-queue --queue-name payment-completed-queue
awslocal sns create-topic --name payment-completed-topic
awslocal sqs get-queue-attributes --queue-url http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/payment-completed-queue --attribute-name QueueArn
awslocal sns subscribe --topic-arn arn:aws:sns:us-east-1:000000000000:payment-completed-topic --protocol sqs --notification-endpoint arn:aws:sqs:us-east-1:000000000000:payment-completed-queue
awslocal sqs create-queue --queue-name payment-failed-queue
awslocal sns create-topic --name payment-failed-topic
awslocal sqs get-queue-attributes --queue-url http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/payment-failed-queue --attribute-name QueueArn
awslocal sns subscribe --topic-arn arn:aws:sns:us-east-1:000000000000:payment-failed-topic --protocol sqs --notification-endpoint arn:aws:sqs:us-east-1:000000000000:payment-failed-queue
```

Clone the project to your local machine, go to the root directory and use
these two commands in separate terminals.
```
./gradlew order:bootRun
```
```
./gradlew payment:bootRun
```
This will start the two microservices each using an in-memory H2 database.
You can also use IntelliJ IDEA to easily run the two services at once.

### Swagger Documentation
Once the entire system is running, you can view the Swagger documentation at:
- http://localhost:8080/ for the **order** service
- http://localhost:8081/ for the **payment** service

## Technology

- [JDK21](https://openjdk.org/projects/jdk/21/) - Latest JDK with long-term support
- [Gradle](https://github.com/gradle/gradle) - Used for compilation, building, testing and dependency management
- [Spring Cloud AWS](https://github.com/awspring/spring-cloud-aws) - For interacting with Amazon Web Services SQS and SNS
- [LocalStack](https://github.com/localstack/localstack) - For testing Amazon Web Services SQS and SNS locally
- [Docker](https://www.docker.com/) - Used to run LocalStack in a Docker container
- [Springdoc](https://github.com/springdoc/springdoc-openapi) - Provides Swagger documentation for REST APIs
- [Spring Boot Web MVC](https://github.com/spring-projects/spring-boot) - For creating REST APIs
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/reference/index.html) - Repository support for JPA
- [PostgreSQL](https://www.postgresql.org/) - Open-source relational database
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
│   ├─ deduplication
│   │   └─ build.gradle.kts
│   ├─ event
│   │   └─ build.gradle.kts
│   ├─ exception
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
│─ exception
│─ jackson
│─ model
│─ outbox
└─ producer

payment
│─ consumer
│─ event
│─ exception
│─ jackson
│─ model
│─ outbox
└─ producer

consumer
│─ event
└─ model

deduplication
└─ model

event
└─ model

outbox
└─ model

producer
│─ event
└─ model

exception, jackson and model has no dependencies
```

## Meta

This project has been created with the sample code structure from:
[thorlauridsen/spring-boot-java-sample](https://github.com/thorlauridsen/spring-boot-java-sample).
