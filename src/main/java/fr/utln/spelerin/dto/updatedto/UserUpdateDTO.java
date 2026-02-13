package fr.utln.spelerin.dto.updatedto;

import fr.utln.spelerin.validation.ValidDiscordUsername;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateDTO(
	@NotBlank(message = "Le nom d'utilisateur est obligatoire.")
	@Size(min = 2, max = 32, message = "Le nom d'utilisateur doit contenir entre {min} et {max} caractères.")
	@ValidDiscordUsername
	String username,
	
	@NotBlank(message = "Le nom affiché est obligatoire.")
	@Size(min = 1, max = 256, message = "Le nom affiché est trop long.")
	String displayName
){}