package similarity;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class Documents {

    //This variable will hold all terms of each document in an array.
    private List<String[]> cveDocsArray = new ArrayList<String[]>();
    private List<String[]> bugsDocsArray = new ArrayList<String[]>();
    private List<String[]> termsDocsArray = new ArrayList<String[]>();
    private List<String> allTerms = new ArrayList<String>(); //to hold all terms
    private List<String> allCveTerms = new ArrayList<String>(); //to hold all terms
    private List<String> allBugTerms = new ArrayList<String>(); //to hold all terms
    private List<double[]> tfidfDocsVectorCve = new ArrayList<double[]>();
    private List<double[]> tfidfDocsVectorBugs = new ArrayList<double[]>();

    // Method to read files and store in array.

    public void parseCveFile(String filePath) throws IOException {

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line = "";
            int i = 0;
            while ((line = br.readLine()) != null && i < 100) {
                String[] cols = line.split(";");
                String cleaned = cleanText(cols[1]);
                //sb.append(cleaned + "\n");
                //System.out.println(cleaned);

                    String[] tokenizedTerms = cleaned.replaceAll("[\\W&&[^\\s]]", "").split("\\W+");
                    for (String term : tokenizedTerms)    //avoid duplicate entries
                    {
                        if (!allCveTerms.contains(term)) {
                            allCveTerms.add(term);
                            //System.out.println(term);
                        }
                    }
                    cveDocsArray.add(tokenizedTerms);
                    i++;
            }
        }
    }

    public void parseBugFile(String filePath) throws IOException {

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line = "";
            int i = 0;
            while ((line = br.readLine()) != null && i < 100) {
                String[] cols = line.split(";");
                String cleaned = cleanText(cols[1]);
                //sb.append(cleaned + "\n");
                //System.out.println(cleaned);

                String[] tokenizedTerms = cleaned.replaceAll("[\\W&&[^\\s]]", "").split("\\W+");
                for (String term : tokenizedTerms)    //avoid duplicate entries
                {
                    if (!allBugTerms.contains(term)) {
                        allBugTerms.add(term);
                        //System.out.println(term);
                    }
                }
                bugsDocsArray.add(tokenizedTerms);
                i++;
            }
        }
    }

    /**
     * Method to create termVector according to its tfidf score.
     */
    public void tfIdfCalculatorCve() {
        double tf; //term frequency
        double idf; //inverse document frequency
        double tfidf; //term frequency inverse document frequency
        for (String[] docTermsArray : cveDocsArray) {
            double[] tfidfvectors = new double[allCveTerms.size()];
            int count = 0;
            for (String terms : allCveTerms) {
                tf = new TFIDFCalculator().tf(docTermsArray, terms);
                idf = new TFIDFCalculator().idf(cveDocsArray, terms);
                tfidf = tf * idf;
                tfidfvectors[count] = tfidf;
                count++;
            }
            tfidfDocsVectorCve.add(tfidfvectors);  //storing document vectors;
        }
    }

    public void tfIdfCalculatorBugs(){
        double tf; //term frequency
        double idf; //inverse document frequency
        double tfidf; //term frequency inverse document frequency
        for (String[] docTermsArray : bugsDocsArray)
        {
            double[] tfidfvectors = new double[allBugTerms.size()];
            int count = 0;
            for (String terms : allBugTerms)
            {
                tf = new TFIDFCalculator().tf(docTermsArray, terms);
                idf = new TFIDFCalculator().idf(bugsDocsArray, terms);
                tfidf = tf * idf;
                tfidfvectors[count] = tfidf;
                count++;
            }
            tfidfDocsVectorBugs.add(tfidfvectors);  //storing document vectors;
        }
    }

    // Method to calculate cosine similarity between all the documents.

    public void getCosineSimilarity() throws IOException {
        CosineSimilarity c = new CosineSimilarity();
        List<Double> scores = new ArrayList<Double>();
        double score = 0.0;
        double cosine = 0.0;
        for (int i = 0; i < tfidfDocsVectorBugs.size(); i++) {
            for (int j = 0; j < tfidfDocsVectorCve.size(); j++) {
                if (i != j)
                    cosine = new CosineSimilarity().cosineSimilarity(tfidfDocsVectorCve.get(i), tfidfDocsVectorBugs.get(j));
                    //System.out.println("between " + i + " and " + j + "  =  " + new CosineSimilarity().cosineSimilarity(tfidfDocsVectorBugs.get(i), tfidfDocsVectorCve.get(j)));
                    if(cosine > score) {
                        score = cosine;
                        System.out.println(score);
                    }
            }
            scores.add(score);
            score = 0.0;
        }
        appendToCsv(scores, "tfidf");
    }

    public void appendToCsv(List<Double> tfidfScores, String columnName) throws IOException {

        BufferedReader br=null;
        BufferedWriter bw=null;

        try {
            File file = new File("/Users/anja/Desktop/master/api/files/test/stackoverflowSBR_small.csv");
            File file2 = new File("/Users/anja/Desktop/master/api/files/test/stackoverflowSBR_small_" + columnName + ".csv");//so the
            //names don't conflict or just use different folders

            br = new BufferedReader(new InputStreamReader(new FileInputStream(file))) ;
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file2)));
            String line = null;
            int i=0;
            for ( line = br.readLine(); line != null; line = br.readLine(),i++)
            {
                bw.write(line + ";" + columnName + "\n");
                for(int j = 0; j < tfidfScores.size(); j++) {
                    String addedColumn = String.valueOf(tfidfScores.get(j));
                    bw.write(line + ";" + addedColumn + "\n");
                }
                break;
            }

        }catch(Exception e){
            System.out.println(e);
        }finally  {
            if(br!=null)
                br.close();
            if(bw!=null)
                bw.close();
        }
    }

    public static String cleanText(String text) {
        Pattern charsPunctuationPattern = Pattern.compile("[\\d:,\"\'\\`\\_\\|?!\n\r@;]+");
        String input_text = charsPunctuationPattern.matcher(text.trim().toLowerCase()).replaceAll("");
        input_text = input_text.replaceAll("\\{.*?\\}", "");
        input_text = input_text.replaceAll("\\[.*?\\]", "");
        input_text = input_text.replaceAll("\\(.*?\\)", "");
        input_text = input_text.replaceAll("[^A-Za-z0-9(),!?@\'\\`\"\\_\n]", " ");
        input_text = input_text.replaceAll("[/]", " ");

        return input_text;
    }
}
