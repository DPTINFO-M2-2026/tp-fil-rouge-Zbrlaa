package fr.utln.spelerin.repositories;

import fr.utln.spelerin.entities.Guild;
import fr.utln.spelerin.entities.Role;
import fr.utln.spelerin.tests.PostgresTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.common.QuarkusTestResource;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@QuarkusTestResource(PostgresTestResource.class)
class RoleRepositoryTest {

	@Inject
	RoleRepository roleRepository;

	@Inject
	GuildRepository guildRepository;

	@Test
	@Transactional
	void persistFindUpdateDeleteRole() {
		Guild g = Guild.builder().name("role-guild").build();
		guildRepository.persist(g);
		assertNotNull(g.getId());

		Role r = Role.builder()
				.name("tc_role")
				.permissions(3L)
				.guild(g)
				.build();

		roleRepository.persist(r);
		assertNotNull(r.getId());

		Role found = roleRepository.findById(r.getId());
		assertNotNull(found);
		assertEquals("tc_role", found.getName());
		assertNotNull(found.getGuild());
		assertEquals(g.getId(), found.getGuild().getId());

		// update
		found.setPermissions(7L);
		roleRepository.persist(found);
		Role updated = roleRepository.findById(r.getId());
		assertEquals(7L, updated.getPermissions());

		// delete
		boolean deleted = roleRepository.deleteById(r.getId());
		assertTrue(deleted);
		assertNull(roleRepository.findById(r.getId()));
	}
}
