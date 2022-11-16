package com.example.testing;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Starts a single PostgreSQL instance per JVM. All tests run on a single JVM reuse this PostgreSQL instance. Reusing
 * the database instance saves considerable time, especially if your test database starts slower than PostgreSQL.
 * <p/>
 * See the <a href="https://www.testcontainers.org/test_framework_integration/manual_lifecycle_control/">Testcontainers
 * documentation</a> for details about singleton containers.
 */
public class AbstractPostgresJupiterTest implements ApplicationContextInitializer<ConfigurableApplicationContext> {
	static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:15.1");

	static {
		postgres.start();
	}

	@Override
	public void initialize(@NotNull ConfigurableApplicationContext applicationContext) {
		TestPropertyValues
			.of(
				"spring.datasource.url=" + postgres.getJdbcUrl(),
				"spring.datasource.password=" + postgres.getPassword(),
				"spring.datasource.username=" + postgres.getUsername(),
				"spring.flyway.clean-disabled=false"
			)
			.applyTo(applicationContext);
	}

}
