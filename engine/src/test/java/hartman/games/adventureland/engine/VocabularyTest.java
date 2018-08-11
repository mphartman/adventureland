package hartman.games.adventureland.engine;

import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;

public class VocabularyTest {

    @Test
    public void findShouldReturnVerbGivenVerbExists() {
        Verb verb = new Verb("suck", "s");

        Set<Verb> verbs = new HashSet<>();
        verbs.add(verb);

        Vocabulary vocabulary = new Vocabulary(verbs, Collections.emptySet());

        assertTrue(vocabulary.find(new Verb("suck")).get().matches(verb));
        assertTrue(vocabulary.find(new Verb("s")).get().matches(verb));
    }

    @Test
    public void anyMatchShouldReturnNounGivenNounExists() {
        Noun noun = new Noun("mouse", "m");

        Set<Noun> nouns = new HashSet<>();
        nouns.add(noun);

        Vocabulary vocabulary = new Vocabulary(Collections.emptySet(), nouns);

        assertTrue(vocabulary.find(new Noun("mouse")).get().matches(noun));
        assertTrue(vocabulary.find(new Noun("m")).get().matches(noun));
    }

    @Test
    public void mergeShouldCombineNounsAndVerbsOfAllVocabulariesIntoASingleVocabulary() {

        Vocabulary vocab1 = new Vocabulary(new HashSet<>(asList(new Verb("v1"))), new HashSet<>(asList(new Noun("n1"))));
        Vocabulary vocab2 = new Vocabulary(new HashSet<>(asList(new Verb("v2"))), new HashSet<>(asList(new Noun("n2"))));
        Vocabulary vocab3 = new Vocabulary(new HashSet<>(asList(new Verb("v1"))), new HashSet<>(asList(new Noun("n2"))));

        Vocabulary vocab4 = vocab1.merge(vocab2).merge(vocab3);

        assertTrue(vocab4.find(new Verb("v1")).isPresent());
        assertTrue(vocab4.find(new Verb("v2")).isPresent());
        assertTrue(vocab4.find(new Noun("n1")).isPresent());
        assertTrue(vocab4.find(new Noun("n2")).isPresent());
    }
}
