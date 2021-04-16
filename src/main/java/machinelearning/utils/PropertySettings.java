package machinelearning.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertySettings {

    public static String DATA_PATH = "./";
    public static boolean HEADER = true;
    public static double TRAIN_SIZE = 0.8;
    public static int CLASS_INDEX = 1;
    public static String SEPARATOR = ";";
    public static String STACKEXCHANGE = "2.2";
    public static String SITE = "stackoverflow";

    public void load() {

        try (InputStream input = new FileInputStream("./config.properties")) {

            Properties prop = new Properties();

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            DATA_PATH = prop.getProperty("DATA_PATH");
            HEADER = Boolean.parseBoolean(prop.getProperty("HEADER"));
            TRAIN_SIZE = Double.parseDouble(prop.getProperty("TRAIN_SIZE"));
            CLASS_INDEX = Integer.parseInt(prop.getProperty("CLASS_INDEX"));
            SEPARATOR = prop.getProperty("SEPARATOR");
            STACKEXCHANGE = prop.getProperty("STACKEXCHANGE");
            SITE = prop.getProperty("SITE");

            System.out.println(SEPARATOR);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
