package similarity;

import java.util.List;

public class TFIDFCalculator {

    /**
     * calculates term frequency
     * @param doc array of strings represents the dataset
     * @param term String represents a term
     * @return term frequency of term in documents
     */
    public static double tf(String[] doc, String term) {
        double result = 0;
        for (String word : doc) {
            if (term.equalsIgnoreCase(word))
                result++;
        }
        return result / doc.length;
    }

    /**
     * calculates boolean term frequency
     * @param doc array of strings represents the dataset
     * @param term String represents a term
     * @return boolean term frequency of term in documents
     */
    public static double booleantf(String[] doc, String term) {
        for (String word : doc) {
            if (term.equalsIgnoreCase(word))
                return 1;
        }
        return 0;
    }

    /**
     * calculates normalized term frequency
     * @param doc array of strings represents the dataset
     * @param term String represents a term
     * @return normalized term frequency of term in documents
     */
    public static double ntf(String[] doc, String term) {
        return (0.5 + ((0.5*tf(doc, term))/maxtf(doc, term)));
    }

    /**
     * calculates max term frequency
     * @param doc array of strings represents the dataset
     * @param term String represents a term
     * @return max term frequency of term in documents
     */
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
     * calculates tf-idf
     * @param doc array of strings represents the dataset
     * @param docs list of list of strings represents the dataset
     * @param term String represents a term
     * @return tf-idf
     */
    /**
     * @param doc  a text document
     * @param docs all documents
     * @param term term
     * @return the TF-IDF of term
     */
    public double tfIdf(String[] doc, List<String[]> docs, String term) {
        return tf(doc, term) * idf(docs, term);
    }

    /**
     * calculates normalized tf-idf
     * @param doc array of strings represents the dataset
     * @param docs list of list of strings represents the dataset
     * @param term String represents a term
     * @return normalized tf-idf
     */
    public double ntfIdf(String[] doc, List<String[]> docs, String term) {
        return ntf(doc, term) * idf(docs, term);
    }

}
