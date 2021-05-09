package feature_extraction;

import java.lang.reflect.InvocationTargetException;

public class FeaturesWord2Vec {

    private String modelFile;
    private String newFeatureFile;
    private int numWords;

    public static class Builder {

        private String modelFile = "";
        private String newFeatureFile = "";
        private int numWords = 100;

        public Builder(){
        }

        public FeaturesWord2Vec.Builder modelFile(String modelFile) {
            this.modelFile = modelFile;
            return this;
        }

        public FeaturesWord2Vec.Builder newFeatureFile(String newFeatureFile) {
            this.newFeatureFile = newFeatureFile;
            return this;
        }

        public FeaturesWord2Vec.Builder numWords(int numWords) {
            this.numWords = numWords;
            return this;
        }

        public FeaturesWord2Vec build() {
            FeaturesWord2Vec featuresWord2Vec = new FeaturesWord2Vec();
            featuresWord2Vec.modelFile = this.modelFile;
            featuresWord2Vec.newFeatureFile = this.newFeatureFile;
            featuresWord2Vec.numWords = this.numWords;

            return featuresWord2Vec;
        }
    }

    private FeaturesWord2Vec(){}

    public void run() throws Exception {
        FeaturesWord2VecHelper ft = new FeaturesWord2VecHelper(this);
        ft.saveWordsToFile();
    }

    public String getModelFile() {
        return modelFile;
    }

    public void setModelFile(String modelFile) {
        this.modelFile = modelFile;
    }

    public String getNewFeatureFile() {
        return newFeatureFile;
    }

    public void setNewFeatureFile(String newFeatureFile) {
        this.newFeatureFile = newFeatureFile;
    }

    public int getNumWords() {
        return numWords;
    }

    public void setNumWords(int numWords) {
        this.numWords = numWords;
    }
}
