import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;

public class CVE_Crawler {

    public static void main(String[] args) {
        ArrayList<String> CVE = new ArrayList<String>();

        // Search through CVE database using keyword(s)
        WebClient client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);

        String searchQuery = "java";

        try {
            String searchUrl = "https://cve.mitre.org/cgi-bin/cvekey.cgi?keyword=" + URLEncoder.encode(searchQuery, "UTF-8");
            HtmlPage page = client.getPage(searchUrl);

            ArrayList<HtmlElement> list = (ArrayList<HtmlElement>) page.getByXPath("/html/body/div[1]/div[3]/div[2]/table");
            final HtmlTable table = (HtmlTable) list.get(0);
            for (final HtmlTableRow row : table.getRows()) {
                //System.out.println("Found match:");
                for (final HtmlTableCell cell : row.getCells()) {
                    if (cell.asText().startsWith("CVE-")) {
                        //System.out.println("   Name:" + cell.asText());
                        //System.out.println("   Link: " + "https://cve.mitre.org/cgi-bin/cvename.cgi?name=" + cell.asText());
                        CVE.add(cell.asText());
                    } else {
                        //System.out.println("   Description: " + cell.asText());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Finds the results from the CVE database in the NVD database (contains more info)
        for(int i = 0; i < 5; i++) {
            System.out.println("Found match:");
            String nvdQuery = CVE.get(i);
            System.out.println("   Name: " + nvdQuery);
            try {
                String searchNVD = "https://nvd.nist.gov/vuln/detail/" + URLEncoder.encode(nvdQuery, "UTF-8");
                HtmlPage pageNVD = client.getPage(searchNVD);

                ArrayList<DomText> list = (ArrayList<DomText>) pageNVD.getByXPath("//*[@id=\"vulnDetailTableView\"]/tbody/tr/td/div/div[1]/p/text()");
                final DomText desc = list.get(0);
                System.out.println("   Description: " + desc.asText());

                System.out.println("   Type-of-source: Vulnerability Database");
                System.out.println("   Type: Vulnerability");

                ArrayList<HtmlSpan> date = (ArrayList<HtmlSpan>) pageNVD.getByXPath("//*[@id=\"vulnDetailTableView\"]/tbody/tr/td/div/div[2]/div/span[1]");
                final HtmlSpan dateText = date.get(0);
                System.out.println("   Date: " + dateText.asText());

                ArrayList<HtmlSpan> source = (ArrayList<HtmlSpan>) pageNVD.getByXPath("//*[@id=\"vulnDetailTableView\"]/tbody/tr/td/div/div[2]/div/span[3]");
                final HtmlSpan sourceText = source.get(0);
                System.out.println("   Source: " + sourceText.asText());

                ArrayList<DomText> weaknessList = (ArrayList<DomText>) pageNVD.getByXPath("//*[@id=\"vulnTechnicalDetailsDiv\"]/table/tbody/tr/td[2]/text()");
                final DomText weakness = weaknessList.get(0);
                System.out.println("   Weakness: " + weakness.asText());

                ArrayList<HtmlAnchor> rating = (ArrayList<HtmlAnchor>) pageNVD.getByXPath("//*[@id=\"Cvss3NistCalculatorAnchor\"]");
                if(!rating.isEmpty()) {
                    final HtmlAnchor severityRating = rating.get(0);
                    System.out.println("   Severity-rating: " + severityRating.asText());
                } else {
                    ArrayList<HtmlAnchor> rating2 = (ArrayList<HtmlAnchor>) pageNVD.getByXPath("//*[@id=\"Cvss3CnaCalculatorAnchor\"]");
                    if(!rating2.isEmpty()) {
                        final HtmlAnchor severityRating = rating2.get(0);
                        System.out.println("   Severity-rating: " + severityRating.asText());
                    }
                }

                System.out.println("   Reference(s):");
                ArrayList<HtmlElement> listTable = (ArrayList<HtmlElement>) pageNVD.getByXPath("//*[@id=\"vulnHyperlinksPanel\"]/table");
                final HtmlTable tableLinks = (HtmlTable) listTable.get(0);
                for (final HtmlTableRow row : tableLinks.getRows()) {
                    //System.out.println("Found match:");
                    for (final HtmlTableCell cell : row.getCells()) {
                        if(cell.asText().startsWith("http"))
                            System.out.println("      " + cell.asText());
                    }
                }

                System.out.println("   Hyperlink: " + "https://nvd.nist.gov/vuln/detail/" + nvdQuery);


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
