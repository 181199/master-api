package machinelearning.utils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class MergeFiles {

    public static void main(String[] args) throws IOException {
        List<Path> paths = Arrays.asList(Paths.get("/Users/anja/Desktop/master/api/files/testing/stackoverflowSR_new.csv"), Paths.get("/Users/anja/Desktop/master/api/files/testing/stackoverflowNSR_new.csv"));
        List<String> mergedLines = getMergedLines(paths);
        Path target = Paths.get("/Users/anja/Desktop/master/api/files/testing/stackoverflowDataset.csv");
        Files.write(target, mergedLines, Charset.forName("UTF-8"));
    }

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
}
