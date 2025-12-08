package fr.utln.spelerin.repositories;

import fr.utln.spelerin.entities.User;
import fr.utln.spelerin.tests.PostgresTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.common.QuarkusTestResource;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@QuarkusTestResource(PostgresTestResource.class)
class UserRepositoryTest {

	private static final Long USER_SNOWFLAKE = 500000000000000000L;
	private static final Long USER_SNOWFLAKE_FIND = 600000000000000000L;

	@Inject
	UserRepository userRepository;

	@Test
	@Transactional
	void persistAndFindUser() {
		// L'ID Snowflake doit être fourni manuellement
		User u = User.builder()
				.id(USER_SNOWFLAKE) // <--- AJOUT CRITIQUE
				.username("tc_user")
				.displayName("TestContainer User")
				.build();

		// persist
		userRepository.persist(u);
		// L'ID ne doit plus être vérifié pour la génération, mais pour l'égalité avec le Snowflake fourni
		assertEquals(USER_SNOWFLAKE, u.getId(), "ID must match the provided Snowflake.");

		// find
		User found = userRepository.findById(u.getId());
		assertNotNull(found);
		assertEquals("tc_user", found.getUsername());

		// update
		found.setDisplayName("Updated Name");
		userRepository.persist(found);

		User updated = userRepository.findById(u.getId());
		assertEquals("Updated Name", updated.getDisplayName());

		// delete
		boolean deleted = userRepository.deleteById(u.getId());
		assertTrue(deleted);

		User afterDelete = userRepository.findById(u.getId());
		assertNull(afterDelete);
	}

	@Test
	@Transactional
	void findByUsernameShouldReturnCorrectUser() {
		String username = "findme-" + System.nanoTime();
		// L'ID Snowflake doit être fourni manuellement
		User u = User.builder()
				.id(USER_SNOWFLAKE_FIND) // <--- AJOUT CRITIQUE
				.username(username)
				.displayName("Find Me")
				.build();
		userRepository.persist(u);
		assertEquals(USER_SNOWFLAKE_FIND, u.getId()); // Vérification du Snowflake

		User byName = userRepository.findByUsername(username);
		assertNotNull(byName);
		assertEquals(username, byName.getUsername());

		// Nettoyage après le test
		userRepository.deleteById(u.getId());
	}
}