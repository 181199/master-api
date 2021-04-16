/**
 *
 */
package sources;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import machinelearning.utils.PropertySettings;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import machinelearning.utils.Cleanup;
import similarity.TFIDFSimilarity;
import similarity.Word2VecSimilarity;


public class StackExchangeAPIHelper extends SSLConfiguration {

    private StackExchangeAPI sed;

    public StackExchangeAPIHelper(StackExchangeAPI sed) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {

        this.sed = sed;

    }

    public void fetchAll() throws UnsupportedEncodingException, FileNotFoundException {

        StringBuilder builder = new StringBuilder();
        String columnNamesList = "Title" + PropertySettings.SEPARATOR + "Description" + PropertySettings.SEPARATOR + "Id" + PropertySettings.SEPARATOR + "Date";

        if(sed.isSecurity())
            columnNamesList += PropertySettings.SEPARATOR + "SR";
        else
            columnNamesList += PropertySettings.SEPARATOR + "NSR";

        builder.append(columnNamesList + "\n");


        HttpClient client = getHttpClient();

        String tags = sed.getTags();
        if(sed.isSecurity()) {				// security posts
            if(tags.isEmpty())
                tags = "security";
            else
                tags = sed.getTags()+PropertySettings.SEPARATOR+"security";
        }
        int page = 1;
        int counter = 0;
        boolean hasMore = false;
        while(counter <= sed.getNumRecords()) {
            String url = "";
            if(sed.isSecurity()) {
                url = sed.getApi()+"?page=" + page + "&pagesize=100&order=desc&sort=activity&tagged="
                        + "" + URLEncoder.encode(tags, "UTF-8") + "&site=" + sed.getSite() + "&filter=!--1nZwT3Ejsm&key=IT8vJtd)vD02vi1lzs5mHg((";
            } else {
                url = sed.getApi()+"?page=" + page + "&pagesize=100&order=desc&sort=activity&site="
                        + "" + sed.getSite() + "&filter=!--1nZwT3Ejsm&key=IT8vJtd)vD02vi1lzs5mHg((";
            }

            HttpGet request = new HttpGet(url);
            System.out.println("page number = "+page);

            try {
                HttpResponse response = client.execute(request);
                HttpEntity entity = response.getEntity();

                // Read the contents of an entity and return it as a String.
                String content = EntityUtils.toString(entity);

                JSONObject result = new JSONObject(content);

                JSONArray tokenList = result.getJSONArray("items");

                for (int i = 0; i < tokenList.length(); i++) {
                    JSONObject oj = tokenList.getJSONObject(i);

                    String title = oj.getString("title");
                    title = title.replace(";", "");

                    int id = oj.getInt("question_id");
                    int date = oj.getInt("creation_date");
                    Date time = new Date((long) date * 1000);

                    String newTime = new SimpleDateFormat("dd-MM-yyyy").format(time);

                    // filter: are we interested in questions greater than certain number of votes?
                    if(sed.getQuestionThreshold() > 0) {
                        int ques_score = oj.getInt("score");
                        if(ques_score < sed.getQuestionThreshold())
                            continue;
                    }

                    String body = "";
                    // filter: are we interested only in code
                    if(sed.isOnlyCode()) {
                        body = oj.getString("body");
                        if(body.contains("<code>")) {
                            body = body.substring(body.indexOf("<code>") + 6);
                            body = body.substring(0, body.indexOf("</code>"));
                            System.out.println(body);
                        } else {
                            continue;
                        }
                    } else {
                        body = oj.getString("body");
                    }
                    String cleanText = html2text(body);
                    cleanText = cleanText.replaceAll("\n", "").replaceAll("\r", "").replaceAll(";", "");

                    // filter: do we want answers as well? - then add answers to cleanText - check other answer related filters
                    if(!sed.isOnlyQuestion()) {
                        boolean is_answered = oj.getBoolean("is_answered");
                        if(is_answered) {
                            cleanText = addAnswers(cleanText, oj);
                        }
                    }

                    builder.append(title + PropertySettings.SEPARATOR);
                    builder.append(cleanText + PropertySettings.SEPARATOR);
                    builder.append(id + PropertySettings.SEPARATOR);
                    builder.append(newTime + PropertySettings.SEPARATOR);
                    if(sed.isSecurity())
                        builder.append("SR");
                    else
                        builder.append("NSR");
                    builder.append('\n');

                    counter++;

                }

                System.out.println("number of records added = "+counter);

                if(counter >= sed.getNumRecords())
                    break;

                // quota remaining
                int quota_remaining = result.getInt("quota_remaining");
                System.out.println("quota_remaining = "+quota_remaining);

                // has_more
                hasMore = result.getBoolean("has_more");
                System.out.println("has_more = "+hasMore);
                if(hasMore)
                    page++;
                else
                    break;

            }catch(Exception e) {
                System.out.println("Error in 'Items': "+e.getLocalizedMessage());
                if(hasMore)
                    page++;
                else
                    break;
            }
        }

        // print to file
        String filename = buildFilename(sed.isDataWithoutSimilarity())+".csv";
        PrintWriter pw = new PrintWriter(new File(sed.getPathToStoreResult()+"/"+filename));
        pw.write(builder.toString());
        pw.close();

    }

    public void fetch() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {

        // how many StringBuilders do we need?
        List<StringBuilder> builders = getStringBuilders();

        // where to keep the counter for each feature round
        List<Map<Integer, Integer>> counters = getcounters();

        TFIDFSimilarity d = new TFIDFSimilarity();
        List<String> features = d.getTermsFromFile(sed.getTerms().get(0));		// index 0 is for tfidf (better to change this later)
        System.out.println("size of tfidf_features = "+ features.size());

        List<String[]> docsArray = d.getDocsArrayFromCsv(sed.getSourceDataset());
        List<double[]> tfidfDocsVector = Utility.tFIDFVecFile(sed.getTfidfVectorFile(), 0);
        System.out.println("size of tfidfvec = "+ tfidfDocsVector.size());

        // do we have word2vec?
        Word2VecSimilarity w = null;
        Word2Vec model = null;
        List<Collection<String>> benchmarkSentences = new ArrayList<>();
        List<INDArray> input_vectors = new ArrayList<>();

        if(sed.getTerms().size() > 1) {
            w = new Word2VecSimilarity();

            model = w.getWord2Vec(sed.getTerms().get(1));					// index 1 is for word2vec (better to change this later)

            w.getSentences(sed.getSourceDataset(), benchmarkSentences);

        }

        HttpClient client = getHttpClient();

        int page = 1;
        int counter = 0;
        int counterTfidf = 0;
        int counterWord2Vec = 0;
        int counterAvg = 0;
        boolean hasMore = true;

        int fetchMoreErrorCounter = 0;

        String tags = sed.getTags();
        if(sed.isSecurity()) {				// security posts
            if(tags.isEmpty())
                tags = "security";
            else
                tags = sed.getTags()+","+"security";
        }

        while(counter <= sed.getNumRecords()) {
            String url = "";
            if(sed.isSecurity()) {
                url = sed.getApi()+"?page=" + page + "&pagesize=100&order=desc&sort=activity&tagged="
                        + "" + URLEncoder.encode(tags, "UTF-8") + "&site=" + sed.getSite() + "&filter=!--1nZwT3Ejsm&key=IT8vJtd)vD02vi1lzs5mHg((";
            } else {
                url = sed.getApi()+"?page=" + page + "&pagesize=100&order=desc&sort=activity&site="
                        + "" + sed.getSite() + "&filter=!--1nZwT3Ejsm&key=IT8vJtd)vD02vi1lzs5mHg((";
            }

            HttpGet request = new HttpGet(url);
            System.out.println("page number = "+page);

            try {
                HttpResponse response = client.execute(request);
                HttpEntity entity = response.getEntity();

                // Read the contents of an entity and return it as a String.
                String content = EntityUtils.toString(entity);

                JSONObject result = new JSONObject(content);

                JSONArray tokenList = result.getJSONArray("items");

                for (int i = 0; i < tokenList.length(); i++) {
                    JSONObject oj = tokenList.getJSONObject(i);

                    String title = oj.getString("title");
                    title = title.replace(";", "");

                    int id = oj.getInt("question_id");
                    int date = oj.getInt("creation_date");
                    Date time = new Date((long) date * 1000);

                    String newTime = new SimpleDateFormat("dd-MM-yyyy").format(time);

                    // filter: are we interested in questions greater than certain number of votes?
                    if(sed.getQuestionThreshold() > 0) {
                        int ques_score = oj.getInt("score");
                        if(ques_score < sed.getQuestionThreshold())
                            continue;
                    }

                    String body = "";
                    // filter: are we interested only in code
                    if(sed.isOnlyCode()) {
                        body = oj.getString("body");
                        if(body.contains("<code>")) {
                            body = body.substring(body.indexOf("<code>") + 6);
                            body = body.substring(0, body.indexOf("</code>"));
                            System.out.println(body);
                        } else {
                            continue;
                        }
                    } else {
                        body = oj.getString("body");
                    }
                    String cleanText = html2text(body);
                    cleanText = cleanText.replace("\n", "").replace("\r", "").replace(";", "");

                    // filter: do we want answers as well? - then add answers to cleanText - check other answer related filters
                    if(!sed.isOnlyQuestion()) {
                        boolean is_answered = oj.getBoolean("is_answered");
                        if(is_answered) {
                            cleanText = addAnswers(cleanText, oj);
                        }
                    }

                    // we start here: what dimension of feature are we using?
                    for(int j=1; j<=sed.getNumFeaturesFactor(); j++) {

                        int num_features = 100*j;											// multiple of 100
//                    	System.out.println("size of tfidfvec = "+ tfidfDocsVector.size());
                        List<String> tfidffeatures_i = features.subList(0, num_features);
                        List<double[]> tfidfDocsVector_i = gettfidfDocsVector_i(tfidfDocsVector, num_features);

                        for (int m = 0; m < benchmarkSentences.size(); m++) {
                            input_vectors.add(w.getVector(benchmarkSentences.get(m), model, num_features));
                        }

                        int index = j-1;
                        // check cosine similarity
                        double tfidf = 0.0;

                        counterTfidf = counters.get(0).get(index);

                        if(counterTfidf < sed.getNumRecords()) {
                            tfidf = getTFIDFScore(cleanText, tfidffeatures_i, docsArray, tfidfDocsVector_i, d, sed.getThreshold());
                            if(sed.isSecurity()) {
                                if(tfidf >= sed.getThreshold()) {
                                    String method = "tfidf";
                                    addRecords(builders, index, title, cleanText, id, newTime, method, tfidf, counters, 0);

                                }
                            } else {
                                if(tfidf <= sed.getThreshold()) {
                                    String method = "tfidf";
                                    addRecords(builders, index, title, cleanText, id, newTime, method, tfidf, counters, 0);

                                }
                            }
                        }


                        if(sed.getTerms().size() > 1) {

                            counterWord2Vec = counters.get(1).get(index);
                            counterAvg = counters.get(2).get(index);

                            double w2v = 0.0;

                            double avgscore = 0.0;

                            if(counterWord2Vec < sed.getNumRecords()) {

                                w2v = getWord2VecScore(cleanText, benchmarkSentences, input_vectors, model, w, num_features, sed.getThreshold());

                                if(sed.isSecurity()) {
                                    if(w2v >= sed.getThreshold()) {

                                        String method = "word2vec";
                                        addRecords(builders, index, title, cleanText, id, newTime, method, w2v, counters, 1);

                                    }
                                } else {
                                    if(w2v <= sed.getThreshold()) {

                                        String method = "word2vec";
                                        addRecords(builders, index, title, cleanText, id, newTime, method, w2v, counters, 1);

                                    }
                                }
                            }

                            if(counterAvg < sed.getNumRecords()) {

                                avgscore = (tfidf + w2v) / 2;

                                if(sed.isSecurity()) {
                                    if (avgscore >= sed.getThreshold()) {

                                        String method = "avgscore";
                                        addRecords(builders, index, title, cleanText, id, newTime, method, avgscore, counters, 2);
                                    }
                                } else {
                                    if (avgscore <= sed.getThreshold()) {

                                        String method = "avgscore";
                                        addRecords(builders, index, title, cleanText, id, newTime, method, avgscore, counters, 2);
                                    }
                                }
                            }

                        }

                    }

                }
                counter = getMinCounter(counters);
                System.out.println("Lowest record added = "+counter);

                if(counter >= sed.getNumRecords())
                    break;

                // quota remaining
                int quota_remaining = result.getInt("quota_remaining");
                System.out.println("quota_remaining = "+quota_remaining);

                // has_more
                hasMore = result.getBoolean("has_more");
                System.out.println("has_more = "+hasMore);
                if(hasMore)
                    page++;
                else
                    break;

            }catch(Exception e) {
                System.out.println("Error in 'Items': "+e.getLocalizedMessage());
                if(hasMore)
                    page++;
                else
                    break;
            }
        }
        // print
        writeToFile(builders, sed.isDataWithoutSimilarity());

    }

    private void writeToFile(List<StringBuilder> builders, boolean simUsed) throws FileNotFoundException {

        for(int i=1; i<=builders.size(); i++) {
            String filename = buildFilename(simUsed) + "_" + 100*i + ".csv";
            PrintWriter pw = new PrintWriter(new File(sed.getPathToStoreResult()+"/"+filename));
            pw.write(builders.get(i-1).toString());
            pw.close();
        }
    }

    private void addRecords(List<StringBuilder> builders, int index, String title, String cleanText,
                            int id, String newTime, String method, double score, List<Map<Integer, Integer>> counters,
                            int pos) {
        StringBuilder builder = builders.get(index);
        builder.append(title + PropertySettings.SEPARATOR);
        builder.append(cleanText + PropertySettings.SEPARATOR);
        builder.append(id + PropertySettings.SEPARATOR);
        builder.append(newTime + PropertySettings.SEPARATOR);
        builder.append(method + PropertySettings.SEPARATOR);
        if(sed.isAppendScoreToCsv())
            builder.append(score);
        builder.append('\n');

        Map<Integer, Integer> countsOfmethod = counters.get(pos);
        int counter = countsOfmethod.get(index);
        countsOfmethod.replace(index, ++counter);
    }

    private String addAnswers(String cleanText, JSONObject oj) {
        //filter: are we interested in answers based on threshold -
        // go through the answers
        StringBuilder sb = new StringBuilder();
        try {
            JSONArray answers = oj.getJSONArray("answers");
            for(int i=0; i<answers.length(); i++) {
                JSONObject answer = answers.getJSONObject(i);
                if(sed.getAnswerThreshold() > 0) {
                    int score = answer.getInt("score");
                    if(score < sed.getAnswerThreshold()) {
                        continue;
                    }
                }

//	          	String ans_body = ((JSONObject)answer).getString("body");
                String ans_body = "";
                // filter: are we interested only in code
                if(sed.isOnlyCode()) {
                    ans_body = ((JSONObject)answer).getString("code");
                } else {
                    ans_body = ((JSONObject)answer).getString("body");
                }
                String cleanText2 = html2text(ans_body);
                cleanText2 = cleanText2.replaceAll("\n", "").replaceAll("\r", "").replaceAll(";", "");
                sb.append(cleanText2+" ");

            }
            sb.append(cleanText);
        }catch(Exception e) {
            System.out.println("Error in addAnswers..."+ e.getMessage());
        }


        return sb.toString();
    }

    private List<StringBuilder> getStringBuilders(){

        // create N number of stringbuilders
        List<StringBuilder> builders = new ArrayList<>();

        for(int k=0; k<sed.getNumFeaturesFactor(); k++) {
            StringBuilder builder = new StringBuilder();

            String columnNamesList = "Title" + PropertySettings.SEPARATOR +"Description" + PropertySettings.SEPARATOR + "Id" + PropertySettings.SEPARATOR + "Date" + PropertySettings.SEPARATOR + "Method";
            if(sed.isAppendScoreToCsv())
                columnNamesList += PropertySettings.SEPARATOR + "Cosinesim";
            builder.append(columnNamesList + "\n");

            builders.add(builder);
        }

        return builders;
    }

    private List<Map<Integer, Integer>> getcounters() {

        List<Map<Integer, Integer>> counters = new ArrayList<>();
        int dim = 1;						// tfidf
        if(sed.getTerms().size() > 1)
            dim = 3;						// tfidf, w2v, avg
        for(int i=0; i<dim; i++) {
            Map<Integer, Integer> counter = new HashMap<>();
            // initialize
            for(int j=0; j<sed.getNumFeaturesFactor(); j++) {
                counter.put(j, 0);
            }
            counters.add(counter);
        }

        return counters;

    }

    private int getMinCounter(List<Map<Integer, Integer>> counters) {
        int min = sed.getNumRecords();

        for(int i=0; i<counters.size(); i++) {
            Map<Integer, Integer> counter = counters.get(i);
            for(int val : counter.values()) {
                min = Math.min(min, val);
            }
        }
        return min;
    }

    private String buildFilename(boolean noSimUsed) {
        String filename = "";
        if(sed.getSite().equals(StackExchangeAPI.STACKOVERFLOW))
            filename = "SO";
        if(sed.getSite().equals(StackExchangeAPI.SOFTWAREENGINEERING))
            filename = "SE";
        if(sed.getSite().equals(StackExchangeAPI.SERVERFAULT))
            filename = "SF";
        if(sed.getSite().equals(StackExchangeAPI.ASKUBUNTU))
            filename = "AU";
        if(sed.isOnlyQuestion())
            filename += "_Q";
        else
            filename += "_QA";

        if(!noSimUsed) {
            if(sed.getSource().equals("CVE"))
                filename +="_CVE";
            if(sed.getSource().equals("CWE"))
                filename +="_CWE";
            if(sed.getSource().equals("CAPEC"))
                filename +="_CAPEC";
        }

        if(sed.isSecurity())
            filename += "_SR";
        else
            filename += "_NSR";

        if(!noSimUsed) {
            // add threshold
            filename += "_"+sed.getThreshold();
        }

        return filename;
    }

    private List<double[]> gettfidfDocsVector_i(List<double[]> tfidfDocsVector, int num_feature){

        List<double[]> temp = new ArrayList<>();

        for(int i=0; i<num_feature; i++) {
            double[] docVec = tfidfDocsVector.get(i);
            double[] vec = new double[num_feature];
            for(int j=0; j<num_feature; j++) {
                vec[j] = docVec[j];
            }

            temp.add(vec);
        }

        return temp;
    }

    private HttpClient getHttpClient() {

        HttpClient client = null;
        try {
            client = sslConfiguredHttpClient();
        } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        return client;
    }


    private double getTFIDFScore(String cleanText, List<String> features, List<String[]> docsArray, List<double[]> tfidfDocsVector, TFIDFSimilarity d, double threshold) {
        // check cosine similarity
        double[] cleanTextDoc = d.getDocumentVectors(cleanText, features, docsArray);

        double score = 0.0;
        double cosine = 0.0;
        for (int k = 0; k < tfidfDocsVector.size(); k++) {
            cosine = d.getCosineSimilarityTwoDocuments(cleanTextDoc, tfidfDocsVector.get(k));

            if (cosine > score) {
                score = cosine;
            }

//            if (score >= threshold) {
//                break;
//            }
        }
        return score;
    }

    private double getWord2VecScore(String cleanText, List<Collection<String>> benchmarkSentences, List<INDArray> input_vectors, Word2Vec model, Word2VecSimilarity w, int num_features, double threshold) {
        Collection<String> sentence = new Cleanup().normalizeText(cleanText);

        double score = 0.0;
        double cosine = 0.0;
        for (int k = 0; k < benchmarkSentences.size(); k++) {
            //INDArray input1_vector = w.getVector(benchmarkSentences.get(k), model);
            INDArray input2_vector = w.getVector(sentence, model, num_features);

            double dot_product = Nd4j.getBlasWrapper().dot(input_vectors.get(k), input2_vector);

            cosine = w.cosine_similarity(input_vectors.get(k).toDoubleVector(), input2_vector.toDoubleVector(), dot_product);

            if (cosine > score) {
                score = cosine;
            }

//            if (score >= threshold) {
//                break;
//            }
        }
        return score;
    }

    private String html2text(String html) {
        return Jsoup.parse(html).text();
    }

}
