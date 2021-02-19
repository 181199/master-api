package machinelearning.classifiers;

import weka.classifiers.Evaluation;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

public class RandomForestClassifier {

    public RandomForestClassifier() {
    }

    public static void classifyModel(Instances test, String model) throws Exception{
        // Random forest classifier
        RandomForest classifier = (RandomForest) weka.core.SerializationHelper.read(model);

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

        System.out.println("Random Forest:");

        System.out.println("TP: " + eval.numTruePositives(1) + " | TN: " + eval.numTrueNegatives(1) + " | FP: " + eval.numFalsePositives(1) + " | FN: " + eval.numFalseNegatives(1));

        System.out.println("Precision: " + precision);
        System.out.println("Recall (PD): " + recall);
        System.out.println("PF: " + pf);
        System.out.println("F-measure: " + fmeasure);
        System.out.println("G-measure: " + gmeasure);
        System.out.println("AUC-ROC: " + aucroc + "\n");
    }

    public static void classify(Instances train, Instances test, String filePath) throws Exception{
        // Random forest classifier
        RandomForest classifier = new RandomForest();

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

        System.out.println("Random Forest:");

        System.out.println("TP: " + eval.numTruePositives(1) + " | TN: " + eval.numTrueNegatives(1) + " | FP: " + eval.numFalsePositives(1) + " | FN: " + eval.numFalseNegatives(1));

        System.out.println("Precision: " + precision);
        System.out.println("Recall (PD): " + recall);
        System.out.println("PF: " + pf);
        System.out.println("F-measure: " + fmeasure);
        System.out.println("G-measure: " + gmeasure);
        System.out.println("AUC-ROC: " + aucroc + "\n");

        weka.core.SerializationHelper.write(filePath + "randomforest.model", classifier);
    }
}
