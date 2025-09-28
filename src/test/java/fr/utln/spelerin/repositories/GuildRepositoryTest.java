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

	@Inject
	GuildRepository guildRepository;

	@Test
	@Transactional
	void persistFindDeleteGuild() {
		Guild g = Guild.builder().name("tc_guild").build();
		guildRepository.persist(g);
		assertNotNull(g.getId());

		Guild found = guildRepository.findById(g.getId());
		assertNotNull(found);
		assertEquals("tc_guild", found.getName());

		// delete
		boolean deleted = guildRepository.deleteById(g.getId());
		assertTrue(deleted);
		assertNull(guildRepository.findById(g.getId()));
	}
}
