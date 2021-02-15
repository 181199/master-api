package machinelearning;

import machinelearning.classifiers.*;
import machinelearning.utils.MyStopwordsHandler;
import org.opencv.ml.LogisticRegression;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.trees.RandomTree;
import weka.core.Debug;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils;
import weka.core.stemmers.SnowballStemmer;
import weka.core.tokenizers.NGramTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.FixedDictionaryStringToWordVector;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Classifier {

    public static void main(String[] args) throws Exception {
        String path1 = "/Users/anja/Desktop/master/api/files/testing/";
        String path2 = "/Users/anja/Desktop/master/api/files/validation/";
        String features = "/Users/anja/Desktop/master/api/files/FeaturesTFIDF.txt";

        //createModel(path1 + "stackoverflow.arff", features, "SO");

        classify(path2 + "derby.arff", features);
    }

    public static void classify(String filepath, String features) throws Exception {

        Instances data = loadDataset(filepath);

        NGramTokenizer tokenizer = new NGramTokenizer();    // get a tokenizer
        SnowballStemmer stemmer = new SnowballStemmer();    // get a word-stemmer
        MyStopwordsHandler stopword = new MyStopwordsHandler();

        FixedDictionaryStringToWordVector filter = new FixedDictionaryStringToWordVector();

        filter.setDictionaryFile(new File(features));
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

        RandomForestClassifier.classifyModel(trainingSet, testSet, "/Users/anja/Desktop/master/api/files/models/SO_randomforest.model");

        NaiveBayesClassifier.classifyModel(trainingSet, testSet, "/Users/anja/Desktop/master/api/files/models/SO_naivebayes.model");

        SVMClassifier.classifyModel(trainingSet, testSet, "/Users/anja/Desktop/master/api/files/models/SO_svm.model");

        KNNClassifier.classifyModel(trainingSet, testSet, "/Users/anja/Desktop/master/api/files/models/SO_ibk.model");

        LogisticRegressionClassifier.classifyModel(trainingSet, testSet, "/Users/anja/Desktop/master/api/files/models/SO_lr.model");
    }

    public static void createModel(String filePath, String features, String datasetName) throws Exception {

        Instances data = loadDataset(filePath);

        NGramTokenizer tokenizer = new NGramTokenizer();	// get a tokenizer
        SnowballStemmer stemmer = new SnowballStemmer();	// get a word-stemmer
        MyStopwordsHandler stopword = new MyStopwordsHandler();

        FixedDictionaryStringToWordVector filter = new FixedDictionaryStringToWordVector();

        filter.setDictionaryFile(new File(features));
        filter.setInputFormat(data);
        filter.setTokenizer(tokenizer);
        filter.setLowerCaseTokens(true);
        filter.setIDFTransform(false);
        filter.setTFTransform(true);
        filter.setOutputWordCounts(true);
        //filter.setStemmer(stemmer);
        filter.setStopwordsHandler(stopword);

        Instances dataFiltered = Filter.useFilter(data, filter);

        Instances sample =	dataFiltered;
        int numFolds = 5;

        // setting up train- and test-set
        sample.randomize(new Debug.Random(42));
        sample.stratify(numFolds);

        Instances trainingSet = sample.trainCV(numFolds, 0);
        Instances testSet = sample.testCV(numFolds, 0);

        RandomForestClassifier.classify(trainingSet, testSet, datasetName);

        NaiveBayesClassifier.classify(trainingSet, testSet, datasetName);

        SVMClassifier.classify(trainingSet, testSet, datasetName);

        KNNClassifier.classify(trainingSet, testSet, datasetName);

        LogisticRegressionClassifier.classify(trainingSet, testSet, datasetName);
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
