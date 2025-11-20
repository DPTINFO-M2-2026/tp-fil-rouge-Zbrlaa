package fr.utln.spelerin.dto;

import java.util.Set;
import java.util.UUID;


public record RoleDTO(
	UUID id,
	String name,
	Long permissions,
	UUID guildId,
	Set<UUID> userIds,
	Set<UUID> accessibleChannelIds
){}