package fr.utln.spelerin.mappers;

import fr.utln.spelerin.dto.InvitationDTO;
import fr.utln.spelerin.dto.createdto.InvitationCreateDTO;
import fr.utln.spelerin.dto.updatedto.InvitationUpdateDTO;
import fr.utln.spelerin.entities.Guild;
import fr.utln.spelerin.entities.Invitation;
import fr.utln.spelerin.entities.Role;
import org.mapstruct.*;

@Mapper(componentModel = "cdi")
public interface InvitationMapper {

	// ----------- Entity -> DTO -----------
	@Mapping(target = "roleId", source = "role.id")
	@Mapping(target = "guildId", source = "guild.id")
	InvitationDTO toDTO(Invitation invitation);

	
	// ----------- CreateDTO + Role + Guild -> Entity -----------
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "discordCode", source = "dto.discordCode")
	@Mapping(target = "role", source = "role")
	@Mapping(target = "guild", source = "guild")
	Invitation toEntity(InvitationCreateDTO dto, Role role, Guild guild);


	// ----------- Update entity from UpdateDTO + Role + Guild -----------
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "discordCode", source = "dto.discordCode")
	@Mapping(target = "role", source = "role")
	@Mapping(target = "guild", source = "guild")
	void updateEntity(@MappingTarget Invitation invitation, InvitationUpdateDTO dto, Role role, Guild guild);
}