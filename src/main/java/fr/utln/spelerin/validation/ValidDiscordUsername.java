package fr.utln.spelerin.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Valide qu'un nom d'utilisateur respecte les règles Discord.
 * <p>
 * Les règles suivantes sont appliquées :
 * <ul>
 *   <li>Ne doit pas contenir '@' (mention)</li>
 *   <li>Ne doit pas contenir '#' (discriminator)</li>
 *   <li>Ne doit pas contenir ':' (emoji)</li>
 *   <li>Ne doit pas contenir '```' (code block)</li>
 *   <li>Ne doit pas être 'everyone' ou 'here' (mentions spéciales)</li>
 * </ul>
 * 
 * @author spelerin
 * @version 1.0
 * @since 2026-02-13
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DiscordUsernameValidator.class)
@Documented
public @interface ValidDiscordUsername {
    
    /**
     * Message d'erreur par défaut.
     */
    String message() default "Le nom d'utilisateur contient des caractères interdits (@, #, :, ```) ou est un mot réservé (everyone, here)";
    
    /**
     * Groupes de validation.
     */
    Class<?>[] groups() default {};
    
    /**
     * Charge utile additionnelle.
     */
    Class<? extends Payload>[] payload() default {};
}
