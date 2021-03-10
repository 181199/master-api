package machinelearning;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class Classifier {

    public static final String RANDOMFOREST = "rf";
    public static final String NAIVEBAYES = "nb";
    public static final String SVM = "svm";
    public static final String IBK = "ibk";
    public static final String LOGISTICTREGRESSION = "lr";

    private String learner;
    private String dataset;
    private String features;
    private String modelDirectory;
    private String saveModelPath;
    private boolean createModel = false;
    private int numFolds = 5;

    public static class Builder {

        private String learner = Classifier.RANDOMFOREST; // standard
        private String dataset;
        private String features;
        private String modelDirectory;
        private String saveModelPath;
        private boolean createModel = false;
        private int numFolds = 5;
        //private boolean stem? etc.

        public Builder(){

        }

        public Builder learner(String learner) {
            this.learner = learner;
            return this;
        }

        public Builder dataset(String dataset) {
            this.dataset = dataset;
            return this;
        }

        public Builder features(String features) {
            this.features = features;
            return this;
        }

        public Builder modelDirectory(String modelDirectory) {
            this.modelDirectory = modelDirectory;
            return this;
        }

        public Builder saveModelPath(String saveModelPath) {
            this.saveModelPath = saveModelPath;
            return this;
        }

        public Builder createModel(boolean createModel) {
            this.createModel = createModel;
            return this;
        }

        public Builder numFolds(int numFolds) {
            this.numFolds = numFolds;
            return this;
        }

        public Classifier build() {

            Classifier classifier = new Classifier();
            classifier.learner = this.learner;
            classifier.dataset = this.dataset;
            classifier.features = this.features;
            classifier.modelDirectory = this.modelDirectory;
            classifier.saveModelPath = this.saveModelPath;
            classifier.createModel = this.createModel;
            classifier.numFolds = this.numFolds;

            return classifier;
        }
    }

    private Classifier(){}

    public void run() throws Exception {
        //
        try {
            ClassifierHelper cl = new ClassifierHelper(this);
            if(this.createModel)
                cl.createModel();
            else
                cl.classify();
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | IOException e) {
            e.printStackTrace();
        }
    }

    public String getLearner() {
        return learner;
    }

    public void setLearner(String learner) {
        this.learner = learner;
    }

    public String getDataset() {
        return dataset;
    }

    public void setDataset(String dataset) {
        this.dataset = dataset;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public String getModelDirectory() {
        return modelDirectory;
    }

    public void setModelDirectory(String modelDirectory) {
        this.modelDirectory = modelDirectory;
    }

    public String getSaveModelPath() {
        return saveModelPath;
    }

    public void setSaveModelPath(String saveModelPath) {
        this.saveModelPath = saveModelPath;
    }

    public boolean isCreateModel() {
        return createModel;
    }

    public void setCreateModel(boolean createModel) {
        this.createModel = createModel;
    }

    public int getNumFolds() {
        return numFolds;
    }

    public void setNumFolds(int numFolds) {
        this.numFolds = numFolds;
    }
}
