package fr.utln.spelerin.dto;

import java.util.Set;
import java.util.UUID;


public record ChannelDTO(
	UUID id,
	String name,
	String type,
	UUID guildId,
	Set<UUID> rolesWithAccessIds
){}