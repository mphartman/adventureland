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

    public class Builder extends Action.Builder {

        private Builder() {}

        public Builder on(Verb verb) {
            verbs.add(verb);
            when(matches(verb));
            return this;
        }

        public Builder onNoVerb() {
            return on(Verb.NONE);
        }

        public Builder onUnrecognizedVerb() {
            return on(Verb.UNRECOGNIZED);
        }

        public Builder onAnyVerb() {
            return on(Verb.ANY);
        }

        public Builder onAnyOf(Verb... verbs) {
            when(anyMatches(verbs));
            return this;
        }

        public Builder with(Noun noun) {
            nouns.add(noun);
            when(matches(noun));
            return this;
        }

        public Builder the(Noun noun) {
            return with(noun);
        }

        public Builder withNoNoun() {
            return with(Noun.NONE);
        }

        public Builder withUnrecognizedNoun() {
            return with(Noun.UNRECOGNIZED);
        }

        public Builder withAnyNoun() {
            return with(Noun.ANY);
        }

        public Builder anything() {
            return withAnyNoun();
        }

        public Builder withAnyOf(Noun... nouns) {
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

    public Builder newAction() {
        return new Builder();
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
