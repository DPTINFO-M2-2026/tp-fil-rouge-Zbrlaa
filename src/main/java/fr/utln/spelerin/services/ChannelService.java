package fr.utln.spelerin.services;

import fr.utln.spelerin.dto.ChannelDTO;
import fr.utln.spelerin.dto.createdto.ChannelCreateDTO;
import fr.utln.spelerin.dto.updatedto.ChannelUpdateDTO;
import fr.utln.spelerin.entities.Channel;
import fr.utln.spelerin.entities.Guild;
import fr.utln.spelerin.entities.Role;
import fr.utln.spelerin.mappers.ChannelMapper;
import fr.utln.spelerin.repositories.ChannelRepository;
import fr.utln.spelerin.repositories.GuildRepository;
import fr.utln.spelerin.repositories.RoleRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
// UUID import removed: using String IDs (Snowflake)

@ApplicationScoped
@Transactional
public class ChannelService {

	private final ChannelRepository channelRepository;
	private final GuildRepository guildRepository;
	private final RoleRepository roleRepository;

	@Inject
	public ChannelService(ChannelRepository channelRepository, GuildRepository guildRepository, RoleRepository roleRepository) {
		this.channelRepository = channelRepository;
		this.guildRepository = guildRepository;
		this.roleRepository = roleRepository;
	}

	public List<ChannelDTO> getAllChannels() {
		return channelRepository.listAll()
				.stream()
				.map(ChannelMapper::toDTO)
				.toList();
	}

	public Optional<ChannelDTO> getChannelById(String id) {
		return channelRepository.findByIdOptional(id)
				.map(ChannelMapper::toDTO);
	}

	public ChannelDTO createChannel(ChannelCreateDTO dto) {
		Guild guild = guildRepository.findById(dto.guildId());
		if (guild == null) {
			throw new IllegalArgumentException("Guild not found with ID: " + dto.guildId());
		}

		Channel channel = ChannelMapper.toEntity(dto, guild);
		channelRepository.persist(channel);
		return ChannelMapper.toDTO(channel);
	}

	public ChannelDTO updateChannel(String id, ChannelUpdateDTO dto) {
		Channel channel = channelRepository.findById(id);
		if (channel == null) {
			throw new NoSuchElementException("Channel not found with ID: " + id);
		}

		Guild guild = guildRepository.findById(dto.guildId());
		if (guild == null) {
			throw new IllegalArgumentException("Guild not found with ID: " + dto.guildId());
		}

		ChannelMapper.updateEntity(channel, dto, guild);
		// Pas besoin d'appeler persist() explicite ici car on est dans une transaction (@Transactional)
		// et l'entité est "attachée" (managed).
		return ChannelMapper.toDTO(channel);
	}

	public boolean deleteChannel(String id) {
		return channelRepository.deleteById(id);
	}

	public ChannelDTO addRoleToChannel(String channelId, String roleId) {
		Channel channel = channelRepository.findById(channelId);
		Role role = roleRepository.findById(roleId);

		if (channel == null) {
			throw new NoSuchElementException("Channel not found with ID: " + channelId);
		}
		if (role == null) {
			throw new NoSuchElementException("Role not found with ID: " + roleId);
		}

		channel.addRoleWithAccess(role);
		return ChannelMapper.toDTO(channel);
	}

	public ChannelDTO removeRoleFromChannel(String channelId, String roleId) {
		Channel channel = channelRepository.findById(channelId);
		Role role = roleRepository.findById(roleId);

		if (channel == null) {
			throw new NoSuchElementException("Channel not found with ID: " + channelId);
		}
		if (role == null) {
			throw new NoSuchElementException("Role not found with ID: " + roleId);
		}

		channel.removeRoleWithAccess(role);
		return ChannelMapper.toDTO(channel);
	}
}