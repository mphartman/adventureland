package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Action;
import hartman.games.adventureland.engine.Noun;
import hartman.games.adventureland.engine.Verb;
import hartman.games.adventureland.engine.Vocabulary;

import java.util.LinkedHashSet;
import java.util.Set;

public final class Actions {

    public static Actions newActionSet() {
        return new Actions();
    }

    private Set<Action> actions = new LinkedHashSet<>();
    private Set<Verb> verbs = new LinkedHashSet<>();
    private Set<Noun> nouns = new LinkedHashSet<>();

    private Actions() {

    }

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

    public Actions addAll(Actions actions) {
        verbs.addAll(actions.verbs);
        nouns.addAll(actions.nouns);
        this.actions.addAll(actions.actions);
        return this;
    }

    public Vocabulary buildVocabulary() {
        return new Vocabulary(verbs, nouns);
    }

}
