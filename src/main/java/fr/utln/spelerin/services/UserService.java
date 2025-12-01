package fr.utln.spelerin.services;

import fr.utln.spelerin.dto.UserDTO;
import fr.utln.spelerin.dto.createupdatedto.UserCreateUpdateDTO;
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
import java.util.UUID;

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

	public Optional<UserDTO> getUserById(UUID id) {
		return userRepository.findByIdOptional(id)
				.map(UserMapper::toDTO);
	}

	public UserDTO createUser(UserCreateUpdateDTO dto) {
		User user = UserMapper.toEntity(dto);
		userRepository.persist(user);
		return UserMapper.toDTO(user);
	}

	public UserDTO updateUser(UUID id, UserCreateUpdateDTO dto) {
		User user = userRepository.findById(id);
		if (user == null) {
			throw new NoSuchElementException("User not found with ID: " + id);
		}
		UserMapper.updateEntity(user, dto);
		return UserMapper.toDTO(user);
	}

	public boolean deleteUser(UUID id) {
		return userRepository.deleteById(id);
	}

	public UserDTO addGuildToUser(UUID userId, UUID guildId) {
		User user = userRepository.findById(userId);
		Guild guild = guildRepository.findById(guildId);

		if (user == null) throw new NoSuchElementException("User not found: " + userId);
		if (guild == null) throw new NoSuchElementException("Guild not found: " + guildId);

		user.addGuild(guild);
		return UserMapper.toDTO(user);
	}

	public UserDTO removeGuildFromUser(UUID userId, UUID guildId) {
		User user = userRepository.findById(userId);
		Guild guild = guildRepository.findById(guildId);

		if (user == null) throw new NoSuchElementException("User not found: " + userId);
		if (guild == null) throw new NoSuchElementException("Guild not found: " + guildId);

		user.removeGuild(guild);
		return UserMapper.toDTO(user);
	}

	public UserDTO addRoleToUser(UUID userId, UUID roleId) {
		User user = userRepository.findById(userId);
		Role role = roleRepository.findById(roleId);

		if (user == null) throw new NoSuchElementException("User not found: " + userId);
		if (role == null) throw new NoSuchElementException("Role not found: " + roleId);

		user.addRole(role);
		return UserMapper.toDTO(user);
	}

	public UserDTO removeRoleFromUser(UUID userId, UUID roleId) {
		User user = userRepository.findById(userId);
		Role role = roleRepository.findById(roleId);

		if (user == null) throw new NoSuchElementException("User not found: " + userId);
		if (role == null) throw new NoSuchElementException("Role not found: " + roleId);

		user.removeRole(role);
		return UserMapper.toDTO(user);
	}
}