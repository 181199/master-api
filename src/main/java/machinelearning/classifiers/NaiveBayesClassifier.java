package machinelearning.classifiers;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

public class NaiveBayesClassifier {

    public NaiveBayesClassifier(){
    }

    public static void classifyModel(Instances train, Instances test, String model) throws Exception {
        // Naive bayes classifier
        NaiveBayes classifier = (NaiveBayes) weka.core.SerializationHelper.read(model);

        classifier.buildClassifier(train);

        // create new Evaluation object and pass the schema of the dataset
        Evaluation eval = new Evaluation(train);

        // evaluate classifier on test-set
        eval.evaluateModel(classifier, test);

        System.out.println("Naive bayes:");
        // print some stats about the result:
        System.out.println(eval.toSummaryString());
        // more details:
        System.out.println(eval.toClassDetailsString());
        // print confusion matrix
        System.out.println(eval.toMatrixString());
    }

    public static void classify(Instances train, Instances test, String dataset) throws Exception {
        // Naive bayes classifier
        NaiveBayes classifier = new NaiveBayes();

        classifier.buildClassifier(train);

        // create new Evaluation object and pass the schema of the dataset
        Evaluation eval = new Evaluation(train);

        // evaluate classifier on test-set
        eval.evaluateModel(classifier, test);

        System.out.println("Naive bayes:");
        // print some stats about the result:
        System.out.println(eval.toSummaryString());
        // more details:
        System.out.println(eval.toClassDetailsString());
        // print confusion matrix
        System.out.println(eval.toMatrixString());

        weka.core.SerializationHelper.write("./files/models/" + dataset + "_naivebayes.model", classifier);
    }
}
