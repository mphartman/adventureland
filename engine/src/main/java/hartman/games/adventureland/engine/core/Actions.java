package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Action;
import hartman.games.adventureland.engine.Noun;
import hartman.games.adventureland.engine.Verb;
import hartman.games.adventureland.engine.Vocabulary;

import java.util.LinkedHashSet;
import java.util.Set;

import static hartman.games.adventureland.engine.core.Conditions.anyMatches;
import static hartman.games.adventureland.engine.core.Conditions.matches;

public final class Actions {

    public static Actions newActionSet() {
        return new Actions();
    }

    public class ActionBuilder extends Action.Builder {

        private ActionBuilder() {}

        public ActionBuilder on(Verb verb) {
            verbs.add(verb);
            when(matches(verb));
            return this;
        }

        public ActionBuilder onNoVerb() {
            return on(Verb.NONE);
        }

        public ActionBuilder onUnrecognizedVerb() {
            return on(Verb.UNRECOGNIZED);
        }

        public ActionBuilder onAnyVerb() {
            return on(Verb.ANY);
        }

        public ActionBuilder onAnyOf(Verb... verbs) {
            when(anyMatches(verbs));
            return this;
        }

        public ActionBuilder with(Noun noun) {
            nouns.add(noun);
            when(matches(noun));
            return this;
        }

        public ActionBuilder the(Noun noun) {
            return with(noun);
        }

        public ActionBuilder withNoNoun() {
            return with(Noun.NONE);
        }

        public ActionBuilder withUnrecognizedNoun() {
            return with(Noun.UNRECOGNIZED);
        }

        public ActionBuilder withAnyNoun() {
            return with(Noun.ANY);
        }

        public ActionBuilder anything() {
            return withAnyNoun();
        }

        public ActionBuilder withAnyOf(Noun... nouns) {
            when(anyMatches(nouns));
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
    private Set<Verb> verbs = new LinkedHashSet<>();
    private Set<Noun> nouns = new LinkedHashSet<>();

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
