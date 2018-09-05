package hartman.games.adventureland.app;

import hartman.games.adventureland.engine.Adventure;
import hartman.games.adventureland.engine.CommandInterpreter;
import hartman.games.adventureland.engine.Display;
import hartman.games.adventureland.engine.Game;
import hartman.games.adventureland.engine.GameState;
import hartman.games.adventureland.script.AdventureScriptParser;
import hartman.games.adventureland.script.AdventureScriptParserImpl;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
public class ConsoleApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ConsoleApplication.class, args);
    }

    private Options options;
    private CommandLineParser parser;
    private CommandLine cmd;
    private HelpFormatter formatter;

    /**
     * Initiates the sub-command
     */
    @PostConstruct
    public void init() {
        options = new Options();
        parser = new DefaultParser();
        formatter = new HelpFormatter();

        final Option scriptOption = Option.builder("s")
                .longOpt("script")
                .argName("script")
                .required()
                .hasArg()
                .desc("Path to the Adventure story script file to load")
                .build();

        options.addOption(scriptOption);
    }

    @Override
    public void run(String... args) throws IOException {

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            formatter.printHelp("adventureland", options);
            System.exit(1);
        }

        runAdventure(loadAdventure(Paths.get(cmd.getOptionValue("s"))));

    }

    @Bean
    public AdventureScriptParser adventureScriptParser() {
        return new AdventureScriptParserImpl();
    }

    private Adventure loadAdventure(Path path) throws IOException {
        try (Reader r = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return adventureScriptParser().parse(r);
        }
    }

    private void runAdventure(Adventure adventure) {
        CommandInterpreter interpreter = new ConsoleInterpreter(adventure.getVocabulary());
        Display display = new ConsoleDisplay();
        GameState gameState = new GameState(adventure.getStartRoom(), adventure.getItems());
        Game game = new Game(adventure, interpreter, display, gameState);
        game.run();
    }

}

