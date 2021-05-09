package feature_extraction;


import java.io.IOException;

/**
 * @author tdoy
 *
 */
public class FeaturesTFIDF {

    private String dataFile;
    private String stopwordsFile;
    private String newFeatureFile;
    private int numFeatures;

    public static class Builder {

        private String dataFile = "";
        private String stopwordsFile = "";
        private String newFeatureFile = "";
        private int numFeatures = 100; // default

        public Builder(){
        }

        public FeaturesTFIDF.Builder dataFile(String dataFile) {
            this.dataFile = dataFile;
            return this;
        }

        public FeaturesTFIDF.Builder stopwordsFile(String stopwordsFile) {
            this.stopwordsFile = stopwordsFile;
            return this;
        }

        public FeaturesTFIDF.Builder newFeatureFile(String newFeatureFile) {
            this.newFeatureFile = newFeatureFile;
            return this;
        }

        public FeaturesTFIDF.Builder numFeatures(int numFeatures) {
            this.numFeatures = numFeatures;
            return this;
        }

        public FeaturesTFIDF build() {
            FeaturesTFIDF featuresTFIDF = new FeaturesTFIDF();
            featuresTFIDF.dataFile = this.dataFile;
            featuresTFIDF.newFeatureFile = this.newFeatureFile;
            featuresTFIDF.stopwordsFile = this.stopwordsFile;
            featuresTFIDF.numFeatures = this.numFeatures;

            return featuresTFIDF;
        }
    }

    private FeaturesTFIDF(){}

    public void run() throws Exception {
        FeaturesTFIDFHelper ft = new FeaturesTFIDFHelper(this);
        ft.createFeatureFile();
    }

    public String getDataFile() {
        return dataFile;
    }

    public void setDataFile(String dataFile) {
        this.dataFile = dataFile;
    }

    public String getStopwordsFile() {
        return stopwordsFile;
    }

    public void setStopwordsFile(String stopwordsFile) {
        this.stopwordsFile = stopwordsFile;
    }

    public String getNewFeatureFile() {
        return newFeatureFile;
    }

    public void setNewFeatureFile(String newFeatureFile) {
        this.newFeatureFile = newFeatureFile;
    }

    public int getNumFeatures() {
        return numFeatures;
    }

    public void setNumFeatures(int numFeatures) {
        this.numFeatures = numFeatures;
    }
}