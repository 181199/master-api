package sources;

public class UsingCrawler {

    public static void main(String[] args) throws Exception {

        Crawler crawler = new Crawler.Builder()
                .newFile("./files/test.csv")
                .source(Crawler.CAPEC)
                .numQueries(10)
                .year(2020)
                .build();

        crawler.run();
    }
}
