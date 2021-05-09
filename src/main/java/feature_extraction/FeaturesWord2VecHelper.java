package feature_extraction;

import org.deeplearning4j.models.word2vec.Word2Vec;
import similarity.Word2VecSimilarity;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

public class FeaturesWord2VecHelper {

    private static StringBuilder builder;

    FeaturesWord2Vec featuresWord2Vec;

    public FeaturesWord2VecHelper(FeaturesWord2Vec featuresWord2Vec) {
        this.featuresWord2Vec = featuresWord2Vec;
    }

    /**
     * create txt file with word2vec feature words
     */
    public void saveWordsToFile() throws Exception {

        if(featuresWord2Vec.getModelFile().isEmpty()){
            throw new Exception("Model file must be set.");
        } else if (featuresWord2Vec.getNewFeatureFile().isEmpty()){
            throw new Exception("New file name must be set.");
        }

        Word2VecSimilarity w = new Word2VecSimilarity();
        Word2Vec model = w.getWord2Vec(featuresWord2Vec.getModelFile());

        List<String> stopwords = Arrays.asList("i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your", "yours", "yourself", "yourselves", "he", "him", "his", "himself", "she", "her", "hers", "herself", "it", "its", "itself", "they", "them", "their", "theirs", "themselves", "what", "which", "who", "whom", "this", "that", "these", "those", "am", "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "having", "do", "does", "did", "doing", "a", "an", "the", "and", "but", "if", "or", "because", "as", "until", "while", "of", "at", "by", "for", "with", "about", "against", "between", "into", "through", "during", "before", "after", "above", "below", "to", "from", "up", "down", "in", "out", "on", "off", "over", "under", "again", "further", "then", "once", "here", "there", "when", "where", "why", "how", "all", "any", "both", "each", "few", "more", "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than", "too", "very", "s", "t", "can", "will", "just", "don", "should", "now");

        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new File(featuresWord2Vec.getNewFeatureFile()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        builder = new StringBuilder();

        for(int i = 0; i < featuresWord2Vec.getNumWords(); i++) {
            String word = model.vocab().wordAtIndex(i);
            if(!stopwords.contains(word) && word.length() > 1) {
                builder.append(word + "\n");
            }
        }

        pw.write(builder.toString());
        pw.close();
    }
}
