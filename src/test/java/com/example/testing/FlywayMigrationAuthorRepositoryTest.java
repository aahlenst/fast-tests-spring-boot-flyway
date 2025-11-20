package com.example.testing;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.flyway.autoconfigure.FlywayMigrationStrategy;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test that uses {@link CleanDatabaseTestExecutionListener} to reinitialise the database after each test.
 */
@JdbcTest
@ContextConfiguration(
	initializers = FlywayMigrationAuthorRepositoryTest.class,
	// Whether explicit inclusion of @Repository and @TestConfiguration is necessary depends on the test slice
	// (like @JdbcTest) being used. Some slices automatically scan for those. Check the Spring Boot documentation on the
	// test slice you are using.
	classes = {
		AuthorRepository.class,
		FlywayMigrationAuthorRepositoryTest.AdditionalTestConfiguration.class
	}
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// An alternative to a TestExecutionListener would be a setup method annotated with @BeforeEach that invokes Flyway. But
// then you would lose the ability to access the database in any TestExecutionListener (incl. the one that processes
// @Sql annotations) because @BeforeEach runs after the TestExecutionListeners.
@TestExecutionListeners(
	value = {CleanDatabaseTestExecutionListener.class},
	mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS // Retains default TestExecutionListeners.
)
@Commit
class FlywayMigrationAuthorRepositoryTest extends AbstractPostgresJupiterTest {

	@Autowired
	AuthorRepository authorRepository;

	@Test
	void findAll_returnsAllAuthors() {
		var authors = this.authorRepository.findAll();

		assertThat(authors)
			.extracting(Author::name)
			.containsExactly("Bert Bates", "Joshua Bloch", "Kathy Sierra", "Trisha Gee");
	}

	@Test
	void add_insertsAuthor() {
		var author = new Author(AuthorId.from("82f4870d-f2e5-4a9f-a2b2-b297f66733a0"), "Brian Goetz");

		this.authorRepository.add(author);

		assertThat(this.authorRepository.findAll())
			.extracting(Author::name)
			.containsExactly("Bert Bates", "Brian Goetz", "Joshua Bloch", "Kathy Sierra", "Trisha Gee");
	}

	@Test
	void delete_ignoresNullAuthorId() {
		this.authorRepository.delete(null);

		assertThat(this.authorRepository.findAll())
			.extracting(Author::name)
			.containsExactly("Bert Bates", "Joshua Bloch", "Kathy Sierra", "Trisha Gee");
	}

	@Test
	void delete_removesAuthor() {
		this.authorRepository.delete(AuthorId.from("1a0f9c80-1309-4e9d-a291-752354b51c51"));

		assertThat(this.authorRepository.findAll())
			.extracting(Author::name)
			.containsExactly("Bert Bates", "Kathy Sierra", "Trisha Gee");
	}

	@Test
	void delete_hasNoEffectWhenAuthorIsUnknown() {
		this.authorRepository.delete(AuthorId.from("06f4cec2-d26d-43ac-9cd5-351825bf2af9"));

		assertThat(this.authorRepository.findAll())
			.extracting(Author::name)
			.containsExactly("Bert Bates", "Joshua Bloch", "Kathy Sierra", "Trisha Gee");
	}

	@TestConfiguration
	public static class AdditionalTestConfiguration {
		@Bean
		public FlywayMigrationStrategy flywayMigrationStrategy() {
			return flyway -> {
				// Do nothing to disable the Flyway migration action on startup without having to disable the Flyway
				// autoconfiguration which is what spring.flyway.enabled=false would do.
			};
		}
	}
}
