package feature_extraction;

public class TFIDF {

    private String term;
    private double tfidf;

    public TFIDF(String term, double tfidf) {
        this.term = term;
        this.tfidf = tfidf;
    }

    public String getTerm() {
        return term;
    }

    public double getTfidf() {
        return tfidf;
    }

}