package fr.utln.spelerin.dto.createdto;

public record RoleCreateDTO(
	String id,
	String name,
	Long permissions,
	String guildId
){}