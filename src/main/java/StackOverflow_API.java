import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

public class StackOverflow_API {

    public static void main(String[] args) throws UnsupportedEncodingException {

        String tags = "java;security";

        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet("https://api.stackexchange.com/2.2/questions?order=desc&sort=activity&tagged=" + URLEncoder.encode(tags, "UTF-8") + "&site=stackoverflow&filter=!--1nZwT3Ejsm");

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
                int id = oj.getInt("question_id");
                int date = oj.getInt("creation_date");
                Date time = new Date((long)date*1000);

                String body = oj.getString("body");

                System.out.println("Title: " + title + "\nId: " + id + "\nDate: " + time + "\nBody: " + body + "\n");

                int answerNumber = oj.getInt("answer_count");
                if(answerNumber != 0) {
                    JSONArray answers = oj.getJSONArray("answers");

                    for(int j = 0; j < answerNumber; j++){
                        JSONObject answerObj = answers.getJSONObject(j);
                        String answer = answerObj.getString("body");
                        System.out.println("Answer " + (j+1) + ": " + answer);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
