package fr.utln.spelerin.dto.createdto;

public record RoleCreateDTO(
	long id,
	String name,
	long permissions,
	long guildId
){}