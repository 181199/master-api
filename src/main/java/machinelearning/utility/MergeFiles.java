package machinelearning.utility;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class MergeFiles {

    /**
     * merge two files and keep only one header
     * @param file1
     * @param file2
     * @param newFile
     */
    public static void merge(String file1, String file2, String newFile) throws IOException {
        List<Path> paths = Arrays.asList(Paths.get(file1), Paths.get(file2));
        List<String> mergedLines = getMergedLines(paths);
        Path target = Paths.get(newFile);
        Files.write(target, mergedLines, Charset.forName("UTF-8"));
    }

    /**
     * get contents of files for merging
     * @param paths
     * @return List<String> mergedLines
     */
    private static List<String> getMergedLines(List<Path> paths) throws IOException {
        List<String> mergedLines = new ArrayList<>();
        for (Path p : paths){
            List<String> lines = Files.readAllLines(p, Charset.forName("UTF-8"));
            if (!lines.isEmpty()) {
                if (mergedLines.isEmpty()) {
                    mergedLines.add(lines.get(0)); //add header only once
                }
                mergedLines.addAll(lines.subList(1, lines.size()));
            }
        }
        return mergedLines;
    }

    /**
     * add security label to front of file
     * @param filePath
     * @param newFile
     * @param security true = security, false = not security
     */
    public static void addSecurityLabel(String filePath, String newFile, boolean security) throws IOException {
        BufferedReader br = null;
        BufferedWriter bw = null;

        int sec = 0;
        if(security){
            sec = 1;
        }

        try {
            br = new BufferedReader(new FileReader(filePath));
            bw = new BufferedWriter(new FileWriter(newFile, false));

            String line = "";
            int i = 0;
            while ((line = br.readLine()) != null) {
                String[] cols = line.split(PropertySettings.SEPARATOR);

                if(i == 0 && PropertySettings.HEADER){
                    // add column for security report (1 = security, 0 != security)
                    bw.write("Security" + PropertySettings.SEPARATOR + line + "\n");
                } else {
                    bw.write(sec + PropertySettings.SEPARATOR + line + "\n");
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
