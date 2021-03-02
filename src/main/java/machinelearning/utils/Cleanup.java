package machinelearning.utils;

import org.deeplearning4j.text.stopwords.StopWords;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Cleanup {

    public Collection<String> normalizeText(String text){
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
    public String cleanText(String text){
        Pattern charsPunctuationPattern = Pattern.compile("[\\d:,\"\'\\`\\_\\|?!\n\r@;]+");
        String input_text = charsPunctuationPattern.matcher(text.trim().toLowerCase()).replaceAll("");
        input_text = input_text.replaceAll("\\{.*?\\}", "");
        input_text = input_text.replaceAll("\\[.*?\\]", "");
        input_text = input_text.replaceAll("\\(.*?\\)", "");
        input_text = input_text.replaceAll("[^A-Za-z0-9(),!?@\'\\`\"\\_\n]", " ");
        input_text = input_text.replaceAll("[/]"," ");

        return input_text;
    }
}
