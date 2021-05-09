package machinelearning.utility;

public class ClassifierUtils {

    /**
     * prints classification results to console
     * @param TP
     * @param TN
     * @param FP
     * @param FN
     * @param recall
     * @param precision
     * @param fmeasure
     * @param gmeasure
     * @param pf
     * @param aucroc
     */
    public static void printResults(double TP, double TN, double FP, double FN, double recall, double precision, double fmeasure, double gmeasure, double pf, double aucroc){
        System.out.println("TN: " + TN + " | TP: " + TP + " | FN: " + FN + " | FP: " + FP);

        System.out.println("Recall (PD): " + recall);
        System.out.println("PF: " + pf);
        System.out.println("Precision: " + precision);
        System.out.println("F-measure: " + fmeasure);
        System.out.println("G-measure: " + gmeasure);
        System.out.println("AUC-ROC: " + aucroc + "\n");
    }
}
