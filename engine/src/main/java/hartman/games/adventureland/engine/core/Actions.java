package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Action;
import hartman.games.adventureland.engine.Noun;
import hartman.games.adventureland.engine.Verb;
import hartman.games.adventureland.engine.Vocabulary;

import java.util.LinkedHashSet;
import java.util.Set;

import static hartman.games.adventureland.engine.Action.Condition;
import static hartman.games.adventureland.engine.core.Conditions.nounMatches;
import static hartman.games.adventureland.engine.core.Conditions.or;
import static hartman.games.adventureland.engine.core.Conditions.verbMatches;

public final class Actions {

    public static Actions newActionSet() {
        return new Actions();
    }

    public class Builder extends Action.Builder {

        private Builder() {}

        public Builder on(Verb verb) {
            if (verb.equals(Verb.NONE)) {
                return onNoVerb();
            }
            verbs.add(verb);
            when(verbMatches(verb));
            return this;
        }

        public Builder onNoVerb() {
            // no logical need for a condition to match nothing
            return this;
        }

        public Builder onUnrecognizedVerb() {
            return on(Verb.UNRECOGNIZED);
        }

        public Builder onAnyVerb() {
            return on(Verb.ANY);
        }

        public Builder with(Noun noun) {
            if (noun.equals(Noun.NONE)) {
                return withNoNoun();
            }
            nouns.add(noun);
            when(nounMatches(noun));
            return this;
        }

        public Builder withNoNoun() {
            // no logical need for a condition to match nothing
            return this;
        }

        public Builder the(Noun noun) {
            return with(noun);
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
            if (nouns.length > 0) {
                if (nouns.length == 1) {
                    return with(nouns[0]);
                }
                Condition c1 = null;
                for (Noun n : nouns) {
                    Condition c2 = nounMatches(n);
                    if (c1 == null) {
                        c1 = c2;
                    } else {
                        c1 = or(c2, c1);
                    }
                }
                when(c1);
                return this;
            }
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
