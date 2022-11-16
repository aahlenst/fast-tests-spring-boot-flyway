package com.example.testing;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test that resets the database after each test with a custom {@link FlywayMigrationStrategy}. Because the
 * {@link FlywayMigrationStrategy} is applied by Spring Boot's autoconfiguration, a context refresh is required to
 * trigger it. That significantly slows down test execution because Spring needs to recreate all beans after each test.
 */
@JdbcTest
@ContextConfiguration(
	initializers = FlywayMigrationStrategyAuthorRepositoryTest.class,
	classes = {
		AuthorRepository.class,
		FlywayMigrationStrategyAuthorRepositoryTest.TestConfiguration.class
	}
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// Tells Spring to perform a context refresh after each test to trigger the FlywayMigrationStrategy (see below). Slow!
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Commit
public class FlywayMigrationStrategyAuthorRepositoryTest extends AbstractPostgresJupiterTest {

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

	@SpringBootApplication
	public static class TestConfiguration {
		@Bean
		public FlywayMigrationStrategy flywayMigrationStrategy() {
			return flyway -> {
				flyway.clean();
				flyway.migrate();
			};
		}
	}
}
