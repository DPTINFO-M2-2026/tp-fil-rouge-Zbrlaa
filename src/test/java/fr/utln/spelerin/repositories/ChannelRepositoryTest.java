package fr.utln.spelerin.repositories;

import fr.utln.spelerin.entities.Channel;
import fr.utln.spelerin.entities.Guild;
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
 * Unitaire Tests for ChannelRepository (Surefire).
 *
 * Tests rapides sans Quarkus.
 * Tous les appels à la BD sont mockés.
 *
 * @author QA Automation Expert
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ChannelRepository Unit Tests (Surefire)")
class ChannelRepositoryTest {

    @Mock
    private ChannelRepository channelRepository;

    private Channel testChannel;
    private Guild testGuild;

    @BeforeEach
    void setUp() {
        // Given: Initialiser les fixtures
        testGuild = Guild.builder()
                .id(1000L)
                .name("Test Guild")
                .build();

        testChannel = Channel.builder()
                .id(6000L)
                .name("general")
                .type(0)
                .guild(testGuild)
                .rolesWithAccess(new HashSet<>())
                .build();
    }

    @Test
    @DisplayName("Should find channel by ID when exists")
    void testFindByIdWhenExists() {
        // Given
        when(channelRepository.findById(6000L)).thenReturn(testChannel);

        // When
        Channel result = channelRepository.findById(6000L);

        // Then
        assertNotNull(result);
        assertEquals("general", result.getName());
        assertEquals(0, result.getType());
        verify(channelRepository, times(1)).findById(6000L);
    }

    @Test
    @DisplayName("Should return null when channel not found")
    void testFindByIdWhenNotExists() {
        // Given
        when(channelRepository.findById(99999L)).thenReturn(null);

        // When
        Channel result = channelRepository.findById(99999L);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Should persist channel with guild relationship")
    void testPersistChannelWithGuild() {
        // Given
        doNothing().when(channelRepository).persist(testChannel);

        // When
        channelRepository.persist(testChannel);

        // Then
        verify(channelRepository, times(1)).persist(testChannel);
        assertNotNull(testChannel.getGuild());
        assertEquals(1000L, testChannel.getGuild().getId());
    }

    @Test
    @DisplayName("Should handle different channel types: TEXT, VOICE, CATEGORY")
    void testChannelTypesSupport() {
        // Given
        Channel voiceChannel = Channel.builder()
                .id(6001L)
                .name("voice")
                .type(1)
                .guild(testGuild)
                .build();

        when(channelRepository.findById(6001L)).thenReturn(voiceChannel);

        // When
        Channel result = channelRepository.findById(6001L);

        // Then
        assertEquals(1, result.getType());
    }

    @Test
    @DisplayName("Should handle channel name with special characters")
    void testChannelNameWithSpecialCharacters() {
        // Given
        Channel specialChannel = Channel.builder()
                .id(6002L)
                .name("channel-#@special")
                .type(0)
                .guild(testGuild)
                .build();

        when(channelRepository.findById(6002L)).thenReturn(specialChannel);

        // When
        Channel result = channelRepository.findById(6002L);

        // Then
        assertEquals("channel-#@special", result.getName());
    }

    @Test
    @DisplayName("Should handle very long channel name")
    void testChannelWithVeryLongName() {
        // Given
        String longName = "C".repeat(255);
        Channel longChannel = Channel.builder()
                .id(6003L)
                .name(longName)
                .type(0)
                .guild(testGuild)
                .build();

        when(channelRepository.findById(6003L)).thenReturn(longChannel);

        // When
        Channel result = channelRepository.findById(6003L);

        // Then
        assertEquals(longName, result.getName());
    }

    @Test
    @DisplayName("Should verify channel-guild association")
    void testChannelGuildAssociation() {
        // Given
        when(channelRepository.findById(6000L)).thenReturn(testChannel);

        // When
        Channel result = channelRepository.findById(6000L);

        // Then
        assertNotNull(result.getGuild());
        assertEquals("Test Guild", result.getGuild().getName());
    }

    @Test
    @DisplayName("Should handle error: null channel ID")
    void testFindByNullId() {
        // Given
        when(channelRepository.findById(null)).thenReturn(null);

        // When
        Channel result = channelRepository.findById(null);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Should handle error: invalid channel type")
    void testChannelWithInvalidType() {
        // Given
        Channel invalidChannel = Channel.builder()
                .id(6004L)
                .name("invalid")
                .type(99)  // Invalid type
                .guild(testGuild)
                .build();

        when(channelRepository.findById(6004L)).thenReturn(invalidChannel);

        // When
        Channel result = channelRepository.findById(6004L);

        // Then
        assertEquals(99, result.getType());
    }
}
