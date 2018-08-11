package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Action;
import hartman.games.adventureland.engine.Item;
import hartman.games.adventureland.engine.Noun;
import hartman.games.adventureland.engine.Verb;
import hartman.games.adventureland.engine.Vocabulary;

import java.util.LinkedHashSet;
import java.util.Set;

public final class Actions {

    private Actions() {
        throw new IllegalStateException("utility class");
    }

    public static class ActionSet {

        private Set<Action> actions = new LinkedHashSet<>();
        private Set<Verb> verbs = new LinkedHashSet<>();
        private Set<Noun> nouns = new LinkedHashSet<>();

        public Action.Builder newAction() {
            return new Action.Builder() {
                @Override
                public Action.Builder on(Verb verb) {
                    verbs.add(verb);
                    return super.on(verb);
                }

                @Override
                public Action.Builder with(Noun noun) {
                    nouns.add(noun);
                    return super.with(noun);
                }

                @Override
                public Action.Builder with(Item item) {
                    nouns.add(item.asNoun());
                    return super.with(item);
                }

                @Override
                public Action build() {
                    Action action = super.build();
                    actions.add(action);
                    return action;
                }

            };
        }

        public Set<Action> copyOfActions() {
            return new LinkedHashSet<>(actions);
        }

        public ActionSet addAll(ActionSet actionSet) {
            if (actionSet != null) {
                verbs.addAll(actionSet.verbs);
                nouns.addAll(actionSet.nouns);
                actions.addAll(actionSet.actions);
            }
            return this;
        }

        public Vocabulary buildVocabulary() {
            return new Vocabulary(verbs, nouns);
        }
    }

    public static ActionSet newActionSet() {
        return new ActionSet();
    }
}
