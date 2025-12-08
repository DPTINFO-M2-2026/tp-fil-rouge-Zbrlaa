package fr.utln.spelerin.services;

import fr.utln.spelerin.dto.UserDTO;
import fr.utln.spelerin.dto.createdto.UserCreateDTO;
import fr.utln.spelerin.dto.updatedto.UserUpdateDTO;
import fr.utln.spelerin.entities.Guild;
import fr.utln.spelerin.entities.Role;
import fr.utln.spelerin.entities.User;
import fr.utln.spelerin.mappers.UserMapper;
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
public class UserService {

	private final UserMapper userMapper;

	private final UserRepository userRepository;
	private final GuildRepository guildRepository;
	private final RoleRepository roleRepository;

	@Inject
	public UserService(UserMapper userMapper ,UserRepository userRepository, GuildRepository guildRepository, RoleRepository roleRepository) {
		this.userMapper = userMapper;
		this.userRepository = userRepository;
		this.guildRepository = guildRepository;
		this.roleRepository = roleRepository;
	}

	public List<UserDTO> getAllUsers() {
		return userRepository.listAll().stream()
				.map(userMapper::toDTO)
				.toList();
	}

	public Optional<UserDTO> getUserById(Long id) {
		return userRepository.findByIdOptional(id)
				.map(userMapper::toDTO);
	}

	public UserDTO createUser(UserCreateDTO dto) {
		User user = userMapper.toEntity(dto);
		userRepository.persist(user);
		return userMapper.toDTO(user);
	}

	public UserDTO updateUser(Long id, UserUpdateDTO dto) {
		User user = userRepository.findById(id);
		if (user == null) {
			throw new NoSuchElementException("User not found with ID: " + id);
		}
		userMapper.updateEntity(user, dto);
		return userMapper.toDTO(user);
	}

	public boolean deleteUser(Long id) {
		return userRepository.deleteById(id);
	}

	public UserDTO addGuildToUser(Long userId, Long guildId) {
		User user = userRepository.findById(userId);
	
		if (user == null) throw new NoSuchElementException("User not found: " + userId);

		Guild guild = guildRepository.findById(guildId);
		if (guild == null) throw new NoSuchElementException("Guild not found: " + guildId);

		user.addGuild(guild);
		return userMapper.toDTO(user);
	}

	public UserDTO removeGuildFromUser(Long userId, Long guildId) {
		User user = userRepository.findById(userId);
		Guild guild = guildRepository.findById(guildId);

		if (user == null) throw new NoSuchElementException("User not found: " + userId);
		if (guild == null) throw new NoSuchElementException("Guild not found: " + guildId);

		user.removeGuild(guild);
		return userMapper.toDTO(user);
	}

	public UserDTO addRoleToUser(Long userId, Long roleId) {
		User user = userRepository.findById(userId);
		Role role = roleRepository.findById(roleId);

		if (user == null) throw new NoSuchElementException("User not found: " + userId);
		if (role == null) throw new NoSuchElementException("Role not found: " + roleId);

		user.addRole(role);
		return userMapper.toDTO(user);
	}

	public UserDTO removeRoleFromUser(Long userId, Long roleId) {
		User user = userRepository.findById(userId);
		Role role = roleRepository.findById(roleId);

		if (user == null) throw new NoSuchElementException("User not found: " + userId);
		if (role == null) throw new NoSuchElementException("Role not found: " + roleId);

		user.removeRole(role);
		return userMapper.toDTO(user);
	}
}