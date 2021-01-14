package similarity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SimilarityCalculator {

    public static void main(String args[]) throws FileNotFoundException, IOException, IOException {
        Documents d = new Documents();
        List<String[]> cveDocsArray = d.getDocsArrayFromCsv("/Users/anja/Desktop/master/api/files/test/cveData.csv");
        List<String[]> bugsDocsArray = d.getDocsArrayFromCsv("/Users/anja/Desktop/master/api/files/test/stackoverflowSBR_small.csv");

        List<String> terms = d.getTermsFromFile("/Users/anja/Desktop/master/api/files/FeaturesTFIDF.txt");
        //d.printTerms(terms);

        List<double[]> tfidfDocsVectorCve = d.tfIdfCalculator(cveDocsArray, terms); //calculates tfidf
        List<double[]> tfidfDocsVectorBugs = d.tfIdfCalculator(bugsDocsArray, terms);

        d.getCosineSimilarity(tfidfDocsVectorBugs, tfidfDocsVectorCve); //calculates cosine similarity

        //d.printDocumentVectors("In getProcessRecordLocked of ActivityManagerService.java isolated apps are not handled correctly. This could lead to local escalation of privilege with no additional execution privileges needed. User interaction is not needed for exploitation. Product: Android Versions: Android-8.0, Android-8.1, Android-9, and Android-10 Android ID: A-140055304", terms, cveDocsArray);

        double[] document1 = d.getDocumentVectors("In getProcessRecordLocked of ActivityManagerService.java isolated apps are not handled correctly. This could lead to local escalation of privilege with no additional execution privileges needed. User interaction is not needed for exploitation. Product: Android Versions: Android-8.0, Android-8.1, Android-9, and Android-10 Android ID: A-140055304", terms, cveDocsArray);
        //double[] document2 = d.getDocumentVectors("The idea is to encrypt a string containing session data (username and token expiry) and store it in a cookie. Whenever an HTTP request comes through the client, it checks if it can decrypt the token in the cookie and then queries the DB to check if the username exists and is allowed to access the target resource and if it has not yet expired: // create token    $token = openssl_encrypt($username . \"::\" . $expiry,  \"rc2-40-cbc\", $key, 0, \"00000000\")// then store in cookie The '::' is just a delimiter to separate the username and expiry. // on http request, verify token    $decryptedToken = openssl_decrypt($token,  \"rc2-40-cbc\", $key, 0, \"00000000\")    if($decryptedToken != false) // token is valid    // extract data    $tokenValues = explode(\"::\", $token)    $username = $tokenValues[0]    $tokenExpiry = $tokenValues[1]    // then validate username and expiry Is this safe?", terms, bugsDocsArray);

        double[] document2 = d.getDocumentVectors("In getProcessRecordLocked of ActivityManagerService.java isolated apps are not handled correctly. This could lead to local escalation of privilege with no additional execution privileges needed. User interaction is not needed for exploitation. Product: Android Versions: Android-8.0, Android-8.1, Android-9, and Android-10 Android ID: A-140055304", terms, cveDocsArray);

        d.getCosineSimilarityTwoDocuments(document1, document2);
    }
}
