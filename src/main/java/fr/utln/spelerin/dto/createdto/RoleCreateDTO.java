package fr.utln.spelerin.dto.createdto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RoleCreateDTO(
	@NotNull(message = "L'ID du rôle est obligatoire.")
	@Min(value = 1L, message = "L'ID doit être un nombre positif (Snowflake).")
	Long id,
	
	@NotBlank(message = "Le nom du rôle est obligatoire.")
	@Size(min = 2, max = 50, message = "Le nom doit contenir entre {min} et {max} caractères.")
	String name,
	
	@NotNull(message = "Les permissions sont obligatoires.")
	@Min(value = 0L, message = "La valeur de permission ne peut pas être négative.")
	Long permissions,
	
	@NotNull(message = "L'ID de la guilde est obligatoire.")
	@Min(value = 1L, message = "L'ID de la guilde doit être positif (Snowflake).")
	Long guildId
){}