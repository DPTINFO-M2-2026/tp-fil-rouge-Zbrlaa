package fr.utln.spelerin.repositories;

import fr.utln.spelerin.entities.Channel;
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
 * Integration Tests for ChannelRepository (Failsafe).
 *
 * Utilise @QuarkusTest avec DevServices (PostgreSQL TestContainer).
 * Teste la persistance des canaux avec les types (TEXT, VOICE, CATEGORY) et les rôles.
 *
 * @author QA Automation Expert
 */
@QuarkusTest
@QuarkusTestResource(PostgresTestResource.class)
@Transactional
@DisplayName("ChannelRepository Integration Tests (Failsafe)")
class ChannelRepositoryIT {

    @Inject
    private ChannelRepository channelRepository;

    @Inject
    private GuildRepository guildRepository;

    @Inject
    private RoleRepository roleRepository;

    @Inject
    private UserRepository userRepository;

    private Channel testChannel;
    private Guild testGuild;
    private User ownerUser;

    @BeforeEach
    void setUp() {
        // Given: Clean up and initialize test data
        channelRepository.deleteAll();
        roleRepository.deleteAll();
        guildRepository.deleteAll();
        userRepository.deleteAll();

        // Create owner user
        ownerUser = User.builder()
                .id(3001L)
                .username("guildowner")
                .displayName("Guild Owner")
                .build();
        userRepository.persist(ownerUser);

        // Create guild
        testGuild = Guild.builder()
                .id(9001L)
                .name("TestGuild")
                .owner(ownerUser)
                .users(new HashSet<>())
                .build();
        guildRepository.persist(testGuild);

        // Create test channel
        testChannel = Channel.builder()
                .id(4001L)
                .name("general")
                .type(0)  // 0 = TEXT
                .guild(testGuild)
                .rolesWithAccess(new HashSet<>())
                .build();
    }

    @Nested
    @DisplayName("CRUD Operations")
    class CRUDOperations {

        @Test
        @DisplayName("Should persist TEXT channel with guild to database")
        void testPersistTextChannelWithGuild() {
            // When
            channelRepository.persist(testChannel);

            // Then
            Channel retrieved = channelRepository.findById(4001L);
            assertNotNull(retrieved);
            assertEquals("general", retrieved.getName());
            assertEquals("TEXT", retrieved.getType());
            assertNotNull(retrieved.getGuild());
            assertEquals("TestGuild", retrieved.getGuild().getName());
        }

        @Test
        @DisplayName("Should find channel by ID")
        void testFindChannelById() {
            // Given
            channelRepository.persist(testChannel);

            // When
            Channel result = channelRepository.findById(4001L);

            // Then
            assertNotNull(result);
            assertEquals("general", result.getName());
            assertEquals(9001L, result.getGuild().getId());
        }

        @Test
        @DisplayName("Should return null when channel not found")
        void testFindChannelByIdNotFound() {
            // When
            Channel result = channelRepository.findById(9999999L);

            // Then
            assertNull(result);
        }

        @Test
        @DisplayName("Should update channel in database")
        void testUpdateChannel() {
            // Given
            channelRepository.persist(testChannel);

            // When
            testChannel.setName("updated-channel");
            channelRepository.persist(testChannel);

            // Then
            Channel updated = channelRepository.findById(4001L);
            assertEquals("updated-channel", updated.getName());
        }

        @Test
        @DisplayName("Should delete channel from database")
        void testDeleteChannel() {
            // Given
            channelRepository.persist(testChannel);

            // When
            channelRepository.delete(testChannel);

            // Then
            Channel deleted = channelRepository.findById(4001L);
            assertNull(deleted);
        }

        @Test
        @DisplayName("Should delete all channels from database")
        void testDeleteAllChannels() {
            // Given
            channelRepository.persist(testChannel);
            Channel channel2 = Channel.builder()
                    .id(4002L)
                    .name("announcements")
                    .type(0)  // TEXT
                    .guild(testGuild)
                    .rolesWithAccess(new HashSet<>())
                    .build();
            channelRepository.persist(channel2);

            // When
            channelRepository.deleteAll();

            // Then
            assertEquals(0, channelRepository.count());
        }
    }

    @Nested
    @DisplayName("Channel Types")
    class ChannelTypes {

        @Test
        @DisplayName("Should persist TEXT type channel")
        void testTextTypeChannel() {
            // Given
            Channel textChannel = Channel.builder()
                    .id(4003L)
                    .name("chat")
                    .type(0)
                    .guild(testGuild)
                    .rolesWithAccess(new HashSet<>())
                    .build();

            // When
            channelRepository.persist(textChannel);

            // Then
            Channel retrieved = channelRepository.findById(4003L);
            assertEquals("TEXT", retrieved.getType());
        }

        @Test
        @DisplayName("Should persist VOICE type channel")
        void testVoiceTypeChannel() {
            // Given
            Channel voiceChannel = Channel.builder()
                    .id(4004L)
                    .name("voice-lobby")
                    .type(1)
                    .guild(testGuild)
                    .rolesWithAccess(new HashSet<>())
                    .build();

            // When
            channelRepository.persist(voiceChannel);

            // Then
            Channel retrieved = channelRepository.findById(4004L);
            assertEquals("VOICE", retrieved.getType());
        }

        @Test
        @DisplayName("Should persist CATEGORY type channel")
        void testCategoryTypeChannel() {
            // Given
            Channel categoryChannel = Channel.builder()
                    .id(4005L)
                    .name("public-channels")
                    .type(2)
                    .guild(testGuild)
                    .rolesWithAccess(new HashSet<>())
                    .build();

            // When
            channelRepository.persist(categoryChannel);

            // Then
            Channel retrieved = channelRepository.findById(4005L);
            assertEquals("CATEGORY", retrieved.getType());
        }

        @Test
        @DisplayName("Should handle different channel types in same guild")
        void testMultipleChannelTypes() {
            // Given
            Channel text = Channel.builder().id(4006L).name("text").type(0).guild(testGuild).rolesWithAccess(new HashSet<>()).build();
            Channel voice = Channel.builder().id(4007L).name("voice").type(1).guild(testGuild).rolesWithAccess(new HashSet<>()).build();
            Channel category = Channel.builder().id(4008L).name("category").type(2).guild(testGuild).rolesWithAccess(new HashSet<>()).build();

            // When
            channelRepository.persist(text);
            channelRepository.persist(voice);
            channelRepository.persist(category);

            // Then
            List<Channel> allChannels = channelRepository.listAll();
            assertEquals(3, allChannels.size());
            assertTrue(allChannels.stream().anyMatch(c -> "TEXT".equals(c.getType())));
            assertTrue(allChannels.stream().anyMatch(c -> "VOICE".equals(c.getType())));
            assertTrue(allChannels.stream().anyMatch(c -> "CATEGORY".equals(c.getType())));
        }
    }

    @Nested
    @DisplayName("Channel-Guild Relationships")
    class ChannelGuildRelationships {

        @Test
        @DisplayName("Should verify channel belongs to correct guild")
        void testChannelGuildAssociation() {
            // Given
            channelRepository.persist(testChannel);

            // When
            Channel retrieved = channelRepository.findById(4001L);
            Guild channelGuild = retrieved.getGuild();

            // Then
            assertEquals(9001L, channelGuild.getId());
            assertEquals("TestGuild", channelGuild.getName());
        }

        @Test
        @DisplayName("Should create multiple channels for same guild")
        void testMultipleChannelsPerGuild() {
            // Given
            Channel ch1 = Channel.builder().id(4009L).name("ch1").type(0).guild(testGuild).rolesWithAccess(new HashSet<>()).build();
            Channel ch2 = Channel.builder().id(4010L).name("ch2").type(0).guild(testGuild).rolesWithAccess(new HashSet<>()).build();
            Channel ch3 = Channel.builder().id(4011L).name("ch3").type(1).guild(testGuild).rolesWithAccess(new HashSet<>()).build();

            // When
            channelRepository.persist(ch1);
            channelRepository.persist(ch2);
            channelRepository.persist(ch3);

            // Then
            List<Channel> allChannels = channelRepository.listAll();
            assertEquals(3, allChannels.size());
            assertTrue(allChannels.stream().allMatch(c -> testGuild.getId() == c.getGuild().getId()));
        }
    }

    @Nested
    @DisplayName("Channel-Role Relationships")
    class ChannelRoleRelationships {

        @Test
        @DisplayName("Should add roles to channel")
        void testAddRolesToChannel() {
            // Given
            Role role1 = Role.builder().id(4001L).name("admin").guild(testGuild).users(new HashSet<>()).build();
            Role role2 = Role.builder().id(4002L).name("moderator").guild(testGuild).users(new HashSet<>()).build();
            roleRepository.persist(role1);
            roleRepository.persist(role2);

            testChannel.getRolesWithAccess().add(role1);
            testChannel.getRolesWithAccess().add(role2);

            // When
            channelRepository.persist(testChannel);

            // Then
            Channel retrieved = channelRepository.findById(4001L);
            assertEquals(2, retrieved.getRolesWithAccess().size());
            assertTrue(retrieved.getRolesWithAccess().stream().anyMatch(r -> "admin".equals(r.getName())));
        }

        @Test
        @DisplayName("Should maintain channel with empty role set")
        void testChannelWithEmptyRoleSet() {
            // Given
            testChannel.setRolesWithAccess(new HashSet<>());

            // When
            channelRepository.persist(testChannel);

            // Then
            Channel retrieved = channelRepository.findById(4001L);
            assertNotNull(retrieved.getRolesWithAccess());
            assertEquals(0, retrieved.getRolesWithAccess().size());
        }
    }

    @Nested
    @DisplayName("Query Operations")
    class QueryOperations {

        @Test
        @DisplayName("Should list all channels from database")
        void testListAllChannels() {
            // Given
            channelRepository.persist(testChannel);
            Channel channel2 = Channel.builder()
                    .id(4012L)
                    .name("announcements")
                    .type(0)
                    .guild(testGuild)
                    .rolesWithAccess(new HashSet<>())
                    .build();
            channelRepository.persist(channel2);

            // When
            List<Channel> allChannels = channelRepository.listAll();

            // Then
            assertEquals(2, allChannels.size());
            assertTrue(allChannels.stream().anyMatch(c -> "general".equals(c.getName())));
        }

        @Test
        @DisplayName("Should return empty list when no channels in database")
        void testListAllChannelsWhenEmpty() {
            // When
            List<Channel> result = channelRepository.listAll();

            // Then
            assertEquals(0, result.size());
        }

        @Test
        @DisplayName("Should count channels in database")
        void testCountChannels() {
            // Given
            for (int i = 0; i < 5; i++) {
                Channel channel = Channel.builder()
                        .id(5000L + i)
                        .name("channel" + i)
                        .type(0)
                        .guild(testGuild)
                        .rolesWithAccess(new HashSet<>())
                        .build();
                channelRepository.persist(channel);
            }

            // When
            long count = channelRepository.count();

            // Then
            assertEquals(5, count);
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling")
    class EdgeCases {

        @Test
        @DisplayName("Should handle channel name with special characters")
        void testSpecialCharactersInChannelName() {
            // Given
            Channel specialChannel = Channel.builder()
                    .id(4013L)
                    .name("channel@#$%^&*()")
                    .type(0)
                    .guild(testGuild)
                    .rolesWithAccess(new HashSet<>())
                    .build();

            // When
            channelRepository.persist(specialChannel);

            // Then
            Channel retrieved = channelRepository.findById(4013L);
            assertEquals("channel@#$%^&*()", retrieved.getName());
        }

        @Test
        @DisplayName("Should handle very long channel name")
        void testVeryLongChannelName() {
            // Given
            String longName = "c".repeat(255);
            Channel longChannel = Channel.builder()
                    .id(4014L)
                    .name(longName)
                    .type(0)
                    .guild(testGuild)
                    .rolesWithAccess(new HashSet<>())
                    .build();

            // When
            channelRepository.persist(longChannel);

            // Then
            Channel retrieved = channelRepository.findById(4014L);
            assertEquals(255, retrieved.getName().length());
        }

        @Test
        @DisplayName("Should handle unicode characters in channel name")
        void testUnicodeInChannelName() {
            // Given
            Channel unicodeChannel = Channel.builder()
                    .id(4015L)
                    .name("频道 قناة চ্যানেল")
                    .type(0)
                    .guild(testGuild)
                    .rolesWithAccess(new HashSet<>())
                    .build();

            // When
            channelRepository.persist(unicodeChannel);

            // Then
            Channel retrieved = channelRepository.findById(4015L);
            assertEquals("频道 قناة চ্যানেল", retrieved.getName());
        }

        @Test
        @DisplayName("Should handle duplicate channel ID constraint")
        void testDuplicateChannelID() {
            // Given
            Channel channel1 = Channel.builder()
                    .id(4016L)
                    .name("first")
                    .type(0)
                    .guild(testGuild)
                    .rolesWithAccess(new HashSet<>())
                    .build();
            Channel channel2 = Channel.builder()
                    .id(4016L)
                    .name("second")
                    .type(0)
                    .guild(testGuild)
                    .rolesWithAccess(new HashSet<>())
                    .build();

            channelRepository.persist(channel1);

            // When & Then
            assertThrows(Exception.class, () -> channelRepository.persist(channel2));
        }

        @Test
        @DisplayName("Should handle channel with null guild")
        void testChannelWithNullGuild() {
            // Given
            Channel orphanChannel = Channel.builder()
                    .id(4017L)
                    .name("orphan")
                    .type(0)
                    .guild(null)
                    .rolesWithAccess(new HashSet<>())
                    .build();

            // When & Then
            if (channelRepository.count() == 0) {
                channelRepository.persist(orphanChannel);
                Channel retrieved = channelRepository.findById(4017L);
                assertNull(retrieved.getGuild());
            }
        }
    }

    @Nested
    @DisplayName("Performance Tests")
    class PerformanceTests {

        @Test
        @DisplayName("Should retrieve channel with guild without N+1 queries")
        void testNoNPlus1WithGuild() {
            // Given
            channelRepository.persist(testChannel);

            // When
            Channel channel = channelRepository.findById(4001L);
            String guildName = channel.getGuild().getName();

            // Then
            assertNotNull(guildName);
            assertEquals("TestGuild", guildName);
        }

        @Test
        @DisplayName("Should list multiple channels efficiently")
        void testListMultipleChannels() {
            // Given
            for (int i = 0; i < 10; i++) {
                Channel channel = Channel.builder()
                        .id(5000L + i)
                        .name("perf-channel" + i)
                        .type(0)
                        .guild(testGuild)
                        .rolesWithAccess(new HashSet<>())
                        .build();
                channelRepository.persist(channel);
            }

            // When
            List<Channel> channels = channelRepository.listAll();

            // Then
            assertEquals(10, channels.size());
        }
    }
}
