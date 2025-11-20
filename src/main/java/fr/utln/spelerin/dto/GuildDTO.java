package fr.utln.spelerin.dto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;


public record GuildDTO(
	UUID id,
	String name,
	Instant createdAt,
	Set<UUID> userIds,
	Set<UUID> roleIds,
	Set<UUID> channelIds
){}