package fr.utln.spelerin.mappers;

import fr.utln.spelerin.dto.GuildDTO;
import fr.utln.spelerin.dto.createdto.GuildCreateDTO;
import fr.utln.spelerin.dto.updatedto.GuildUpdateDTO;
import fr.utln.spelerin.entities.Guild;
import fr.utln.spelerin.entities.User;

import org.mapstruct.*;


@Mapper(componentModel = "cdi")
public interface GuildMapper {

	// ----------- Entity -> DTO -----------
	@Mapping(target = "userIds", source = "userIds")
	@Mapping(target = "roleIds", source = "roleIds")
	@Mapping(target = "channelIds", source = "channelIds")
	@Mapping(target = "invitationIds", source = "invitationIds")
	GuildDTO toDTO(Guild guild);


	// ----------- CreateDTO -> Entity -----------
	@Mapping(target = "id", source = "dto.id")
	@Mapping(target = "users", ignore = true)
	@Mapping(target = "roles", ignore = true)
	@Mapping(target = "channels", ignore = true)
	@Mapping(target = "invitations", ignore = true)
	Guild toEntity(GuildCreateDTO dto, User owner);

	// ----------- Update entity from UpdateDTO -----------
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "owner", source = "owner")
	@Mapping(target = "users", ignore = true)
	@Mapping(target = "roles", ignore = true)
	@Mapping(target = "channels", ignore = true)
	@Mapping(target = "userIds", ignore = true)
	@Mapping(target = "roleIds", ignore = true)
	@Mapping(target = "channelIds", ignore = true)
	@Mapping(target = "invitations", ignore = true)
	@Mapping(target = "invitationIds", ignore = true)
	void updateEntity(@MappingTarget Guild guild, GuildUpdateDTO dto, User owner);
}