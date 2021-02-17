package similarity;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class Documents {

    // Method to read files and store in array.

    public List<String[]> getDocsArrayFromCsv(String filePath) throws IOException {

        List<String[]> cveDocsArray = new ArrayList<String[]>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line = "";
            int i = 0;
            while ((line = br.readLine()) != null) {
                String[] cols = line.split(";");
                String cleaned = cleanText(cols[1]);

                if(i != 0) {
                    String[] tokenizedTerms = cleaned.replaceAll("[\\W&&[^\\s]]", "").split("\\W+");
                    cveDocsArray.add(tokenizedTerms);
                }
                i++;
            }
        }
        return cveDocsArray;
    }

    public List<String> getTermsFromFile(String filePath) throws IOException {

        List<String> allTerms = new ArrayList<String>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line = "";
            while ((line = br.readLine()) != null) {
                allTerms.add(line);
                //System.out.println(line);
            }
        }
        return allTerms;
    }

    public void printTerms(List<String> terms){
        for(int i = 0; i < terms.size(); i++){
            System.out.println(terms.get(i));
        }
    }

    /**
     * Method to create termVector according to its tfidf score.
     */
    public List<double[]> tfIdfCalculator(List<String[]> docsArray, List<String[]> benchmarkDocsArray, List<String> allTerms) {

        List<double[]> tfidfDocsVector = new ArrayList<>();
        double tf; //term frequency
        double idf; //inverse document frequency
        double tfidf; //term frequency inverse document frequency
        for (String[] docTermsArray : docsArray) {
            double[] tfidfvectors = new double[allTerms.size()];
            int count = 0;
            for (String terms : allTerms) {
                //System.out.println(terms);
                tf = new TFIDFCalculator().tf(docTermsArray, terms);
                idf = new TFIDFCalculator().idf(benchmarkDocsArray, terms);
                if(Double.isInfinite(idf)){
                    idf = 0.0;
                }
                tfidf = tf * idf;
                tfidfvectors[count] = tfidf;
                count++;
            }
            tfidfDocsVector.add(tfidfvectors);  //storing document vectors;
        }
        return tfidfDocsVector;
    }

    public List<double[]> ntfIdfCalculator(List<String[]> docsArray, List<String[]> benchmarkDocsArray, List<String> allTerms) {

        List<double[]> ntfidfDocsVector = new ArrayList<>();
        double ntf; //term frequency
        double idf; //inverse document frequency
        double ntfidf; //term frequency inverse document frequency
        for (String[] docTermsArray : docsArray) {
            double[] ntfidfvectors = new double[allTerms.size()];
            int count = 0;
            for (String terms : allTerms) {
                //System.out.println(terms);
                ntf = new TFIDFCalculator().ntf(docTermsArray, terms);
                idf = new TFIDFCalculator().idf(benchmarkDocsArray, terms);
                if(Double.isInfinite(idf)){
                    idf = 0.0;
                }
                ntfidf = ntf * idf;
                ntfidfvectors[count] = ntfidf;
                count++;
            }
            ntfidfDocsVector.add(ntfidfvectors);  //storing document vectors;
        }
        return ntfidfDocsVector;
    }

    public void printDocumentVectors(String document, List<String> allTerms, List<String[]> docsArray){
        double tf; //term frequency
        double idf; //inverse document frequency
        double tfidf; //term frequency inverse document frequency

        String[] s = document.split("\\W+"); // split on whitespace

            for (String terms : allTerms) {
                tf = new TFIDFCalculator().tf(s, terms);
                idf = new TFIDFCalculator().idf(docsArray, terms);
                if(Double.isInfinite(idf)){
                    idf = 0.0;
                }
                tfidf = tf * idf;
                System.out.println(terms + ": " + tfidf);
            }
    }

    public double[] getDocumentVectors(String document, List<String> allTerms, List<String[]> docsArray){
        double tf; //term frequency
        double idf; //inverse document frequency
        double tfidf; //term frequency inverse document frequency

        String[] s = document.split("\\W+"); // split on whitespace
        double[] tfidfvectors = new double[allTerms.size()];

        int count = 0;
        for (String terms : allTerms) {
            tf = new TFIDFCalculator().tf(s, terms);
            idf = new TFIDFCalculator().idf(docsArray, terms);
            if(Double.isInfinite(idf)){
                idf = 0.0;
            }
            tfidf = tf * idf;
            tfidfvectors[count] = tfidf;
            count++;
        }
        return tfidfvectors;
    }

    public double getCosineSimilarityTwoDocuments(double[] document1, double[] document2){
        return new CosineSimilarity().cosineSimilarity(document1, document2);
    }

    // Method to calculate cosine similarity between all the documents.
    public void getCosineSimilarity(List<double[]> tfidfDocsVector1, List<double[]> tfidfDocsVector2) throws IOException {
        List<Double> scores = new ArrayList<Double>();
        double score = 0.0;
        double cosine = 0.0;
        for (int i = 0; i < tfidfDocsVector1.size(); i++) {
            for (int j = 0; j < tfidfDocsVector2.size(); j++) {
                cosine = new CosineSimilarity().cosineSimilarity(tfidfDocsVector1.get(i), tfidfDocsVector2.get(j));
                    //System.out.println("between " + i + " and " + j + "  =  " + new CosineSimilarity().cosineSimilarity(tfidfDocsVectorBugs.get(i), tfidfDocsVectorCve.get(j)));

                // use the highest score for each bug report
                if (cosine > score) {
                    score = cosine;
                }
            }
            scores.add(score);
            System.out.println(score);
            score = 0.0;
        }
        appendToCsv(scores, "tfidf");
    }

    public void appendToCsv(List<Double> tfidfScores, String columnName) throws IOException {

        BufferedReader br = null;
        BufferedWriter bw = null;

        try {
            File file = new File("/Users/anja/Desktop/master/api/files/testing/stackoverflowSR_small.csv");
            File file2 = new File("/Users/anja/Desktop/master/api/files/testing/stackoverflowSR_small_" + columnName + ".csv");//so the
            //names don't conflict or just use different folders

            br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file2)));
            String line = "";

            int i = 0;
            while ((line = br.readLine()) != null && i < tfidfScores.size()) {
                if(i == 0){
                    bw.write(line + ";" + columnName + "\n");
                } else {
                    String addedColumn = String.valueOf(tfidfScores.get(i));
                    bw.write(line + addedColumn + "\n");
                }
                i++;
            }
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (br != null)
                br.close();
            if (bw != null)
                bw.close();
        }
    }

    public static String cleanText(String text) {
        Pattern charsPunctuationPattern = Pattern.compile("[\\d:,\"\'\\`\\_\\|?!\n\r@;]+");
        String input_text = charsPunctuationPattern.matcher(text.trim().toLowerCase()).replaceAll("");
        input_text = input_text.replaceAll("\\{.*?\\}", "");
        input_text = input_text.replaceAll("\\[.*?\\]", "");
        input_text = input_text.replaceAll("\\(.*?\\)", "");
        input_text = input_text.replaceAll("[^A-Za-z0-9(),!?@\'\\`\"\\_\n]", " ");
        input_text = input_text.replaceAll("[/]", " ");

        return input_text;
    }
}
