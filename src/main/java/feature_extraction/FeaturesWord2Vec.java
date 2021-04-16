package feature_extraction;

import machinelearning.utils.Cleanup;
import machinelearning.utils.PropertySettings;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import similarity.Word2VecSimilarity;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class FeaturesWord2Vec {

    private static Logger log = LoggerFactory.getLogger(FeaturesWord2Vec.class);

    public static String dataLocalPath;

    private static StringBuilder builder;

    public static void main(String[] args) throws Exception {
        String file = "/Users/anja/Desktop/master/api/files/sources/cve.csv";

        createWord2VecModel(file,"./files/cve_word2vec_model.txt", 1, 100, 100, 42, 5);

        //saveWordsToFile("/Users/anja/Desktop/master/api/files/features/cve_word2vec_model.txt", "/Users/anja/Desktop/master/api/files/features/cveFeaturesWord2Vec.txt", 200);
    }

    public static void createWord2VecModel(String file, String modelFile, int minWordFrequency, int iterations, int layerSize, int seed, int windowSize) throws FileNotFoundException {
        createCleanedTextFile(file, "./files/sentences_cleaned.txt");
        String filePath = new File(dataLocalPath,"./files/sentences_cleaned.txt").getAbsolutePath();

        log.info("Load & Vectorize Sentences....");
        // Strip white space before and after for each line
        SentenceIterator iter = new BasicLineIterator(filePath);
        // Split on white spaces in the line to get words
        TokenizerFactory t = new DefaultTokenizerFactory();

        t.setTokenPreProcessor(new CommonPreprocessor());

        log.info("Building model....");
        Word2Vec vec = new Word2Vec.Builder()
                .minWordFrequency(minWordFrequency)
                .iterations(iterations)
                .layerSize(layerSize)
                .seed(seed)
                .windowSize(windowSize)
                .iterate(iter)
                .tokenizerFactory(t)
                .build();

        log.info("Fitting Word2Vec model....");
        vec.fit();

        log.info("Writing word vectors to text file....");

        // saving the model
        WordVectorSerializer.writeWord2VecModel(vec, modelFile);
    }

    public static void createCleanedTextFile(String infile, String outfile) {

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(outfile))){

            try(BufferedReader br = new BufferedReader(new FileReader(infile))){
                String line = "";
                while((line=br.readLine())!=null) {
                    String[] cols = line.split(PropertySettings.SEPARATOR);
                    String cleaned = new Cleanup().cleanText(cols[1]);
                    bw.write(cleaned+"\n");
                }

            }catch(Exception e) {
                e.printStackTrace();
            }

        }catch(Exception e) {
            //
        }
    }

    public static void saveWordsToFile(String word2Vec, String newFile, int numWords) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Word2VecSimilarity w = new Word2VecSimilarity();
        Word2Vec model = w.getWord2Vec(word2Vec);

        List<String> stopwords = Arrays.asList("i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your", "yours", "yourself", "yourselves", "he", "him", "his", "himself", "she", "her", "hers", "herself", "it", "its", "itself", "they", "them", "their", "theirs", "themselves", "what", "which", "who", "whom", "this", "that", "these", "those", "am", "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "having", "do", "does", "did", "doing", "a", "an", "the", "and", "but", "if", "or", "because", "as", "until", "while", "of", "at", "by", "for", "with", "about", "against", "between", "into", "through", "during", "before", "after", "above", "below", "to", "from", "up", "down", "in", "out", "on", "off", "over", "under", "again", "further", "then", "once", "here", "there", "when", "where", "why", "how", "all", "any", "both", "each", "few", "more", "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than", "too", "very", "s", "t", "can", "will", "just", "don", "should", "now");

        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new File(newFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        builder = new StringBuilder();

        for(int i = 0; i < numWords; i++) {
            String word = model.vocab().wordAtIndex(i);
            if(!stopwords.contains(word) && word.length() > 1) {
                builder.append(word + "\n");
            }
        }

        pw.write(builder.toString());
        pw.close();
    }
}
