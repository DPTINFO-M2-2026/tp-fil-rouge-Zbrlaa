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

	private static final Long GUILD_SNOWFLAKE = 300000000000000000L;
	private static final Long ROLE_SNOWFLAKE = 400000000000000000L;

	@Inject
	RoleRepository roleRepository;

	@Inject
	GuildRepository guildRepository;

	@Test
	@Transactional
	void persistFindUpdateDeleteRole() {
		// 1. CREATE GUILD (L'ID Snowflake doit être fourni manuellement)
		Guild g = Guild.builder()
				.id(GUILD_SNOWFLAKE) // <--- AJOUT CRITIQUE
				.name("role-guild")
				.build();
		guildRepository.persist(g);
		assertEquals(GUILD_SNOWFLAKE, g.getId());

		// 2. CREATE ROLE (L'ID Snowflake doit être fourni manuellement)
		Role r = Role.builder()
				.id(ROLE_SNOWFLAKE) // <--- AJOUT CRITIQUE
				.name("tc_role")
				.permissions(3L)
				.guild(g)
				.build();

		roleRepository.persist(r);
		assertEquals(ROLE_SNOWFLAKE, r.getId());

		// FIND
		Role found = roleRepository.findById(r.getId());
		assertNotNull(found);
		assertEquals("tc_role", found.getName());
		assertNotNull(found.getGuild());
		assertEquals(g.getId(), found.getGuild().getId());

		// UPDATE
		found.setPermissions(7L);
		roleRepository.persist(found);
		Role updated = roleRepository.findById(r.getId());
		assertEquals(7L, updated.getPermissions());

		// DELETE
		boolean deleted = roleRepository.deleteById(r.getId());
		assertTrue(deleted);
		assertNull(roleRepository.findById(r.getId()));

		// Nettoyer la guilde parente (sinon elle reste en base)
		guildRepository.deleteById(g.getId());
	}
}