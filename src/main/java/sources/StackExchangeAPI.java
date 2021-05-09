/**
 *
 */
package sources;


import machinelearning.utility.PropertySettings;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;



public class StackExchangeAPI {

    public static final String STACKOVERFLOW = "stackoverflow";
    public static final String ASKUBUNTU = "askubuntu";
    public static final String SERVERFAULT = "serverfault";
    public static final String SOFTWAREENGINEERING = "softwareengineering";


    private String tags;
    private String site;
    private String api;
    private String key;
    private String source;
    private String sourceDataset;
    private List<String> terms;
    private String tfidfVectorFile;
    private int numFeaturesFactor;
    private int numRecords;
    private boolean onlyQuestion;
    private boolean security;
    private double threshold;
    private boolean appendScoreToCsv;
    private String pathToStoreResult;

    // additional filters
    private int questionThreshold;
    private int answerThreshold;
    private boolean onlyCode;

    // do we just want to collect without similarity
    private boolean dataWithoutSimilarity;


    public static class Builder {

        private String tags = "";
        private String site = "stackoverflow";
        private String api = "https://api.stackexchange.com/" + PropertySettings.STACKEXCHANGE + "/questions";		// must also think of being able to configure an access key
        private String key = PropertySettings.KEY;
        private String source = "";
        private String sourceDataset = "";
        private List<String> terms;
        private String tfidfVectorFile = "";
        private int numFeaturesFactor = 1;			// 5 means, do multiple of 100: 100, 200, 300, 400, 500
        private int numRecords = 1000;				// default. 1000 records
        private boolean onlyQuestion = true;		// default
        private boolean security = true;			// default
        private double threshold = 0.6;				// default
        private boolean appendScoreToCsv = true;	// default
        private String pathToStoreResult = "";

        // additional filters
        private int questionThreshold = 0;			// default = 0. Do we want voted questions?
        private int answerThreshold = 0;			// default = 0. Do we want voted answers?
        private boolean onlyCode = false;			// default = false. Do we want to extract only code?

        // do we just want to collect without similarity
        private boolean dataWithoutSimilarity = false;	// default

        public Builder() {
            // empty constructor
        }

        /**
         *
         * @param api - which stackexchange api should be used?
         * default = https://api.stackexchange.com/2.2/questions
         */
        public Builder stackexchangeAPI(String api) {
            this.api = api;

            return this;
        }

        /**
         *
         * @param tags - provide the tags to use for filtering post (e.g. security, iot, database, java)
         */
        public Builder tags(String tags) {
            this.tags = tags;

            return this;
        }

        /**
         * @param site - stackexchange site, e.g. stackoverflow
         * default = stackoverflow
         */
        public Builder site(String site) {
            this.site = site;

            return this;
        }

        /**
         * @param source - which security source to use as benchmark for computing similarity: CVE, CWE, CAPEC
         */
        public Builder source(String source) {
            this.source = source;

            return this;
        }

        /**
         * @param sourceDataset - full directory of the security source dataset (e.g. /User/owner/source/cve.csv)
         */
        public Builder sourceDataset(String sourceDataset) {
            this.sourceDataset = sourceDataset;

            return this;
        }

        /**
         *
         * @param terms - A List containing the full paths to each type of feature (e.g. /User/owner/features/TFIDFCVEFeatures.txt, /User/owner/features/word2vecCVEFeatures.txt)
         */
        public Builder terms(List<String> terms) {
            this.terms = terms;

            return this;
        }

        /**
         *
         * @param tfidfVectorFile - provide the file where the tfidf vector for the benchmark dataset is computed.
         */
        public Builder tfidfVectorFile(String tfidfVectorFile) {
            this.tfidfVectorFile = tfidfVectorFile;

            return this;
        }

        /**
         * @param onlyQuestion - Are we interested in only question or do we want to collect question and question+answer dataset?
         * default = true
         */
        public Builder onlyQuestion(boolean onlyQuestion) {
            this.onlyQuestion = onlyQuestion;

            return this;
        }

        /**
         *
         * @param factor - a factor showing how many multiple of 100 for each feature type. Factor of 5 means, the algorithm will collect 5 different types of dataset using 100, 200, 300, 400, 500 features
         * default = 1
         */
        public Builder numFeaturesFactor(int factor) {
            this.numFeaturesFactor = factor;

            return this;
        }

        /**
         *
         * @param numRecords - how many should we collect? default = 1000
         */
        public Builder numRecords(int numRecords) {
            this.numRecords = numRecords;

            return this;
        }

        /**
         * @param security - is it security dataset we are interested in?
         * default = true
         */
        public Builder security(boolean security) {
            this.security = security;

            return this;
        }

        /**
         * @param threshold - what threshold should be used for cosine similarity between the source and the security record?
         * default = 0.6
         */
        public Builder threshold(double threshold) {
            this.threshold = threshold;

            return this;
        }

        /**
         *
         * @param appendScoreToCsv - should we append cosine similarity score to the result file?
         */
        public Builder appendScoreToCsv(boolean appendScoreToCsv) {
            this.appendScoreToCsv = appendScoreToCsv;

            return this;
        }

        /**
         *
         * @param questionThreshold - Do we want only questions that have been voted?
         * default = 0 (accept all questions)
         */
        public Builder questionThreshold(int questionThreshold) {
            this.questionThreshold = questionThreshold;

            return this;
        }

        /**
         *
         * @param answerThreshold - Do we want voted answers?
         * default = 0 (accept all answers)
         */
        public Builder answerThreshold(int answerThreshold) {
            this.answerThreshold = answerThreshold;

            return this;
        }

        /**
         *
         * @param onlyCode - Do we want only code? What type of code language? Combine with tags (e.g. tag = java)
         */
        public Builder onlyCode(boolean onlyCode) {
            this.onlyCode = onlyCode;

            return this;
        }

        /**
         *
         * @param pathToStoreResult - full directory where results should be stored
         */
        public Builder pathToStoreResult(String pathToStoreResult) {
            this.pathToStoreResult = pathToStoreResult;

            return this;
        }

        /**
         *
         * @param dataWithoutSimilarity - do we want to collect data (SR or NSR) without any similarity measure
         */
        public Builder dataWithoutSimilarity(boolean dataWithoutSimilarity) {
            this.dataWithoutSimilarity = dataWithoutSimilarity;

            return this;
        }

        public StackExchangeAPI build() {

            StackExchangeAPI dataapi = new StackExchangeAPI();
            dataapi.tags = this.tags;
            dataapi.site = this.site;
            dataapi.api = this.api;
            dataapi.source = this.source;
            dataapi.sourceDataset = this.sourceDataset;
            dataapi.terms = this.terms;
            dataapi.tfidfVectorFile = this.tfidfVectorFile;
            dataapi.numFeaturesFactor = this.numFeaturesFactor;
            dataapi.numRecords = this.numRecords;
            dataapi.onlyQuestion = this.onlyQuestion;
            dataapi.security = this.security;
            dataapi.threshold = this.threshold;
            dataapi.appendScoreToCsv = this.appendScoreToCsv;
            dataapi.pathToStoreResult = this.pathToStoreResult;

            dataapi.questionThreshold = this.questionThreshold;
            dataapi.answerThreshold = this.answerThreshold;
            dataapi.onlyCode = this.onlyCode;

            dataapi.dataWithoutSimilarity = this.dataWithoutSimilarity;

            return dataapi;
        }

    }

    private StackExchangeAPI() {
        // private constructor
    }

    /**
     * call this method to start collecting data
     */
    public void run() throws Exception {
        //
        try {
            StackExchangeAPIHelper api = new StackExchangeAPIHelper(this);
            if(this.dataWithoutSimilarity)
                api.fetchAll();
            else
                api.fetch();
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return the tags
     */
    public String getTags() {
        return tags;
    }

    /**
     * @return the site
     */
    public String getSite() {
        return site;
    }

    /**
     * @return the api
     */
    public String getApi() {
        return api;
    }

    /**
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * @return the sourceDataset
     */
    public String getSourceDataset() {
        return sourceDataset;
    }

    /**
     * @return the terms
     */
    public List<String> getTerms() {
        return terms;
    }

    /**
     * @return the tfidfVectorFile
     */
    public String getTfidfVectorFile() {
        return tfidfVectorFile;
    }

    /**
     * @return the numFeaturesFactor
     */
    public int getNumFeaturesFactor() {
        return numFeaturesFactor;
    }

    /**
     * @return the numRecords
     */
    public int getNumRecords() {
        return numRecords;
    }

    /**
     * @return the onlyQuestion
     */
    public boolean isOnlyQuestion() {
        return onlyQuestion;
    }

    /**
     * @return the security
     */
    public boolean isSecurity() {
        return security;
    }

    /**
     * @return the threshold
     */
    public double getThreshold() {
        return threshold;
    }

    /**
     * @return the appendScoreToCsv
     */
    public boolean isAppendScoreToCsv() {
        return appendScoreToCsv;
    }

    /**
     * @return the questionThreshold
     */
    public int getQuestionThreshold() {
        return questionThreshold;
    }

    /**
     * @return the answerThreshold
     */
    public int getAnswerThreshold() {
        return answerThreshold;
    }

    /**
     * @return the onlyCode
     */
    public boolean isOnlyCode() {
        return onlyCode;
    }

    /**
     * @return the pathToStoreResult
     */
    public String getPathToStoreResult() {
        return pathToStoreResult;
    }

    /**
     * @return the dataWithoutSimilarity
     */
    public boolean isDataWithoutSimilarity() {
        return dataWithoutSimilarity;
    }
}
