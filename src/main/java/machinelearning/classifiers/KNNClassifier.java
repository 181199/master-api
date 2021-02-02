package machinelearning.classifiers;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instances;
import weka.core.SelectedTag;

public class KNNClassifier {

    public KNNClassifier(){
    }

    public static void classify(Instances train, Instances test, String dataset) throws Exception {
        // Naive bayes classifier
        IBk ibk = new IBk();

        ibk.setKNN(15);
        ibk.setDistanceWeighting(new SelectedTag(IBk.WEIGHT_INVERSE, IBk.TAGS_WEIGHTING));

        // set up FilteredClassifier
        FilteredClassifier classifier = new FilteredClassifier();
        classifier.setClassifier(ibk);

        classifier.buildClassifier(train);

        // create new Evaluation object and pass the schema of the dataset
        Evaluation eval = new Evaluation(train);

        // evaluate classifier on test-set
        eval.evaluateModel(classifier, test);

        System.out.println("Ibk:");
        // print some stats about the result:
        System.out.println(eval.toSummaryString());
        // more details:
        System.out.println(eval.toClassDetailsString());
        // print confusion matrix
        System.out.println(eval.toMatrixString());

        weka.core.SerializationHelper.write("./files/models/" + dataset + "_ibk.model", classifier);
    }
}
