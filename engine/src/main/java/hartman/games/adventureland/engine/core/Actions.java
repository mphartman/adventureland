package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Action;
import hartman.games.adventureland.engine.Vocabulary;
import hartman.games.adventureland.engine.Word;

import java.util.LinkedHashSet;
import java.util.Set;

import static hartman.games.adventureland.engine.core.Conditions.anyMatches;
import static hartman.games.adventureland.engine.core.Conditions.anyVerbMatches;
import static hartman.games.adventureland.engine.core.Conditions.matches;
import static hartman.games.adventureland.engine.core.Conditions.verbMatches;

public final class Actions {

    public static Actions newActionSet() {
        return new Actions();
    }

    public class ActionBuilder extends Action.Builder {

        private ActionBuilder() {}

        public ActionBuilder on(Word verb) {
            verbs.add(verb);
            when(verbMatches(verb));
            return this;
        }

        public ActionBuilder onNoVerb() {
            return on(Word.NONE);
        }

        public ActionBuilder onUnrecognizedVerb() {
            return on(Word.UNRECOGNIZED);
        }

        public ActionBuilder onAnyVerb() {
            return on(Word.ANY);
        }

        public ActionBuilder onAnyOf(Word... verbs) {
            when(anyVerbMatches(verbs));
            return this;
        }

        public ActionBuilder with(Word word) {
            nouns.add(word);
            when(matches(word));
            return this;
        }

        public ActionBuilder the(Word word) {
            return with(word);
        }

        public ActionBuilder withNoNoun() {
            return with(Word.NONE);
        }

        public ActionBuilder withUnrecognizedNoun() {
            return with(Word.UNRECOGNIZED);
        }

        public ActionBuilder withAnyNoun() {
            return with(Word.ANY);
        }

        public ActionBuilder anything() {
            return withAnyNoun();
        }

        public ActionBuilder withAnyOf(Word... words) {
            when(anyMatches(words));
            return this;
        }

        @Override
        public Action build() {
            Action action = super.build();
            actions.add(action);
            return action;
        }

    }

    private Set<Action> actions = new LinkedHashSet<>();
    private Set<Word> verbs = new LinkedHashSet<>();
    private Set<Word> nouns = new LinkedHashSet<>();

    private Actions() {

    }

    public ActionBuilder newAction() {
        return new ActionBuilder();
    }

    public Set<Action> copyOfActions() {
        return new LinkedHashSet<>(actions);
    }

    public Actions addAll(Actions actions) {
        this.verbs.addAll(actions.verbs);
        this.nouns.addAll(actions.nouns);
        this.actions.addAll(actions.actions);
        return this;
    }

    public Vocabulary buildVocabulary() {
        return new Vocabulary(verbs, nouns);
    }

}
