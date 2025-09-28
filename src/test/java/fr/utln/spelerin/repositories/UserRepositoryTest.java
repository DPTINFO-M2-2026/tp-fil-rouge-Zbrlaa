package fr.utln.spelerin.repositories;

import fr.utln.spelerin.entities.User;
import fr.utln.spelerin.tests.PostgresTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.common.QuarkusTestResource;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@QuarkusTestResource(PostgresTestResource.class)
class UserRepositoryTest {

	@Inject
	UserRepository userRepository;

	@Test
	@Transactional
	void persistAndFindUser() {
		User u = User.builder()
				.username("tc_user")
				.displayName("TestContainer User")
				.build();

		// persist
		userRepository.persist(u);
		assertNotNull(u.getId(), "Id must be generated after persist");

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
		String username = "findme-" + UUID.randomUUID();
		User u = User.builder()
				.username(username)
				.displayName("Find Me")
				.build();
		userRepository.persist(u);

		User byName = userRepository.findByUsername(username);
		assertNotNull(byName);
		assertEquals(username, byName.getUsername());
	}
}