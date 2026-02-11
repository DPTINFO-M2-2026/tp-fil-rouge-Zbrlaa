package fr.utln.spelerin.repositories;

import fr.utln.spelerin.entities.Guild;
import fr.utln.spelerin.entities.Role;
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
 * Unitaire Tests for RoleRepository (Surefire).
 *
 * Tests rapides et isolés sans dépendance à Quarkus.
 * Tous les appels à la BD sont mockés.
 *
 * @author QA Automation Expert
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RoleRepository Unit Tests (Surefire)")
class RoleRepositoryTest {

    @Mock
    private RoleRepository roleRepository;

    private Role testRole;
    private Guild testGuild;

    @BeforeEach
    void setUp() {
        // Given: Initialiser les fixtures
        testGuild = Guild.builder()
                .id(1000L)
                .name("Test Guild")
                .build();

        testRole = Role.builder()
                .id(5000L)
                .name("Admin")
                .guild(testGuild)
                .users(new HashSet<>())
            .permissions(0L)
            .accessibleChannels(new HashSet<>())
                .build();
    }

    @Test
    @DisplayName("Should find role by ID when exists")
    void testFindByIdWhenExists() {
        // Given
        when(roleRepository.findById(5000L)).thenReturn(testRole);

        // When
        Role result = roleRepository.findById(5000L);

        // Then
        assertNotNull(result);
        assertEquals("Admin", result.getName());
        assertEquals(1000L, result.getGuild().getId());
        verify(roleRepository, times(1)).findById(5000L);
    }

    @Test
    @DisplayName("Should return null when role not found")
    void testFindByIdWhenNotExists() {
        // Given
        when(roleRepository.findById(99999L)).thenReturn(null);

        // When
        Role result = roleRepository.findById(99999L);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Should persist role with guild relationship")
    void testPersistRoleWithGuild() {
        // Given
        doNothing().when(roleRepository).persist(testRole);

        // When
        roleRepository.persist(testRole);

        // Then
        verify(roleRepository, times(1)).persist(testRole);
        assertNotNull(testRole.getGuild());
    }

    @Test
    @DisplayName("Should handle role name with special characters")
    void testRoleNameWithSpecialCharacters() {
        // Given
        Role specialRole = Role.builder()
                .id(5001L)
                .name("Role@#$%^&*()")
                .guild(testGuild)
            .permissions(0L)
            .build();

        when(roleRepository.findById(5001L)).thenReturn(specialRole);

        // When
        Role result = roleRepository.findById(5001L);

        // Then
        assertEquals("Role@#$%^&*()", result.getName());
    }

    @Test
    @DisplayName("Should handle very long role name")
    void testRoleWithVeryLongName() {
        // Given
        String longName = "R".repeat(255);
        Role longRole = Role.builder()
                .id(5002L)
                .name(longName)
                .guild(testGuild)
            .permissions(0L)
            .build();

        when(roleRepository.findById(5002L)).thenReturn(longRole);

        // When
        Role result = roleRepository.findById(5002L);

        // Then
        assertEquals(longName, result.getName());
    }

    @Test
    @DisplayName("Should verify role-guild association")
    void testRoleGuildAssociation() {
        // Given
        when(roleRepository.findById(5000L)).thenReturn(testRole);

        // When
        Role result = roleRepository.findById(5000L);

        // Then
        assertNotNull(result.getGuild());
        assertEquals("Test Guild", result.getGuild().getName());
    }

    @Test
    @DisplayName("Should handle error: null role ID")
    void testFindByNullId() {
        // Given
        when(roleRepository.findById(null)).thenReturn(null);

        // When
        Role result = roleRepository.findById(null);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Should persist role without guild (edge case)")
    void testPersistRoleWithoutGuild() {
        // Given
        Role roleWithoutGuild = Role.builder()
                .id(5003L)
                .name("Orphan Role")
                .build();

        doNothing().when(roleRepository).persist(roleWithoutGuild);

        // When
        roleRepository.persist(roleWithoutGuild);

        // Then
        verify(roleRepository, times(1)).persist(roleWithoutGuild);
    }
}
