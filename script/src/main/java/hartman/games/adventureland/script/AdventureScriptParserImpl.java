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
import hartman.games.adventureland.engine.core.Rooms;
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
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Stream;

import static hartman.games.adventureland.script.AdventureParser.ActionConditionDeclarationContext;
import static hartman.games.adventureland.script.AdventureParser.ActionDeclarationContext;
import static hartman.games.adventureland.script.AdventureParser.ActionResultDeclarationContext;
import static hartman.games.adventureland.script.AdventureParser.ActionWordAnyContext;
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
import static hartman.games.adventureland.script.AdventureParser.GlobalParameterStartContext;
import static hartman.games.adventureland.script.AdventureParser.ItemAliasesContext;
import static hartman.games.adventureland.script.AdventureParser.ItemDeclarationContext;
import static hartman.games.adventureland.script.AdventureParser.ItemInRoomContext;
import static hartman.games.adventureland.script.AdventureParser.ItemIsInInventoryContext;
import static hartman.games.adventureland.script.AdventureParser.ItemIsNowhereContext;
import static hartman.games.adventureland.script.AdventureParser.ItemLocationContext;
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
import static hartman.games.adventureland.script.AdventureParser.RoomExitsContext;
import static hartman.games.adventureland.script.AdventureParser.WordGroupContext;
import static java.util.Optional.ofNullable;
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
            Rooms rooms = getRooms(adventureContext);
            Set<Room> roomSet = rooms.copyOfRooms();

            Room startingRoom = getStartingRoom(adventureContext, roomSet);

            Items items = getItems(adventureContext, roomSet);
            Set<Item> itemSet = items.copyOfItems();

            Actions occurs = getOccurs(adventureContext, itemSet, roomSet);

            Vocabulary vocabulary = getVocabulary(adventureContext).merge(rooms.buildVocabulary());

            Actions actions = getActions(adventureContext, vocabulary, itemSet, roomSet);

            vocabulary = vocabulary.merge(actions.buildVocabulary());

            return new Adventure(vocabulary, occurs.copyOfActions(), actions.copyOfActions(), itemSet, startingRoom);
        }

        private Rooms getRooms(AdventureContext adventureContext) {
            Rooms rooms = Rooms.newRoomSet();
            RoomDeclarationVisitor visitor = new RoomDeclarationVisitor(rooms);
            adventureContext.gameElement().stream()
                    .filter(gameElementContext -> null != gameElementContext.roomDeclaration())
                    .forEach(gameElementContext -> gameElementContext.roomDeclaration().accept(visitor));
            return rooms;
        }

        private Room getStartingRoom(AdventureContext adventureContext, Set<Room> rooms) {
            if (rooms.isEmpty()) {
                return Room.NOWHERE;
            }
            return getGlobalParameterStart(adventureContext)
                    .flatMap(roomName -> rooms.stream()
                            .filter(room -> room.getName().equals(roomName))
                            .findFirst())
                    .orElse(rooms.iterator().next());
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

        private Items getItems(AdventureContext adventureContext, Set<Room> rooms) {
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

        private Actions getActions(AdventureContext adventureContext, Vocabulary vocabulary, Set<Item> items, Set<Room> rooms) {
            Actions actions = Actions.newActionSet();
            ActionDeclarationVisitor visitor = new ActionDeclarationVisitor(actions, items, rooms, vocabulary);
            adventureContext.gameElement().stream()
                    .filter(gameElementContext -> null != gameElementContext.actionDeclaration())
                    .forEach(gameElementContext -> gameElementContext.actionDeclaration().accept(visitor));
            return actions;
        }

        private Actions getOccurs(AdventureContext adventureContext, Set<Item> items, Set<Room> rooms) {
            Actions actions = Actions.newActionSet();
            OccursDeclarationVisitor visitor = new OccursDeclarationVisitor(actions, items, rooms);
            adventureContext.gameElement().stream()
                    .filter(gameElementContext -> null != gameElementContext.occursDeclaration())
                    .forEach(gameElementContext -> gameElementContext.occursDeclaration().accept(visitor));
            return actions;
        }
    }

    private static class RoomDeclarationVisitor extends AdventureBaseVisitor<Void> {

        private Rooms rooms;

        private RoomDeclarationVisitor(Rooms rooms) {
            this.rooms = rooms;
        }

        @Override
        public Void visitRoomDeclaration(RoomDeclarationContext ctx) {
            Rooms.RoomBuilder roomBuilder = rooms
                    .newRoom()
                    .named(ctx.roomName().getText())
                    .describedAs(ctx.roomDescription().getText());
            setExits(ctx.roomExits(), roomBuilder);
            roomBuilder.build();
            return null;
        }

        private void setExits(RoomExitsContext context, Rooms.RoomBuilder roomBuilder) {
            RoomExitVisitor roomExitVisitor = new RoomExitVisitor(roomBuilder);
            ofNullable(context)
                    .ifPresent(roomExitsContext -> roomExitsContext.roomExit()
                            .forEach(roomExitContext -> roomExitContext.accept(roomExitVisitor)));
        }
    }

    private static class RoomExitVisitor extends AdventureBaseVisitor<Void> {
        private Rooms.RoomBuilder roomBuilder;

        private RoomExitVisitor(Rooms.RoomBuilder roomBuilder) {
            this.roomBuilder = roomBuilder;
        }

        @Override
        public Void visitRoomExit(RoomExitContext ctx) {
            Rooms.RoomExitBuilder roomExitBuilder = roomBuilder.withExit();
            roomExitBuilder.inDirectionOf(ctx.exitDirection().getText());
            ofNullable(ctx.roomName())
                    .map(RuleContext::getText)
                    .ifPresent(roomExitBuilder::towards);
            roomExitBuilder.buildExit();
            return null;
        }
    }

    private static class ItemDeclarationVisitor extends AdventureBaseVisitor<Item> {
        private final Items items;
        private final Set<Room> rooms;

        private ItemDeclarationVisitor(Items items, Set<Room> rooms) {
            this.items = items;
            this.rooms = rooms;
        }

        @Override
        public Item visitItemDeclaration(ItemDeclarationContext ctx) {
            Item.Builder builder = items.newItem()
                    .named(ctx.itemName().getText())
                    .describedAs(ctx.itemDescription().getText());
            setAliases(ctx.itemAliases(), builder);
            setLocation(ctx.itemLocation(), builder);
            return builder.build();
        }

        private void setAliases(ItemAliasesContext context, Item.Builder builder) {
            ofNullable(context)
                    .ifPresent(aliasesContext -> ofNullable(aliasesContext.itemAlias())
                            .ifPresent(aliasContexts -> aliasContexts.stream()
                                    .map(RuleContext::getText)
                                    .forEach(s -> builder.alias(s).portable())));
        }

        private void setLocation(ItemLocationContext context, Item.Builder builder) {
            ofNullable(context)
                    .<Runnable>map(itemLocationContext -> () -> itemLocationContext.accept(new ItemLocationVisitor(builder, rooms)))
                    .orElse(() -> builder.in(Room.NOWHERE))
                    .run(); // Hack for ifPresentOrElse
        }
    }

    private static class ItemLocationVisitor extends AdventureBaseVisitor<Item.Builder> {
        private final Item.Builder builder;
        private final Set<Room> rooms;

        private ItemLocationVisitor(Item.Builder builder, Set<Room> rooms) {
            this.builder = builder;
            this.rooms = rooms;
        }

        @Override
        public Item.Builder visitItemInRoom(ItemInRoomContext ctx) {
            String roomName = ctx.roomName().getText();
            return builder.in(rooms.stream()
                    .filter(room -> room.getName().equals(roomName)).findFirst()
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
        private final Set<Room> rooms;

        private OccursDeclarationVisitor(Actions actions, Set<Item> items, Set<Room> rooms) {
            this.actions = actions;
            this.items = items;
            this.rooms = rooms;
        }

        @Override
        public Action visitOccursDeclaration(OccursDeclarationContext ctx) {
            Actions.ActionBuilder builder = actions.newAction();
            ofNullable(ctx.Number()).ifPresent(number -> builder.when(Conditions.random(Integer.parseInt(number.getText()))));
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

        private ActionDeclarationVisitor(Actions actions, Set<Item> items, Set<Room> rooms, Vocabulary vocabulary) {
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
            AtomicInteger position = new AtomicInteger(1);
            for (ActionWordOrListContext actionWordOrListContext : ctx.actionCommand().actionWordOrList()) {

                ofNullable(actionWordOrListContext.actionWord())
                        .map(actionWordContext -> actionWordContext.accept(actionWordVisitor))
                        .ifPresent(word -> builder.onWordAt(position.getAndIncrement(), word));

                ofNullable(actionWordOrListContext.actionWordList())
                        .map(ActionWordListContext::actionWord)
                        .map(actionWordContextList -> actionWordContextList.stream().map(actionWordContext -> actionWordContext.accept(actionWordVisitor)))
                        .map(wordStream -> wordStream.toArray(Word[]::new))
                        .ifPresent(list -> builder.onAnyWordAt(position.getAndIncrement(), list));
            }
        }

    }

    private static class ActionWordVisitor extends AdventureBaseVisitor<Word> {

        private final Function<String, Word> toWord;

        private ActionWordVisitor(Vocabulary vocabulary) {
            this.toWord = text -> vocabulary.findMatch(text).orElse(new Word(text));
        }

        @Override
        public Word visitActionWordWord(ActionWordWordContext ctx) {
            return toWord.apply(ctx.getText());
        }

        @Override
        public Word visitActionWordDirection(ActionWordDirectionContext ctx) {
            return toWord.apply(ctx.exitDirection().getText());
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
            return Word.unrecognized();
        }

    }

    private abstract static class ActionVisitor<T> extends AdventureBaseVisitor<T> {
        private final Set<Item> items;
        private final Set<Room> rooms;

        private ActionVisitor(Set<Item> items, Set<Room> rooms) {
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

        private ActionResultDeclarationVisitor(Set<Item> items, Set<Room> rooms) {
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
            if (null == ctx.word()) {
                return Results.goInDirectionMatchingCommandWordAt(1);
            }
            String word = ctx.word().getText();
            if (word.startsWith("$")) {
                int position = Integer.parseInt(word.substring(1));
                return Results.goInDirectionMatchingCommandWordAt(position);
            }
            return Results.go(Word.of(word));
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
            Item item = getItemOrFail(ctx.itemName().getText());
            return Results.get(item);
        }

        @Override
        public Action.Result visitResultDrop(ResultDropContext ctx) {
            Item item = getItemOrFail(ctx.itemName().getText());
            return Results.drop(item);
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

        private ActionConditionDeclarationVisitor(Set<Item> items, Set<Room> rooms) {
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
            if (null == ctx.word()) {
                return Conditions.hasExitMatchingCommandWordAt(1);
            }
            String word = ctx.word().getText();
            if (word.startsWith("$")) {
                int position = Integer.parseInt(word.substring(1));
                return Conditions.hasExitMatchingCommandWordAt(position);
            }
            return Conditions.hasExit(Word.of(word));
        }

    }

}
