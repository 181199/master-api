package sources;

public class UsingCrawler {

    public static void main(String[] args) throws Exception {

        // collect records rom CVE, CWE or CAPEC
//        Crawler crawler = new Crawler.Builder()
//                .newFile("./files/test.csv")
//                .source(Crawler.CVE)
//                .numQueries(10)
//                .year(2020)
//                .build();
//
//        crawler.run();

        // collect code from Programcreek
        Crawler crawler = new Crawler.Builder()
                .newFile("./files/test.csv")
                .source(Crawler.PROGRAMCREEK)
                .numQueries(10)
                .codeLanguage(Crawler.JAVA)
                .tags("sort")
                .build();

        crawler.run();
    }
}
