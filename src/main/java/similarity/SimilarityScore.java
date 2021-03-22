package similarity;

import machinelearning.utils.Cleanup;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import sources.Utility;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SimilarityScore {

    private static List<String> documentsToKeep;
    private static List<String> documentsToKeep2;

    public static void main(String args[]) throws FileNotFoundException, IOException, IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        String path = "/Users/anja/Desktop/master/api/files/test/";
        //createDatasetFromScores(path + "stackoverflowNSR_small_tfidf_word2vec.csv",
        //        path + "stackoverflowNSR.csv", true, true, false);

        String benchmark = path + "cveData_100.csv";
        String docs = path + "stackoverflowSR_100.csv";
        //String docs2 = path + "stackoverflowNSR_small.csv";
        String features = "/Users/anja/Desktop/master/api/files/features/CVEFeaturesTFIDF.txt";
        String word2vec = "/Users/anja/Desktop/master/api/files/cve_word2vec_model.txt";

        //mostSimilarSourceTFIDF("./files/sources/cve.csv", "./files/experiments/tfidf/stackoverflow_CVE/stackoverflow_SR.csv", features);

        //listMostSimilarWord2VecCve("/Users/anja/Desktop/master/api/files/test/cveData.csv", "/Users/anja/Desktop/master/api/files/test/stackoverflowSBR_small.csv", word2vec);

        // SR
        documentsToKeep = new ArrayList<>();

        //test(benchmark, docs, features);

        //listMostSimilarAndSuggestTagsTFIDF(benchmark, docs, features);
        //listMostSimilarAndSuggestTagsWord2Vec(benchmark, docs, word2vec);
        //listMostSimilarSourceForBRsTFIDF(benchmark, docs, features);
//        listMostSimilarSourceForBRsWord2Vec(benchmark, docs, word2vec);
//
//        String newFilePath = path + "stackoverflowSR_new_similarity.csv";
//        createDatasetFromCVESimilarity(docs, newFilePath, documentsToKeep);
//
//        // NSR
//        documentsToKeep2 = new ArrayList<>();
//
//        listMostSimilarTFIDF(benchmark, docs2, features);
//        System.out.println("TFIDF done 2");
//        listMostSimilarWord2Vec(benchmark, docs2, word2vec);
//        System.out.println("Word2Vec done 2");
//
//        String newFilePath2 = path + "stackoverflowNSR_new_similarity.csv";
//        createDatasetFromCVESimilarity(docs2, newFilePath2, documentsToKeep2);
    }

    // prints the most similar bug report for each report in benchmark dataset using TFIDF and cosine similarity and prints suggested tags
    public static void listMostSimilarAndSuggestTagsTFIDF(String benchmarkDataset, String file, String features) throws IOException {
        TFIDFSimilarity d = new TFIDFSimilarity();

        List<String> benchmarkIds = getIds(benchmarkDataset, 0);
        List<String> bugIds = getIds(file, 3);

        List<String[]> cveDocsArray = d.getDocsArrayFromCsv(benchmarkDataset);
        List<String> terms = d.getTermsFromFile(features);
        List<double[]> tfidfDocsVectorCve = Utility.tFIDFVecFile("files/features/CVETFIDFFeaturesVec.csv", 200);

        //d.tfIdfCalculator(cveDocsArray, cveDocsArray, terms);
        List<String> documents = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line = "";
            int i = 0;
            while ((line = br.readLine()) != null) {
                if (i != 0) {
                    String[] cols = line.split(";");
                    String cleaned = new Cleanup().cleanText(cols[2]);
                    documents.add(cleaned);
                }
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

                //printTags(k, n, benchmarkDataset, benchmarkIds, bugIds, score, false);

                if (!documentsToKeep.contains(bugIds.get(n))) {
                    documentsToKeep.add(bugIds.get(n));
                }
                score = 0.0;
            }
        }
    }

    // prints the most similar report from benchmark dataset for each bug report using TFIDF and cosine similarity and prints suggested tags
    public static void mostSimilarSourceTFIDF(List<String[]> docsArray, List<String> terms, List<double[]> tfidfDocsVector, String benchmarkDataset, String file, String features, String source) throws IOException {
        TFIDFSimilarity d = new TFIDFSimilarity();

        List<SecurityRecord> benchmarks = getRecords(benchmarkDataset);
        List<Bug> bugs = getBugs(file);

        boolean writeHeader = false;
        if (!(new File("files/RQ3/SR_NSR_sources_" + source + ".csv").isFile())) {
            writeHeader = true;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("files/RQ3/SR_NSR_sources_" + source + ".csv", true))) {

            if ((benchmarkDataset.contains("cve") && writeHeader)) {
                bw.write("Security;Title;Description;Id;Date;Cossim;Source;Description;Type;Type-of-source;Weakness;Link;Severity Score;Severity" + "\n");
            } else if (writeHeader) {
                bw.write("Security;Title;Description;Id;Date;Cossim;Source;Description;Type;Type-of-source;Weakness;Link" + "\n");
            }

            double score = 0.0;
            double cosine = 0.0;
            int n = 0;
            for (int i = 0; i < bugs.size(); i++) {
                for (int j = 0; j < tfidfDocsVector.size(); j++) {
                    cosine = d.getCosineSimilarityTwoDocuments((d.getDocumentVectors(bugs.get(i).getDescription(), terms, docsArray)), tfidfDocsVector.get(j));

                    // use the highest score for each cve record
                    if (cosine > score) {
                        score = cosine;
                        n = j;
                    }
                }
                String forum = "";
                if (i < 200) {
                    forum = "SO";
                } else if (i < 300) {
                    forum = "AU";
                } else if (i < 400) {
                    forum = "SE";
                } else if (i < 500) {
                    forum = "SF";
                } else if (i < 700) {
                    forum = "SO";
                } else if (i < 800) {
                    forum = "AU";
                } else if (i < 900) {
                    forum = "SE";
                } else if (i < 1000) {
                    forum = "SF";
                }
                String bug = bugs.get(i).getSecurity() + ";" + bugs.get(i).getTitle() + ";" + bugs.get(i).getDescription() + ";" + forum + "_" + bugs.get(i).getId() + ";" + bugs.get(i).getDate();

                if (score == 0.0) {
                    System.out.println(bugs.get(i).getId() + " got 0.0 similarity for all documents");
                    bw.write(bug + ";No similar sources found" + "\n");
                } else {
                    if (benchmarkDataset.contains("cve")) {
                        String record = benchmarks.get(n).getId() + ";" + benchmarks.get(n).getDesc() + ";" + benchmarks.get(n).getType() + ";" + benchmarks.get(n).getTypeofsource() + ";"
                                + benchmarks.get(n).getWeakness() + ";" + benchmarks.get(n).getLink() + ";" + benchmarks.get(n).getSeverityScore() + ";" + benchmarks.get(n).getSeverity();
                        bw.write(bug + ";" + score + ";" + record + "\n");
                        System.out.println(score);
                    } else {
                        String record = benchmarks.get(n).getId() + ";" + benchmarks.get(n).getDesc() + ";" + benchmarks.get(n).getType() + ";" + benchmarks.get(n).getTypeofsource() + ";"
                                + benchmarks.get(n).getWeakness() + ";" + benchmarks.get(n).getLink();
                        bw.write(bug + ";" + score + ";" + record + "\n");
                        System.out.println(score);
                    }
                }
                score = 0.0;
            }
        }
    }

    // prints the most similar report from benchmark dataset for each bug report using TFIDF and cosine similarity and prints suggested tags
    public static void mostSimilarSourceVal(List<String[]> docsArray, List<String> terms, List<double[]> tfidfDocsVector, String benchmarkDataset, String file, String features, String source) throws IOException {
        TFIDFSimilarity d = new TFIDFSimilarity();

        List<SecurityRecord> benchmarks = getRecords(benchmarkDataset);
        List<Bug> bugs = getVal(file);

        boolean writeHeader = false;
        if (!(new File("files/RQ2/validation/validation_" + source + ".csv").isFile())) {
            writeHeader = true;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("files/RQ2/validation/validation_" + source + ".csv", true))) {

            if ((benchmarkDataset.contains("cve") && writeHeader)) {
                if(file.contains("chromium")) {
                    bw.write("Security;Description;Cossim;Source;Description;Type;Type-of-source;Weakness;Link;Severity Score;Severity" + "\n");
                } else {
                    bw.write("Security;Summary;Description;Cossim;Source;Description;Type;Type-of-source;Weakness;Link;Severity Score;Severity" + "\n");
                }
            } else if (writeHeader) {
                if(file.contains("chromium")) {
                    bw.write("Security;Description;Date;Cossim;Source;Description;Type;Type-of-source;Weakness;Link" + "\n");
                } else {
                    bw.write("Security;Summary;Description;Date;Cossim;Source;Description;Type;Type-of-source;Weakness;Link" + "\n");                }
            }

            double score = 0.0;
            double cosine = 0.0;
            int n = 0;
            for (int i = 0; i < bugs.size(); i++) {
                for (int j = 0; j < tfidfDocsVector.size(); j++) {
                    cosine = d.getCosineSimilarityTwoDocuments((d.getDocumentVectors(bugs.get(i).getDescription(), terms, docsArray)), tfidfDocsVector.get(j));

                    // use the highest score for each cve record
                    if (cosine > score) {
                        score = cosine;
                        n = j;
                    }
                }

                String bug = "";
                if(file.contains("chromium")) {
                    bug = bugs.get(i).getSecurity() + ";" + bugs.get(i).getDescription();
                } else {
                    bug = bugs.get(i).getSecurity() + ";" + bugs.get(i).getTitle() + ";" + bugs.get(i).getDescription();
                }

                if (score == 0.0) {
                    System.out.println(bugs.get(i).getId() + " got 0.0 similarity for all documents");
                    bw.write(bug + ";No similar sources found" + "\n");
                } else {
                    if (benchmarkDataset.contains("cve")) {
                        String record = benchmarks.get(n).getId() + ";" + benchmarks.get(n).getDesc() + ";" + benchmarks.get(n).getType() + ";" + benchmarks.get(n).getTypeofsource() + ";"
                                + benchmarks.get(n).getWeakness() + ";" + benchmarks.get(n).getLink() + ";" + benchmarks.get(n).getSeverityScore() + ";" + benchmarks.get(n).getSeverity();
                        bw.write(bug + ";" + score + ";" + record + "\n");
                        System.out.println(score);
                    } else {
                        String record = benchmarks.get(n).getId() + ";" + benchmarks.get(n).getDesc() + ";" + benchmarks.get(n).getType() + ";" + benchmarks.get(n).getTypeofsource() + ";"
                                + benchmarks.get(n).getWeakness() + ";" + benchmarks.get(n).getLink();
                        bw.write(bug + ";" + score + ";" + record + "\n");
                        System.out.println(score);
                    }
                }
                score = 0.0;
            }
        }
    }

    public static List<Bug> getVal(String file) {
        List<Bug> bugs = new ArrayList<>();

        int security;
        String desc = "";
        String date = "";
        String line = "";
        String title = "";
        try (BufferedReader bw = new BufferedReader(new FileReader(file))) {
            int i = 0;
            while ((line = bw.readLine()) != null) {
                if (i == 0) {
                    i++;
                    continue;
                }
                String[] cols = line.split(";");
                if(file.contains("chromium")){
                    desc = cols[1];
                } else {
                    desc = cols[2];
                    title = cols[1];
                }
                security = Integer.parseInt(cols[0]);

                Bug bug = new Bug(security, "", desc, title, date);

                bugs.add(bug);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bugs;
    }

    // prints the most similar bug report for each report in benchmark dataset using Word2Vec and cosine similarity and prints suggested tags
    public static void listMostSimilarAndSuggestTagsWord2Vec(String benchmarkDataset, String file, String word2vec, int numFeatures) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Word2VecSimilarity w = new Word2VecSimilarity();

        Word2Vec model = w.getWord2Vec(word2vec);

        List<Collection<String>> benchmarkSentences = new ArrayList<>();
        w.getSentences("/Users/anja/Desktop/master/api/files/test/cveData.csv", benchmarkSentences);

        List<Collection<String>> bugSentences = new ArrayList<>();
        w.getSentences("/Users/anja/Desktop/master/api/files/test/stackoverflowSBR_small.csv", bugSentences);

        List<String> benchmarkIds = getIds(benchmarkDataset, 0);
        List<String> bugIds = getIds(file, 2);

        double score = 0.0;
        double cosine_sim = 0.0;
        int n = 0;
        for (int i = 0; i < benchmarkSentences.size(); i++) {
            for (int j = 0; j < bugSentences.size(); j++) {
                INDArray input1_vector = w.getVector(benchmarkSentences.get(i), model, numFeatures);
                INDArray input2_vector = w.getVector(bugSentences.get(j), model, numFeatures);

                double dot_product = Nd4j.getBlasWrapper().dot(input1_vector, input2_vector);

                cosine_sim = w.cosine_similarity(input1_vector.toDoubleVector(), input2_vector.toDoubleVector(), dot_product);
                //System.out.println("Cosine similarity: " + cosine_sim);

                // use the highest score for each bug report
                if (cosine_sim > score) {
                    score = cosine_sim;
                    n = j;
                }
            }

            //printTags(i, n, benchmarkDataset, benchmarkIds, bugIds, score, false);

            if (!documentsToKeep.contains(bugIds.get(n))) {
                documentsToKeep.add(bugIds.get(n));
            }
            score = 0.0;
        }
    }

    // prints the most similar report from benchmark dataset for each  bug report using Word2Vec and cosine similarity and prints suggested tags
    public static void listMostSimilarSourceForBRsWord2Vec(String benchmarkDataset, String file, String word2vec, int numFeatures) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Word2VecSimilarity w = new Word2VecSimilarity();

        Word2Vec model = w.getWord2Vec(word2vec);

        List<Collection<String>> benchmarkSentences = new ArrayList<>();
        w.getSentences("/Users/anja/Desktop/master/api/files/test/cveData.csv", benchmarkSentences);

        List<Collection<String>> bugSentences = new ArrayList<>();
        w.getSentences("/Users/anja/Desktop/master/api/files/test/stackoverflowSBR_small.csv", bugSentences);

        List<String> benchmarkIds = getIds(benchmarkDataset, 0);
        List<String> bugIds = getIds(file, 2);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file + ".pred"))) {

                String line = "";
                int p = 0;
                while ((line = br.readLine()) != null) {
                    if (p == 0) {
                        if (benchmarkDataset.contains("CVE")) {
                            bw.write(line + ";Cossim;Source;Description;Type;Type-of-source;Weakness;Link;Severity Score;Severity" + "\n");
                        } else {
                            bw.write(line + ";Cossim;Source;Description;Type;Type-of-source;Weakness;Link" + "\n");
                        }
                    } else {
                        bw.write(line);
                    }
                    p++;
                }

                double score = 0.0;
                double cosine_sim = 0.0;
                int n = 0;
                for (int i = 0; i < bugSentences.size(); i++) {
                    for (int j = 0; j < benchmarkSentences.size(); j++) {
                        INDArray input1_vector = w.getVector(benchmarkSentences.get(j), model, numFeatures);
                        INDArray input2_vector = w.getVector(bugSentences.get(i), model, numFeatures);

                        double dot_product = Nd4j.getBlasWrapper().dot(input1_vector, input2_vector);

                        cosine_sim = w.cosine_similarity(input1_vector.toDoubleVector(), input2_vector.toDoubleVector(), dot_product);
                        //System.out.println("Cosine similarity: " + cosine_sim);

                        // use the highest score for each bug report
                        if (cosine_sim > score) {
                            score = cosine_sim;
                            n = j;
                        }
                    }
                    if (score == 0.0) {
                        System.out.println("SO_" + bugIds.get(i) + " got 0.0 similarity for all documents");
                    } else {
                        List<String> tags = getTags(n, i, benchmarkDataset, benchmarkIds, bugIds, score, true);
                        if (benchmarkDataset.contains("CVE")) {
                            bw.write(";" + score + ";" + tags.get(0) + ";" + tags.get(1) + ";" + tags.get(2) + ";" + tags.get(3) + ";" + tags.get(4) + ";" + tags.get(5) + ";" + tags.get(6) + ";" + tags.get(7) + "\n");
                        } else {
                            bw.write(";" + score + ";" + tags.get(0) + ";" + tags.get(1) + ";" + tags.get(2) + ";" + tags.get(3) + ";" + tags.get(4) + ";" + tags.get(5) + "\n");
                        }
                    }
                    score = 0.0;
                }
            }
        }
    }

    // creates dataset from the most similar bug reports for each report in benchmark dataset
    public static void createDatasetFromSimilarity(String filePath, String newFilePath, List<String> documents) throws IOException {
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

                if (i == 0) {
                    // add column for security report (1 = security, 0 != security)
                    bw.write("Security;" + cols[0] + ";" + cols[1] + ";" + cols[2] + ";" + cols[3] + "\n");
                }

                if (documents.contains(cols[2])) {
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
            while ((line = br.readLine()) != null) {
                String[] cols = line.split(";");
                if (i != 0) {
                    ids.add(cols[column]);
                }
                //System.out.println(cols[column]);
                i++;
            }
        }
        return ids;
    }

    // creates new dataset from scores TFIDF and Word2Vec scores saved in dataset file
    public static void createDatasetFromScores(String filePath, String newFilePath, boolean tfidf, boolean word2vec, boolean security, double threshold) throws IOException {
        File file = new File(newFilePath);

        BufferedReader br = null;
        BufferedWriter bw = null;

        int sec = 0;
        if (security) {
            sec = 1;
        }

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

                if (i == 0) {
                    // add column for security report (1 = security, 0 != security)
                    bw.write("Security;" + cols[0] + ";" + cols[1] + ";" + cols[2] + ";" + cols[3] + "\n");
                } else {
                    tfidfScore = Double.parseDouble(cols[4]);
                    word2vecScore = Double.parseDouble(cols[5]);
                    average = (tfidfScore + word2vecScore) / 2;
                    //System.out.println(average);
                }

                if (i != 0) {
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

    public static List<String> getTags(int benchmark, int bug, String file, List<String> benchmarkIds, List<String> bugIds, double score, boolean bugFirst) {
        List<String> tags = new ArrayList<>();

        double severityScore = 0.0;
        String severity = "";
        String type = "";
        String typeofsource = "";
        String weakness = "";
        String link = "";
        String line = "";
        String desc = "";
        try (BufferedReader bw = new BufferedReader(new FileReader(file))) {
            while ((line = bw.readLine()) != null) {
                String[] cols = line.split(";");
                if (benchmarkIds.get(benchmark).contains("CVE") && benchmarkIds.get(benchmark).equals(cols[0])) {
                    desc = cols[1];
                    type = cols[2];
                    typeofsource = cols[3];
                    weakness = cols[4];
                    link = cols[7];
                    try {
                        severityScore = Double.parseDouble(cols[8]);
                    } catch (NumberFormatException e) {
                        severityScore = 0.0;
                    }
                    severity = cols[9];

                    tags.add(benchmarkIds.get(benchmark));
                    tags.add(desc);
                    tags.add(type);
                    tags.add(typeofsource);
                    tags.add(weakness);
                    tags.add(link);
                    tags.add(String.valueOf(severityScore));
                    tags.add(severity);

                    if (bugFirst) {
                        System.out.println(bugIds.get(bug) + " and " + benchmarkIds.get(benchmark) + ": " + score);
                    } else {
                        System.out.println(benchmarkIds.get(benchmark) + " and SO_" + bugIds.get(bug) + ": " + score);
                    }
                    System.out.println("Suggested tags: \n" + "Type: " + type + " Type-of-source: " + typeofsource + " Weakness: " + weakness + " Score: " + severityScore + " Severity: " + severity + " Link: " + link + "\n");
                    break;
                } else if (benchmarkIds.get(benchmark).equals(cols[0])) {
                    desc = cols[1];
                    type = cols[2];
                    typeofsource = cols[3];
                    weakness = cols[4];
                    link = cols[6];

                    tags.add(benchmarkIds.get(benchmark));
                    tags.add(desc);
                    tags.add(type);
                    tags.add(typeofsource);
                    tags.add(weakness);
                    tags.add(link);

                    System.out.println(benchmarkIds.get(benchmark) + " and " + bugIds.get(bug) + ": " + score);
                    System.out.println("Suggested tags: \n" + "Type: " + type + " Type-of-source: " + typeofsource + " Weakness: " + weakness + " Link: " + link + "\n");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tags;
    }

    public static List<Bug> getBugs(String file) {
        List<Bug> bugs = new ArrayList<>();

        int security;
        String id;
        String title;
        String desc = "";
        String date = "";
        String line = ";";
        try (BufferedReader bw = new BufferedReader(new FileReader(file))) {
            int i = 0;
            while ((line = bw.readLine()) != null) {
                if (i == 0) {
                    i++;
                    continue;
                }
                String[] cols = line.split(";");
                id = cols[3];
                desc = cols[2];
                title = cols[1];
                date = cols[4];
                security = Integer.parseInt(cols[0]);

                Bug bug = new Bug(security, id, desc, title, date);

                bugs.add(bug);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bugs;
    }

    public static List<SecurityRecord> getRecords(String file) {
        List<SecurityRecord> records = new ArrayList<>();

        String id;
        String severityScore;
        String severity = "";
        String type = "";
        String typeofsource = "";
        String weakness = "";
        String link = "";
        String line = "";
        String desc = "";
        try (BufferedReader bw = new BufferedReader(new FileReader(file))) {
            int i = 0;
            while ((line = bw.readLine()) != null) {
                if (i == 0) {
                    i++;
                    continue;
                }
                String[] cols = line.split(";");
                if (file.contains("cve")) {
                    id = cols[0];
                    desc = cols[1];
                    type = cols[2];
                    typeofsource = cols[3];
                    weakness = cols[4];
                    link = cols[7];
                    try {
                        severityScore = cols[8];
                    } catch (Exception e) {
                        severityScore = "0.0";
                    }
                    try {
                        severity = cols[9];
                    } catch (Exception e ){
                        severity = "No severity rating";
                    }
                } else {
                    id = cols[0];
                    desc = cols[1];
                    type = cols[2];
                    typeofsource = cols[3];
                    weakness = cols[4];
                    try {
                        link = cols[6];
                    } catch (Exception e){
                        link = "";
                    }
                    severityScore = "0.0";
                    severity = "";
                }

                SecurityRecord record = new SecurityRecord(id, desc, type, typeofsource, weakness, link, severityScore, severity);

                records.add(record);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return records;
    }
}
