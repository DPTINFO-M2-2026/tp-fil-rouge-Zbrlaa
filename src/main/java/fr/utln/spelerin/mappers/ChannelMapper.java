package fr.utln.spelerin.mappers;

import fr.utln.spelerin.dto.ChannelDTO;
import fr.utln.spelerin.entities.Channel;


public class ChannelMapper {
	// Entity -> DTO
	public static ChannelDTO toDTO(Channel channel) {
		return new ChannelDTO(
				channel.getId(),
				channel.getName(),
				channel.getType(),
				channel.getGuildId(),
				channel.getRoleIds()
		);
	}
}