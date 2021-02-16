package machinelearning.classifiers;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

public class NaiveBayesClassifier {

    public NaiveBayesClassifier(){
    }

    public static void classifyModel(Instances test, String model) throws Exception {
        // Naive bayes classifier
        NaiveBayes classifier = (NaiveBayes) weka.core.SerializationHelper.read(model);

        // create new Evaluation object and pass the schema of the dataset
        Evaluation eval = new Evaluation(test);

        // evaluate classifier on test-set
        eval.evaluateModel(classifier, test);

        double recall = eval.recall(1);
        double precision = eval.precision(1);
        double fmeasure = eval.fMeasure(1);
        double gmeasure = (2 * eval.recall(1)*100*(100 - eval.falsePositiveRate(1)*100))/(eval.recall(1)*100 + (100 - eval.falsePositiveRate(1)*100));

        System.out.println("Naive Bayes:");

        System.out.println("TP: " + eval.numTruePositives(1) + " | TN: " + eval.numTrueNegatives(1) + " | FP: " + eval.numFalsePositives(1) + " | FN: " + eval.numFalseNegatives(1));

        System.out.println("Precision: " + precision);
        System.out.println("Recall: " + recall);
        System.out.println("F-measure: " + fmeasure);
        System.out.println("G-measure: " + gmeasure + "\n");
    }

    public static void classify(Instances train, Instances test, String dataset) throws Exception {
        // Naive bayes classifier
        NaiveBayes classifier = new NaiveBayes();

        classifier.buildClassifier(train);

        // create new Evaluation object and pass the schema of the dataset
        Evaluation eval = new Evaluation(train);

        // evaluate classifier on test-set
        eval.evaluateModel(classifier, test);

        double recall = eval.recall(1);
        double precision = eval.precision(1);
        double fmeasure = eval.fMeasure(1);
        double gmeasure = (2 * eval.recall(1)*100*(100 - eval.falsePositiveRate(1)*100))/(eval.recall(1)*100 + (100 - eval.falsePositiveRate(1)*100));

        System.out.println("Naive Bayes:");

        System.out.println("TP: " + eval.numTruePositives(1) + " | TN: " + eval.numTrueNegatives(1) + " | FP: " + eval.numFalsePositives(1) + " | FN: " + eval.numFalseNegatives(1));

        System.out.println("Precision: " + precision);
        System.out.println("Recall: " + recall);
        System.out.println("F-measure: " + fmeasure);
        System.out.println("G-measure: " + gmeasure + "\n");

        weka.core.SerializationHelper.write("./files/models/" + dataset + "_naivebayes.model", classifier);
    }
}
