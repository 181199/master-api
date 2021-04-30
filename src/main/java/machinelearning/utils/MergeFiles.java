package machinelearning.utils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MergeFiles {

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
                String[] cols = line.split(PropertySettings.SEPARATOR);

                if(i == 0){
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

    public static void fixWord2VecFiles(String directory){
        try (Stream<Path> walk = Files.walk(Paths.get(directory))) {

            List<String> result = walk.filter(Files::isRegularFile)
                    .map(x -> x.toString()).collect(Collectors.toList());

            result.forEach(System.out::println);

                for (int i = 0; i < result.size(); i++) {

                    if ((result.get(i).contains("chromium") && !result.get(i).contains("fixed"))) {

                        try (BufferedReader br = new BufferedReader(new FileReader(result.get(i)))) {
                            String file = result.get(i);
                            String newFile = file.substring(0, file.length()-4) + "_fixed.csv";

                            try (BufferedWriter bw = new BufferedWriter(new FileWriter(newFile, false))) {

                                //System.out.println("\ufffd");

                                String line = "";
                                while ((line = br.readLine()) != null) {
                                    String newLine = line.replace("\ufffd", "0.0");
                                    bw.write(newLine + "\n");
                                }
                            }
                        }
                    }
                }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
