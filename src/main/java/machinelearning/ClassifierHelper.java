package machinelearning;

import machinelearning.classifiers.*;
import machinelearning.utils.MyStopwordsHandler;
import weka.core.Debug;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.core.stemmers.SnowballStemmer;
import weka.core.tokenizers.NGramTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.FixedDictionaryStringToWordVector;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClassifierHelper {

    Classifier classifier;

    public ClassifierHelper(Classifier classifier) {
        this.classifier = classifier;
    }

    public void classify() throws Exception {

        Instances testing = loadDataset(classifier.getDataset());

        NGramTokenizer tokenizer = new NGramTokenizer();    // get a tokenizer
        SnowballStemmer stemmer = new SnowballStemmer();    // get a word-stemmer
        MyStopwordsHandler stopword = new MyStopwordsHandler();

        FixedDictionaryStringToWordVector filter = new FixedDictionaryStringToWordVector();

        filter.setDictionaryFile(new File(classifier.getFeatures()));
        filter.setInputFormat(testing);
        filter.setTokenizer(tokenizer);
        filter.setLowerCaseTokens(true);
        filter.setIDFTransform(false);
        filter.setTFTransform(true);
        filter.setOutputWordCounts(true);
        //filter.setStemmer(stemmer);
        filter.setStopwordsHandler(stopword);

        Instances testSet = Filter.useFilter(testing, filter);

        if (classifier.getLearner().equals("rf")) {
            RandomForestClassifier.classifyModel(testSet, classifier.getModelDirectory() + "randomforest.model");
        } else if (classifier.getLearner().equals("nb")) {
            NaiveBayesClassifier.classifyModel(testSet, classifier.getModelDirectory() + "naivebayes.model");
        } else if (classifier.getLearner().equals("svm")) {
            SVMClassifier.classifyModel(testSet, classifier.getModelDirectory() + "svm.model");
        } else if (classifier.getLearner().equals("ibk")) {
            KNNClassifier.classifyModel(testSet, classifier.getModelDirectory() + "ibk.model");
        } else if (classifier.getLearner().equals("lr")) {
            LogisticRegressionClassifier.classifyModel(testSet, classifier.getModelDirectory() + "lr.model");
        }
    }

    public void createModel() throws Exception {

        Instances data = loadDataset(classifier.getDataset());

        NGramTokenizer tokenizer = new NGramTokenizer();    // get a tokenizer
        SnowballStemmer stemmer = new SnowballStemmer();    // get a word-stemmer
        MyStopwordsHandler stopword = new MyStopwordsHandler();

        FixedDictionaryStringToWordVector filter = new FixedDictionaryStringToWordVector();

        filter.setDictionaryFile(new File(classifier.getFeatures()));
        filter.setInputFormat(data);
        filter.setTokenizer(tokenizer);
        filter.setLowerCaseTokens(true);
        filter.setIDFTransform(false);
        filter.setTFTransform(true);
        filter.setOutputWordCounts(true);
        //filter.setStemmer(stemmer);
        filter.setStopwordsHandler(stopword);

        Instances dataFiltered = Filter.useFilter(data, filter);

        Instances sample = dataFiltered;
        int numFolds = 5;

        // setting up train- and test-set
        sample.randomize(new Debug.Random(42));
        sample.stratify(numFolds);

        Instances trainingSet = sample.trainCV(numFolds, 0);
        Instances testSet = sample.testCV(numFolds, 0);

        if (classifier.getLearner().equals("rf")) {
            RandomForestClassifier.classify(trainingSet, testSet, classifier.getSaveModelPath());
        } else if (classifier.getLearner().equals("nb")) {
            NaiveBayesClassifier.classify(trainingSet, testSet, classifier.getSaveModelPath());
        } else if (classifier.getLearner().equals("svm")) {
            SVMClassifier.classify(trainingSet, testSet, classifier.getSaveModelPath());
        } else if (classifier.getLearner().equals("ibk")) {
            KNNClassifier.classify(trainingSet, testSet, classifier.getSaveModelPath());
        } else if (classifier.getLearner().equals("lr")) {
            LogisticRegressionClassifier.classify(trainingSet, testSet, classifier.getSaveModelPath());
        }
    }

    public static Instances loadDataset(String path) {
        Instances dataset = null;
        try {
            dataset = ConverterUtils.DataSource.read(path);
            if (dataset.classIndex() == -1) {
                dataset.setClassIndex(dataset.numAttributes() - 1);
            }
        } catch (Exception ex) {
            Logger.getLogger(Classifier.class.getName()).log(Level.SEVERE, null, ex);
        }

        return dataset;
    }

}
