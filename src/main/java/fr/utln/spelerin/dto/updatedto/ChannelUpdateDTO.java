package fr.utln.spelerin.dto.updatedto;

public record ChannelUpdateDTO(
	String name,
	int type,
	long guildId
){ }