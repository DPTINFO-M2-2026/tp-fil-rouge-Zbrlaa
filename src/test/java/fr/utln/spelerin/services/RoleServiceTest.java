package fr.utln.spelerin.services;

import fr.utln.spelerin.dto.RoleDTO;
import fr.utln.spelerin.dto.createdto.RoleCreateDTO;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@QuarkusTest
class RoleServiceTest {

	// Faux IDs (Snowflakes) pour la stabilité des tests
	private static final String MOCK_GUILD_ID = "123456789012345678"; 
	private static final String MOCK_ROLE_ID = "246813579024681357"; // Nouveau Snowflake pour le rôle

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
		// ARRANGE
		// 1. DTO doit fournir le Snowflake du Rôle
		RoleCreateDTO dto = new RoleCreateDTO(MOCK_ROLE_ID, "Admin", 8L, MOCK_GUILD_ID);

		// 2. Mock de la Guilde parente
		Guild mockGuild = Guild.builder().name("Guild").build();
		mockGuild.setId(MOCK_GUILD_ID);

		// 3. Simuler la récupération de la Guilde
		Mockito.when(guildRepository.findById(MOCK_GUILD_ID)).thenReturn(mockGuild);
		
		// 4. Simplifier la simulation de persist. 
		// L'entité a déjà son ID fourni par le DTO, pas besoin de simuler la BDD.
		Mockito.doNothing().when(roleRepository).persist(any(Role.class));

		// ACT
		RoleDTO result = roleService.createRole(dto);

		// ASSERT
		assertNotNull(result);
		assertEquals("Admin", result.name());
		assertEquals(MOCK_GUILD_ID, result.guildId());
		// Vérifier que le bon ID a été utilisé
		assertEquals(MOCK_ROLE_ID, result.id()); 
		
		// Vérifier que la sauvegarde a été appelée une fois
		Mockito.verify(roleRepository, Mockito.times(1)).persist(any(Role.class));
	}

	@Test
	void createRole_GuildNotFound_ThrowsException() {
		// ARRANGE
		String unknownGuildId = "000000000000000001";
		// Le DTO doit toujours contenir un ID de Rôle valide pour l'initialisation
		RoleCreateDTO dto = new RoleCreateDTO(MOCK_ROLE_ID, "Admin", 8L, unknownGuildId);

		// Simuler la non-trouvaille de la guilde
		Mockito.when(guildRepository.findById(unknownGuildId)).thenReturn(null);

		// ACT & ASSERT
		assertThrows(NoSuchElementException.class, () -> roleService.createRole(dto));
		
		// Vérifier qu'on n'a JAMAIS sauvegardé le rôle
		Mockito.verify(roleRepository, Mockito.never()).persist(any(Role.class));
	}
}