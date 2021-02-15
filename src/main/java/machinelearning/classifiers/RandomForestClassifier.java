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

        System.out.println("Random forest:");
        // print some stats about the result:
        System.out.println(eval.toSummaryString());
        // more details:
        System.out.println(eval.toClassDetailsString());
        // print confusion matrix
        System.out.println(eval.toMatrixString());
    }

    public static void classify(Instances train, Instances test, String dataset) throws Exception{
        // Random forest classifier
        RandomForest classifier = new RandomForest();

        classifier.buildClassifier(train);

        // create new Evaluation object and pass the schema of the dataset
        Evaluation eval = new Evaluation(train);

        // evaluate classifier on test-set
        eval.evaluateModel(classifier, test);

        System.out.println("Random forest:");
        // print some stats about the result:
        System.out.println(eval.toSummaryString());
        // more details:
        System.out.println(eval.toClassDetailsString());
        // print confusion matrix
        System.out.println(eval.toMatrixString());

        weka.core.SerializationHelper.write("./files/models/" + dataset + "_randomforest.model", classifier);
    }
}
