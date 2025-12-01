package fr.utln.spelerin.services;

import fr.utln.spelerin.dto.UserDTO;
import fr.utln.spelerin.dto.createupdatedto.UserCreateUpdateDTO;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@QuarkusTest
class UserServiceTest {

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
		UserCreateUpdateDTO dto = new UserCreateUpdateDTO("testuser", "Test User");

		Mockito.doAnswer(invocation -> {
			User u = invocation.getArgument(0);
			u.setId(UUID.randomUUID());
			return null;
		}).when(userRepository).persist(any(User.class));

		UserDTO result = userService.createUser(dto);

		assertNotNull(result);
		assertEquals("testuser", result.username());
		Mockito.verify(userRepository).persist(any(User.class));
	}

	@Test
	void addGuildToUser_Success() {
		UUID userId = UUID.randomUUID();
		UUID guildId = UUID.randomUUID();

		User mockUser = User.builder().username("u").displayName("U").build();
		mockUser.setId(userId);

		Guild mockGuild = Guild.builder().name("G").build();
		mockGuild.setId(guildId);

		Mockito.when(userRepository.findById(userId)).thenReturn(mockUser);
		Mockito.when(guildRepository.findById(guildId)).thenReturn(mockGuild);

		UserDTO result = userService.addGuildToUser(userId, guildId);

		assertNotNull(result);
		assertTrue(mockUser.getGuilds().contains(mockGuild)); // Vérifie l'ajout côté entité
		Mockito.verify(userRepository).findById(userId);
	}

	@Test
	void addGuildToUser_UserNotFound_ThrowsException() {
		UUID userId = UUID.randomUUID();
		UUID guildId = UUID.randomUUID();

		Mockito.when(userRepository.findById(userId)).thenReturn(null);

		assertThrows(NoSuchElementException.class, () -> userService.addGuildToUser(userId, guildId));
	}
}