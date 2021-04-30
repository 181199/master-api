package similarity;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class UsingSimilarity {

    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, IOException {

        Similarity sim = new Similarity.Builder()
                .build();

        sim.run();
    }
}
