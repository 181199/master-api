package sources;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class cve {

    private static StringBuilder builder;

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws IOException, ParseException, java.text.ParseException {

        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new File("cvsData.csv"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        builder = new StringBuilder();
        String columnNamesList = "CVS_ID;Description;Score;Severity;Date;Reference";
        // No need give the headers Like: id, Name on builder.append
        builder.append(columnNamesList +"\n");

        writeToCSV("/Users/anja/Desktop/master/nvd/nvdcve-1.1-2020.json");
        writeToCSV("/Users/anja/Desktop/master/nvd/nvdcve-1.1-2019.json");
        writeToCSV("/Users/anja/Desktop/master/nvd/nvdcve-1.1-2018.json");
        writeToCSV("/Users/anja/Desktop/master/nvd/nvdcve-1.1-2017.json");
        writeToCSV("/Users/anja/Desktop/master/nvd/nvdcve-1.1-2016.json");

        pw.write(builder.toString());
        pw.close();
        System.out.println("done!");
    }

    public static void writeToCSV(String filename) throws IOException, ParseException, java.text.ParseException {
        // parsing file "JSONExample.json"
        Object obj = new JSONParser().parse(new FileReader(filename));

        // typecasting obj to JSONObject
        org.json.simple.JSONObject jo = (JSONObject) obj;

        String CVE_data_type = (String) jo.get("CVE_data_type");

        System.out.println("CVE_data_type: " + CVE_data_type);

        JSONArray items = (JSONArray) jo.get("CVE_Items");

        for (Object o : items) {
            JSONObject item = (JSONObject) o;
            JSONObject key = (JSONObject) item.get("cve");

            JSONObject data = (JSONObject) key.get("CVE_data_meta");
            String id = (String) data.get("ID").toString();

            JSONObject desc = (JSONObject) key.get("description");
            JSONArray desc_data = (JSONArray) desc.get("description_data");
            JSONObject description = (JSONObject) desc_data.get(0);
            String desc_value = description.get("value").toString();

            String ref_url = "";
            JSONObject ref = (JSONObject) key.get("references");
            JSONArray ref_data = (JSONArray) ref.get("reference_data");
            if(!ref_data.isEmpty()) {
                JSONObject references = (JSONObject) ref_data.get(0);
                ref_url = references.get("url").toString();
            }

            String score = "";
            String severity = "";
            JSONObject impact = (JSONObject) item.get("impact");
            if((JSONObject) impact.get("baseMetricV3") != null) {
                JSONObject baseMetric = (JSONObject) impact.get("baseMetricV3");
                JSONObject cvss = (JSONObject) baseMetric.get("cvssV3");
                score = cvss.get("baseScore").toString();
                severity = cvss.get("baseSeverity").toString();
            }

            String date = item.get("publishedDate").toString();

//            System.out.println("\nItem:");
//            System.out.println(id);
//            System.out.println(desc_value);
//            System.out.println(score);
//            System.out.println(severity);
//            System.out.println(date);
//            System.out.println(ref_url);

            builder.append(id+";");
            builder.append(desc_value+";");
            builder.append(score+";");
            builder.append(severity+";");
            builder.append(date+";");
            builder.append(ref_url);
            builder.append('\n');
        }
    }
}
