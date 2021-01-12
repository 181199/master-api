package similarity;

import java.io.FileNotFoundException;
import java.io.IOException;

public class SimilarityCalculator {

    public static void main(String args[]) throws FileNotFoundException, IOException, IOException {
        Documents dp = new Documents();
        dp.parseCveFile("/Users/anja/Desktop/master/api/files/test/cveData.csv");
        dp.parseBugFile("/Users/anja/Desktop/master/api/files/test/stackoverflowSBR_small.csv");
        dp.tfIdfCalculatorCve(); //calculates tfidf
        dp.tfIdfCalculatorBugs();
        dp.getCosineSimilarity(); //calculates cosine similarity
    }
}
