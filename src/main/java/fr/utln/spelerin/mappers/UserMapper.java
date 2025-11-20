package fr.utln.spelerin.mappers;

import fr.utln.spelerin.dto.UserDTO;
import fr.utln.spelerin.dto.createupdatedto.UserCreateUpdateDTO;
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
	public static User toEntity(UserCreateUpdateDTO dto) {
		return User.builder()
				.username(dto.username())
				.displayName(dto.displayName())
				.build();
	}

	public static void updateEntity(User user, UserCreateUpdateDTO dto) {
		user.setUsername(dto.username());
		user.setDisplayName(dto.displayName());
	}
}