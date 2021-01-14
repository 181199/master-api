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

import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StackExchangeAPI {

    private static StringBuilder builder;

    public static void main(String[] args) throws UnsupportedEncodingException {

        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new File("./files/stackoverflowSBR_small.csv"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        builder = new StringBuilder();
        String columnNamesList = "Title;Description;Id;Date";
        builder.append(columnNamesList +"\n");

        String tags = "security";
        String site = "stackoverflow";
        int page = 1;

        while(page <= 5) {
            getSBRs(site, tags, page);
            page++;
        }

//        while(page <= 2){
//            getNSBRs(site, page);
//            page++;
//        }

        pw.write(builder.toString());
        pw.close();
        System.out.println("done!");
    }

    public static void getSBRs(String site, String tags, int page) throws UnsupportedEncodingException {
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

    public static void getNSBRs(String site, int page) throws UnsupportedEncodingException {
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

    public static String html2text(String html) {
        return Jsoup.parse(html).text();
    }
}
