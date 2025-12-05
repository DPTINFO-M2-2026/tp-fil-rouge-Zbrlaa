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

	private final UserRepository userRepository;
	private final GuildRepository guildRepository;
	private final RoleRepository roleRepository;

	@Inject
	public UserService(UserRepository userRepository, GuildRepository guildRepository, RoleRepository roleRepository) {
		this.userRepository = userRepository;
		this.guildRepository = guildRepository;
		this.roleRepository = roleRepository;
	}

	public List<UserDTO> getAllUsers() {
		return userRepository.listAll().stream()
				.map(UserMapper::toDTO)
				.toList();
	}

	public Optional<UserDTO> getUserById(String id) {
		return userRepository.findByIdOptional(id)
				.map(UserMapper::toDTO);
	}

	public UserDTO createUser(UserCreateDTO dto) {
		User user = UserMapper.toEntity(dto);
		userRepository.persist(user);
		return UserMapper.toDTO(user);
	}

	public UserDTO updateUser(String id, UserUpdateDTO dto) {
		User user = userRepository.findById(id);
		if (user == null) {
			throw new NoSuchElementException("User not found with ID: " + id);
		}
		UserMapper.updateEntity(user, dto);
		return UserMapper.toDTO(user);
	}

	public boolean deleteUser(String id) {
		return userRepository.deleteById(id);
	}

	public UserDTO addGuildToUser(String userId, String guildId) {
		User user = userRepository.findById(userId);
	
		if (user == null) throw new NoSuchElementException("User not found: " + userId);

		Guild guild = guildRepository.findById(guildId);
		if (guild == null) throw new NoSuchElementException("Guild not found: " + guildId);

		user.addGuild(guild);
		return UserMapper.toDTO(user);
	}

	public UserDTO removeGuildFromUser(String userId, String guildId) {
		User user = userRepository.findById(userId);
		Guild guild = guildRepository.findById(guildId);

		if (user == null) throw new NoSuchElementException("User not found: " + userId);
		if (guild == null) throw new NoSuchElementException("Guild not found: " + guildId);

		user.removeGuild(guild);
		return UserMapper.toDTO(user);
	}

	public UserDTO addRoleToUser(String userId, String roleId) {
		User user = userRepository.findById(userId);
		Role role = roleRepository.findById(roleId);

		if (user == null) throw new NoSuchElementException("User not found: " + userId);
		if (role == null) throw new NoSuchElementException("Role not found: " + roleId);

		user.addRole(role);
		return UserMapper.toDTO(user);
	}

	public UserDTO removeRoleFromUser(String userId, String roleId) {
		User user = userRepository.findById(userId);
		Role role = roleRepository.findById(roleId);

		if (user == null) throw new NoSuchElementException("User not found: " + userId);
		if (role == null) throw new NoSuchElementException("Role not found: " + roleId);

		user.removeRole(role);
		return UserMapper.toDTO(user);
	}
}