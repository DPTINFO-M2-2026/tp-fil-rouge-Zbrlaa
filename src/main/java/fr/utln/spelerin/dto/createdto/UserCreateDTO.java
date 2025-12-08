package fr.utln.spelerin.dto.createdto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserCreateDTO(
	@NotNull(message = "L'ID de l'utilisateur est obligatoire.")
	@Min(value = 1L, message = "L'ID doit être un nombre positif (Snowflake).")
	Long id,
	
	@NotBlank(message = "Le nom d'utilisateur est obligatoire.")
	@Size(min = 2, max = 32, message = "Le nom d'utilisateur doit contenir entre {min} et {max} caractères.")
	String username,
	
	@NotBlank(message = "Le nom affiché est obligatoire.")
	@Size(min = 1, max = 256, message = "Le nom affiché est trop long.")
	String displayName
){}