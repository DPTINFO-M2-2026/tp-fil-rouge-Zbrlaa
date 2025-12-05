package fr.utln.spelerin.dto.createdto;

public record ChannelCreateDTO(
	long id,
	String name,
	int type,
	long guildId
){ }