package hartman.games.adventureland.api;

import hartman.games.adventureland.script.AdventureScriptParser;
import hartman.games.adventureland.script.AdventureScriptParserImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }

    @Bean
    public AdventureScriptParser adventureScriptParser() {
        return new AdventureScriptParserImpl();
    }
}
