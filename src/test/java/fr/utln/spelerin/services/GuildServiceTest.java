package fr.utln.spelerin.services;

import fr.utln.spelerin.dto.GuildDTO;
import fr.utln.spelerin.dto.createdto.GuildCreateDTO;
import fr.utln.spelerin.entities.Guild;
import fr.utln.spelerin.entities.User;
import fr.utln.spelerin.repositories.ChannelRepository;
import fr.utln.spelerin.repositories.GuildRepository;
import fr.utln.spelerin.repositories.RoleRepository;
import fr.utln.spelerin.repositories.UserRepository;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@QuarkusTest
class GuildServiceTest {

	// Faux IDs (Snowflakes) pour la stabilité des tests
	private static final String MOCK_GUILD_ID = "123456789012345678"; 
	private static final String MOCK_USER_ID = "987654321098765432";

	@Inject
	GuildService guildService;

	@InjectMock
	GuildRepository guildRepository;

	@InjectMock
	UserRepository userRepository;

	@InjectMock
	RoleRepository roleRepository;

	@InjectMock
	ChannelRepository channelRepository;

	@Test
	void createGuild_Success() {
		// ARRANGE
		// 1. Le DTO doit fournir le Snowflake de la Guilde
		GuildCreateDTO dto = new GuildCreateDTO(MOCK_GUILD_ID, "My New Guild");

		// 2. Simplifier la simulation de persist. 
		// L'ID est déjà sur l'entité, pas besoin de simuler la génération par la BDD.
		Mockito.doNothing().when(guildRepository).persist(any(Guild.class));

		// ACT
		GuildDTO result = guildService.createGuild(dto);

		// ASSERT
		assertNotNull(result);
		assertEquals("My New Guild", result.name());
		// L'ID retourné doit être celui que nous avons fourni au DTO
		assertEquals(MOCK_GUILD_ID, result.id()); 
		
		// On vérifie que la sauvegarde a été appelée avec l'entité
		Mockito.verify(guildRepository, Mockito.times(1)).persist(any(Guild.class));
	}

	@Test
	void addUserToGuild_Success() {
		// ARRANGE
		// La Guilde existe et a son ID Snowflake
		Guild mockGuild = Guild.builder().name("G").build();
		mockGuild.setId(MOCK_GUILD_ID);
		
		// L'Utilisateur existe et a son ID Snowflake
		User mockUser = User.builder().username("u").displayName("U").build();
		mockUser.setId(MOCK_USER_ID);

		// On simule la récupération des entités en base
		Mockito.when(guildRepository.findById(MOCK_GUILD_ID)).thenReturn(mockGuild);
		Mockito.when(userRepository.findById(MOCK_USER_ID)).thenReturn(mockUser);

		// ACT
		// On utilise les IDs Snowflakes fixes pour l'appel au service
		GuildDTO result = guildService.addUserToGuild(MOCK_GUILD_ID, MOCK_USER_ID);

		// ASSERT
		assertNotNull(result);
		// Vérifie que l'utilisateur a été ajouté à la collection de l'entité mockée
		assertTrue(mockGuild.getUsers().contains(mockUser));
		
		// Optionnel: Vérifier que la guilde a été mise à jour (si elle est marquée comme dirty dans le service)
		// Mockito.verify(guildRepository, Mockito.times(1)).persist(mockGuild); 
	}
}