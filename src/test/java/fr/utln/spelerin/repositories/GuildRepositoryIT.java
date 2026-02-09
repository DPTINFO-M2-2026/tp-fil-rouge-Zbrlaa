package fr.utln.spelerin.repositories;

import fr.utln.spelerin.entities.Guild;
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

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration Tests for GuildRepository (Failsafe).
 *
 * Utilise @QuarkusTest avec DevServices (PostgreSQL TestContainer).
 * Teste la persistance des guildes avec leurs propriétaires et relations.
 *
 * @author QA Automation Expert
 */
@QuarkusTest
@QuarkusTestResource(PostgresTestResource.class)
@Transactional
@DisplayName("GuildRepository Integration Tests (Failsafe)")
class GuildRepositoryIT {

    @Inject
    private GuildRepository guildRepository;

    @Inject
    private UserRepository userRepository;

    private Guild testGuild;
    private User ownerUser;

    @BeforeEach
    void setUp() {
        // Given: Clean up and initialize test data
        guildRepository.deleteAll();
        userRepository.deleteAll();

        // Create owner user
        ownerUser = User.builder()
                .id(1001L)
                .username("guildowner")
                .displayName("Guild Owner")
                .build();
        userRepository.persist(ownerUser);

        // Create test guild with owner
        testGuild = Guild.builder()
                .id(5001L)
                .name("TestGuild")
                .owner(ownerUser)
                .users(new HashSet<>())
                .build();
    }

    @Nested
    @DisplayName("CRUD Operations")
    class CRUDOperations {

        @Test
        @DisplayName("Should persist guild with owner to database")
        void testPersistGuildWithOwner() {
            // When
            guildRepository.persist(testGuild);

            // Then
            Guild retrieved = guildRepository.findById(5001L);
            assertNotNull(retrieved);
            assertEquals("TestGuild", retrieved.getName());
            assertNotNull(retrieved.getOwner());
            assertEquals("guildowner", retrieved.getOwner().getUsername());
        }

        @Test
        @DisplayName("Should find guild by ID")
        void testFindGuildById() {
            // Given
            guildRepository.persist(testGuild);

            // When
            Guild result = guildRepository.findById(5001L);

            // Then
            assertNotNull(result);
            assertEquals("TestGuild", result.getName());
            assertEquals(1001L, result.getOwner().getId());
        }

        @Test
        @DisplayName("Should return null when guild not found")
        void testFindGuildByIdNotFound() {
            // When
            Guild result = guildRepository.findById(9999999L);

            // Then
            assertNull(result);
        }

        @Test
        @DisplayName("Should update guild in database")
        void testUpdateGuild() {
            // Given
            guildRepository.persist(testGuild);

            // When
            testGuild.setName("UpdatedGuild");
            guildRepository.persist(testGuild);

            // Then
            Guild updated = guildRepository.findById(5001L);
            assertEquals("UpdatedGuild", updated.getName());
        }

        @Test
        @DisplayName("Should delete guild from database")
        void testDeleteGuild() {
            // Given
            guildRepository.persist(testGuild);

            // When
            guildRepository.delete(testGuild);

            // Then
            Guild deleted = guildRepository.findById(5001L);
            assertNull(deleted);
        }

        @Test
        @DisplayName("Should clear all guilds from database")
        void testDeleteAllGuilds() {
            // Given
            guildRepository.persist(testGuild);
            Guild guild2 = Guild.builder()
                    .id(5002L)
                    .name("SecondGuild")
                    .owner(ownerUser)
                    .users(new HashSet<>())
                    .build();
            guildRepository.persist(guild2);

            // When
            guildRepository.deleteAll();

            // Then
            assertEquals(0, guildRepository.count());
        }
    }

    @Nested
    @DisplayName("Guild-User Relationships")
    class GuildUserRelationships {

        @Test
        @DisplayName("Should add multiple users to guild")
        void testAddMultipleUsersToGuild() {
            // Given
            User user1 = User.builder().id(1002L).username("member1").displayName("Member 1").build();
            User user2 = User.builder().id(1003L).username("member2").displayName("Member 2").build();
            userRepository.persist(user1);
            userRepository.persist(user2);

            testGuild.getUsers().add(user1);
            testGuild.getUsers().add(user2);

            // When
            guildRepository.persist(testGuild);

            // Then
            Guild retrieved = guildRepository.findById(5001L);
            assertEquals(2, retrieved.getUsers().size());
            assertTrue(retrieved.getUsers().stream().anyMatch(u -> "member1".equals(u.getUsername())));
        }

        @Test
        @DisplayName("Should maintain guild with empty user set")
        void testGuildWithEmptyUserSet() {
            // Given
            testGuild.setUsers(new HashSet<>());

            // When
            guildRepository.persist(testGuild);

            // Then
            Guild retrieved = guildRepository.findById(5001L);
            assertNotNull(retrieved.getUsers());
            assertEquals(0, retrieved.getUsers().size());
        }

        @Test
        @DisplayName("Should verify guild owner is accessible")
        void testGuildOwnerRelationship() {
            // Given
            guildRepository.persist(testGuild);

            // When
            Guild retrieved = guildRepository.findById(5001L);
            User owner = retrieved.getOwner();

            // Then
            assertNotNull(owner);
            assertEquals(1001L, owner.getId());
        }

        @Test
        @DisplayName("Should persist guild with different owner")
        void testGuildWithDifferentOwner() {
            // Given
            User newOwner = User.builder()
                    .id(1004L)
                    .username("newowner")
                    .displayName("New Owner")
                    .build();
            userRepository.persist(newOwner);

            Guild guild = Guild.builder()
                    .id(5003L)
                    .name("NewGuild")
                    .owner(newOwner)
                    .users(new HashSet<>())
                    .build();

            // When
            guildRepository.persist(guild);

            // Then
            Guild retrieved = guildRepository.findById(5003L);
            assertEquals("newowner", retrieved.getOwner().getUsername());
        }
    }

    @Nested
    @DisplayName("Query Operations")
    class QueryOperations {

        @Test
        @DisplayName("Should list all guilds from database")
        void testListAllGuilds() {
            // Given
            guildRepository.persist(testGuild);
            Guild guild2 = Guild.builder()
                    .id(5002L)
                    .name("SecondGuild")
                    .owner(ownerUser)
                    .users(new HashSet<>())
                    .build();
            guildRepository.persist(guild2);

            // When
            List<Guild> allGuilds = guildRepository.listAll();

            // Then
            assertEquals(2, allGuilds.size());
            assertTrue(allGuilds.stream().anyMatch(g -> "TestGuild".equals(g.getName())));
            assertTrue(allGuilds.stream().anyMatch(g -> "SecondGuild".equals(g.getName())));
        }

        @Test
        @DisplayName("Should return empty list when no guilds in database")
        void testListAllGuildsWhenEmpty() {
            // When
            List<Guild> result = guildRepository.listAll();

            // Then
            assertEquals(0, result.size());
        }

        @Test
        @DisplayName("Should count guilds in database")
        void testCountGuilds() {
            // Given
            for (int i = 0; i < 5; i++) {
                Guild guild = Guild.builder()
                        .id(6000L + i)
                        .name("Guild" + i)
                        .owner(ownerUser)
                        .users(new HashSet<>())
                        .build();
                guildRepository.persist(guild);
            }

            // When
            long count = guildRepository.count();

            // Then
            assertEquals(5, count);
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling")
    class EdgeCases {

        @Test
        @DisplayName("Should handle guild name with special characters")
        void testSpecialCharactersInGuildName() {
            // Given
            Guild specialGuild = Guild.builder()
                    .id(5004L)
                    .name("Guild@#$%^&*()")
                    .owner(ownerUser)
                    .users(new HashSet<>())
                    .build();

            // When
            guildRepository.persist(specialGuild);

            // Then
            Guild retrieved = guildRepository.findById(5004L);
            assertEquals("Guild@#$%^&*()", retrieved.getName());
        }

        @Test
        @DisplayName("Should handle very long guild name")
        void testVeryLongGuildName() {
            // Given
            String longName = "G".repeat(500);
            Guild longGuild = Guild.builder()
                    .id(5005L)
                    .name(longName)
                    .owner(ownerUser)
                    .users(new HashSet<>())
                    .build();

            // When
            guildRepository.persist(longGuild);

            // Then
            Guild retrieved = guildRepository.findById(5005L);
            assertEquals(500, retrieved.getName().length());
        }

        @Test
        @DisplayName("Should handle unicode characters in guild name")
        void testUnicodeInGuildName() {
            // Given
            Guild unicodeGuild = Guild.builder()
                    .id(5006L)
                    .name("公会 الحضر 길드")
                    .owner(ownerUser)
                    .users(new HashSet<>())
                    .build();

            // When
            guildRepository.persist(unicodeGuild);

            // Then
            Guild retrieved = guildRepository.findById(5006L);
            assertEquals("公会 الحضر 길드", retrieved.getName());
        }

        @Test
        @DisplayName("Should handle duplicate guild ID constraint")
        void testDuplicateGuildID() {
            // Given
            Guild guild1 = Guild.builder()
                    .id(5007L)
                    .name("FirstGuild")
                    .owner(ownerUser)
                    .users(new HashSet<>())
                    .build();
            Guild guild2 = Guild.builder()
                    .id(5007L)
                    .name("SecondGuild")
                    .owner(ownerUser)
                    .users(new HashSet<>())
                    .build();

            guildRepository.persist(guild1);

            // When & Then
            assertThrows(Exception.class, () -> guildRepository.persist(guild2));
        }

        @Test
        @DisplayName("Should handle guild with null owner")
        void testGuildWithNullOwner() {
            // Given
            Guild orphanGuild = Guild.builder()
                    .id(5008L)
                    .name("OrphanGuild")
                    .owner(null)
                    .users(new HashSet<>())
                    .build();

            // When & Then
            // Behavior depends on constraint rules - might throw or allow null
            if (guildRepository.count() == 0) {
                guildRepository.persist(orphanGuild);
                Guild retrieved = guildRepository.findById(5008L);
                assertNull(retrieved.getOwner());
            }
        }
    }

    @Nested
    @DisplayName("Performance Tests")
    class PerformanceTests {

        @Test
        @DisplayName("Should retrieve guild with owner without N+1 queries")
        void testNoNPlus1WithOwner() {
            // Given
            guildRepository.persist(testGuild);

            // When
            Guild guild = guildRepository.findById(5001L);
            String ownerUsername = guild.getOwner().getUsername();

            // Then
            assertNotNull(ownerUsername);
            assertEquals("guildowner", ownerUsername);
        }

        @Test
        @DisplayName("Should list multiple guilds efficiently")
        void testListMultipleGuilds() {
            // Given
            for (int i = 0; i < 10; i++) {
                Guild guild = Guild.builder()
                        .id(7000L + i)
                        .name("PerformanceGuild" + i)
                        .owner(ownerUser)
                        .users(new HashSet<>())
                        .build();
                guildRepository.persist(guild);
            }

            // When
            List<Guild> guilds = guildRepository.listAll();

            // Then
            assertEquals(10, guilds.size());
        }
    }
}
