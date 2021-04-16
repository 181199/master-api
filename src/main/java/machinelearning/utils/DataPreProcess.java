package machinelearning.utils; /**
 * 
 */

import weka.core.Instances;
import weka.core.converters.CSVSaver;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author tdoy
 *
 */
public class DataPreProcess {

    private static String path = "./files/experiments/AU/";

	/**
	 *
	 */
	public DataPreProcess() {
		// TODO Auto-generated constructor stub
	}

	public static void loadFileToARFF(String file, String newFile) {
		// create a your own arff file from the polarity data. This must contain both the neg and the pos dataset
		File out = new File(newFile);
		File fin = new File(file);
		
		String header = "@RELATION 'security'\n" +
				"\n" + 
				"@ATTRIBUTE title string\n" +
                "@ATTRIBUTE description string\n" +
                "@ATTRIBUTE id string\n" +
                "@ATTRIBUTE date string\n" +
                "@ATTRIBUTE class-att {0,1} nominal\n" +
				"\n" + 
				"@data\n";
		
		writeHeader(header, out.getAbsolutePath());
		readWriteFile(fin.getAbsolutePath(),out.getAbsolutePath());
	}
	
	private static void writeHeader(String header, String outfile) {
		
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(outfile))){
			
			bw.write(header);
		}catch(Exception e) {
			//
		}
	}
	
	private static void readWriteFile(String path, String outfile) {
		
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(outfile, true))){
			
			try(BufferedReader br = new BufferedReader(new FileReader(path))){
				String line = "";
				
				int i=0;
				while((line=br.readLine())!=null) {
					if(i++==0) continue;									// ignore header in file
					line = line.replace("\"", "");
					line = line.replace("\\", "");
					String[] cols = line.split(PropertySettings.SEPARATOR);					// this is separated by semicolon
					bw.write("\""+cols[1]+"\","+"\""+cols[2]+"\","+"\""+cols[3]+"\","+"\""+cols[4]+"\","+cols[0]+"\n");			// cols[4]=bugreport: cols[2]=type-of-bug (sec/no-sec)
				}
				
			}catch(Exception e) {
				//
			}
		}catch(Exception e) {
			//
		}		
	}

	public static void cleanFile(String path, String outfile){
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(outfile, true))){

            try(BufferedReader br = new BufferedReader(new FileReader(path))){
                String line = "";

                bw.write("Title" + PropertySettings.SEPARATOR + "Description"+ PropertySettings.SEPARATOR + "Id" + PropertySettings.SEPARATOR + "Date");

                int i=0;
                while((line=br.readLine())!=null) {
                    if(i++==0) continue;									// ignore header in file
                    line = line.replace("\"", "");
                    line = line.replace("\\", "");
                    String[] cols = line.split(PropertySettings.SEPARATOR);					// this is separated by semicolon
                    bw.write(cols[0]+PropertySettings.SEPARATOR+new Cleanup().cleanText(cols[1])+PropertySettings.SEPARATOR+new Cleanup().cleanText(cols[2])+PropertySettings.SEPARATOR+cols[3]+PropertySettings.SEPARATOR+cols[4]+"\n");			// cols[4]=bugreport: cols[2]=type-of-bug (sec/no-sec)
                }

            }catch(Exception e) {
                //
            }
        }catch(Exception e) {
            //
        }
    }

    public static void splitDataset(String source, String data, String questions, int i){
        try(BufferedWriter bw = new BufferedWriter(new FileWriter("files/experiments/" + data + "/datasets/" + data + "_" + questions + "_" + source + "_SR_0.4_" + i + "00_tfidf.csv", false))){
            try(BufferedWriter br = new BufferedWriter(new FileWriter("files/experiments/" + data + "/datasets/" + data + "_" + questions + "_" + source + "_SR_0.4_" + i + "00_word2vec.csv", false))){
                try(BufferedWriter bt = new BufferedWriter(new FileWriter("files/experiments/" + data + "/datasets/" + data + "_" + questions + "_" + source + "_SR_0.4_" + i + "00_avg.csv", false))) {
                    try (BufferedReader bp = new BufferedReader(new FileReader("files/experiments/" + data + "/" + data + "_" + questions + "_" + source + "_SR_0.4_" + i + "00.csv"))) {
                        String line = "";

                        int j = 0;
                        while ((line = bp.readLine()) != null) {
                            if (j++ == 0){
                                bw.write(line + "\n");
                                br.write(line + "\n");
                                bt.write(line + "\n");
                            }
                            line = line.replace("\"", "");
                            line = line.replace("\\", "");
                            String[] cols = line.split(PropertySettings.SEPARATOR);
                            if (cols[4].contains("tfidf")) {
                                bw.write(line + "\n");
                            } else if (cols[4].contains("word2vec")){
                                br.write(line + "\n");
                            } else if (cols[4].contains("avgscore")){
                                bt.write(line + "\n");
                            }
                        }
                    }
                    catch(Exception e) {
                        //
                    }
                }catch(Exception e) {
                    //
                }
            }catch(Exception e) {
                //
            }
        }catch(Exception e) {
            //
        }
    }

    public static void combineFiles(String data, String questions, int numFeatures) {

        Set<String> records = new HashSet<>();

        try (BufferedReader br = new BufferedReader(new FileReader("files/experiments/" + data + "/" + data + "_" + questions + "_" + "CVE" + "_SR_0.4_" + numFeatures + ".csv"))) {
            String line = "";
            while ((line = br.readLine()) != null) {
                records.add(line);
            }

            try (BufferedReader bp = new BufferedReader(new FileReader("files/experiments/" + data + "/" + data + "_" + questions + "_" + "CWE" + "_SR_0.4_" + numFeatures + ".csv"))) {

                String line2 = "";
                while ((line2 = bp.readLine()) != null) {
                    records.add(line2);
                }
                try (BufferedReader bk = new BufferedReader(new FileReader("files/experiments/" + data + "/" + data + "_" + questions + "_" + "CWE" + "_SR_0.4_" + numFeatures + ".csv"))) {

                    String line3 = "";
                    while ((line3 = bk.readLine()) != null) {
                        records.add(line3);
                    }

                    try (BufferedWriter bw = new BufferedWriter(new FileWriter("files/experiments/" + data + "/datasets/" + data + "_" + questions + "_" + "all" + "_SR_0.4_" + numFeatures + ".csv", false))) {
                        for (String word : records) {
                            bw.write(word + "\n");
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void splitDatasetAll(String source, String data, String questions, int i){

        Set<String> tfidf = new HashSet<>();
        Set<String> w2v = new HashSet<>();
        Set<String> avg = new HashSet<>();

        try(BufferedWriter bw = new BufferedWriter(new FileWriter("files/experiments/" + data + "/datasets/" + data + "_" + questions + "_" + source + "_SR_0.4_" + i + "00_tfidf.csv", false))){
            try(BufferedWriter br = new BufferedWriter(new FileWriter("files/experiments/" + data + "/datasets/" + data + "_" + questions + "_" + source + "_SR_0.4_" + i + "00_word2vec.csv", false))){
                try(BufferedWriter bt = new BufferedWriter(new FileWriter("files/experiments/" + data + "/datasets/" + data + "_" + questions + "_" + source + "_SR_0.4_" + i + "00_avg.csv", false))) {
                    try (BufferedReader bp = new BufferedReader(new FileReader("files/experiments/" + data + "/datasets/" + data + "_" + questions + "_" + source + "_SR_0.4_" + i + "00.csv"))) {
                        String line = "";

                        int j = 0;
                        while ((line = bp.readLine()) != null) {
                            if (j++ == 0){
                                bw.write(line + "\n");
                                br.write(line + "\n");
                                bt.write(line + "\n");
                            }
                            line = line.replace("\"", "");
                            line = line.replace("\\", "");
                            String[] cols = line.split(PropertySettings.SEPARATOR);
                            if (cols[4].contains("tfidf")) {
                                tfidf.add(line);
                                //bw.write(line + "\n");
                            } else if (cols[4].contains("word2vec")){
                                w2v.add(line);
                                //br.write(line + "\n");
                            } else if (cols[4].contains("avgscore")){
                                avg.add(line);
                                //bt.write(line + "\n");
                            }
                        }
                        for (String word : tfidf) {
                            bw.write(word + "\n");
                        }
                        for (String word : w2v) {
                            br.write(word + "\n");
                        }
                        for (String word : avg) {
                            bt.write(word + "\n");
                        }
                    }
                    catch(Exception e) {
                        //
                    }
                }catch(Exception e) {
                    //
                }
            }catch(Exception e) {
                //
            }
        }catch(Exception e) {
            //
        }
    }

}
