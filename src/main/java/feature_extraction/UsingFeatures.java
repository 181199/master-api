package feature_extraction;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class UsingFeatures {

    public static void main(String[] args) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        FeaturesTFIDF ft = new FeaturesTFIDF.Builder()
                .dataFile("files/sources/cwe.csv")
                .stopwordsFile("files/stopwords.txt")
                .newFeatureFile("./files/feat.txt")
                .numFeatures(100)
                .build();

        //ft.run();

        Word2VecModel ftmod = new Word2VecModel.Builder()
                .file("files/sources/cwe.csv")
                .newModelFile("files/w2v.txt")
                .build();

        ftmod.run();

        FeaturesWord2Vec ftw2v = new FeaturesWord2Vec.Builder()
                .modelFile("./files/w2v.txt")
                .newFeatureFile("files/features.txt")
                .numWords(100)
                .build();

        ftw2v.run();
    }
}
