package fr.utln.spelerin.mappers;

import fr.utln.spelerin.dto.GuildDTO;
import fr.utln.spelerin.dto.createdto.GuildCreateDTO;
import fr.utln.spelerin.dto.updatedto.GuildUpdateDTO;
import fr.utln.spelerin.entities.Guild;


public class GuildMapper {
	// Entity -> DTO
	public static GuildDTO toDTO(Guild guild) {
		return new GuildDTO(
				guild.getId(),
				guild.getName(),
				guild.getCreatedAt(),
				guild.getUserIds(),
				guild.getRoleIds(),
				guild.getChannelIds()
		);
	}

	// DTO -> Entity
	public static Guild toEntity(GuildCreateDTO dto) {
		return Guild.builder()
				.id(dto.id())
				.name(dto.name())
				.build();
	}

	public static void updateEntity(Guild guild, GuildUpdateDTO dto) {
		guild.setName(dto.name());
	}
}