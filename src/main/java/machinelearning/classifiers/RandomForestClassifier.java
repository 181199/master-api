package machinelearning.classifiers;

import machinelearning.utility.ClassifierUtils;
import machinelearning.utility.PropertySettings;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

public class RandomForestClassifier {

    public static double recall;
    public static double precision;
    public static double fmeasure;
    public static double gmeasure;
    public static double pf;
    public static double aucroc;
    public static double TP;
    public static double TN;
    public static double FP;
    public static double FN;

    public RandomForestClassifier() {
    }

    /**
     * classifies instances
     * @param test
     * @param model
     * @returns Collection<String> labels
     */
    public static void classifyModel(Instances test, String model) throws Exception{
        // Random forest classifier
        RandomForest classifier = (RandomForest) weka.core.SerializationHelper.read(model);

        // create new Evaluation object and pass the schema of the dataset
        Evaluation eval = new Evaluation(test);

        // evaluate classifier on test-set
        eval.evaluateModel(classifier, test);

        recall = eval.recall(1)*100;
        precision = eval.precision(1)*100;
        fmeasure = eval.fMeasure(1)*100;
        gmeasure = (2 * eval.recall(1)*100*(100 - eval.falsePositiveRate(1)*100))/(eval.recall(1)*100 + (100 - eval.falsePositiveRate(1)*100));
        pf = (eval.numFalsePositives(1)/(eval.numFalsePositives(1) + eval.numTrueNegatives(1)))*100;
        aucroc = eval.areaUnderROC(1)*100;
        TP = eval.numTruePositives(1);
        TN = eval.numTrueNegatives(1);
        FP = eval.numFalsePositives(1);
        FN = eval.numFalseNegatives(1);

        ClassifierUtils.printResults(TP, TN, FP, FN, recall, precision, fmeasure, gmeasure, pf, aucroc);
    }

    /**
     * creates classification model
     * @param train
     * @param test
     * @param filePath
     * @returns Collection<String> labels
     */
    public static void classify(Instances train, Instances test, String filePath) throws Exception{
        // Random forest classifier
        RandomForest classifier = new RandomForest();

        classifier.buildClassifier(train);

        // create new Evaluation object and pass the schema of the dataset
        Evaluation eval = new Evaluation(train);

        // evaluate classifier on test-set
        eval.evaluateModel(classifier, test);

        recall = eval.recall(1)*100;
        precision = eval.precision(1)*100;
        fmeasure = eval.fMeasure(1)*100;
        gmeasure = (2 * eval.recall(1)*100*(100 - eval.falsePositiveRate(1)*100))/(eval.recall(1)*100 + (100 - eval.falsePositiveRate(1)*100));
        pf = (eval.numFalsePositives(1)/(eval.numFalsePositives(1) + eval.numTrueNegatives(1)))*100;
        aucroc = eval.areaUnderROC(1)*100;
        TP = eval.numTruePositives(1);
        TN = eval.numTrueNegatives(1);
        FP = eval.numFalsePositives(1);
        FN = eval.numFalseNegatives(1);

        ClassifierUtils.printResults(TP, TN, FP, FN, recall, precision, fmeasure, gmeasure, pf, aucroc);

        weka.core.SerializationHelper.write(filePath, classifier);
    }

    /**
     * classifies instances and prints results to file
     * @param dataset
     * @param test
     * @param model
     * @param outfile
     * @returns Collection<String> labels
     */
    public void classifyAndPrint(String dataset, Instances test, String model, String outfile) throws Exception {
        // Random forest classifier
        RandomForest classifier = (RandomForest) weka.core.SerializationHelper.read(model);

        // create new Evaluation object and pass the schema of the dataset
        Evaluation eval = new Evaluation(test);

        // evaluate classifier on test-set
        eval.evaluateModel(classifier, test);

        recall = eval.recall(1)*100;
        precision = eval.precision(1)*100;
        fmeasure = eval.fMeasure(1)*100;
        gmeasure = (2 * eval.recall(1)*100*(100 - eval.falsePositiveRate(1)*100))/(eval.recall(1)*100 + (100 - eval.falsePositiveRate(1)*100));
        pf = (eval.numFalsePositives(1)/(eval.numFalsePositives(1) + eval.numTrueNegatives(1)))*100;
        aucroc = eval.areaUnderROC(1)*100;
        TP = eval.numTruePositives(1);
        TN = eval.numTrueNegatives(1);
        FP = eval.numFalsePositives(1);
        FN = eval.numFalseNegatives(1);

        ClassifierUtils.printResults(TP, TN, FP, FN, recall, precision, fmeasure, gmeasure, pf, aucroc);

        try(BufferedReader br = new BufferedReader(new FileReader(dataset))) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(outfile))) {

                String line = "";
                int i = 0;
                while((line=br.readLine())!=null && i < test.size()) {
                    if(i == 0){
                        i++;
                        bw.write(line + PropertySettings.SEPARATOR + "Predicted" + "\n");
                        continue;
                    }
                        double label = classifier.classifyInstance(test.instance(i-1));
                        test.instance(i-1).setClassValue(label);

                        bw.write(line + PropertySettings.SEPARATOR + label + "\n");
                        i++;
                }
            }
        }
    }

    public double getRecall() {
        return recall;
    }

    public void setRecall(double recall) {
        this.recall = recall;
    }

    public double getPrecision() {
        return precision;
    }

    public void setPrecision(double precision) {
        this.precision = precision;
    }

    public double getFmeasure() {
        return fmeasure;
    }

    public void setFmeasure(double fmeasure) {
        this.fmeasure = fmeasure;
    }

    public double getGmeasure() {
        return gmeasure;
    }

    public void setGmeasure(double gmeasure) {
        this.gmeasure = gmeasure;
    }

    public double getPf() {
        return pf;
    }

    public void setPf(double pf) {
        this.pf = pf;
    }

    public double getAucroc() {
        return aucroc;
    }

    public void setAucroc(double aucroc) {
        this.aucroc = aucroc;
    }

    public double getTP() {
        return TP;
    }

    public void setTP(double TP) {
        this.TP = TP;
    }

    public double getTN() {
        return TN;
    }

    public void setTN(double TN) {
        this.TN = TN;
    }

    public double getFP() {
        return FP;
    }

    public void setFP(double FP) {
        this.FP = FP;
    }

    public double getFN() {
        return FN;
    }

    public void setFN(double FN) {
        this.FN = FN;
    }
}
