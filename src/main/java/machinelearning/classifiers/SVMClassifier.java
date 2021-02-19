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

        double recall = eval.recall(1);
        double precision = eval.precision(1);
        double fmeasure = eval.fMeasure(1);
        double gmeasure = (2 * eval.recall(1)*100*(100 - eval.falsePositiveRate(1)*100))/(eval.recall(1)*100 + (100 - eval.falsePositiveRate(1)*100));
        double pf = eval.numFalsePositives(1)/(eval.numFalsePositives(1) + eval.numTrueNegatives(1));
        double aucroc = eval.areaUnderROC(1);

        System.out.println("SVM:");

        System.out.println("TP: " + eval.numTruePositives(1) + " | TN: " + eval.numTrueNegatives(1) + " | FP: " + eval.numFalsePositives(1) + " | FN: " + eval.numFalseNegatives(1));

        System.out.println("Precision: " + precision);
        System.out.println("Recall (PD): " + recall);
        System.out.println("PF: " + pf);
        System.out.println("F-measure: " + fmeasure);
        System.out.println("G-measure: " + gmeasure);
        System.out.println("AUC-ROC: " + aucroc + "\n");
    }

    public static void classify(Instances train, Instances test, String filePath) throws Exception {
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

        double recall = eval.recall(1);
        double precision = eval.precision(1);
        double fmeasure = eval.fMeasure(1);
        double gmeasure = (2 * eval.recall(1)*100*(100 - eval.falsePositiveRate(1)*100))/(eval.recall(1)*100 + (100 - eval.falsePositiveRate(1)*100));
        double pf = eval.numFalsePositives(1)/(eval.numFalsePositives(1) + eval.numTrueNegatives(1));
        double aucroc = eval.areaUnderROC(1);

        System.out.println("SVM:");

        System.out.println("TP: " + eval.numTruePositives(1) + " | TN: " + eval.numTrueNegatives(1) + " | FP: " + eval.numFalsePositives(1) + " | FN: " + eval.numFalseNegatives(1));

        System.out.println("Precision: " + precision);
        System.out.println("Recall (PD): " + recall);
        System.out.println("PF: " + pf);
        System.out.println("F-measure: " + fmeasure);
        System.out.println("G-measure: " + gmeasure);
        System.out.println("AUC-ROC: " + aucroc + "\n");

        weka.core.SerializationHelper.write(filePath + "svm.model", classifier);
    }
}
