package sources;

import machinelearning.utils.Cleanup;
import machinelearning.utils.MergeFiles;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
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
import java.util.regex.Pattern;

public class StackExchangeAPI {

    private static StringBuilder builder;
    public static final String STACKOVERFLOW = "stackoverflow";
    public static final String ASKUBUNTU = "askubuntu";
    public static final String SERVERFAULT = "severfault";
    public static final String SOFTWAREENGINEERING = "softwareengineering";

    public static void main(String[] args) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        MergeFiles m = new MergeFiles();

        String tags = "security";
        String site = "stackoverflow";
        String source = "CVE";
        int numPages = 10;
        String dataset = "/Users/anja/Desktop/master/api/files/sources/";
        String terms = "/Users/anja/Desktop/master/api/files/features/";
        String word2vec = "/Users/anja/Desktop/master/api/files/features/";

        String path = "./files/experiments/tfidf/";

//        getNSRs("./files/experiments/NSR.csv", site, 1000, true);

    }

    public static void getSRs(String newFile, String site, int numSRs, boolean getAnswers) throws UnsupportedEncodingException {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new File(newFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        builder = new StringBuilder();
        String columnNamesList = "Title;Description;Id;Date";
        builder.append(columnNamesList + "\n");

        Boolean hasMore = true;
        int page = 1;
        int SRs = 0;
        while (SRs < numSRs) {
            HttpClient client = HttpClientBuilder.create()
                    .setDefaultRequestConfig(RequestConfig.custom()
                            .setCookieSpec(CookieSpecs.STANDARD).build())
                    .build();
            HttpGet request = new HttpGet("https://api.stackexchange.com/2.2/questions?page=" + page + "&pagesize=100&order=desc&sort=activity&site=" + site + "&filter=!--1nZwT3Ejsm&key=IT8vJtd)vD02vi1lzs5mHg((");

            try {
                HttpResponse response = client.execute(request);
                HttpEntity entity = response.getEntity();

                // Read the contents of an entity and return it as a String.
                String content = EntityUtils.toString(entity);
                System.out.println(content);

                JSONObject result = new JSONObject(content);

                try {
                    hasMore = result.getBoolean("has_more");
                } catch (Exception e) {
                    hasMore = false;
                }

                boolean security = false;
                JSONArray tokenList = result.getJSONArray("items");
                for (int i = 0; i < tokenList.length(); i++) {
                    JSONObject oj = tokenList.getJSONObject(i);

                    JSONArray tags = oj.getJSONArray("tags");
                    for (int t = 0; t < tags.length(); t++) {
                        if (tags.get(t).toString().contains("security")) {
                            security = true;
                            break;
                        }
                    }

                    if (security && SRs < numSRs) {
                        String title = oj.getString("title");
                        title = title.replace(";", "");

                        int id = oj.getInt("question_id");
                        int date = oj.getInt("creation_date");
                        Date time = new Date((long) date * 1000);

                        String newTime = new SimpleDateFormat("dd-MM-yyyy").format(time);

                        String body = oj.getString("body");
                        String cleanText = html2text(body);

                        Boolean is_answered = oj.getBoolean("is_answered");
                        if (getAnswers && is_answered) {
                            int answerNumber = oj.getInt("answer_count");
                            if (answerNumber != 0) {
                                JSONArray answers = oj.getJSONArray("answers");

                                for (int j = 0; j < answerNumber; j++) {
                                    JSONObject answerObj = answers.getJSONObject(j);
                                    String answer = answerObj.getString("body");
                                    cleanText = cleanText + " " + html2text(answer);
                                }
                            }
                        }

                        cleanText = cleanText.replace("\n", "").replace("\r", "").replace(";", "");

                        builder.append(title + ";");
                        builder.append(cleanText + ";");
                        builder.append(id + ";");
                        builder.append(newTime);
                        builder.append('\n');
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (hasMore) {
                page++;
            } else {
                break;
            }
        }
        pw.write(builder.toString());
        pw.close();
        System.out.println("done!");
    }

    public static void getNSRs(String newFile, String site, int numNSRs, boolean getAnswers) throws UnsupportedEncodingException {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new File(newFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        builder = new StringBuilder();
        String columnNamesList = "Title;Description;Id;Date";
        builder.append(columnNamesList + "\n");

        boolean hasMore = true;
        int page = 1;
        int NSRs = 0;
        while (NSRs < numNSRs) {
            HttpClient client = HttpClientBuilder.create()
                    .setDefaultRequestConfig(RequestConfig.custom()
                            .setCookieSpec(CookieSpecs.STANDARD).build())
                    .build();
            HttpGet request = new HttpGet("https://api.stackexchange.com/2.2/questions?page=" + page + "&pagesize=100&order=desc&sort=activity&&site=" + site + "&filter=!--1nZwT3Ejsm&key=IT8vJtd)vD02vi1lzs5mHg((");

            try {
                HttpResponse response = client.execute(request);
                HttpEntity entity = response.getEntity();

                // Read the contents of an entity and return it as a String.
                String content = EntityUtils.toString(entity);

                JSONObject result = new JSONObject(content);

                hasMore = result.getBoolean("has_more");

                boolean security = false;

                JSONArray tokenList = result.getJSONArray("items");
                for (int i = 0; i < tokenList.length(); i++) {
                    JSONObject oj = tokenList.getJSONObject(i);
                    String title = oj.getString("title");
                    title = title.replace(";", "");

                    JSONArray tags = oj.getJSONArray("tags");
                    for (int j = 0; j < tags.length(); j++) {
                        if (tags.get(j).toString().contains("security")) {
                            security = true;
                            break;
                        }
                    }

                    if (!security && NSRs < numNSRs) {
                        int id = oj.getInt("question_id");
                        int date = oj.getInt("creation_date");
                        Date time = new Date((long) date * 1000);

                        String newTime = new SimpleDateFormat("dd-MM-yyyy").format(time);

                        String body = oj.getString("body");
                        String cleanText = html2text(body);

                        Boolean is_answered = oj.getBoolean("is_answered");
                        if (getAnswers && is_answered) {
                            int answerNumber = oj.getInt("answer_count");
                            if (answerNumber != 0) {
                                JSONArray answers = oj.getJSONArray("answers");

                                for (int j = 0; j < answerNumber; j++) {
                                    JSONObject answerObj = answers.getJSONObject(j);
                                    String answer = answerObj.getString("body");
                                    cleanText = cleanText + " " + html2text(answer);
                                }
                            }
                        }

                        cleanText = cleanText.replace("\n", "").replace("\r", "").replace(";", "");

                        builder.append(title + ";");
                        builder.append(cleanText + ";");
                        builder.append(id + ";");
                        builder.append(newTime);
                        builder.append('\n');
                        NSRs++;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (hasMore) {
                page++;
            } else {
                break;
            }
        }
        pw.write(builder.toString());
        pw.close();
        System.out.println("done!");
    }

    // using the average of the highest tfidf and word2vec score under a set threshold
    public static void getNSRsWithThreshold(String newFile, String benchmarkDataset, String terms, String word2vec, String site, double threshold, int numNSRs, boolean getAnswers, boolean getAnswersWithThreshold, boolean appendScoreToCSV) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new File(newFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        builder = new StringBuilder();
        String columnNamesList = "";
        if (appendScoreToCSV) {
            columnNamesList = "Title;Description;Id;Date;cossim";
        } else {
            columnNamesList = "Title;Description;Id;Date";
        }
        builder.append(columnNamesList + "\n");

        TFIDFSimilarity d = new TFIDFSimilarity();

        List<String> features = d.getTermsFromFile(terms);

        List<String[]> docsArray = d.getDocsArrayFromCsv(benchmarkDataset);
        List<double[]> tfidfDocsVector = d.tfIdfCalculator(docsArray, docsArray, features);

        Word2VecSimilarity w = new Word2VecSimilarity();

        Word2Vec model = w.getWord2Vec(word2vec);

        List<Collection<String>> benchmarkSentences = new ArrayList<>();
        w.getSentences(benchmarkDataset, benchmarkSentences);
        List<INDArray> input_vectors = new ArrayList<>();

        for (int m = 0; m < benchmarkSentences.size(); m++) {
            input_vectors.add(w.getVector(benchmarkSentences.get(m), model));
        }

        Boolean hasMore = true;
        int page = 1;
        int NSRs = 0;
        while (NSRs < numNSRs) {
            HttpClient client = HttpClientBuilder.create()
                    .setDefaultRequestConfig(RequestConfig.custom()
                            .setCookieSpec(CookieSpecs.STANDARD).build())
                    .build();
            HttpGet request = new HttpGet("https://api.stackexchange.com/2.2/questions?page=" + page + "&pagesize=100&order=desc&sort=activity&&site=" + site + "&filter=!--1nZwT3Ejsm&key=IT8vJtd)vD02vi1lzs5mHg((");

            try {
                HttpResponse response = client.execute(request);
                HttpEntity entity = response.getEntity();

                // Read the contents of an entity and return it as a String.
                String content = EntityUtils.toString(entity);

                JSONObject result = new JSONObject(content);

                try {
                    hasMore = result.getBoolean("has_more");
                } catch (Exception e) {
                    hasMore = false;
                }

                boolean security = false;

                JSONArray tokenList = result.getJSONArray("items");

                for (int i = 0; i < tokenList.length(); i++) {
                    JSONObject oj = tokenList.getJSONObject(i);
                    String title = oj.getString("title");
                    title = title.replace(";", "");

                    JSONArray tags = oj.getJSONArray("tags");
                    for (int j = 0; j < tags.length(); j++) {
                        if (tags.get(j).toString().contains("security")) {
                            security = true;
                        }
                    }

                    if (!security && NSRs < numNSRs) {
                        int id = oj.getInt("question_id");
                        int date = oj.getInt("creation_date");
                        Date time = new Date((long) date * 1000);

                        String newTime = new SimpleDateFormat("dd-MM-yyyy").format(time);

                        String body = oj.getString("body");
                        String cleanText = html2text(body);

                        List<String> postAnswers = new ArrayList<>();
                        Boolean is_answered = oj.getBoolean("is_answered");
                        if (getAnswers && is_answered) {
                            int answerNumber = oj.getInt("answer_count");
                            if (answerNumber != 0) {
                                JSONArray answers = oj.getJSONArray("answers");

                                for (int j = 0; j < answerNumber; j++) {
                                    JSONObject answerObj = answers.getJSONObject(j);
                                    String answer = answerObj.getString("body");
                                    postAnswers.add(new Cleanup().cleanText(html2text(answer)).replace("\n", "").replace("\r", "").replace(";", ""));
                                    if (!getAnswersWithThreshold) {
                                        cleanText = cleanText + " " + html2text(answer);
                                    }
                                }
                            }
                        }

                        cleanText = cleanText.replace("\n", "").replace("\r", "").replace(";", "");

                        double tfidf = getTFIDFScore(cleanText, features, docsArray, tfidfDocsVector, d);

                        double w2v = getWord2VecScore(cleanText, benchmarkSentences, input_vectors, model, w);

                        double score = (tfidf + w2v) / 2;

                        if (score <= threshold && !security && NSRs < numNSRs) {
                            if (!postAnswers.isEmpty() && getAnswersWithThreshold) {
                                for (int p = 0; p < postAnswers.size(); p++) {
                                    double tfidfAnswer = getTFIDFScore(postAnswers.get(p), features, docsArray, tfidfDocsVector, d);
                                    double w2vAnswer = getWord2VecScore(postAnswers.get(p), benchmarkSentences, input_vectors, model, w);

                                    double answerScore = (tfidfAnswer + w2vAnswer) / 2;
                                    if (answerScore <= threshold) {
                                        cleanText = cleanText + " " + postAnswers.get(p);
                                    }
                                }
                            }

                            builder.append(title + ";");
                            builder.append(cleanText + ";");
                            builder.append(id + ";");
                            builder.append(newTime);
                            if (appendScoreToCSV) {
                                builder.append(";" + score);
                            }
                            builder.append('\n');

                            NSRs++;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (hasMore) {
                page++;
            } else {
                break;
            }
        }
        pw.write(builder.toString());
        pw.close();
        System.out.println("done!");
    }

    // using the average of the highest tfidf and word2vec score over a set threshold
    public static void getSRsWithThreshold(String newFile, String benchmarkDataset, String terms, String word2vec, String site, double threshold, int numSRs, boolean getAnswers, boolean getAnswersWithThreshold, boolean appendScoreToCSV) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new File(newFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        builder = new StringBuilder();
        String columnNamesList = "";
        if (appendScoreToCSV) {
            columnNamesList = "Title;Description;Id;Date;cossim";
        } else {
            columnNamesList = "Title;Description;Id;Date";
        }
        builder.append(columnNamesList + "\n");

        TFIDFSimilarity d = new TFIDFSimilarity();
        List<String> features = d.getTermsFromFile(terms);

        List<String[]> docsArray = d.getDocsArrayFromCsv(benchmarkDataset);
        List<double[]> tfidfDocsVector = d.tfIdfCalculator(docsArray, docsArray, features);

        Word2VecSimilarity w = new Word2VecSimilarity();

        Word2Vec model = w.getWord2Vec(word2vec);

        List<Collection<String>> benchmarkSentences = new ArrayList<>();
        w.getSentences(benchmarkDataset, benchmarkSentences);
        List<INDArray> input_vectors = new ArrayList<>();

        for (int m = 0; m < benchmarkSentences.size(); m++) {
            input_vectors.add(w.getVector(benchmarkSentences.get(m), model));
        }

        Boolean hasMore = true;
        int page = 1;
        int SRs = 0;
        while (SRs < numSRs) {
            HttpClient client = HttpClientBuilder.create()
                    .setDefaultRequestConfig(RequestConfig.custom()
                            .setCookieSpec(CookieSpecs.STANDARD).build())
                    .build();
            HttpGet request = new HttpGet("https://api.stackexchange.com/2.2/questions?page=" + page + "&pagesize=100&order=desc&sort=activity&site=" + site + "&filter=!--1nZwT3Ejsm&key=IT8vJtd)vD02vi1lzs5mHg((");

            try {
                HttpResponse response = client.execute(request);
                HttpEntity entity = response.getEntity();

                // Read the contents of an entity and return it as a String.
                String content = EntityUtils.toString(entity);

                JSONObject result = new JSONObject(content);

                try {
                    hasMore = result.getBoolean("has_more");
                } catch (Exception e) {
                    hasMore = false;
                }

                boolean security = false;
                JSONArray tokenList = result.getJSONArray("items");
                for (int i = 0; i < tokenList.length(); i++) {
                    JSONObject oj = tokenList.getJSONObject(i);

                    JSONArray tags = oj.getJSONArray("tags");
                    for (int t = 0; t < tags.length(); t++) {
                        if (tags.get(t).toString().contains("security")) {
                            security = true;
                            break;
                        }
                    }

                    if (security && SRs < numSRs) {
                        String title = oj.getString("title");
                        title = title.replace(";", "");

                        int id = oj.getInt("question_id");
                        int date = oj.getInt("creation_date");
                        Date time = new Date((long) date * 1000);

                        String newTime = new SimpleDateFormat("dd-MM-yyyy").format(time);

                        String body = oj.getString("body");
                        String cleanText = html2text(body);

                        List<String> postAnswers = new ArrayList<>();
                        Boolean is_answered = oj.getBoolean("is_answered");
                        if (getAnswers && is_answered) {
                            int answerNumber = oj.getInt("answer_count");
                            if (answerNumber != 0) {
                                JSONArray answers = oj.getJSONArray("answers");

                                for (int j = 0; j < answerNumber; j++) {
                                    JSONObject answerObj = answers.getJSONObject(j);
                                    String answer = answerObj.getString("body");
                                    postAnswers.add(new Cleanup().cleanText(html2text(answer)).replace("\n", "").replace("\r", "").replace(";", ""));
                                    if (!getAnswersWithThreshold) {
                                        cleanText = cleanText + " " + html2text(answer);
                                    }
                                }
                            }
                        }

                        cleanText = cleanText.replace("\n", "").replace("\r", "").replace(";", "");

                        double tfidf = getTFIDFScore(cleanText, features, docsArray, tfidfDocsVector, d);

                        double w2v = getWord2VecScore(cleanText, benchmarkSentences, input_vectors, model, w);

                        double score = (tfidf + w2v) / 2;

                        if (score >= threshold && SRs < numSRs) {
                            if (!postAnswers.isEmpty() && getAnswersWithThreshold) {
                                for (int p = 0; p < postAnswers.size(); p++) {
                                    double tfidfAnswer = getTFIDFScore(postAnswers.get(p), features, docsArray, tfidfDocsVector, d);
                                    double w2vAnswer = getWord2VecScore(postAnswers.get(p), benchmarkSentences, input_vectors, model, w);

                                    double answerScore = (tfidfAnswer + w2vAnswer) / 2;
                                    if (answerScore >= threshold) {
                                        cleanText = cleanText + " " + postAnswers.get(p);
                                    }
                                }
                            }

                            builder.append(title + ";");
                            builder.append(cleanText + ";");
                            builder.append(id + ";");
                            builder.append(newTime);
                            if (appendScoreToCSV) {
                                builder.append(";" + score);
                            }
                            builder.append('\n');

                            SRs++;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (hasMore) {
                page++;
            } else {
                break;
            }
        }
        pw.write(builder.toString());
        pw.close();
        System.out.println("done!");
    }

    public static void getNSRsWithThresholdTFIDF(String newFile, String benchmarkDataset, String terms, String site, double threshold, int numNSRs, boolean getAnswers, boolean getAnswersWithThreshold, boolean appendScoreToCSV) throws IOException {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new File(newFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        builder = new StringBuilder();
        String columnNamesList = "";
        if (appendScoreToCSV) {
            columnNamesList = "Title;Description;Id;Date;cossim";
        } else {
            columnNamesList = "Title;Description;Id;Date";
        }
        builder.append(columnNamesList + "\n");

        TFIDFSimilarity d = new TFIDFSimilarity();

        List<String> features = d.getTermsFromFile(terms);

        List<String[]> docsArray = d.getDocsArrayFromCsv(benchmarkDataset);
        List<double[]> tfidfDocsVector = d.tfIdfCalculator(docsArray, docsArray, features);

        Boolean hasMore = true;
        int page = 1;
        int NSRs = 0;
        while (NSRs < numNSRs) {
            HttpClient client = HttpClientBuilder.create()
                    .setDefaultRequestConfig(RequestConfig.custom()
                            .setCookieSpec(CookieSpecs.STANDARD).build())
                    .build();
            HttpGet request = new HttpGet("https://api.stackexchange.com/2.2/questions?page=" + page + "&pagesize=100&order=desc&sort=activity&&site=" + site + "&filter=!--1nZwT3Ejsm&key=IT8vJtd)vD02vi1lzs5mHg((");

            try {
                HttpResponse response = client.execute(request);
                HttpEntity entity = response.getEntity();

                // Read the contents of an entity and return it as a String.
                String content = EntityUtils.toString(entity);

                JSONObject result = new JSONObject(content);

                try {
                    hasMore = result.getBoolean("has_more");
                } catch (Exception e) {
                    hasMore = false;
                }

                boolean security = false;

                JSONArray tokenList = result.getJSONArray("items");
                for (int i = 0; i < tokenList.length(); i++) {
                    JSONObject oj = tokenList.getJSONObject(i);

                    JSONArray tags = oj.getJSONArray("tags");
                    for (int j = 0; j < tags.length(); j++) {
                        if (tags.get(j).toString().contains("security")) {
                            security = true;
                            break;
                        }
                    }

                    if (!security && NSRs < numNSRs) {
                        String title = oj.getString("title");
                        title = title.replace(";", "");

                        int id = oj.getInt("question_id");
                        int date = oj.getInt("creation_date");
                        Date time = new Date((long) date * 1000);

                        String newTime = new SimpleDateFormat("dd-MM-yyyy").format(time);

                        String body = oj.getString("body");
                        String cleanText = html2text(body);

                        List<String> postAnswers = new ArrayList<>();
                        Boolean is_answered = oj.getBoolean("is_answered");
                        if (getAnswers && is_answered) {
                            int answerNumber = oj.getInt("answer_count");
                            if (answerNumber != 0) {
                                JSONArray answers = oj.getJSONArray("answers");

                                for (int j = 0; j < answerNumber; j++) {
                                    JSONObject answerObj = answers.getJSONObject(j);
                                    String answer = answerObj.getString("body");
                                    postAnswers.add(new Cleanup().cleanText(html2text(answer)).replace("\n", "").replace("\r", "").replace(";", ""));
                                    if (!getAnswersWithThreshold) {
                                        cleanText = cleanText + " " + html2text(answer);
                                    }
                                }
                            }
                        }

                        cleanText = cleanText.replace("\n", "").replace("\r", "").replace(";", "");

                        double score = getTFIDFScore(cleanText, features, docsArray, tfidfDocsVector, d);

                        if (score <= threshold && NSRs < numNSRs) {
                            if (!postAnswers.isEmpty() && getAnswersWithThreshold) {
                                for (int p = 0; p < postAnswers.size(); p++) {
                                    double answerScore = getTFIDFScore(postAnswers.get(p), features, docsArray, tfidfDocsVector, d);

                                    if (answerScore <= threshold) {
                                        cleanText = cleanText + " " + postAnswers.get(p);
                                    }
                                }
                            }

                            builder.append(title + ";");
                            builder.append(cleanText + ";");
                            builder.append(id + ";");
                            builder.append(newTime);
                            if (appendScoreToCSV) {
                                builder.append(";" + score);
                            }
                            builder.append('\n');

                            NSRs++;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (hasMore) {
                page++;
            } else {
                break;
            }
        }
        pw.write(builder.toString());
        pw.close();
        System.out.println("done!");
    }

    public static void getSRsWithThresholdTFIDF(String newFile, String benchmarkDataset, String terms, String site, double threshold, int numSRs, boolean getAnswers, boolean getAnswersWithThreshold, boolean appendScoreToCSV) throws IOException {

        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new File(newFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        builder = new StringBuilder();
        String columnNamesList = "";
        if (appendScoreToCSV) {
            columnNamesList = "Title;Description;Id;Date;cossim";
        } else {
            columnNamesList = "Title;Description;Id;Date";
        }
        builder.append(columnNamesList + "\n");

        TFIDFSimilarity d = new TFIDFSimilarity();
        List<String> features = d.getTermsFromFile(terms);

        List<String[]> docsArray = d.getDocsArrayFromCsv(benchmarkDataset);
        List<double[]> tfidfDocsVector = d.tfIdfCalculator(docsArray, docsArray, features);

        Boolean hasMore = true;
        int page = 1;
        int SRs = 0;
        while (SRs < numSRs) {
            HttpClient client = HttpClientBuilder.create()
                    .setDefaultRequestConfig(RequestConfig.custom()
                            .setCookieSpec(CookieSpecs.STANDARD).build())
                    .build();
            HttpGet request = new HttpGet("https://api.stackexchange.com/2.2/questions?page=" + page + "&pagesize=100&order=desc&sort=activity&site=" + site + "&filter=!--1nZwT3Ejsm&key=IT8vJtd)vD02vi1lzs5mHg((");

            try {
                HttpResponse response = client.execute(request);
                HttpEntity entity = response.getEntity();

                // Read the contents of an entity and return it as a String.
                String content = EntityUtils.toString(entity);

                JSONObject result = new JSONObject(content);

                try {
                    hasMore = result.getBoolean("has_more");
                } catch (Exception e) {
                    hasMore = false;
                }

                boolean security = false;
                JSONArray tokenList = result.getJSONArray("items");
                for (int i = 0; i < tokenList.length(); i++) {
                    JSONObject oj = tokenList.getJSONObject(i);

                    JSONArray tags = oj.getJSONArray("tags");
                    for (int t = 0; t < tags.length(); t++) {
                        if (tags.get(t).toString().contains("security")) {
                            security = true;
                            break;
                        }
                    }

                    if (security && SRs < numSRs) {
                        String title = oj.getString("title");
                        title = title.replace(";", "");

                        int id = oj.getInt("question_id");
                        int date = oj.getInt("creation_date");
                        Date time = new Date((long) date * 1000);

                        String newTime = new SimpleDateFormat("dd-MM-yyyy").format(time);

                        String body = oj.getString("body");
                        String cleanText = html2text(body);

                        List<String> postAnswers = new ArrayList<>();
                        Boolean is_answered = oj.getBoolean("is_answered");
                        if (getAnswers && is_answered) {
                            int answerNumber = oj.getInt("answer_count");
                            if (answerNumber != 0) {
                                JSONArray answers = oj.getJSONArray("answers");

                                for (int j = 0; j < answerNumber; j++) {
                                    JSONObject answerObj = answers.getJSONObject(j);
                                    String answer = answerObj.getString("body");
                                    postAnswers.add(new Cleanup().cleanText(html2text(answer)).replace("\n", "").replace("\r", "").replace(";", ""));
                                    if (!getAnswersWithThreshold) {
                                        cleanText = cleanText + " " + html2text(answer);
                                    }
                                }
                            }
                        }

                        cleanText = cleanText.replace("\n", "").replace("\r", "").replace(";", "");

                        double score = getTFIDFScore(cleanText, features, docsArray, tfidfDocsVector, d);

                        if (score >= threshold) {
                            if (!postAnswers.isEmpty() && getAnswersWithThreshold) {
                                for (int p = 0; p < postAnswers.size(); p++) {
                                    double answerScore = getTFIDFScore(postAnswers.get(p), features, docsArray, tfidfDocsVector, d);

                                    if (answerScore >= threshold) {
                                        cleanText = cleanText + " " + postAnswers.get(p);
                                    }
                                }
                            }

                            builder.append(title + ";");
                            builder.append(cleanText + ";");
                            builder.append(id + ";");
                            builder.append(newTime);
                            if (appendScoreToCSV) {
                                builder.append(";" + score);
                            }
                            builder.append('\n');

                            SRs++;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (hasMore) {
                page++;
            } else {
                break;
            }
        }
        pw.write(builder.toString());
        pw.close();
        System.out.println("done!");
    }

    public static void getNSRsWithThresholdWord2Vec(String newFile, String benchmarkDataset, String word2vec, String site, double threshold, int numNSRs, boolean getAnswers, boolean getAnswersWithThreshold, boolean appendScoreToCSV) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new File(newFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        builder = new StringBuilder();
        String columnNamesList = "";
        if (appendScoreToCSV) {
            columnNamesList = "Title;Description;Id;Date;cossim";
        } else {
            columnNamesList = "Title;Description;Id;Date";
        }
        builder.append(columnNamesList + "\n");

        Word2VecSimilarity w = new Word2VecSimilarity();

        Word2Vec model = w.getWord2Vec(word2vec);

        List<Collection<String>> benchmarkSentences = new ArrayList<>();
        w.getSentences(benchmarkDataset, benchmarkSentences);
        List<INDArray> input_vectors = new ArrayList<>();

        for (int m = 0; m < benchmarkSentences.size(); m++) {
            input_vectors.add(w.getVector(benchmarkSentences.get(m), model));
        }

        Boolean hasMore = true;
        int page = 1;
        int NSRs = 0;
        while (NSRs < numNSRs) {
            HttpClient client = HttpClientBuilder.create()
                    .setDefaultRequestConfig(RequestConfig.custom()
                            .setCookieSpec(CookieSpecs.STANDARD).build())
                    .build();
            HttpGet request = new HttpGet("https://api.stackexchange.com/2.2/questions?page=" + page + "&pagesize=100&order=desc&sort=activity&&site=" + site + "&filter=!--1nZwT3Ejsm&key=IT8vJtd)vD02vi1lzs5mHg((");

            try {
                HttpResponse response = client.execute(request);
                HttpEntity entity = response.getEntity();

                // Read the contents of an entity and return it as a String.
                String content = EntityUtils.toString(entity);

                JSONObject result = new JSONObject(content);

                try {
                    hasMore = result.getBoolean("has_more");
                } catch (Exception e) {
                    hasMore = false;
                }

                boolean security = false;

                JSONArray tokenList = result.getJSONArray("items");
                for (int i = 0; i < tokenList.length(); i++) {
                    JSONObject oj = tokenList.getJSONObject(i);

                    JSONArray tags = oj.getJSONArray("tags");
                    for (int j = 0; j < tags.length(); j++) {
                        if (tags.get(j).toString().equals("security")) {
                            security = true;
                            break;
                        }
                    }

                    if (!security && NSRs < numNSRs) {
                        String title = oj.getString("title");
                        title = title.replace(";", "");

                        int id = oj.getInt("question_id");
                        int date = oj.getInt("creation_date");
                        Date time = new Date((long) date * 1000);

                        String newTime = new SimpleDateFormat("dd-MM-yyyy").format(time);

                        String body = oj.getString("body");
                        String cleanText = html2text(body);

                        List<String> postAnswers = new ArrayList<>();
                        Boolean is_answered = oj.getBoolean("is_answered");
                        if (getAnswers && is_answered) {
                            int answerNumber = oj.getInt("answer_count");
                            if (answerNumber != 0) {
                                JSONArray answers = oj.getJSONArray("answers");

                                for (int j = 0; j < answerNumber; j++) {
                                    JSONObject answerObj = answers.getJSONObject(j);
                                    String answer = answerObj.getString("body");
                                    postAnswers.add(new Cleanup().cleanText(html2text(answer)).replace("\n", "").replace("\r", "").replace(";", ""));
                                    if (!getAnswersWithThreshold) {
                                        cleanText = cleanText + " " + html2text(answer);
                                    }
                                }
                            }
                        }

                        cleanText = cleanText.replace("\n", "").replace("\r", "").replace(";", "");

                        double score = getWord2VecScore(cleanText, benchmarkSentences, input_vectors, model, w);

                        if (score <= threshold && NSRs < numNSRs) {
                            if (!postAnswers.isEmpty() && getAnswersWithThreshold) {
                                for (int p = 0; p < postAnswers.size(); p++) {
                                    double answerScore = getWord2VecScore(postAnswers.get(p), benchmarkSentences, input_vectors, model, w);

                                    if (answerScore <= threshold) {
                                        cleanText = cleanText + " " + postAnswers.get(p);
                                    }
                                }
                            }

                            builder.append(title + ";");
                            builder.append(cleanText + ";");
                            builder.append(id + ";");
                            builder.append(newTime);
                            if (appendScoreToCSV) {
                                builder.append(";" + score);
                            }
                            builder.append('\n');

                            NSRs++;
                        }
                    }
                    security = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (hasMore) {
                page++;
            } else {
                break;
            }
        }
        pw.write(builder.toString());
        pw.close();
        System.out.println("done!");
    }

    public static void getSRsWithThresholdWord2Vec(String newFile, String benchmarkDataset, String word2vec, String site, double threshold, int numSRs, boolean getAnswers, boolean getAnswersWithThreshold, boolean appendScoreToCSV) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new File(newFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        builder = new StringBuilder();
        String columnNamesList = "";
        if (appendScoreToCSV) {
            columnNamesList = "Title;Description;Id;Date;cossim";
        } else {
            columnNamesList = "Title;Description;Id;Date";
        }
        builder.append(columnNamesList + "\n");

        Word2VecSimilarity w = new Word2VecSimilarity();

        Word2Vec model = w.getWord2Vec(word2vec);

        List<Collection<String>> benchmarkSentences = new ArrayList<>();
        w.getSentences(benchmarkDataset, benchmarkSentences);
        List<INDArray> input_vectors = new ArrayList<>();

        for (int m = 0; m < benchmarkSentences.size(); m++) {
            input_vectors.add(w.getVector(benchmarkSentences.get(m), model));
        }

        Boolean hasMore = true;
        int page = 1;
        int SRs = 0;
        while (SRs <= numSRs) {
            HttpClient client = HttpClientBuilder.create()
                    .setDefaultRequestConfig(RequestConfig.custom()
                            .setCookieSpec(CookieSpecs.STANDARD).build())
                    .build();
            //HttpGet request = new HttpGet("https://api.stackexchange.com/2.2/questions?page=" + page + "&pagesize=100&order=desc&sort=activity&tagged=" + URLEncoder.encode(tags, "UTF-8") + "&site=" + site + "&filter=!--1nZwT3Ejsm&key=IT8vJtd)vD02vi1lzs5mHg((");
            HttpGet request = new HttpGet("https://api.stackexchange.com/2.2/questions?page=" + page + "&pagesize=100&order=desc&sort=activity&site=" + site + "&filter=!--1nZwT3Ejsm&key=IT8vJtd)vD02vi1lzs5mHg((");

            try {
                HttpResponse response = client.execute(request);
                HttpEntity entity = response.getEntity();

                // Read the contents of an entity and return it as a String.
                String content = EntityUtils.toString(entity);

                JSONObject result = new JSONObject(content);

                try {
                    hasMore = result.getBoolean("has_more");
                } catch (Exception e) {
                    hasMore = false;
                }

                boolean security = false;
                JSONArray tokenList = result.getJSONArray("items");
                for (int i = 0; i < tokenList.length(); i++) {
                    JSONObject oj = tokenList.getJSONObject(i);

                    JSONArray tags = oj.getJSONArray("tags");
                    for (int t = 0; t < tags.length(); t++) {
                        if (tags.get(t).toString().contains("security")) {
                            security = true;
                            break;
                        }
                    }

                    if (security && SRs < numSRs) {
                        String title = oj.getString("title");
                        title = title.replace(";", "");

                        int id = oj.getInt("question_id");
                        int date = oj.getInt("creation_date");
                        Date time = new Date((long) date * 1000);

                        String newTime = new SimpleDateFormat("dd-MM-yyyy").format(time);

                        String body = oj.getString("body");
                        String cleanText = html2text(body);
                        cleanText = cleanText.replace("\n", "").replace("\r", "").replace(";", "");

                        List<String> postAnswers = new ArrayList<>();
                        Boolean is_answered = oj.getBoolean("is_answered");
                        if (getAnswers && is_answered) {
                            int answerNumber = oj.getInt("answer_count");
                            if (answerNumber != 0) {
                                JSONArray answers = oj.getJSONArray("answers");

                                for (int j = 0; j < answerNumber; j++) {
                                    JSONObject answerObj = answers.getJSONObject(j);
                                    String answer = answerObj.getString("body");
                                    postAnswers.add(new Cleanup().cleanText(html2text(answer)).replace("\n", "").replace("\r", "").replace(";", ""));
                                    if (!getAnswersWithThreshold) {
                                        cleanText = cleanText + " " + html2text(answer);
                                    }
                                }
                            }
                        }

                        double score = getWord2VecScore(cleanText, benchmarkSentences, input_vectors, model, w);

                        if (score >= threshold) {
                            if (!postAnswers.isEmpty() && getAnswersWithThreshold) {
                                for (int p = 0; p < postAnswers.size(); p++) {
                                    double answerScore = getWord2VecScore(postAnswers.get(p), benchmarkSentences, input_vectors, model, w);

                                    if (answerScore >= threshold) {
                                        cleanText = cleanText + " " + postAnswers.get(p);
                                    }
                                }
                            }

                            builder.append(title + ";");
                            builder.append(cleanText + ";");
                            builder.append(id + ";");
                            builder.append(newTime);
                            if (appendScoreToCSV) {
                                builder.append(";" + score);
                            }
                            builder.append('\n');

                            SRs++;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (hasMore) {
                page++;
            } else {
                break;
            }
        }
        pw.write(builder.toString());
        pw.close();
        System.out.println("done!");
    }

    private static double getTFIDFScore(String cleanText, List<String> features, List<String[]> docsArray, List<double[]> tfidfDocsVector, TFIDFSimilarity d) {
        // check cosine similarity
        double[] cleanTextDoc = d.getDocumentVectors(cleanText, features, docsArray);

        double score = 0.0;
        double cosine = 0.0;
        for (int k = 0; k < tfidfDocsVector.size(); k++) {
            cosine = d.getCosineSimilarityTwoDocuments(cleanTextDoc, tfidfDocsVector.get(k));

            if (cosine > score) {
                score = cosine;
            }
        }
        return score;
    }

    private static double getWord2VecScore(String cleanText, List<Collection<String>> benchmarkSentences, List<INDArray> input_vectors, Word2Vec model, Word2VecSimilarity w) {
        Collection<String> sentence = new Cleanup().normalizeText(cleanText);

        double score = 0.0;
        double cosine = 0.0;
        for (int k = 0; k < benchmarkSentences.size(); k++) {
            //INDArray input1_vector = w.getVector(benchmarkSentences.get(k), model);
            INDArray input2_vector = w.getVector(sentence, model);

            double dot_product = Nd4j.getBlasWrapper().dot(input_vectors.get(k), input2_vector);

            cosine = w.cosine_similarity(input_vectors.get(k).toDoubleVector(), input2_vector.toDoubleVector(), dot_product);

            if (cosine > score) {
                score = cosine;
            }
        }
        return score;
    }

    public static String html2text(String html) {
        return Jsoup.parse(html).text();
    }
}
