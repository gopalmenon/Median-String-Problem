import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MedianStringFinderClient {

	public static final String TEXT_FILE_EXTENSION = ".txt";
	public static final String ERROR_MESSAGE_FOR_MEDIAN_STRING_NOT_FOUND = "Median string could not be found!!!";
	public static final String HUMAN_DNA_SEQUENCES_FILE = "HumanSequence.txt";
	public static final String HUMAN_DNA_SEQUENCES_REGEX = ">hg38[A-Za-z_0-9\\.]*";
	public static final String USE_DNA_SEQUENCE_FROM_TEXT_FILE = "T";
	public static final String USE_HUMAN_DNA_SEQUENCE = "H";
	public static final String USE_GENERATED_DNA_SEQUENCE = "G";
	public static final int DEFAULT_TARGET_MEDIAN_STRING_LENGTH = 12;
	
	public static void main(String[] args) {
		
		MedianStringFinderClient medianStringFinderClient = new MedianStringFinderClient();
		
		try{
		if (args.length == 0) {
			System.out.println(medianStringFinderClient.findMedianString(medianStringFinderClient.getDnaSequences(), DEFAULT_TARGET_MEDIAN_STRING_LENGTH));
		} else if (USE_DNA_SEQUENCE_FROM_TEXT_FILE.equals(args[0].trim())) {
			System.out.println(medianStringFinderClient.findMedianString(medianStringFinderClient.getDnaSequences(), DEFAULT_TARGET_MEDIAN_STRING_LENGTH));
		} else if (USE_HUMAN_DNA_SEQUENCE.equals(args[0].trim())) {
			System.out.println(medianStringFinderClient.findMedianString(medianStringFinderClient.getHumanDnaSequences(), DEFAULT_TARGET_MEDIAN_STRING_LENGTH));
		} else if (USE_GENERATED_DNA_SEQUENCE.equals(args[0].trim())) {
			System.out.println(medianStringFinderClient.findMedianString(medianStringFinderClient.getGeneratedDnaSequences(args), Integer.parseInt(args[3])));
		} else {
			System.out.println(medianStringFinderClient.findMedianString(medianStringFinderClient.getGeneratedDnaSequences(args), Integer.parseInt(args[3])));
		}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			System.exit(0);
		} 
		
		
	}
	
	/**
	 * @param dnaSequences
	 * @return a median string 
	 */
	private String findMedianString(List<List<Nucleotide>> dnaSequences, int medianStringLength) {
				
		try {
			MedianStringFinder medianStringFinder = new MedianStringFinder(dnaSequences);
			return medianStringFinder.findMedianString(medianStringLength);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		return ERROR_MESSAGE_FOR_MEDIAN_STRING_NOT_FOUND;
	}
	
	/**
	 * @return list of DNA sequences from a text file
	 */
	private List<List<Nucleotide>> getDnaSequences() {
		
		List<List<Nucleotide>> dnaSequences = new ArrayList<List<Nucleotide>>();
		String[] dnaSequenceFiles = getFolderContents(".");
		
		for (String dnaSequenceFile: dnaSequenceFiles) {
			
			try {
				if (dnaSequenceFile.endsWith(TEXT_FILE_EXTENSION)) {
					dnaSequences.add(Nucleotide.getDnaSequence(getDnaStringFromFile(dnaSequenceFile)));
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			}
			
		}
		
		return dnaSequences;
		
	}
	
	/**
	 * @return human DNA sequences from a text file
	 */
	private List<List<Nucleotide>> getHumanDnaSequences() {
		
		
		List<List<Nucleotide>> dnaSequences = new ArrayList<List<Nucleotide>>();
		try {
			
			//Get file contents and make a single string
			List<String> humanDnaSequences = getFileContents(HUMAN_DNA_SEQUENCES_FILE);
			StringBuffer humanDnaSequencesStringBuffer = new StringBuffer(); 
			for (String humanDnaSequenceString : humanDnaSequences) {
				humanDnaSequencesStringBuffer.append(humanDnaSequenceString);
			}
			
			//Split the string into multiple sequences
			String humanDnaSequencesString = humanDnaSequencesStringBuffer.toString();
			String[] humanDnaSequence = humanDnaSequencesString.split(HUMAN_DNA_SEQUENCES_REGEX);
			
			//Create the list of list of nucleotides
			for (String nucleotideString : humanDnaSequence) {
				if (nucleotideString.trim().length() != 0) {
					dnaSequences.add(Nucleotide.getDnaSequence(nucleotideString.trim()));
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		return dnaSequences;
		
	}
	
	
	/**
	 * @return a list of generated DNA sequences
	 */
	private List<List<Nucleotide>> getGeneratedDnaSequences(String[] args) {
		
		//Retrieve parameters for determining DNA sequence size
		try {
			int dnaSequenceLength = Integer.parseInt(args[1]);
			int numberOfDnaSequences = Integer.parseInt(args[2]);
			int medianStringLength = Integer.parseInt(args[3]);
			int numberOfMedianStringMutations = Integer.parseInt(args[4]);
			Random randomNumberGenerator = new Random(System.currentTimeMillis());
			return MedianStringFinder.generateDnaSequences(dnaSequenceLength, numberOfDnaSequences, medianStringLength, numberOfMedianStringMutations, randomNumberGenerator);
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		return null;
		
	}
	
	/**
	 * @param fileName
	 * @return a string containing the contents of the file
	 */
	private String getDnaStringFromFile(String fileName) {
		
		StringBuffer dnaString = new StringBuffer();
		
		try {
			List<String> fileContents = getFileContents(fileName);
			for (String dnaSequence : fileContents) {
				dnaString.append(dnaSequence);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		return dnaString.toString();
	}

	/**
	 * @param fileName
	 * @return true if the text file exists
	 */
	public boolean fileExists(String fileName) {
		
		File file = new File(fileName);
		if(file.exists() && !file.isDirectory() && fileName.endsWith(TEXT_FILE_EXTENSION)) { 
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * @param folderName
	 * @return true if the folder exists
	 */
	public boolean folderExists(String folderName) {
		
		File folder = new File(folderName);
		if(folder.exists() && folder.isDirectory()) { 
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * @param folderName
	 * @return names of files in a folder
	 */
	public String[] getFolderContents(String folderName) {
		
		File folder = new File(folderName);
		if (folderExists(folderName)) { 
			return folder.list();
		} else {
			return null;
		}
		
	}
	
	/**
	 * @param fileName
	 * @return contents of text file
	 * @throws IOException
	 */
	private List<String> getFileContents(String fileName) throws IOException {
		
		List<String> returnValue = new ArrayList<String>();
		if (fileExists(fileName)) {
			FileReader inputFile = new FileReader(fileName);
			BufferedReader bufferReader = new BufferedReader(inputFile);
			String line = null;
			while ((line = bufferReader.readLine()) != null)   {
				returnValue.add(line);
	        }
	        bufferReader.close();
		}
		return returnValue;
		
	}
}
