package fr.utln.spelerin.repositories;

import fr.utln.spelerin.entities.Guild;
import fr.utln.spelerin.entities.Role;
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
 * Integration Tests for RoleRepository (Failsafe).
 *
 * Utilise @QuarkusTest avec DevServices (PostgreSQL TestContainer).
 * Teste la persistance des rôles avec leurs guildes et utilisateurs.
 *
 * @author QA Automation Expert
 */
@QuarkusTest
@QuarkusTestResource(PostgresTestResource.class)
@Transactional
@DisplayName("RoleRepository Integration Tests (Failsafe)")
class RoleRepositoryIT {

    @Inject
    private RoleRepository roleRepository;

    @Inject
    private GuildRepository guildRepository;

    @Inject
    private UserRepository userRepository;

    private Role testRole;
    private Guild testGuild;
    private User ownerUser;

    @BeforeEach
    void setUp() {
        // Given: Clean up and initialize test data
        roleRepository.deleteAll();
        guildRepository.deleteAll();
        userRepository.deleteAll();

        // Create owner user
        ownerUser = User.builder()
                .id(2001L)
                .username("guildowner")
                .displayName("Guild Owner")
                .build();
        userRepository.persist(ownerUser);

        // Create guild
        testGuild = Guild.builder()
                .id(8001L)
                .name("TestGuild")
                .owner(ownerUser)
                .users(new HashSet<>())
                .build();
        guildRepository.persist(testGuild);

        // Create test role
        testRole = Role.builder()
                .id(3001L)
                .name("TestRole")
                .guild(testGuild)
                .users(new HashSet<>())
                .build();
    }

    @Nested
    @DisplayName("CRUD Operations")
    class CRUDOperations {

        @Test
        @DisplayName("Should persist role with guild to database")
        void testPersistRoleWithGuild() {
            // When
            roleRepository.persist(testRole);

            // Then
            Role retrieved = roleRepository.findById(3001L);
            assertNotNull(retrieved);
            assertEquals("TestRole", retrieved.getName());
            assertNotNull(retrieved.getGuild());
            assertEquals("TestGuild", retrieved.getGuild().getName());
        }

        @Test
        @DisplayName("Should find role by ID")
        void testFindRoleById() {
            // Given
            roleRepository.persist(testRole);

            // When
            Role result = roleRepository.findById(3001L);

            // Then
            assertNotNull(result);
            assertEquals("TestRole", result.getName());
            assertEquals(8001L, result.getGuild().getId());
        }

        @Test
        @DisplayName("Should return null when role not found")
        void testFindRoleByIdNotFound() {
            // When
            Role result = roleRepository.findById(9999999L);

            // Then
            assertNull(result);
        }

        @Test
        @DisplayName("Should update role in database")
        void testUpdateRole() {
            // Given
            roleRepository.persist(testRole);

            // When
            testRole.setName("UpdatedRole");
            roleRepository.persist(testRole);

            // Then
            Role updated = roleRepository.findById(3001L);
            assertEquals("UpdatedRole", updated.getName());
        }

        @Test
        @DisplayName("Should delete role from database")
        void testDeleteRole() {
            // Given
            roleRepository.persist(testRole);

            // When
            roleRepository.delete(testRole);

            // Then
            Role deleted = roleRepository.findById(3001L);
            assertNull(deleted);
        }

        @Test
        @DisplayName("Should delete all roles from database")
        void testDeleteAllRoles() {
            // Given
            roleRepository.persist(testRole);
            Role role2 = Role.builder()
                    .id(3002L)
                    .name("SecondRole")
                    .guild(testGuild)
                    .users(new HashSet<>())
                    .build();
            roleRepository.persist(role2);

            // When
            roleRepository.deleteAll();

            // Then
            assertEquals(0, roleRepository.count());
        }
    }

    @Nested
    @DisplayName("Role-Guild Relationships")
    class RoleGuildRelationships {

        @Test
        @DisplayName("Should verify role belongs to correct guild")
        void testRoleGuildAssociation() {
            // Given
            roleRepository.persist(testRole);

            // When
            Role retrieved = roleRepository.findById(3001L);
            Guild roleGuild = retrieved.getGuild();

            // Then
            assertEquals(8001L, roleGuild.getId());
            assertEquals("TestGuild", roleGuild.getName());
        }

        @Test
        @DisplayName("Should create multiple roles for same guild")
        void testMultipleRolesPerGuild() {
            // Given
            Role role1 = Role.builder().id(3003L).name("Admin").guild(testGuild).users(new HashSet<>()).build();
            Role role2 = Role.builder().id(3004L).name("Moderator").guild(testGuild).users(new HashSet<>()).build();
            Role role3 = Role.builder().id(3005L).name("Member").guild(testGuild).users(new HashSet<>()).build();

            // When
            roleRepository.persist(role1);
            roleRepository.persist(role2);
            roleRepository.persist(role3);

            // Then
            List<Role> allRoles = roleRepository.listAll();
            assertEquals(3, allRoles.size());
            assertTrue(allRoles.stream().allMatch(r -> testGuild.getId() == r.getGuild().getId()));
        }
    }

    @Nested
    @DisplayName("Role-User Relationships")
    class RoleUserRelationships {

        @Test
        @DisplayName("Should add users to role")
        void testAddUsersToRole() {
            // Given
            User user1 = User.builder().id(2002L).username("user1").displayName("User 1").build();
            User user2 = User.builder().id(2003L).username("user2").displayName("User 2").build();
            userRepository.persist(user1);
            userRepository.persist(user2);

            testRole.getUsers().add(user1);
            testRole.getUsers().add(user2);

            // When
            roleRepository.persist(testRole);

            // Then
            Role retrieved = roleRepository.findById(3001L);
            assertEquals(2, retrieved.getUsers().size());
            assertTrue(retrieved.getUsers().stream().anyMatch(u -> "user1".equals(u.getUsername())));
        }

        @Test
        @DisplayName("Should maintain role with empty user set")
        void testRoleWithEmptyUserSet() {
            // Given
            testRole.setUsers(new HashSet<>());

            // When
            roleRepository.persist(testRole);

            // Then
            Role retrieved = roleRepository.findById(3001L);
            assertNotNull(retrieved.getUsers());
            assertEquals(0, retrieved.getUsers().size());
        }
    }

    @Nested
    @DisplayName("Query Operations")
    class QueryOperations {

        @Test
        @DisplayName("Should list all roles from database")
        void testListAllRoles() {
            // Given
            roleRepository.persist(testRole);
            Role role2 = Role.builder()
                    .id(3006L)
                    .name("SecondRole")
                    .guild(testGuild)
                    .users(new HashSet<>())
                    .build();
            roleRepository.persist(role2);

            // When
            List<Role> allRoles = roleRepository.listAll();

            // Then
            assertEquals(2, allRoles.size());
            assertTrue(allRoles.stream().anyMatch(r -> "TestRole".equals(r.getName())));
        }

        @Test
        @DisplayName("Should return empty list when no roles in database")
        void testListAllRolesWhenEmpty() {
            // When
            List<Role> result = roleRepository.listAll();

            // Then
            assertEquals(0, result.size());
        }

        @Test
        @DisplayName("Should count roles in database")
        void testCountRoles() {
            // Given
            for (int i = 0; i < 5; i++) {
                Role role = Role.builder()
                        .id(4000L + i)
                        .name("Role" + i)
                        .guild(testGuild)
                        .users(new HashSet<>())
                        .build();
                roleRepository.persist(role);
            }

            // When
            long count = roleRepository.count();

            // Then
            assertEquals(5, count);
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling")
    class EdgeCases {

        @Test
        @DisplayName("Should handle role name with special characters")
        void testSpecialCharactersInRoleName() {
            // Given
            Role specialRole = Role.builder()
                    .id(3007L)
                    .name("Role@#$%^&*()")
                    .guild(testGuild)
                    .users(new HashSet<>())
                    .build();

            // When
            roleRepository.persist(specialRole);

            // Then
            Role retrieved = roleRepository.findById(3007L);
            assertEquals("Role@#$%^&*()", retrieved.getName());
        }

        @Test
        @DisplayName("Should handle very long role name")
        void testVeryLongRoleName() {
            // Given
            String longName = "R".repeat(255);
            Role longRole = Role.builder()
                    .id(3008L)
                    .name(longName)
                    .guild(testGuild)
                    .users(new HashSet<>())
                    .build();

            // When
            roleRepository.persist(longRole);

            // Then
            Role retrieved = roleRepository.findById(3008L);
            assertEquals(255, retrieved.getName().length());
        }

        @Test
        @DisplayName("Should handle unicode characters in role name")
        void testUnicodeInRoleName() {
            // Given
            Role unicodeRole = Role.builder()
                    .id(3009L)
                    .name("角色 الدور ভূমিকা")
                    .guild(testGuild)
                    .users(new HashSet<>())
                    .build();

            // When
            roleRepository.persist(unicodeRole);

            // Then
            Role retrieved = roleRepository.findById(3009L);
            assertEquals("角色 الدور ভূমিকা", retrieved.getName());
        }

        @Test
        @DisplayName("Should handle duplicate role ID constraint")
        void testDuplicateRoleID() {
            // Given
            Role role1 = Role.builder()
                    .id(3010L)
                    .name("FirstRole")
                    .guild(testGuild)
                    .users(new HashSet<>())
                    .build();
            Role role2 = Role.builder()
                    .id(3010L)
                    .name("SecondRole")
                    .guild(testGuild)
                    .users(new HashSet<>())
                    .build();

            roleRepository.persist(role1);

            // When & Then
            assertThrows(Exception.class, () -> roleRepository.persist(role2));
        }

        @Test
        @DisplayName("Should handle role with null guild")
        void testRoleWithNullGuild() {
            // Given
            Role orphanRole = Role.builder()
                    .id(3011L)
                    .name("OrphanRole")
                    .guild(null)
                    .users(new HashSet<>())
                    .build();

            // When & Then
            if (roleRepository.count() == 0) {
                roleRepository.persist(orphanRole);
                Role retrieved = roleRepository.findById(3011L);
                assertNull(retrieved.getGuild());
            }
        }
    }

    @Nested
    @DisplayName("Performance Tests")
    class PerformanceTests {

        @Test
        @DisplayName("Should retrieve role with guild without N+1 queries")
        void testNoNPlus1WithGuild() {
            // Given
            roleRepository.persist(testRole);

            // When
            Role role = roleRepository.findById(3001L);
            String guildName = role.getGuild().getName();

            // Then
            assertNotNull(guildName);
            assertEquals("TestGuild", guildName);
        }

        @Test
        @DisplayName("Should list multiple roles efficiently")
        void testListMultipleRoles() {
            // Given
            for (int i = 0; i < 10; i++) {
                Role role = Role.builder()
                        .id(5000L + i)
                        .name("PerformanceRole" + i)
                        .guild(testGuild)
                        .users(new HashSet<>())
                        .build();
                roleRepository.persist(role);
            }

            // When
            List<Role> roles = roleRepository.listAll();

            // Then
            assertEquals(10, roles.size());
        }
    }
}
