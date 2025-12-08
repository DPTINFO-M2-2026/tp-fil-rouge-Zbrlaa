package fr.utln.spelerin.services;

import fr.utln.spelerin.dto.GuildDTO;
import fr.utln.spelerin.dto.createdto.GuildCreateDTO;
import fr.utln.spelerin.dto.updatedto.GuildUpdateDTO;
import fr.utln.spelerin.entities.Channel;
import fr.utln.spelerin.entities.Guild;
import fr.utln.spelerin.entities.Role;
import fr.utln.spelerin.entities.User;
import fr.utln.spelerin.mappers.GuildMapper;
import fr.utln.spelerin.repositories.ChannelRepository;
import fr.utln.spelerin.repositories.GuildRepository;
import fr.utln.spelerin.repositories.RoleRepository;
import fr.utln.spelerin.repositories.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class GuildService {

	private final GuildRepository guildRepository;
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final ChannelRepository channelRepository;

	@Inject
	public GuildService(GuildRepository guildRepository, UserRepository userRepository, RoleRepository roleRepository, ChannelRepository channelRepository) {
		this.guildRepository = guildRepository;
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.channelRepository = channelRepository;
	}

	public List<GuildDTO> getAllGuilds() {
		return guildRepository.listAll().stream().map(GuildMapper::toDTO).toList();
	}

	public Optional<GuildDTO> getGuildById(Long id) {
		return guildRepository.findByIdOptional(id).map(GuildMapper::toDTO);
	}

	public GuildDTO createGuild(GuildCreateDTO dto) {
		Guild guild = GuildMapper.toEntity(dto);
		guildRepository.persist(guild);
		return GuildMapper.toDTO(guild);
	}

	public GuildDTO updateGuild(Long id, GuildUpdateDTO dto) {
		Guild guild = guildRepository.findById(id);
		if (guild == null) throw new NoSuchElementException("Guild not found: " + id);
		GuildMapper.updateEntity(guild, dto);
		return GuildMapper.toDTO(guild);
	}

	public boolean deleteGuild(Long id) {
		return guildRepository.deleteById(id);
	}

	public GuildDTO addUserToGuild(Long guildId, Long userId) {
		Guild guild = guildRepository.findById(guildId);
		User user = userRepository.findById(userId);

		if (guild == null) throw new NoSuchElementException("Guild not found: " + guildId);
		if (user == null) throw new NoSuchElementException("User not found: " + userId);

		guild.addUser(user);
		return GuildMapper.toDTO(guild);
	}

	public GuildDTO removeUserFromGuild(Long guildId, Long userId) {
		Guild guild = guildRepository.findById(guildId);
		User user = userRepository.findById(userId);

		if (guild == null) throw new NoSuchElementException("Guild not found: " + guildId);
		if (user == null) throw new NoSuchElementException("User not found: " + userId);

		guild.removeUser(user);
		return GuildMapper.toDTO(guild);
	}

	public GuildDTO addRoleToGuild(Long guildId, Long roleId) {
		Guild guild = guildRepository.findById(guildId);
		Role role = roleRepository.findById(roleId);

		if (guild == null) throw new NoSuchElementException("Guild not found: " + guildId);
		if (role == null) throw new NoSuchElementException("Role not found: " + roleId);

		guild.addRole(role);
		return GuildMapper.toDTO(guild);
	}

	public GuildDTO removeRoleFromGuild(Long guildId, Long roleId) {
		Guild guild = guildRepository.findById(guildId);
		Role role = roleRepository.findById(roleId);

		if (guild == null) throw new NoSuchElementException("Guild not found: " + guildId);
		if (role == null) throw new NoSuchElementException("Role not found: " + roleId);

		guild.removeRole(role);
		return GuildMapper.toDTO(guild);
	}

	public GuildDTO addChannelToGuild(Long guildId, Long channelId) {
		Guild guild = guildRepository.findById(guildId);
		Channel channel = channelRepository.findById(channelId);

		if (guild == null) throw new NoSuchElementException("Guild not found: " + guildId);
		if (channel == null) throw new NoSuchElementException("Channel not found: " + channelId);

		guild.addChannel(channel);
		return GuildMapper.toDTO(guild);
	}

	public GuildDTO removeChannelFromGuild(Long guildId, Long channelId) {
		Guild guild = guildRepository.findById(guildId);
		Channel channel = channelRepository.findById(channelId);

		if (guild == null) throw new NoSuchElementException("Guild not found: " + guildId);
		if (channel == null) throw new NoSuchElementException("Channel not found: " + channelId);

		guild.removeChannel(channel);
		return GuildMapper.toDTO(guild);
	}
}