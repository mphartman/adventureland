package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Action;
import hartman.games.adventureland.engine.Vocabulary;
import hartman.games.adventureland.engine.Word;

import java.util.LinkedHashSet;
import java.util.Set;

import static hartman.games.adventureland.engine.Word.ANY;
import static hartman.games.adventureland.engine.Word.NONE;
import static hartman.games.adventureland.engine.Word.UNRECOGNIZED;
import static hartman.games.adventureland.engine.core.Conditions.anyMatchesWord;
import static hartman.games.adventureland.engine.core.Conditions.wordMatches;
import static java.util.Arrays.asList;

public final class Actions {

    public static Actions newActionSet() {
        return new Actions();
    }

    public class ActionBuilder extends Action.Builder {

        private ActionBuilder() {
        }

        public ActionBuilder onWordAt(int position, Word word) {
            words.add(word);
            when(wordMatches(word, position));
            return this;
        }

        public ActionBuilder onAnyWordAt(int position, Word... wordList) {
            words.addAll(asList(wordList));
            when(anyMatchesWord(position, wordList));
            return this;
        }

        public ActionBuilder on(Word verb) {
            return onWordAt(1, verb);
        }

        public ActionBuilder onNoFirstWord() {
            return on(NONE);
        }

        public ActionBuilder onUnrecognizedFirstWord() {
            return on(UNRECOGNIZED);
        }

        public ActionBuilder onAnyFirstWord() {
            return on(ANY);
        }

        public ActionBuilder onAnyFirstWords(Word... verbs) {
            return onAnyWordAt(1, verbs);
        }

        public ActionBuilder with(Word word) {
            return onWordAt(2, word);
        }

        public ActionBuilder the(Word word) {
            return with(word);
        }

        public ActionBuilder withNoSecondWord() {
            return with(NONE);
        }

        public ActionBuilder withUnrecognizedSecondWord() {
            return with(UNRECOGNIZED);
        }

        public ActionBuilder withAnySecondWord() {
            return with(ANY);
        }

        public ActionBuilder anything() {
            return withAnySecondWord();
        }

        public ActionBuilder withAnySecondWords(Word... nouns) {
            return onAnyWordAt(2, nouns);
        }

        @Override
        public Action build() {
            Action action = super.build();
            actions.add(action);
            return action;
        }

    }

    private Set<Action> actions = new LinkedHashSet<>();
    private Set<Word> words = new LinkedHashSet<>();

    private Actions() {

    }

    public ActionBuilder newAction() {
        return new ActionBuilder();
    }

    public Set<Action> copyOfActions() {
        return new LinkedHashSet<>(actions);
    }

    public Actions merge(Actions that) {
        Actions mergedActions = new Actions();
        mergedActions.actions.addAll(this.actions);
        mergedActions.actions.addAll(that.actions);
        mergedActions.words.addAll(this.words);
        mergedActions.words.addAll(that.words);
        return mergedActions;
    }

    public Vocabulary buildVocabulary() {
        return new Vocabulary(words);
    }

}
