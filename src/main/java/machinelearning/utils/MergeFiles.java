package machinelearning.utils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class MergeFiles {

    public static void main(String[] args) throws IOException {
        //addSecurityLabel("files/experiments/stackoverflowThreshold/stackoverflow_NSR.csv", "files/experiments/stackoverflowThreshold/stackoverflowNSR.csv", false);
        //addSecurityLabel("files/experiments/stackoverflowThreshold/stackoverflow_SR.csv", "files/experiments/stackoverflowThreshold/stackoverflowSR.csv", true);

        List<Path> paths = Arrays.asList(Paths.get("./files/experiments/tfidf/stackoverflow_CVE/stackoverflowSR.csv"), Paths.get("./files/experiments/stackoverflowNSR.csv"));
        List<String> mergedLines = getMergedLines(paths);
        Path target = Paths.get("files/experiments/tfidf/stackoverflow_CVE/stackoverflow_CVE.csv");
        Files.write(target, mergedLines, Charset.forName("UTF-8"));
    }

    public static void merge(String file1, String file2, String newFile) throws IOException {
        List<Path> paths = Arrays.asList(Paths.get(file1), Paths.get(file2));
        List<String> mergedLines = getMergedLines(paths);
        Path target = Paths.get(newFile);
        Files.write(target, mergedLines, Charset.forName("UTF-8"));
    }

    public static List<String> getMergedLines(List<Path> paths) throws IOException {
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
                String[] cols = line.split(";");

                if(i == 0){
                    // add column for security report (1 = security, 0 != security)
                    bw.write("Security;" + cols[0] + ";" + cols[1] + ";" + cols[2] + ";" + cols[3] + "\n");
                } else {
                    bw.write(sec + ";" + cols[0] + ";" + cols[1] + ";" + cols[2] + ";" + cols[3] + "\n");
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
