package fr.utln.spelerin.mappers;

import fr.utln.spelerin.dto.UserDTO;
import fr.utln.spelerin.dto.createdto.UserCreateDTO;
import fr.utln.spelerin.dto.updatedto.UserUpdateDTO;
import fr.utln.spelerin.entities.User;
import org.mapstruct.*;

@Mapper(componentModel = "cdi")
public interface UserMapper {

	// ----------- Entity -> DTO -----------
	UserDTO toDTO(User user);

	// ----------- CreateDTO -> Entity -----------
	@Mapping(target = "id", source = "dto.id")
	@Mapping(target = "username", source = "dto.username")
	@Mapping(target = "displayName", source = "dto.displayName")
	@Mapping(target = "guilds", ignore = true)
	@Mapping(target = "ownedGuilds", ignore = true)
	@Mapping(target = "roles", ignore = true)
	User toEntity(UserCreateDTO dto);

	// ----------- Update entity from UpdateDTO -----------
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "username", source = "dto.username")
	@Mapping(target = "displayName", source = "dto.displayName")
	@Mapping(target = "guilds", ignore = true)
	@Mapping(target = "ownedGuilds", ignore = true)
	@Mapping(target = "roles", ignore = true)
	@Mapping(target = "guildIds", ignore = true)
	@Mapping(target = "ownedGuildIds", ignore = true)
	@Mapping(target = "roleIds", ignore = true)
	void updateEntity(@MappingTarget User user, UserUpdateDTO dto);
}