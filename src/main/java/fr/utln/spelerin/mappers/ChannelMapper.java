package fr.utln.spelerin.mappers;

import fr.utln.spelerin.dto.ChannelDTO;
import fr.utln.spelerin.dto.createdto.ChannelCreateDTO;
import fr.utln.spelerin.dto.updatedto.ChannelUpdateDTO;
import fr.utln.spelerin.entities.Channel;
import fr.utln.spelerin.entities.Guild;


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

	// DTO -> Entity
	public static Channel toEntity(ChannelCreateDTO dto, Guild guild) {
		return Channel.builder()
				.id(dto.id())
				.name(dto.name())
				.type(dto.type())
				.guild(guild)
				.build();
	}

	public static void updateEntity(Channel channel, ChannelUpdateDTO dto, Guild guild) {
		channel.setName(dto.name());
		channel.setType(dto.type());
		channel.setGuild(guild);
	}
}