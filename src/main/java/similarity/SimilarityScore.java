package similarity;

import java.io.*;
import java.util.List;

public class SimilarityScore {

    public static void main(String args[]) throws FileNotFoundException, IOException, IOException {
        String path = "/Users/anja/Desktop/master/api/files/test/";
        createDatasetFromScores(path + "stackoverflowSBR_small_tfidf_word2vec.csv",
                path + "stackoverflowSBR_new.csv", true, true);
    }

    public static void createDatasetFromScores(String filePath, String newFilePath, boolean tfidf, boolean word2vec) throws IOException {
        File file = new File(newFilePath);

        BufferedReader br = null;
        BufferedWriter bw = null;

        try {
            br = new BufferedReader(new FileReader(filePath));
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));

            String line = "";
            int i = 0;
            double tfidfScore = 0.0;
            double word2vecScore = 0.0;
            double average = 0.0;
            while ((line = br.readLine()) != null) {
                String[] cols = line.split(";");

                if(i == 0){
                    // add column for security report (1 = security, 0 != security)
                    bw.write("Security;" + cols[0] + ";" + cols[1] + ";" + cols[2] + ";" + cols[3] + "\n");
                } else {
                    tfidfScore = Double.parseDouble(cols[4]);
                    word2vecScore = Double.parseDouble(cols[5]);
                    average = (tfidfScore+word2vecScore)/2;
                    //System.out.println(average);
                }

                if(tfidf && !word2vec){
                    if(tfidfScore >= 0.6){
                        // add column for security report (1 = security, 0 != security)
                        bw.write("1;" + cols[0] + ";" + cols[1] + ";" + cols[2] + ";" + cols[3] + "\n");
                    }
                } else if(!tfidf && word2vec){
                    if(word2vecScore >= 0.6){
                        // add column for security report (1 = security, 0 != security)
                        bw.write("1;" + cols[0] + ";" + cols[1] + ";" + cols[2] + ";" + cols[3] + "\n");
                    }
                } else if(tfidf && word2vec){
                    if(average >= 0.6){
                        // add column for security report (1 = security, 0 != security)
                        bw.write("1;" + cols[0] + ";" + cols[1] + ";" + cols[2] + ";" + cols[3] + "\n");
                    }
                }
                i++;
            }
        } catch (Exception e) {
        System.out.println(e);
    } finally {
        if (br != null)
            br.close();
        if (bw != null)
            bw.close();
    }
    }
}
