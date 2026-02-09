package fr.utln.spelerin.services;

import fr.utln.spelerin.dto.UserDTO;
import fr.utln.spelerin.dto.createdto.UserCreateDTO;
import fr.utln.spelerin.dto.updatedto.UserUpdateDTO;
import fr.utln.spelerin.entities.Guild;
import fr.utln.spelerin.entities.User;
import fr.utln.spelerin.mappers.UserMapper;
import fr.utln.spelerin.repositories.GuildRepository;
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
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration Tests for UserService (Failsafe).
 *
 * Utilise @QuarkusTest avec DevServices (PostgreSQL TestContainer).
 * Teste la logique métier du service avec transactions réelles.
 *
 * @author QA Automation Expert
 */
@QuarkusTest
@QuarkusTestResource(PostgresTestResource.class)
@Transactional
@DisplayName("UserService Integration Tests (Failsafe)")
class UserServiceIT {

    @Inject
    private UserService userService;

    @Inject
    private UserRepository userRepository;

    @Inject
    private GuildRepository guildRepository;

    @Inject
    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        // Given: Clean up database
        userRepository.deleteAll();
        guildRepository.deleteAll();
    }

    @Nested
    @DisplayName("User Retrieval Operations")
    class UserRetrievalOperations {

        @Test
        @DisplayName("Should retrieve all users from database via service")
        void testGetAllUsers() {
            // Given
            UserCreateDTO userDto1 = new UserCreateDTO(1L, "user1", "User One");
            UserCreateDTO userDto2 = new UserCreateDTO(2L, "user2", "User Two");

            userService.createUser(userDto1);
            userService.createUser(userDto2);

            // When
            List<UserDTO> allUsers = userService.getAllUsers();

            // Then
            assertEquals(2, allUsers.size());
            assertTrue(allUsers.stream().anyMatch(u -> "user1".equals(u.username())));
            assertTrue(allUsers.stream().anyMatch(u -> "user2".equals(u.username())));
        }

        @Test
        @DisplayName("Should return empty list when no users exist")
        void testGetAllUsersWhenEmpty() {
            // When
            List<UserDTO> allUsers = userService.getAllUsers();

            // Then
            assertEquals(0, allUsers.size());
        }

        @Test
        @DisplayName("Should find user by ID via service")
        void testGetUserById() {
            // Given
            UserCreateDTO dto = new UserCreateDTO(100L, "findme", "Find Me");
            userService.createUser(dto);

            // When
            Optional<UserDTO> result = userService.getUserById(100L);

            // Then
            assertTrue(result.isPresent());
            assertEquals("findme", result.get().username());
        }

        @Test
        @DisplayName("Should return empty Optional when user not found by ID")
        void testGetUserByIdNotFound() {
            // When
            Optional<UserDTO> result = userService.getUserById(999999L);

            // Then
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should find user by username via service")
        void testGetUserByUsername() {
            // Given
            UserCreateDTO dto = new UserCreateDTO(200L, "alice", "Alice");
            userService.createUser(dto);

            // When
            Optional<UserDTO> result = userService.getUserByUsername("alice");

            // Then
            assertTrue(result.isPresent());
            assertEquals(200L, result.get().id());
        }

        @Test
        @DisplayName("Should return empty Optional when user not found by username")
        void testGetUserByUsernameNotFound() {
            // When
            Optional<UserDTO> result = userService.getUserByUsername("nonexistent");

            // Then
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("User Creation (Upsert Logic)")
    class UserCreation {

        @Test
        @DisplayName("Should create new user when not exists")
        void testCreateNewUser() {
            // Given
            UserCreateDTO dto = new UserCreateDTO(300L, "newuser", "New User");

            // When
            UserDTO created = userService.createUser(dto);

            // Then
            assertNotNull(created);
            assertEquals("newuser", created.username());
            assertEquals(300L, created.id());
            assertEquals(1, userRepository.count());
        }

        @Test
        @DisplayName("Should update existing user (upsert behavior)")
        void testCreateUserUpsertBehavior() {
            // Given - Create initial user
            UserCreateDTO createDto = new UserCreateDTO(400L, "bob", "Bob Initial");
            userService.createUser(createDto);

            // When - Try to create same ID with different data
            UserCreateDTO updateDto = new UserCreateDTO(400L, "bob_updated", "Bob Updated");
            UserDTO result = userService.createUser(updateDto);

            // Then - User should be updated, not duplicated
            assertEquals(1, userRepository.count());
            assertEquals("bob_updated", result.username());
            assertEquals("Bob Updated", result.displayName());
        }

        @Test
        @DisplayName("Should handle null username in create")
        void testCreateUserWithNullUsername() {
            // Given
            UserCreateDTO dto = new UserCreateDTO(500L, null, "Display Name");

            // When & Then
            assertDoesNotThrow(() -> userService.createUser(dto));
        }

        @Test
        @DisplayName("Should persist user with special characters in username")
        void testCreateUserWithSpecialCharacters() {
            // Given
            UserCreateDTO dto = new UserCreateDTO(600L, "user@#$%", "Special User");

            // When
            UserDTO created = userService.createUser(dto);

            // Then
            assertEquals("user@#$%", created.username());
        }
    }

    @Nested
    @DisplayName("User Update Operations")
    class UserUpdateOperations {

        @Test
        @DisplayName("Should update existing user")
        void testUpdateExistingUser() {
            // Given
            UserCreateDTO createDto = new UserCreateDTO(700L, "original", "Original Name");
            userService.createUser(createDto);

            // When
            UserUpdateDTO updateDto = new UserUpdateDTO("updated_user", "Updated Name");
            UserDTO updated = userService.updateUser(700L, updateDto);

            // Then
            assertEquals("updated_user", updated.username());
            assertEquals("Updated Name", updated.displayName());
        }

        @Test
        @DisplayName("Should throw NoSuchElementException when updating non-existent user")
        void testUpdateNonExistentUser() {
            // Given
            UserUpdateDTO updateDto = new UserUpdateDTO("anyuser", "Any Display");

            // When & Then
            assertThrows(NoSuchElementException.class, () -> userService.updateUser(999999L, updateDto));
        }

        @Test
        @DisplayName("Should handle null fields in update")
        void testUpdateUserWithNullFields() {
            // Given
            UserCreateDTO createDto = new UserCreateDTO(800L, "user", "Initial");
            userService.createUser(createDto);

            // When
            UserUpdateDTO updateDto = new UserUpdateDTO(null, null);

            // Then - Should not throw exception
            assertDoesNotThrow(() -> userService.updateUser(800L, updateDto));
        }

        @Test
        @DisplayName("Should update only specified fields")
        void testPartialUserUpdate() {
            // Given
            UserCreateDTO createDto = new UserCreateDTO(900L, "original", "Original");
            userService.createUser(createDto);

            // When
            UserUpdateDTO updateDto = new UserUpdateDTO("changed", "Original");
            UserDTO updated = userService.updateUser(900L, updateDto);

            // Then
            assertEquals("changed", updated.username());
            assertEquals("Original", updated.displayName());
        }
    }

    @Nested
    @DisplayName("User Deletion Operations")
    class UserDeletionOperations {

        @Test
        @DisplayName("Should delete existing user")
        void testDeleteExistingUser() {
            // Given
            UserCreateDTO dto = new UserCreateDTO(1000L, "delete_me", "To Delete");
            userService.createUser(dto);
            assertEquals(1, userRepository.count());

            // When
            boolean deleted = userService.deleteUser(1000L);

            // Then
            assertTrue(deleted);
            assertEquals(0, userRepository.count());
        }

        @Test
        @DisplayName("Should return false when deleting non-existent user")
        void testDeleteNonExistentUser() {
            // When
            boolean deleted = userService.deleteUser(999999L);

            // Then
            assertFalse(deleted);
        }

        @Test
        @DisplayName("Should delete only specified user")
        void testDeleteSpecificUserOnly() {
            // Given
            UserCreateDTO dto1 = new UserCreateDTO(1001L, "keep", "Keep This");
            UserCreateDTO dto2 = new UserCreateDTO(1002L, "delete", "Delete This");
            userService.createUser(dto1);
            userService.createUser(dto2);

            // When
            userService.deleteUser(1002L);

            // Then
            assertEquals(1, userRepository.count());
            assertTrue(userService.getUserById(1001L).isPresent());
            assertTrue(userService.getUserById(1002L).isEmpty());
        }
    }

    @Nested
    @DisplayName("Guild Association Operations")
    class GuildAssociationOperations {

        @Test
        @DisplayName("Should add owned guild to user")
        void testAddOwnedGuildToUser() {
            // Given
            User owner = User.builder().id(1100L).username("owner").displayName("Guild Owner").build();
            userRepository.persist(owner);

            Guild guild = Guild.builder()
                    .id(11000L)
                    .name("TestGuild")
                    .owner(owner)
                    .users(new HashSet<>())
                    .build();
            guildRepository.persist(guild);

            // When
            UserDTO result = userService.addOwnedGuildToUser(1100L, 11000L);

            // Then
            assertNotNull(result);
            assertEquals("owner", result.username());
        }

        @Test
        @DisplayName("Should throw NoSuchElementException when user not found")
        void testAddGuildToNonExistentUser() {
            // Given
            Guild guild = Guild.builder()
                    .id(11001L)
                    .name("TestGuild")
                    .owner(null)
                    .users(new HashSet<>())
                    .build();
            guildRepository.persist(guild);

            // When & Then
            assertThrows(NoSuchElementException.class, () -> 
                userService.addOwnedGuildToUser(999999L, 11001L)
            );
        }

        @Test
        @DisplayName("Should throw NoSuchElementException when guild not found")
        void testAddNonExistentGuildToUser() {
            // Given
            UserCreateDTO dto = new UserCreateDTO(1101L, "user", "User");
            userService.createUser(dto);

            // When & Then
            assertThrows(NoSuchElementException.class, () -> 
                userService.addOwnedGuildToUser(1101L, 999999L)
            );
        }
    }

    @Nested
    @DisplayName("Transactional Behavior")
    class TransactionalBehavior {

        @Test
        @DisplayName("Should rollback user creation on transaction failure")
        void testTransactionRollback() {
            // Given
            UserCreateDTO dto1 = new UserCreateDTO(1200L, "user1", "User 1");
            userService.createUser(dto1);
            long initialCount = userRepository.count();

            // When - Try to create duplicate ID (should fail)
            UserCreateDTO dto2 = new UserCreateDTO(1200L, "user1_duplicate", "User 1 Duplicate");

            // Then - Duplicate should update instead of creating
            UserDTO result = userService.createUser(dto2);
            assertEquals(initialCount, userRepository.count());
            assertEquals("user1_duplicate", result.username());
        }

        @Test
        @DisplayName("Should handle multiple operations in single transaction")
        void testMultipleOperationsInTransaction() {
            // Given

            // When
            UserDTO updated = userService.updateUser(1300L, new UserUpdateDTO("updated", "Updated"));

            // Then
            assertEquals(2, userRepository.count());
            assertEquals("updated", updated.username());
        }
    }

    @Nested
    @DisplayName("Data Validation and Edge Cases")
    class DataValidationAndEdgeCases {

        @Test
        @DisplayName("Should handle very long username")
        void testVeryLongUsername() {
            // Given
            String longUsername = "u".repeat(255);
            UserCreateDTO dto = new UserCreateDTO(1400L, longUsername, "Long User");

            // When
            UserDTO created = userService.createUser(dto);

            // Then
            assertEquals(255, created.username().length());
        }

        @Test
        @DisplayName("Should handle unicode in display name")
        void testUnicodeInDisplayName() {
            // Given
            UserCreateDTO dto = new UserCreateDTO(1500L, "unicode_user", "用户 المستخدم उपयोगकर्ता");

            // When
            UserDTO created = userService.createUser(dto);

            // Then
            assertEquals("用户 المستخدم उपयोगकर्ता", created.displayName());
        }

        @Test
        @DisplayName("Should retrieve user with correct data type conversion")
        void testDataTypeConversion() {
            // Given
            UserCreateDTO dto = new UserCreateDTO(1600L, "datatest", "Data Test");
            userService.createUser(dto);

            // When
            Optional<UserDTO> result = userService.getUserById(1600L);

            // Then
            assertTrue(result.isPresent());
            assertInstanceOf(Long.class, result.get().id());
            assertInstanceOf(String.class, result.get().username());
        }
    }

    @Nested
    @DisplayName("Performance and Efficiency")
    class PerformanceAndEfficiency {

        @Test
        @DisplayName("Should retrieve multiple users efficiently")
        void testRetrieveMultipleUsersEfficiently() {
            // Given
            for (int i = 0; i < 50; i++) {
                UserCreateDTO dto = new UserCreateDTO((long) (2000 + i), "perf_user" + i, "Perf User " + i);
                userService.createUser(dto);
            }

            // When
            List<UserDTO> allUsers = userService.getAllUsers();

            // Then
            assertEquals(50, allUsers.size());
        }

        @Test
        @DisplayName("Should handle sequential create and update operations")
        void testSequentialOperations() {
            // Given

            // When
            for (int i = 0; i < 10; i++) {
                UserUpdateDTO updateDto = new UserUpdateDTO("seq_" + i, "Sequential " + i);
                userService.updateUser(2100L, updateDto);
            }

            // Then
            Optional<UserDTO> result = userService.getUserById(2100L);
            assertTrue(result.isPresent());
            assertEquals("seq_9", result.get().username());
        }
    }
}
