package feature_extraction;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import machinelearning.utils.PropertySettings;
import weka.core.stemmers.SnowballStemmer;
import weka.core.stemmers.Stemmer;

/**
 * @author tdoy
 *
 */
public class FeaturesTFIDF {

    private Map<String, Integer> tfreq;
    private Map<String, Double> idf;
    private List<String> documents;
    private List<String> terms;
    private List<TFIDF> tfIDF;
    private boolean STEM = false;

    public FeaturesTFIDF() {
    }

    public List<String> getFeatures(int topN) {

        List<String> features = new ArrayList<String>();

        for (int i = 0; i < topN; i++) {
            String name = tfIDF.get(i).getTerm();

            features.add(name);
        }

        return features;
    }

    public void readData(String file, boolean onlysecurityterms) throws IOException {

        documents = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = "";

        int i = 0;
        while ((line = br.readLine()) != null) {
            i++;
            if (i == 0) continue;

            line = line.toLowerCase().trim();

            if (onlysecurityterms) {
                String[] cols = line.split(PropertySettings.SEPARATOR); // this is separated by semicolon
                String sec = cols[1].trim();
                line = filterStrings(sec);
                //System.out.println(line);
                documents.add(line);
            } else {
                line = getOnlyStrings(line);
                documents.add(line);
            }
        }

        br.close();

    }

    public List<String> readStopwords(String file) throws IOException {

        List<String> stops = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = "";

        while ((line = br.readLine()) != null) {
            stops.add(line.trim());
        }

        br.close();

        return stops;
    }

    public void tokenize(List<String> stopwords) {
        Stemmer stemmer = new SnowballStemmer();
        tfreq = new HashMap<String, Integer>();
        Set<String> tokens = new HashSet<String>();
        for (String s : documents) {
            for (String t : s.split(" ")) {
                t = t.trim();
                if (!stopwords.contains(t) && t.length() > 2) {
                    // stem?
                    if (STEM = true)
                        t = stemmer.stem(t);
                    tokens.add(t);
                    if (tfreq.containsKey(t)) {
                        int value = tfreq.get(t) + 1;
                        tfreq.put(t, value);
                    } else {
                        tfreq.put(t, 1);
                    }
                }
            }
        }
        terms = new ArrayList<String>(tokens);
    }

    public void computeAggregateTFIDF() {

        idf = new HashMap<String, Double>();
        tfIDF = new ArrayList<TFIDF>();
        this.computeIDFCorpus();

        Map<String, Double> atfidf = new HashMap<String, Double>();

        for (String document : documents) {
            Map<String, Double> tfidfs = this.computeTFIDFDocument(document);
            for (String term : tfidfs.keySet()) {
                if (atfidf.containsKey(term)) {
                    double val = atfidf.get(term) + tfidfs.get(term);
                    atfidf.put(term, val);
                } else {
                    atfidf.put(term, tfidfs.get(term));
                }
            }

        }
        // store the total tfidf
        atfidf.forEach((term, tidf) -> {
            tfIDF.add(new TFIDF(term, tidf));
        });

        // sort
        tfIDF.sort(Comparator.comparingDouble(TFIDF::getTfidf).reversed());

    }

    private Map<String, Double> computeTFIDFDocument(String document) {
        Map<String, Double> tfd = new HashMap<>();
        String[] terms = document.split(" ");
        for (String term : terms) {
            if (!this.terms.contains(term)) continue;
            term = term.trim();

            if (term.isEmpty()) continue;

            if (tfd.containsKey(term)) {
                double nval = tfd.get(term) + 1;
                tfd.put(term, nval);
            } else {
                tfd.put(term, 1.0);
            }
        }
        double max = 0;
        for (Double d : tfd.values())
            max = Math.max(max, d);

        // tfidf
        for (String term : tfd.keySet()) {

            double tfreq = tfd.get(term);
            // compute tf
            double tf = 0.5 + (0.5 * tfreq / max);

            try {
                double tfidf = tf * this.idf.get(term);
                tfd.put(term, tfidf);
            } catch (Exception e) {
                System.out.println(term);
            }
        }

        return tfd;
    }

    public void computeIDFCorpus() {

        terms.forEach(term -> {
            int appear = 0;
            double N = documents.size();
            for (String document : documents) {
                List<String> tokens = Arrays.asList(document.split(" "));
                if (tokens.contains(term.trim()))
                    appear++;
            }

            double t_idf = Math.log(N / ((double) appear + 1));

            idf.put(term, t_idf);
        });

    }


    private String filterStrings(String s) {
        Pattern pattern = Pattern.compile("[^a-z A-Z]");
        Matcher matcher = pattern.matcher(s);
        String string = matcher.replaceAll("");

        return string;
    }

    public static String getOnlyStrings(String s) {
        Pattern pattern = Pattern.compile("[^a-z A-Z]");
        Matcher matcher = pattern.matcher(s);
        String string = matcher.replaceAll(" ");

        return string;
    }

    /**
     * @return the terms
     */
    public List<String> getTerms() {
        return terms;
    }

    public static void createFeatureFile(String datafile, String stopfile, String dictfile, int numFeatures) throws IOException {
        FeaturesTFIDF tfidf = new FeaturesTFIDF();

        List<String> stops = tfidf.readStopwords(stopfile);
        tfidf.readData(datafile, true);
        tfidf.tokenize(stops);

        tfidf.computeAggregateTFIDF();

        List<String> features = tfidf.getFeatures(numFeatures);

        PrintWriter pw = new PrintWriter(dictfile);
        for(int i = 0; i < features.size(); i++){
            pw.append(features.get(i) + "\n");
        }
        pw.close();
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        String datafile = "/Users/anja/Desktop/master/api/files/sources/capec.csv";
        String stopfile = "/Users/anja/Desktop/master/api/files/stopwords.txt";
        String dictfile = "/Users/anja/Desktop/master/api/files/features/CAPECFeaturesTFIDF500.txt";

        FeaturesTFIDF tfidf = new FeaturesTFIDF();

        List<String> stops = tfidf.readStopwords(stopfile);
        tfidf.readData(datafile, true);
        tfidf.tokenize(stops);

        tfidf.computeAggregateTFIDF();

        List<String> features = tfidf.getFeatures(500);

        PrintWriter pw = new PrintWriter(dictfile);
        for(int i = 0; i < features.size(); i++){
            pw.append(features.get(i) + "\n");
        }
        pw.close();

    }
}