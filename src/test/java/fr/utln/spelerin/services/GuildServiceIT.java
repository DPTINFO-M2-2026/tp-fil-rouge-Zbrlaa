package fr.utln.spelerin.services;

import fr.utln.spelerin.dto.GuildDTO;
import fr.utln.spelerin.dto.createdto.GuildCreateDTO;
import fr.utln.spelerin.dto.updatedto.GuildUpdateDTO;
import fr.utln.spelerin.entities.User;
import fr.utln.spelerin.mappers.GuildMapper;
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

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration Tests for GuildService (Failsafe).
 *
 * Utilise @QuarkusTest avec DevServices (PostgreSQL TestContainer).
 * Teste la logique métier du service de guildes avec ses relations.
 *
 * @author QA Automation Expert
 */
@QuarkusTest
@QuarkusTestResource(PostgresTestResource.class)
@Transactional
@DisplayName("GuildService Integration Tests (Failsafe)")
class GuildServiceIT {

    @Inject
    private GuildService guildService;

    @Inject
    private GuildRepository guildRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private GuildMapper guildMapper;

    private User ownerUser;

    @BeforeEach
    void setUp() {
        // Given: Clean up database
        guildRepository.deleteAll();
        userRepository.deleteAll();

        // Create owner user for tests
        ownerUser = User.builder()
                .id(5001L)
                .username("guildowner")
                .displayName("Guild Owner")
                .build();
        userRepository.persist(ownerUser);
    }

    @Nested
    @DisplayName("Guild Retrieval Operations")
    class GuildRetrievalOperations {

        @Test
        @DisplayName("Should retrieve all guilds from database via service")
        void testGetAllGuilds() {
            // Given
            GuildCreateDTO guildDto1 = new GuildCreateDTO(1L, "Guild One", 5001L);
            GuildCreateDTO guildDto2 = new GuildCreateDTO(2L, "Guild Two", 5001L);

            guildService.createGuild(guildDto1);
            guildService.createGuild(guildDto2);

            // When
            List<GuildDTO> allGuilds = guildService.getAllGuilds();

            // Then
            assertEquals(2, allGuilds.size());
            assertTrue(allGuilds.stream().anyMatch(g -> "Guild One".equals(g.name())));
            assertTrue(allGuilds.stream().anyMatch(g -> "Guild Two".equals(g.name())));
        }

        @Test
        @DisplayName("Should return empty list when no guilds exist")
        void testGetAllGuildsWhenEmpty() {
            // When
            List<GuildDTO> allGuilds = guildService.getAllGuilds();

            // Then
            assertEquals(0, allGuilds.size());
        }

        @Test
        @DisplayName("Should find guild by ID via service")
        void testGetGuildById() {
            // Given
            GuildCreateDTO dto = new GuildCreateDTO(10L, "Find Me Guild", 5001L);
            guildService.createGuild(dto);

            // When
            Optional<GuildDTO> result = guildService.getGuildById(10L);

            // Then
            assertTrue(result.isPresent());
            assertEquals("Find Me Guild", result.get().name());
        }

        @Test
        @DisplayName("Should return empty Optional when guild not found by ID")
        void testGetGuildByIdNotFound() {
            // When
            Optional<GuildDTO> result = guildService.getGuildById(999999L);

            // Then
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Guild Creation (Upsert Logic)")
    class GuildCreation {

        @Test
        @DisplayName("Should create new guild with valid owner")
        void testCreateNewGuild() {
            // Given
            GuildCreateDTO dto = new GuildCreateDTO(20L, "New Guild", 5001L);

            // When
            GuildDTO created = guildService.createGuild(dto);

            // Then
            assertNotNull(created);
            assertEquals("New Guild", created.name());
            assertEquals(20L, created.id());
            assertEquals(1, guildRepository.count());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when owner not found")
        void testCreateGuildWithInvalidOwner() {
            // Given
            GuildCreateDTO dto = new GuildCreateDTO(30L, "Invalid Guild", 999999L);

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> guildService.createGuild(dto));
        }

        @Test
        @DisplayName("Should update existing guild (upsert behavior)")
        void testCreateGuildUpsertBehavior() {
            // Given - Create initial guild
            GuildCreateDTO createDto = new GuildCreateDTO(40L, "Initial Guild", 5001L);
            guildService.createGuild(createDto);

            // When - Try to create same ID with different name
            GuildCreateDTO updateDto = new GuildCreateDTO(40L, "Updated Guild Name", 5001L);
            GuildDTO result = guildService.createGuild(updateDto);

            // Then
            assertEquals(1, guildRepository.count());
            assertEquals("Updated Guild Name", result.name());
        }

        @Test
        @DisplayName("Should persist guild with special characters in name")
        void testCreateGuildWithSpecialCharacters() {
            // Given
            GuildCreateDTO dto = new GuildCreateDTO(50L, "Guild@#$%^&*()", 5001L);

            // When
            GuildDTO created = guildService.createGuild(dto);

            // Then
            assertEquals("Guild@#$%^&*()", created.name());
        }
    }

    @Nested
    @DisplayName("Guild Update Operations")
    class GuildUpdateOperations {

        @Test
        @DisplayName("Should update existing guild")
        void testUpdateExistingGuild() {
            // Given
            GuildCreateDTO createDto = new GuildCreateDTO(60L, "Original Guild", 5001L);
            guildService.createGuild(createDto);

            // When
            GuildUpdateDTO updateDto = new GuildUpdateDTO("Updated Guild", 5001L);
            GuildDTO updated = guildService.updateGuild(60L, updateDto);

            // Then
            assertEquals("Updated Guild", updated.name());
        }

        @Test
        @DisplayName("Should throw NoSuchElementException when updating non-existent guild")
        void testUpdateNonExistentGuild() {
            // Given
            GuildUpdateDTO updateDto = new GuildUpdateDTO("Any Guild", 5001L);

            // When & Then
            assertThrows(NoSuchElementException.class, () -> guildService.updateGuild(999999L, updateDto));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when updating with invalid owner")
        void testUpdateGuildWithInvalidOwner() {
            // Given
            GuildCreateDTO createDto = new GuildCreateDTO(70L, "Guild", 5001L);
            guildService.createGuild(createDto);

            // When
            GuildUpdateDTO updateDto = new GuildUpdateDTO("Updated", 999999L);

            // Then
            assertThrows(IllegalArgumentException.class, () -> guildService.updateGuild(70L, updateDto));
        }
    }

    @Nested
    @DisplayName("Guild Deletion Operations")
    class GuildDeletionOperations {

        @Test
        @DisplayName("Should delete existing guild")
        void testDeleteExistingGuild() {
            // Given
            GuildCreateDTO dto = new GuildCreateDTO(80L, "Delete Me", 5001L);
            guildService.createGuild(dto);
            assertEquals(1, guildRepository.count());

            // When
            boolean deleted = guildService.deleteGuild(80L);

            // Then
            assertTrue(deleted);
            assertEquals(0, guildRepository.count());
        }

        @Test
        @DisplayName("Should return false when deleting non-existent guild")
        void testDeleteNonExistentGuild() {
            // When
            boolean deleted = guildService.deleteGuild(999999L);

            // Then
            assertFalse(deleted);
        }

        @Test
        @DisplayName("Should delete only specified guild")
        void testDeleteSpecificGuildOnly() {
            // Given
            GuildCreateDTO dto1 = new GuildCreateDTO(90L, "Keep Guild", 5001L);
            GuildCreateDTO dto2 = new GuildCreateDTO(91L, "Delete Guild", 5001L);
            guildService.createGuild(dto1);
            guildService.createGuild(dto2);

            // When
            guildService.deleteGuild(91L);

            // Then
            assertEquals(1, guildRepository.count());
            assertTrue(guildService.getGuildById(90L).isPresent());
            assertTrue(guildService.getGuildById(91L).isEmpty());
        }
    }

    @Nested
    @DisplayName("Guild-User Owner Management")
    class GuildUserOwnerManagement {

        @Test
        @DisplayName("Should verify guild has correct owner")
        void testGuildOwnerAssociation() {
            // Given
            GuildCreateDTO dto = new GuildCreateDTO(100L, "Owned Guild", 5001L);
            guildService.createGuild(dto);

            // When
            Optional<GuildDTO> result = guildService.getGuildById(100L);

            // Then
            assertTrue(result.isPresent());
            assertEquals(5001L, result.get().ownerId());
        }

        @Test
        @DisplayName("Should transfer guild ownership")
        void testTransferGuildOwnership() {
            // Given
            User newOwner = User.builder()
                    .id(5002L)
                    .username("newowner")
                    .displayName("New Owner")
                    .build();
            userRepository.persist(newOwner);

            GuildCreateDTO createDto = new GuildCreateDTO(110L, "Guild", 5001L);
            guildService.createGuild(createDto);

            // When
            GuildUpdateDTO updateDto = new GuildUpdateDTO("Guild", 5002L);
            GuildDTO updated = guildService.updateGuild(110L, updateDto);

            // Then
            assertEquals(5002L, updated.ownerId());
        }
    }

    @Nested
    @DisplayName("Data Validation and Edge Cases")
    class DataValidationAndEdgeCases {

        @Test
        @DisplayName("Should handle very long guild name")
        void testVeryLongGuildName() {
            // Given
            String longName = "G".repeat(500);
            GuildCreateDTO dto = new GuildCreateDTO(120L, longName, 5001L);

            // When
            GuildDTO created = guildService.createGuild(dto);

            // Then
            assertEquals(500, created.name().length());
        }

        @Test
        @DisplayName("Should handle unicode in guild name")
        void testUnicodeInGuildName() {
            // Given
            GuildCreateDTO dto = new GuildCreateDTO(130L, "公会 الحضر 길드", 5001L);

            // When
            GuildDTO created = guildService.createGuild(dto);

            // Then
            assertEquals("公会 الحضر 길드", created.name());
        }

        @Test
        @DisplayName("Should retrieve guild with correct data types")
        void testDataTypeConversion() {
            // Given
            GuildCreateDTO dto = new GuildCreateDTO(140L, "DataTest", 5001L);
            guildService.createGuild(dto);

            // When
            Optional<GuildDTO> result = guildService.getGuildById(140L);

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
        @DisplayName("Should handle multiple guild operations in single transaction")
        void testMultipleGuildOperationsInTransaction() {
            // Given

            GuildUpdateDTO updateDto = new GuildUpdateDTO("Updated Guild 1", 5001L);
            GuildDTO updated = guildService.updateGuild(150L, updateDto);

            // Then
            assertEquals(2, guildRepository.count());
            assertEquals("Updated Guild 1", updated.name());
        }
    }

    @Nested
    @DisplayName("Performance and Efficiency")
    class PerformanceAndEfficiency {

        @Test
        @DisplayName("Should retrieve multiple guilds efficiently")
        void testRetrieveMultipleGuildsEfficiently() {
            // Given
            for (int i = 0; i < 30; i++) {
                GuildCreateDTO dto = new GuildCreateDTO((long) (200 + i), "Guild " + i, 5001L);
                guildService.createGuild(dto);
            }

            // When
            List<GuildDTO> allGuilds = guildService.getAllGuilds();

            // Then
            assertEquals(30, allGuilds.size());
        }

        @Test
        @DisplayName("Should handle sequential create and update operations")
        void testSequentialGuildOperations() {
            // Given

            // When
            for (int i = 0; i < 10; i++) {
                GuildUpdateDTO updateDto = new GuildUpdateDTO("Sequential " + i, 5001L);
                guildService.updateGuild(300L, updateDto);
            }

            // Then
            Optional<GuildDTO> result = guildService.getGuildById(300L);
            assertTrue(result.isPresent());
            assertEquals("Sequential 9", result.get().name());
        }
    }
}
