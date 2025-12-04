package fr.utln.spelerin.repositories;

import fr.utln.spelerin.entities.Guild;
import fr.utln.spelerin.tests.PostgresTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.common.QuarkusTestResource;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@QuarkusTestResource(PostgresTestResource.class)
class GuildRepositoryTest {

	// Snowflake de test fixe pour la reproductibilité
	private static final String GUILD_SNOWFLAKE = "200000000000000000";

	@Inject
	GuildRepository guildRepository;

	@Test
	@Transactional
	void persistFindDeleteGuild() {
		// L'ID Snowflake doit être fourni manuellement
		Guild g = Guild.builder()
				.id(GUILD_SNOWFLAKE) // <--- AJOUT CRITIQUE
				.name("tc_guild")
				.build();
		
		guildRepository.persist(g);
		assertEquals(GUILD_SNOWFLAKE, g.getId());

		// FIND
		Guild found = guildRepository.findById(g.getId());
		assertNotNull(found);
		assertEquals("tc_guild", found.getName());

		// UPDATE (Optionnel, mais bonne pratique de CRUD)
		found.setName("tc_guild_updated");
		guildRepository.persist(found);
		
		Guild updated = guildRepository.findById(GUILD_SNOWFLAKE);
		assertEquals("tc_guild_updated", updated.getName());


		// DELETE
		boolean deleted = guildRepository.deleteById(g.getId());
		assertTrue(deleted);
		assertNull(guildRepository.findById(g.getId()));
	}
}