package fr.utln.spelerin.dto;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;


public record GuildDTO(
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	long id,
	String name,
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Long ownerId,
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Set<Long> userIds,
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Set<Long> roleIds,
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Set<Long> channelIds,
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Set<Long> invitationIds
){}