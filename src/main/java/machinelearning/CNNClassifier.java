package machinelearning;

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
import org.nd4j.linalg.io.ClassPathResource;
import org.nd4j.linalg.learning.config.AdaDelta;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.File;
import java.io.IOException;

//import org.deeplearning4j.eval.Evaluation;

/**
 *
 * @author tdoy
 * This code is copied directly from:
 * https://github.com/eugenp/tutorials/tree/master/deeplearning4j
 *
 */

public class CNNClassifier {

    private static final int CLASSES_COUNT = 2;
    private static final int INDEX_LABEL = 99;				// label is appended to the end of the text in this case
    private static final int FEATURE_COUNT = 99; 			// word2vec vector size

    public static void main(String[] args) throws IOException, InterruptedException {

        String filePath = "/Users/anja/Desktop/master/api/dataset/camel_word2vec.csv";
        String saveModel = "./files/models/SO_cnn.model";

        //createModel(filePath, saveModel);

        classify(filePath);
    }

    public static void classify(String filePath) throws IOException, InterruptedException {
        DataSet allData;
        try (RecordReader recordReader = new CSVRecordReader(1, ';')) {
            recordReader.initialize(new FileSplit(new File(filePath)));

            DataSetIterator iterator = new RecordReaderDataSetIterator(recordReader, 5000, INDEX_LABEL, CLASSES_COUNT);
            allData = iterator.next();
        }

        allData.shuffle(42);

        DataNormalization normalizer = new NormalizerStandardize();
        normalizer.fit(allData);
        normalizer.transform(allData);

        MultiLayerNetwork model = ModelSerializer.restoreMultiLayerNetwork("/Users/anja/Desktop/master/api/files/models/SO_cnn.model");

        INDArray output = model.output(allData.getFeatures());

        Evaluation eval = new Evaluation(CLASSES_COUNT);
        eval.eval(allData.getLabels(), output);
        System.out.println(eval.stats());
    }

    public static void createModel(String filepath, String saveModel) throws IOException, InterruptedException {
        DataSet allData;
        try (RecordReader recordReader = new CSVRecordReader(1, ';')) {
            recordReader.initialize(new FileSplit(new File(filepath)));

            DataSetIterator iterator = new RecordReaderDataSetIterator(recordReader, 1000, INDEX_LABEL, CLASSES_COUNT);
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
                .layer(0, new DenseLayer.Builder().nIn(FEATURE_COUNT).nOut(FEATURE_COUNT)
                        .build())
                .layer(1, new DenseLayer.Builder().nIn(FEATURE_COUNT).nOut(FEATURE_COUNT)
                        .build())
                .layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .activation(Activation.SOFTMAX)
                        .nIn(FEATURE_COUNT).nOut(CLASSES_COUNT).build())
                .backpropType(BackpropType.Standard)
                .build();

        MultiLayerNetwork model = new MultiLayerNetwork(configuration);
        model.init();
        model.fit(trainingData);

        INDArray output = model.output(testData.getFeatures());

        Evaluation eval = new Evaluation(CLASSES_COUNT);
        eval.eval(testData.getLabels(), output);
        System.out.println(eval.stats());

        ModelSerializer.writeModel(model, saveModel, true);
    }
}
