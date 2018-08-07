package hartman.games.adventureland.engine;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class VocabularyTest {

    @Test
    public void toVerbShouldReturnVerbGivenVerbExists() {
        Verb verb = new Verb("suck");

        Set<Verb> verbs = new HashSet<>();
        verbs.add(verb);

        Vocabulary vocabulary = new Vocabulary(verbs, Collections.emptySet());

        Assert.assertEquals(verb, vocabulary.toVerb("suck").get());
    }

    @Test
    public void toNounShouldReturnNounGivenNounExists() {
        Noun noun = new Noun("mouse");

        Set<Noun> nouns = new HashSet<>();
        nouns.add(noun);

        Vocabulary vocabulary = new Vocabulary(Collections.emptySet(), nouns);

        Assert.assertEquals(noun, vocabulary.toNoun("mouse").get());
    }
}
