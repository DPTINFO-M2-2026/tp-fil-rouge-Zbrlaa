package fr.utln.spelerin.mappers;

import fr.utln.spelerin.dto.GuildDTO;
import fr.utln.spelerin.dto.createupdatedto.GuildCreateUpdateDTO;
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
	public static Guild toEntity(GuildCreateUpdateDTO dto) {
		return Guild.builder()
				.name(dto.name())
				.build();
	}

	public static void updateEntity(Guild guild, GuildCreateUpdateDTO dto) {
		guild.setName(dto.name());
	}
}