package fr.utln.spelerin.mappers;

import fr.utln.spelerin.dto.RoleDTO;
import fr.utln.spelerin.dto.createdto.RoleCreateDTO;
import fr.utln.spelerin.dto.updatedto.RoleUpdateDTO;
import fr.utln.spelerin.entities.Guild;
import fr.utln.spelerin.entities.Role;
import org.mapstruct.*;

@Mapper(componentModel = "cdi")
public interface RoleMapper {

	// ----------- Entity -> DTO -----------
	@Mapping(target = "guildId", source = "guild.id")
	@Mapping(target = "userIds", source = "userIds")
	@Mapping(target = "accessibleChannelIds", source = "accessibleChannelIds")
	RoleDTO toDTO(Role role);

	// ----------- CreateDTO + Guild -> Entity -----------
	@Mapping(target = "id", source = "dto.id")
	@Mapping(target = "name", source = "dto.name")
	@Mapping(target = "permissions", source = "dto.permissions")
	@Mapping(target = "guild", source = "guild")
	@Mapping(target = "users", ignore = true)
	@Mapping(target = "accessibleChannels", ignore = true)
	Role toEntity(RoleCreateDTO dto, Guild guild);

	// ----------- Update entity from UpdateDTO + Guild -----------
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "name", source = "dto.name")
	@Mapping(target = "permissions", source = "dto.permissions")
	@Mapping(target = "guild", source = "guild")
	@Mapping(target = "users", ignore = true)
	@Mapping(target = "accessibleChannels", ignore = true)
	@Mapping(target = "guildId", ignore = true)
	@Mapping(target = "userIds", ignore = true)
	@Mapping(target = "accessibleChannelIds", ignore = true)
	void updateEntity(@MappingTarget Role role, RoleUpdateDTO dto, Guild guild);
}