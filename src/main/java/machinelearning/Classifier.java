package machinelearning;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class Classifier {

    public static final String RANDOMFOREST = "rf";
    public static final String NAIVEBAYES = "nb";
    public static final String SVM = "svm";
    public static final String IBK = "ibk";
    public static final String LOGISTICTREGRESSION = "lr";
    public static final String CNN = "cnn";

    private String learner;
    private String dataset;
    private String features;
    private String model;
    private String saveModelPath;
    private boolean createModel;
    private int numFolds;
    private boolean stem;
    private int indexLabel;
    private int classesCount;
    private int featureCount;

    public static class Builder {

        private String learner = Classifier.RANDOMFOREST; // standard
        private String dataset;
        private String features;
        private String model;
        private String saveModelPath;
        private boolean createModel = false;
        private int numFolds = 5;
        private boolean stem;
        private int indexLabel = 99;
        private int classesCount = 2;
        private int featureCount = 99;

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

        public Builder model(String model) {
            this.model = model;
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

        public Builder stem(boolean stem) {
            this.stem = stem;
            return this;
        }

        public Builder indexLabel(int indexLabel) {
            this.indexLabel = indexLabel;
            return this;
        }

        public Builder classesCount(int classesCount) {
            this.classesCount = classesCount;
            return this;
        }

        public Builder featureCount(int featureCount) {
            this.featureCount = featureCount;
            return this;
        }

        public Classifier build() {

            Classifier classifier = new Classifier();
            classifier.learner = this.learner;
            classifier.dataset = this.dataset;
            classifier.features = this.features;
            classifier.model = this.model;
            classifier.saveModelPath = this.saveModelPath;
            classifier.createModel = this.createModel;
            classifier.numFolds = this.numFolds;
            classifier.stem = this.stem;
            classifier.indexLabel = this.indexLabel;
            classifier.classesCount = this.classesCount;
            classifier.featureCount = this.featureCount;

            return classifier;
        }
    }

    private Classifier(){}

    public void run() throws Exception {
        //
        try {
            ClassifierHelper cl = new ClassifierHelper(this);
            if(this.createModel)
                if(this.learner.equals(Classifier.CNN)){
                    cl.createCNNModel();
                } else {
                    cl.createModel();
                }
            else {
                if(this.learner.equals(Classifier.CNN)){
                    cl.classifyCNN();
                } else {
                    cl.classify();
                }
            }
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

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
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

    public boolean isStem() {
        return stem;
    }

    public void setStem(boolean stem) {
        this.stem = stem;
    }

    public int getIndexLabel() {
        return indexLabel;
    }

    public void setIndexLabel(int indexLabel) {
        this.indexLabel = indexLabel;
    }

    public int getClassesCount() {
        return classesCount;
    }

    public void setClassesCount(int classesCount) {
        this.classesCount = classesCount;
    }

    public int getFeatureCount() {
        return featureCount;
    }

    public void setFeatureCount(int featureCount) {
        this.featureCount = featureCount;
    }
}
