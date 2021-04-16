package machinelearning;

import machinelearning.classifiers.*;
import machinelearning.utils.MyStopwordsHandler;
import machinelearning.utils.PropertySettings;
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
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import weka.core.Debug;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.core.stemmers.SnowballStemmer;
import weka.core.tokenizers.NGramTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.FixedDictionaryStringToWordVector;

import java.io.*;
import java.util.Random;
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
        if (classifier.isStem()) {
            filter.setStemmer(stemmer);
        }
        filter.setStopwordsHandler(stopword);

        Instances testSet = Filter.useFilter(testing, filter);

        if (classifier.getLearner().equals("rf")) {
            RandomForestClassifier rf = new RandomForestClassifier();
            rf.classifyModel(testSet, classifier.getModel());
            setResult(rf.getTP(), rf.getTN(), rf.getFP(), rf.getFN(), rf.getRecall(), rf.getPrecision(), rf.getFmeasure(), rf.getGmeasure(), rf.getPf(), rf.getAucroc());
        } else if (classifier.getLearner().equals("nb")) {
            NaiveBayesClassifier nb = new NaiveBayesClassifier();
            nb.classifyModel(testSet, classifier.getModel());
            setResult(nb.getTP(), nb.getTN(), nb.getFP(), nb.getFN(), nb.getRecall(), nb.getPrecision(), nb.getFmeasure(), nb.getGmeasure(), nb.getPf(), nb.getAucroc());
        } else if (classifier.getLearner().equals("svm")) {
            SVMClassifier svm = new SVMClassifier();
            svm.classifyModel(testSet, classifier.getModel());
            setResult(svm.getTP(), svm.getTN(), svm.getFP(), svm.getFN(), svm.getRecall(), svm.getPrecision(), svm.getFmeasure(), svm.getGmeasure(), svm.getPf(), svm.getAucroc());
        } else if (classifier.getLearner().equals("ibk")) {
            KNNClassifier knn = new KNNClassifier();
            knn.classifyModel(testSet, classifier.getModel());
            setResult(knn.getTP(), knn.getTN(), knn.getFP(), knn.getFN(), knn.getRecall(), knn.getPrecision(), knn.getFmeasure(), knn.getGmeasure(), knn.getPf(), knn.getAucroc());
        } else if (classifier.getLearner().equals("lr")) {
            LogisticRegressionClassifier lr = new LogisticRegressionClassifier();
            lr.classifyModel(testSet, classifier.getModel());
            setResult(lr.getTP(), lr.getTN(), lr.getFP(), lr.getFN(), lr.getRecall(), lr.getPrecision(), lr.getFmeasure(), lr.getGmeasure(), lr.getPf(), lr.getAucroc());
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
        if (classifier.isStem()) {
            filter.setStemmer(stemmer);
        }
        filter.setStopwordsHandler(stopword);

        Instances dataFiltered = Filter.useFilter(data, filter);

        Instances sample = dataFiltered;
        int numFolds = classifier.getNumFolds();

        Random r = new Random();

        // setting up train- and test-set
        sample.randomize(new Debug.Random(r.nextInt()));
        sample.stratify(numFolds);

        Instances trainingSet = sample.trainCV(numFolds, 0);
        Instances testSet = sample.testCV(numFolds, 0);

        if (classifier.getLearner().equals("rf")) {
            RandomForestClassifier rf = new RandomForestClassifier();
            rf.classify(trainingSet, testSet, classifier.getSaveModelPath());
            setResult(rf.getTP(), rf.getTN(), rf.getFP(), rf.getFN(), rf.getRecall(), rf.getPrecision(), rf.getFmeasure(), rf.getGmeasure(), rf.getPf(), rf.getAucroc());
        } else if (classifier.getLearner().equals("nb")) {
            NaiveBayesClassifier nb = new NaiveBayesClassifier();
            nb.classify(trainingSet, testSet, classifier.getSaveModelPath());
            setResult(nb.getTP(), nb.getTN(), nb.getFP(), nb.getFN(), nb.getRecall(), nb.getPrecision(), nb.getFmeasure(), nb.getGmeasure(), nb.getPf(), nb.getAucroc());
        } else if (classifier.getLearner().equals("svm")) {
            SVMClassifier svm = new SVMClassifier();
            svm.classify(trainingSet, testSet, classifier.getSaveModelPath());
            setResult(svm.getTP(), svm.getTN(), svm.getFP(), svm.getFN(), svm.getRecall(), svm.getPrecision(), svm.getFmeasure(), svm.getGmeasure(), svm.getPf(), svm.getAucroc());
        } else if (classifier.getLearner().equals("ibk")) {
            KNNClassifier knn = new KNNClassifier();
            knn.classify(trainingSet, testSet, classifier.getSaveModelPath());
            setResult(knn.getTP(), knn.getTN(), knn.getFP(), knn.getFN(), knn.getRecall(), knn.getPrecision(), knn.getFmeasure(), knn.getGmeasure(), knn.getPf(), knn.getAucroc());
        } else if (classifier.getLearner().equals("lr")) {
            LogisticRegressionClassifier lr = new LogisticRegressionClassifier();
            lr.classify(trainingSet, testSet, classifier.getSaveModelPath());
            setResult(lr.getTP(), lr.getTN(), lr.getFP(), lr.getFN(), lr.getRecall(), lr.getPrecision(), lr.getFmeasure(), lr.getGmeasure(), lr.getPf(), lr.getAucroc());
        }
    }

    private static Instances loadDataset(String path) {
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
        DataSet allData = new DataSet();
        try (RecordReader recordReader = new CSVRecordReader(1, PropertySettings.SEPARATOR)) {
            recordReader.initialize(new FileSplit(new File(classifier.getDataset())));

            DataSetIterator iterator = new RecordReaderDataSetIterator(recordReader, classifier.getDatasetSize(), classifier.getIndexLabel(), classifier.getClassesCount());
            allData = iterator.next();
        }

        Random r = new Random();

        allData.shuffle(r.nextInt());

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
        double gmeasure = (2 * eval.recall(1) * 100 * (100 - eval.falsePositiveRate(1) * 100)) / (eval.recall(1) * 100 + (100 - eval.falsePositiveRate(1) * 100));
        double pf = (double) eval.falseNegatives().get(1) / ((double) eval.falseNegatives().get(1) + (double) eval.trueNegatives().get(1));

        setResult(eval.truePositives().get(1), eval.trueNegatives().get(1), eval.falsePositives().get(1), eval.falseNegatives().get(1), recall, precision, fmeasure, gmeasure, pf, 0);

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
        try (RecordReader recordReader = new CSVRecordReader(1, PropertySettings.SEPARATOR)) {
            recordReader.initialize(new FileSplit(new File(classifier.getDataset())));

            DataSetIterator iterator = new RecordReaderDataSetIterator(recordReader, classifier.getDatasetSize(), classifier.getIndexLabel(), classifier.getClassesCount());
            allData = iterator.next();
        }

        Random r = new Random();

        allData.shuffle(r.nextInt());

        DataNormalization normalizer = new NormalizerStandardize();
        normalizer.fit(allData);
        normalizer.transform(allData);

        SplitTestAndTrain testAndTrain = allData.splitTestAndTrain(PropertySettings.TRAIN_SIZE);
        DataSet trainingData = testAndTrain.getTrain();
        DataSet testData = testAndTrain.getTest();

        int numNeurons = 10;

        MultiLayerConfiguration configuration = new NeuralNetConfiguration.Builder()
                //.iterations(1000)
                .seed(42)
                //.updater(new AdaDelta())
                .updater(new Sgd(0.015)) //0.0015
                //.updater(new Adam(0.0025))
                //.updater(new Nesterovs(0.008,0.9))
                .convolutionMode(ConvolutionMode.Same)      //This is important so we can 'stack' the results later
                .l2(3)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .maxNumLineSearchIterations(1000)
                .activation(Activation.TANH)
                .weightInit(WeightInit.XAVIER)
                //.biasInit(0.5)
                .list()
                .layer(0, new DenseLayer.Builder().nIn(classifier.getFeatureCount()).nOut(numNeurons)
                        .build())
                .layer(1, new DenseLayer.Builder().nIn(numNeurons).nOut(numNeurons)
                        .build())
                .layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .activation(Activation.SOFTMAX)
                        .nIn(numNeurons).nOut(classifier.getClassesCount()).build())
                .backpropType(BackpropType.Standard)
                .build();

        MultiLayerNetwork model = new MultiLayerNetwork(configuration);
        model.init();
        //model.fit(trainingData);
        // here is the iteration part. It means, the number of iteration = 100
        for (int i = 0; i < 10; i++) {
            model.fit(trainingData);
        }

        INDArray output = model.output(testData.getFeatures());

        Evaluation eval = new Evaluation(classifier.getClassesCount());
        eval.eval(testData.getLabels(), output);

        double recall = eval.recall(1) * 100;
        double precision = eval.precision(1) * 100;
        double fmeasure = eval.f1(1) * 100;
        double gmeasure = (2 * eval.recall(1) * 100 * (100 - eval.falsePositiveRate(1) * 100)) / (eval.recall(1) * 100 + (100 - eval.falsePositiveRate(1) * 100));
        double pf = (eval.falseNegatives().get(1) / (eval.falseNegatives().get(1) + eval.trueNegatives().get(1))) * 100;

        System.out.println("CNN");
        System.out.println("TP: " + eval.truePositives().get(1) + " | TN: " + eval.trueNegatives().get(1) + " | FP: " + eval.falsePositives().get(1) + " | FN: " + eval.falseNegatives().get(1));

        System.out.println("Precision: " + precision);
        System.out.println("Recall (PD): " + recall);
        System.out.println("PF: " + pf);
        System.out.println("F-measure: " + fmeasure);
        System.out.println("G-measure: " + gmeasure + "\n");

        setResult(eval.truePositives().get(1), eval.trueNegatives().get(1), eval.falsePositives().get(1), eval.falseNegatives().get(1), recall, precision, fmeasure, gmeasure, pf, 0);

        ModelSerializer.writeModel(model, classifier.getSaveModelPath(), true);
    }

    public void classifyAndPrint() throws Exception {
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
        if (classifier.isStem()) {
            filter.setStemmer(stemmer);
        }
        filter.setStopwordsHandler(stopword);

        Instances testSet = Filter.useFilter(testing, filter);

        if (classifier.getLearner().equals("rf")) {
            RandomForestClassifier rf = new RandomForestClassifier();
            rf.classifyAndPrint(classifier.getDatasetFileCsv(), testSet, classifier.getModel(), classifier.getDatasetFileCsv().substring(0, classifier.getDatasetFileCsv().length()-4) + "_rf_pred.csv");
            setResult(rf.getTP(), rf.getTN(), rf.getFP(), rf.getFN(), rf.getRecall(), rf.getPrecision(), rf.getFmeasure(), rf.getGmeasure(), rf.getPf(), rf.getAucroc());
        } else if (classifier.getLearner().equals("nb")) {
            NaiveBayesClassifier nb = new NaiveBayesClassifier();
            nb.classifyAndPrint(classifier.getDatasetFileCsv(), testSet, classifier.getModel(), classifier.getDatasetFileCsv().substring(0, classifier.getDatasetFileCsv().length()-4) + "_nb_pred.csv");
            setResult(nb.getTP(), nb.getTN(), nb.getFP(), nb.getFN(), nb.getRecall(), nb.getPrecision(), nb.getFmeasure(), nb.getGmeasure(), nb.getPf(), nb.getAucroc());
        } else if (classifier.getLearner().equals("svm")) {
            SVMClassifier svm = new SVMClassifier();
            svm.classifyAndPrint(classifier.getDatasetFileCsv(), testSet, classifier.getModel(), classifier.getDatasetFileCsv().substring(0, classifier.getDatasetFileCsv().length()-4) + "_svm_pred.csv");
            setResult(svm.getTP(), svm.getTN(), svm.getFP(), svm.getFN(), svm.getRecall(), svm.getPrecision(), svm.getFmeasure(), svm.getGmeasure(), svm.getPf(), svm.getAucroc());
        } else if (classifier.getLearner().equals("ibk")) {
            KNNClassifier knn = new KNNClassifier();
            knn.classifyAndPrint(classifier.getDatasetFileCsv(), testSet, classifier.getModel(), classifier.getDatasetFileCsv().substring(0, classifier.getDatasetFileCsv().length()-4) + "_knn_pred.csv");
            setResult(knn.getTP(), knn.getTN(), knn.getFP(), knn.getFN(), knn.getRecall(), knn.getPrecision(), knn.getFmeasure(), knn.getGmeasure(), knn.getPf(), knn.getAucroc());
        } else if (classifier.getLearner().equals("lr")) {
            LogisticRegressionClassifier lr = new LogisticRegressionClassifier();
            lr.classifyAndPrint(classifier.getDatasetFileCsv(), testSet, classifier.getModel(), classifier.getDatasetFileCsv().substring(0, classifier.getDatasetFileCsv().length()-4) + "_lr_pred.csv");
            setResult(lr.getTP(), lr.getTN(), lr.getFP(), lr.getFN(), lr.getRecall(), lr.getPrecision(), lr.getFmeasure(), lr.getGmeasure(), lr.getPf(), lr.getAucroc());
        }
    }

    private void setResult(double TP, double TN, double FP, double FN, double recall, double precision, double fmeasure, double gmeasure, double pf, double aucroc) {
        classifier.setTP(TP);
        classifier.setTN(TN);
        classifier.setFP(FP);
        classifier.setFN(FN);
        classifier.setRecall(recall);
        classifier.setPrecision(precision);
        classifier.setFmeasure(fmeasure);
        classifier.setGmeasure(gmeasure);
        classifier.setPf(pf);
        classifier.setAucroc(aucroc);
    }

}
