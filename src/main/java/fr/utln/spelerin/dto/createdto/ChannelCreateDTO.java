package fr.utln.spelerin.dto.createdto;

public record ChannelCreateDTO(
	String id,
	String name,
	String type,
	String guildId
){ }