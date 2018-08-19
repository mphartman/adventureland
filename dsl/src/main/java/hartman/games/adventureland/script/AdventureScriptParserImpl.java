package hartman.games.adventureland.script;

import hartman.games.adventureland.engine.Action;
import hartman.games.adventureland.engine.Adventure;
import hartman.games.adventureland.engine.Item;
import hartman.games.adventureland.engine.Room;
import hartman.games.adventureland.engine.Vocabulary;
import hartman.games.adventureland.engine.Word;
import hartman.games.adventureland.engine.core.Actions;
import hartman.games.adventureland.engine.core.Items;
import hartman.games.adventureland.engine.core.Results;
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
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static hartman.games.adventureland.script.AdventureParser.ActionDeclarationContext;
import static hartman.games.adventureland.script.AdventureParser.ActionWordAnyContext;
import static hartman.games.adventureland.script.AdventureParser.ActionWordNoneContext;
import static hartman.games.adventureland.script.AdventureParser.ActionWordUnknownContext;
import static hartman.games.adventureland.script.AdventureParser.ActionWordWordContext;
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
import static hartman.games.adventureland.script.AdventureParser.ResultDecrementCounterContext;
import static hartman.games.adventureland.script.AdventureParser.ResultDestroyContext;
import static hartman.games.adventureland.script.AdventureParser.ResultDropContext;
import static hartman.games.adventureland.script.AdventureParser.ResultGetContext;
import static hartman.games.adventureland.script.AdventureParser.ResultGoContext;
import static hartman.games.adventureland.script.AdventureParser.ResultGotoRoomContext;
import static hartman.games.adventureland.script.AdventureParser.ResultIncrementCounterContext;
import static hartman.games.adventureland.script.AdventureParser.ResultInventoryContext;
import static hartman.games.adventureland.script.AdventureParser.ResultLookContext;
import static hartman.games.adventureland.script.AdventureParser.ResultPrintContext;
import static hartman.games.adventureland.script.AdventureParser.ResultPutContext;
import static hartman.games.adventureland.script.AdventureParser.ResultPutHereContext;
import static hartman.games.adventureland.script.AdventureParser.ResultPutWithContext;
import static hartman.games.adventureland.script.AdventureParser.ResultQuitContext;
import static hartman.games.adventureland.script.AdventureParser.ResultResetCounterContext;
import static hartman.games.adventureland.script.AdventureParser.ResultResetFlagContext;
import static hartman.games.adventureland.script.AdventureParser.ResultSetCounterContext;
import static hartman.games.adventureland.script.AdventureParser.ResultSetFlagContext;
import static hartman.games.adventureland.script.AdventureParser.ResultSetStringContext;
import static hartman.games.adventureland.script.AdventureParser.ResultSwapContext;
import static hartman.games.adventureland.script.AdventureParser.RoomDeclarationContext;
import static hartman.games.adventureland.script.AdventureParser.RoomExitContext;
import static hartman.games.adventureland.script.AdventureParser.RoomExitsContext;
import static hartman.games.adventureland.script.AdventureParser.VerbGroupContext;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

/**
 * ANTLR-based parser implementation which recognizes scripts written in Adventure grammar.
 */
public class AdventureScriptParserImpl implements AdventureScriptParser {

    private static final Function<String, String> stripQuotes = s -> {
        if (s.startsWith("\"")) s = s.substring(1);
        if (s.endsWith("\"")) s = s.substring(0, s.length() - 1);
        return s;
    };

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
            Items items = getItems(adventureContext, rooms);
            Set<Item> itemSet = items.copyOfItems();
            Vocabulary vocabulary = getVocabulary(adventureContext);
            Actions actions = getActions(adventureContext, vocabulary, itemSet, rooms);
            vocabulary = vocabulary.merge(actions.buildVocabulary());
            return new Adventure(vocabulary, emptySet(), actions.copyOfActions(), itemSet, startingRoom);
        }

        private List<Room> getRooms(AdventureContext adventureContext) {
            RoomDeclarationVisitor visitor = new RoomDeclarationVisitor();
            List<RoomDef> roomDefs = adventureContext.gameElement().stream()
                    .filter(gameElementContext -> null != gameElementContext.roomDeclaration())
                    .map(gameElementContext -> gameElementContext.roomDeclaration().accept(visitor))
                    .collect(toList());

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

        private Optional<String> getGlobalParameterStart(AdventureContext adventureContext) {
            return adventureContext.globalParameter().stream()
                    .map(globalParameterContext -> globalParameterContext.accept(new AdventureBaseVisitor<String>() {
                        @Override
                        public String visitGlobalParameterStart(GlobalParameterStartContext ctx) {
                            return ctx.startParameter().roomName().getText();
                        }
                    })).findFirst();
        }

        private Items getItems(AdventureContext adventureContext, List<Room> rooms) {
            Items items = Items.newItemSet();
            ItemDeclarationVisitor visitor = new ItemDeclarationVisitor(items, rooms);
            adventureContext.gameElement().stream()
                    .filter(gameElementContext -> null != gameElementContext.itemDeclaration())
                    .forEach(gameElementContext -> gameElementContext.itemDeclaration().accept(visitor));
            return items;
        }

        private Vocabulary getVocabulary(AdventureContext adventureContext) {
            VocabularyDeclarationVisitor visitor = new VocabularyDeclarationVisitor();
            Set<Word> words = adventureContext.gameElement().stream()
                    .filter(gameElementContext -> null != gameElementContext.vocabularyDeclaration())
                    .map(gameElementContext -> gameElementContext.vocabularyDeclaration().accept(visitor))
                    .collect(toSet());
            return new Vocabulary(words);
        }

        private Actions getActions(AdventureContext adventureContext, Vocabulary vocabulary, Set<Item> items, List<Room> rooms) {
            Actions actions = Actions.newActionSet();
            ActionDeclarationVisitor visitor = new ActionDeclarationVisitor(actions, vocabulary, items, rooms);
            adventureContext.gameElement().stream()
                    .filter(gameElementContext -> null != gameElementContext.actionDeclaration())
                    .forEach(gameElementContext -> gameElementContext.actionDeclaration().accept(visitor));
            return actions;
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
            String description = stripQuotes.apply(ctx.roomDescription().getText());
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
        private final Items items;
        private final List<Room> rooms;

        private ItemDeclarationVisitor(Items items, List<Room> rooms) {
            this.items = items;
            this.rooms = rooms;
        }

        @Override
        public Item visitItemDeclaration(ItemDeclarationContext ctx) {
            Item.Builder builder = items.newItem()
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
            String verb = stripQuotes.apply(ctx.verb.getText());
            String[] synonyms = new String[0];
            if (null != ctx.synonym()) {
                synonyms = ctx.synonym().stream().map(RuleContext::getText).toArray(String[]::new);
            }
            return new Word(verb, synonyms);
        }

        @Override
        public Word visitNounGroup(NounGroupContext ctx) {
            String noun = stripQuotes.apply(ctx.noun.getText());
            String[] synonyms = new String[0];
            if (null != ctx.synonym()) {
                synonyms = ctx.synonym().stream().map(RuleContext::getText).toArray(String[]::new);
            }
            return new Word(noun, synonyms);
        }
    }

    private static class ActionDeclarationVisitor extends AdventureBaseVisitor<Action> {
        private final Actions actions;
        private final Vocabulary vocabulary;
        private final Set<Item> items;
        private final List<Room> rooms;

        private ActionDeclarationVisitor(Actions actions, Vocabulary vocabulary, Set<Item> items, List<Room> rooms) {
            this.actions = actions;
            this.vocabulary = vocabulary;
            this.items = items;
            this.rooms = rooms;
        }

        @Override
        public Action visitActionDeclaration(ActionDeclarationContext ctx) {
            Actions.ActionBuilder builder = actions.newAction();
            actionCommand(ctx, builder);
            actionResults(ctx, builder);
            return builder.build();
        }

        private void actionCommand(ActionDeclarationContext ctx, Actions.ActionBuilder builder) {
            ActionWordContextVisitor visitor = new ActionWordContextVisitor(text -> vocabulary.findMatch(text).orElse(new Word(text)));
            List<Consumer<? super Word>> consumers = asList(builder::on, builder::with);
            IntStream.range(0, 2).forEach(i ->
                    ofNullable(ctx.actionCommand().actionWord(i))
                            .map(actionWordContext -> actionWordContext.accept(visitor))
                            .filter(w -> !w.equals(Word.NONE))
                            .ifPresent(consumers.get(i)));
        }

        private void actionResults(ActionDeclarationContext ctx, Actions.ActionBuilder builder) {
            ActionResultDeclarationVisitor visitor = new ActionResultDeclarationVisitor(items, rooms);
            ofNullable(ctx.actionResultDeclaration())
                    .ifPresent(actionResultDeclarationContexts -> actionResultDeclarationContexts.stream()
                            .map(actionResultDeclarationContext -> actionResultDeclarationContext.accept(visitor))
                            .forEach(builder::then));
        }
    }

    private static class ActionWordContextVisitor extends AdventureBaseVisitor<Word> {

        private Function<String, Word> toWord;

        private ActionWordContextVisitor(Function<String, Word> toWord) {
            this.toWord = toWord;
        }

        @Override
        public Word visitActionWordWord(ActionWordWordContext ctx) {
            return of(ctx.getText())
                    .map(stripQuotes)
                    .map(toWord)
                    .get();
        }

        @Override
        public Word visitActionWordAny(ActionWordAnyContext ctx) {
            return Word.ANY;
        }

        @Override
        public Word visitActionWordNone(ActionWordNoneContext ctx) {
            return Word.NONE;
        }

        @Override
        public Word visitActionWordUnknown(ActionWordUnknownContext ctx) {
            return Word.UNRECOGNIZED;
        }

    }

    private static class ActionResultDeclarationVisitor extends AdventureBaseVisitor<Action.Result> {
        private final Set<Item> items;
        private final List<Room> rooms;

        private ActionResultDeclarationVisitor(Set<Item> items, List<Room> rooms) {
            this.items = items;
            this.rooms = rooms;
        }

        private Item getItemOrFail(String itemName) {
            return items.stream()
                    .filter(item -> item.getName().equals(itemName))
                    .findFirst()
                    .orElseThrow(ParseCancellationException::new);
        }

        private Room getRoomOrFail(String roomName) {
            return rooms.stream()
                    .filter(room -> room.getName().equals(roomName))
                    .findFirst()
                    .orElseThrow(ParseCancellationException::new);
        }

        @Override
        public Action.Result visitResultPrint(ResultPrintContext ctx) {
            return Results.println(stripQuotes.apply(ctx.message.getText()));
        }

        @Override
        public Action.Result visitResultLook(ResultLookContext ctx) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Action.Result visitResultGo(ResultGoContext ctx) {
            return Results.go;
        }

        @Override
        public Action.Result visitResultQuit(ResultQuitContext ctx) {
            return Results.quit;
        }

        @Override
        public Action.Result visitResultInventory(ResultInventoryContext ctx) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Action.Result visitResultSwap(ResultSwapContext ctx) {
            Item item1 = getItemOrFail(ctx.i1.getText());
            Item item2 = getItemOrFail(ctx.i2.getText());
            return Results.swap(item1, item2);
        }

        @Override
        public Action.Result visitResultGotoRoom(ResultGotoRoomContext ctx) {
            Room room = getRoomOrFail(ctx.roomName().getText());
            return Results.gotoRoom(room);
        }

        @Override
        public Action.Result visitResultPut(ResultPutContext ctx) {
            Item item = getItemOrFail(ctx.itemName().getText());
            Room room = getRoomOrFail(ctx.roomName().getText());
            return Results.put(item, room);
        }

        @Override
        public Action.Result visitResultPutHere(ResultPutHereContext ctx) {
            Item item = getItemOrFail(ctx.itemName().getText());
            return Results.putHere(item);
        }

        @Override
        public Action.Result visitResultGet(ResultGetContext ctx) {
            return Results.get;
        }

        @Override
        public Action.Result visitResultDrop(ResultDropContext ctx) {
            return Results.drop;
        }

        @Override
        public Action.Result visitResultPutWith(ResultPutWithContext ctx) {
            Item item1 = getItemOrFail(ctx.i1.getText());
            Item item2 = getItemOrFail(ctx.i2.getText());
            return Results.putWith(item1, item2);
        }

        @Override
        public Action.Result visitResultDestroy(ResultDestroyContext ctx) {
            Item item = getItemOrFail(ctx.itemName().getText());
            return Results.destroy(item);
        }

        @Override
        public Action.Result visitResultSetFlag(ResultSetFlagContext ctx) {
            String name = ctx.word().getText();
            Boolean val = Stream.of("yes", "on", "true").anyMatch(s -> s.equalsIgnoreCase(ctx.booleanValue().getText()));
            return Results.setFlag(name, val);
        }

        @Override
        public Action.Result visitResultResetFlag(ResultResetFlagContext ctx) {
            return Results.resetFlag(ctx.word().getText());
        }

        @Override
        public Action.Result visitResultSetCounter(ResultSetCounterContext ctx) {
            String name = ctx.word().getText();
            Integer val = Integer.parseInt(ctx.Number().getText());
            return Results.setCounter(name, val);
        }

        @Override
        public Action.Result visitResultIncrementCounter(ResultIncrementCounterContext ctx) {
            return Results.incrementCounter(ctx.word().getText());
        }

        @Override
        public Action.Result visitResultDecrementCounter(ResultDecrementCounterContext ctx) {
            return Results.decrementCounter(ctx.word().getText());
        }

        @Override
        public Action.Result visitResultResetCounter(ResultResetCounterContext ctx) {
            return Results.resetCounter(ctx.word().getText());
        }

        @Override
        public Action.Result visitResultSetString(ResultSetStringContext ctx) {
            String key = ctx.k.getText();
            String value = ctx.v.getText();
            return Results.setString(key, value);
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
