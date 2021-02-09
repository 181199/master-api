package similarity;

import java.util.Arrays;
import java.util.List;

public class TFIDFCalculator {

    public static double tf(String[] doc, String term) {
        double result = 0;
        for (String word : doc) {
            if (term.equalsIgnoreCase(word))
                result++;
        }
        return result / doc.length;
    }

    public static double booleantf(String[] doc, String term) {
        for (String word : doc) {
            if (term.equalsIgnoreCase(word))
                return 1;
        }
        return 0;
    }

    public static double ntf(String[] doc, String term) {
        return (0.5 + ((0.5*tf(doc, term))/maxtf(doc, term)));
    }

    public static double maxtf(String[] doc, String term){
        double max = 0;
        double result = 0;
        for (String word : doc) {
            if (term.equalsIgnoreCase(word)) {
                result++;
            }

            if(result > max){
                max = result;
            }
        }
        return max / doc.length;
    }

    /**
     * @param docs list of list of strings represents the dataset
     * @param term String represents a term
     * @return the inverse term frequency of term in documents
     */
    public static double idf(List<String[]> docs, String term) {
        double n = 0;
        for (String[] doc : docs) {
            for (String word : doc) {
                if (term.equalsIgnoreCase(word)) {
                    n++;
                    break;
                }
            }
        }
        return Math.log(docs.size() / n);
    }

    /**
     * @param doc  a text document
     * @param docs all documents
     * @param term term
     * @return the TF-IDF of term
     */
    public double tfIdf(String[] doc, List<String[]> docs, String term) {
        return tf(doc, term) * idf(docs, term);
    }

    public double ntfIdf(String[] doc, List<String[]> docs, String term) {
        return ntf(doc, term) * idf(docs, term);
    }

}
