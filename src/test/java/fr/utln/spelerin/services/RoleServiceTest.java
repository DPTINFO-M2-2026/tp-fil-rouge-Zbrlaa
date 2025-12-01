package fr.utln.spelerin.services;

import fr.utln.spelerin.dto.RoleDTO;
import fr.utln.spelerin.dto.createupdatedto.RoleCreateUpdateDTO;
import fr.utln.spelerin.entities.Guild;
import fr.utln.spelerin.entities.Role;
import fr.utln.spelerin.repositories.ChannelRepository;
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
class RoleServiceTest {

	@Inject
	RoleService roleService;

	@InjectMock
	RoleRepository roleRepository;

	@InjectMock
	GuildRepository guildRepository;

	@InjectMock
	UserRepository userRepository;

	@InjectMock
	ChannelRepository channelRepository;

	@Test
	void createRole_Success() {
		UUID guildId = UUID.randomUUID();
		RoleCreateUpdateDTO dto = new RoleCreateUpdateDTO("Admin", 8L, guildId);

		Guild mockGuild = Guild.builder().name("Guild").build();
		mockGuild.setId(guildId);

		Mockito.when(guildRepository.findById(guildId)).thenReturn(mockGuild);
		Mockito.doAnswer(inv -> {
			Role r = inv.getArgument(0);
			r.setId(UUID.randomUUID());
			return null;
		}).when(roleRepository).persist(any(Role.class));

		RoleDTO result = roleService.createRole(dto);

		assertNotNull(result);
		assertEquals("Admin", result.name());
		assertEquals(guildId, result.guildId());
	}

	@Test
	void createRole_GuildNotFound_ThrowsException() {
		UUID guildId = UUID.randomUUID();
		RoleCreateUpdateDTO dto = new RoleCreateUpdateDTO("Admin", 8L, guildId);

		Mockito.when(guildRepository.findById(guildId)).thenReturn(null);

		assertThrows(NoSuchElementException.class, () -> roleService.createRole(dto));
		Mockito.verify(roleRepository, Mockito.never()).persist(any(Role.class));
	}
}