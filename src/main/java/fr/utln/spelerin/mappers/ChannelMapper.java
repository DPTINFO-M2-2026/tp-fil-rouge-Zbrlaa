package fr.utln.spelerin.mappers;

import fr.utln.spelerin.dto.ChannelDTO;
import fr.utln.spelerin.dto.createdto.ChannelCreateDTO;
import fr.utln.spelerin.dto.updatedto.ChannelUpdateDTO;
import fr.utln.spelerin.entities.Channel;
import fr.utln.spelerin.entities.Guild;
import org.mapstruct.*;

@Mapper(componentModel = "cdi")
public interface ChannelMapper {

	// ----------- Entity -> DTO -----------
	@Mapping(target = "guildId", source = "guild.id")
	@Mapping(target = "rolesWithAccessIds", source = "roleIds")
	ChannelDTO toDTO(Channel channel);

	// ----------- CreateDTO + Guild -> Entity -----------
	@Mapping(target = "id", source = "dto.id")
	@Mapping(target = "name", source = "dto.name")
	@Mapping(target = "type", source = "dto.type")
	@Mapping(target = "rolesWithAccess", ignore = true)
	Channel toEntity(ChannelCreateDTO dto, Guild guild);

	// ----------- Update entity from UpdateDTO + Guild -----------
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "name", source = "dto.name")
	@Mapping(target = "guild", source = "guild")
	@Mapping(target = "guildId", ignore = true)
	@Mapping(target = "rolesWithAccess", ignore = true)
	void updateEntity(@MappingTarget Channel channel, ChannelUpdateDTO dto, Guild guild);
}