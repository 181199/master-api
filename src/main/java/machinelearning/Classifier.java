package machinelearning;

import machinelearning.classifiers.NaiveBayesClassifier;
import machinelearning.classifiers.RandomForestClassifier;
import machinelearning.classifiers.SVMClassifier;
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

public class Classifier {

    public static void main(String[] args) throws Exception {

        String path = "/Users/anja/Desktop/master/api/files/testing/";

        Instances data = loadDataset(path + "stackoverflow.arff");

        NGramTokenizer tokenizer = new NGramTokenizer();	// get a tokenizer
        SnowballStemmer stemmer = new SnowballStemmer();	// get a word-stemmer
        MyStopwordsHandler stopword = new MyStopwordsHandler();

        FixedDictionaryStringToWordVector filter = new FixedDictionaryStringToWordVector();

        filter.setDictionaryFile(new File("/Users/anja/Desktop/master/api/files/FeaturesTFIDF.txt"));
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

        RandomForestClassifier.classify(trainingSet, testSet, "stack");

        NaiveBayesClassifier.classify(trainingSet, testSet, "stack");

        SVMClassifier.classify(trainingSet, testSet, "stack");

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
