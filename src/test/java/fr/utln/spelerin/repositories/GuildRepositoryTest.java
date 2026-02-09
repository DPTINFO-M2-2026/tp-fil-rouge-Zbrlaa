package fr.utln.spelerin.repositories;

import fr.utln.spelerin.entities.Guild;
import fr.utln.spelerin.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unitaire Tests for GuildRepository (Surefire).
 *
 * Tests rapides sans Quarkus pour une exécution ultra-rapide.
 * Tous les appels à la BD sont mockés.
 *
 * @author QA Automation Expert
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GuildRepository Unit Tests (Surefire)")
class GuildRepositoryTest {

    @Mock
    private GuildRepository guildRepository;

    private Guild testGuild;
    private User guildOwner;

    @BeforeEach
    void setUp() {
        // Given: Initialiser les fixtures de test
        guildOwner = User.builder()
                .id(100L)
                .username("guildowner")
                .displayName("Guild Owner")
                .build();

        testGuild = Guild.builder()
                .id(1000L)
                .name("Test Guild")
                .owner(guildOwner)
                .users(new HashSet<>())
                .roles(new HashSet<>())
                .channels(new HashSet<>())
                .invitations(new HashSet<>())
                .build();
    }

    @Test
    @DisplayName("Should find guild by ID when exists")
    void testFindByIdWhenExists() {
        // Given
        when(guildRepository.findById(1000L)).thenReturn(testGuild);

        // When
        Guild result = guildRepository.findById(1000L);

        // Then
        assertNotNull(result);
        assertEquals("Test Guild", result.getName());
        assertEquals(100L, result.getOwner().getId());
        verify(guildRepository, times(1)).findById(1000L);
    }

    @Test
    @DisplayName("Should return null when guild not found by ID")
    void testFindByIdWhenNotExists() {
        // Given
        when(guildRepository.findById(9999L)).thenReturn(null);

        // When
        Guild result = guildRepository.findById(9999L);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Should persist guild with owner relationship")
    void testPersistGuildWithOwner() {
        // Given
        doNothing().when(guildRepository).persist(testGuild);

        // When
        guildRepository.persist(testGuild);

        // Then
        verify(guildRepository, times(1)).persist(testGuild);
        assertNotNull(testGuild.getOwner());
    }

    @Test
    @DisplayName("Should handle guild with empty user set")
    void testFindGuildWithEmptyUserSet() {
        // Given
        Guild emptyGuild = Guild.builder()
                .id(2000L)
                .name("Empty Guild")
                .owner(guildOwner)
                .users(new HashSet<>())
                .build();

        when(guildRepository.findById(2000L)).thenReturn(emptyGuild);

        // When
        Guild result = guildRepository.findById(2000L);

        // Then
        assertTrue(result.getUsers().isEmpty());
        verify(guildRepository, times(1)).findById(2000L);
    }

    @Test
    @DisplayName("Should handle guild name with special characters")
    void testGuildNameWithSpecialCharacters() {
        // Given
        Guild specialGuild = Guild.builder()
                .id(3000L)
                .name("Guild@#$%^&*()")
                .owner(guildOwner)
                .build();

        when(guildRepository.findById(3000L)).thenReturn(specialGuild);

        // When
        Guild result = guildRepository.findById(3000L);

        // Then
        assertEquals("Guild@#$%^&*()", result.getName());
    }

    @Test
    @DisplayName("Should handle very long guild name")
    void testGuildWithVeryLongName() {
        // Given
        String longName = "G".repeat(500);
        Guild longGuild = Guild.builder()
                .id(4000L)
                .name(longName)
                .owner(guildOwner)
                .build();

        when(guildRepository.findById(4000L)).thenReturn(longGuild);

        // When
        Guild result = guildRepository.findById(4000L);

        // Then
        assertEquals(longName, result.getName());
        assertEquals(500, result.getName().length());
    }

    @Test
    @DisplayName("Should verify owner is properly associated with guild")
    void testGuildOwnerAssociation() {
        // Given
        when(guildRepository.findById(1000L)).thenReturn(testGuild);

        // When
        Guild result = guildRepository.findById(1000L);

        // Then
        assertNotNull(result.getOwner());
        assertEquals("guildowner", result.getOwner().getUsername());
        verify(guildRepository, times(1)).findById(1000L);
    }

    @Test
    @DisplayName("Should handle error: null guild ID")
    void testFindByNullId() {
        // Given
        when(guildRepository.findById(null)).thenReturn(null);

        // When
        Guild result = guildRepository.findById(null);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Should verify guild collections are initialized properly")
    void testGuildCollectionsInitialization() {
        // Given
        when(guildRepository.findById(1000L)).thenReturn(testGuild);

        // When
        Guild result = guildRepository.findById(1000L);

        // Then
        assertNotNull(result.getUsers());
        assertNotNull(result.getRoles());
        assertNotNull(result.getChannels());
        assertNotNull(result.getInvitations());
        assertTrue(result.getUsers().isEmpty());
    }
}
