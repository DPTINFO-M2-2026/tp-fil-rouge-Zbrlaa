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

@ApplicationScoped
@Transactional
public class ChannelService {

	private final ChannelMapper channelMapper;

	private final ChannelRepository channelRepository;
	private final GuildRepository guildRepository;
	private final RoleRepository roleRepository;

	@Inject
	public ChannelService(ChannelMapper channelMapper, ChannelRepository channelRepository, GuildRepository guildRepository, RoleRepository roleRepository) {
		this.channelMapper = channelMapper;
		this.channelRepository = channelRepository;
		this.guildRepository = guildRepository;
		this.roleRepository = roleRepository;
	}

	public List<ChannelDTO> getAllChannels() {
		return channelRepository.listAll()
				.stream()
				.map(channelMapper::toDTO)
				.toList();
	}

	public Optional<ChannelDTO> getChannelById(Long id) {
		return channelRepository.findByIdOptional(id)
				.map(channelMapper::toDTO);
	}

	//Upsert
	public ChannelDTO createChannel(ChannelCreateDTO dto) {
		Channel existing = channelRepository.findById(dto.id());

		Guild guild = guildRepository.findById(dto.guildId());
		if (guild == null) {
			throw new IllegalArgumentException("Guild not found with ID: " + dto.guildId());
		}

		if (existing != null) {
			existing.setName(dto.name());
			existing.setType(dto.type());
			if (existing.getGuildId() != dto.guildId()) {
				existing.setGuild(guild); 
			}
			return channelMapper.toDTO(existing);
		}
		
		Channel channel = channelMapper.toEntity(dto, guild);
		channelRepository.persist(channel);
		return channelMapper.toDTO(channel);
	}

	public ChannelDTO updateChannel(Long id, ChannelUpdateDTO dto) {
		Channel channel = channelRepository.findById(id);
		if (channel == null) {
			throw new NoSuchElementException("Channel not found with ID: " + id);
		}

		Guild guild = guildRepository.findById(dto.guildId());
		if (guild == null) {
			throw new IllegalArgumentException("Guild not found with ID: " + dto.guildId());
		}

		channelMapper.updateEntity(channel, dto, guild);
		// Pas besoin d'appeler persist() explicite ici car on est dans une transaction (@Transactional)
		// et l'entité est "attachée" (managed).
		return channelMapper.toDTO(channel);
	}

	public boolean deleteChannel(Long id) {
		return channelRepository.deleteById(id);
	}

	public ChannelDTO addRoleToChannel(Long channelId, Long roleId) {
		Channel channel = channelRepository.findById(channelId);
		Role role = roleRepository.findById(roleId);

		if (channel == null) {
			throw new NoSuchElementException("Channel not found with ID: " + channelId);
		}
		if (role == null) {
			throw new NoSuchElementException("Role not found with ID: " + roleId);
		}

		channel.addRoleWithAccess(role);
		return channelMapper.toDTO(channel);
	}

	public ChannelDTO removeRoleFromChannel(Long channelId, Long roleId) {
		Channel channel = channelRepository.findById(channelId);
		Role role = roleRepository.findById(roleId);

		if (channel == null) {
			throw new NoSuchElementException("Channel not found with ID: " + channelId);
		}
		if (role == null) {
			throw new NoSuchElementException("Role not found with ID: " + roleId);
		}

		channel.removeRoleWithAccess(role);
		return channelMapper.toDTO(channel);
	}
}