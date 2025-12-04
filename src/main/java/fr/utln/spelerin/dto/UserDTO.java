package fr.utln.spelerin.dto;

import java.time.Instant;
import java.util.Set;


public record UserDTO(
	String id,
	String username,
	String displayName,
	Instant joinedAt,
	Set<String> guildIds,
	Set<String> roleIds
){}