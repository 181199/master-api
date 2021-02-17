package sources;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class CVECrawler {

    private static StringBuilder builder;

    public static void main(String[] args) {

        String newFile = "/Users/anja/Desktop/master/api/files/cveTest.csv";
        queryCVE(newFile, 2020, 2500);
    }

    public static void queryCVE(String newFile, int year, int numQueries){
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new File(newFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        builder = new StringBuilder();
        String columnNamesList = "CVE_ID;Description;Score;Severity;Type;Type-of-source;Weakness;Date;Source;Reference";
        builder.append(columnNamesList + "\n");

        WebClient client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);

        int number = 1;
        final DecimalFormat decimalFormat = new DecimalFormat("0000");

        while (number <= numQueries) {
            try {
                String searchUrl = "https://nvd.nist.gov/vuln/detail/CVE-" + URLEncoder.encode(String.valueOf(year), "UTF-8") + "-" + URLEncoder.encode(decimalFormat.format(number), "UTF-8");
                HtmlPage page = client.getPage(searchUrl);

                // check if page returns CVE ID not found
                ArrayList<DomText> error = (ArrayList<DomText>) page.getByXPath("/html/body/div[2]/div[2]/div[2]/h2");
                if(!error.isEmpty()){
                    number++;
                    continue;
                }

                String id = "CVE-" + year + "-" + decimalFormat.format(number);
                System.out.println(id);

                ArrayList<DomText> list = (ArrayList<DomText>) page.getByXPath("//*[@id=\"vulnDetailTableView\"]/tbody/tr/td/div/div[1]/p/text()");
                final DomText desc = list.get(0);
                System.out.println("   Description: " + desc.asText());

                // skip CVE if it is rejected
                if(desc.asText().contains("** REJECT **")){
                    number++;
                    continue;
                }

                System.out.println("   Type-of-source: Vulnerability Database");
                System.out.println("   Type: Vulnerability");

                ArrayList<HtmlSpan> date = (ArrayList<HtmlSpan>) page.getByXPath("//*[@id=\"vulnDetailTableView\"]/tbody/tr/td/div/div[2]/div/span[1]");
                final HtmlSpan dateText = date.get(0);
                System.out.println("   Date: " + dateText.asText());

                ArrayList<HtmlSpan> source = (ArrayList<HtmlSpan>) page.getByXPath("//*[@id=\"vulnDetailTableView\"]/tbody/tr/td/div/div[2]/div/span[3]");
                final HtmlSpan sourceText = source.get(0);
                System.out.println("   Source: " + sourceText.asText());

                ArrayList<DomText> weaknessList = (ArrayList<DomText>) page.getByXPath("//*[@id=\"vulnTechnicalDetailsDiv\"]/table/tbody/tr/td[2]/text()");
                final DomText weakness = weaknessList.get(0);
                System.out.println("   Weakness: " + weakness.asText());

                HtmlAnchor severityRating = null;
                ArrayList<HtmlAnchor> rating = (ArrayList<HtmlAnchor>) page.getByXPath("//*[@id=\"Cvss3NistCalculatorAnchor\"]");
                if (!rating.isEmpty()) {
                    severityRating = rating.get(0);
                    System.out.println("   Severity-rating: " + severityRating.asText());
                } else {
                    ArrayList<HtmlAnchor> rating2 = (ArrayList<HtmlAnchor>) page.getByXPath("//*[@id=\"Cvss3CnaCalculatorAnchor\"]");
                    if (!rating2.isEmpty()) {
                        severityRating = rating2.get(0);
                        System.out.println("   Severity-rating: " + severityRating.asText());
                    }
                }

                String score = "";
                String severityText = "";
                if(severityRating != null){
                    String severity = severityRating.asText();
                    String[] split = severity.split(" ");
                    score = split[0];
                    severityText = split[1];
                } else {
                    score = "No score";
                    severityText = "No severity rating";
                }

                System.out.println("   Reference(s):");
                ArrayList<HtmlElement> listTable = (ArrayList<HtmlElement>) page.getByXPath("//*[@id=\"vulnHyperlinksPanel\"]/table");
                final HtmlTable tableLinks = (HtmlTable) listTable.get(0);
                for (final HtmlTableRow row : tableLinks.getRows()) {
                    //System.out.println("Found match:");
                    for (final HtmlTableCell cell : row.getCells()) {
                        if (cell.asText().startsWith("http"))
                            System.out.println("      " + cell.asText());
                    }
                }

                System.out.println("   CVE-link: " + "https://cve.mitre.org/cgi-bin/cvename.cgi?name=" + id);
                System.out.println();

                //"CVE_ID;Description;Score;Severity;Type;Type-of-source;Weakness;Date;Source;Reference"

                builder.append(id + ";");
                builder.append(desc.asText() + ";");
                builder.append(score + ";");
                builder.append(severityText + ";");
                builder.append("Vulnerability" + ";");
                builder.append("Vulnerability Database" + ";");
                builder.append(weakness.asText() + ";");
                builder.append(dateText.asText() + ";");
                builder.append(sourceText.asText() + ";");
                builder.append("https://cve.mitre.org/cgi-bin/cvename.cgi?name=" + id);
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
