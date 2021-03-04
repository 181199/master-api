package similarity;

import org.deeplearning4j.models.word2vec.Word2Vec;
import org.nd4j.linalg.api.ndarray.INDArray;
import sources.StackExchangeAPI;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Main {

    public static void main(String args[]) throws FileNotFoundException, IOException, IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InterruptedException {
        SimilarityScore s = new SimilarityScore();
        TFIDFSimilarity d = new TFIDFSimilarity();
        StackExchangeAPI sapi = new StackExchangeAPI();
        String dataset = "/Users/anja/Desktop/master/api/files/sources/";
        String terms = "/Users/anja/Desktop/master/api/files/features/";
        String word2vec = "/Users/anja/Desktop/master/api/files/features/";
        String tags = "security";
        String site = "stackoverflow";
        int num_features = 100;

        List<String[]> benchmarkDocsArray = d.getDocsArrayFromCsv(dataset + "cve.csv");
        List<String> features = d.getTermsFromFile(terms + "CVEFeaturesTFIDF500.txt", num_features);

        //d.tfIdfCalculatorToFile("./files/features/cve_tfidf_vectors.txt", benchmarkDocsArray, benchmarkDocsArray, features);

        List<double[]> tfidfDocsVector = d.getTFIDFVectorsFromFile("./files/features/cve_tfidf_vectors.txt", num_features);

        sapi.allMethods("./files/experiments/tfidf/SO_CVE/SO_QA_CVE_TFIDF_SR_" + num_features + ".csv","./files/experiments/word2vec/SO_CVE/SO_QA_CVE_Word2Vec_SR_" + num_features + ".csv", "./files/experiments/tfidfword2vec/SO_CVE/SO_QA_CVE_TFIDFWord2Vec_SR_" + num_features + ".csv", dataset + "cve.csv", terms + "CVEFeaturesTFIDF500.txt", "./files/features/cve_tfidf_vectors.txt",word2vec + "cve_word2vec_model.txt", site, tags, 0.7, 1000, num_features, true, true);

    }
}
