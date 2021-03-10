/**
 *
 */
package sources;

import java.util.ArrayList;
import java.util.List;


public class UsingStackExchangeAPI {

    /**
     * @param args
     */
    public static void main(String[] args) {

//		this is an example of how to configure the api for data collection
//	    
//	    // additional filters
//	    int questionThreshold;
//	    int answerThreshold;
//	    boolean onlyCode;

        String src = "cve";

        List<String> terms = new ArrayList<>();
        terms.add("./files/features/"+src.toUpperCase()+"FeaturesTFIDF500.txt");
        terms.add("./files/features/"+src+"_word2vec_model.txt");

        StackExchangeAPI sed = new StackExchangeAPI.Builder()
                .site(StackExchangeAPI.STACKOVERFLOW)
                .source(src.toUpperCase())
                .sourceDataset("./files/sources/"+src+".csv")
                .terms(terms)
                .tfidfVectorFile("./files/features/"+src.toUpperCase()+"TFIDFFeaturesVec.csv")
                .numFeaturesFactor(3)
                .numRecords(1)
                .onlyQuestion(false)
                .security(false)
                .threshold(0.7)
                .appendScoreToCsv(true)
                .pathToStoreResult("./files/experiments/")
                .build();

        sed.run(); 						// start to collect data

        // for collecting data without using any similarity measures
//	    StackExchangeAPI sed = new StackExchangeAPI.Builder()
//	    		.site(StackExchangeAPI.STACKOVERFLOW)
//	    		.numRecords(10000)
//	    		.onlyQuestion(false)
//	    		.security(true)
//	    		.pathToStoreResult("/Users/tdoy/anja-master-project/api-updated/dataset")
//	    		.dataWithoutSimilarity(true)
//	    		.build();
//
//	    sed.run(); 						// start to collect data

    }

}
