package similarity;

import machinelearning.utils.PropertySettings;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SimilarityScore {

    // prints the most similar report from benchmark dataset for each bug report using TFIDF and cosine similarity and prints suggested tags
    public static void mostSimilarSourceTFIDF(List<String[]> docsArray, List<String> terms, List<double[]> tfidfDocsVector, String benchmarkDataset, String file, String features, String source) throws IOException {
        TFIDFSimilarity d = new TFIDFSimilarity();

        List<SecurityRecord> benchmarks = getRecords(benchmarkDataset);
        List<Bug> bugs = getBugs(file);

        boolean writeHeader = false;
        if (!(new File(file.substring(0, file.length()-4) + "_tfidf_" + source + ".csv").isFile())) {
            writeHeader = true;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file.substring(0, file.length()-4) + "_tfidf_" + source + ".csv", true))) {

            if ((benchmarkDataset.contains("cve") && writeHeader)) {
                bw.write("Security" + PropertySettings.SEPARATOR + "Title" + PropertySettings.SEPARATOR + "Description" + PropertySettings.SEPARATOR + "Id" + PropertySettings.SEPARATOR +
                        "Date" + PropertySettings.SEPARATOR + "Cossim" + PropertySettings.SEPARATOR + "Source" + PropertySettings.SEPARATOR + "Description" + PropertySettings.SEPARATOR + "Type" + PropertySettings.SEPARATOR +
                        "Type-of-source" + PropertySettings.SEPARATOR + "Weakness" + PropertySettings.SEPARATOR + "Link" + PropertySettings.SEPARATOR + "Severity Score" + PropertySettings.SEPARATOR + "Severity" + "\n");
            } else if (writeHeader) {
                bw.write("Security" + PropertySettings.SEPARATOR + "Title" + PropertySettings.SEPARATOR + "Description" + PropertySettings.SEPARATOR + "Id" + PropertySettings.SEPARATOR + "Date" + PropertySettings.SEPARATOR + "Cossim" + PropertySettings.SEPARATOR +
                        "Source" + PropertySettings.SEPARATOR + "Description" + PropertySettings.SEPARATOR + "Type" + PropertySettings.SEPARATOR + "Type-of-source" + PropertySettings.SEPARATOR + "Weakness" + PropertySettings.SEPARATOR + "Link" + "\n");
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
                String bug = bugs.get(i).getSecurity() + PropertySettings.SEPARATOR + bugs.get(i).getTitle() + PropertySettings.SEPARATOR + bugs.get(i).getDescription() + PropertySettings.SEPARATOR + bugs.get(i).getId() + PropertySettings.SEPARATOR + bugs.get(i).getDate();

                if (score == 0.0) {
                    System.out.println(bugs.get(i).getId() + " got 0.0 similarity for all documents");
                    bw.write(bug + ";No similar sources found" + "\n");
                } else {
                    if (benchmarkDataset.contains("cve")) {
                        String record = benchmarks.get(n).getId() + PropertySettings.SEPARATOR + benchmarks.get(n).getDesc() + PropertySettings.SEPARATOR + benchmarks.get(n).getType() + PropertySettings.SEPARATOR + benchmarks.get(n).getTypeofsource() + PropertySettings.SEPARATOR
                                + benchmarks.get(n).getWeakness() + PropertySettings.SEPARATOR + benchmarks.get(n).getLink() + PropertySettings.SEPARATOR + benchmarks.get(n).getSeverityScore() + PropertySettings.SEPARATOR + benchmarks.get(n).getSeverity();
                        bw.write(bug + PropertySettings.SEPARATOR + score + PropertySettings.SEPARATOR + record + "\n");
                        System.out.println(score);
                    } else {
                        String record = benchmarks.get(n).getId() + PropertySettings.SEPARATOR + benchmarks.get(n).getDesc() + PropertySettings.SEPARATOR + benchmarks.get(n).getType() + PropertySettings.SEPARATOR + benchmarks.get(n).getTypeofsource() + PropertySettings.SEPARATOR
                                + benchmarks.get(n).getWeakness() + PropertySettings.SEPARATOR + benchmarks.get(n).getLink();
                        bw.write(bug + PropertySettings.SEPARATOR + score + PropertySettings.SEPARATOR + record + "\n");
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

    // prints the most similar report from benchmark dataset for each  bug report using Word2Vec and cosine similarity and prints suggested tags
    public static void mostSimilarSourceWord2Vec(String benchmarkDataset, String file, String word2vec, int numFeatures, String source) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Word2VecSimilarity w = new Word2VecSimilarity();

        Word2Vec model = w.getWord2Vec(word2vec);

        List<SecurityRecord> benchmarks = getRecords(benchmarkDataset);
        List<Bug> bugs = getBugs(file);

        boolean writeHeader = false;
        if (!(new File(file.substring(0, file.length()-4) + "_word2vec_" + source + ".csv").isFile())) {
            writeHeader = true;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file.substring(0, file.length()-4) + "_word2vec_" + source + ".csv", true))) {

            if ((benchmarkDataset.contains("cve") && writeHeader)) {
                bw.write("Security" + PropertySettings.SEPARATOR + "Title" + PropertySettings.SEPARATOR + "Description" + PropertySettings.SEPARATOR + "Id" + PropertySettings.SEPARATOR +
                        "Date" + PropertySettings.SEPARATOR + "Cossim" + PropertySettings.SEPARATOR + "Source" + PropertySettings.SEPARATOR + "Description" + PropertySettings.SEPARATOR + "Type" + PropertySettings.SEPARATOR +
                        "Type-of-source" + PropertySettings.SEPARATOR + "Weakness" + PropertySettings.SEPARATOR + "Link" + PropertySettings.SEPARATOR + "Severity Score" + PropertySettings.SEPARATOR + "Severity" + "\n");
            } else if (writeHeader) {
                bw.write("Security" + PropertySettings.SEPARATOR + "Title" + PropertySettings.SEPARATOR + "Description" + PropertySettings.SEPARATOR + "Id" + PropertySettings.SEPARATOR + "Date" + PropertySettings.SEPARATOR + "Cossim" + PropertySettings.SEPARATOR +
                        "Source" + PropertySettings.SEPARATOR + "Description" + PropertySettings.SEPARATOR + "Type" + PropertySettings.SEPARATOR + "Type-of-source" + PropertySettings.SEPARATOR + "Weakness" + PropertySettings.SEPARATOR + "Link" + "\n");
            }

            List<Collection<String>> benchmarkSentences = new ArrayList<>();
            w.getSentences(benchmarkDataset, benchmarkSentences);

            List<Collection<String>> bugSentences = new ArrayList<>();
            w.getSentences(file, bugSentences);

            double score = 0.0;
            double cosine_sim = 0.0;
            int n = 0;
            for (int i = 0; i < bugs.size(); i++) {
                for (int j = 0; j < benchmarks.size(); j++) {
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
                String bug = bugs.get(i).getSecurity() + PropertySettings.SEPARATOR + bugs.get(i).getTitle() + PropertySettings.SEPARATOR + bugs.get(i).getDescription() + PropertySettings.SEPARATOR + bugs.get(i).getId() + PropertySettings.SEPARATOR + bugs.get(i).getDate();

                if (score == 0.0) {
                    System.out.println(bugs.get(i).getId() + " got 0.0 similarity for all documents");
                    bw.write(bug + ";No similar sources found" + "\n");
                } else {
                    if (benchmarkDataset.contains("cve")) {
                        String record = benchmarks.get(n).getId() + PropertySettings.SEPARATOR + benchmarks.get(n).getDesc() + PropertySettings.SEPARATOR + benchmarks.get(n).getType() + PropertySettings.SEPARATOR + benchmarks.get(n).getTypeofsource() + PropertySettings.SEPARATOR
                                + benchmarks.get(n).getWeakness() + PropertySettings.SEPARATOR + benchmarks.get(n).getLink() + PropertySettings.SEPARATOR + benchmarks.get(n).getSeverityScore() + PropertySettings.SEPARATOR + benchmarks.get(n).getSeverity();
                        bw.write(bug + PropertySettings.SEPARATOR + score + PropertySettings.SEPARATOR + record + "\n");
                        System.out.println(score);
                    } else {
                        String record = benchmarks.get(n).getId() + PropertySettings.SEPARATOR + benchmarks.get(n).getDesc() + PropertySettings.SEPARATOR + benchmarks.get(n).getType() + PropertySettings.SEPARATOR + benchmarks.get(n).getTypeofsource() + PropertySettings.SEPARATOR
                                + benchmarks.get(n).getWeakness() + PropertySettings.SEPARATOR + benchmarks.get(n).getLink();
                        bw.write(bug + PropertySettings.SEPARATOR + score + PropertySettings.SEPARATOR + record + "\n");
                        System.out.println(score);
                    }
                }
                score = 0.0;
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
                String[] cols = line.split(PropertySettings.SEPARATOR);

                if (i == 0) {
                    // add column for security report (1 = security, 0 != security)
                    bw.write("Security" + PropertySettings.SEPARATOR + cols[0] + PropertySettings.SEPARATOR + cols[1] + PropertySettings.SEPARATOR + cols[2] + PropertySettings.SEPARATOR + cols[3] + "\n");
                }

                if (documents.contains(cols[2])) {
                    bw.write("1" + PropertySettings.SEPARATOR + cols[0] + PropertySettings.SEPARATOR + cols[1] + PropertySettings.SEPARATOR + cols[2] + PropertySettings.SEPARATOR + cols[3] + "\n");
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
                String[] cols = line.split(PropertySettings.SEPARATOR);

                if (i == 0) {
                    // add column for security report (1 = security, 0 != security)
                    bw.write("Security" + PropertySettings.SEPARATOR + cols[0] + PropertySettings.SEPARATOR + cols[1] + PropertySettings.SEPARATOR + cols[2] + PropertySettings.SEPARATOR + cols[3] + "\n");
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
                            bw.write(sec + PropertySettings.SEPARATOR + cols[0] + PropertySettings.SEPARATOR + cols[1] + PropertySettings.SEPARATOR + cols[2] + PropertySettings.SEPARATOR + cols[3] + "\n");
                        }
                    } else if (!tfidf && word2vec) {
                        if (word2vecScore <= threshold) {
                            // add column for security report (1 = security, 0 != security)
                            bw.write(sec + PropertySettings.SEPARATOR + cols[0] + PropertySettings.SEPARATOR + cols[1] + PropertySettings.SEPARATOR + cols[2] + PropertySettings.SEPARATOR + cols[3] + "\n");
                        }
                    } else if (tfidf && word2vec) {
                        if (average <= threshold) {
                            // add column for security report (1 = security, 0 != security)
                            bw.write(sec + PropertySettings.SEPARATOR + cols[0] + PropertySettings.SEPARATOR + cols[1] + PropertySettings.SEPARATOR + cols[2] + PropertySettings.SEPARATOR + cols[3] + "\n");
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
                String[] cols = line.split(PropertySettings.SEPARATOR);
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
                String[] cols = line.split(PropertySettings.SEPARATOR);
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
