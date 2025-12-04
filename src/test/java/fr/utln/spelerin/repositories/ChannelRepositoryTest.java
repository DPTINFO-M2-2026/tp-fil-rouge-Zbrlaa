package fr.utln.spelerin.repositories;

import fr.utln.spelerin.entities.Channel;
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
class ChannelRepositoryTest {

	private static final String GUILD_SNOWFLAKE = "123456789012345678";
	private static final String CHANNEL_SNOWFLAKE = "987654321098765432";

	@Inject
	ChannelRepository channelRepository;

	@Inject
	GuildRepository guildRepository;

	@Test
	@Transactional
	void persistFindDeleteChannel() {
		// 1. CREATE GUILD (L'ID Snowflake doit être fourni manuellement)
		Guild g = Guild.builder()
				.id(GUILD_SNOWFLAKE) // <--- AJOUT CRITIQUE
				.name("channel-guild")
				.build();
		
		guildRepository.persist(g);
		assertEquals(GUILD_SNOWFLAKE, g.getId());

		// 2. CREATE CHANNEL (L'ID Snowflake doit être fourni manuellement)
		Channel c = Channel.builder()
				.id(CHANNEL_SNOWFLAKE) // <--- AJOUT CRITIQUE
				.name("tc_channel")
				.type("text")
				.guild(g)
				.build();

		channelRepository.persist(c);
		assertEquals(CHANNEL_SNOWFLAKE, c.getId());

		// FIND
		Channel found = channelRepository.findById(c.getId());
		assertNotNull(found);
		assertEquals("tc_channel", found.getName());
		assertNotNull(found.getGuild());
		assertEquals(g.getId(), found.getGuild().getId());

		// DELETE
		boolean deleted = channelRepository.deleteById(c.getId());
		assertTrue(deleted);
		assertNull(channelRepository.findById(c.getId()));
		
		// Nettoyer la guilde (bonne pratique dans les tests de repository)
		guildRepository.deleteById(g.getId());
		assertNull(guildRepository.findById(g.getId()));
	}
}