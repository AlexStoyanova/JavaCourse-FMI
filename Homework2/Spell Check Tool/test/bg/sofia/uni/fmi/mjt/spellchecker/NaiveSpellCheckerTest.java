package bg.sofia.uni.fmi.mjt.spellchecker;

import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class NaiveSpellCheckerTest {

    private static final String DICTIONARY_INPUT = """
            cat
            dog
            love
            like
            lovely
            very
            something
            hello
            smart
            """;
    private static final String STOP_WORDS_INPUT = """
            my
            i
            he
            and
            is
            """;

    private static final String TEXT_WITH_MISTAKES = """
            Hallo, I lovee my dag.
            He is vrey lovely and smrt.
            """;

    private static final String TEXT_WITHOUT_MISTAKES = """
            Hello, I love my dog.
            He is very lovely and smart.
            """;

    private static NaiveSpellChecker naiveSpellChecker;

    @Before
    public void setUp() {
        StringReader dictionary = new StringReader(DICTIONARY_INPUT);
        StringReader stopwords = new StringReader(STOP_WORDS_INPUT);
        naiveSpellChecker = new NaiveSpellChecker(dictionary, stopwords);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAnalyzeIllegalArgument() {
        StringReader textReader = new StringReader(TEXT_WITH_MISTAKES);
        StringWriter output = new StringWriter();
        naiveSpellChecker.analyze(textReader, output, -1);
    }

    @Test
    public void testAnalyzeTextWithoutMistakes() {
        StringReader textReader = new StringReader(TEXT_WITHOUT_MISTAKES);
        StringWriter output = new StringWriter();
        naiveSpellChecker.analyze(textReader, output, 2);

        final String expectedOutput = """
                Hello, I love my dog.
                He is very lovely and smart.
                = = = Metadata = = =
                40 characters, 6 words, 0 spelling issue(s) found
                = = = Findings = = ="""
                .replaceAll("\n", System.lineSeparator());


        assertEquals("Text with no mistakes.", expectedOutput, output.toString());
    }

    @Test
    public void testAnalyzeTextWithMistakes() {
        StringReader textReader = new StringReader(TEXT_WITH_MISTAKES);
        StringWriter output = new StringWriter();
        naiveSpellChecker.analyze(textReader, output, 2);

        final String expectedOutput = """
                Hallo, I lovee my dag.
                He is vrey lovely and smrt.
                = = = Metadata = = =
                40 characters, 6 words, 5 spelling issue(s) found
                = = = Findings = = =
                Line #1, {Hallo,} - Possible suggestions are {hello, love}
                Line #1, {lovee} - Possible suggestions are {love, lovely}
                Line #1, {dag.} - Possible suggestions are {love, lovely}
                Line #2, {vrey} - Possible suggestions are {love, lovely}
                Line #2, {smrt.} - Possible suggestions are {smart, love}"""
                .replaceAll("\n", System.lineSeparator());

        assertEquals("Text with mistakes.", expectedOutput, output.toString());
    }

    @Test
    public void testMetadataWithMistakesInTextCheckCharacters() {
        StringReader textReader = new StringReader(TEXT_WITH_MISTAKES);
        Metadata metadata = naiveSpellChecker.metadata(textReader);
        final int expectedCharacters = 40;
        assertEquals("Metadata characters should be 40!", expectedCharacters, metadata.characters());
    }

    @Test
    public void testMetadataWithMistakesInTextCheckWords() {
        StringReader textReader = new StringReader(TEXT_WITH_MISTAKES);
        Metadata metadata = naiveSpellChecker.metadata(textReader);
        final int expectedWords = 6;
        assertEquals("Metadata words should be 6!", expectedWords, metadata.words());
    }

    @Test
    public void testMetadataWithMistakesInTextCheckMistakes() {
        StringReader textReader = new StringReader(TEXT_WITH_MISTAKES);
        Metadata metadata = naiveSpellChecker.metadata(textReader);
        final int expectedMistakes = 5;
        assertEquals("Metadata mistakes should be 5!", expectedMistakes, metadata.mistakes());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindClosestWordsIllegalArgument() {
        final int n = -1;
        naiveSpellChecker.findClosestWords("Hello!", n);
    }

    @Test
    public void testFindClosestWordsWithMistake() {
        final int n = 1;
        List<String> words = naiveSpellChecker.findClosestWords("Helllo!", n);
        assertEquals("Should be returned one correct word.", "hello", words.get(0));
    }
}
