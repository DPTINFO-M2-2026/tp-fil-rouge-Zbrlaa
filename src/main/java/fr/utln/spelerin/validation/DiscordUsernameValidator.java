package fr.utln.spelerin.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validateur pour l'annotation {@link ValidDiscordUsername}.
 * <p>
 * Vérifie que le nom d'utilisateur respecte les contraintes Discord :
 * <ul>
 *   <li>Pas de caractères spéciaux : @, #, :, ```</li>
 *   <li>Pas de mots réservés : everyone, here</li>
 *   <li>Accepte null (utilisez @NotNull séparément si nécessaire)</li>
 * </ul>
 * 
 * @author spelerin
 * @version 1.0
 * @since 2026-02-13
 */
public class DiscordUsernameValidator implements ConstraintValidator<ValidDiscordUsername, String> {
    
    private static final String MENTION_CHAR = "@";
    private static final String DISCRIMINATOR_CHAR = "#";
    private static final String EMOJI_CHAR = ":";
    private static final String CODE_BLOCK = "```";
    private static final String RESERVED_EVERYONE = "everyone";
    private static final String RESERVED_HERE = "here";
    
    @Override
    public void initialize(ValidDiscordUsername annotation) {
        // Pas de configuration nécessaire
    }
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // null est considéré valide (utilisez @NotNull séparément)
        if (value == null) {
            return true;
        }
        
        // Vérification des caractères interdits
        if (value.contains(MENTION_CHAR)) {
            buildViolation(context, "Le caractère '@' n'est pas autorisé");
            return false;
        }
        
        if (value.contains(DISCRIMINATOR_CHAR)) {
            buildViolation(context, "Le caractère '#' n'est pas autorisé");
            return false;
        }
        
        if (value.contains(EMOJI_CHAR)) {
            buildViolation(context, "Le caractère ':' n'est pas autorisé");
            return false;
        }
        
        if (value.contains(CODE_BLOCK)) {
            buildViolation(context, "La séquence '```' n'est pas autorisée");
            return false;
        }
        
        // Vérification des mots réservés (insensible à la casse)
        String lowerValue = value.toLowerCase().trim();
        if (lowerValue.equals(RESERVED_EVERYONE)) {
            buildViolation(context, "Le nom 'everyone' est réservé");
            return false;
        }
        
        if (lowerValue.equals(RESERVED_HERE)) {
            buildViolation(context, "Le nom 'here' est réservé");
            return false;
        }
        
        return true;
    }
    
    /**
     * Construit un message de violation personnalisé.
     */
    private void buildViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
               .addConstraintViolation();
    }
}
