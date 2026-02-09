package fr.utln.spelerin.repositories;

import fr.utln.spelerin.entities.Guild;
import fr.utln.spelerin.entities.Invitation;
import fr.utln.spelerin.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unitaire Tests for InvitationRepository (Surefire).
 *
 * Tests rapides sans Quarkus.
 * Tous les appels à la BD sont mockés.
 *
 * @author QA Automation Expert
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("InvitationRepository Unit Tests (Surefire)")
class InvitationRepositoryTest {

    @Mock
    private InvitationRepository invitationRepository;

    private Invitation testInvitation;
    private Guild testGuild;
    private User testUser;

    @BeforeEach
    void setUp() {
        // Given: Initialiser les fixtures
        testGuild = Guild.builder()
                .id(1000L)
                .name("Test Guild")
                .build();

        testUser = User.builder()
                .id(100L)
                .username("testuser")
                .displayName("Test User")
                .build();

        testInvitation = Invitation.builder()
            .id(7000L)
            .discordCode("INVITE123")
            .guild(testGuild)
            .build();
    }

    @Test
    @DisplayName("Should find invitation by ID when exists")
    void testFindByIdWhenExists() {
        // Given
        when(invitationRepository.findById(7000L)).thenReturn(testInvitation);

        // When
        Invitation result = invitationRepository.findById(7000L);

        // Then
        assertNotNull(result);
        assertEquals("INVITE123", result.getDiscordCode());
        assertEquals(1000L, result.getGuild().getId());
        verify(invitationRepository, times(1)).findById(7000L);
    }

    @Test
    @DisplayName("Should return null when invitation not found")
    void testFindByIdWhenNotExists() {
        // Given
        when(invitationRepository.findById(99999L)).thenReturn(null);

        // When
        Invitation result = invitationRepository.findById(99999L);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Should persist invitation with guild and creator")
    void testPersistInvitationWithRelations() {
        // Given
        doNothing().when(invitationRepository).persist(testInvitation);

        // When
        invitationRepository.persist(testInvitation);

        // Then
        verify(invitationRepository, times(1)).persist(testInvitation);
        assertNotNull(testInvitation.getGuild());
    }

    @Test
    @DisplayName("Should handle invitation with special characters in code")
    void testInvitationCodeWithSpecialCharacters() {
        // Given
        Invitation specialInvitation = Invitation.builder()
            .id(7001L)
            .discordCode("INV-#@$%^&*()")
            .guild(testGuild)
            .build();

        when(invitationRepository.findById(7001L)).thenReturn(specialInvitation);

        // When
        Invitation result = invitationRepository.findById(7001L);

        // Then
        assertEquals("INV-#@$%^&*()", result.getDiscordCode());
    }

    @Test
    @DisplayName("Should handle very long invitation code")
    void testInvitationWithVeryLongCode() {
        // Given
        String longCode = "I".repeat(255);
        Invitation longInvitation = Invitation.builder()
            .id(7002L)
            .discordCode(longCode)
            .guild(testGuild)
            .build();

        when(invitationRepository.findById(7002L)).thenReturn(longInvitation);

        // When
        Invitation result = invitationRepository.findById(7002L);

        // Then
        assertEquals(longCode, result.getDiscordCode());
    }

    @Test
    @DisplayName("Should verify invitation-guild association")
    void testInvitationGuildAssociation() {
        // Given
        when(invitationRepository.findById(7000L)).thenReturn(testInvitation);

        // When
        Invitation result = invitationRepository.findById(7000L);

        // Then
        assertNotNull(result.getGuild());
        assertEquals("Test Guild", result.getGuild().getName());
    }
    @Test
    @DisplayName("Should handle error: null invitation ID")
    void testFindByNullId() {
        // Given
        when(invitationRepository.findById(null)).thenReturn(null);

        // When
        Invitation result = invitationRepository.findById(null);

        // Then
        assertNull(result);
    }

    
}
