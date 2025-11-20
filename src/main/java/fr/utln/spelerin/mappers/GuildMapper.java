package fr.utln.spelerin.mappers;

import fr.utln.spelerin.dto.GuildDTO;
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
}