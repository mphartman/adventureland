package hartman.games.adventureland.engine;

import org.junit.Test;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class VocabularyTest {

    @Test
    public void findShouldReturnWordGivenWordExists() {
        Word verb = new Word("suck", "s");

        Set<Word> verbs = new HashSet<>();
        verbs.add(verb);

        Vocabulary vocabulary = new Vocabulary(verbs);

        assertTrue(vocabulary.findMatch(new Word("suck")).get().matches(verb));
        assertTrue(vocabulary.findMatch(new Word("s")).get().matches(verb));
    }

    @Test
    public void mergeShouldCombineWordsOfAllVocabulariesIntoASingleVocabulary() {

        Vocabulary vocab1 = new Vocabulary(singleton(new Word("v1")));
        Vocabulary vocab2 = new Vocabulary(singleton(new Word("v2")));
        Vocabulary vocab3 = new Vocabulary(singleton(new Word("v1")));

        Vocabulary vocab4 = vocab1.merge(vocab2).merge(vocab3);

        assertTrue(vocab4.findMatch(new Word("v1")).isPresent());
        assertTrue(vocab4.findMatch(new Word("v2")).isPresent());
    }

    @Test
    public void vocabularyShouldNotContainNullOrNoneOrUnrecognized() {
        Vocabulary vocabulary = new Vocabulary(new LinkedHashSet<>(asList(null, new Word("collect"), Word.NONE, Word.ANY, Word.unrecognized())));

        assertFalse(vocabulary.findMatch(Word.NONE).isPresent());
        assertTrue("Any matches Any so this is always true", vocabulary.findMatch(Word.ANY).isPresent());
        assertFalse(vocabulary.findMatch(Word.unrecognized()).isPresent());
        assertTrue(vocabulary.findMatch(new Word("collect")).isPresent());
    }
}
