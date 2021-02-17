package sources;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;

public class CAPECCrawler {

    private static StringBuilder builder;

    public static void main(String[] args) {

        String newFile = "/Users/anja/Desktop/master/api/files/sources/capec.csv";
        queryCAPEC(newFile, 1000);
    }

    public static void queryCAPEC(String newFile, int numQueries){
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new File(newFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        builder = new StringBuilder();
        String columnNamesList = "CAPEC_ID;Description;Type;Type-of-source;Weakness;Date;Source";
        builder.append(columnNamesList + "\n");

        WebClient client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
        client.getOptions().setThrowExceptionOnFailingStatusCode(false);

        int number = 1;
        while (number <= numQueries) {
            try {
                String searchUrl = "https://capec.mitre.org/data/definitions/" + URLEncoder.encode(String.valueOf(number), "UTF-8") +".html";
                HtmlPage page = client.getPage(searchUrl);

                ArrayList<HtmlHeading2> titleList = (ArrayList<HtmlHeading2>) page.getByXPath("//*[@id=\"Contentpane\"]/div[1]/table/tbody/tr/td/h2");
                if(titleList.isEmpty()){
                    number++;
                    continue;
                }
                final HtmlHeading2 weakness = titleList.get(0);
                if(weakness.asText().contains("CATEGORY") || weakness.asText().contains("DEPRECATED")){
                    number++;
                    continue;
                }

                String id = "CAPEC-" + number;
                System.out.println(id);

                String weaknessText = "";
                if(weakness.asText() != null){
                    String weak = weakness.asText();
                    String[] split = weak.split(":", 2);
                    weaknessText = split[1];
                    weaknessText = weaknessText.substring(1);
                }

                System.out.println("   Weakness: " + weaknessText);

                String xpathDesc = "//*[@id=\"oc_" + number + "_Description\"]/div";
                ArrayList<HtmlDivision> list = (ArrayList<HtmlDivision>) page.getByXPath(xpathDesc);
                if(list.isEmpty()){
                    number++;
                    continue;
                }
                final HtmlDivision desc = list.get(0);
                System.out.println("   Description: " + desc.asText());

                System.out.println("   Type-of-source: Attack Database");
                System.out.println("   Type: Attack");

                String xpathDate = "//*[@id=\"footbar\"]/text()";
                ArrayList<DomText> date = (ArrayList<DomText>) page.getByXPath(xpathDate);
                final DomText dateText = date.get(0);
                System.out.println("   Date: " + dateText.asText());

                System.out.println("   CAPEC-link: " + "https://cve.mitre.org/cgi-bin/cvename.cgi?name=" + id);
                System.out.println();

                builder.append(id + ";");
                builder.append(desc.asText().replace("\n", " "));
                builder.append("Vulnerability" + ";");
                builder.append("Vulnerability Database" + ";");
                builder.append(weaknessText + ";");
                builder.append(dateText.asText() + ";");
                builder.append("https://cwe.mitre.org/data/definitions/" + id);
                builder.append('\n');

                number++;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        pw.write(builder.toString());
        pw.close();
    }
}
