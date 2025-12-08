package fr.utln.spelerin.services;

import fr.utln.spelerin.dto.RoleDTO;
import fr.utln.spelerin.dto.createdto.RoleCreateDTO;
import fr.utln.spelerin.dto.updatedto.RoleUpdateDTO;
import fr.utln.spelerin.entities.Channel;
import fr.utln.spelerin.entities.Guild;
import fr.utln.spelerin.entities.Role;
import fr.utln.spelerin.entities.User;
import fr.utln.spelerin.mappers.RoleMapper;
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
public class RoleService {

	private final RoleMapper roleMapper;

	private final RoleRepository roleRepository;
	private final GuildRepository guildRepository;
	private final UserRepository userRepository;
	private final ChannelRepository channelRepository;

	@Inject
	public RoleService(RoleMapper roleMapper, RoleRepository roleRepository, GuildRepository guildRepository, UserRepository userRepository, ChannelRepository channelRepository) {
		this.roleMapper = roleMapper;
		this.roleRepository = roleRepository;
		this.guildRepository = guildRepository;
		this.userRepository = userRepository;
		this.channelRepository = channelRepository;
	}

	public List<RoleDTO> getAllRoles() {
		return roleRepository.listAll().stream().map(roleMapper::toDTO).toList();
	}

	public Optional<RoleDTO> getRoleById(Long id) {
		return roleRepository.findByIdOptional(id).map(roleMapper::toDTO);
	}

	public RoleDTO createRole(RoleCreateDTO dto) {
		Guild guild = guildRepository.findById(dto.guildId());
		if (guild == null) {
			throw new NoSuchElementException("Guild not found: " + dto.guildId());
		}
		Role role = roleMapper.toEntity(dto, guild);
		roleRepository.persist(role);
		return roleMapper.toDTO(role);
	}

	public RoleDTO updateRole(Long id, RoleUpdateDTO dto) {
		Role role = roleRepository.findById(id);
		if (role == null) throw new NoSuchElementException("Role not found: " + id);

		Guild guild = guildRepository.findById(dto.guildId());
		if (guild == null) throw new IllegalArgumentException("Guild not found: " + dto.guildId());

		roleMapper.updateEntity(role, dto, guild);
		return roleMapper.toDTO(role);
	}

	public boolean deleteRole(Long id) {
		return roleRepository.deleteById(id);
	}

	public RoleDTO addUserToRole(Long roleId, Long userId) {
		Role role = roleRepository.findById(roleId);
		User user = userRepository.findById(userId);

		if (role == null) throw new NoSuchElementException("Role not found: " + roleId);
		if (user == null) throw new NoSuchElementException("User not found: " + userId);

		role.addUser(user);
		return roleMapper.toDTO(role);
	}

	public RoleDTO removeUserFromRole(Long roleId, Long userId) {
		Role role = roleRepository.findById(roleId);
		User user = userRepository.findById(userId);

		if (role == null) throw new NoSuchElementException("Role not found: " + roleId);
		if (user == null) throw new NoSuchElementException("User not found: " + userId);

		role.removeUser(user);
		return roleMapper.toDTO(role);
	}

	public RoleDTO addChannelToRole(Long roleId, Long channelId) {
		Role role = roleRepository.findById(roleId);
		Channel channel = channelRepository.findById(channelId);

		if (role == null) throw new NoSuchElementException("Role not found: " + roleId);
		if (channel == null) throw new NoSuchElementException("Channel not found: " + channelId);

		role.addAccessibleChannel(channel);
		return roleMapper.toDTO(role);
	}

	public RoleDTO removeChannelFromRole(Long roleId, Long channelId) {
		Role role = roleRepository.findById(roleId);
		Channel channel = channelRepository.findById(channelId);

		if (role == null) throw new NoSuchElementException("Role not found: " + roleId);
		if (channel == null) throw new NoSuchElementException("Channel not found: " + channelId);

		role.removeAccessibleChannel(channel);
		return roleMapper.toDTO(role);
	}
}