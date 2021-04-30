package feature_extraction;

import machinelearning.utils.Cleanup;
import machinelearning.utils.PropertySettings;
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

public class Word2VecModelHelper {

    private static Logger log = LoggerFactory.getLogger(Word2VecModelHelper.class);

    public static String dataLocalPath;

    Word2VecModel word2VecModel;

    public Word2VecModelHelper(Word2VecModel word2VecModel) {
        this.word2VecModel = word2VecModel;
    }

    public void createWord2VecModel() throws FileNotFoundException {
        createCleanedTextFile(word2VecModel.getFile(), word2VecModel.getFile().substring(0,word2VecModel.getFile().length()-4) + "_sentences.txt");
        String filePath = new File(dataLocalPath,word2VecModel.getFile().substring(0,word2VecModel.getFile().length()-4) + "_sentences.txt").getAbsolutePath();

        log.info("Load & Vectorize Sentences....");
        // Strip white space before and after for each line
        SentenceIterator iter = new BasicLineIterator(filePath);
        // Split on white spaces in the line to get words
        TokenizerFactory t = new DefaultTokenizerFactory();

        t.setTokenPreProcessor(new CommonPreprocessor());

        log.info("Building model....");
        Word2Vec vec = new Word2Vec.Builder()
                .minWordFrequency(word2VecModel.getMinWordFrequency())
                .iterations(word2VecModel.getIterations())
                .layerSize(word2VecModel.getLayerSize())
                .seed(word2VecModel.getSeed())
                .windowSize(word2VecModel.getWindowSize())
                .iterate(iter)
                .tokenizerFactory(t)
                .build();

        log.info("Fitting Word2Vec model....");
        vec.fit();

        log.info("Writing word vectors to text file....");

        // saving the model
        WordVectorSerializer.writeWord2VecModel(vec, word2VecModel.getModelFile());
    }

    private static void createCleanedTextFile(String infile, String outfile) {

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(outfile))){

            try(BufferedReader br = new BufferedReader(new FileReader(infile))){
                String line = "";
                while((line=br.readLine())!=null) {
                    String[] cols = line.split(PropertySettings.SEPARATOR);
                    String cleaned = new Cleanup().cleanText(cols[1]);
                    bw.write(cleaned+"\n");
                }

            }catch(Exception e) {
                e.printStackTrace();
            }

        }catch(Exception e) {
            //
        }
    }
}
