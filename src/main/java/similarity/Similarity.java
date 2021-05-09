package similarity;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class Similarity {

    public static final String TFIDF = "TFIDF";
    public static final String Word2Vec = "Word2Vec";

    private String file;
    private String benchmarkDataset;
    private String features;
    private String source;
    private String method;
    private String word2vec;
    private int numFeatures;
    private int descriptionIndex;
    private int numSimilarSources;
    private double threshold;
    private boolean getOnlyMostSimilar;

    public static class Builder {

        private String file = "";
        private String benchmarkDataset = "";
        private String features = "";
        private String source = "";
        private String method = "";
        private String word2vec = "";
        private int numFeatures = 0;
        private int descriptionIndex = -1;
        private int numSimilarSources = 1; // default
        private double threshold = 0.6; // default
        private boolean getOnlyMostSimilar = false;

        public Builder(){
        }

        public Similarity.Builder source(String source) {
            this.source = source;
            return this;
        }

        public Similarity.Builder file(String file) {
            this.file = file;
            return this;
        }

        public Similarity.Builder features(String features) {
            this.features = features;
            return this;
        }

        public Similarity.Builder benchmarkDataset(String benchmarkDataset) {
            this.benchmarkDataset = benchmarkDataset;
            return this;
        }

        public Similarity.Builder method(String method) {
            this.method = method;
            return this;
        }

        public Similarity.Builder word2vec(String word2vec) {
            this.word2vec = word2vec;
            return this;
        }

        public Similarity.Builder numFeatures(int numFeatures) {
            this.numFeatures = numFeatures;
            return this;
        }

        public Similarity.Builder descriptionIndex(int descriptionIndex) {
            this.descriptionIndex = descriptionIndex;
            return this;
        }

        public Similarity.Builder numSimilarSources(int numSimilarSources) {
            this.numSimilarSources = numSimilarSources;
            return this;
        }

        public Similarity.Builder threshold(double threshold) {
            this.threshold = threshold;
            return this;
        }

        public Similarity.Builder getOnlyMostSimilar(boolean getOnlyMostSimilar) {
            this.getOnlyMostSimilar = getOnlyMostSimilar;
            return this;
        }

        public Similarity build() {
            Similarity similarity = new Similarity();
            similarity.source = this.source;
            similarity.file = this.file;
            similarity.features = this.features;
            similarity.benchmarkDataset = this.benchmarkDataset;
            similarity.method = this.method;
            similarity.word2vec = this.word2vec;
            similarity.numFeatures = this.numFeatures;
            similarity.descriptionIndex = this.descriptionIndex;
            similarity.numSimilarSources = this.numSimilarSources;
            similarity.threshold = this.threshold;
            similarity.getOnlyMostSimilar = this.getOnlyMostSimilar;

            return similarity;
        }
    }

    private Similarity(){}

    public void run() throws Exception {
        SimilarityHelper sim = new SimilarityHelper(this);
        if(this.method.equals(Similarity.TFIDF) && getOnlyMostSimilar){
            sim.mostSimilarSourceTFIDF();
        } else if (this.method.equals(Similarity.Word2Vec) && getOnlyMostSimilar){
            sim.mostSimilarSourceWord2Vec();
        } else if (this.method.equals(Similarity.TFIDF) && !getOnlyMostSimilar){
            sim.similarSourcesTFIDF();
        } else if (this.method.equals(Similarity.Word2Vec) && !getOnlyMostSimilar){
            sim.similarSourcesWord2Vec();
        }
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getBenchmarkDataset() {
        return benchmarkDataset;
    }

    public void setBenchmarkDataset(String benchmarkDataset) {
        this.benchmarkDataset = benchmarkDataset;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getWord2vec() {
        return word2vec;
    }

    public void setWord2vec(String word2vec) {
        this.word2vec = word2vec;
    }

    public int getNumFeatures() {
        return numFeatures;
    }

    public void setNumFeatures(int numFeatures) {
        this.numFeatures = numFeatures;
    }

    public int getDescriptionIndex() {
        return descriptionIndex;
    }

    public void setDescriptionIndex(int descriptionIndex) {
        this.descriptionIndex = descriptionIndex;
    }

    public int getNumSimilarSources() {
        return numSimilarSources;
    }

    public void setNumSimilarSources(int numSimilarSources) {
        this.numSimilarSources = numSimilarSources;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public boolean isGetOnlyMostSimilar() {
        return getOnlyMostSimilar;
    }

    public void setGetOnlyMostSimilar(boolean getOnlyMostSimilar) {
        this.getOnlyMostSimilar = getOnlyMostSimilar;
    }
}
