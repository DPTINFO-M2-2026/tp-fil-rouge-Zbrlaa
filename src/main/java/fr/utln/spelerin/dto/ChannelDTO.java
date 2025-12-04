package fr.utln.spelerin.dto;

import java.util.Set;


public record ChannelDTO(
	String id,
	String name,
	String type,
	String guildId,
	Set<String> rolesWithAccessIds
){}