package fr.utln.spelerin.dto.createupdatedto;

import java.util.UUID;


public record ChannelCreateUpdateDTO(
	String name,
	String type,
	UUID guildId
){}