import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class cve {

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws IOException, ParseException {

        // parsing file "JSONExample.json"
        Object obj = new JSONParser().parse(new FileReader("sources/nvdcve-1.1-2020.json"));

        // typecasting obj to JSONObject
        org.json.simple.JSONObject jo = (JSONObject) obj;

        // getting firstName and lastName
        String CVE_data_type = (String) jo.get("CVE_data_type");

        System.out.println("CVE_data_type: " + CVE_data_type);

        JSONArray lineItems = (JSONArray) jo.get("CVE_Items");
        for (Object o : lineItems) {
            JSONObject jsonLineItem = (JSONObject) o;
            String key = jsonLineItem.get("cve").toString();
            System.out.println(key);
        }
    }
}
