package fr.utln.spelerin.mappers;

import fr.utln.spelerin.dto.UserDTO;
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
}