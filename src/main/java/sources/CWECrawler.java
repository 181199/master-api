package sources;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class CWECrawler {

    private static StringBuilder builder;

    public static void main(String[] args) {

        String newFile = "/Users/anja/Desktop/master/api/files/cweTest.csv";
        queryCVE(newFile, 100);
    }

    public static void queryCVE(String newFile, int numQueries){
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new File(newFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        builder = new StringBuilder();
        String columnNamesList = "CWE_ID;Description;Type;Type-of-source;Weakness;Date;Source";
        builder.append(columnNamesList + "\n");

        WebClient client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);

        int number = 1;
        while (number <= numQueries) {
            try {
                String searchUrl = "https://cwe.mitre.org/data/definitions/" + URLEncoder.encode(String.valueOf(number), "UTF-8") +".html";
                HtmlPage page = client.getPage(searchUrl);

                ArrayList<HtmlHeading2> titleList = (ArrayList<HtmlHeading2>) page.getByXPath("//*[@id=\"Contentpane\"]/div[2]/h2");
                final HtmlHeading2 weakness = titleList.get(0);
                if(weakness.asText().contains("CATEGORY") || weakness.asText().contains("DEPRECATED")){
                    number++;
                    continue;
                }

                String id = "CWE-" + number;
                System.out.println(id);

                String weaknessText = "";
                if(weakness.asText() != null){
                    String weak = weakness.asText();
                    String[] split = weak.split(":", 2);
                    weaknessText = split[1];
                    weaknessText = weaknessText.substring(1);
                }

                System.out.println("   Weakness: " + weaknessText);

                String xpathDesc = "//*[@id=\"oc_" + number + "_Description\"]/div/div/text()";
                ArrayList<DomText> list = (ArrayList<DomText>) page.getByXPath(xpathDesc);
                final DomText desc = list.get(0);
                System.out.println("   Description: " + desc.asText());

                String notes = "";
                String xpathNotes = "//*[@id=\"oc_" + number + "_Notes\"]/div/div/div";
                ArrayList<HtmlDivision> notesList = (ArrayList<HtmlDivision>) page.getByXPath(xpathNotes);
                if(!notesList.isEmpty()) {
                    final HtmlDivision note = notesList.get(0);
                    notes = note.asText();
                    System.out.println("   Notes: " + note.asText());
                }

                System.out.println("   Type-of-source: Weakness Database");
                System.out.println("   Type: Weakness");

                String xpathDate = "//*[@id=\"oc_" + number + "_Submissions\"]/tr[2]/td[1]";
                ArrayList<HtmlTableDataCell> date = (ArrayList<HtmlTableDataCell>) page.getByXPath(xpathDate);
                final HtmlTableDataCell dateText = date.get(0);
                System.out.println("   Date: " + dateText.asText());

                System.out.println("   CVE-link: " + "https://cve.mitre.org/cgi-bin/cvename.cgi?name=" + id);
                System.out.println();

                builder.append(id + ";");
                builder.append(desc.asText().replace("\n", " ") + ". " + notes.replace("\n", " ") + ";");
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
