package fr.utln.spelerin.dto.createupdatedto;

import java.util.UUID;


public record RoleCreateUpdateDTO(
	String name,
	Long permissions,
	UUID guildId
){}