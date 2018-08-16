package hartman.games.adventureland.script;

import hartman.games.adventureland.engine.Adventure;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.junit.Test;

import java.io.IOException;

public class AdventureGrammarTest {

    @Test
    public void singleRoom() throws IOException {

        CharStream input = CharStreams.fromStream(getClass().getResourceAsStream("/scripts/001adventure.txt"));

        AdventureLexer lexer = new AdventureLexer(input);

        AdventureParser parser = new AdventureParser(new CommonTokenStream(lexer));
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                throw new IllegalStateException("failed to parse at line " + line + " due to " + msg, e);
            }
        });

        parser.adventure().accept(new AdventureBaseVisitor<Adventure>() {
            @Override
            public Adventure visitAdventure(AdventureParser.AdventureContext ctx) {
                System.out.println(ctx.roomDeclaration());
                return super.visitAdventure(ctx);
            }

            @Override
            public Adventure visitRoomName(AdventureParser.RoomNameContext ctx) {
                System.out.println(ctx.Identifier().getText());
                return super.visitRoomName(ctx);
            }
        });
    }
}
