package machinelearning;

import machinelearning.classifiers.*;
import machinelearning.utils.MyStopwordsHandler;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.BackpropType;
import org.deeplearning4j.nn.conf.ConvolutionMode;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.learning.config.AdaDelta;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import weka.core.Debug;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.core.stemmers.SnowballStemmer;
import weka.core.tokenizers.NGramTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.FixedDictionaryStringToWordVector;

import java.io.File;
import java.io.IOException;
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
        if(classifier.isStem()) {
            filter.setStemmer(stemmer);
        }
        filter.setStopwordsHandler(stopword);

        Instances testSet = Filter.useFilter(testing, filter);

        if (classifier.getLearner().equals("rf")) {
            RandomForestClassifier.classifyModel(testSet, classifier.getModel());
        } else if (classifier.getLearner().equals("nb")) {
            NaiveBayesClassifier.classifyModel(testSet, classifier.getModel());
        } else if (classifier.getLearner().equals("svm")) {
            SVMClassifier.classifyModel(testSet, classifier.getModel());
        } else if (classifier.getLearner().equals("ibk")) {
            KNNClassifier.classifyModel(testSet, classifier.getModel());
        } else if (classifier.getLearner().equals("lr")) {
            LogisticRegressionClassifier.classifyModel(testSet, classifier.getModel());
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
        if(classifier.isStem()) {
            filter.setStemmer(stemmer);
        }
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

    public void classifyCNN() throws IOException, InterruptedException {
        DataSet allData;
        try (RecordReader recordReader = new CSVRecordReader(1, ';')) {
            recordReader.initialize(new FileSplit(new File(classifier.getDataset())));

            DataSetIterator iterator = new RecordReaderDataSetIterator(recordReader, 1000, classifier.getIndexLabel(), classifier.getClassesCount());
            allData = iterator.next();
        }

        allData.shuffle(42);

        DataNormalization normalizer = new NormalizerStandardize();
        normalizer.fit(allData);
        normalizer.transform(allData);

        MultiLayerNetwork model = ModelSerializer.restoreMultiLayerNetwork(classifier.getModel());

        INDArray output = model.output(allData.getFeatures());

        Evaluation eval = new Evaluation(classifier.getClassesCount());
        eval.eval(allData.getLabels(), output);

        double recall = eval.recall(1);
        double precision = eval.precision(1);
        double fmeasure = eval.f1(1);
        double gmeasure = (2 * eval.recall(1)*100*(100 - eval.falsePositiveRate(1)*100))/(eval.recall(1)*100 + (100 - eval.falsePositiveRate(1)*100));
        double pf = (double) eval.falseNegatives().get(1)/( (double) eval.falseNegatives().get(1) + (double) eval.trueNegatives().get(1));

        System.out.println("CNN");
        System.out.println("TP: " + eval.truePositives().get(1) + " | TN: " + eval.trueNegatives().get(1) + " | FP: " + eval.falsePositives().get(1) + " | FN: " + eval.falseNegatives().get(1));

        System.out.println("Precision: " + precision);
        System.out.println("Recall (PD): " + recall);
        System.out.println("PF: " + pf);
        System.out.println("F-measure: " + fmeasure);
        System.out.println("G-measure: " + gmeasure + "\n");
    }

    public void createCNNModel() throws IOException, InterruptedException {
        DataSet allData;
        try (RecordReader recordReader = new CSVRecordReader(1, ';')) {
            recordReader.initialize(new FileSplit(new File(classifier.getDataset())));

            DataSetIterator iterator = new RecordReaderDataSetIterator(recordReader, 1000, classifier.getIndexLabel(), classifier.getClassesCount());
            allData = iterator.next();
        }

        allData.shuffle(42);

        DataNormalization normalizer = new NormalizerStandardize();
        normalizer.fit(allData);
        normalizer.transform(allData);

        SplitTestAndTrain testAndTrain = allData.splitTestAndTrain(0.80);
        DataSet trainingData = testAndTrain.getTrain();
        DataSet testData = testAndTrain.getTest();

        MultiLayerConfiguration configuration = new NeuralNetConfiguration.Builder()
                //.iterations(1000)
                .seed(42)
                .updater(new AdaDelta())
                .convolutionMode(ConvolutionMode.Same)      //This is important so we can 'stack' the results later
                .l2(3)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .maxNumLineSearchIterations(1000)
                .activation(Activation.TANH)
                .weightInit(WeightInit.XAVIER)
                .list()
                .layer(0, new DenseLayer.Builder().nIn(classifier.getFeatureCount()).nOut(classifier.getFeatureCount())
                        .build())
                .layer(1, new DenseLayer.Builder().nIn(classifier.getFeatureCount()).nOut(classifier.getFeatureCount())
                        .build())
                .layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .activation(Activation.SOFTMAX)
                        .nIn(classifier.getFeatureCount()).nOut(classifier.getClassesCount()).build())
                .backpropType(BackpropType.Standard)
                .build();

        MultiLayerNetwork model = new MultiLayerNetwork(configuration);
        model.init();
        model.fit(trainingData);

        INDArray output = model.output(testData.getFeatures());

        Evaluation eval = new Evaluation(classifier.getClassesCount());
        eval.eval(testData.getLabels(), output);

        double recall = eval.recall(1);
        double precision = eval.precision(1);
        double fmeasure = eval.f1(1);
        double gmeasure = (2 * eval.recall(1)*100*(100 - eval.falsePositiveRate(1)*100))/(eval.recall(1)*100 + (100 - eval.falsePositiveRate(1)*100));
        double pf = eval.falseNegatives().get(1)/(eval.falseNegatives().get(1) + eval.trueNegatives().get(1));

        System.out.println("CNN");
        System.out.println("TP: " + eval.truePositives().get(1) + " | TN: " + eval.trueNegatives().get(1) + " | FP: " + eval.falsePositives().get(1) + " | FN: " + eval.falseNegatives().get(1));

        System.out.println("Precision: " + precision);
        System.out.println("Recall (PD): " + recall);
        System.out.println("PF: " + pf);
        System.out.println("F-measure: " + fmeasure);
        System.out.println("G-measure: " + gmeasure + "\n");

        ModelSerializer.writeModel(model, classifier.getSaveModelPath(), true);
    }

}
