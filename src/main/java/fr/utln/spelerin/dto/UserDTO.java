package fr.utln.spelerin.dto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;


public record UserDTO(
	UUID id,
	String username,
	String displayName,
	Instant joinedAt,
	Set<UUID> channelIds,
	Set<UUID> roleIds
){}