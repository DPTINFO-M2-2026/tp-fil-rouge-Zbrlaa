package fr.utln.spelerin.services;

import fr.utln.spelerin.dto.UserDTO;
import fr.utln.spelerin.dto.createdto.UserCreateDTO;
import fr.utln.spelerin.entities.Guild;
import fr.utln.spelerin.entities.User;
import fr.utln.spelerin.repositories.GuildRepository;
import fr.utln.spelerin.repositories.RoleRepository;
import fr.utln.spelerin.repositories.UserRepository;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.NoSuchElementException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@QuarkusTest
class UserServiceTest {

	// Faux IDs (Snowflakes) pour la stabilité des tests
	private static final String MOCK_USER_ID = "999888777666555444"; 
	private static final String MOCK_GUILD_ID = "111222333444555666"; 

	@Inject
	UserService userService;

	@InjectMock
	UserRepository userRepository;

	@InjectMock
	GuildRepository guildRepository;

	@InjectMock
	RoleRepository roleRepository;

	@Test
	void createUser_Success() {
		// ARRANGE
		// 1. Le DTO doit fournir le Snowflake de l'Utilisateur
		UserCreateDTO dto = new UserCreateDTO(
			MOCK_USER_ID, // <--- AJOUTÉ : ID Snowflake
			"testuser", 
			"Test User"
		);

		// 2. Simplifier la simulation de persist. 
		// L'entité a déjà son ID, on simule juste la persistance.
		Mockito.doNothing().when(userRepository).persist(any(User.class));

		// ACT
		UserDTO result = userService.createUser(dto);

		// ASSERT
		assertNotNull(result);
		assertEquals(MOCK_USER_ID, result.id()); // Vérification de l'ID fourni
		assertEquals("testuser", result.username());
		Mockito.verify(userRepository, Mockito.times(1)).persist(any(User.class));
	}

	@Test
	void addGuildToUser_Success() {
		// ARRANGE
		// Utilisation des IDs Snowflakes fixes
		User mockUser = User.builder().username("u").displayName("U").build();
		mockUser.setId(MOCK_USER_ID);

		Guild mockGuild = Guild.builder().name("G").build();
		mockGuild.setId(MOCK_GUILD_ID);

		// Simuler la récupération des entités
		Mockito.when(userRepository.findById(MOCK_USER_ID)).thenReturn(mockUser);
		Mockito.when(guildRepository.findById(MOCK_GUILD_ID)).thenReturn(mockGuild);

		// ACT
		UserDTO result = userService.addGuildToUser(MOCK_USER_ID, MOCK_GUILD_ID);

		// ASSERT
		assertNotNull(result);
		assertTrue(mockUser.getGuilds().contains(mockGuild)); // Vérifie l'ajout côté entité
		Mockito.verify(userRepository, Mockito.times(1)).findById(MOCK_USER_ID);
		Mockito.verify(guildRepository, Mockito.times(1)).findById(MOCK_GUILD_ID);
		// Note: Si le service persiste explicitement l'User après modification, ajoutez la vérification ici.
	}

	@Test
	void addGuildToUser_UserNotFound_ThrowsException() {
		// ARRANGE
		// Utilisation des IDs Snowflakes fixes (seul l'ID de l'User est introuvable)
		Mockito.when(userRepository.findById(MOCK_USER_ID)).thenReturn(null);

		// ACT & ASSERT
		assertThrows(NoSuchElementException.class, () -> userService.addGuildToUser(MOCK_USER_ID, MOCK_GUILD_ID));
		
		// Vérifier que la recherche de Guilde n'a jamais été faite (si l'User n'est pas trouvé en premier)
		Mockito.verify(guildRepository, Mockito.never()).findById(any(String.class));
	}
}