# Fast, Reliable Integration Tests with Spring Boot and Flyway

This sample project shows different ways to set up reliable integration tests with [Spring Boot](https://spring.io/projects/spring-boot), [Flyway](https://flywaydb.org/), and [Testcontainers](https://www.testcontainers.org/). All other technologies used (JDBC, [PostgreSQL](https://postgresql.org/), [JUnit 5](https://junit.org/junit5/)) are interchangeable: Using, for example, [Hibernate](https://hibernate.org/), [MariaDB](https://mariadb.org/), and [TestNG](https://testng.org/) is possible with the same techniques.

All approaches guarantee that all test cases are independent (start with a fresh database) and do not hide errors (like database rollbacks). Furthermore, the test classes can be executed concurrently[^1] to take advantage of multicore processors. What varies, however, is their execution speed and how much custom plumbing is necessary.

* [FlywayMigrationStrategyAuthorRepositoryTest.java](src/test/java/com/example/testing/FlywayMigrationStrategyAuthorRepositoryTest.java) uses only facilities provided by Spring Boot but is the slowest (100% runtime).
* [FlywayMigrationAuthorRepositoryTest.java](src/test/java/com/example/testing/FlywayMigrationAuthorRepositoryTest.java) requires a [TestExecutionListener](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/test/context/TestExecutionListener.html) but is significantly faster than the previous approach (~70% runtime).
* [DatabaseDumpAuthorRepositoryTest.java](src/test/java/com/example/testing/DatabaseDumpAuthorRepositoryTest.java) uses a dynamically generated SQL dump to initialise the database. This is the fastest approach (~50% runtime) and stays fast even if the number of Flyway migrations grows. However, [you need a fair number of custom Gradle tasks](build.gradle) to convert the Flyway migrations into a SQL dump at build time.

The performance numbers are indicative. They depend on your hardware and the specifics of your project, for example, the number of Flyway migrations.

[^1]: The project relies on [Gradle's parallel test execution](https://docs.gradle.org/current/userguide/performance.html#parallel_test_execution) because it uses process isolation to run the tests concurrently. This ensures that tests that run simultaneously do not interfere with each other while keeping the number of running PostgreSQL containers at a reasonable number (one PostgreSQL container per worker process).
