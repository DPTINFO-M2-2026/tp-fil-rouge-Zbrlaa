package fr.utln.spelerin.services;

import fr.utln.spelerin.dto.RoleDTO;
import fr.utln.spelerin.dto.createdto.RoleCreateDTO;
import fr.utln.spelerin.dto.updatedto.RoleUpdateDTO;
import fr.utln.spelerin.entities.Guild;
import fr.utln.spelerin.entities.User;
import fr.utln.spelerin.mappers.RoleMapper;
import fr.utln.spelerin.repositories.GuildRepository;
import fr.utln.spelerin.repositories.RoleRepository;
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
 * Integration Tests for RoleService (Failsafe).
 *
 * Utilise @QuarkusTest avec DevServices (PostgreSQL TestContainer).
 * Teste la logique métier du service de rôles avec ses relations.
 *
 * @author QA Automation Expert
 */
@QuarkusTest
@QuarkusTestResource(PostgresTestResource.class)
@Transactional
@DisplayName("RoleService Integration Tests (Failsafe)")
class RoleServiceIT {

    @Inject
    private RoleService roleService;

    @Inject
    private RoleRepository roleRepository;

    @Inject
    private GuildRepository guildRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private RoleMapper roleMapper;

    private Guild testGuild;
    private User ownerUser;

    @BeforeEach
    void setUp() {
        // Given: Clean up database
        roleRepository.deleteAll();
        guildRepository.deleteAll();
        userRepository.deleteAll();

        // Create owner and guild
        ownerUser = User.builder()
                .id(6001L)
                .username("owner")
                .displayName("Guild Owner")
                .build();
        userRepository.persist(ownerUser);

        testGuild = Guild.builder()
                .id(12001L)
                .name("TestGuild")
                .owner(ownerUser)
                .users(new HashSet<>())
                .build();
        guildRepository.persist(testGuild);
    }

    @Nested
    @DisplayName("Role Retrieval Operations")
    class RoleRetrievalOperations {

        @Test
        @DisplayName("Should retrieve all roles from database via service")
        void testGetAllRoles() {
            // Given
            RoleCreateDTO roleDto1 = new RoleCreateDTO(20L, "Admin", 0L, 12001L);
            RoleCreateDTO roleDto2 = new RoleCreateDTO(21L, "Moderator", 0L, 12001L);

            roleService.createRole(roleDto1);
            roleService.createRole(roleDto2);

            // When
            List<RoleDTO> allRoles = roleService.getAllRoles();

            // Then
            assertEquals(2, allRoles.size());
            assertTrue(allRoles.stream().anyMatch(r -> "Admin".equals(r.name())));
            assertTrue(allRoles.stream().anyMatch(r -> "Moderator".equals(r.name())));
        }

        @Test
        @DisplayName("Should return empty list when no roles exist")
        void testGetAllRolesWhenEmpty() {
            // When
            List<RoleDTO> allRoles = roleService.getAllRoles();

            // Then
            assertEquals(0, allRoles.size());
        }

        @Test
        @DisplayName("Should find role by ID via service")
        void testGetRoleById() {
            // Given
            RoleCreateDTO dto = new RoleCreateDTO(30L, "Find Me", 0L, 12001L);
            roleService.createRole(dto);

            // When
            Optional<RoleDTO> result = roleService.getRoleById(10L);

            // Then
            assertTrue(result.isPresent());
            assertEquals("Find Me", result.get().name());
        }

        @Test
        @DisplayName("Should return empty Optional when role not found")
        void testGetRoleByIdNotFound() {
            // When
            Optional<RoleDTO> result = roleService.getRoleById(999999L);

            // Then
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Role Creation (Upsert Logic)")
    class RoleCreation {

        @Test
        @DisplayName("Should create new role with valid guild")
        void testCreateNewRole() {
            // Given
            RoleCreateDTO dto = new RoleCreateDTO(10L, "New Role", 0L, 12001L);

            // When
            RoleDTO created = roleService.createRole(dto);

            // Then
            assertNotNull(created);
            assertEquals("New Role", created.name());
            assertEquals(20L, created.id());
            assertEquals(1, roleRepository.count());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when guild not found")
        void testCreateRoleWithInvalidGuild() {
            // Given
            RoleCreateDTO dto = new RoleCreateDTO(10L, "Invalid Role", 0L, 999999L);

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> roleService.createRole(dto));
        }

        @Test
        @DisplayName("Should update existing role (upsert behavior)")
        void testCreateRoleUpsertBehavior() {
            // Given - Create initial role
            RoleCreateDTO createDto = new RoleCreateDTO(10L, "Initial", 0L, 12001L);
            roleService.createRole(createDto);

            // When - Try to create same ID with different name
            RoleCreateDTO updateDto = new RoleCreateDTO(10L, "Updated", 0L, 12001L);
            RoleDTO result = roleService.createRole(updateDto);

            // Then
            assertEquals(1, roleRepository.count());
            assertEquals("Updated", result.name());
        }

        @Test
        @DisplayName("Should persist role with special characters in name")
        void testCreateRoleWithSpecialCharacters() {
            // Given
            RoleCreateDTO dto = new RoleCreateDTO(10L, "Role@#$%^&*()", 0L, 12001L);

            // When
            RoleDTO created = roleService.createRole(dto);

            // Then
            assertEquals("Role@#$%^&*()", created.name());
        }
    }

    @Nested
    @DisplayName("Role Update Operations")
    class RoleUpdateOperations {

        @Test
        @DisplayName("Should update existing role")
        void testUpdateExistingRole() {
            // Given
            RoleCreateDTO createDto = new RoleCreateDTO(10L, "Original", 0L, 12001L);
            roleService.createRole(createDto);

            // When
            RoleUpdateDTO updateDto = new RoleUpdateDTO("Updated Role", 0L, 12001L);
            RoleDTO updated = roleService.updateRole(60L, updateDto);

            // Then
            assertEquals("Updated Role", updated.name());
        }

        @Test
        @DisplayName("Should throw NoSuchElementException when updating non-existent role")
        void testUpdateNonExistentRole() {
            // Given
            RoleUpdateDTO updateDto = new RoleUpdateDTO("Any Role", 0L, 12001L);

            // When & Then
            assertThrows(NoSuchElementException.class, () -> roleService.updateRole(999999L, updateDto));
        }
    }

    @Nested
    @DisplayName("Role Deletion Operations")
    class RoleDeletionOperations {

        @Test
        @DisplayName("Should delete existing role")
        void testDeleteExistingRole() {
            // Given
            RoleCreateDTO dto = new RoleCreateDTO(10L, "Delete Me", 0L, 12001L);
            roleService.createRole(dto);
            assertEquals(1, roleRepository.count());

            // When
            boolean deleted = roleService.deleteRole(70L);

            // Then
            assertTrue(deleted);
            assertEquals(0, roleRepository.count());
        }

        @Test
        @DisplayName("Should return false when deleting non-existent role")
        void testDeleteNonExistentRole() {
            // When
            boolean deleted = roleService.deleteRole(999999L);

            // Then
            assertFalse(deleted);
        }

        @Test
        @DisplayName("Should delete only specified role")
        void testDeleteSpecificRoleOnly() {
            // Given
            RoleCreateDTO dto1 = new RoleCreateDTO(10L, "Keep", 0L, 12001L);
            RoleCreateDTO dto2 = new RoleCreateDTO(10L, "Delete", 0L, 12001L);
            roleService.createRole(dto1);
            roleService.createRole(dto2);

            // When
            roleService.deleteRole(81L);

            // Then
            assertEquals(1, roleRepository.count());
            assertTrue(roleService.getRoleById(80L).isPresent());
            assertTrue(roleService.getRoleById(81L).isEmpty());
        }
    }

    @Nested
    @DisplayName("Role-Guild Management")
    class RoleGuildManagement {

        @Test
        @DisplayName("Should verify role has correct guild")
        void testRoleGuildAssociation() {
            // Given
            RoleCreateDTO dto = new RoleCreateDTO(10L, "TestRole", 0L, 12001L);
            roleService.createRole(dto);

            // When
            Optional<RoleDTO> result = roleService.getRoleById(90L);

            // Then
            assertTrue(result.isPresent());
            assertEquals(12001L, result.get().guildId());
        }

        @Test
        @DisplayName("Should create multiple roles for same guild")
        void testMultipleRolesPerGuild() {
            // Given
            RoleCreateDTO dto1 = new RoleCreateDTO(10L, "Admin", 0L, 12001L);
            RoleCreateDTO dto2 = new RoleCreateDTO(10L, "Mod", 0L, 12001L);
            RoleCreateDTO dto3 = new RoleCreateDTO(10L, "Member", 0L, 12001L);

            roleService.createRole(dto1);
            roleService.createRole(dto2);
            roleService.createRole(dto3);

            // When
            List<RoleDTO> allRoles = roleService.getAllRoles();

            // Then
            assertEquals(3, allRoles.size());
            assertTrue(allRoles.stream().allMatch(r -> 12001L == r.guildId()));
        }
    }

    @Nested
    @DisplayName("Data Validation and Edge Cases")
    class DataValidationAndEdgeCases {

        @Test
        @DisplayName("Should handle very long role name")
        void testVeryLongRoleName() {
            // Given
            String longName = "R".repeat(255);
            RoleCreateDTO dto = new RoleCreateDTO(130L, longName, 0L, 12001L);

            // When
            RoleDTO created = roleService.createRole(dto);

            // Then
            assertEquals(255, created.name().length());
        }

        @Test
        @DisplayName("Should handle unicode in role name")
        void testUnicodeInRoleName() {
            // Given
            RoleCreateDTO dto = new RoleCreateDTO(10L, "角色 الدور ভূমিকা", 0L, 12001L);

            // When
            RoleDTO created = roleService.createRole(dto);

            // Then
            assertEquals("角色 الدور ভূমিকা", created.name());
        }

        @Test
        @DisplayName("Should retrieve role with correct data types")
        void testDataTypeConversion() {
            // Given
            RoleCreateDTO dto = new RoleCreateDTO(10L, "DataTest", 0L, 12001L);
            roleService.createRole(dto);

            // When
            Optional<RoleDTO> result = roleService.getRoleById(130L);

            // Then
            assertTrue(result.isPresent());
            assertInstanceOf(Long.class, result.get().id());
            assertInstanceOf(String.class, result.get().name());
        }
    }

    @Nested
    @DisplayName("Transactional Behavior")
    class TransactionalBehavior {

        @Test
        @DisplayName("Should handle multiple role operations in single transaction")
        void testMultipleRoleOperationsInTransaction() {
            // Given
            RoleCreateDTO dto1 = new RoleCreateDTO(50L, "Role1", 0L, 12001L);

            // When
            RoleDTO created1 = roleService.createRole(dto1);
            RoleUpdateDTO updateDto = new RoleUpdateDTO("Updated Role1", 0L, 12001L);
            RoleDTO updated = roleService.updateRole(created1.id(), updateDto);

            // Then
            assertEquals(2, roleRepository.count());
            assertEquals("Updated Role1", updated.name());
        }
    }

    @Nested
    @DisplayName("Performance and Efficiency")
    class PerformanceAndEfficiency {

        @Test
        @DisplayName("Should retrieve multiple roles efficiently")
        void testRetrieveMultipleRolesEfficiently() {
            // Given
            for (int i = 0; i < 20; i++) {
                RoleCreateDTO dto = new RoleCreateDTO((long) (200 + i), "Role " + i, 0L, 12001L);
                roleService.createRole(dto);
            }

            // When
            List<RoleDTO> allRoles = roleService.getAllRoles();

            // Then
            assertEquals(20, allRoles.size());
        }

        @Test
        @DisplayName("Should handle sequential create and update operations")
        void testSequentialRoleOperations() {
            // Given
            RoleCreateDTO createDto = new RoleCreateDTO(60L, "Sequential", 0L, 12001L);
            RoleDTO created = roleService.createRole(createDto);
            long createdId = created.id();

            // When
            for (int i = 0; i < 5; i++) {
                RoleUpdateDTO updateDto = new RoleUpdateDTO("Sequential " + i, 0L, 12001L);
                roleService.updateRole(createdId, updateDto);
            }

            // Then
            Optional<RoleDTO> result = roleService.getRoleById(createdId);
            assertTrue(result.isPresent());
            assertEquals("Sequential 4", result.get().name());
        }
    }
}
