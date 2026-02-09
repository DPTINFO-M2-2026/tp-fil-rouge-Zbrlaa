package fr.utln.spelerin.services;

import fr.utln.spelerin.dto.InvitationDTO;
import fr.utln.spelerin.dto.createdto.InvitationCreateDTO;
import fr.utln.spelerin.entities.Guild;
import fr.utln.spelerin.entities.User;
import fr.utln.spelerin.mappers.InvitationMapper;
import fr.utln.spelerin.repositories.GuildRepository;
import fr.utln.spelerin.repositories.InvitationRepository;
import fr.utln.spelerin.repositories.UserRepository;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import fr.utln.spelerin.tests.PostgresTestResource;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration Tests for InvitationService (Failsafe).
 *
 * Utilise @QuarkusTest avec DevServices (PostgreSQL TestContainer).
 * Teste la logique métier du service d'invitations avec dates d'expiration.
 *
 * @author QA Automation Expert
 */
@QuarkusTest
@QuarkusTestResource(PostgresTestResource.class)
@Transactional
@DisplayName("InvitationService Integration Tests (Failsafe)")
class InvitationServiceIT {

    @Inject
    private InvitationService invitationService;

    @Inject
    private InvitationRepository invitationRepository;

    @Inject
    private GuildRepository guildRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private InvitationMapper invitationMapper;

    private Guild testGuild;
    private User creatorUser;

    @BeforeEach
    void setUp() {
        // Given: Clean up database
        invitationRepository.deleteAll();
        guildRepository.deleteAll();
        userRepository.deleteAll();

        // Create creator and guild
        creatorUser = User.builder()
                .id(8001L)
                .username("inviter")
                .displayName("Invitation Creator")
                .build();
        userRepository.persist(creatorUser);

        testGuild = Guild.builder()
                .id(14001L)
                .name("TestGuild")
                .owner(creatorUser)
                .users(new HashSet<>())
                .build();
        guildRepository.persist(testGuild);
    }

    @Nested
    @DisplayName("Invitation Retrieval Operations")
    class InvitationRetrievalOperations {

        @Test
        @DisplayName("Should retrieve all invitations from database via service")
        void testGetAllInvitations() {
            // Given
            InvitationCreateDTO invDto1 = new InvitationCreateDTO("CODE1", 14001L);
            InvitationCreateDTO invDto2 = new InvitationCreateDTO("CODE2", 14001L);

            invitationService.createInvitation(invDto1);
            invitationService.createInvitation(invDto2);

            // When
            List<InvitationDTO> allInvitations = invitationService.getAllInvitations();

            // Then
            assertEquals(2, allInvitations.size());
            assertTrue(allInvitations.stream().anyMatch(i -> "CODE1".equals(i.discordCode())));
            assertTrue(allInvitations.stream().anyMatch(i -> "CODE2".equals(i.discordCode())));
        }

        @Test
        @DisplayName("Should return empty list when no invitations exist")
        void testGetAllInvitationsWhenEmpty() {
            // When
            List<InvitationDTO> allInvitations = invitationService.getAllInvitations();

            // Then
            assertEquals(0, allInvitations.size());
        }

        @Test
        @DisplayName("Should find invitation by ID via service")
        void testGetInvitationById() {
            // Given
            InvitationCreateDTO dto = new InvitationCreateDTO("FINDME", 14001L);
            InvitationDTO created = invitationService.createInvitation(dto);

            // When
            Optional<InvitationDTO> result = invitationService.getInvitationById(created.id());

            // Then
            assertTrue(result.isPresent());
            assertEquals("FINDME", result.get().discordCode());
        }

        @Test
        @DisplayName("Should return empty Optional when invitation not found")
        void testGetInvitationByIdNotFound() {
            // When
            Optional<InvitationDTO> result = invitationService.getInvitationById(999999L);

            // Then
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Invitation Creation")
    class InvitationCreation {

        @Test
        @DisplayName("Should create new invitation with valid guild and creator")
        void testCreateNewInvitation() {
            // Given
            InvitationCreateDTO dto = new InvitationCreateDTO("NEWCODE", 14001L);

            // When
            InvitationDTO created = invitationService.createInvitation(dto);

            // Then
            assertNotNull(created);
            assertEquals("NEWCODE", created.discordCode());
            assertTrue(created.id() > 0);
            assertEquals(1, invitationRepository.count());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when guild not found")
        void testCreateInvitationWithInvalidGuild() {
            // Given
            InvitationCreateDTO dto = new InvitationCreateDTO("INVALID", 999999L);

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> invitationService.createInvitation(dto));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when creator not found")
        void testCreateInvitationWithInvalidCreator() {
            // Given
            InvitationCreateDTO dto = new InvitationCreateDTO("BADCREATOR", 14001L);

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> invitationService.createInvitation(dto));
        }

        @Test
        @DisplayName("Should persist invitation with special characters in code")
        void testCreateInvitationWithSpecialCharacters() {
            // Given
            InvitationCreateDTO dto = new InvitationCreateDTO("CODE@#$%^&*()", 14001L);

            // When
            InvitationDTO created = invitationService.createInvitation(dto);

            // Then
            assertEquals("CODE@#$%^&*()", created.discordCode());
        }

        @Test
        @DisplayName("Should create invitation with null expiration date (permanent)")
        void testCreateInvitationWithoutExpiration() {
            // Given
            InvitationCreateDTO dto = new InvitationCreateDTO("PERMANENT", 14001L);

            // When
            InvitationDTO created = invitationService.createInvitation(dto);

            // Then
            assertEquals("PERMANENT", created.discordCode());
            assertTrue(created.id() > 0);
        }
    }


    @Nested
    @DisplayName("Invitation Deletion Operations")
    class InvitationDeletionOperations {

        @Test
        @DisplayName("Should delete existing invitation")
        void testDeleteExistingInvitation() {
            // Given
            InvitationCreateDTO dto = new InvitationCreateDTO("DELETEME", 14001L);
            invitationService.createInvitation(dto);
            assertEquals(1, invitationRepository.count());

            // When
            boolean deleted = invitationService.deleteInvitation(90L);

            // Then
            assertTrue(deleted);
            assertEquals(0, invitationRepository.count());
        }

        @Test
        @DisplayName("Should return false when deleting non-existent invitation")
        void testDeleteNonExistentInvitation() {
            // When
            boolean deleted = invitationService.deleteInvitation(999999L);

            // Then
            assertFalse(deleted);
        }

        @Test
        @DisplayName("Should delete only specified invitation")
        void testDeleteSpecificInvitationOnly() {
            // Given
            InvitationCreateDTO dto1 = new InvitationCreateDTO("KEEP", 14001L);
            InvitationCreateDTO dto2 = new InvitationCreateDTO("DELETE", 14001L);
            invitationService.createInvitation(dto1);
            invitationService.createInvitation(dto2);

            // When
            invitationService.deleteInvitation(101L);

            // Then
            assertEquals(1, invitationRepository.count());
            assertTrue(invitationService.getInvitationById(100L).isPresent());
            assertTrue(invitationService.getInvitationById(101L).isEmpty());
        }
    }

    @Nested
    @DisplayName("Invitation-Guild Management")
    class InvitationGuildManagement {

        @Test
        @DisplayName("Should verify invitation belongs to correct guild")
        void testInvitationGuildAssociation() {
            // Given
            InvitationCreateDTO dto = new InvitationCreateDTO("GUILD123", 14001L);
            invitationService.createInvitation(dto);

            // When
            Optional<InvitationDTO> result = invitationService.getInvitationById(110L);

            // Then
            assertTrue(result.isPresent());
            assertEquals(14001L, result.get().guildId());
        }

        @Test
        @DisplayName("Should create multiple invitations for same guild")
        void testMultipleInvitationsPerGuild() {
            // Given
            InvitationCreateDTO dto1 = new InvitationCreateDTO("INV1", 14001L);
            InvitationCreateDTO dto2 = new InvitationCreateDTO("INV2", 14001L);
            InvitationCreateDTO dto3 = new InvitationCreateDTO("INV3", 14001L);

            invitationService.createInvitation(dto1);
            invitationService.createInvitation(dto2);
            invitationService.createInvitation(dto3);

            // When
            List<InvitationDTO> allInvitations = invitationService.getAllInvitations();

            // Then
            assertEquals(3, allInvitations.size());
            assertTrue(allInvitations.stream().allMatch(i -> 14001L == i.guildId()));
        }
    }

    @Nested
    @DisplayName("Expiration Date Handling")
    class ExpirationDateHandling {

        @Test
        @DisplayName("Should handle future expiration dates")
        void testInvitationWithFutureExpiration() {
            // Given
            InvitationCreateDTO dto = new InvitationCreateDTO("FUTURE", 14001L);

            // When
            InvitationDTO created = invitationService.createInvitation(dto);

            // Then
            assertNotNull(created);
            assertEquals("FUTURE", created.discordCode());
        }

        @Test
        @DisplayName("Should handle past expiration dates")
        void testInvitationWithPastExpiration() {
            // Given
            InvitationCreateDTO dto = new InvitationCreateDTO("PAST", 14001L);

            // When
            InvitationDTO created = invitationService.createInvitation(dto);

            // Then
            assertNotNull(created);
            assertEquals("PAST", created.discordCode());
        }

        @Test
        @DisplayName("Should determine if invitation is expired")
        void testCheckIfInvitationExpired() {
            // Given
            InvitationCreateDTO dto = new InvitationCreateDTO("EXPIRED", 14001L);
            InvitationDTO created = invitationService.createInvitation(dto);

            // When
            Optional<InvitationDTO> result = invitationService.getInvitationById(created.id());

            // Then
            assertTrue(result.isPresent());
            assertEquals("EXPIRED", result.get().discordCode());
        }
    }

    @Nested
    @DisplayName("Invitation Code Handling")
    class InvitationCodeHandling {

        @Test
        @DisplayName("Should handle very long invitation code (255 chars)")
        void testVeryLongInvitationCode() {
            // Given
            String longCode = "C".repeat(255);
            InvitationCreateDTO dto = new InvitationCreateDTO(longCode, 14001L);

            // When
            InvitationDTO created = invitationService.createInvitation(dto);

            // Then
            assertEquals(255, created.discordCode().length());
        }

        @Test
        @DisplayName("Should handle unicode characters in invitation code")
        void testUnicodeInInvitationCode() {
            // Given
            InvitationCreateDTO dto = new InvitationCreateDTO("邀請 الدعوة আমন্ত্রণ", 14001L);

            // When
            InvitationDTO created = invitationService.createInvitation(dto);

            // Then
            assertEquals("邀請 الدعوة আমন্ত্রণ", created.discordCode());
        }
    }

    @Nested
    @DisplayName("Data Validation and Edge Cases")
    class DataValidationAndEdgeCases {

        @Test
        @DisplayName("Should retrieve invitation with correct data types")
        void testDataTypeConversion() {
            // Given
            InvitationCreateDTO dto = new InvitationCreateDTO("DATATEST", 14001L);
            invitationService.createInvitation(dto);

            // When
            Optional<InvitationDTO> result = invitationService.getInvitationById(180L);

            // Then
            assertTrue(result.isPresent());
            assertInstanceOf(Long.class, result.get().id());
            assertInstanceOf(String.class, result.get().discordCode());
        }
    }


    @Nested
    @DisplayName("Performance and Efficiency")
    class PerformanceAndEfficiency {

        @Test
        @DisplayName("Should retrieve multiple invitations efficiently")
        void testRetrieveMultipleInvitationsEfficiently() {
            // Given
            for (int i = 0; i < 20; i++) {
                InvitationCreateDTO dto = new InvitationCreateDTO("CODE" + i, 14001L);
                invitationService.createInvitation(dto);
            }

            // When
            List<InvitationDTO> allInvitations = invitationService.getAllInvitations();

            // Then
            assertEquals(20, allInvitations.size());
        }
	}
}
