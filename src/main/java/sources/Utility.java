/**
 * 
 */
package sources;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import similarity.TFIDFSimilarity;


public class Utility {

	
	/**
	 * 
	 * @param terms
	 * @param benchmarkDataset
	 * @param tfidfVecFile
	 * @throws IOException
	 */
	public static void saveTFIDFSourceVectors(String terms, String benchmarkDataset, String tfidfVecFile) throws IOException {
        
		TFIDFSimilarity d = new TFIDFSimilarity();
        List<String> features = d.getTermsFromFile(terms);

        List<String[]> sourceDocsArray = d.getDocsArrayFromCsv(benchmarkDataset);
        System.out.println("done docs");
        List<double[]> tfidfDocsVectorCve = d.tfIdfCalculator(sourceDocsArray, sourceDocsArray, features);
        
        System.out.println("done tfidf");
        
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<tfidfDocsVectorCve.size(); i++) {
        	double[] vec = tfidfDocsVectorCve.get(i);
        	for(int j=0; j<vec.length; j++) {
        		sb.append(vec[j]+";");
        	}
        	sb.append("\n");
        }
        
		PrintWriter pw = new PrintWriter(tfidfVecFile);
		pw.write(sb.toString());
		pw.close();
	}
	
	/**
	 * if num_of_feature = n then we will collect n by n vector
	 * @param tfidfVecFile
	 * @param dim
	 * @return List<double[]> tFIDFVec
	 * @throws IOException
	 */
	public static List<double[]> tFIDFVecFile(String tfidfVecFile, int num_features) throws IOException {
		
		List<double[]> tfidfDocsVector = new ArrayList<>();
		BufferedReader br = new BufferedReader(new FileReader(tfidfVecFile));
		String line = "";
		
		int row = 0;
		while((line=br.readLine())!=null) {	
			
			String[] cell = line.split(";");
			if(num_features == 0)
				num_features = cell.length;
			double[] vecs = new double[num_features];
			for(int i=0; i<num_features; i++) {
				vecs[i] = Double.valueOf(cell[i].trim());
			}
			tfidfDocsVector.add(vecs);
			++row;
			
			if(row == num_features)
				break;
		}
		
		br.close();
		
		return tfidfDocsVector;
	}
}
