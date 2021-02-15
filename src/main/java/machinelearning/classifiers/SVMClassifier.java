package machinelearning.classifiers;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.SelectedTag;

public class SVMClassifier {

    public SVMClassifier() {
    }

    public static void classifyModel(Instances test, String model) throws Exception {
        // configure SVM
        LibSVM classifier = (LibSVM) weka.core.SerializationHelper.read(model);

        // create new Evaluation object and pass the schema of the dataset
        Evaluation eval = new Evaluation(test);

        // evaluate classifier on test-set
        eval.evaluateModel(classifier, test);

        System.out.println("SVM:");
        // print some stats about the result:
        System.out.println(eval.toSummaryString());
        // more details:
        System.out.println(eval.toClassDetailsString());
        // print confusion matrix
        System.out.println(eval.toMatrixString());
    }

    public static void classify(Instances train, Instances test, String dataset) throws Exception {
        // configure SVM
        LibSVM classifier = new LibSVM();
        classifier.setKernelType(new SelectedTag(LibSVM.KERNELTYPE_RBF,LibSVM.TAGS_KERNELTYPE)); // type: RBF
        classifier.setCost(100.0);		// the C parameter
        classifier.setProbabilityEstimates(false);
        classifier.setDoNotReplaceMissingValues(true);


        classifier.buildClassifier(train);

        // create new Evaluation object and pass the schema of the dataset
        Evaluation eval = new Evaluation(train);

        // evaluate classifier on test-set
        eval.evaluateModel(classifier, test);

        System.out.println("SVM:");
        // print some stats about the result:
        System.out.println(eval.toSummaryString());
        // more details:
        System.out.println(eval.toClassDetailsString());
        // print confusion matrix
        System.out.println(eval.toMatrixString());

        weka.core.SerializationHelper.write("./files/models/" + dataset + "_svm.model", classifier);
    }
}
