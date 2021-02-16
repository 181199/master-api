package sources;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import similarity.Documents;

import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class StackExchangeAPI {

    private static StringBuilder builder;

    public static void main(String[] args) throws IOException {

        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new File("./files/stackoverflowSR_threshold.csv"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        builder = new StringBuilder();
        String columnNamesList = "Title;Description;Id;Date";
        builder.append(columnNamesList +"\n");

        String tags = "security";
        String site = "stackoverflow";
        int page = 1;
        String dataset = "/Users/anja/Desktop/master/api/files/testing/cveData_small.csv";
        String terms = "/Users/anja/Desktop/master/api/files/FeaturesTFIDF.txt";

        //getNSRsWithThreshold(dataset, terms,  site, 0.05, 500);
        getSRsWithThreshold(dataset, terms, site, tags, 0.8, 500);

//        //each page has max 100 posts
//        while(page <= 25) {
//            getSRs(site, tags, page);
//            page++;
//        }
//
//        while(page <= 25){
//            getNSRs(site, page);
//            page++;
//        }

        pw.write(builder.toString());
        pw.close();
        System.out.println("done!");
    }

    public static void getSRs(String site, String tags, int page) throws UnsupportedEncodingException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet("https://api.stackexchange.com/2.2/questions?page=" + page + "&pagesize=100&order=desc&sort=activity&tagged=" + URLEncoder.encode(tags, "UTF-8") + "&site=" + site + "&filter=!--1nZwT3Ejsm");

        try {
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();

            // Read the contents of an entity and return it as a String.
            String content = EntityUtils.toString(entity);
            System.out.println(content);

            JSONObject result = new JSONObject(content);

            JSONArray tokenList = result.getJSONArray("items");
            for(int i = 0; i < tokenList.length(); i++){
                JSONObject oj = tokenList.getJSONObject(i);
                String title = oj.getString("title");
                title = title.replace(";", "");

                int id = oj.getInt("question_id");
                int date = oj.getInt("creation_date");
                Date time = new Date((long)date*1000);

                String newTime = new SimpleDateFormat("dd-MM-yyyy").format(time);

                String body = oj.getString("body");
                String cleanText = html2text(body);
                cleanText = cleanText.replace("\n", "").replace("\r", "").replace(";","");

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

                builder.append(title+";");
                builder.append(cleanText+";");
                builder.append(id+";");
                builder.append(newTime+";");
                builder.append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void getNSRs(String site, int page) throws UnsupportedEncodingException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet("https://api.stackexchange.com/2.2/questions?page=" + page + "&pagesize=100&order=desc&sort=activity&&site=" + site + "&filter=!--1nZwT3Ejsm");

        try {
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();

            // Read the contents of an entity and return it as a String.
            String content = EntityUtils.toString(entity);
            System.out.println(content);

            JSONObject result = new JSONObject(content);

            boolean security = false;

            JSONArray tokenList = result.getJSONArray("items");
            for(int i = 0; i < tokenList.length(); i++){
                JSONObject oj = tokenList.getJSONObject(i);
                String title = oj.getString("title");
                title = title.replace(";", "");

                JSONArray tags = oj.getJSONArray("tags");
                for(int j = 0; j < tags.length(); j++){
                    if(tags.get(j).toString().equals("security")){
                        security = true;
                    }
                }

                int id = oj.getInt("question_id");
                int date = oj.getInt("creation_date");
                Date time = new Date((long)date*1000);

                String newTime = new SimpleDateFormat("dd-MM-yyyy").format(time);

                String body = oj.getString("body");
                String cleanText = html2text(body);
                cleanText = cleanText.replace("\n", "").replace("\r", "").replace(";","");

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

                if(!security) {
                    builder.append(title + ";");
                    builder.append(cleanText + ";");
                    builder.append(id + ";");
                    builder.append(newTime + ";");
                    builder.append('\n');
                }

                security = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void getNSRsWithThreshold(String dataset, String terms, String site, double threshold, int nrNSRs) throws IOException {

        Documents d = new Documents();

        List<String> features = d.getTermsFromFile(terms);

        List<String[]> cveDocsArray = d.getDocsArrayFromCsv(dataset);
        System.out.println("done docs");
        List<double[]> tfidfDocsVectorCve = d.tfIdfCalculator(cveDocsArray, cveDocsArray, features);
        System.out.println("done tfidf");

        int page = 1;
        int NSRs = 0;
        while(NSRs <= nrNSRs) {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet("https://api.stackexchange.com/2.2/questions?page=" + page + "&pagesize=100&order=desc&sort=activity&&site=" + site + "&filter=!--1nZwT3Ejsm");

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
                        builder.append(newTime + ";");
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
    }

    public static void getSRsWithThreshold(String dataset, String terms, String site, String tags, double threshold, int nrSRs) throws IOException {

        Documents d = new Documents();

        List<String> features = d.getTermsFromFile(terms);

        List<String[]> cveDocsArray = d.getDocsArrayFromCsv(dataset);
        System.out.println("done docs");
        List<double[]> tfidfDocsVectorCve = d.tfIdfCalculator(cveDocsArray, cveDocsArray, features);
        System.out.println("done tfidf");

        int page = 1;
        int SRs = 0;
        while(SRs <= nrSRs) {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet("https://api.stackexchange.com/2.2/questions?page=" + page + "&pagesize=100&order=desc&sort=activity&tagged=" + URLEncoder.encode(tags, "UTF-8") + "&site=" + site + "&filter=!--1nZwT3Ejsm");

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
                        builder.append(newTime + ";");
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
    }

    public static String html2text(String html) {
        return Jsoup.parse(html).text();
    }
}
