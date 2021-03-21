package similarity;

import machinelearning.Classifier;
import machinelearning.utils.DataPreProcess;
import machinelearning.utils.MergeFiles;
import org.nd4j.linalg.api.ops.impl.controlflow.compat.Merge;
import sources.StackExchangeAPI;
import sources.StackExchangeAPI_Old;
import sources.Utility;
import weka.core.Instances;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String args[]) throws Exception {
        SimilarityScore s = new SimilarityScore();
        TFIDFSimilarity d = new TFIDFSimilarity();
        StackExchangeAPI_Old sapi = new StackExchangeAPI_Old();
        String dataset = "/Users/anja/Desktop/master/api/files/sources/";
        //String terms = "/Users/anja/Desktop/master/api/files/features/";
        String word2vec = "/Users/anja/Desktop/master/api/files/features/";
        String tags = "security";
        String site = "stackoverflow";
        int num_features = 200;

        MergeFiles.addSecurityLabel("files/RQ3/NSR.csv", "files/RQ3/NSR_sec.csv", false);
        MergeFiles.addSecurityLabel("files/RQ3/SR.csv", "files/RQ3/SR_sec.csv", true);

        MergeFiles.merge("files/RQ3/SR_sec.csv", "files/RQ3/NSR_sec.csv", "files/RQ3/SR_NSR.csv");

//        DataPreProcess.loadFileToARFF("files/RQ3/SR_NSR.csv", "files/RQ3/SR_NSR.arff");
//        // Get data
//        Instances data = new Instances(new FileReader("files/RQ3/SR_NSR.arff"));
//        data.setClassIndex(4);
//
//        Classifier classifier = new Classifier.Builder()
//                .dataset("files/RQ3/SR_NSR.arff")
//                .features("files/features/CWEFeaturesTFIDF200.txt")
//                .model("files/experiments/SO/models/external/SO_QA_CWE_SR_NSR_0.4_rf_200_word2vec_external2.model")
//                .learner(Classifier.RANDOMFOREST)
//                .printPreds(true)
//                .datasetFileCsv("files/RQ3/SR_NSR_pred.csv")
//                .build();
//
//        classifier.run();
//
        SimilarityScore sim = new SimilarityScore();

        List<String[]> docsArraycve = d.getDocsArrayFromCsv("files/sources/cve1.csv");
        List<String> termscve = d.getTermsFromFile("files/features/CVEFeaturesTFIDF200.txt");
        List<double[]> tfidfDocsVectorcve = d.tfIdfCalculator(docsArraycve, docsArraycve, termscve);

        //sim.mostSimilarSourceTFIDF(docsArraycve, termscve, tfidfDocsVectorcve, "files/sources/cve" + 1 + ".csv", "./files/RQ3/SR_NSR.csv", "files/features/CVEFeaturesTFIDF200.txt", "CVE");

        List<String[]> docsArraycve2 = d.getDocsArrayFromCsv("files/sources/cve2.csv");
        List<String> termscve2 = d.getTermsFromFile("files/features/CVEFeaturesTFIDF200.txt");
        List<double[]> tfidfDocsVectorcve2 = d.tfIdfCalculator(docsArraycve, docsArraycve, termscve);

        //sim.mostSimilarSourceTFIDF(docsArraycve2, termscve2, tfidfDocsVectorcve2, "files/sources/cve" + 2 + ".csv", "./files/RQ3/SR_NSR.csv", "files/features/CVEFeaturesTFIDF200.txt", "CVE");


        List<String[]> docsArraycve3 = d.getDocsArrayFromCsv("files/sources/cve3.csv");
        List<String> termscve3 = d.getTermsFromFile("files/features/CVEFeaturesTFIDF200.txt");
        List<double[]> tfidfDocsVectorcve3 = d.tfIdfCalculator(docsArraycve, docsArraycve, termscve);

        //sim.mostSimilarSourceTFIDF(docsArraycve3, termscve2, tfidfDocsVectorcve3, "files/sources/cve" + 3 + ".csv", "./files/RQ3/SR_NSR.csv", "files/features/CVEFeaturesTFIDF200.txt", "CVE");


        List<String[]> docsArraycve4 = d.getDocsArrayFromCsv("files/sources/cve4.csv");
        List<String> termscve4 = d.getTermsFromFile("files/features/CVEFeaturesTFIDF200.txt");
        List<double[]> tfidfDocsVectorcve4 = d.tfIdfCalculator(docsArraycve, docsArraycve, termscve);

        sim.mostSimilarSourceTFIDF(docsArraycve4, termscve4, tfidfDocsVectorcve4, "files/sources/cve" + 4 + ".csv", "./files/RQ3/SR_NSR.csv", "files/features/CVEFeaturesTFIDF200.txt", "CVE");


        List<String[]> docsArraycve5 = d.getDocsArrayFromCsv("files/sources/cve5.csv");
        List<String> termscve5 = d.getTermsFromFile("files/features/CVEFeaturesTFIDF200.txt");
        List<double[]> tfidfDocsVectorcve5 = d.tfIdfCalculator(docsArraycve, docsArraycve, termscve);

        sim.mostSimilarSourceTFIDF(docsArraycve5, termscve5, tfidfDocsVectorcve5, "files/sources/cve" + 5 + ".csv", "./files/RQ3/SR_NSR.csv", "files/features/CVEFeaturesTFIDF200.txt", "CVE");


        List<String[]> docsArraycve6 = d.getDocsArrayFromCsv("files/sources/cve6.csv");
        List<String> termscve6 = d.getTermsFromFile("files/features/CVEFeaturesTFIDF200.txt");
        List<double[]> tfidfDocsVectorcve6 = d.tfIdfCalculator(docsArraycve, docsArraycve, termscve);

        sim.mostSimilarSourceTFIDF(docsArraycve6, termscve6, tfidfDocsVectorcve6, "files/sources/cve" + 6 + ".csv", "./files/RQ3/SR_NSR.csv", "files/features/CVEFeaturesTFIDF200.txt", "CVE");


        List<String[]> docsArraycve7 = d.getDocsArrayFromCsv("files/sources/cve7.csv");
        List<String> termscve7 = d.getTermsFromFile("files/features/CVEFeaturesTFIDF200.txt");
        List<double[]> tfidfDocsVectorcve7 = d.tfIdfCalculator(docsArraycve, docsArraycve, termscve);

        sim.mostSimilarSourceTFIDF(docsArraycve7, termscve7, tfidfDocsVectorcve7, "files/sources/cve" + 7 + ".csv", "./files/RQ3/SR_NSR.csv", "files/features/CVEFeaturesTFIDF200.txt", "CVE");


        List<String[]> docsArraycve8 = d.getDocsArrayFromCsv("files/sources/cve8.csv");
        List<String> termscve8 = d.getTermsFromFile("files/features/CVEFeaturesTFIDF200.txt");
        List<double[]> tfidfDocsVectorcve8 = d.tfIdfCalculator(docsArraycve, docsArraycve, termscve);

        sim.mostSimilarSourceTFIDF(docsArraycve8, termscve8, tfidfDocsVectorcve8, "files/sources/cve" + 8 + ".csv", "./files/RQ3/SR_NSR.csv", "files/features/CVEFeaturesTFIDF200.txt", "CVE");


        List<String[]> docsArraycve9 = d.getDocsArrayFromCsv("files/sources/cve9.csv");
        List<String> termscve9 = d.getTermsFromFile("files/features/CVEFeaturesTFIDF200.txt");
        List<double[]> tfidfDocsVectorcve9 = d.tfIdfCalculator(docsArraycve, docsArraycve, termscve);

        sim.mostSimilarSourceTFIDF(docsArraycve9, termscve9, tfidfDocsVectorcve9, "files/sources/cve" + 9 + ".csv", "./files/RQ3/SR_NSR.csv", "files/features/CVEFeaturesTFIDF200.txt", "CVE");


        List<String[]> docsArraycve10 = d.getDocsArrayFromCsv("files/sources/cve10.csv");
        List<String> termscve10 = d.getTermsFromFile("files/features/CVEFeaturesTFIDF200.txt");
        List<double[]> tfidfDocsVectorcve10 = d.tfIdfCalculator(docsArraycve, docsArraycve, termscve);

        sim.mostSimilarSourceTFIDF(docsArraycve10, termscve10, tfidfDocsVectorcve10, "files/sources/cve" + 10 + ".csv", "./files/RQ3/SR_NSR.csv", "files/features/CVEFeaturesTFIDF200.txt", "CVE");


        List<String[]> docsArraycwe = d.getDocsArrayFromCsv("files/sources/cwe.csv");
        List<String> termscwe = d.getTermsFromFile("files/features/CWEFeaturesTFIDF200.txt");
        List<double[]> tfidfDocsVectorcwe = d.tfIdfCalculator(docsArraycwe, docsArraycwe, termscwe);

        sim.mostSimilarSourceTFIDF(docsArraycwe, termscwe, tfidfDocsVectorcwe, "files/sources/cwe.csv", "./files/RQ3/SR_NSR.csv", "files/features/CWEFeaturesTFIDF200.txt", "CWE");

        List<String[]> docsArraycapec = d.getDocsArrayFromCsv("files/sources/capec.csv");
        List<String> termscapec = d.getTermsFromFile("files/features/CAPECFeaturesTFIDF200.txt");
        List<double[]> tfidfDocsVectorcapec = d.tfIdfCalculator(docsArraycapec, docsArraycapec, termscapec);

        sim.mostSimilarSourceTFIDF(docsArraycapec, termscapec, tfidfDocsVectorcapec, "files/sources/capec.csv", "./files/RQ3/SR_NSR.csv", "files/features/CAPECFeaturesTFIDF200.txt", "CAPEC");


        List<String> val = new ArrayList<>();
        val.add("ambari");
        val.add("camel");
        val.add("chromium");
        val.add("derby");
        val.add("wicket");

        for(int i = 0; i < val.size(); i++) {

            sim.mostSimilarSourceVal(docsArraycve, termscve, tfidfDocsVectorcve, "files/sources/cve1.csv", "files/RQ2/validation/" + val.get(i) + ".csv", "files/features/CVEFeaturesTFIDF200.txt", "CVE_" + val.get(i));

            sim.mostSimilarSourceVal(docsArraycve2, termscve2, tfidfDocsVectorcve2, "files/sources/cve2.csv", "files/RQ2/validation/" + val.get(i) + ".csv", "files/features/CVEFeaturesTFIDF200.txt", "CVE_" + val.get(i));

            sim.mostSimilarSourceVal(docsArraycve3, termscve3, tfidfDocsVectorcve3, "files/sources/cve3.csv", "files/RQ2/validation/" + val.get(i) + ".csv", "files/features/CVEFeaturesTFIDF200.txt", "CVE_" + val.get(i));

            sim.mostSimilarSourceVal(docsArraycve4, termscve4, tfidfDocsVectorcve4, "files/sources/cve4.csv", "files/RQ2/validation/" + val.get(i) + ".csv", "files/features/CVEFeaturesTFIDF200.txt", "CVE_" + val.get(i));

            sim.mostSimilarSourceVal(docsArraycve5, termscve5, tfidfDocsVectorcve5, "files/sources/cve5.csv", "files/RQ2/validation/" + val.get(i) + ".csv", "files/features/CVEFeaturesTFIDF200.txt", "CVE_" + val.get(i));

            sim.mostSimilarSourceVal(docsArraycve6, termscve6, tfidfDocsVectorcve6, "files/sources/cve6.csv", "files/RQ2/validation/" + val.get(i) + ".csv", "files/features/CVEFeaturesTFIDF200.txt", "CVE_" + val.get(i));

            sim.mostSimilarSourceVal(docsArraycve7, termscve7, tfidfDocsVectorcve7, "files/sources/cve7.csv", "files/RQ2/validation/" + val.get(i) + ".csv", "files/features/CVEFeaturesTFIDF200.txt", "CVE_" + val.get(i));

            sim.mostSimilarSourceVal(docsArraycve8, termscve8, tfidfDocsVectorcve8, "files/sources/cve8.csv", "files/RQ2/validation/" + val.get(i) + ".csv", "files/features/CVEFeaturesTFIDF200.txt", "CVE_" + val.get(i));

            sim.mostSimilarSourceVal(docsArraycve9, termscve9, tfidfDocsVectorcve9, "files/sources/cve9.csv", "files/RQ2/validation/" + val.get(i) + ".csv", "files/features/CVEFeaturesTFIDF200.txt", "CVE_" + val.get(i));

            sim.mostSimilarSourceVal(docsArraycve10, termscve10, tfidfDocsVectorcve10, "files/sources/cve10.csv", "files/RQ2/validation/" + val.get(i) + ".csv", "files/features/CVEFeaturesTFIDF200.txt", "CVE_" + val.get(i));

            sim.mostSimilarSourceVal(docsArraycwe, termscwe, tfidfDocsVectorcwe, "files/sources/cwe.csv", "files/RQ2/validation/" + val.get(i) + ".csv", "files/features/CWEFeaturesTFIDF200.txt", "CWE_" + val.get(i));

            sim.mostSimilarSourceVal(docsArraycapec, termscapec, tfidfDocsVectorcapec, "files/sources/capec.csv", "files/RQ2/validation/" + val.get(i) + ".csv", "files/features/CAPECFeaturesTFIDF200.txt", "CAPEC_" + val.get(i));
        }

    }
}
