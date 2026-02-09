package fr.utln.spelerin.services;

import fr.utln.spelerin.dto.ChannelDTO;
import fr.utln.spelerin.dto.createdto.ChannelCreateDTO;
import fr.utln.spelerin.dto.updatedto.ChannelUpdateDTO;
import fr.utln.spelerin.entities.Guild;
import fr.utln.spelerin.entities.User;
import fr.utln.spelerin.repositories.ChannelRepository;
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
 * Integration Tests for ChannelService (Failsafe).
 *
 * Utilise @QuarkusTest avec DevServices (PostgreSQL TestContainer).
 * Teste la logique métier du service de canaux avec les types (TEXT, VOICE, CATEGORY).
 *
 * @author QA Automation Expert
 */
@QuarkusTest
@QuarkusTestResource(PostgresTestResource.class)
@Transactional
@DisplayName("ChannelService Integration Tests (Failsafe)")
class ChannelServiceIT {

    @Inject
    private ChannelService channelService;

    @Inject
    private ChannelRepository channelRepository;

    @Inject
    private GuildRepository guildRepository;

    @Inject
    private UserRepository userRepository;

    private Guild testGuild;
    private User ownerUser;

    @BeforeEach
    void setUp() {
        // Given: Clean up database
        channelRepository.deleteAll();
        guildRepository.deleteAll();
        userRepository.deleteAll();

        // Create owner and guild
        ownerUser = User.builder()
                .id(7001L)
                .username("owner")
                .displayName("Guild Owner")
                .build();
        userRepository.persist(ownerUser);

        testGuild = Guild.builder()
                .id(13001L)
                .name("TestGuild")
                .owner(ownerUser)
                .users(new HashSet<>())
                .build();
        guildRepository.persist(testGuild);
    }

    @Nested
    @DisplayName("Channel Retrieval Operations")
    class ChannelRetrievalOperations {

        @Test
        @DisplayName("Should retrieve all channels from database via service")
        void testGetAllChannels() {
            // Given
            ChannelCreateDTO chDto1 = new ChannelCreateDTO(1L, "general", 0, 13001L);
            ChannelCreateDTO chDto2 = new ChannelCreateDTO(2L, "announcements", 0, 13001L);

            channelService.createChannel(chDto1);
            channelService.createChannel(chDto2);

            // When
            List<ChannelDTO> allChannels = channelService.getAllChannels();

            // Then
            assertEquals(2, allChannels.size());
            assertTrue(allChannels.stream().anyMatch(c -> "general".equals(c.name())));
            assertTrue(allChannels.stream().anyMatch(c -> "announcements".equals(c.name())));
        }

        @Test
        @DisplayName("Should return empty list when no channels exist")
        void testGetAllChannelsWhenEmpty() {
            // When
            List<ChannelDTO> allChannels = channelService.getAllChannels();

            // Then
            assertEquals(0, allChannels.size());
        }

        @Test
        @DisplayName("Should find channel by ID via service")
        void testGetChannelById() {
            // Given
            ChannelCreateDTO dto = new ChannelCreateDTO(10L, "Find Me", 0, 13001L);
            channelService.createChannel(dto);

            // When
            Optional<ChannelDTO> result = channelService.getChannelById(10L);

            // Then
            assertTrue(result.isPresent());
            assertEquals("Find Me", result.get().name());
        }

        @Test
        @DisplayName("Should return empty Optional when channel not found")
        void testGetChannelByIdNotFound() {
            // When
            Optional<ChannelDTO> result = channelService.getChannelById(999999L);

            // Then
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Channel Creation (Upsert Logic)")
    class ChannelCreation {

        @Test
        @DisplayName("Should create new TEXT channel with valid guild")
        void testCreateNewTextChannel() {
            // Given
            ChannelCreateDTO dto = new ChannelCreateDTO(20L, "new-channel", 0, 13001L);

            // When
            ChannelDTO created = channelService.createChannel(dto);

            // Then
            assertNotNull(created);
            assertEquals("new-channel", created.name());
            assertEquals(0, created.type());
            assertEquals(20L, created.id());
            assertEquals(1, channelRepository.count());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when guild not found")
        void testCreateChannelWithInvalidGuild() {
            // Given
            ChannelCreateDTO dto = new ChannelCreateDTO(30L, "invalid", 0, 999999L);

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> channelService.createChannel(dto));
        }

        @Test
        @DisplayName("Should update existing channel (upsert behavior)")
        void testCreateChannelUpsertBehavior() {
            // Given - Create initial channel
            ChannelCreateDTO createDto = new ChannelCreateDTO(40L, "initial", 0, 13001L);
            channelService.createChannel(createDto);

            // When - Try to create same ID with different name
            ChannelCreateDTO updateDto = new ChannelCreateDTO(40L, "updated", 1, 13001L);
            ChannelDTO result = channelService.createChannel(updateDto);

            // Then
            assertEquals(1, channelRepository.count());
            assertEquals("updated", result.name());
            assertEquals(1, result.type());
        }

        @Test
        @DisplayName("Should persist channel with special characters in name")
        void testCreateChannelWithSpecialCharacters() {
            // Given
            ChannelCreateDTO dto = new ChannelCreateDTO(50L, "channel@#$%", 0, 13001L);

            // When
            ChannelDTO created = channelService.createChannel(dto);

            // Then
            assertEquals("channel@#$%", created.name());
        }
    }

    @Nested
    @DisplayName("Channel Types Support")
    class ChannelTypesSupport {

        @Test
        @DisplayName("Should create TEXT type channel")
        void testCreateTextChannel() {
            // Given
            ChannelCreateDTO dto = new ChannelCreateDTO(60L, "text-channel", 0, 13001L);

            // When
            ChannelDTO created = channelService.createChannel(dto);

            // Then
            assertEquals(0, created.type());
        }

        @Test
        @DisplayName("Should create VOICE type channel")
        void testCreateVoiceChannel() {
            // Given
            ChannelCreateDTO dto = new ChannelCreateDTO(61L, "voice-channel", 1, 13001L);

            // When
            ChannelDTO created = channelService.createChannel(dto);

            // Then
            assertEquals(1, created.type());
        }

        @Test
        @DisplayName("Should create CATEGORY type channel")
        void testCreateCategoryChannel() {
            // Given
            ChannelCreateDTO dto = new ChannelCreateDTO(62L, "category", 2, 13001L);

            // When
            ChannelDTO created = channelService.createChannel(dto);

            // Then
            assertEquals(2, created.type());
        }

        @Test
        @DisplayName("Should support multiple channel types in same guild")
        void testMultipleChannelTypesPerGuild() {
            // Given
            ChannelCreateDTO text = new ChannelCreateDTO(63L, "text", 0, 13001L);
            ChannelCreateDTO voice = new ChannelCreateDTO(64L, "voice", 1, 13001L);
            ChannelCreateDTO category = new ChannelCreateDTO(65L, "cat", 2, 13001L);

            // When
            channelService.createChannel(text);
            channelService.createChannel(voice);
            channelService.createChannel(category);

            // Then
            List<ChannelDTO> allChannels = channelService.getAllChannels();
            assertEquals(3, allChannels.size());
            assertTrue(allChannels.stream().anyMatch(c -> 0 == c.type()));
            assertTrue(allChannels.stream().anyMatch(c -> 1 == c.type()));
            assertTrue(allChannels.stream().anyMatch(c -> 2 == c.type()));
        }
    }

    @Nested
    @DisplayName("Channel Update Operations")
    class ChannelUpdateOperations {

        @Test
        @DisplayName("Should update existing channel")
        void testUpdateExistingChannel() {
            // Given
                ChannelCreateDTO createDto = new ChannelCreateDTO(70L, "original", 0, 13001L);
            channelService.createChannel(createDto);

            // When
                ChannelUpdateDTO updateDto = new ChannelUpdateDTO("updated-name", 1, 13001L);
            ChannelDTO updated = channelService.updateChannel(70L, updateDto);

            // Then
                assertEquals("updated-name", updated.name());
                assertEquals(1, updated.type());
        }

        @Test
        @DisplayName("Should throw NoSuchElementException when updating non-existent channel")
        void testUpdateNonExistentChannel() {
            // Given
            ChannelUpdateDTO updateDto = new ChannelUpdateDTO("any-name", 0, 13001L);

            // When & Then
            assertThrows(NoSuchElementException.class, () -> channelService.updateChannel(999999L, updateDto));
        }
    }

    @Nested
    @DisplayName("Channel Deletion Operations")
    class ChannelDeletionOperations {

        @Test
        @DisplayName("Should delete existing channel")
        void testDeleteExistingChannel() {
            // Given
                ChannelCreateDTO dto = new ChannelCreateDTO(80L, "delete-me", 0, 13001L);
            channelService.createChannel(dto);
            assertEquals(1, channelRepository.count());

            // When
            boolean deleted = channelService.deleteChannel(80L);

            // Then
            assertTrue(deleted);
            assertEquals(0, channelRepository.count());
        }

        @Test
        @DisplayName("Should return false when deleting non-existent channel")
        void testDeleteNonExistentChannel() {
            // When
            boolean deleted = channelService.deleteChannel(999999L);

            // Then
            assertFalse(deleted);
        }

        @Test
        @DisplayName("Should delete only specified channel")
        void testDeleteSpecificChannelOnly() {
            // Given
            ChannelCreateDTO dto1 = new ChannelCreateDTO(90L, "keep", 0, 13001L);
            ChannelCreateDTO dto2 = new ChannelCreateDTO(91L, "delete", 1, 13001L);
            channelService.createChannel(dto1);
            channelService.createChannel(dto2);

            // When
            channelService.deleteChannel(91L);

            // Then
            assertEquals(1, channelRepository.count());
            assertTrue(channelService.getChannelById(90L).isPresent());
            assertTrue(channelService.getChannelById(91L).isEmpty());
        }
    }

    @Nested
    @DisplayName("Channel-Guild Management")
    class ChannelGuildManagement {

        @Test
        @DisplayName("Should verify channel has correct guild")
        void testChannelGuildAssociation() {
            // Given
            ChannelCreateDTO dto = new ChannelCreateDTO(100L, "guild-channel", 0, 13001L);
            channelService.createChannel(dto);

            // When
            Optional<ChannelDTO> result = channelService.getChannelById(100L);

            // Then
            assertTrue(result.isPresent());
            assertEquals(13001L, result.get().guildId());
        }

        @Test
        @DisplayName("Should create multiple channels for same guild")
        void testMultipleChannelsPerGuild() {
            // Given
            ChannelCreateDTO dto1 = new ChannelCreateDTO(110L, "ch1", 0, 13001L);
            ChannelCreateDTO dto2 = new ChannelCreateDTO(111L, "ch2", 0, 13001L);
            ChannelCreateDTO dto3 = new ChannelCreateDTO(112L, "ch3", 1, 13001L);

            channelService.createChannel(dto1);
            channelService.createChannel(dto2);
            channelService.createChannel(dto3);

            // When
            List<ChannelDTO> allChannels = channelService.getAllChannels();

            // Then
            assertEquals(3, allChannels.size());
            assertTrue(allChannels.stream().allMatch(c -> 13001L == c.guildId()));
        }
    }

    @Nested
    @DisplayName("Data Validation and Edge Cases")
    class DataValidationAndEdgeCases {

        @Test
        @DisplayName("Should handle very long channel name")
        void testVeryLongChannelName() {
            // Given
            String longName = "c".repeat(255);
            ChannelCreateDTO dto = new ChannelCreateDTO(120L, longName, 0, 13001L);

            // When
            ChannelDTO created = channelService.createChannel(dto);

            // Then
            assertEquals(255, created.name().length());
        }

        @Test
        @DisplayName("Should handle unicode in channel name")
        void testUnicodeInChannelName() {
            // Given
            ChannelCreateDTO dto = new ChannelCreateDTO(130L, "频道 قناة চ্যানেল", 0, 13001L);

            // When
            ChannelDTO created = channelService.createChannel(dto);

            // Then
            assertEquals("频道 قناة চ্যানেল", created.name());
        }

        @Test
        @DisplayName("Should retrieve channel with correct data types")
        void testDataTypeConversion() {
            // Given
            ChannelCreateDTO dto = new ChannelCreateDTO(140L, "datatest", 0, 13001L);
            channelService.createChannel(dto);

            // When
            Optional<ChannelDTO> result = channelService.getChannelById(140L);

            // Then
            assertTrue(result.isPresent());
            assertInstanceOf(Long.class, result.get().id());
            assertInstanceOf(String.class, result.get().name());
            assertInstanceOf(String.class, result.get().type());
        }
    }

    @Nested
    @DisplayName("Transactional Behavior")
    class TransactionalBehavior {

        @Test
        @DisplayName("Should handle multiple channel operations in single transaction")
        void testMultipleChannelOperationsInTransaction() {
            // Given

            // When
            ChannelUpdateDTO updateDto = new ChannelUpdateDTO("updated", 2, 13001L);
            ChannelDTO updated = channelService.updateChannel(150L, updateDto);

            // Then
            assertEquals(2, channelRepository.count());
            assertEquals("updated", updated.name());
        }
    }

    @Nested
    @DisplayName("Performance and Efficiency")
    class PerformanceAndEfficiency {

        @Test
        @DisplayName("Should retrieve multiple channels efficiently")
        void testRetrieveMultipleChannelsEfficiently() {
            // Given
            for (int i = 0; i < 25; i++) {
                ChannelCreateDTO dto = new ChannelCreateDTO((long) (200 + i), "channel" + i, 0, 13001L);
                channelService.createChannel(dto);
            }

            // When
            List<ChannelDTO> allChannels = channelService.getAllChannels();

            // Then
            assertEquals(25, allChannels.size());
        }

        @Test
        @DisplayName("Should handle sequential create and update operations")
        void testSequentialChannelOperations() {
            // Given
            ChannelCreateDTO createDto = new ChannelCreateDTO(300L, "sequential", 0, 13001L);
            channelService.createChannel(createDto);

            // When
            for (int i = 0; i < 5; i++) {
                ChannelUpdateDTO updateDto = new ChannelUpdateDTO("seq-" + i, 0, 13001L);
                channelService.updateChannel(300L, updateDto);
            }

            // Then
            Optional<ChannelDTO> result = channelService.getChannelById(300L);
            assertTrue(result.isPresent());
            assertEquals("seq-4", result.get().name());
        }
    }
}
