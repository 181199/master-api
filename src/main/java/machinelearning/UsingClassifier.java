package machinelearning;

import sources.StackExchangeAPI;

public class UsingClassifier {

    public static void main(String[] args) throws Exception {
        String path1 = "./files/testing/";
        String path2 = "./files/validation/";
        String path3 = "./files/experiments/tfidf/stackoverflow_CVE/";
        String modelDirectory = "./files/experiments/";
        String features = "./files/features/CVEFeaturesTFIDF.txt";

        // create model
        Classifier classifier = new Classifier.Builder()
                .dataset("/Users/anja/Desktop/master/api/files/experiments/stackoverflow.arff")
                .features(features)
                .createModel(true)
                .saveModelPath(modelDirectory)
                .learner(Classifier.RANDOMFOREST)
                .build();

        classifier.run();

        // test model
        Classifier cl = new Classifier.Builder()
                .dataset(path2 + "derby.arff")
                .features(features)
                .modelDirectory(modelDirectory)
                .learner(Classifier.RANDOMFOREST)
                .build();

        cl.run();

    }
}
