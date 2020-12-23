package bg.sofia.uni.fmi.mjt.spellchecker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class NaiveSpellChecker implements SpellChecker {

    private final Set<String> dictionary;
    private final Set<String> stopwords;

    private static final int N_GRAM = 2;
    private static final String ARGUMENT_CANNOT_BE_NEGATIVE_MESSAGE
            = "Provided value for the argument cannot be negative";
    private static final String COULD_NOT_LOAD_DATASET_MESSAGE = "Could not load dataset.";
    private static final String COULD_NOT_OPEN_OUTPUT_FILE_MESSAGE = "Could not open output file.";
    private static final String METADATA = "= = = Metadata = = =";
    private static final String FINDINGS = "= = = Findings = = =";

    public NaiveSpellChecker(Reader dictionaryReader, Reader stopwordsReader) {
        dictionary = new HashSet<>();
        stopwords = new HashSet<>();
        readDictionary(dictionaryReader);
        readStopwords(stopwordsReader);
    }

    @Override
    public void analyze(Reader textReader, Writer output, int suggestionsCount) {
        if (suggestionsCount < 0) {
            throw new IllegalArgumentException(ARGUMENT_CANNOT_BE_NEGATIVE_MESSAGE);
        }

        try (BufferedReader bufferedReader = new BufferedReader(textReader);
                BufferedWriter bufferedWriter = new BufferedWriter(output)) {

            List<Pair<String, Integer>> wrongWordsAtLine = new ArrayList<>();
            List<Pair<String, Integer>> wordsWithMistakesAtLine;
            int characters = 0;
            int words = 0;
            int mistakes = 0;
            int numberOfLine = 0;
            String line;
            String[] allWords;

            while ((line = bufferedReader.readLine()) != null) {
                bufferedWriter.append(line).append(System.lineSeparator());
                numberOfLine++;
                allWords = line.split("\\s+");

                characters += countCharacters(allWords);
                words += countWords(allWords);

                wordsWithMistakesAtLine = findWordsWithMistakes(allWords, numberOfLine);
                wrongWordsAtLine.addAll(wordsWithMistakesAtLine);

                mistakes += wordsWithMistakesAtLine.size();
            }
            bufferedWriter.flush();
            writeMetadataInOutput(bufferedWriter, new Metadata(characters, words, mistakes));
            writePossibleSuggestionsInOutput(bufferedWriter, wrongWordsAtLine, suggestionsCount);

        } catch (IOException e) {
            throw new IllegalArgumentException(COULD_NOT_LOAD_DATASET_MESSAGE
                    + "or" + COULD_NOT_OPEN_OUTPUT_FILE_MESSAGE, e);
        }
    }

    @Override
    public Metadata metadata(Reader textReader) {
        int characters = 0;
        int words = 0;
        int mistakes = 0;

        try (BufferedReader bufferedReader = new BufferedReader(textReader)) {
            String line;
            String[] allWords;

            while ((line = bufferedReader.readLine()) != null) {
                allWords = line.split("\\s+");
                characters += countCharacters(allWords);
                words += countWords(allWords);
                mistakes += countMistakes(allWords);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(COULD_NOT_LOAD_DATASET_MESSAGE, e);
        }
        return new Metadata(characters, words, mistakes);
    }

    @Override
    public List<String> findClosestWords(String word, int n) {
        if (n < 0) {
            throw new IllegalArgumentException(ARGUMENT_CANNOT_BE_NEGATIVE_MESSAGE);
        }
        List<String> closestWords = findAllPossibleSuggestions(changeWord(word));
        n = Math.min(n, closestWords.size());
        return closestWords.subList(0, n);
    }

    private void readDictionary(Reader dictionaryReader) {
        try (BufferedReader reader = new BufferedReader(dictionaryReader)) {
            String word;
            String changedWord;
            while ((word = reader.readLine()) != null) {
                changedWord = changeWord(word);
                if (changedWord.length() > 1) {
                    dictionary.add(changedWord);
                }
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(COULD_NOT_LOAD_DATASET_MESSAGE, e);
        }
    }

    private void readStopwords(Reader stopwordsReader) {
        try (BufferedReader reader = new BufferedReader(stopwordsReader)) {
            String word;
            String changedWord;
            while ((word = reader.readLine()) != null) {
                changedWord = changeStopwords(word);
                stopwords.add(changedWord);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(COULD_NOT_LOAD_DATASET_MESSAGE, e);
        }
    }

    private String changeWord(String word) {
        return word
                .toLowerCase()
                .replaceAll("^[^a-zA-Z0-9]*|[^a-zA-Z0-9]*$", "")
                .trim();
    }

    private String changeStopwords(String stopword) {
        return stopword
                .toLowerCase()
                .trim();
    }

    private double findSimilarityOfTwoWordsWithCosineSimilarity(String firstWord, String secondWord) {
        Map<String, Integer> vectorForFirstWord = makeVectorOfWord(firstWord);
        Map<String, Integer> vectorForSecondWord = makeVectorOfWord(secondWord);

        double lengthOfFirstVector = findLengthOfVector(vectorForFirstWord);
        double lengthOfSecondVector = findLengthOfVector(vectorForSecondWord);
        double dotProductOfVectors = findDotProductOfTwoVectors(vectorForFirstWord, vectorForSecondWord);

        return dotProductOfVectors / (lengthOfFirstVector * lengthOfSecondVector);
    }

    private Map<String, Integer> makeVectorOfWord(String word) {
        HashMap<String, Integer> vector = new HashMap<>();
        int numberOfDuplicates;
        int wordLength = word.length();
        String twoGram;
        for (int i = 0; i < wordLength - 1; ++i) {
            twoGram = word.substring(i, i + N_GRAM);
            if (vector.containsKey(twoGram)) {
                numberOfDuplicates = 1 + vector.get(twoGram);
            } else {
                numberOfDuplicates = 1;
            }
            vector.put(twoGram, numberOfDuplicates);
        }
        return vector;
    }

    private double findLengthOfVector(Map<String, Integer> vector) {
        return vector.values().stream().mapToInt(i -> i * i).sum();
    }

    private double findDotProductOfTwoVectors(Map<String, Integer> firstVector, Map<String, Integer> secondVector) {
        double result = 0;
        for (Map.Entry<String, Integer> entry : firstVector.entrySet()) {
            if (secondVector.containsKey(entry.getKey())) {
                result += entry.getValue() * secondVector.get(entry.getKey());
            }
        }
        return result;
    }

    private List<String> findAllPossibleSuggestions(String word) {
        return dictionary.stream()
                .map(w -> new Pair<>(w, findSimilarityOfTwoWordsWithCosineSimilarity(word, w)))
                .sorted(Comparator.comparing((Function<Pair<String, Double>, Double>) Pair::value).reversed())
                .map(Pair::key)
                .collect(Collectors.toList());
    }

    private int countCharacters(String[] words) {
        int sum = 0;
        for (String word : words) {
            sum += word.length();
        }
        return sum;
    }

    private long countWords(String[] words) {
        return Arrays.stream(words)
                .map(this::changeWord)
                .filter(w -> !w.isEmpty())
                .filter(w -> !stopwords.contains(w))
                .count();
    }

    private long countMistakes(String[] words) {
        return Arrays.stream(words)
                .map(this::changeWord)
                .filter(w -> !stopwords.contains(w))
                .filter(w -> !dictionary.contains(w))
                .filter(w -> w.length() > 1)
                .count();
    }

    private List<Pair<String, Integer>> findWordsWithMistakes(String[] words, int numberOfLine) {
        return Arrays.stream(words)
                .map(w -> new Pair<>(w, changeWord(w)))
                .filter(p -> !stopwords.contains(p.value()))
                .filter(p -> !dictionary.contains(p.value()))
                .filter(p -> p.value().length() > 1)
                .map(p -> new Pair<>(p.key(), numberOfLine))
                .collect(Collectors.toList());
    }

    private void writeMetadataInOutput(BufferedWriter bufferedWriter, Metadata metadata) throws IOException {
        bufferedWriter.append(METADATA).append(System.lineSeparator());
        String lineWithMetadata = String.format("%d characters, %d words, %d spelling issue(s) found",
                metadata.characters(), metadata.words(), metadata.mistakes());
        bufferedWriter.append(lineWithMetadata).append(System.lineSeparator());
        bufferedWriter.flush();
    }

    private void writePossibleSuggestionsInOutput(BufferedWriter bufferedWriter,
                                                  List<Pair<String, Integer>> wrongWordsAtLine,
                                                  int suggestionsCount) throws IOException {

        bufferedWriter.append(FINDINGS);
        List<String> allPossibleSuggestions;
        String lineWithFindings;
        int sizeOfSuggestions;

        for (Pair<String, Integer> pair : wrongWordsAtLine) {

            bufferedWriter.append(System.lineSeparator());
            lineWithFindings = String.format("Line #%d, {%s} - Possible suggestions are ", pair.value(), pair.key());
            bufferedWriter.append(lineWithFindings);
            allPossibleSuggestions = findClosestWords(pair.key(), suggestionsCount);
            sizeOfSuggestions = allPossibleSuggestions.size();
            bufferedWriter.append("{");

            if (sizeOfSuggestions != 0) {
                for (int i = 0; i < sizeOfSuggestions - 1; ++i) {
                    bufferedWriter.append(allPossibleSuggestions.get(i)).append(", ");
                }
                bufferedWriter.append(allPossibleSuggestions.get(sizeOfSuggestions - 1));
            }
            bufferedWriter.append("}");
        }
        bufferedWriter.flush();
    }
}
