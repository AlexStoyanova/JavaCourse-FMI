package bg.sofia.uni.fmi.mjt.spellchecker;

import java.io.Reader;
import java.io.Writer;
import java.util.List;

public interface SpellChecker {

    void analyze(Reader textReader, Writer output, int suggestionsCount);

    Metadata metadata(Reader textReader);

    List<String> findClosestWords(String word, int n);
}
