package similarity;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.stopwords.StopWords;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Word2VecCalculator {

    private static Logger logger = LoggerFactory.getLogger(Word2VecCalculator.class);

    public static void main(String[] args) throws Exception {

        Word2Vec word2vec = getWord2Vec("/Users/anja/Desktop/master/api/files/word2vec_model.txt");

        List<Collection<String>> cveSentences = new ArrayList<>();
        getSentences("/Users/anja/Desktop/master/api/files/test/cveData.csv", cveSentences);

        List<Collection<String>> bugSentences = new ArrayList<>();
        getSentences("/Users/anja/Desktop/master/api/files/test/stackoverflowSBR_small.csv", bugSentences);

        getCosineSimilarity(cveSentences, bugSentences, word2vec);
    }

    public static void getCosineSimilarity(List<Collection<String>> cve, List<Collection<String>> bugs, Word2Vec word2vec) throws IOException {
        List<Double> scores = new ArrayList<Double>();
        double score = 0.0;
        double cosine_sim = 0.0;
        for(int i = 0; i < cve.size(); i++){
            for(int j = 0; j < bugs.size(); j++){
                INDArray input1_vector = getVector(cve.get(i), word2vec);
                INDArray input2_vector = getVector(bugs.get(j), word2vec);

                double dot_product = Nd4j.getBlasWrapper().dot(input1_vector, input2_vector);

                cosine_sim = cosine_similarity(input1_vector.toDoubleVector(), input2_vector.toDoubleVector(), dot_product);
                //System.out.println("Cosine similarity: " + cosine_sim);

                // use the highest score for each bug report
                if(cosine_sim > score){
                    score = cosine_sim;
                }
            }
            scores.add(score);
            System.out.println(score);
            score = 0.0;
        }
        appendToCsv(scores, "word2vec");
    }

    public static void getSentences(String file, List<Collection<String>> sentences) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = "";
            int i = 0;
            while ((line = br.readLine()) != null && i < 100) {
                String[] cols = line.split(";");
                String cleaned = cleanText(cols[1]);

                if(i != 0) {
                    Collection<String> labels = normalizeText(cleaned);
                    sentences.add(labels);
                }
                i++;
            }
        }
    }

    public static Word2Vec getWord2Vec(String path) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        File file = new File(path);
        Method method = WordVectorSerializer.class.getDeclaredMethod("readWord2VecModel", File.class);
        method.setAccessible(true);
        Word2Vec word2vec = (Word2Vec)method.invoke(null, file);
        return word2vec;
    }

    public static Collection<String> normalizeText(String text){
        Pattern charsPunctuationPattern = Pattern.compile("[\\d:,\"\'\\`\\_\\|?!\n\r@;]+");
        String input_text = charsPunctuationPattern.matcher(text.trim().toLowerCase()).replaceAll("");
        input_text = input_text.replaceAll("\\{.*?\\}", "");
        input_text = input_text.replaceAll("\\[.*?\\]", "");
        input_text = input_text.replaceAll("\\(.*?\\)", "");
        input_text = input_text.replaceAll("[^A-Za-z0-9(),!?@\'\\`\"\\_\n]", " ");
        input_text = input_text.replaceAll("[/]"," ");
        input_text = input_text.replaceAll(";"," ");
        Collection<String> labels = Arrays.asList(input_text.split(" ")).parallelStream().filter(label->label.length()>0).collect(Collectors.toList());
        labels = labels.parallelStream().filter(label ->  !StopWords.getStopWords().contains(label.trim())).collect(Collectors.toList());
        return labels;
    }

    // use the same text cleaning function as above for creating our word2vec dataset for the NN
    public static String cleanText(String text){
        Pattern charsPunctuationPattern = Pattern.compile("[\\d:,\"\'\\`\\_\\|?!\n\r@;]+");
        String input_text = charsPunctuationPattern.matcher(text.trim().toLowerCase()).replaceAll("");
        input_text = input_text.replaceAll("\\{.*?\\}", "");
        input_text = input_text.replaceAll("\\[.*?\\]", "");
        input_text = input_text.replaceAll("\\(.*?\\)", "");
        input_text = input_text.replaceAll("[^A-Za-z0-9(),!?@\'\\`\"\\_\n]", " ");
        input_text = input_text.replaceAll("[/]"," ");

        return input_text;
    }

    public static INDArray getVector(Collection<String> labels, Word2Vec word2Vec){
        double sentence_vector[] = new double[100];
        // initializing the vector
        Arrays.fill(sentence_vector,0d);
        int i=0;
        for(String word: labels)
        {
            // make vector embeddings for a one word at a time
            double[] word_vec = word2Vec.getWordVector(word);
            // array sum of vectors
            if(word_vec != null) {
                sentence_vector = (i == 0) ? word_vec : double_array_sum(word_vec, sentence_vector);
            }
            ++i;
        }

        // average of array
        return Nd4j.create(double_array_avg(sentence_vector, i));
    }

    public static double cosine_similarity(double[] input1_vector, double[] input2_vector, double dot_product)
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

    public static double[] double_array_sum(double[] word_vec, double[] sentence_vector){
        double[] sum = new double[word_vec.length];

        for (int i = 0; i < word_vec.length; i++) {
            sum[i] = word_vec[i] + sentence_vector[i];
        }
        return sum;
    }

    public static double[] double_array_avg(double[] sentence_vector, int size){
        double[] avg = new double[sentence_vector.length];
        for (int i=0; i < size; i++) {
            try {
                avg[i] = sentence_vector[i] / size;
            } catch(ArrayIndexOutOfBoundsException exception) {
            }
        }

        return avg;
    }

    public static void appendToCsv(List<Double> cosineScores, String columnName) throws IOException {

        BufferedReader br=null;
        BufferedWriter bw=null;

        try {
            File file = new File("/Users/anja/Desktop/master/api/files/test/stackoverflowSBR_small_tfidf.csv");
            File file2 = new File("/Users/anja/Desktop/master/api/files/test/stackoverflowSBR_small_tfidf_" + columnName + ".csv");//so the
            //names don't conflict or just use different folders

            br = new BufferedReader(new InputStreamReader(new FileInputStream(file))) ;
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file2)));
            String line = "";

            int i = 0;
            while ((line = br.readLine()) != null && i < cosineScores.size()) {
                if(i == 0){
                    bw.write(line + ";" + columnName + "\n");
                } else {
                    String addedColumn = String.valueOf(cosineScores.get(i));
                    bw.write(line + ";" + addedColumn + "\n");
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
