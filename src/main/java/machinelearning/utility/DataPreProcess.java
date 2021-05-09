package machinelearning.utility; /**
 * 
 */


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author tdoy
 *
 */
public class DataPreProcess {

    /**
     * create arff file from csv input file
     * @param file should be csv format
     * @param newFile new file will be arff format
     */
	public void loadFileToARFF(String file, String newFile) {
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

    /**
     * write header for arff file
     * @param header
     * @param outfile
     */
	private void writeHeader(String header, String outfile) {
		
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(outfile))){
			
			bw.write(header);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

    /**
     * wrtie contents of file to arff
     * @param file
     * @param outfile
     */
	private void readWriteFile(String file, String outfile) {
		
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(outfile, true))){
			
			try(BufferedReader br = new BufferedReader(new FileReader(file))){
				String line = "";
				
				int i=0;
				while((line=br.readLine())!=null) {
				    if(PropertySettings.HEADER) {
                        if (i++ == 0) continue;
                    } // ignore header in file
					line = line.replace("\"", "");
					line = line.replace("\\", "");
					String[] cols = line.split(PropertySettings.SEPARATOR);					// this is separated by semicolon
					bw.write("\""+cols[1]+"\","+"\""+cols[2]+"\","+"\""+cols[3]+"\","+"\""+cols[4]+"\","+cols[0]+"\n");			// cols[4]=bugreport: cols[2]=type-of-bug (sec/no-sec)
				}
				
			}catch(Exception e) {
                e.printStackTrace();
			}
		}catch(Exception e) {
            e.printStackTrace();
		}		
	}

    /**
     * create new csv file with cleaned text
     * @param file csv format
     * @param outfile csv format
     * @return List<double[]> tFIDFVec
     * @throws IOException
     */
	public void cleanFile(String file, String outfile){
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(outfile, true))){

            try(BufferedReader br = new BufferedReader(new FileReader(file))){
                String line = "";

                bw.write("Title" + PropertySettings.SEPARATOR + "Description"+ PropertySettings.SEPARATOR + "Id" + PropertySettings.SEPARATOR + "Date");

                int i=0;
                while((line=br.readLine())!=null) {
                    if(PropertySettings.HEADER) {
                        if (i++ == 0) continue;
                    } // ignore header in file
                    line = line.replace("\"", "");
                    line = line.replace("\\", "");
                    String[] cols = line.split(PropertySettings.SEPARATOR);					// this is separated by semicolon
                    bw.write(cols[0]+PropertySettings.SEPARATOR+new Cleanup().cleanText(cols[1])+PropertySettings.SEPARATOR+new Cleanup().cleanText(cols[2])+PropertySettings.SEPARATOR+cols[3]+PropertySettings.SEPARATOR+cols[4]+"\n");			// cols[4]=bugreport: cols[2]=type-of-bug (sec/no-sec)
                }

            }catch(Exception e) {
                e.printStackTrace();
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * split dataset collected from StackExchangeAPI by method
     * @param file
     */
    public void splitDataset(String file){
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(file.substring(0,file.length()-4) + "_tfidf.csv", false))){
            try(BufferedWriter br = new BufferedWriter(new FileWriter(file.substring(0,file.length()-4) +  "_word2vec.csv", false))){
                try(BufferedWriter bt = new BufferedWriter(new FileWriter(file.substring(0,file.length()-4) +  "_avgscore.csv", false))) {
                    try (BufferedReader bp = new BufferedReader(new FileReader(file))) {
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
                        e.printStackTrace();
                    }
                }catch(Exception e) {
                    e.printStackTrace();
                }
            }catch(Exception e) {
                e.printStackTrace();
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * clean all files in directory containing name
     * @param directory
     * @param name
     */
    public void fixWord2VecFiles(String directory, String name){
        try (Stream<Path> walk = Files.walk(Paths.get(directory))) {

            List<String> result = walk.filter(Files::isRegularFile)
                    .map(x -> x.toString()).collect(Collectors.toList());

            result.forEach(System.out::println);

            for (int i = 0; i < result.size(); i++) {

                if ((result.get(i).contains(name))) {

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
