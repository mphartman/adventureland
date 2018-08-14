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
        Word verb = new Word("suck", "s");

        Set<Word> verbs = new HashSet<>();
        verbs.add(verb);

        Vocabulary vocabulary = new Vocabulary(verbs, emptySet());

        assertTrue(vocabulary.findMatchingVerb(new Word("suck")).get().matches(verb));
        assertTrue(vocabulary.findMatchingVerb(new Word("s")).get().matches(verb));
    }

    @Test
    public void findShouldReturnNounGivenNounExists() {
        Word word = new Word("mouse", "m");

        Set<Word> words = new HashSet<>();
        words.add(word);

        Vocabulary vocabulary = new Vocabulary(emptySet(), words);

        assertTrue(vocabulary.findMatchingNoun(new Word("mouse")).get().matches(word));
        assertTrue(vocabulary.findMatchingNoun(new Word("m")).get().matches(word));
    }

    @Test
    public void mergeShouldCombineNounsAndVerbsOfAllVocabulariesIntoASingleVocabulary() {

        Vocabulary vocab1 = new Vocabulary(singleton(new Word("v1")), singleton(new Word("n1")));
        Vocabulary vocab2 = new Vocabulary(singleton(new Word("v2")), singleton(new Word("n2")));
        Vocabulary vocab3 = new Vocabulary(singleton(new Word("v1")), singleton(new Word("n2")));

        Vocabulary vocab4 = vocab1.merge(vocab2).merge(vocab3);

        assertTrue(vocab4.findMatchingVerb(new Word("v1")).isPresent());
        assertTrue(vocab4.findMatchingVerb(new Word("v2")).isPresent());
        assertTrue(vocab4.findMatchingNoun(new Word("n1")).isPresent());
        assertTrue(vocab4.findMatchingNoun(new Word("n2")).isPresent());
    }

    @Test
    public void vocabularyShouldNotContainAnyNoneOrUnrecognized() {
        Vocabulary vocabulary = new Vocabulary(
                new LinkedHashSet<>(asList(new Word("collect"), Word.NONE, Word.ANY, Word.UNRECOGNIZED)),
                new LinkedHashSet<>(asList(new Word("sandal"), Word.ANY, Word.NONE, Word.UNRECOGNIZED)));

        assertFalse(vocabulary.findMatchingVerb(Word.NONE).isPresent());
        assertTrue("Any matches Any so this is always true", vocabulary.findMatchingVerb(Word.ANY).isPresent());
        assertFalse(vocabulary.findMatchingVerb(Word.UNRECOGNIZED).isPresent());
        assertTrue(vocabulary.findMatchingVerb(new Word("collect")).isPresent());

        assertFalse(vocabulary.findMatchingNoun(Word.NONE).isPresent());
        assertTrue("Any matches Any so this is always true", vocabulary.findMatchingNoun(Word.ANY).isPresent());
        assertFalse(vocabulary.findMatchingNoun(Word.UNRECOGNIZED).isPresent());
        assertTrue(vocabulary.findMatchingNoun(new Word("sandal")).isPresent());
    }
}
