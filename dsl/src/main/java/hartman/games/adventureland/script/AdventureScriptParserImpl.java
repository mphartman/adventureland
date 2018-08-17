package hartman.games.adventureland.script;

import hartman.games.adventureland.engine.Adventure;
import hartman.games.adventureland.engine.Item;
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
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static hartman.games.adventureland.script.AdventureParser.AdventureContext;
import static hartman.games.adventureland.script.AdventureParser.ExitDownContext;
import static hartman.games.adventureland.script.AdventureParser.ExitEastContext;
import static hartman.games.adventureland.script.AdventureParser.ExitNorthContext;
import static hartman.games.adventureland.script.AdventureParser.ExitSouthContext;
import static hartman.games.adventureland.script.AdventureParser.ExitUpContext;
import static hartman.games.adventureland.script.AdventureParser.ExitWestContext;
import static hartman.games.adventureland.script.AdventureParser.GlobalParameterStartContext;
import static hartman.games.adventureland.script.AdventureParser.ItemDeclarationContext;
import static hartman.games.adventureland.script.AdventureParser.RoomExitContext;
import static hartman.games.adventureland.script.AdventureParser.RoomExitsContext;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

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

        return new AdventureVisitor().visit(parser.adventure());
    }

    private static class AdventureVisitor extends AdventureBaseVisitor<Adventure> {

        @Override
        public Adventure visitAdventure(AdventureContext adventureContext) {
            Room startingRoom = getStartingRoom(adventureContext);
            Set<Item> items = getItems(adventureContext);
            return new Adventure(new Vocabulary(emptySet()), emptySet(), emptySet(), items, startingRoom);
        }

        private Room getStartingRoom(AdventureContext adventureContext) {

            RoomDeclarationVisitor visitor = new RoomDeclarationVisitor();
            List<RoomDef> roomDefs = adventureContext.gameElement().stream()
                    .filter(gameElementContext -> null != gameElementContext.roomDeclaration())
                    .map(gameElementContext -> gameElementContext.roomDeclaration().accept(visitor))
                    .collect(toList());

            setExits(roomDefs);

            return getGlobalParameterStart(adventureContext)
                    .flatMap(roomName -> roomDefs.stream()
                            .filter(roomDef -> roomDef.getRoomName().equals(roomName))
                            .map(RoomDef::getRoom)
                            .findFirst())
                    .orElse(roomDefs.isEmpty() ? Room.NOWHERE : roomDefs.get(0).getRoom());
        }

        private void setExits(List<RoomDef> roomDefs) {
            Map<String, Room> roomsByName = roomDefs.stream()
                    .collect(toMap(RoomDef::getRoomName, RoomDef::getRoom));

            roomDefs.forEach(roomDef -> roomDef.getExits().forEach(exitDef -> {
                String exitRoomName = exitDef.getRoomName();
                if (null == exitRoomName || exitRoomName.equals(roomDef.getRoomName())) {
                    roomDef.getRoom().setExitTowardsSelf(exitDef.getDirection());
                } else {
                    if (!roomsByName.containsKey(exitRoomName)) {
                        throw new ParseCancellationException(String.format("Invalid exit. '%s' from room '%s' refers to non-existent room '%s'.", exitDef.getDirection().getName(), roomDef.getRoomName(), exitRoomName));
                    }
                    roomDef.getRoom().setExit(exitDef.getDirection(), roomsByName.get(exitRoomName));
                }
            }));
        }

        private Optional<String> getGlobalParameterStart(AdventureContext adventureContext) {
            return adventureContext.globalParameter().stream()
                    .map(globalParameterContext -> globalParameterContext.accept(new AdventureBaseVisitor<String>() {
                        @Override
                        public String visitGlobalParameterStart(GlobalParameterStartContext ctx) {
                            return ctx.startParameter().roomName().getText();
                        }
                    })).findFirst();
        }

        private Set<Item> getItems(AdventureContext adventureContext) {
            ItemDeclarationVisitor visitor = new ItemDeclarationVisitor();
            return adventureContext.gameElement().stream()
                    .filter(gameElementContext -> null != gameElementContext.itemDeclaration())
                    .map(gameElementContext -> gameElementContext.itemDeclaration().accept(visitor))
                    .collect(toSet());
        }
    }

    private static class RoomDeclarationVisitor extends AdventureBaseVisitor<RoomDef> {
        @Override
        public RoomDef visitRoomDeclaration(AdventureParser.RoomDeclarationContext ctx) {
            Room room = newRoom(ctx);
            List<ExitDef> exitDefs = Optional.ofNullable(ctx.roomExits())
                    .map(roomExits -> roomExits.accept(new RoomExitsVisitor()))
                    .orElse(emptyList());
            return new RoomDef(room, exitDefs);
        }

        private Room newRoom(AdventureParser.RoomDeclarationContext ctx) {
            String name = ctx.roomName().getText();
            String description = ctx.roomDescription().getText();
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

    private static class RoomExitsVisitor extends AdventureBaseVisitor<List<ExitDef>> {
        @Override
        public List<ExitDef> visitRoomExits(RoomExitsContext ctx) {
            RoomExitVisitor visitor = new RoomExitVisitor();
            return ctx.roomExit().stream()
                    .map(roomExit -> roomExit.accept(visitor))
                    .collect(toList());
        }
    }

    private static class RoomExitVisitor extends AdventureBaseVisitor<ExitDef> {
        @Override
        public ExitDef visitRoomExit(RoomExitContext ctx) {
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
        public Word visitExitNorth(ExitNorthContext ctx) {
            return Words.NORTH;
        }

        @Override
        public Word visitExitSouth(ExitSouthContext ctx) {
            return Words.SOUTH;
        }

        @Override
        public Word visitExitEast(ExitEastContext ctx) {
            return Words.EAST;
        }

        @Override
        public Word visitExitWest(ExitWestContext ctx) {
            return Words.WEST;
        }

        @Override
        public Word visitExitUp(ExitUpContext ctx) {
            return Words.UP;
        }

        @Override
        public Word visitExitDown(ExitDownContext ctx) {
            return Words.DOWN;
        }
    }

    private static class ItemDeclarationVisitor extends AdventureBaseVisitor<Item> {
        @Override
        public Item visitItemDeclaration(ItemDeclarationContext ctx) {
            String name = ctx.itemName().getText();
            String description = ctx.itemDescription().getText();

            Item.Builder builder = new Item.Builder().named(name).describedAs(description);

            getAliases(ctx).forEach(builder::alias);

            return builder.build();
        }

        private List<String> getAliases(ItemDeclarationContext ctx) {
            if (ctx.itemAliases() != null && ctx.itemAliases().itemAlias() != null) {
                return ctx.itemAliases().itemAlias().stream()
                        .map(RuleContext::getText)
                        .collect(Collectors.toList());
            }
            return emptyList();
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
}
