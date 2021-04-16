package machinelearning;

public class UsingClassifier {

    public static void main(String[] args) throws Exception {
        String path1 = "./files/testing/";
        String path2 = "./files/validation/";
        String path3 = "./files/experiments/tfidf/stackoverflow_CVE/";
        String saveModel = "./files/experiments/rf.model";
        String model = "./files/experiments/rf.model";
        String features = "./files/features/CVEFeaturesTFIDF.txt";

        // create model
//        Classifier classifier = new Classifier.Builder()
//                .dataset("/Users/anja/Desktop/master/api/files/experiments/stackoverflow.arff")
//                .features(features)
//                .createModel(true)
//                .saveModelPath(saveModel)
//                .learner(Classifier.RANDOMFOREST)
//                .numFolds(5)
//                .build();
//
//        classifier.run();
//
//        // test model
//        Classifier cl = new Classifier.Builder()
//                .dataset(path2 + "derby.arff")
//                .features(features)
//                .model(model)
//                .learner(Classifier.RANDOMFOREST)
//                .build();
//
//        cl.run();
//
//        // create model CNN
//        Classifier cnn = new Classifier.Builder()
//                .dataset("/Users/anja/Desktop/master/api/files/experiments/stackoverflow_word2vec.csv")
//                .createModel(true)
//                .saveModelPath("./files/experiments/cnn.model")
//                .learner(Classifier.CNN)
//                .classesCount(2)
//                .indexLabel(99)
//                .featureCount(99)
//                .build();
//
//        cnn.run();
//
//        // test model CNN
//        Classifier cnnTest = new Classifier.Builder()
//                .dataset("./files/validationWord2Vec/derby_word2vec_cve.csv")
//                .model("./files/experiments/cnn.model")
//                .learner(Classifier.CNN)
//                .classesCount(2)
//                .indexLabel(99)
//                .build();
//
//        cnnTest.run();

        Classifier cl = new Classifier.Builder()
                .dataset("files/chromium.arff")
                .features("files/features/CWE_combined_features_300.txt")
                .model("files/experiments/AU/models/external/AU_Q_CWE_SR_NSR_0.4_rf_300_avg_external2_both.model")
                .learner(Classifier.RANDOMFOREST)
                .printPreds(true)
                .datasetFileCsv("files/chromium.csv")
                .build();

        cl.run();
    }
}
