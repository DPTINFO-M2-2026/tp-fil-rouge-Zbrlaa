package fr.utln.spelerin.dto.updatedto;

public record ChannelUpdateDTO(
	String name,
	String type,
	String guildId
){ }