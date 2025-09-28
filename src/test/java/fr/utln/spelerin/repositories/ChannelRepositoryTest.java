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

	@Inject
	ChannelRepository channelRepository;

	@Inject
	GuildRepository guildRepository;

	@Test
	@Transactional
	void persistFindDeleteChannel() {
		Guild g = Guild.builder().name("channel-guild").build();
		guildRepository.persist(g);
		assertNotNull(g.getId());

		Channel c = Channel.builder()
				.name("tc_channel")
				.type("text")
				.guild(g)
				.build();

		channelRepository.persist(c);
		assertNotNull(c.getId());

		Channel found = channelRepository.findById(c.getId());
		assertNotNull(found);
		assertEquals("tc_channel", found.getName());
		assertNotNull(found.getGuild());
		assertEquals(g.getId(), found.getGuild().getId());

		// delete
		boolean deleted = channelRepository.deleteById(c.getId());
		assertTrue(deleted);
		assertNull(channelRepository.findById(c.getId()));
	}
}
