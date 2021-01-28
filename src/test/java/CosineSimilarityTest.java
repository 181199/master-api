import org.junit.jupiter.api.Test;
import similarity.Documents;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class CosineSimilarityTest {

    String path = "/Users/anja/Desktop/master/api/files/junit/";
    String path1 = path + "doc1.csv";
    String path2 = path + "doc2.csv";
    Documents d;

    // delta is set because the amount of decimals is not the same
    double delta = 0.00000001;

    @Test
    public void TestTFIDFCosine() throws IOException {
        d = new Documents();

        List<String[]> cveDocsArray = d.getDocsArrayFromCsv(path1);
        List<String[]> bugsDocsArray = d.getDocsArrayFromCsv(path2);

        List<String> terms = d.getTermsFromFile(path + "features.txt");

        List<double[]> tfidfDocsVectorCve = d.tfIdfCalculator(cveDocsArray, cveDocsArray, terms); //calculates tfidf
        List<double[]> tfidfDocsVectorBugs = d.tfIdfCalculator(bugsDocsArray, cveDocsArray, terms);

        assertEquals(0.0, tfidfDocsVectorCve.get(0)[0], delta);
        assertEquals(0.09902102579, tfidfDocsVectorCve.get(0)[1], delta);
        assertEquals(0.0, tfidfDocsVectorCve.get(0)[2], delta);

        assertEquals(0.06931471805, tfidfDocsVectorCve.get(1)[0], delta);
        assertEquals(0.0, tfidfDocsVectorCve.get(1)[1], delta);
        assertEquals(0.06931471805, tfidfDocsVectorCve.get(1)[2], delta);

        assertEquals(0.0, tfidfDocsVectorBugs.get(0)[0], delta);
        assertEquals(0.0, tfidfDocsVectorBugs.get(0)[1], delta);
        assertEquals(0.06931471805, tfidfDocsVectorBugs.get(0)[2], delta);

        assertEquals(0.06301338005, tfidfDocsVectorBugs.get(1)[0], delta);
        assertEquals(0.06301338005, tfidfDocsVectorBugs.get(1)[1], delta);
        assertEquals(0.0, tfidfDocsVectorBugs.get(1)[2], delta);

        assertEquals(0.0, d.getCosineSimilarityTwoDocuments(tfidfDocsVectorCve.get(0), tfidfDocsVectorBugs.get(0)), delta);
        assertEquals(0.707106781, d.getCosineSimilarityTwoDocuments(tfidfDocsVectorCve.get(0), tfidfDocsVectorBugs.get(1)), delta);
        assertEquals(0.707106781, d.getCosineSimilarityTwoDocuments(tfidfDocsVectorCve.get(1), tfidfDocsVectorBugs.get(0)), delta);
        assertEquals(0.5, d.getCosineSimilarityTwoDocuments(tfidfDocsVectorCve.get(1), tfidfDocsVectorBugs.get(1)), delta);
    }
}
