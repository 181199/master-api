package sources;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import similarity.TFIDFSimilarity;
import similarity.Word2VecSimilarity;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

public class StackExchangeAPI {

    private static StringBuilder builder;

    public static void main(String[] args) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        String newFile = "./files/experiments/stackoverflow_CWE/stackoverflow_SR.csv";
        boolean appendScoreToCSV = true;

        String tags = "security";
        String site = "stackoverflow";
        int numPages = 10;
        String dataset = "/Users/anja/Desktop/master/api/files/sources/cwe.csv";
        String terms = "/Users/anja/Desktop/master/api/files/features/CWEFeaturesTFIDF.txt";
        String word2vec = "/Users/anja/Desktop/master/api/files/features/cwe_word2vec_model.txt";

        //getNSRsWithThreshold(newFile, dataset, terms, word2vec,  site, 0.3, 10, true);
        getSRsWithThreshold(newFile, dataset, terms, word2vec, site, tags, 0.7, 10, true);
        //getNSRsWithThresholdWord2Vec(newFile, dataset, word2vec, site, 0.3, 10, true);
    }

    public static void getSRs(String newFile, String site, String tags, int numPages) throws UnsupportedEncodingException {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new File(newFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        builder = new StringBuilder();
        String columnNamesList = "Title;Description;Id;Date";
        builder.append(columnNamesList +"\n");

        int page = 1;
        while(page <= numPages) {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet("https://api.stackexchange.com/2.2/questions?page=" + page + "&pagesize=100&order=desc&sort=activity&tagged=" + URLEncoder.encode(tags, "UTF-8") + "&site=" + site + "&filter=!--1nZwT3Ejsm&key=IT8vJtd)vD02vi1lzs5mHg((");

            try {
                HttpResponse response = client.execute(request);
                HttpEntity entity = response.getEntity();

                // Read the contents of an entity and return it as a String.
                String content = EntityUtils.toString(entity);
                System.out.println(content);

                JSONObject result = new JSONObject(content);

                JSONArray tokenList = result.getJSONArray("items");
                for (int i = 0; i < tokenList.length(); i++) {
                    JSONObject oj = tokenList.getJSONObject(i);
                    String title = oj.getString("title");
                    title = title.replace(";", "");

                    int id = oj.getInt("question_id");
                    int date = oj.getInt("creation_date");
                    Date time = new Date((long) date * 1000);

                    String newTime = new SimpleDateFormat("dd-MM-yyyy").format(time);

                    String body = oj.getString("body");
                    String cleanText = html2text(body);
                    cleanText = cleanText.replace("\n", "").replace("\r", "").replace(";", "");

                    //System.out.println("Title: " + title + "\nId: " + id + "\nDate: " + newTime + "\nBody: " + cleanText + "\n");

                    // get answers to question
//                int answerNumber = oj.getInt("answer_count");
//                if(answerNumber != 0) {
//                    JSONArray answers = oj.getJSONArray("answers");
//
//                    for(int j = 0; j < answerNumber; j++){
//                        JSONObject answerObj = answers.getJSONObject(j);
//                        String answer = answerObj.getString("body");
//                        System.out.println("Answer " + (j+1) + ": " + answer);
//                    }
//                }

                    builder.append(title + ";");
                    builder.append(cleanText + ";");
                    builder.append(id + ";");
                    builder.append(newTime);
                    builder.append('\n');
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            page++;
        }
        pw.write(builder.toString());
        pw.close();
        System.out.println("done!");
    }

    public static void getNSRs(String newFile, String site, int numPages) throws UnsupportedEncodingException {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new File(newFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        builder = new StringBuilder();
        String columnNamesList = "Title;Description;Id;Date";
        builder.append(columnNamesList +"\n");

        int page = 1;
        while(page <= numPages) {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet("https://api.stackexchange.com/2.2/questions?page=" + page + "&pagesize=100&order=desc&sort=activity&&site=" + site + "&filter=!--1nZwT3Ejsm&key=IT8vJtd)vD02vi1lzs5mHg((");

            try {
                HttpResponse response = client.execute(request);
                HttpEntity entity = response.getEntity();

                // Read the contents of an entity and return it as a String.
                String content = EntityUtils.toString(entity);
                System.out.println(content);

                JSONObject result = new JSONObject(content);

                boolean security = false;

                JSONArray tokenList = result.getJSONArray("items");
                for (int i = 0; i < tokenList.length(); i++) {
                    JSONObject oj = tokenList.getJSONObject(i);
                    String title = oj.getString("title");
                    title = title.replace(";", "");

                    JSONArray tags = oj.getJSONArray("tags");
                    for (int j = 0; j < tags.length(); j++) {
                        if (tags.get(j).toString().equals("security")) {
                            security = true;
                        }
                    }

                    int id = oj.getInt("question_id");
                    int date = oj.getInt("creation_date");
                    Date time = new Date((long) date * 1000);

                    String newTime = new SimpleDateFormat("dd-MM-yyyy").format(time);

                    String body = oj.getString("body");
                    String cleanText = html2text(body);
                    cleanText = cleanText.replace("\n", "").replace("\r", "").replace(";", "");

                    //System.out.println("Title: " + title + "\nId: " + id + "\nDate: " + newTime + "\nBody: " + cleanText + "\n");

                    // get answers to question
//                int answerNumber = oj.getInt("answer_count");
//                if(answerNumber != 0) {
//                    JSONArray answers = oj.getJSONArray("answers");
//
//                    for(int j = 0; j < answerNumber; j++){
//                        JSONObject answerObj = answers.getJSONObject(j);
//                        String answer = answerObj.getString("body");
//                        System.out.println("Answer " + (j+1) + ": " + answer);
//                    }
//                }

                    if (!security) {
                        builder.append(title + ";");
                        builder.append(cleanText + ";");
                        builder.append(id + ";");
                        builder.append(newTime);
                        builder.append('\n');
                    }

                    security = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            page++;
        }
        pw.write(builder.toString());
        pw.close();
        System.out.println("done!");
    }

    // using the average of the highest tfidf and word2vec score under a set threshold
    public static void getNSRsWithThreshold(String newFile, String benchmarkDataset, String terms, String word2vec, String site, double threshold, int numNSRs, boolean appendScoreToCSV) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new File(newFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        builder = new StringBuilder();
        String columnNamesList = "";
        if(appendScoreToCSV){
            columnNamesList = "Title;Description;Id;Date;cossim";
        } else {
            columnNamesList = "Title;Description;Id;Date";
        }
        builder.append(columnNamesList +"\n");

        TFIDFSimilarity d = new TFIDFSimilarity();

        List<String> features = d.getTermsFromFile(terms);

        List<String[]> cveDocsArray = d.getDocsArrayFromCsv(benchmarkDataset);
        System.out.println("done docs");
        List<double[]> tfidfDocsVectorCve = d.tfIdfCalculator(cveDocsArray, cveDocsArray, features);
        System.out.println("done tfidf");

        Word2VecSimilarity w = new Word2VecSimilarity();

        Word2Vec model = w.getWord2Vec(word2vec);

        List<Collection<String>> benchmarkSentences = new ArrayList<>();
        w.getSentences(benchmarkDataset, benchmarkSentences);
        System.out.println("done word2vec");

        int page = 1;
        int NSRs = 0;
        while(NSRs <= numNSRs) {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet("https://api.stackexchange.com/2.2/questions?page=" + page + "&pagesize=100&order=desc&sort=activity&&site=" + site + "&filter=!--1nZwT3Ejsm&key=IT8vJtd)vD02vi1lzs5mHg((");

            try {
                HttpResponse response = client.execute(request);
                HttpEntity entity = response.getEntity();

                // Read the contents of an entity and return it as a String.
                String content = EntityUtils.toString(entity);
                System.out.println(content);

                JSONObject result = new JSONObject(content);

                boolean security = false;

                JSONArray tokenList = result.getJSONArray("items");
                for (int i = 0; i < tokenList.length(); i++) {
                    JSONObject oj = tokenList.getJSONObject(i);
                    String title = oj.getString("title");
                    title = title.replace(";", "");

                    JSONArray tags = oj.getJSONArray("tags");
                    for (int j = 0; j < tags.length(); j++) {
                        if (tags.get(j).toString().equals("security")) {
                            security = true;
                        }
                    }

                    int id = oj.getInt("question_id");
                    int date = oj.getInt("creation_date");
                    Date time = new Date((long) date * 1000);

                    String newTime = new SimpleDateFormat("dd-MM-yyyy").format(time);

                    String body = oj.getString("body");
                    String cleanText = html2text(body);
                    cleanText = cleanText.replace("\n", "").replace("\r", "").replace(";", "");

                    // check cosine similarity
                    double[] cleanTextDoc = d.getDocumentVectors(cleanText, features, cveDocsArray);

                    // word2vec
                    Collection<String> sentence = w.normalizeText(cleanText);

                    double score = 0.0;
                    double tfidf = 0.0;
                    for (int k = 0; k < tfidfDocsVectorCve.size(); k++) {
                        double cosine = d.getCosineSimilarityTwoDocuments(cleanTextDoc, tfidfDocsVectorCve.get(k));

                        if (cosine > tfidf) {
                            tfidf = cosine;
                        }
                    }

                    double w2v = 0.0;
                    for (int k = 0; k < benchmarkSentences.size(); k++) {
                        INDArray input1_vector = w.getVector(benchmarkSentences.get(k), model);
                        INDArray input2_vector = w.getVector(sentence, model);

                        double dot_product = Nd4j.getBlasWrapper().dot(input1_vector, input2_vector);

                        double cosine = w.cosine_similarity(input1_vector.toDoubleVector(), input2_vector.toDoubleVector(), dot_product);

                        if (cosine > w2v) {
                            w2v = cosine;
                        }
                    }

                    score = (tfidf+w2v)/2;

                    if (score <= threshold && !security && NSRs <= numNSRs) {
                        builder.append(title + ";");
                        builder.append(cleanText + ";");
                        builder.append(id + ";");
                        builder.append(newTime);
                        if(appendScoreToCSV){
                            builder.append(";" + score);
                        }
                        builder.append('\n');

                        System.out.println("added");

                        NSRs++;
                    }
                    security = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            page++;
        }
        pw.write(builder.toString());
        pw.close();
        System.out.println("done!");
    }

    // using the average of the highest tfidf and word2vec score over a set threshold
    public static void getSRsWithThreshold(String newFile, String benchmarkDataset, String terms, String word2vec, String site, String tags, double threshold, int numSRs, boolean appendScoreToCSV) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new File(newFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        builder = new StringBuilder();
        String columnNamesList = "";
        if(appendScoreToCSV){
            columnNamesList = "Title;Description;Id;Date;cossim";
        } else {
            columnNamesList = "Title;Description;Id;Date";
        }
        builder.append(columnNamesList +"\n");

        TFIDFSimilarity d = new TFIDFSimilarity();
        List<String> features = d.getTermsFromFile(terms);

        List<String[]> cveDocsArray = d.getDocsArrayFromCsv(benchmarkDataset);
        System.out.println("done docs");
        List<double[]> tfidfDocsVectorCve = d.tfIdfCalculator(cveDocsArray, cveDocsArray, features);
        System.out.println("done tfidf");

        Word2VecSimilarity w = new Word2VecSimilarity();

        Word2Vec model = w.getWord2Vec(word2vec);

        List<Collection<String>> benchmarkSentences = new ArrayList<>();
        w.getSentences(benchmarkDataset, benchmarkSentences);
        System.out.println("done word2vec");

        int page = 1;
        int SRs = 0;
        while(SRs <= numSRs) {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet("https://api.stackexchange.com/2.2/questions?page=" + page + "&pagesize=100&order=desc&sort=activity&tagged=" + URLEncoder.encode(tags, "UTF-8") + "&site=" + site + "&filter=!--1nZwT3Ejsm&key=IT8vJtd)vD02vi1lzs5mHg((");

            try {
                HttpResponse response = client.execute(request);
                HttpEntity entity = response.getEntity();

                // Read the contents of an entity and return it as a String.
                String content = EntityUtils.toString(entity);
                System.out.println(content);

                JSONObject result = new JSONObject(content);

                JSONArray tokenList = result.getJSONArray("items");
                for (int i = 0; i < tokenList.length(); i++) {
                    JSONObject oj = tokenList.getJSONObject(i);
                    String title = oj.getString("title");
                    title = title.replace(";", "");

                    int id = oj.getInt("question_id");
                    int date = oj.getInt("creation_date");
                    Date time = new Date((long) date * 1000);

                    String newTime = new SimpleDateFormat("dd-MM-yyyy").format(time);

                    String body = oj.getString("body");
                    String cleanText = html2text(body);
                    cleanText = cleanText.replace("\n", "").replace("\r", "").replace(";", "");

                    // check cosine similarity
                    double[] cleanTextDoc = d.getDocumentVectors(cleanText, features, cveDocsArray);

                    // word2vec
                    Collection<String> sentence = w.normalizeText(cleanText);

                    double score = 0.0;
                    double tfidf = 0.0;
                    for (int k = 0; k < tfidfDocsVectorCve.size(); k++) {
                        double cosine = d.getCosineSimilarityTwoDocuments(cleanTextDoc, tfidfDocsVectorCve.get(k));

                        if (cosine > tfidf) {
                            tfidf = cosine;
                        }
                    }

                    double w2v = 0.0;
                    for (int k = 0; k < benchmarkSentences.size(); k++) {
                        INDArray input1_vector = w.getVector(benchmarkSentences.get(k), model);
                        INDArray input2_vector = w.getVector(sentence, model);

                        double dot_product = Nd4j.getBlasWrapper().dot(input1_vector, input2_vector);

                        double cosine = w.cosine_similarity(input1_vector.toDoubleVector(), input2_vector.toDoubleVector(), dot_product);

                        if (cosine > w2v) {
                            w2v = cosine;
                        }
                    }

                    score = (tfidf+w2v)/2;

                    if (score >= threshold && SRs <= numSRs) {
                        System.out.println(score);

                        builder.append(title + ";");
                        builder.append(cleanText + ";");
                        builder.append(id + ";");
                        builder.append(newTime);
                        if(appendScoreToCSV){
                            builder.append(";" + score);
                        }
                        builder.append('\n');

                        System.out.println("added");

                        SRs++;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            page++;
        }
        pw.write(builder.toString());
        pw.close();
        System.out.println("done!");
    }

    public static void getNSRsWithThresholdTFIDF(String newFile, String benchmarkDataset, String terms, String site, double threshold, int numNSRs, boolean appendScoreToCSV) throws IOException {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new File(newFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        builder = new StringBuilder();
        String columnNamesList = "";
        if(appendScoreToCSV){
            columnNamesList = "Title;Description;Id;Date;tfidf";
        } else {
            columnNamesList = "Title;Description;Id;Date";
        }
        builder.append(columnNamesList +"\n");

        TFIDFSimilarity d = new TFIDFSimilarity();

        List<String> features = d.getTermsFromFile(terms);

        List<String[]> cveDocsArray = d.getDocsArrayFromCsv(benchmarkDataset);
        System.out.println("done docs");
        List<double[]> tfidfDocsVectorCve = d.tfIdfCalculator(cveDocsArray, cveDocsArray, features);
        System.out.println("done tfidf");

        int page = 1;
        int NSRs = 0;
        while(NSRs <= numNSRs) {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet("https://api.stackexchange.com/2.2/questions?page=" + page + "&pagesize=100&order=desc&sort=activity&&site=" + site + "&filter=!--1nZwT3Ejsm&key=IT8vJtd)vD02vi1lzs5mHg((");

            try {
                HttpResponse response = client.execute(request);
                HttpEntity entity = response.getEntity();

                // Read the contents of an entity and return it as a String.
                String content = EntityUtils.toString(entity);
                System.out.println(content);

                JSONObject result = new JSONObject(content);

                boolean security = false;

                JSONArray tokenList = result.getJSONArray("items");
                for (int i = 0; i < tokenList.length(); i++) {
                    JSONObject oj = tokenList.getJSONObject(i);
                    String title = oj.getString("title");
                    title = title.replace(";", "");

                    JSONArray tags = oj.getJSONArray("tags");
                    for (int j = 0; j < tags.length(); j++) {
                        if (tags.get(j).toString().equals("security")) {
                            security = true;
                        }
                    }

                    int id = oj.getInt("question_id");
                    int date = oj.getInt("creation_date");
                    Date time = new Date((long) date * 1000);

                    String newTime = new SimpleDateFormat("dd-MM-yyyy").format(time);

                    String body = oj.getString("body");
                    String cleanText = html2text(body);
                    cleanText = cleanText.replace("\n", "").replace("\r", "").replace(";", "");

                    // check cosine similarity
                    double[] cleanTextDoc = d.getDocumentVectors(cleanText, features, cveDocsArray);

                    double score = 0.0;
                    double cosine = 0.0;
                    for (int k = 0; k < tfidfDocsVectorCve.size(); k++) {
                        cosine = d.getCosineSimilarityTwoDocuments(cleanTextDoc, tfidfDocsVectorCve.get(k));

                        if (cosine > score) {
                            score = cosine;
                        }
                    }

                    if (score <= threshold && !security) {
                        builder.append(title + ";");
                        builder.append(cleanText + ";");
                        builder.append(id + ";");
                        builder.append(newTime);
                        if(appendScoreToCSV){
                            builder.append(";" + score);
                        }
                        builder.append('\n');

                        System.out.println("added");

                        NSRs++;

                        if(NSRs == 500){
                            continue;
                        }
                    }
                    security = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            page++;
        }
        pw.write(builder.toString());
        pw.close();
        System.out.println("done!");
    }

    public static void getSRsWithThresholdTFIDF(String newFile, String benchmarkDataset, String terms, String site, String tags, double threshold, int numSRs, boolean appendScoreToCSV) throws IOException {

        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new File(newFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        builder = new StringBuilder();
        String columnNamesList = "";
        if(appendScoreToCSV){
            columnNamesList = "Title;Description;Id;Date;tfidf";
        } else {
            columnNamesList = "Title;Description;Id;Date";
        }
        builder.append(columnNamesList +"\n");

        TFIDFSimilarity d = new TFIDFSimilarity();
        List<String> features = d.getTermsFromFile(terms);

        List<String[]> cveDocsArray = d.getDocsArrayFromCsv(benchmarkDataset);
        System.out.println("done docs");
        List<double[]> tfidfDocsVectorCve = d.tfIdfCalculator(cveDocsArray, cveDocsArray, features);
        System.out.println("done tfidf");

        int page = 1;
        int SRs = 0;
        while(SRs <= numSRs) {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet("https://api.stackexchange.com/2.2/questions?page=" + page + "&pagesize=100&order=desc&sort=activity&tagged=" + URLEncoder.encode(tags, "UTF-8") + "&site=" + site + "&filter=!--1nZwT3Ejsm&key=IT8vJtd)vD02vi1lzs5mHg((");

            try {
                HttpResponse response = client.execute(request);
                HttpEntity entity = response.getEntity();

                // Read the contents of an entity and return it as a String.
                String content = EntityUtils.toString(entity);
                System.out.println(content);

                JSONObject result = new JSONObject(content);

                JSONArray tokenList = result.getJSONArray("items");
                for (int i = 0; i < tokenList.length(); i++) {
                    JSONObject oj = tokenList.getJSONObject(i);
                    String title = oj.getString("title");
                    title = title.replace(";", "");

                    int id = oj.getInt("question_id");
                    int date = oj.getInt("creation_date");
                    Date time = new Date((long) date * 1000);

                    String newTime = new SimpleDateFormat("dd-MM-yyyy").format(time);

                    String body = oj.getString("body");
                    String cleanText = html2text(body);
                    cleanText = cleanText.replace("\n", "").replace("\r", "").replace(";", "");

                    // check cosine similarity
                    double[] cleanTextDoc = d.getDocumentVectors(cleanText, features, cveDocsArray);

                    double score = 0.0;
                    double cosine = 0.0;
                    for (int k = 0; k < tfidfDocsVectorCve.size(); k++) {
                        cosine = d.getCosineSimilarityTwoDocuments(cleanTextDoc, tfidfDocsVectorCve.get(k));

                        if (cosine > score) {
                            score = cosine;
                        }
                    }

                    if (score >= threshold) {
                        System.out.println(score);

                        builder.append(title + ";");
                        builder.append(cleanText + ";");
                        builder.append(id + ";");
                        builder.append(newTime);
                        if(appendScoreToCSV){
                            builder.append(";" + score);
                        }
                        builder.append('\n');

                        System.out.println("added");

                        SRs++;

                        if(SRs == 500){
                            continue;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        pw.write(builder.toString());
        pw.close();
        System.out.println("done!");
    }

    public static void getNSRsWithThresholdWord2Vec(String newFile, String benchmarkDataset, String word2vec, String site, double threshold, int numNSRs, boolean appendScoreToCSV) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new File(newFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        builder = new StringBuilder();
        String columnNamesList = "";
        if(appendScoreToCSV){
            columnNamesList = "Title;Description;Id;Date;cossim";
        } else {
            columnNamesList = "Title;Description;Id;Date";
        }
        builder.append(columnNamesList +"\n");

        Word2VecSimilarity w = new Word2VecSimilarity();

        Word2Vec model = w.getWord2Vec(word2vec);

        List<Collection<String>> benchmarkSentences = new ArrayList<>();
        w.getSentences(benchmarkDataset, benchmarkSentences);
        System.out.println("word2vec done");

        int page = 1;
        int NSRs = 0;
        while(NSRs <= numNSRs) {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet("https://api.stackexchange.com/2.2/questions?page=" + page + "&pagesize=100&order=desc&sort=activity&&site=" + site + "&filter=!--1nZwT3Ejsm&key=IT8vJtd)vD02vi1lzs5mHg((");

            try {
                HttpResponse response = client.execute(request);
                HttpEntity entity = response.getEntity();

                // Read the contents of an entity and return it as a String.
                String content = EntityUtils.toString(entity);
                System.out.println(content);

                JSONObject result = new JSONObject(content);

                boolean security = false;

                JSONArray tokenList = result.getJSONArray("items");
                for (int i = 0; i < tokenList.length(); i++) {
                    JSONObject oj = tokenList.getJSONObject(i);
                    String title = oj.getString("title");
                    title = title.replace(";", "");

                    JSONArray tags = oj.getJSONArray("tags");
                    for (int j = 0; j < tags.length(); j++) {
                        if (tags.get(j).toString().equals("security")) {
                            security = true;
                        }
                    }

                    int id = oj.getInt("question_id");
                    int date = oj.getInt("creation_date");
                    Date time = new Date((long) date * 1000);

                    String newTime = new SimpleDateFormat("dd-MM-yyyy").format(time);

                    String body = oj.getString("body");
                    String cleanText = html2text(body);
                    cleanText = cleanText.replace("\n", "").replace("\r", "").replace(";", "");

                    Collection<String> sentence = w.normalizeText(cleanText);

                    double score = 0.0;
                    double cosine = 0.0;
                    for (int k = 0; k < benchmarkSentences.size(); k++) {
                        INDArray input1_vector = w.getVector(benchmarkSentences.get(k), model);
                        INDArray input2_vector = w.getVector(sentence, model);

                        double dot_product = Nd4j.getBlasWrapper().dot(input1_vector, input2_vector);

                        cosine = w.cosine_similarity(input1_vector.toDoubleVector(), input2_vector.toDoubleVector(), dot_product);

                        if (cosine > score) {
                            score = cosine;
                        }
                    }

                    if (score <= threshold && !security && NSRs <= numNSRs) {
                        builder.append(title + ";");
                        builder.append(cleanText + ";");
                        builder.append(id + ";");
                        builder.append(newTime);
                        if(appendScoreToCSV){
                            builder.append(";" + score);
                        }
                        builder.append('\n');

                        System.out.println("added");

                        NSRs++;

                        if(NSRs == numNSRs){
                            continue;
                        }
                    }
                    security = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            page++;
        }
        pw.write(builder.toString());
        pw.close();
        System.out.println("done!");
    }

    public static void getSRsWithThresholdWord2Vec(String newFile, String benchmarkDataset, String word2vec, String site, String tags, double threshold, int numSRs, boolean appendScoreToCSV) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new File(newFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        builder = new StringBuilder();
        String columnNamesList = "";
        if(appendScoreToCSV){
            columnNamesList = "Title;Description;Id;Date;cossim";
        } else {
            columnNamesList = "Title;Description;Id;Date";
        }
        builder.append(columnNamesList +"\n");

        Word2VecSimilarity w = new Word2VecSimilarity();

        Word2Vec model = w.getWord2Vec(word2vec);

        List<Collection<String>> benchmarkSentences = new ArrayList<>();
        w.getSentences(benchmarkDataset, benchmarkSentences);
        System.out.println("word2vec done");

        int page = 1;
        int SRs = 0;
        while(SRs <= numSRs) {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet("https://api.stackexchange.com/2.2/questions?page=" + page + "&pagesize=100&order=desc&sort=activity&tagged=" + URLEncoder.encode(tags, "UTF-8") + "&site=" + site + "&filter=!--1nZwT3Ejsm&key=IT8vJtd)vD02vi1lzs5mHg((");

            try {
                HttpResponse response = client.execute(request);
                HttpEntity entity = response.getEntity();

                // Read the contents of an entity and return it as a String.
                String content = EntityUtils.toString(entity);
                System.out.println(content);

                JSONObject result = new JSONObject(content);

                JSONArray tokenList = result.getJSONArray("items");
                for (int i = 0; i < tokenList.length(); i++) {
                    JSONObject oj = tokenList.getJSONObject(i);
                    String title = oj.getString("title");
                    title = title.replace(";", "");

                    int id = oj.getInt("question_id");
                    int date = oj.getInt("creation_date");
                    Date time = new Date((long) date * 1000);

                    String newTime = new SimpleDateFormat("dd-MM-yyyy").format(time);

                    String body = oj.getString("body");
                    String cleanText = html2text(body);
                    cleanText = cleanText.replace("\n", "").replace("\r", "").replace(";", "");

                    Collection<String> sentence = w.normalizeText(cleanText);

                    double score = 0.0;
                    double cosine = 0.0;
                    for (int k = 0; k < benchmarkSentences.size(); k++) {
                        INDArray input1_vector = w.getVector(benchmarkSentences.get(k), model);
                        INDArray input2_vector = w.getVector(sentence, model);

                        double dot_product = Nd4j.getBlasWrapper().dot(input1_vector, input2_vector);

                        cosine = w.cosine_similarity(input1_vector.toDoubleVector(), input2_vector.toDoubleVector(), dot_product);

                        if (cosine > score) {
                            score = cosine;
                        }
                    }

                    if (score >= threshold && SRs <= numSRs) {
                        System.out.println(score);

                        builder.append(title + ";");
                        builder.append(cleanText + ";");
                        builder.append(id + ";");
                        builder.append(newTime);
                        if(appendScoreToCSV){
                            builder.append(";" + score);
                        }
                        builder.append('\n');

                        System.out.println("added");

                        SRs++;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        pw.write(builder.toString());
        pw.close();
        System.out.println("done!");
    }

    public static String html2text(String html) {
        return Jsoup.parse(html).text();
    }
}
