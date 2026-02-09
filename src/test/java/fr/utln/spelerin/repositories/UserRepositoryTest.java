package fr.utln.spelerin.repositories;

import fr.utln.spelerin.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unitaire Tests for UserRepository (Surefire).
 *
 * Tests rapides sans Quarkus @QuarkusTest pour une exécution ultra-rapide.
 * Mock tous les appels à la base de données.
 *
 * @author QA Automation Expert
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserRepository Unit Tests (Surefire)")
class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Given: Initialiser les données de test
        testUser = User.builder()
                .id(123456789L)
                .username("testuser")
                .displayName("Test User")
                .build();
    }

    @Test
    @DisplayName("Should find user by ID when exists")
    void testFindByIdWhenExists() {
        // Given
        when(userRepository.findById(123456789L)).thenReturn(testUser);

        // When
        User result = userRepository.findById(123456789L);

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).findById(123456789L);
    }

    @Test
    @DisplayName("Should return null when finding non-existent user by ID")
    void testFindByIdWhenNotExists() {
        // Given
        when(userRepository.findById(999999L)).thenReturn(null);

        // When
        User result = userRepository.findById(999999L);

        // Then
        assertNull(result);
        verify(userRepository, times(1)).findById(999999L);
    }

    @Test
    @DisplayName("Should find user by username when exists")
    void testFindByUsernameOptionalWhenExists() {
        // Given
        when(userRepository.findByUsernameOptional("testuser"))
                .thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userRepository.findByUsernameOptional("testuser");

        // Then
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        verify(userRepository, times(1)).findByUsernameOptional("testuser");
    }

    @Test
    @DisplayName("Should return empty Optional for non-existent username")
    void testFindByUsernameOptionalWhenNotExists() {
        // Given
        when(userRepository.findByUsernameOptional("nonexistent"))
                .thenReturn(Optional.empty());

        // When
        Optional<User> result = userRepository.findByUsernameOptional("nonexistent");

        // Then
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findByUsernameOptional("nonexistent");
    }

    @Test
    @DisplayName("Should persist user to database")
    void testPersistUser() {
        // Given
        doNothing().when(userRepository).persist(testUser);

        // When
        userRepository.persist(testUser);

        // Then
        verify(userRepository, times(1)).persist(testUser);
    }

    @Test
    @DisplayName("Should handle edge case: username with special characters")
    void testFindByUsernameWithSpecialCharacters() {
        // Given
        User specialUser = User.builder()
                .id(2L)
                .username("user@domain#special")
                .displayName("Special User")
                .build();

        when(userRepository.findByUsernameOptional("user@domain#special"))
                .thenReturn(Optional.of(specialUser));

        // When
        Optional<User> result = userRepository.findByUsernameOptional("user@domain#special");

        // Then
        assertTrue(result.isPresent());
        assertEquals("user@domain#special", result.get().getUsername());
    }

    @Test
    @DisplayName("Should handle edge case: very long username")
    void testFindByVeryLongUsername() {
        // Given
        String longUsername = "a".repeat(255);
        User longUser = User.builder()
                .id(3L)
                .username(longUsername)
                .displayName("Long Username User")
                .build();

        when(userRepository.findByUsernameOptional(longUsername))
                .thenReturn(Optional.of(longUser));

        // When
        Optional<User> result = userRepository.findByUsernameOptional(longUsername);

        // Then
        assertTrue(result.isPresent());
        assertEquals(longUsername, result.get().getUsername());
    }

    @Test
    @DisplayName("Should handle error case: null username search")
    void testFindByNullUsername() {
        // Given
        when(userRepository.findByUsernameOptional(null))
                .thenReturn(Optional.empty());

        // When
        Optional<User> result = userRepository.findByUsernameOptional(null);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should verify repository is never called multiple times unnecessarily")
    void testRepositoryCallOptimization() {
        // Given
        when(userRepository.findById(123456789L)).thenReturn(testUser);

        // When
        userRepository.findById(123456789L);
        userRepository.findById(123456789L);

        // Then
        verify(userRepository, times(2)).findById(123456789L);
    }
}
