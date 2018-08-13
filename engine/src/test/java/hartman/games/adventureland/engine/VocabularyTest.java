package hartman.games.adventureland.engine;

import org.junit.Test;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class VocabularyTest {

    @Test
    public void findShouldReturnVerbGivenVerbExists() {
        Verb verb = new Verb("suck", "s");

        Set<Verb> verbs = new HashSet<>();
        verbs.add(verb);

        Vocabulary vocabulary = new Vocabulary(verbs, emptySet());

        assertTrue(vocabulary.findMatch(new Verb("suck")).get().matches(verb));
        assertTrue(vocabulary.findMatch(new Verb("s")).get().matches(verb));
    }

    @Test
    public void findShouldReturnNounGivenNounExists() {
        Noun noun = new Noun("mouse", "m");

        Set<Noun> nouns = new HashSet<>();
        nouns.add(noun);

        Vocabulary vocabulary = new Vocabulary(emptySet(), nouns);

        assertTrue(vocabulary.findMatch(new Noun("mouse")).get().matches(noun));
        assertTrue(vocabulary.findMatch(new Noun("m")).get().matches(noun));
    }

    @Test
    public void mergeShouldCombineNounsAndVerbsOfAllVocabulariesIntoASingleVocabulary() {

        Vocabulary vocab1 = new Vocabulary(singleton(new Verb("v1")), singleton(new Noun("n1")));
        Vocabulary vocab2 = new Vocabulary(singleton(new Verb("v2")), singleton(new Noun("n2")));
        Vocabulary vocab3 = new Vocabulary(singleton(new Verb("v1")), singleton(new Noun("n2")));

        Vocabulary vocab4 = vocab1.merge(vocab2).merge(vocab3);

        assertTrue(vocab4.findMatch(new Verb("v1")).isPresent());
        assertTrue(vocab4.findMatch(new Verb("v2")).isPresent());
        assertTrue(vocab4.findMatch(new Noun("n1")).isPresent());
        assertTrue(vocab4.findMatch(new Noun("n2")).isPresent());
    }

    @Test
    public void vocabularyShouldNotContainAnyNoneOrUnrecognized() {
        Vocabulary vocabulary = new Vocabulary(new LinkedHashSet<>(asList(new Verb("collect"), Verb.NONE, Verb.ANY, Verb.UNRECOGNIZED)), new LinkedHashSet<>(asList(new Noun("sandal"), Noun.ANY, Noun.NONE, Noun.UNRECOGNIZED)));
        assertFalse(vocabulary.findMatch(Verb.NONE).isPresent());
        assertTrue("Any matches Any so this is always true", vocabulary.findMatch(Verb.ANY).isPresent());
        assertFalse(vocabulary.findMatch(Verb.UNRECOGNIZED).isPresent());
        assertTrue(vocabulary.findMatch(new Verb("collect")).isPresent());
        assertFalse(vocabulary.findMatch(Noun.NONE).isPresent());
        assertTrue("Any matches Any so this is always true", vocabulary.findMatch(Noun.ANY).isPresent());
        assertFalse(vocabulary.findMatch(Noun.UNRECOGNIZED).isPresent());
        assertTrue(vocabulary.findMatch(new Noun("sandal")).isPresent());
    }
}
