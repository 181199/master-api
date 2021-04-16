package sources;

import java.io.IOException;

public class Crawler {

    public static final String CVE = "CVE";
    public static final String CWE = "CWE";
    public static final String CAPEC = "CAPEC";
    public static final String PROGRAMCREEK = "PROGRAMCREEK";
    public static final String JAVA = "java-api-examples";
    public static final String CPP = "cpp";
    //public static final String PYTHON = "python";
    public static final String SCALA = "scala";

    private String source;
    private String newFile;
    private int numQueries;
    private int year;
    private String tags;
    private String codeLanguage;

    public static class Builder {

        private String source = Crawler.CVE; //standard
        private String newFile;
        private int numQueries = 100;
        private int year = 2020;
        private String tags;
        private String codeLanguage;

        public Builder(){
        }

        public Builder source(String source) {
            this.source = source;
            return this;
        }

        public Builder newFile(String newFile) {
            this.newFile = newFile;
            return this;
        }

        public Builder numQueries(int numQueries) {
            this.numQueries = numQueries;
            return this;
        }

        public Builder year(int year) {
            this.year = year;
            return this;
        }

        public Builder tags(String tags) {
            this.tags = tags;
            return this;
        }

        public Builder codeLanguage(String codeLanguage) {
            this.codeLanguage = codeLanguage;
            return this;
        }

        public Crawler build() {
            Crawler crawler = new Crawler();
            crawler.source = this.source;
            crawler.newFile = this.newFile;
            crawler.numQueries = this.numQueries;
            crawler.year = this.year;
            crawler.tags = this.tags;
            crawler.codeLanguage = this.codeLanguage;


            return crawler;
        }
    }

    private Crawler(){}

    public void run() throws IOException {
        CrawlerHelper ch = new CrawlerHelper(this);
        if (this.source.equals(Crawler.CVE)) {
            ch.queryCVE();
        } else if (this.source.equals(Crawler.CWE)) {
            ch.queryCWE();
        } else if (this.source.equals(Crawler.CAPEC)) {
            ch.queryCAPEC();
        } else if (this.source.equals(Crawler.PROGRAMCREEK)){
            ch.queryProgramcreek();
        }
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getNewFile() {
        return newFile;
    }

    public void setNewFile(String newFile) {
        this.newFile = newFile;
    }

    public int getNumQueries() {
        return numQueries;
    }

    public void setNumQueries(int numQueries) {
        this.numQueries = numQueries;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getCodeLanguage() {
        return codeLanguage;
    }

    public void setCodeLanguage(String codeLanguage) {
        this.codeLanguage = codeLanguage;
    }
}
