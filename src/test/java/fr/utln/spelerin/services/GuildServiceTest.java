package fr.utln.spelerin.services;

import fr.utln.spelerin.dto.GuildDTO;
import fr.utln.spelerin.dto.createupdatedto.GuildCreateUpdateDTO;
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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@QuarkusTest
class GuildServiceTest {

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
		GuildCreateUpdateDTO dto = new GuildCreateUpdateDTO("My New Guild");

		Mockito.doAnswer(inv -> {
			Guild g = inv.getArgument(0);
			g.setId(UUID.randomUUID());
			return null;
		}).when(guildRepository).persist(any(Guild.class));

		GuildDTO result = guildService.createGuild(dto);

		assertNotNull(result);
		assertEquals("My New Guild", result.name());
	}

	@Test
	void addUserToGuild_Success() {
		UUID guildId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();

		Guild mockGuild = Guild.builder().name("G").build();
		mockGuild.setId(guildId);
		User mockUser = User.builder().username("u").displayName("U").build();
		mockUser.setId(userId);

		Mockito.when(guildRepository.findById(guildId)).thenReturn(mockGuild);
		Mockito.when(userRepository.findById(userId)).thenReturn(mockUser);

		GuildDTO result = guildService.addUserToGuild(guildId, userId);

		assertNotNull(result);
		assertTrue(mockGuild.getUsers().contains(mockUser));
	}
}