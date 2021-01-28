package similarity;

import org.deeplearning4j.models.word2vec.Word2Vec;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SimilarityScore {

    private static List<String> documentsToKeep;

    public static void main(String args[]) throws FileNotFoundException, IOException, IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        String path = "/Users/anja/Desktop/master/api/files/testing/";
        createDatasetFromScores(path + "stackoverflowNSR_small_tfidf_word2vec.csv",
                path + "stackoverflowNSR_new.csv", true, true, false);

        String benchmark = path + "cveData.csv";
        String docs = path + "stackoverflowSBR_small.csv";
        String features = "/Users/anja/Desktop/master/api/files/FeaturesTFIDF.txt";
        String word2vec = "/Users/anja/Desktop/master/api/files/word2vec_model.txt";

        //listMostSimilarTFIDF(benchmark, docs, features);
//        listMostSimilarWord2Vec(benchmark, docs, word2vec);
//
//        String newFilePath = path + "stackoverflowSBR_new_word2vec.csv";
//        createDatasetFromCVESimilarity(docs, newFilePath, documentsToKeep);
    }


    // burde endres til å ta inn docsArray for benchmark
    public static void listMostSimilarTFIDF(String benchmarkDataset, String file, String features) throws IOException {
        Documents d = new Documents();

        List<String> benchmarkIds = getIds(benchmarkDataset, 0);
        List<String> bugIds = getIds(file, 2);
        documentsToKeep = new ArrayList<>();

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
                System.out.println(benchmarkIds.get(k) + " and SO_" + bugIds.get(n) + ": " + score);
                if(!documentsToKeep.contains(bugIds.get(n))) {
                    documentsToKeep.add(bugIds.get(n));
                }
                score = 0.0;
            }
        }
    }

    public static void listMostSimilarWord2Vec(String benchmarkDataset, String file, String word2vec) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Word2VecCalculator w = new Word2VecCalculator();

        Word2Vec model = w.getWord2Vec(word2vec);

        List<Collection<String>> benchmarkSentences = new ArrayList<>();
        w.getSentences("/Users/anja/Desktop/master/api/files/test/cveData.csv", benchmarkSentences);

        List<Collection<String>> bugSentences = new ArrayList<>();
        w.getSentences("/Users/anja/Desktop/master/api/files/test/stackoverflowSBR_small.csv", bugSentences);

        List<String> benchmarkIds = getIds(benchmarkDataset, 0);
        List<String> bugIds = getIds(file, 2);
        documentsToKeep = new ArrayList<>();

        double score = 0.0;
        double cosine_sim = 0.0;
        int n = 0;
        for(int i = 0; i < benchmarkSentences.size(); i++) {
            for (int j = 0; j < bugSentences.size(); j++) {
                INDArray input1_vector = w.getVector(benchmarkSentences.get(i), model);
                INDArray input2_vector = w.getVector(bugSentences.get(j), model);

                double dot_product = Nd4j.getBlasWrapper().dot(input1_vector, input2_vector);

                cosine_sim = w.cosine_similarity(input1_vector.toDoubleVector(), input2_vector.toDoubleVector(), dot_product);
                //System.out.println("Cosine similarity: " + cosine_sim);

                // use the highest score for each bug report
                if (cosine_sim > score) {
                    score = cosine_sim;
                    n = j;
                }
            }
            System.out.println(benchmarkIds.get(i) + " and SO_" + bugIds.get(n) + ": " + score);
            if(!documentsToKeep.contains(bugIds.get(n))) {
                documentsToKeep.add(bugIds.get(n));
            }
            score = 0.0;
        }
    }

    public static void createDatasetFromCVESimilarity(String filePath, String newFilePath, List<String> documents) throws IOException {
        File file = new File(newFilePath);

        BufferedReader br = null;
        BufferedWriter bw = null;

        try {
            br = new BufferedReader(new FileReader(filePath));
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));

            String line = "";
            int i = 0;
            while ((line = br.readLine()) != null) {
                String[] cols = line.split(";");

                if(i == 0){
                    // add column for security report (1 = security, 0 != security)
                    bw.write("Security;" + cols[0] + ";" + cols[1] + ";" + cols[2] + ";" + cols[3] + "\n");
                }

                if(documents.contains(cols[2])) {
                    bw.write("1;" + cols[0] + ";" + cols[1] + ";" + cols[2] + ";" + cols[3] + "\n");
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

    public static void createDatasetFromScores(String filePath, String newFilePath, boolean tfidf, boolean word2vec, boolean security) throws IOException {
        File file = new File(newFilePath);

        BufferedReader br = null;
        BufferedWriter bw = null;

        int sec = 0;
        if(security){
            sec = 1;
        }

        double threshold = 0.6;

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

                if(i != 0) {
                    if (tfidf && !word2vec) {
                        if (tfidfScore <= threshold) {
                            // add column for security report (1 = security, 0 != security)
                            bw.write(sec + ";" + cols[0] + ";" + cols[1] + ";" + cols[2] + ";" + cols[3] + "\n");
                        }
                    } else if (!tfidf && word2vec) {
                        if (word2vecScore <= threshold) {
                            // add column for security report (1 = security, 0 != security)
                            bw.write(sec + ";" + cols[0] + ";" + cols[1] + ";" + cols[2] + ";" + cols[3] + "\n");
                        }
                    } else if (tfidf && word2vec) {
                        if (average <= threshold) {
                            // add column for security report (1 = security, 0 != security)
                            bw.write(sec + ";" + cols[0] + ";" + cols[1] + ";" + cols[2] + ";" + cols[3] + "\n");
                        }
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
