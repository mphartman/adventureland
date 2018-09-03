package hartman.games.adventureland;

import hartman.games.adventureland.script.AdventureScriptParser;
import hartman.games.adventureland.script.AdventureScriptParserImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public AdventureScriptParser adventureScriptParser() {
        return new AdventureScriptParserImpl();
    }
}
