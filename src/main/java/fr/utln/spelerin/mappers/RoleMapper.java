package fr.utln.spelerin.mappers;

import fr.utln.spelerin.dto.RoleDTO;
import fr.utln.spelerin.dto.createupdatedto.RoleCreateUpdateDTO;
import fr.utln.spelerin.entities.Guild;
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

	// DTO -> Entity
	public static Role toEntity(RoleCreateUpdateDTO dto, Guild guild) {
		return Role.builder()
				.name(dto.name())
				.permissions(dto.permissions())
				.guild(guild)
				.build();
	}

	public static void updateEntity(Role role, RoleCreateUpdateDTO dto, Guild guild) {
		role.setName(dto.name());
		role.setPermissions(dto.permissions());
		role.setGuild(guild);
	}
}