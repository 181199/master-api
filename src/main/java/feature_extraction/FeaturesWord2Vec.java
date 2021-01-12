package feature_extraction;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.regex.Pattern;

public class FeaturesWord2Vec {

    private static Logger log = LoggerFactory.getLogger(FeaturesWord2Vec.class);

    public static String dataLocalPath;

    public static void main(String[] args) throws Exception {

        createCleanedTextFile("./files/cveData.csv", "./files/sentences.txt");
        String filePath = new File(dataLocalPath,"./files/sentences.txt").getAbsolutePath();

        log.info("Load & Vectorize Sentences....");
        // Strip white space before and after for each line
        SentenceIterator iter = new BasicLineIterator(filePath);
        // Split on white spaces in the line to get words
        TokenizerFactory t = new DefaultTokenizerFactory();

        t.setTokenPreProcessor(new CommonPreprocessor());

        log.info("Building model....");
        Word2Vec vec = new Word2Vec.Builder()
                .minWordFrequency(1)
                .iterations(1)
                .layerSize(100)
                .seed(42)
                .windowSize(5)
                .iterate(iter)
                .tokenizerFactory(t)
                .build();

        log.info("Fitting Word2Vec model....");
        vec.fit();

        log.info("Writing word vectors to text file....");

        // saving the model
        WordVectorSerializer.writeWord2VecModel(vec, "./files/word2vec_model.txt");
    }

    public static void createCleanedTextFile(String infile, String outfile) {

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(outfile))){

            try(BufferedReader br = new BufferedReader(new FileReader(infile))){
                String line = "";
                while((line=br.readLine())!=null) {
                    String[] cols = line.split(";");
                    String cleaned = cleanText(cols[1]);
                    bw.write(cleaned+"\n");
                }

            }catch(Exception e) {
                e.printStackTrace();
            }

        }catch(Exception e) {
            //
        }
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
        input_text = input_text.trim().replaceAll(" +", " ");

        return input_text;
    }
}
