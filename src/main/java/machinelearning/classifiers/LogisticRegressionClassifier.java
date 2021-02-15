package machinelearning.classifiers;

import org.opencv.ml.LogisticRegression;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.matrix.LinearRegression;

public class LogisticRegressionClassifier {

    public LogisticRegressionClassifier(){
    }

    public static void classifyModel(Instances test, String model) throws Exception {
        // Naive bayes classifier

        Logistic classifier = (Logistic) weka.core.SerializationHelper.read(model);

        // create new Evaluation object and pass the schema of the dataset
        Evaluation eval = new Evaluation(test);

        // evaluate classifier on test-set
        eval.evaluateModel(classifier, test);

        System.out.println("Logistic regression:");
        // print some stats about the result:
        System.out.println(eval.toSummaryString());
        // more details:
        System.out.println(eval.toClassDetailsString());
        // print confusion matrix
        System.out.println(eval.toMatrixString());
    }

    public static void classify(Instances train, Instances test, String dataset) throws Exception {
        // Naive bayes classifier

        Logistic classifier = new Logistic();

        classifier.buildClassifier(train);

        // create new Evaluation object and pass the schema of the dataset
        Evaluation eval = new Evaluation(train);

        // evaluate classifier on test-set
        eval.evaluateModel(classifier, test);

        System.out.println("Logistic regression:");
        // print some stats about the result:
        System.out.println(eval.toSummaryString());
        // more details:
        System.out.println(eval.toClassDetailsString());
        // print confusion matrix
        System.out.println(eval.toMatrixString());

        weka.core.SerializationHelper.write("./files/models/" + dataset + "_lr.model", classifier);
    }
}
