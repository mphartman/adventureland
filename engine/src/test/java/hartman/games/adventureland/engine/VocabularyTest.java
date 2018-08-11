package hartman.games.adventureland.engine;

import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class VocabularyTest {

    @Test
    public void toVerbShouldReturnVerbGivenVerbExists() {
        Verb verb = new Verb("suck");

        Set<Verb> verbs = new HashSet<>();
        verbs.add(verb);

        Vocabulary vocabulary = new Vocabulary(verbs, Collections.emptySet());

        assertEquals(verb, vocabulary.findVerb("suck").get());
    }

    @Test
    public void toNounShouldReturnNounGivenNounExists() {
        Noun noun = new Noun("mouse");

        Set<Noun> nouns = new HashSet<>();
        nouns.add(noun);

        Vocabulary vocabulary = new Vocabulary(Collections.emptySet(), nouns);

        assertEquals(noun, vocabulary.findNoun("mouse").get());
    }

    @Test
    public void mergeShouldCombineNounsAndVerbsOfAllVocabulariesIntoASingleVocabulary() {

        Vocabulary vocab1 = new Vocabulary(new HashSet<>(asList(new Verb("v1"))), new HashSet<>(asList(new Noun("n1"))));
        Vocabulary vocab2 = new Vocabulary(new HashSet<>(asList(new Verb("v2"))), new HashSet<>(asList(new Noun("n2"))));
        Vocabulary vocab3 = new Vocabulary(new HashSet<>(asList(new Verb("v1"))), new HashSet<>(asList(new Noun("n2"))));

        Vocabulary vocab4 = Vocabulary.merge(vocab1, vocab2, vocab3);

        assertEquals(new Verb("v1"), vocab4.findVerb("v1").get());
        assertEquals(new Verb("v2"), vocab4.findVerb("v2").get());
        assertEquals(new Noun("n1"), vocab4.findNoun("n1").get());
        assertEquals(new Noun("n2"), vocab4.findNoun("n2").get());

        vocab4 = vocab1.merge(vocab2).merge(vocab3);

        assertEquals(new Verb("v1"), vocab4.findVerb("v1").get());
        assertEquals(new Verb("v2"), vocab4.findVerb("v2").get());
        assertEquals(new Noun("n1"), vocab4.findNoun("n1").get());
        assertEquals(new Noun("n2"), vocab4.findNoun("n2").get());
    }
}
