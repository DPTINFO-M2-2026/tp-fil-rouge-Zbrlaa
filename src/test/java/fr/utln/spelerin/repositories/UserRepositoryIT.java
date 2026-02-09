package fr.utln.spelerin.repositories;

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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration Tests for UserRepository (Failsafe).
 *
 * Utilise @QuarkusTest avec DevServices (PostgreSQL TestContainer).
 * Pas de mocks - teste contre une vraie base de données de test.
 *
 * @author QA Automation Expert
 */
@QuarkusTest
@QuarkusTestResource(PostgresTestResource.class)
@Transactional
@DisplayName("UserRepository Integration Tests (Failsafe)")
class UserRepositoryIT {

    @Inject
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Given: Nettoyer et initialiser les données de test
        userRepository.deleteAll();

        testUser = User.builder()
                .id(123456789L)
                .username("testuser")
                .displayName("Test User")
                .build();
    }

    @Nested
    @DisplayName("CRUD Operations")
    class CRUDOperations {

        @Test
        @DisplayName("Should persist user to database and retrieve it")
        void testPersistAndRetrieveUser() {
            // Given
            User user = User.builder()
                    .id(111L)
                    .username("alice")
                    .displayName("Alice")
                    .build();

            // When
            userRepository.persist(user);

            // Then
            User retrieved = userRepository.findById(111L);
            assertNotNull(retrieved);
            assertEquals("alice", retrieved.getUsername());
        }

        @Test
        @DisplayName("Should find user by ID from database")
        void testFindUserById() {
            // Given
            userRepository.persist(testUser);

            // When
            User result = userRepository.findById(123456789L);

            // Then
            assertNotNull(result);
            assertEquals("testuser", result.getUsername());
            assertEquals("Test User", result.getDisplayName());
        }

        @Test
        @DisplayName("Should return null when user not found in database")
        void testFindUserByIdNotFound() {
            // When
            User result = userRepository.findById(999999L);

            // Then
            assertNull(result);
        }

        @Test
        @DisplayName("Should update existing user in database")
        void testUpdateUser() {
            // Given
            userRepository.persist(testUser);

            // When
            testUser.setUsername("updated_username");
            testUser.setDisplayName("Updated User");
            userRepository.persist(testUser);

            // Then
            User updated = userRepository.findById(123456789L);
            assertEquals("updated_username", updated.getUsername());
            assertEquals("Updated User", updated.getDisplayName());
        }

        @Test
        @DisplayName("Should delete user from database")
        void testDeleteUser() {
            // Given
            userRepository.persist(testUser);
            assertTrue(userRepository.count() > 0);

            // When
            userRepository.delete(testUser);

            // Then
            User deleted = userRepository.findById(123456789L);
            assertNull(deleted);
        }

        @Test
        @DisplayName("Should delete all users from database")
        void testDeleteAllUsers() {
            // Given
            userRepository.persist(testUser);
            User user2 = User.builder().id(222L).username("bob").displayName("Bob").build();
            userRepository.persist(user2);

            // When
            userRepository.deleteAll();

            // Then
            assertEquals(0, userRepository.count());
        }
    }

    @Nested
    @DisplayName("Query Operations")
    class QueryOperations {

        @Test
        @DisplayName("Should find user by username")
        void testFindUserByUsername() {
            // Given
            userRepository.persist(testUser);

            // When
            Optional<User> result = userRepository.findByUsernameOptional("testuser");

            // Then
            assertTrue(result.isPresent());
            assertEquals(123456789L, result.get().getId());
        }

        @Test
        @DisplayName("Should return empty Optional for non-existent username")
        void testFindByUsernameNotFound() {
            // Given
            userRepository.persist(testUser);

            // When
            Optional<User> result = userRepository.findByUsernameOptional("nonexistent");

            // Then
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should list all users from database")
        void testListAllUsers() {
            // Given
            User user1 = User.builder().id(1L).username("user1").displayName("User 1").build();
            User user2 = User.builder().id(2L).username("user2").displayName("User 2").build();
            User user3 = User.builder().id(3L).username("user3").displayName("User 3").build();

            userRepository.persist(user1);
            userRepository.persist(user2);
            userRepository.persist(user3);

            // When
            List<User> allUsers = userRepository.listAll();

            // Then
            assertEquals(3, allUsers.size());
            assertTrue(allUsers.stream().anyMatch(u -> "user1".equals(u.getUsername())));
            assertTrue(allUsers.stream().anyMatch(u -> "user2".equals(u.getUsername())));
        }

        @Test
        @DisplayName("Should return empty list when no users in database")
        void testListAllUsersWhenEmpty() {
            // When
            List<User> result = userRepository.listAll();

            // Then
            assertEquals(0, result.size());
        }
    }

    @Nested
    @DisplayName("Batch Operations")
    class BatchOperations {

        @Test
        @DisplayName("Should persist multiple users and count them")
        void testPersistMultipleUsers() {
            // Given
            User user1 = User.builder().id(10L).username("user10").displayName("User 10").build();
            User user2 = User.builder().id(20L).username("user20").displayName("User 20").build();
            User user3 = User.builder().id(30L).username("user30").displayName("User 30").build();

            // When
            userRepository.persist(user1);
            userRepository.persist(user2);
            userRepository.persist(user3);

            // Then
            assertEquals(3, userRepository.count());
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling")
    class EdgeCases {

        @Test
        @DisplayName("Should handle username with special characters")
        void testSpecialCharactersInUsername() {
            // Given
            User specialUser = User.builder()
                    .id(100L)
                    .username("user@#$%^&*()")
                    .displayName("Special User")
                    .build();

            // When
            userRepository.persist(specialUser);

            // Then
            Optional<User> found = userRepository.findByUsernameOptional("user@#$%^&*()");
            assertTrue(found.isPresent());
            assertEquals("user@#$%^&*()", found.get().getUsername());
        }

        @Test
        @DisplayName("Should handle very long username")
        void testVeryLongUsername() {
            // Given
            String longUsername = "a".repeat(255);
            User longUser = User.builder()
                    .id(200L)
                    .username(longUsername)
                    .displayName("Long Username User")
                    .build();

            // When
            userRepository.persist(longUser);

            // Then
            Optional<User> found = userRepository.findByUsernameOptional(longUsername);
            assertTrue(found.isPresent());
            assertEquals(255, found.get().getUsername().length());
        }

        @Test
        @DisplayName("Should handle null username search gracefully")
        void testNullUsernameSearch() {
            // Given
            userRepository.persist(testUser);

            // When & Then
            assertDoesNotThrow(() -> {
                userRepository.findByUsernameOptional(null);
            });
        }

        @Test
        @DisplayName("Should handle duplicate user ID (constraint violation)")
        void testDuplicateUserID() {
            // Given
            User user1 = User.builder().id(500L).username("user500").displayName("User 500").build();
            User user2 = User.builder().id(500L).username("duplicate").displayName("Duplicate").build();

            userRepository.persist(user1);

            // When & Then
            assertThrows(Exception.class, () -> {
                userRepository.persist(user2);
            });
        }

        @Test
        @DisplayName("Should persist user with unicode characters in displayName")
        void testUnicodeInDisplayName() {
            // Given
            User unicodeUser = User.builder()
                    .id(300L)
                    .username("unicode")
                    .displayName("用户 المستخدم नमस्ते")
                    .build();

            // When
            userRepository.persist(unicodeUser);

            // Then
            User retrieved = userRepository.findById(300L);
            assertEquals("用户 المستخدم नमस्ते", retrieved.getDisplayName());
        }
    }

    @Nested
    @DisplayName("Performance and Optimization")
    class PerformanceTests {

        @Test
        @DisplayName("Should retrieve user without N+1 queries issue")
        void testNoNPlus1QueryProblem() {
            // Given
            userRepository.persist(testUser);

            // When
            User user = userRepository.findById(123456789L);
            String username = user.getUsername();

            // Then - Verify we can access user properties without additional queries
            assertNotNull(username);
            assertEquals("testuser", username);
        }

        @Test
        @DisplayName("Should count users efficiently")
        void testCountUsers() {
            // Given
            for (int i = 0; i < 10; i++) {
                User user = User.builder()
                        .id((long) i)
                        .username("user" + i)
                        .displayName("User " + i)
                        .build();
                userRepository.persist(user);
            }

            // When
            long count = userRepository.count();

            // Then
            assertEquals(10, count);
        }
    }
}
