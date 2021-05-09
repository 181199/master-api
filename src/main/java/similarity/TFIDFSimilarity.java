package similarity;

import machinelearning.utility.Cleanup;
import machinelearning.utility.PropertySettings;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TFIDFSimilarity {

    private static StringBuilder builder;

    // Method to read files and store in array.

    /**
     * Method to read files and store in array
     *
     * @param filePath
     * @return document array from file
     */
    public List<String[]> getDocsArrayFromCsv(String filePath) throws IOException {

        List<String[]> docsArray = new ArrayList<String[]>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line = "";
            int i = 0;
            while ((line = br.readLine()) != null) {
                String[] cols = line.split(PropertySettings.SEPARATOR);
                String cleaned = new Cleanup().cleanText(cols[1]);

                if (PropertySettings.HEADER && i == 0) {
                    i++;
                    continue;
                }
                String[] tokenizedTerms = cleaned.replaceAll("[\\W&&[^\\s]]", "").split("\\W+");
                docsArray.add(tokenizedTerms);
                i++;
            }
        }
        return docsArray;
    }

    /**
     * prints terms from file
     */
    public List<String> getTermsFromFile(String filePath) throws IOException {

        List<String> allTerms = new ArrayList<String>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line = "";
            while ((line = br.readLine()) != null) {
                allTerms.add(line);
            }
        }
        return allTerms;
    }

    public void printTerms(List<String> terms) {
        for (int i = 0; i < terms.size(); i++) {
            System.out.println(terms.get(i));
        }
    }

    /**
     * Method to create termVector according to its tfidf score.
     *
     * @param docsArray          document array of dataset
     * @param benchmarkDocsArray document array of benchmark dataset
     * @param allTerms           all terms in document
     * @return tfidf document vector
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
                if (Double.isInfinite(idf)) {
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

    /**
     * Method to create termVector according to its tfidf score and print it to file
     *
     * @param docsArray          document array of dataset
     * @param benchmarkDocsArray document array of benchmark dataset
     * @param allTerms           all terms in document
     * @return tfidf document vector to file
     */
    public void tfIdfCalculatorToFile(String newFile, List<String[]> docsArray, List<String[]> benchmarkDocsArray, List<String> allTerms) {

        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new File(newFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        builder = new StringBuilder();

        double tf; //term frequency
        double idf; //inverse document frequency
        double tfidf; //term frequency inverse document frequency
        for (String[] docTermsArray : docsArray) {
            double[] tfidfvectors = new double[allTerms.size()];
            for (String terms : allTerms) {
                //System.out.println(terms);
                tf = new TFIDFCalculator().tf(docTermsArray, terms);
                idf = new TFIDFCalculator().idf(benchmarkDocsArray, terms);
                if (Double.isInfinite(idf)) {
                    idf = 0.0;
                }
                tfidf = tf * idf;
                builder.append(tfidf + " ");
            }
            builder.append("\n");
        }
        pw.write(builder.toString());
        pw.close();
    }

    /**
     * Method to create termVector according to its ntfidf score.
     *
     * @param docsArray          document array of dataset
     * @param benchmarkDocsArray document array of benchmark dataset
     * @param allTerms           all terms in document
     * @return ntfidf document vector
     */
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
                if (Double.isInfinite(idf)) {
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

    /**
     * prints document vectors
     */
    public void printDocumentVectors(String document, List<String> allTerms, List<String[]> docsArray) {
        double tf; //term frequency
        double idf; //inverse document frequency
        double tfidf; //term frequency inverse document frequency

        String[] s = document.split("\\W+"); // split on whitespace

        for (String terms : allTerms) {
            tf = new TFIDFCalculator().tf(s, terms);
            idf = new TFIDFCalculator().idf(docsArray, terms);
            if (Double.isInfinite(idf)) {
                idf = 0.0;
            }
            tfidf = tf * idf;
            System.out.println(terms + ": " + tfidf);
        }
    }

    /**
     * @return document vectors
     */
    public double[] getDocumentVectors(String document, List<String> allTerms, List<String[]> docsArray) {
        double tf; //term frequency
        double idf; //inverse document frequency
        double tfidf; //term frequency inverse document frequency

        String[] s = document.split("\\W+"); // split on whitespace
        double[] tfidfvectors = new double[allTerms.size()];

        int count = 0;
        for (String terms : allTerms) {
            tf = new TFIDFCalculator().tf(s, terms);
            idf = new TFIDFCalculator().idf(docsArray, terms);
            if (Double.isInfinite(idf)) {
                idf = 0.0;
            }
            tfidf = tf * idf;
            tfidfvectors[count] = tfidf;
            count++;
        }
        return tfidfvectors;
    }
}
