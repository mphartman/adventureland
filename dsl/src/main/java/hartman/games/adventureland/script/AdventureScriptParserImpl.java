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
import org.antlr.v4.runtime.tree.RuleNode;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static hartman.games.adventureland.script.AdventureParser.AdventureContext;
import static hartman.games.adventureland.script.AdventureParser.ExitDownContext;
import static hartman.games.adventureland.script.AdventureParser.ExitEastContext;
import static hartman.games.adventureland.script.AdventureParser.ExitNorthContext;
import static hartman.games.adventureland.script.AdventureParser.ExitSouthContext;
import static hartman.games.adventureland.script.AdventureParser.ExitUpContext;
import static hartman.games.adventureland.script.AdventureParser.ExitWestContext;
import static hartman.games.adventureland.script.AdventureParser.GlobalParameterStartContext;
import static hartman.games.adventureland.script.AdventureParser.ItemDeclarationContext;
import static hartman.games.adventureland.script.AdventureParser.ItemInRoomContext;
import static hartman.games.adventureland.script.AdventureParser.ItemIsInInventoryContext;
import static hartman.games.adventureland.script.AdventureParser.ItemIsNowhereContext;
import static hartman.games.adventureland.script.AdventureParser.NounGroupContext;
import static hartman.games.adventureland.script.AdventureParser.RoomDeclarationContext;
import static hartman.games.adventureland.script.AdventureParser.RoomExitContext;
import static hartman.games.adventureland.script.AdventureParser.RoomExitsContext;
import static hartman.games.adventureland.script.AdventureParser.VerbGroupContext;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Optional.ofNullable;
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
            List<Room> rooms = getRooms(adventureContext);
            Room startingRoom = getStartingRoom(adventureContext, rooms);
            Set<Item> items = getItems(adventureContext, rooms);
            Vocabulary vocabulary = getVocabulary(adventureContext);
            return new Adventure(vocabulary, emptySet(), emptySet(), items, startingRoom);
        }

        private List<Room> getRooms(AdventureContext adventureContext) {
            RoomDeclarationVisitor visitor = new RoomDeclarationVisitor();
            List<RoomDef> roomDefs = adventureContext.gameElement().stream()
                    .filter(gameElementContext -> null != gameElementContext.roomDeclaration())
                    .map(gameElementContext -> gameElementContext.roomDeclaration().accept(visitor))
                    .collect(toList());

            setExits(roomDefs);

            return roomDefs.stream().map(RoomDef::getRoom).collect(toList());
        }

        private Room getStartingRoom(AdventureContext adventureContext, List<Room> rooms) {
            if (rooms.isEmpty()) {
                return Room.NOWHERE;
            }
            return getGlobalParameterStart(adventureContext)
                    .flatMap(roomName -> rooms.stream()
                            .filter(room -> room.getName().equals(roomName))
                            .findFirst())
                    .orElse(rooms.get(0));
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

        private Set<Item> getItems(AdventureContext adventureContext, List<Room> rooms) {
            ItemDeclarationVisitor visitor = new ItemDeclarationVisitor(rooms);
            return adventureContext.gameElement().stream()
                    .filter(gameElementContext -> null != gameElementContext.itemDeclaration())
                    .map(gameElementContext -> gameElementContext.itemDeclaration().accept(visitor))
                    .collect(toSet());
        }

        private Vocabulary getVocabulary(AdventureContext adventureContext) {
            VocabularyDeclarationVisitor visitor = new VocabularyDeclarationVisitor();
            Set<Word> words = adventureContext.gameElement().stream()
                    .filter(gameElementContext -> null != gameElementContext.vocabularyDeclaration())
                    .map(gameElementContext -> gameElementContext.vocabularyDeclaration().accept(visitor))
                    .collect(toSet());
            return new Vocabulary(words);
        }
    }

    private static class RoomDeclarationVisitor extends AdventureBaseVisitor<RoomDef> {
        @Override
        public RoomDef visitRoomDeclaration(RoomDeclarationContext ctx) {
            Room room = newRoom(ctx);
            List<ExitDef> exitDefs = ofNullable(ctx.roomExits())
                    .map(roomExits -> roomExits.accept(new RoomExitsVisitor()))
                    .orElse(emptyList());
            return new RoomDef(room, exitDefs);
        }

        private Room newRoom(RoomDeclarationContext ctx) {
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
        private List<Room> rooms;

        private ItemDeclarationVisitor(List<Room> rooms) {
            this.rooms = rooms;
        }

        @Override
        public Item visitItemDeclaration(ItemDeclarationContext ctx) {
            Item.Builder builder = new Item.Builder()
                    .named(ctx.itemName().getText())
                    .describedAs(ctx.itemDescription().getText());

            setAliases(ctx, builder);

            setLocation(ctx, builder);

            return builder.build();
        }

        private void setAliases(ItemDeclarationContext ctx, Item.Builder builder) {
            ofNullable(ctx.itemAliases())
                    .ifPresent(aliasesContext -> ofNullable(aliasesContext.itemAlias())
                            .ifPresent(aliasContexts -> aliasContexts.stream()
                                    .map(RuleContext::getText)
                                    .forEach(s -> builder.alias(s).portable())));
        }

        private void setLocation(ItemDeclarationContext thisItemCtx, Item.Builder builder) {
            if (null != thisItemCtx.itemLocation()) {
                thisItemCtx.itemLocation().accept(
                        new AdventureBaseVisitor<Item.Builder>() {
                            @Override
                            public Item.Builder visitItemInRoom(ItemInRoomContext itemInRoomCtx) {
                                return builder.in(rooms.stream()
                                        .filter(room -> room.getName().equals(itemInRoomCtx.roomName().getText())).findFirst()
                                        .orElseThrow(ParseCancellationException::new));
                            }

                            @Override
                            public Item.Builder visitItemIsNowhere(ItemIsNowhereContext ctx) {
                                return builder.in(Room.NOWHERE);
                            }

                            @Override
                            public Item.Builder visitItemIsInInventory(ItemIsInInventoryContext ctx) {
                                return builder.inInventory();
                            }
                        });
            } else {
                // find the closest previous room
                ofNullable(thisItemCtx.getParent().getParent().accept(
                        new AdventureBaseVisitor<String>() {

                            private String roomName = null;
                            private boolean shouldVisitNextChild = true;

                            @Override
                            public String visitRoomDeclaration(RoomDeclarationContext thatRoomCtx) {
                                roomName = thatRoomCtx.roomName().getText();
                                return super.visitRoomDeclaration(thatRoomCtx);
                            }

                            @Override
                            public String visitItemDeclaration(ItemDeclarationContext thatItemCtx) {
                                if (thisItemCtx.equals(thatItemCtx)) {
                                    shouldVisitNextChild = false;
                                    return roomName;
                                }
                                return super.visitItemDeclaration(thatItemCtx);
                            }

                            @Override
                            protected boolean shouldVisitNextChild(RuleNode node, String currentResult) {
                                return shouldVisitNextChild;
                            }
                        }
                )).flatMap(roomName -> rooms.stream()
                        .filter(room -> room.getName().equals(roomName))
                        .findFirst())
                        .ifPresent(builder::in);
            }

        }
    }

    private static class VocabularyDeclarationVisitor extends AdventureBaseVisitor<Word> {

        @Override
        public Word visitVerbGroup(VerbGroupContext ctx) {
            String verb = ctx.verb().getText();
            String[] synonyms = new String[0];
            if (null != ctx.synonym()) {
                synonyms = ctx.synonym().stream().map(RuleContext::getText).toArray(String[]::new);
            }
            return new Word(verb, synonyms);
        }

        @Override
        public Word visitNounGroup(NounGroupContext ctx) {
            String noun = ctx.noun().getText();
            String[] synonyms = new String[0];
            if (null != ctx.synonym()) {
                synonyms = ctx.synonym().stream().map(RuleContext::getText).toArray(String[]::new);
            }
            return new Word(noun, synonyms);
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
