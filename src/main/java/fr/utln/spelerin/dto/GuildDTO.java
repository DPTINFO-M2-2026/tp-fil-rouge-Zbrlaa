package fr.utln.spelerin.dto;

import java.time.Instant;
import java.util.Set;


public record GuildDTO(
	long id,
	String name,
	Instant createdAt,
	Set<String> userIds,
	Set<String> roleIds,
	Set<String> channelIds
){}