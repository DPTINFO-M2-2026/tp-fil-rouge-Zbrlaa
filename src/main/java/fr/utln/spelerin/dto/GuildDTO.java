package fr.utln.spelerin.dto;

import java.util.Set;


public record GuildDTO(
	long id,
	String name,
	Set<Long> userIds,
	Set<Long> roleIds,
	Set<Long> channelIds
){}