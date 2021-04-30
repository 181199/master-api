package feature_extraction;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class Word2VecModel {

    private String file;
    private String newModelFile;
    private int minWordFrequency;
    private int iterations;
    private int layerSize;
    private int seed;
    private int windowSize;

    public static class Builder {

        private String file;
        private String newModelFile;
        private String newFeatureFile;
        private boolean createModel = false;
        private boolean getFeatureWords = false;
        private int minWordFrequency = 1;
        private int iterations = 100;
        private int layerSize = 100;
        private int seed = 42;
        private int windowSize = 5;

        public Builder(){
        }

        public Word2VecModel.Builder file(String file) {
            this.file = file;
            return this;
        }

        public Word2VecModel.Builder newModelFile(String newModelFile) {
            this.newModelFile = newModelFile;
            return this;
        }

        public Word2VecModel.Builder iterations(int iterations) {
            this.iterations = iterations;
            return this;
        }

        public Word2VecModel.Builder layerSize(int layerSize) {
            this.layerSize = layerSize;
            return this;
        }

        public Word2VecModel.Builder seed(int seed) {
            this.seed = seed;
            return this;
        }

        public Word2VecModel.Builder windowSize(int windowSize) {
            this.windowSize = windowSize;
            return this;
        }

        public Word2VecModel build() {
            Word2VecModel word2VecModel = new Word2VecModel();
            word2VecModel.file = this.file;
            word2VecModel.newModelFile = this.newModelFile;
            word2VecModel.minWordFrequency = this.minWordFrequency;
            word2VecModel.iterations = this.iterations;
            word2VecModel.layerSize = this.layerSize;
            word2VecModel.seed = this.seed;
            word2VecModel.windowSize = this.windowSize;

            return word2VecModel;
        }
    }

    private Word2VecModel(){}

    public void run() throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Word2VecModelHelper ft = new Word2VecModelHelper(this);
        ft.createWord2VecModel();
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getNewModelFile() {
        return newModelFile;
    }

    public void setNewModelFile(String newModelFile) {
        this.newModelFile = newModelFile;
    }

    public int getMinWordFrequency() {
        return minWordFrequency;
    }

    public void setMinWordFrequency(int minWordFrequency) {
        this.minWordFrequency = minWordFrequency;
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public int getLayerSize() {
        return layerSize;
    }

    public void setLayerSize(int layerSize) {
        this.layerSize = layerSize;
    }

    public int getSeed() {
        return seed;
    }

    public void setSeed(int seed) {
        this.seed = seed;
    }

    public int getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(int windowSize) {
        this.windowSize = windowSize;
    }
}
