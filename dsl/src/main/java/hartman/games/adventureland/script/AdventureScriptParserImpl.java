package hartman.games.adventureland.script;

import hartman.games.adventureland.engine.Adventure;
import hartman.games.adventureland.engine.Room;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ANTLR-based parser implementation which recognizes scripts written in Adventure grammar.
 */
public class AdventureScriptParserImpl implements AdventureScriptParser {

    @Override
    public Adventure parse(InputStream is) throws IOException {
        CharStream input = CharStreams.fromStream(is);
        AdventureLexer lexer = new AdventureLexer(input);
        AdventureParser parser = new AdventureParser(new CommonTokenStream(lexer));

        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                throw new IllegalStateException("failed to parse at line " + line + " due to " + msg, e);
            }
        });

        AdventureVisitor visitor = new AdventureVisitor();
        return visitor.visit(parser.adventure());
    }

    private static class AdventureVisitor extends AdventureBaseVisitor<Adventure> {
        @Override
        public Adventure visitAdventure(AdventureParser.AdventureContext ctx) {
            RoomVisitor roomVisitor = new RoomVisitor();
            List<Room> rooms = ctx.roomDeclaration().stream()
                    .map(roomDeclaration -> roomDeclaration.accept(roomVisitor))
                    .collect(Collectors.toList());
            Room startingRoom = rooms.get(0);
            return new Adventure(null, Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), startingRoom);
        }
    }

    private static class RoomVisitor extends AdventureBaseVisitor<Room> {
        @Override
        public Room visitRoomDeclaration(AdventureParser.RoomDeclarationContext ctx) {
            String name = ctx.roomName().getText();
            String description = ctx.roomDescription().getText();
            if (description.isEmpty()) {
                throw new ParseCancellationException(String.format("Room \"%s\" is missing a description.", name));
            }
            if (description.startsWith("\"")) {
                description = description.substring(1);
            }
            if (description.endsWith("\"")) {
                description = description.substring(0, description.length() - 1);
            }
            return new Room(name, description);
        }
    }


}
