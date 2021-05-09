package similarity;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class UsingSimilarity {

    public static void main(String[] args) throws Exception {

        Similarity sim = new Similarity.Builder()
                .benchmarkDataset("files/sources/capec.csv")
                .source("CAPEC")
                //.features("files/features/CAPECFeaturesTFIDF100.txt")
                .file("files/SO_Q_SR.csv")
                .word2vec("files/features/capec_word2vec_model.txt")
                .descriptionIndex(1)
                .numFeatures(100)
                .numSimilarSources(3)
                .threshold(0.4)
                .method(Similarity.Word2Vec)
                //.getOnlyMostSimilar(true)
                .build();

        sim.run();
    }
}
