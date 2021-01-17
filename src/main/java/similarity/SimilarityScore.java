package similarity;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SimilarityScore {

    public static void main(String args[]) throws FileNotFoundException, IOException, IOException {
        String path = "/Users/anja/Desktop/master/api/files/test/";
//        createDatasetFromScores(path + "stackoverflowSBR_small_tfidf_word2vec.csv",
//                path + "stackoverflowSBR_new.csv", true, true);

        String benchmark = path + "cveData.csv";
        String docs = path + "stackoverflowSBR_small.csv";
        String features = "/Users/anja/Desktop/master/api/files/FeaturesTFIDF.txt";

        listMostSimilar(benchmark, docs, features);
    }

    public static void listMostSimilar(String benchmarkDataset, String file, String features) throws IOException {
        Documents d = new Documents();

        List<String> benchmarkIds = getIds(benchmarkDataset, 0);
        List<String> bugIds = getIds(file, 2);

        List<String[]> cveDocsArray = d.getDocsArrayFromCsv(benchmarkDataset);
        List<String> terms = d.getTermsFromFile(features);
        List<double[]> tfidfDocsVectorCve = d.tfIdfCalculator(cveDocsArray, cveDocsArray, terms);
        List<String> documents = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line = "";
            int i = 0;
            while ((line = br.readLine()) != null && i <= 100) {
                String[] cols = line.split(";");
                String cleaned = d.cleanText(cols[1]);
                documents.add(cleaned);
                i++;
            }

            double score = 0.0;
            double cosine = 0.0;
            int n = 0;
            for (int k = 0; k < benchmarkIds.size(); k++) {
                for (int j = 0; j < documents.size(); j++) {
                    cosine = d.getCosineSimilarityTwoDocuments((d.getDocumentVectors(documents.get(j), terms, cveDocsArray)), tfidfDocsVectorCve.get(k));

                    // use the highest score for each cve record
                    if (cosine > score) {
                        score = cosine;
                        n = j;
                    }
                }
                System.out.println(benchmarkIds.get(k) + " and SO ID " + bugIds.get(n) + ": " + score);
                score = 0.0;
            }
        }
    }

    public static List<String> getIds(String file, int column) throws IOException {
        List<String> ids = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line = "";
            int i = 0;
            while ((line = br.readLine()) != null && i <= 100) {
                String[] cols = line.split(";");
                if(i != 0) {
                    ids.add(cols[column]);
                }
                //System.out.println(cols[column]);
                i++;
            }
        }
        return ids;
    }

    public static void createDatasetFromScores(String filePath, String newFilePath, boolean tfidf, boolean word2vec) throws IOException {
        File file = new File(newFilePath);

        BufferedReader br = null;
        BufferedWriter bw = null;

        try {
            br = new BufferedReader(new FileReader(filePath));
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));

            String line = "";
            int i = 0;
            double tfidfScore = 0.0;
            double word2vecScore = 0.0;
            double average = 0.0;
            while ((line = br.readLine()) != null) {
                String[] cols = line.split(";");

                if(i == 0){
                    // add column for security report (1 = security, 0 != security)
                    bw.write("Security;" + cols[0] + ";" + cols[1] + ";" + cols[2] + ";" + cols[3] + "\n");
                } else {
                    tfidfScore = Double.parseDouble(cols[4]);
                    word2vecScore = Double.parseDouble(cols[5]);
                    average = (tfidfScore+word2vecScore)/2;
                    //System.out.println(average);
                }

                if(tfidf && !word2vec){
                    if(tfidfScore >= 0.6){
                        // add column for security report (1 = security, 0 != security)
                        bw.write("1;" + cols[0] + ";" + cols[1] + ";" + cols[2] + ";" + cols[3] + "\n");
                    }
                } else if(!tfidf && word2vec){
                    if(word2vecScore >= 0.6){
                        // add column for security report (1 = security, 0 != security)
                        bw.write("1;" + cols[0] + ";" + cols[1] + ";" + cols[2] + ";" + cols[3] + "\n");
                    }
                } else if(tfidf && word2vec){
                    if(average >= 0.6){
                        // add column for security report (1 = security, 0 != security)
                        bw.write("1;" + cols[0] + ";" + cols[1] + ";" + cols[2] + ";" + cols[3] + "\n");
                    }
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
}
