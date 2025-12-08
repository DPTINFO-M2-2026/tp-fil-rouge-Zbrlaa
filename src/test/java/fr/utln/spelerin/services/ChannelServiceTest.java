package fr.utln.spelerin.services;

import fr.utln.spelerin.dto.ChannelDTO;
import fr.utln.spelerin.dto.createdto.ChannelCreateDTO;
import fr.utln.spelerin.entities.Channel;
import fr.utln.spelerin.entities.Guild;
import fr.utln.spelerin.repositories.ChannelRepository;
import fr.utln.spelerin.repositories.GuildRepository;
import fr.utln.spelerin.repositories.RoleRepository;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@QuarkusTest
class ChannelServiceTest {

	// Faux IDs (Snowflakes) pour la stabilité des tests
	private static final Long MOCK_GUILD_ID = 123456789012345678L; 
	private static final Long MOCK_CHANNEL_ID = 987654321098765432L; 

	@Inject
	ChannelService channelService; // On injecte le vrai Service

	@InjectMock
	ChannelRepository channelRepository; // On mocke le repo (pas de vraie BDD)

	@InjectMock
	GuildRepository guildRepository; // On mocke le repo Guild

	@InjectMock
	RoleRepository roleRepository; // On mocke le repo Role

	@Test
	void createChannel_Success() {
		// ARRANGE (Préparation)
		
		Guild mockGuild = Guild.builder().name("TestGuild").build();
		mockGuild.setId(MOCK_GUILD_ID); 
		
		// Le DTO contient le Snowflake du Channel et l'ID de la Guilde
		ChannelCreateDTO dto = new ChannelCreateDTO(
			MOCK_CHANNEL_ID, 
			"Général", 
			0,
			MOCK_GUILD_ID
		);

		// Quand le service demandera la guilde, on retourne notre faux objet
		Mockito.when(guildRepository.findById(MOCK_GUILD_ID)).thenReturn(mockGuild);
		
		// Quand le service voudra sauvegarder, on fait juste 'nothing' car l'ID est déjà set
		Mockito.doNothing().when(channelRepository).persist(any(Channel.class));

		// ACT (Action)
		ChannelDTO result = channelService.createChannel(dto);

		// ASSERT (Vérification)
		assertNotNull(result);
		// On vérifie que le résultat a bien récupéré le Snowflake que nous avons fourni
		assertEquals(MOCK_CHANNEL_ID, result.id()); 
		assertEquals("Général", result.name());
		assertEquals(MOCK_GUILD_ID, result.guildId());
		
		// On vérifie que le repo a bien été appelé une fois
		Mockito.verify(channelRepository, Mockito.times(1)).persist(any(Channel.class));
	}

	@Test
	void createChannel_GuildNotFound_ShouldThrowException() {
		// ARRANGE
		Long unknownGuildId = 000000000000000001L;

		// Le DTO doit être créé avec le Channel ID en premier argument
		ChannelCreateDTO dto = new ChannelCreateDTO(
			MOCK_CHANNEL_ID,
			"Général", 
			0,
			unknownGuildId
		);

		// Quand on cherche la guilde, on retourne null (introuvable)
		Mockito.when(guildRepository.findById(unknownGuildId)).thenReturn(null);

		// ACT & ASSERT
		// On s'attend à ce que le service lance une IllegalArgumentException
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			channelService.createChannel(dto);
		});

		// Le reste de la vérification reste identique
		assertTrue(exception.getMessage().contains("Guild not found"));
		
		// On vérifie qu'on n'a JAMAIS essayé de sauvegarder en base
		Mockito.verify(channelRepository, Mockito.never()).persist(any(Channel.class));
	}
}