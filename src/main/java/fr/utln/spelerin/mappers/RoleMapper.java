package fr.utln.spelerin.mappers;

import fr.utln.spelerin.dto.RoleDTO;
import fr.utln.spelerin.entities.Role;


public class RoleMapper {
	// Entity -> DTO
	public static RoleDTO toDTO(Role role) {
		return new RoleDTO(
				role.getId(),
				role.getName(),
				role.getPermissions(),
				role.getGuildId(),
				role.getUserIds(),
				role.getAccessibleChannelIds()
		);
	}
}