package similarity;

import machinelearning.utils.Cleanup;
import machinelearning.utils.PropertySettings;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.stopwords.StopWords;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.shade.guava.collect.Iterables;
import org.nd4j.shade.jackson.core.sym.NameN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Word2VecSimilarity {

    private static Logger logger = LoggerFactory.getLogger(Word2VecSimilarity.class);
    private static StringBuilder builder;

    public void getCosineSimilarity(List<Collection<String>> cve, List<Collection<String>> bugs, Word2Vec word2vec, int vector_length) throws IOException {
        List<Double> scores = new ArrayList<Double>();
        double score = 0.0;
        double cosine_sim = 0.0;
        for(int i = 0; i < cve.size(); i++){
            for(int j = 0; j < bugs.size(); j++){
                INDArray input1_vector = getVector(cve.get(i), word2vec, vector_length);
                INDArray input2_vector = getVector(bugs.get(j), word2vec, vector_length);

                double dot_product = Nd4j.getBlasWrapper().dot(input1_vector, input2_vector);

                cosine_sim = cosine_similarity(input1_vector.toDoubleVector(), input2_vector.toDoubleVector(), dot_product);
                //System.out.println("Cosine similarity: " + cosine_sim);

                // use the highest score for each bug report
                if(cosine_sim > score){
                    score = cosine_sim;
                }
            }
            scores.add(score);
            //System.out.println(score);
            score = 0.0;
        }
        appendToCsv(scores, "word2vec");
    }

    public void getSentences(String file, List<Collection<String>> sentences) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = "";
            int i = 0;
            while ((line = br.readLine()) != null) {
                String[] cols = line.split(PropertySettings.SEPARATOR);
                String cleaned = new Cleanup().cleanText(cols[1]);

                if(i != 0) {
                    Collection<String> labels = new Cleanup().normalizeText(cleaned);
                    sentences.add(labels);
                }
                i++;
            }
        }
    }

    public Word2Vec getWord2Vec(String path) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        File file = new File(path);
        Method method = WordVectorSerializer.class.getDeclaredMethod("readWord2VecModel", File.class);
        method.setAccessible(true);
        Word2Vec word2vec = (Word2Vec)method.invoke(null, file);
        return word2vec;
    }

    public void writeWord2Vectors(String infile, String outfile, Word2Vec word2vec, int vector_length) {

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(outfile))){

            try(BufferedReader br = new BufferedReader(new FileReader(infile))){
                String line = "";
                int index = 0;
                while((line=br.readLine())!=null) {

                    String[] toks = line.split(PropertySettings.SEPARATOR);
                    Collection<String> tokens = null;
                    if(infile.contains("chromium")) {
                        tokens = new Cleanup().normalizeText(toks[1]);
                    } else {
                        tokens = new Cleanup().normalizeText(toks[1] + " " + toks[2]);
                    }
                    INDArray invector = getVector(tokens, word2vec, vector_length);

                    String rowvecpluslabel = getWordVectorsAndLabel(invector, index);
                    rowvecpluslabel = rowvecpluslabel+toks[0]; 		// append the label at the end
                    bw.write(rowvecpluslabel+"\n");
                    index++;
                }

            }catch(Exception e) {
                e.printStackTrace();
            }

        }catch(Exception e) {
            //
        }
    }

    private String getWordVectorsAndLabel(INDArray vec, int index) {

        String rowvecs = "";
        for(int i=0; i<vec.columns()-1; i++) {
            NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
            nf.setMaximumFractionDigits(6);
            String val = nf.format(vec.getRow(0).getDouble(i));
            rowvecs += val+PropertySettings.SEPARATOR;
        }

        return rowvecs;

    }

    public INDArray getVector(Collection<String> labels, Word2Vec word2Vec, int vector_length){
        double sentence_vector[] = new double[vector_length];
        // initializing the vector
        Arrays.fill(sentence_vector,0d);
        int i=0;
        for(String word: labels)
        {
            try {
                boolean skip = false;
                // make vector embeddings for a one word at a time
                double[] word_vec = word2Vec.getWordVector(word);
                for(double str : word_vec) {
                    if (Double.isNaN(str) || Double.isInfinite(str)){
                        skip = true;
                    }
                }

                if(skip){
                    continue;
                }
                //System.out.println("word_vec_len: " + word_vec.length);
                // array sum of vectors
                if (word_vec != null) {
                    sentence_vector = (i == 0) ? word_vec : double_array_sum(word_vec, sentence_vector);
                }
                ++i;
            } catch (Exception e){
                //System.out.println("Exception: " + word);
            }
        }

        // average of array
        return Nd4j.create(double_array_avg(sentence_vector, i));
    }

    public double cosine_similarity(double[] input1_vector, double[] input2_vector, double dot_product)
    {
        double norm_a = 0.0;
        double norm_b = 0.0;

        for (int i = 0; i < input1_vector.length; i++)
        {
            norm_a += Math.pow(input1_vector[i], 2);
            norm_b += Math.pow(input2_vector[i], 2);
        }
        double cosine_sim = (dot_product / (Math.sqrt(norm_a) * Math.sqrt(norm_b)));
        return cosine_sim;
    }

    private double[] double_array_sum(double[] word_vec, double[] sentence_vector){
        double[] sum = new double[word_vec.length];

        for (int i = 0; i < word_vec.length; i++) {
            sum[i] = word_vec[i] + sentence_vector[i];
        }
        return sum;
    }

    private double[] double_array_avg(double[] sentence_vector, int size){
        double[] avg = new double[sentence_vector.length];
        for (int i=0; i < avg.length; i++) {
            avg[i] = sentence_vector[i] / size;
        }
        return avg;
    }

    private void appendToCsv(List<Double> cosineScores, String columnName) throws IOException {

        BufferedReader br=null;
        BufferedWriter bw=null;

        try {
            File file = new File("/Users/anja/Desktop/master/api/files/testing/stackoverflowSR_small_tfidf.csv");
            File file2 = new File("/Users/anja/Desktop/master/api/files/testing/stackoverflowSR_small_tfidf_" + columnName + ".csv");//so the
            //names don't conflict or just use different folders

            br = new BufferedReader(new InputStreamReader(new FileInputStream(file))) ;
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file2)));
            String line = "";

            int i = 0;
            while ((line = br.readLine()) != null && i < cosineScores.size()) {
                if(i == 0){
                    bw.write(line + PropertySettings.SEPARATOR + columnName + "\n");
                } else {
                    String addedColumn = String.valueOf(cosineScores.get(i));
                    bw.write(line + PropertySettings.SEPARATOR + addedColumn + "\n");
                }
                i++;
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
}
