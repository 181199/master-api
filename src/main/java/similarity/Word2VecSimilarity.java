package similarity;

import machinelearning.utility.Cleanup;
import machinelearning.utility.PropertySettings;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.util.*;

public class Word2VecSimilarity {

    /**
     * Method to get cleaned sentences from data file and store in file
     *
     * @param file
     * @param sentences
     * @param descriptionIndex index of description in data file
     */
    public void getSentences(String file, List<Collection<String>> sentences, int descriptionIndex) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = "";
            int i = 0;
            while ((line = br.readLine()) != null) {
                String[] cols = line.split(PropertySettings.SEPARATOR);
                String cleaned = new Cleanup().cleanText(cols[descriptionIndex]);

                if (PropertySettings.HEADER && i == 0) {
                    i++;
                    continue;
                }
                Collection<String> labels = new Cleanup().normalizeText(cleaned);
                sentences.add(labels);
                i++;
            }
        }
    }

    /**
     * Method to get word2vec model from file
     */
    public Word2Vec getWord2Vec(String path) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        File file = new File(path);
        Method method = WordVectorSerializer.class.getDeclaredMethod("readWord2VecModel", File.class);
        method.setAccessible(true);
        Word2Vec word2vec = (Word2Vec) method.invoke(null, file);
        return word2vec;
    }

    /**
     * Method to write word2vec word vectors to file
     *
     * @param infile           data file
     * @param outfile          new word vector file
     * @param word2vec         word2vec model file
     * @param vector_length    length of word vector
     * @param descriptionIndex index of description in data file
     */
    public void writeWord2Vectors(String infile, String outfile, Word2Vec word2vec, int vector_length, int descriptionIndex) {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outfile))) {

            try (BufferedReader br = new BufferedReader(new FileReader(infile))) {
                String line = "";
                while ((line = br.readLine()) != null) {

                    String[] toks = line.split(PropertySettings.SEPARATOR);
                    Collection<String> tokens = new Cleanup().normalizeText(toks[descriptionIndex]);
                    INDArray invector = getVector(tokens, word2vec, vector_length);

                    String rowvecpluslabel = getWordVectorsAndLabel(invector);
                    rowvecpluslabel = rowvecpluslabel + toks[PropertySettings.CLASS_INDEX];        // append the label at the end
                    bw.write(rowvecpluslabel + "\n");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to get word2vec vectors and label
     */
    private String getWordVectorsAndLabel(INDArray vec) {

        String rowvecs = "";
        for (int i = 0; i < vec.columns() - 1; i++) {
            NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
            nf.setMaximumFractionDigits(6);
            String val = nf.format(vec.getRow(0).getDouble(i));
            rowvecs += val + PropertySettings.SEPARATOR;
        }

        return rowvecs;
    }

    /**
     * Method to get word2vec vectors from collection of strings
     *
     * @param labels        collection of strings
     * @param word2Vec      word2vec model file
     * @param vector_length length of word vector
     * @return INDArray word vector
     */
    public INDArray getVector(Collection<String> labels, Word2Vec word2Vec, int vector_length) {
        double sentence_vector[] = new double[vector_length];
        // initializing the vector
        Arrays.fill(sentence_vector, 0d);
        int i = 0;
        for (String word : labels) {
            try {
                boolean skip = false;
                // make vector embeddings for a one word at a time
                double[] word_vec = word2Vec.getWordVector(word);
                for (double str : word_vec) {
                    if (Double.isNaN(str) || Double.isInfinite(str)) {
                        skip = true;
                    }
                }

                if (skip) {
                    continue;
                }
                //System.out.println("word_vec_len: " + word_vec.length);
                // array sum of vectors
                if (word_vec != null) {
                    sentence_vector = (i == 0) ? word_vec : double_array_sum(word_vec, sentence_vector);
                }
                ++i;
            } catch (Exception e) {
                //System.out.println("Exception: " + word);
                //e.printStackTrace();
            }
        }

        // average of array
        return Nd4j.create(double_array_avg(sentence_vector, i));
    }

    /**
     * Method to get cosine similarity between two input vectors
     */
    public double cosine_similarity(double[] input1_vector, double[] input2_vector, double dot_product) {
        double norm_a = 0.0;
        double norm_b = 0.0;

        for (int i = 0; i < input1_vector.length; i++) {
            norm_a += Math.pow(input1_vector[i], 2);
            norm_b += Math.pow(input2_vector[i], 2);
        }
        double cosine_sim = (dot_product / (Math.sqrt(norm_a) * Math.sqrt(norm_b)));
        return cosine_sim;
    }

    /**
     * Method to get sum of array
     */
    private double[] double_array_sum(double[] word_vec, double[] sentence_vector) {
        double[] sum = new double[word_vec.length];

        for (int i = 0; i < word_vec.length; i++) {
            sum[i] = word_vec[i] + sentence_vector[i];
        }
        return sum;
    }

    /**
     * Method to get average of array
     */
    private double[] double_array_avg(double[] sentence_vector, int size) {
        double[] avg = new double[sentence_vector.length];
        for (int i = 0; i < avg.length; i++) {
            avg[i] = sentence_vector[i] / size;
        }
        return avg;
    }
}
