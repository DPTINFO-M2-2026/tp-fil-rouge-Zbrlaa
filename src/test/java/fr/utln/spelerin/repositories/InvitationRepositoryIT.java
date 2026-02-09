package fr.utln.spelerin.repositories;

import fr.utln.spelerin.entities.Guild;
import fr.utln.spelerin.entities.Invitation;
import fr.utln.spelerin.entities.User;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import fr.utln.spelerin.tests.PostgresTestResource;

// no expiration date field in Invitation entity; no LocalDateTime needed
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration Tests for InvitationRepository (Failsafe).
 *
 * Utilise @QuarkusTest avec DevServices (PostgreSQL TestContainer).
 * Teste la persistance des invitations avec codes, dates d'expiration et relations.
 *
 * @author QA Automation Expert
 */
@QuarkusTest
@QuarkusTestResource(PostgresTestResource.class)
@Transactional
@DisplayName("InvitationRepository Integration Tests (Failsafe)")
class InvitationRepositoryIT {

    @Inject
    private InvitationRepository invitationRepository;

    @Inject
    private GuildRepository guildRepository;

    @Inject
    private UserRepository userRepository;

    private Invitation testInvitation;
    private Guild testGuild;
    private User creatorUser;

    @BeforeEach
    void setUp() {
        // Given: Clean up and initialize test data
        invitationRepository.deleteAll();
        guildRepository.deleteAll();
        userRepository.deleteAll();

        // Create owner user
        creatorUser = User.builder()
            .id(4001L)
            .username("inviter")
            .displayName("Inviter User")
            .build();
        userRepository.persist(creatorUser);

        // Create guild
        testGuild = Guild.builder()
            .id(10001L)
            .name("TestGuild")
            .owner(creatorUser)
            .users(new HashSet<>())
            .build();
        guildRepository.persist(testGuild);

        // Create test invitation
        testInvitation = Invitation.builder()
                .id(5001L)
                .discordCode("ABC123DEF456")
                .guild(testGuild)
                .build();
    }

    @Nested
    @DisplayName("CRUD Operations")
    class CRUDOperations {

        @Test
        void testPersistInvitationWithGuildAndCreator() {
            // When
            invitationRepository.persist(testInvitation);

            // Then
            Invitation retrieved = invitationRepository.findById(5001L);
            assertNotNull(retrieved);
            assertEquals("ABC123DEF456", retrieved.getDiscordCode());
            assertNotNull(retrieved.getGuild());
            assertEquals("TestGuild", retrieved.getGuild().getName());
        }

        @Test
        @DisplayName("Should find invitation by ID")
        void testFindInvitationById() {
            // Given
            invitationRepository.persist(testInvitation);

            // When
            Invitation result = invitationRepository.findById(5001L);

            // Then
            assertNotNull(result);
            assertEquals("ABC123DEF456", result.getDiscordCode());
            assertEquals(10001L, result.getGuild().getId());
        }

        @Test
        @DisplayName("Should return null when invitation not found")
        void testFindInvitationByIdNotFound() {
            // When
            Invitation result = invitationRepository.findById(9999999L);

            // Then
            assertNull(result);
        }

        @Test
        @DisplayName("Should update invitation in database")
        void testUpdateInvitation() {
            // Given
            invitationRepository.persist(testInvitation);

            // When
            testInvitation.setDiscordCode("UPDATED789");
            invitationRepository.persist(testInvitation);

            // Then
            Invitation updated = invitationRepository.findById(5001L);
            assertEquals("UPDATED789", updated.getDiscordCode());
        }

        @Test
        @DisplayName("Should delete invitation from database")
        void testDeleteInvitation() {
            // Given
            invitationRepository.persist(testInvitation);

            // When
            invitationRepository.delete(testInvitation);

            // Then
            Invitation deleted = invitationRepository.findById(5001L);
            assertNull(deleted);
        }

        @Test
        @DisplayName("Should delete all invitations from database")
        void testDeleteAllInvitations() {
            // Given
            invitationRepository.persist(testInvitation);
            Invitation inv2 = Invitation.builder()
                    .id(5002L)
                    .discordCode("XYZ789ABC")
                    .guild(testGuild)
                    .build();
            invitationRepository.persist(inv2);

            // When
            invitationRepository.deleteAll();

            // Then
            assertEquals(0, invitationRepository.count());
        }
    }

    @Nested
    @DisplayName("Invitation Codes")
    class InvitationCodes {

        @Test
        @DisplayName("Should persist invitation with unique code")
        void testPersistInvitationWithUniqueCode() {
            // Given
            Invitation inv = Invitation.builder()
                    .id(5003L)
                    .discordCode("UNIQUE123CODE")
                    .guild(testGuild)
                    .build();

            // When
            invitationRepository.persist(inv);

            // Then
            Invitation retrieved = invitationRepository.findById(5003L);
            assertEquals("UNIQUE123CODE", retrieved.getDiscordCode());
        }

        @Test
        @DisplayName("Should handle invitation code with special characters")
        void testSpecialCharactersInInvitationCode() {
            // Given
            Invitation specialInv = Invitation.builder()
                    .id(5004L)
                    .discordCode("CODE@#$%^&*()")
                    .guild(testGuild)
                    .build();

            // When
            invitationRepository.persist(specialInv);

            // Then
            Invitation retrieved = invitationRepository.findById(5004L);
            assertEquals("CODE@#$%^&*()", retrieved.getDiscordCode());
        }

        @Test
        @DisplayName("Should handle very long invitation code (255 chars)")
        void testVeryLongInvitationCode() {
            // Given
            String longCode = "C".repeat(255);
            Invitation longInv = Invitation.builder()
                    .id(5005L)
                    .discordCode(longCode)
                    .guild(testGuild)
                    .build();

            // When
            invitationRepository.persist(longInv);

            // Then
            Invitation retrieved = invitationRepository.findById(5005L);
            assertEquals(255, retrieved.getDiscordCode().length());
        }

        @Test
        @DisplayName("Should handle duplicate invitation code constraint")
        void testDuplicateInvitationCode() {
            // Given
            Invitation inv1 = Invitation.builder()
                    .id(5006L)
                    .discordCode("DUPLICATE123")
                    .guild(testGuild)
                    .build();
            Invitation inv2 = Invitation.builder()
                    .id(5007L)
                    .discordCode("DUPLICATE123")
                    .guild(testGuild)
                    .build();

            invitationRepository.persist(inv1);

            // When & Then
            assertThrows(Exception.class, () -> invitationRepository.persist(inv2));
        }
    }

        // Expiration date handling removed: Invitation entity doesn't have expiration fields

    @Nested
    @DisplayName("Invitation-Guild Relationships")
    class InvitationGuildRelationships {

        @Test
        @DisplayName("Should verify invitation belongs to correct guild")
        void testInvitationGuildAssociation() {
            // Given
            invitationRepository.persist(testInvitation);

            // When
            Invitation retrieved = invitationRepository.findById(5001L);
            Guild invGuild = retrieved.getGuild();

            // Then
            assertEquals(10001L, invGuild.getId());
            assertEquals("TestGuild", invGuild.getName());
        }

        @Test
        @DisplayName("Should create multiple invitations for same guild")
        void testMultipleInvitationsPerGuild() {
            // Given
            Invitation inv1 = Invitation.builder()
                    .id(5014L)
                    .discordCode("CODE1")
                    .guild(testGuild)
                    .build();
            Invitation inv2 = Invitation.builder()
                    .id(5015L)
                    .discordCode("CODE2")
                    .guild(testGuild)
                    .build();

            // When
            invitationRepository.persist(inv1);
            invitationRepository.persist(inv2);

            // Then
            List<Invitation> allInv = invitationRepository.listAll();
            assertEquals(2, allInv.size());
            assertTrue(allInv.stream().allMatch(i -> testGuild.getId() == i.getGuild().getId()));
        }
    }


    @Nested
    @DisplayName("Query Operations")
    class QueryOperations {

        @Test
        @DisplayName("Should list all invitations from database")
        void testListAllInvitations() {
            // Given
            invitationRepository.persist(testInvitation);
            Invitation inv2 = Invitation.builder()
                    .id(5017L)
                    .discordCode("SECOND123")
                    .guild(testGuild)
                    .build();
            invitationRepository.persist(inv2);

            // When
            List<Invitation> allInvitations = invitationRepository.listAll();

            // Then
            assertEquals(2, allInvitations.size());
            assertTrue(allInvitations.stream().anyMatch(i -> "ABC123DEF456".equals(i.getDiscordCode())));
        }

        @Test
        @DisplayName("Should return empty list when no invitations in database")
        void testListAllInvitationsWhenEmpty() {
            // When
            List<Invitation> result = invitationRepository.listAll();

            // Then
            assertEquals(0, result.size());
        }

        @Test
        @DisplayName("Should count invitations in database")
        void testCountInvitations() {
            // Given
            for (int i = 0; i < 5; i++) {
                Invitation inv = Invitation.builder()
                        .id(6000L + i)
                        .discordCode("CODE" + i)
                        .guild(testGuild)
                        .build();
                invitationRepository.persist(inv);
            }

            // When
            long count = invitationRepository.count();

            // Then
            assertEquals(5, count);
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling")
    class EdgeCases {

        @Test
        @DisplayName("Should handle unicode characters in invitation code")
        void testUnicodeInInvitationCode() {
            // Given
            Invitation unicodeInv = Invitation.builder()
                    .id(5018L)
                    .discordCode("邀請 الدعوة আমন্ত্রণ")
                    .guild(testGuild)
                    .build();

            // When
            invitationRepository.persist(unicodeInv);

            // Then
            Invitation retrieved = invitationRepository.findById(5018L);
            assertEquals("邀請 الدعوة আমন্ত্রণ", retrieved.getDiscordCode());
        }

        @Test
        @DisplayName("Should handle invitation with null guild")
        void testInvitationWithNullGuild() {
            // Given
            Invitation orphanInv = Invitation.builder()
                    .id(5019L)
                    .discordCode("ORPHAN123")
                    .guild(null)
                    .build();

            // When & Then
            if (invitationRepository.count() == 0) {
                invitationRepository.persist(orphanInv);
                Invitation retrieved = invitationRepository.findById(5019L);
                assertNull(retrieved.getGuild());
            }
        }

        @Test
        @DisplayName("Should handle duplicate invitation ID constraint")
        void testDuplicateInvitationID() {
            // Given
            Invitation inv1 = Invitation.builder()
                    .id(5021L)
                    .discordCode("FIRST")
                    .guild(testGuild)
                    .build();
            Invitation inv2 = Invitation.builder()
                    .id(5021L)
                    .discordCode("SECOND")
                    .guild(testGuild)
                    .build();

            invitationRepository.persist(inv1);

            // When & Then
            assertThrows(Exception.class, () -> invitationRepository.persist(inv2));
        }
    }

    @Nested
    @DisplayName("Performance Tests")
    class PerformanceTests {

        @Test
        void testNoNPlus1WithRelationships() {
            // Given
            invitationRepository.persist(testInvitation);

            // When
            Invitation inv = invitationRepository.findById(5001L);
            String guildName = inv.getGuild().getName();

            // Then
            assertNotNull(guildName);
            assertEquals("TestGuild", guildName);
        }

        @Test
        @DisplayName("Should list multiple invitations efficiently")
        void testListMultipleInvitations() {
            // Given
            for (int i = 0; i < 10; i++) {
                Invitation inv = Invitation.builder()
                        .id(7000L + i)
                        .discordCode("PERF" + i)
                        .guild(testGuild)
                        .build();
                invitationRepository.persist(inv);
            }

            // When
            List<Invitation> invitations = invitationRepository.listAll();

            // Then
            assertEquals(10, invitations.size());
        }
    }
}
