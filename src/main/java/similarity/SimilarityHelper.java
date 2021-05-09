package similarity;

import machinelearning.utility.PropertySettings;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SimilarityHelper {

    Similarity similarity;

    public SimilarityHelper(Similarity similarity) {
        this.similarity = similarity;
    }

    /**
     * prints the most similar report from benchmark dataset for each bug report using TFIDF and cosine similarity and prints suggested tags
     */
    public void mostSimilarSourceTFIDF() throws Exception {
        if (similarity.getBenchmarkDataset().isEmpty()) {
            throw new Exception("Security source dataset must be set.");
        } else if (similarity.getSource().isEmpty()) {
            throw new Exception("Security source name must be set.");
        } else if (similarity.getFile().isEmpty()) {
            throw new Exception("Data file must be set.");
        } else if (similarity.getFeatures().isEmpty()) {
            throw new Exception("Feature file must be set.");
        } else if (similarity.getDescriptionIndex() == -1) {
            throw new Exception("Description index in data file must be set.");
        } else if (similarity.getNumFeatures() == 0) {
            throw new Exception("Num features must be set.");
        }

        TFIDFSimilarity d = new TFIDFSimilarity();

        List<String[]> docsArray = d.getDocsArrayFromCsv(similarity.getFile());
        List<String> terms = d.getTermsFromFile(similarity.getFeatures());
        List<double[]> tfidfDocsVector = d.tfIdfCalculator(docsArray, docsArray, terms);

        List<SecurityRecord> benchmarks = getRecords(similarity.getBenchmarkDataset());
        List<Bug> bugs = getBugs(similarity.getFile(), similarity.getDescriptionIndex());

        boolean writeHeader = false;
        if (!(new File(similarity.getFile().substring(0, similarity.getFile().length()-4) + "_tfidf_" + similarity.getSource() + ".csv").isFile())) {
            writeHeader = true;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(similarity.getFile().substring(0, similarity.getFile().length()-4) + "_tfidf_" + similarity.getSource() + ".csv", true))) {

            if ((similarity.getBenchmarkDataset().contains("cve") && writeHeader)) {
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
                    cosine = new CosineSimilarity().cosineSimilarity((d.getDocumentVectors(bugs.get(i).getDescription(), terms, docsArray)), tfidfDocsVector.get(j));

                    // use the highest score for each cve record
                    if (cosine > score) {
                        score = cosine;
                        n = j;
                    }
                }
                String bug = bugs.get(i).getBug();

                if (score == 0.0) {
                    //System.out.println(bugs.get(i).getId() + " got 0.0 similarity for all documents");
                    bw.write(bug + ";No similar sources found" + "\n");
                } else {
                    if (similarity.getBenchmarkDataset().contains("cve")) {
                        String record = benchmarks.get(n).getId() + PropertySettings.SEPARATOR + benchmarks.get(n).getDesc() + PropertySettings.SEPARATOR + benchmarks.get(n).getType() + PropertySettings.SEPARATOR + benchmarks.get(n).getTypeofsource() + PropertySettings.SEPARATOR
                                + benchmarks.get(n).getWeakness() + PropertySettings.SEPARATOR + benchmarks.get(n).getLink() + PropertySettings.SEPARATOR + benchmarks.get(n).getSeverityScore() + PropertySettings.SEPARATOR + benchmarks.get(n).getSeverity();
                        bw.write(bug + PropertySettings.SEPARATOR + score + PropertySettings.SEPARATOR + record + "\n");
                        //System.out.println(score);
                    } else {
                        String record = benchmarks.get(n).getId() + PropertySettings.SEPARATOR + benchmarks.get(n).getDesc() + PropertySettings.SEPARATOR + benchmarks.get(n).getType() + PropertySettings.SEPARATOR + benchmarks.get(n).getTypeofsource() + PropertySettings.SEPARATOR
                                + benchmarks.get(n).getWeakness() + PropertySettings.SEPARATOR + benchmarks.get(n).getLink();
                        bw.write(bug + PropertySettings.SEPARATOR + score + PropertySettings.SEPARATOR + record + "\n");
                        //System.out.println(score);
                    }
                }
                score = 0.0;
            }
        }
    }

    /**
     * prints the n first similar reports from benchmark dataset over set threshold for each bug report using TFIDF and cosine similarity and prints suggested tags
     */
    public void similarSourcesTFIDF() throws Exception {

        if (similarity.getBenchmarkDataset().isEmpty()) {
            throw new Exception("Security source dataset must be set.");
        } else if (similarity.getSource().isEmpty()) {
            throw new Exception("Security source name must be set.");
        } else if (similarity.getFile().isEmpty()) {
            throw new Exception("Data file must be set.");
        } else if (similarity.getFeatures().isEmpty()) {
            throw new Exception("Feature file must be set.");
        } else if (similarity.getDescriptionIndex() == -1) {
            throw new Exception("Description index in data file must be set.");
        } else if (similarity.getNumFeatures() == 0) {
            throw new Exception("Num features must be set.");
        }

        TFIDFSimilarity d = new TFIDFSimilarity();

        List<String[]> docsArray = d.getDocsArrayFromCsv(similarity.getFile());
        List<String> terms = d.getTermsFromFile(similarity.getFeatures());
        List<double[]> tfidfDocsVector = d.tfIdfCalculator(docsArray, docsArray, terms);

        List<SecurityRecord> benchmarks = getRecords(similarity.getBenchmarkDataset());
        List<Bug> bugs = getBugs(similarity.getFile(), similarity.getDescriptionIndex());

        boolean writeHeader = false;
        if (!(new File(similarity.getFile().substring(0, similarity.getFile().length() - 4) + "_tfidf_" + similarity.getSource() + ".csv").isFile())) {
            writeHeader = true;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(similarity.getFile().substring(0, similarity.getFile().length() - 4) + "_tfidf_" + similarity.getSource() + ".csv", true))) {

            if ((similarity.getBenchmarkDataset().contains("cve") && writeHeader)) {
                bw.write("Security" + PropertySettings.SEPARATOR + "Title" + PropertySettings.SEPARATOR + "Description" + PropertySettings.SEPARATOR + "Id" + PropertySettings.SEPARATOR +
                        "Date" + PropertySettings.SEPARATOR + "Cossim" + PropertySettings.SEPARATOR + "Source" + PropertySettings.SEPARATOR + "Description" + PropertySettings.SEPARATOR + "Type" + PropertySettings.SEPARATOR +
                        "Type-of-source" + PropertySettings.SEPARATOR + "Weakness" + PropertySettings.SEPARATOR + "Link" + PropertySettings.SEPARATOR + "Severity Score" + PropertySettings.SEPARATOR + "Severity" + "\n");
            } else if (writeHeader) {
                bw.write("Security" + PropertySettings.SEPARATOR + "Title" + PropertySettings.SEPARATOR + "Description" + PropertySettings.SEPARATOR + "Id" + PropertySettings.SEPARATOR + "Date" + PropertySettings.SEPARATOR + "Cossim" + PropertySettings.SEPARATOR +
                        "Source" + PropertySettings.SEPARATOR + "Description" + PropertySettings.SEPARATOR + "Type" + PropertySettings.SEPARATOR + "Type-of-source" + PropertySettings.SEPARATOR + "Weakness" + PropertySettings.SEPARATOR + "Link" + "\n");
            }

            int num = 0;
            double score = 0.0;
            double cosine = 0.0;
            int n = 0;
            for (int i = 0; i < bugs.size(); i++) {
                for (int j = 0; j < tfidfDocsVector.size(); j++) {
                    if (num < similarity.getNumSimilarSources()) {
                        cosine = new CosineSimilarity().cosineSimilarity((d.getDocumentVectors(bugs.get(i).getDescription(), terms, docsArray)), tfidfDocsVector.get(j));

                        // use the highest score for each cve record
//                    if (cosine > score) {
//                        score = cosine;
//                        n = j;
//                    }
//                }
                        if (cosine >= similarity.getThreshold()) {
                            score = cosine;
                            String bug = bugs.get(i).getBug();

                            if (similarity.getBenchmarkDataset().contains("cve")) {
                                String record = benchmarks.get(n).getId() + PropertySettings.SEPARATOR + benchmarks.get(n).getDesc() + PropertySettings.SEPARATOR + benchmarks.get(n).getType() + PropertySettings.SEPARATOR + benchmarks.get(n).getTypeofsource() + PropertySettings.SEPARATOR
                                        + benchmarks.get(n).getWeakness() + PropertySettings.SEPARATOR + benchmarks.get(n).getLink() + PropertySettings.SEPARATOR + benchmarks.get(n).getSeverityScore() + PropertySettings.SEPARATOR + benchmarks.get(n).getSeverity();
                                bw.write(bug + PropertySettings.SEPARATOR + score + PropertySettings.SEPARATOR + record + "\n");
                                //System.out.println(score);
                            } else {
                                String record = benchmarks.get(n).getId() + PropertySettings.SEPARATOR + benchmarks.get(n).getDesc() + PropertySettings.SEPARATOR + benchmarks.get(n).getType() + PropertySettings.SEPARATOR + benchmarks.get(n).getTypeofsource() + PropertySettings.SEPARATOR
                                        + benchmarks.get(n).getWeakness() + PropertySettings.SEPARATOR + benchmarks.get(n).getLink();
                                bw.write(bug + PropertySettings.SEPARATOR + score + PropertySettings.SEPARATOR + record + "\n");
                                //System.out.println(score);
                            }
                            num++;
                        }
                    }
                }
                num = 0;
            }
        }
    }

    /**
     * prints the most similar report from benchmark dataset for each  bug report using Word2Vec and cosine similarity and prints suggested tags
     */
    public void mostSimilarSourceWord2Vec() throws Exception {

        if (similarity.getBenchmarkDataset().isEmpty()) {
            throw new Exception("Security source dataset must be set.");
        } else if (similarity.getSource().isEmpty()) {
            throw new Exception("Security source name must be set.");
        } else if (similarity.getFile().isEmpty()) {
            throw new Exception("Data file must be set.");
        } else if (similarity.getWord2vec().isEmpty()) {
            throw new Exception("Word2Vec model file must be set.");
        } else if (similarity.getDescriptionIndex() == -1) {
            throw new Exception("Description index in data file must be set.");
        } else if (similarity.getNumFeatures() == 0) {
            throw new Exception("Num features must be set.");
        }

        Word2VecSimilarity w = new Word2VecSimilarity();

        Word2Vec model = w.getWord2Vec(similarity.getWord2vec());

        List<SecurityRecord> benchmarks = getRecords(similarity.getBenchmarkDataset());
        List<Bug> bugs = getBugs(similarity.getFile(), similarity.getDescriptionIndex());

        boolean writeHeader = false;
        if (!(new File(similarity.getFile().substring(0, similarity.getFile().length() - 4) + "_word2vec_" + similarity.getSource() + ".csv").isFile())) {
            writeHeader = true;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(similarity.getFile().substring(0, similarity.getFile().length() - 4) + "_word2vec_" + similarity.getSource() + ".csv", true))) {

            if ((similarity.getBenchmarkDataset().contains("cve") && writeHeader)) {
                bw.write("Security" + PropertySettings.SEPARATOR + "Title" + PropertySettings.SEPARATOR + "Description" + PropertySettings.SEPARATOR + "Id" + PropertySettings.SEPARATOR +
                        "Date" + PropertySettings.SEPARATOR + "Cossim" + PropertySettings.SEPARATOR + "Source" + PropertySettings.SEPARATOR + "Description" + PropertySettings.SEPARATOR + "Type" + PropertySettings.SEPARATOR +
                        "Type-of-source" + PropertySettings.SEPARATOR + "Weakness" + PropertySettings.SEPARATOR + "Link" + PropertySettings.SEPARATOR + "Severity Score" + PropertySettings.SEPARATOR + "Severity" + "\n");
            } else if (writeHeader) {
                bw.write("Security" + PropertySettings.SEPARATOR + "Title" + PropertySettings.SEPARATOR + "Description" + PropertySettings.SEPARATOR + "Id" + PropertySettings.SEPARATOR + "Date" + PropertySettings.SEPARATOR + "Cossim" + PropertySettings.SEPARATOR +
                        "Source" + PropertySettings.SEPARATOR + "Description" + PropertySettings.SEPARATOR + "Type" + PropertySettings.SEPARATOR + "Type-of-source" + PropertySettings.SEPARATOR + "Weakness" + PropertySettings.SEPARATOR + "Link" + "\n");
            }

            List<Collection<String>> benchmarkSentences = new ArrayList<>();
            w.getSentences(similarity.getBenchmarkDataset(), benchmarkSentences, similarity.getDescriptionIndex());

            List<Collection<String>> bugSentences = new ArrayList<>();
            w.getSentences(similarity.getFile(), bugSentences, similarity.getDescriptionIndex());

            double score = 0.0;
            double cosine_sim = 0.0;
            int n = 0;
            for (int i = 0; i < bugSentences.size(); i++) {
                for (int j = 0; j < benchmarkSentences.size(); j++) {
                    INDArray input1_vector = w.getVector(benchmarkSentences.get(j), model, similarity.getNumFeatures());
                    INDArray input2_vector = w.getVector(bugSentences.get(i), model, similarity.getNumFeatures());

                    double dot_product = Nd4j.getBlasWrapper().dot(input1_vector, input2_vector);

                    cosine_sim = w.cosine_similarity(input1_vector.toDoubleVector(), input2_vector.toDoubleVector(), dot_product);
                    //System.out.println("Cosine similarity: " + cosine_sim);

                    // use the highest score for each bug report
                    if (cosine_sim > score) {
                        score = cosine_sim;
                        n = j;
                    }
                }
                String bug = bugs.get(i).getBug();

                if (score == 0.0) {
                    //System.out.println("0.0 similarity for all documents");
                    bw.write(bug + ";No similar sources found" + "\n");
                } else {
                    if (similarity.getBenchmarkDataset().contains("cve")) {
                        String record = benchmarks.get(n).getId() + PropertySettings.SEPARATOR + benchmarks.get(n).getDesc() + PropertySettings.SEPARATOR + benchmarks.get(n).getType() + PropertySettings.SEPARATOR + benchmarks.get(n).getTypeofsource() + PropertySettings.SEPARATOR
                                + benchmarks.get(n).getWeakness() + PropertySettings.SEPARATOR + benchmarks.get(n).getLink() + PropertySettings.SEPARATOR + benchmarks.get(n).getSeverityScore() + PropertySettings.SEPARATOR + benchmarks.get(n).getSeverity();
                        bw.write(bug + PropertySettings.SEPARATOR + score + PropertySettings.SEPARATOR + record + "\n");
                        //System.out.println(score);
                    } else {
                        String record = benchmarks.get(n).getId() + PropertySettings.SEPARATOR + benchmarks.get(n).getDesc() + PropertySettings.SEPARATOR + benchmarks.get(n).getType() + PropertySettings.SEPARATOR + benchmarks.get(n).getTypeofsource() + PropertySettings.SEPARATOR
                                + benchmarks.get(n).getWeakness() + PropertySettings.SEPARATOR + benchmarks.get(n).getLink();
                        bw.write(bug + PropertySettings.SEPARATOR + score + PropertySettings.SEPARATOR + record + "\n");
                        //System.out.println(score);
                    }
                }
                score = 0.0;
            }
        }
    }

    /**
     * prints the n first similar reports from benchmark dataset over set threshold for each bug report using Word2Vec and cosine similarity and prints suggested tags
     */
    public void similarSourcesWord2Vec() throws Exception {

        if (similarity.getBenchmarkDataset().isEmpty()) {
            throw new Exception("Security source dataset must be set.");
        } else if (similarity.getSource().isEmpty()) {
            throw new Exception("Security source name must be set.");
        } else if (similarity.getFile().isEmpty()) {
            throw new Exception("Data file must be set.");
        } else if (similarity.getWord2vec().isEmpty()) {
            throw new Exception("Word2Vec model file must be set.");
        } else if (similarity.getDescriptionIndex() == -1) {
            throw new Exception("Description index in data file must be set.");
        } else if (similarity.getNumFeatures() == 0) {
            throw new Exception("Num features must be set.");
        }

        Word2VecSimilarity w = new Word2VecSimilarity();

        Word2Vec model = w.getWord2Vec(similarity.getWord2vec());

        List<SecurityRecord> benchmarks = getRecords(similarity.getBenchmarkDataset());
        List<Bug> bugs = getBugs(similarity.getFile(), similarity.getDescriptionIndex());

        boolean writeHeader = false;
        if (!(new File(similarity.getFile().substring(0, similarity.getFile().length() - 4) + "_word2vec_" + similarity.getSource() + ".csv").isFile())) {
            writeHeader = true;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(similarity.getFile().substring(0, similarity.getFile().length() - 4) + "_word2vec_" + similarity.getSource() + ".csv", true))) {

            if ((similarity.getBenchmarkDataset().contains("cve") && writeHeader)) {
                bw.write("Security" + PropertySettings.SEPARATOR + "Title" + PropertySettings.SEPARATOR + "Description" + PropertySettings.SEPARATOR + "Id" + PropertySettings.SEPARATOR +
                        "Date" + PropertySettings.SEPARATOR + "Cossim" + PropertySettings.SEPARATOR + "Source" + PropertySettings.SEPARATOR + "Description" + PropertySettings.SEPARATOR + "Type" + PropertySettings.SEPARATOR +
                        "Type-of-source" + PropertySettings.SEPARATOR + "Weakness" + PropertySettings.SEPARATOR + "Link" + PropertySettings.SEPARATOR + "Severity Score" + PropertySettings.SEPARATOR + "Severity" + "\n");
            } else if (writeHeader) {
                bw.write("Security" + PropertySettings.SEPARATOR + "Title" + PropertySettings.SEPARATOR + "Description" + PropertySettings.SEPARATOR + "Id" + PropertySettings.SEPARATOR + "Date" + PropertySettings.SEPARATOR + "Cossim" + PropertySettings.SEPARATOR +
                        "Source" + PropertySettings.SEPARATOR + "Description" + PropertySettings.SEPARATOR + "Type" + PropertySettings.SEPARATOR + "Type-of-source" + PropertySettings.SEPARATOR + "Weakness" + PropertySettings.SEPARATOR + "Link" + "\n");
            }

            List<Collection<String>> benchmarkSentences = new ArrayList<>();
            w.getSentences(similarity.getBenchmarkDataset(), benchmarkSentences, similarity.getDescriptionIndex());

            List<Collection<String>> bugSentences = new ArrayList<>();
            w.getSentences(similarity.getFile(), bugSentences, similarity.getDescriptionIndex());

            int num = 0;
            double score = 0.0;
            double cosine_sim = 0.0;
            int n = 0;
            for (int i = 0; i < bugs.size(); i++) {
                for (int j = 0; j < benchmarks.size(); j++) {
                    if (num < similarity.getNumSimilarSources()) {
                        INDArray input1_vector = w.getVector(benchmarkSentences.get(j), model, similarity.getNumFeatures());
                        INDArray input2_vector = w.getVector(bugSentences.get(i), model, similarity.getNumFeatures());

                        double dot_product = Nd4j.getBlasWrapper().dot(input1_vector, input2_vector);

                        cosine_sim = w.cosine_similarity(input1_vector.toDoubleVector(), input2_vector.toDoubleVector(), dot_product);

                        if(cosine_sim >= similarity.getThreshold()) {
                            score = cosine_sim;

                            String bug = bugs.get(i).getBug();

                            if (similarity.getBenchmarkDataset().contains("cve")) {
                                String record = benchmarks.get(n).getId() + PropertySettings.SEPARATOR + benchmarks.get(n).getDesc() + PropertySettings.SEPARATOR + benchmarks.get(n).getType() + PropertySettings.SEPARATOR + benchmarks.get(n).getTypeofsource() + PropertySettings.SEPARATOR
                                        + benchmarks.get(n).getWeakness() + PropertySettings.SEPARATOR + benchmarks.get(n).getLink() + PropertySettings.SEPARATOR + benchmarks.get(n).getSeverityScore() + PropertySettings.SEPARATOR + benchmarks.get(n).getSeverity();
                                bw.write(bug + PropertySettings.SEPARATOR + score + PropertySettings.SEPARATOR + record + "\n");
                                //System.out.println(score);
                            } else {
                                String record = benchmarks.get(n).getId() + PropertySettings.SEPARATOR + benchmarks.get(n).getDesc() + PropertySettings.SEPARATOR + benchmarks.get(n).getType() + PropertySettings.SEPARATOR + benchmarks.get(n).getTypeofsource() + PropertySettings.SEPARATOR
                                        + benchmarks.get(n).getWeakness() + PropertySettings.SEPARATOR + benchmarks.get(n).getLink();
                                bw.write(bug + PropertySettings.SEPARATOR + score + PropertySettings.SEPARATOR + record + "\n");
                                //System.out.println(score);
                            }
                            num++;
                        }
                    }
                }
                num = 0;
            }
        }
    }

    /**
     * gets bug reports from file
     * @param file
     * @param descIndex
     * @return List<Bug> bugs
     */
    private static List<Bug> getBugs(String file, int descIndex) {
        List<Bug> bugs = new ArrayList<>();

        String desc = "";
        String line = ";";
        try (BufferedReader bw = new BufferedReader(new FileReader(file))) {
            int i = 0;
            while ((line = bw.readLine()) != null) {
                if (PropertySettings.HEADER && i == 0) {
                    i++;
                    continue;
                }

                String[] cols = line.split(PropertySettings.SEPARATOR);
                desc = cols[descIndex];

                Bug bug = new Bug(line, desc);

                bugs.add(bug);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bugs;
    }

    /**
     * gets security source records from file
     * @param file
     * @return List<SecurityRecord> records
     */
    private static List<SecurityRecord> getRecords(String file) {
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
                if (PropertySettings.HEADER && i == 0) {
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
                    } catch (Exception e) {
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
                    } catch (Exception e) {
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
