package fr.utln.spelerin.dto;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;


public record UserDTO(
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	long id,
	String username,
	String displayName,
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Set<Long> guildIds,
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Set<Long> ownedGuildIds,
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Set<Long> roleIds
){}