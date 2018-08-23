package hartman.games.adventureland.script;

import hartman.games.adventureland.engine.Action;
import hartman.games.adventureland.engine.Adventure;
import hartman.games.adventureland.engine.Item;
import hartman.games.adventureland.engine.Room;
import hartman.games.adventureland.engine.Vocabulary;
import hartman.games.adventureland.engine.Word;
import hartman.games.adventureland.engine.core.Actions;
import hartman.games.adventureland.engine.core.Conditions;
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
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static hartman.games.adventureland.script.AdventureParser.ActionConditionDeclarationContext;
import static hartman.games.adventureland.script.AdventureParser.ActionDeclarationContext;
import static hartman.games.adventureland.script.AdventureParser.ActionResultDeclarationContext;
import static hartman.games.adventureland.script.AdventureParser.ActionWordAnyContext;
import static hartman.games.adventureland.script.AdventureParser.ActionWordContext;
import static hartman.games.adventureland.script.AdventureParser.ActionWordDirectionContext;
import static hartman.games.adventureland.script.AdventureParser.ActionWordListContext;
import static hartman.games.adventureland.script.AdventureParser.ActionWordNoneContext;
import static hartman.games.adventureland.script.AdventureParser.ActionWordOrListContext;
import static hartman.games.adventureland.script.AdventureParser.ActionWordUnknownContext;
import static hartman.games.adventureland.script.AdventureParser.ActionWordWordContext;
import static hartman.games.adventureland.script.AdventureParser.AdventureContext;
import static hartman.games.adventureland.script.AdventureParser.ConditionCounterEqualsContext;
import static hartman.games.adventureland.script.AdventureParser.ConditionCounterGreaterThanContext;
import static hartman.games.adventureland.script.AdventureParser.ConditionCounterLessThanContext;
import static hartman.games.adventureland.script.AdventureParser.ConditionFlagIsTrueContext;
import static hartman.games.adventureland.script.AdventureParser.ConditionInRoomContext;
import static hartman.games.adventureland.script.AdventureParser.ConditionItemCarriedContext;
import static hartman.games.adventureland.script.AdventureParser.ConditionItemExistsContext;
import static hartman.games.adventureland.script.AdventureParser.ConditionItemHasMovedContext;
import static hartman.games.adventureland.script.AdventureParser.ConditionItemIsHereContext;
import static hartman.games.adventureland.script.AdventureParser.ConditionItemIsPresentContext;
import static hartman.games.adventureland.script.AdventureParser.ConditionRoomHasExitContext;
import static hartman.games.adventureland.script.AdventureParser.ConditionTimesContext;
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
import static hartman.games.adventureland.script.AdventureParser.OccursDeclarationContext;
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
import static hartman.games.adventureland.script.AdventureParser.WordGroupContext;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
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
            Items items = getItems(adventureContext, rooms);
            Set<Item> itemSet = items.copyOfItems();
            Actions occurs = getOccurs(adventureContext, itemSet, rooms);
            Vocabulary vocabulary = getVocabulary(adventureContext);
            Actions actions = getActions(adventureContext, vocabulary, itemSet, rooms);
            vocabulary = vocabulary.merge(actions.buildVocabulary());
            return new Adventure(vocabulary, occurs.copyOfActions(), actions.copyOfActions(), itemSet, startingRoom);
        }

        private List<Room> getRooms(AdventureContext adventureContext) {

            RoomDeclarationVisitor visitor = new RoomDeclarationVisitor();
            List<RoomHolder> roomHolders = adventureContext.gameElement().stream()
                    .filter(gameElementContext -> null != gameElementContext.roomDeclaration())
                    .map(gameElementContext -> gameElementContext.roomDeclaration().accept(visitor))
                    .collect(toList());

            // resolve the room names to each exit to point to the referenced room object
            roomHolders.forEach(roomHolder ->
                    roomHolder.getExits().forEach(roomExitHolder -> {

                        String exitRoomName = roomExitHolder.getRoomName();

                        if (null == exitRoomName || exitRoomName.equals(roomHolder.getRoomName())) {
                            roomHolder.setExitTowardsSelf(roomExitHolder.getDirection());

                        } else {
                            roomHolders.stream()
                                    .filter(holder -> holder.getRoomName().equals(exitRoomName))
                                    .map(RoomHolder::getRoom)
                                    .findFirst()
                                    .<Runnable>map(room -> () -> roomHolder.setExit(roomExitHolder.getDirection(), room))
                                    .orElse(() -> {
                                        throw new ParseCancellationException(String.format("Invalid exit. Room %s is not defined.", exitRoomName));
                                    }).run(); // could be replaced with Java 9 ifPresentOrElse
                        }
                    }));

            return roomHolders.stream().map(RoomHolder::getRoom).collect(toList());
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
            ActionDeclarationVisitor visitor = new ActionDeclarationVisitor(actions, items, rooms, vocabulary);
            adventureContext.gameElement().stream()
                    .filter(gameElementContext -> null != gameElementContext.actionDeclaration())
                    .forEach(gameElementContext -> gameElementContext.actionDeclaration().accept(visitor));
            return actions;
        }

        private Actions getOccurs(AdventureContext adventureContext, Set<Item> items, List<Room> rooms) {
            Actions actions = Actions.newActionSet();
            OccursDeclarationVisitor visitor = new OccursDeclarationVisitor(actions, items, rooms);
            adventureContext.gameElement().stream()
                    .filter(gameElementContext -> null != gameElementContext.occursDeclaration())
                    .forEach(gameElementContext -> gameElementContext.occursDeclaration().accept(visitor));
            return actions;
        }
    }

    private static class RoomDeclarationVisitor extends AdventureBaseVisitor<RoomHolder> {

        private RoomExitVisitor roomExitVisitor = new RoomExitVisitor();

        @Override
        public RoomHolder visitRoomDeclaration(RoomDeclarationContext ctx) {
            String name = ctx.roomName().getText();
            String description = ctx.roomDescription().getText();
            Room room = new Room(name, description);

            List<RoomExitHolder> roomExitHolders =
                    ofNullable(ctx.roomExits())
                            .map(roomExitsContext -> roomExitsContext.roomExit().stream()
                                    .map(roomExitContext -> roomExitContext.accept(roomExitVisitor))
                                    .collect(toList()))
                            .orElse(emptyList());

            return new RoomHolder(room, roomExitHolders);
        }
    }

    private static class RoomExitVisitor extends AdventureBaseVisitor<RoomExitHolder> {

        private ExitDirectionVisitor exitDirectionVisitor = new ExitDirectionVisitor();

        @Override
        public RoomExitHolder visitRoomExit(RoomExitContext ctx) {
            Word direction = ctx.exitDirection().accept(exitDirectionVisitor);
            String roomName = ofNullable(ctx.roomName()).map(RuleContext::getText).orElse(null);
            return new RoomExitHolder(direction, roomName);
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
        public Word visitWordGroup(WordGroupContext ctx) {
            String word = ctx.word().getText();
            String[] synonyms = ofNullable(ctx.synonym())
                    .map(x -> x.stream().map(RuleContext::getText).toArray(String[]::new))
                    .orElse(new String[0]);
            return new Word(word, synonyms);
        }
    }

    private static class OccursDeclarationVisitor extends AdventureBaseVisitor<Action> {
        private final Actions actions;
        private final Set<Item> items;
        private final List<Room> rooms;

        private OccursDeclarationVisitor(Actions actions, Set<Item> items, List<Room> rooms) {
            this.actions = actions;
            this.items = items;
            this.rooms = rooms;
        }

        @Override
        public Action visitOccursDeclaration(OccursDeclarationContext ctx) {
            Actions.ActionBuilder builder = actions.newAction();
            if (null != ctx.Number()) {
                builder.when(Conditions.random(Integer.parseInt(ctx.Number().getText())));
            }
            actionResults(ctx.actionResultDeclaration(), builder);
            actionConditions(ctx.actionConditionDeclaration(), builder);
            return builder.build();
        }

        void actionResults(List<ActionResultDeclarationContext> contextList, Actions.ActionBuilder builder) {
            ActionResultDeclarationVisitor visitor = new ActionResultDeclarationVisitor(items, rooms);
            ofNullable(contextList).ifPresent(actionResultDeclarationContexts -> actionResultDeclarationContexts.stream()
                    .map(actionResultDeclarationContext -> actionResultDeclarationContext.accept(visitor))
                    .forEach(builder::then));
        }

        void actionConditions(List<ActionConditionDeclarationContext> contextList, Actions.ActionBuilder builder) {
            ActionConditionDeclarationVisitor visitor = new ActionConditionDeclarationVisitor(items, rooms);
            ofNullable(contextList).ifPresent(actionConditionDeclarationContexts -> actionConditionDeclarationContexts.stream()
                    .map(actionConditionDeclarationContext -> actionConditionDeclarationContext.accept(visitor))
                    .forEach(builder::when));
        }
    }

    private static class ActionDeclarationVisitor extends OccursDeclarationVisitor {
        private final Actions actions;
        private final ActionWordVisitor actionWordVisitor;

        private ActionDeclarationVisitor(Actions actions, Set<Item> items, List<Room> rooms, Vocabulary vocabulary) {
            super(actions, items, rooms);
            this.actions = actions;
            this.actionWordVisitor = new ActionWordVisitor(vocabulary);
        }

        @Override
        public Action visitActionDeclaration(ActionDeclarationContext ctx) {
            Actions.ActionBuilder builder = actions.newAction();
            actionCommand(ctx, builder);
            super.actionResults(ctx.actionResultDeclaration(), builder);
            super.actionConditions(ctx.actionConditionDeclaration(), builder);
            return builder.build();
        }

        private void actionCommand(ActionDeclarationContext ctx, Actions.ActionBuilder builder) {
            int wordSetCount = 0;
            for (ActionWordOrListContext actionWordOrListContext : ctx.actionCommand().actionWordOrList()) {

                if (wordSetCount >= 2) {
                    break;
                }

                if (actionWord((wordSetCount == 0) ? builder::on : builder::with, actionWordOrListContext.actionWord())) {
                    wordSetCount++;
                }

                if (actionWordList((wordSetCount == 0) ? builder::onAnyFirstWords : builder::withAnySecondWords, actionWordOrListContext.actionWordList())) {
                    wordSetCount++;
                }
            }
        }

        private boolean actionWord(Consumer<Word> builderFn, ActionWordContext actionWordContext) {
            if (null == actionWordContext) {
                return false;
            }
            Word word = actionWordContext.accept(actionWordVisitor);
            builderFn.accept(word);
            return true;
        }

        private boolean actionWordList(Consumer<Word[]> builderFn, ActionWordListContext actionWordListContext) {
            if (null == actionWordListContext) {
                return false;
            }

            Word[] words = actionWordListContext.actionWord().stream()
                    .map(actionWordContext -> actionWordContext.accept(actionWordVisitor))
                    .toArray(Word[]::new);

            if (words.length > 0) {
                builderFn.accept(words);
                return true;
            }
            return false;
        }

    }

    private static class ActionWordVisitor extends AdventureBaseVisitor<Word> {

        private final Function<String, Word> toWord;
        private final ExitDirectionVisitor exitDirectionVisitor;

        private ActionWordVisitor(Vocabulary vocabulary) {
            this.toWord = text -> vocabulary.findMatch(text).orElse(new Word(text));
            this.exitDirectionVisitor = new ExitDirectionVisitor();
        }

        @Override
        public Word visitActionWordWord(ActionWordWordContext ctx) {
            return toWord.apply(ctx.getText());
        }

        @Override
        public Word visitActionWordDirection(ActionWordDirectionContext ctx) {
            return ctx.exitDirection().accept(exitDirectionVisitor);
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

    private abstract static class ActionVisitor<T> extends AdventureBaseVisitor<T> {
        private final Set<Item> items;
        private final List<Room> rooms;

        private ActionVisitor(Set<Item> items, List<Room> rooms) {
            this.items = items;
            this.rooms = rooms;
        }

        Item getItemOrFail(String itemName) {
            return items.stream()
                    .filter(item -> item.getName().equals(itemName))
                    .findFirst()
                    .orElseThrow(ParseCancellationException::new);
        }

        Room getRoomOrFail(String roomName) {
            return rooms.stream()
                    .filter(room -> room.getName().equals(roomName))
                    .findFirst()
                    .orElseThrow(ParseCancellationException::new);
        }

    }

    private static class ActionResultDeclarationVisitor extends ActionVisitor<Action.Result> {

        private ActionResultDeclarationVisitor(Set<Item> items, List<Room> rooms) {
            super(items, rooms);
        }

        @Override
        public Action.Result visitResultPrint(ResultPrintContext ctx) {
            return Results.println(ctx.message.getText());
        }

        @Override
        public Action.Result visitResultLook(ResultLookContext ctx) {
            return Results.look;
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
            return Results.inventory;
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
            Item item = getItemOrFail(ctx.itemName().getText());
            return Results.dropItem(item);
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

    private static class ActionConditionDeclarationVisitor extends ActionVisitor<Action.Condition> {

        private ActionConditionDeclarationVisitor(Set<Item> items, List<Room> rooms) {
            super(items, rooms);
        }

        @Override
        public Action.Condition visitActionConditionDeclaration(ActionConditionDeclarationContext ctx) {
            Action.Condition condition = super.visitActionConditionDeclaration(ctx);
            if (null != ctx.NOT()) {
                return Conditions.not(condition);
            }
            return condition;
        }

        @Override
        public Action.Condition visitConditionInRoom(ConditionInRoomContext ctx) {
            Room room = getRoomOrFail(ctx.roomName().getText());
            return Conditions.in(room);
        }

        @Override
        public Action.Condition visitConditionItemCarried(ConditionItemCarriedContext ctx) {
            Item item = getItemOrFail(ctx.itemName().getText());
            return Conditions.carrying(item);
        }

        @Override
        public Action.Condition visitConditionItemIsHere(ConditionItemIsHereContext ctx) {
            Item item = getItemOrFail(ctx.itemName().getText());
            return Conditions.here(item);
        }

        @Override
        public Action.Condition visitConditionItemIsPresent(ConditionItemIsPresentContext ctx) {
            Item item = getItemOrFail(ctx.itemName().getText());
            return Conditions.present(item);
        }

        @Override
        public Action.Condition visitConditionItemExists(ConditionItemExistsContext ctx) {
            Item item = getItemOrFail(ctx.itemName().getText());
            return Conditions.exists(item);
        }

        @Override
        public Action.Condition visitConditionItemHasMoved(ConditionItemHasMovedContext ctx) {
            Item item = getItemOrFail(ctx.itemName().getText());
            return Conditions.hasMoved(item);
        }

        @Override
        public Action.Condition visitConditionFlagIsTrue(ConditionFlagIsTrueContext ctx) {
            return Conditions.isFlagSet(ctx.word().getText());
        }

        @Override
        public Action.Condition visitConditionCounterEquals(ConditionCounterEqualsContext ctx) {
            Integer number = Integer.parseInt(ctx.Number().getText());
            return Conditions.compareCounter(ctx.word().getText(), val -> val.equals(number));
        }

        @Override
        public Action.Condition visitConditionCounterLessThan(ConditionCounterLessThanContext ctx) {
            Integer number = Integer.parseInt(ctx.Number().getText());
            return Conditions.compareCounter(ctx.word().getText(), val -> val < number);
        }

        @Override
        public Action.Condition visitConditionCounterGreaterThan(ConditionCounterGreaterThanContext ctx) {
            Integer number = Integer.parseInt(ctx.Number().getText());
            return Conditions.compareCounter(ctx.word().getText(), val -> val > number);
        }

        @Override
        public Action.Condition visitConditionRoomHasExit(ConditionRoomHasExitContext ctx) {
            return Conditions.roomHasExit;
        }

        @Override
        public Action.Condition visitConditionTimes(ConditionTimesContext ctx) {
            int val = Integer.parseInt(ctx.Number().getText());
            return Conditions.times(val);
        }
    }

}


class RoomHolder {
    private final Room room;
    private final List<RoomExitHolder> exits;

    RoomHolder(Room room, List<RoomExitHolder> exits) {
        this.room = room;
        this.exits = exits;
    }

    Room getRoom() {
        return room;
    }

    String getRoomName() {
        return room.getName();
    }

    List<RoomExitHolder> getExits() {
        return exits;
    }

    void setExitTowardsSelf(Word direction) {
        room.setExitTowardsSelf(direction);
    }

    void setExit(Word direction, Room towards) {
        room.setExit(direction, towards);
    }

}

class RoomExitHolder {
    private final Word direction;
    private final String roomName;

    RoomExitHolder(Word direction, String roomName) {
        this.direction = direction;
        this.roomName = roomName;
    }

    Word getDirection() {
        return direction;
    }

    String getRoomName() {
        return roomName;
    }
}
