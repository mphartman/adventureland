package hartman.games.adventureland.script;

import hartman.games.adventureland.engine.Adventure;
import hartman.games.adventureland.engine.Room;
import hartman.games.adventureland.engine.Vocabulary;
import hartman.games.adventureland.engine.Word;
import hartman.games.adventureland.engine.core.Words;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * ANTLR-based parser implementation which recognizes scripts written in Adventure grammar.
 */
public class AdventureScriptParserImpl implements AdventureScriptParser {

    @Override
    public Adventure parse(Reader r) throws IOException {
        CharStream input = CharStreams.fromReader(r);
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

            List<RoomDef> roomDefs = ctx.roomDeclaration().stream()
                    .map(roomDeclaration -> roomDeclaration.accept(roomVisitor))
                    .collect(toList());

            setExits(roomDefs);

            Room startingRoom = roomDefs.get(0).getRoom();

            return new Adventure(new Vocabulary(emptySet()), emptySet(), emptySet(), emptySet(), startingRoom);
        }

        private void setExits(List<RoomDef> roomDefs) {
            Map<String, Room> roomsByName = roomDefs.stream()
                    .collect(toMap(RoomDef::getRoomName, RoomDef::getRoom));

            roomDefs.forEach(r -> r.getExits().forEach(e -> {
                String exitRoomName = e.getRoomName();
                if (null == exitRoomName || exitRoomName.isEmpty() || exitRoomName.equals(r.getRoomName())) {
                    r.getRoom().setExitTowardsSelf(e.getDirection());
                } else {
                    if (!roomsByName.containsKey(exitRoomName)) {
                        throw new ParseCancellationException(String.format("Invalid exit. '%s' from room '%s' refers to non-existent room '%s'.", e.getDirection().getName(), r.getRoomName(), exitRoomName));
                    }
                    r.getRoom().setExit(e.getDirection(), roomsByName.get(exitRoomName));
                }
            }));
        }
    }

    private static class RoomDef {
        private final Room room;
        private final List<ExitDef> exits;

        public RoomDef(Room room, List<ExitDef> exits) {
            this.room = room;
            this.exits = exits;
        }

        public Room getRoom() {
            return room;
        }

        public String getRoomName() {
            return room.getName();
        }

        public List<ExitDef> getExits() {
            return exits;
        }
    }

    private static class RoomVisitor extends AdventureBaseVisitor<RoomDef> {
        @Override
        public RoomDef visitRoomDeclaration(AdventureParser.RoomDeclarationContext ctx) {
            Room room = newRoom(ctx);
            List<ExitDef> exitDefs = emptyList();
            if (ctx.roomExits() != null) {
                exitDefs = ctx.roomExits().accept(new RoomExitsVisitor());
            }
            return new RoomDef(room, exitDefs);
        }

        private Room newRoom(AdventureParser.RoomDeclarationContext ctx) {
            String name = ctx.roomName().getText();
            String description = ctx.roomDescription().getText();
            if (null == description || description.isEmpty()) {
                throw new ParseCancellationException(String.format("Room \"%s\" is missing a description.", name));
            }
            if (description.startsWith("\"")) {
                description = description.substring(1);
            }
            if (description.endsWith("\"")) {
                description = description.substring(0, description.length() - 1);
            }
            // change and \" to just "
            description = description.replaceAll("\\\\\\\"", "\"");
            return new Room(name, description);
        }
    }

    private static class ExitDef {
        private final Word direction;
        private final String roomName;

        public ExitDef(Word direction, String roomName) {
            this.direction = direction;
            this.roomName = roomName;
        }

        public Word getDirection() {
            return direction;
        }

        public String getRoomName() {
            return roomName;
        }
    }

    private static class RoomExitsVisitor extends AdventureBaseVisitor<List<ExitDef>> {
        @Override
        public List<ExitDef> visitRoomExits(AdventureParser.RoomExitsContext ctx) {
            RoomExitVisitor visitor = new RoomExitVisitor();
            return ctx.roomExit().stream()
                    .map(roomExit -> roomExit.accept(visitor))
                    .collect(toList());
        }
    }

    private static class RoomExitVisitor extends AdventureBaseVisitor<ExitDef> {
        @Override
        public ExitDef visitRoomExit(AdventureParser.RoomExitContext ctx) {
            ExitDirectionVisitor exitDirectionVisitor = new ExitDirectionVisitor();
            Word direction = ctx.exitDirection().accept(exitDirectionVisitor);
            String roomName = null;
            if (null != ctx.roomName()) {
                roomName = ctx.roomName().isEmpty() ? null : ctx.roomName().getText();
            }
            return new ExitDef(direction, roomName);
        }
    }

    private static class ExitDirectionVisitor extends AdventureBaseVisitor<Word> {
        @Override
        public Word visitExitNorth(AdventureParser.ExitNorthContext ctx) {
            return Words.NORTH;
        }

        @Override
        public Word visitExitSouth(AdventureParser.ExitSouthContext ctx) {
            return Words.SOUTH;
        }

        @Override
        public Word visitExitEast(AdventureParser.ExitEastContext ctx) {
            return Words.EAST;
        }

        @Override
        public Word visitExitWest(AdventureParser.ExitWestContext ctx) {
            return Words.WEST;
        }

        @Override
        public Word visitExitUp(AdventureParser.ExitUpContext ctx) {
            return Words.UP;
        }

        @Override
        public Word visitExitDown(AdventureParser.ExitDownContext ctx) {
            return Words.DOWN;
        }
    }

}
