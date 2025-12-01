package fr.utln.spelerin.services;

import fr.utln.spelerin.dto.ChannelDTO;
import fr.utln.spelerin.dto.createupdatedto.ChannelCreateUpdateDTO;
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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@QuarkusTest
class ChannelServiceTest {

	@Inject
	ChannelService channelService; // On injecte le vrai Service

	@InjectMock
	ChannelRepository channelRepository; // On mocke le repo (pas de vraie BDD)

	@InjectMock
	GuildRepository guildRepository; // On mocke le repo Guild

	@InjectMock
	RoleRepository roleRepository;   // On mocke le repo Role

	@Test
	void createChannel_Success() {
		// ARRANGE (Préparation)
		UUID guildId = UUID.randomUUID();
		Guild mockGuild = Guild.builder().name("TestGuild").build();
		// On force l'ID car le builder ne le met pas et la BDD n'est pas là pour le générer
		mockGuild.setId(guildId); 
		
		ChannelCreateUpdateDTO dto = new ChannelCreateUpdateDTO("Général", "text", guildId);

		// Quand le service demandera la guilde, on retourne notre faux objet
		Mockito.when(guildRepository.findById(guildId)).thenReturn(mockGuild);
		
		// Quand le service voudra sauvegarder, on ne fait rien (void) mais on simule l'ID généré
		Mockito.doAnswer(invocation -> {
			Channel c = invocation.getArgument(0);
			c.setId(UUID.randomUUID()); // On simule la génération d'ID par la BDD
			return null;
		}).when(channelRepository).persist(any(Channel.class));

		// ACT (Action)
		ChannelDTO result = channelService.createChannel(dto);

		// ASSERT (Vérification)
		assertNotNull(result);
		assertEquals("Général", result.name());
		assertEquals(guildId, result.guildId());
		
		// On vérifie que le repo a bien été appelé une fois
		Mockito.verify(channelRepository, Mockito.times(1)).persist(any(Channel.class));
	}

	@Test
	void createChannel_GuildNotFound_ShouldThrowException() {
		// ARRANGE
		UUID unknownGuildId = UUID.randomUUID();
		ChannelCreateUpdateDTO dto = new ChannelCreateUpdateDTO("Général", "text", unknownGuildId);

		// Quand on cherche la guilde, on retourne null (introuvable)
		Mockito.when(guildRepository.findById(unknownGuildId)).thenReturn(null);

		// ACT & ASSERT
		// On s'attend à ce que le service lance une IllegalArgumentException
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			channelService.createChannel(dto);
		});

		assertTrue(exception.getMessage().contains("Guild not found"));
		
		// On vérifie qu'on n'a JAMAIS essayé de sauvegarder en base
		Mockito.verify(channelRepository, Mockito.never()).persist(any(Channel.class));
	}
}