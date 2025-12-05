package fr.utln.spelerin.mappers;

import fr.utln.spelerin.dto.UserDTO;
import fr.utln.spelerin.dto.createdto.UserCreateDTO;
import fr.utln.spelerin.dto.updatedto.UserUpdateDTO;
import fr.utln.spelerin.entities.User;


public class UserMapper {
	// Entity -> DTO
	public static UserDTO toDTO(User user) {
		return new UserDTO(
				user.getId(),
				user.getUsername(),
				user.getDisplayName(),
				user.getJoinedAt(),
				user.getGuildIds(),
				user.getRoleIds()
		);
	}

	// DTO -> Entity
	public static User toEntity(UserCreateDTO dto) {
		return User.builder()
				.id(dto.id())
				.username(dto.username())
				.displayName(dto.displayName())
				.build();
	}

	public static void updateEntity(User user, UserUpdateDTO dto) {
		user.setUsername(dto.username());
		user.setDisplayName(dto.displayName());
	}
}