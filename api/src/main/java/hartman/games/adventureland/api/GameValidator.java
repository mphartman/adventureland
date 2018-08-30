package hartman.games.adventureland.api;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component("beforeCreateGameValidator")
public class GameValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return Game.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        System.out.println("Validating game...");
        Game game = (Game) target;
        ValidationUtils.rejectIfEmpty(errors, "player", "player.empty");
        ValidationUtils.rejectIfEmpty(errors, "adventureId", "adventure.empty");
    }
}
