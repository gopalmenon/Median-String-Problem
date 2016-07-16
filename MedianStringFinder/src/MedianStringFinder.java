import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;


public class MedianStringFinder {

	public static final String EMPTY_STRING = "";
	public static final String MEDIAN_STRING_KEY = "MEDIAN_STRING_KEY";
	public static final String DNA_SEQUENCE_KEY = "DNA_SEQUENCE_KEY";
	public static int BRANCH_AND_BOUND_TEST_START_LENGTH = 4;
	
	private int numberOfDnaSequences;
	private int dnaSequenceLength;
	private List<List<Nucleotide>> dnaSequences;
	private String bestMedianString;
	private int globalBestScore;
	
	/**
	 * Constructor
	 * @param DnaSequences - list of DNA sequences. Each DNA sequence needs to be of the same length.
	 * @throws Exception 
	 */
	public MedianStringFinder(List<List<Nucleotide>> dnaSequences) throws Exception {
		
		//The DNA sequence need to exist
		if (dnaSequences == null) {
			throw new Exception("DNA Sequences were not passed in.");
		}
		
		this.numberOfDnaSequences = dnaSequences.size();
		int testDnaSequenceLength = 0;
		boolean dnaSequenceFound = false;
		
		//Look at all the DNA sequences to make sure they all exist and are of the same length
		for (List<Nucleotide> dnaSequence : dnaSequences) {
			
			//Each DNA sequence needs to be not null
			if (dnaSequence == null) {
				throw new Exception("One DNA Sequence in the list of sequences does not exist.");
			}
			
			if (!dnaSequenceFound) {
				dnaSequenceFound = true;
				testDnaSequenceLength = dnaSequence.size();
				continue;
			}
			
			if (testDnaSequenceLength != dnaSequence.size()) {
				throw new Exception("All DNA Sequences need to be of the same size");
			}
			
		}
		
		this.dnaSequenceLength = testDnaSequenceLength;
		this.dnaSequences = dnaSequences;
		
	}

	public int getNumberOfDnaSequences() {
		return numberOfDnaSequences;
	}

	public int getDnaSequenceLength() {
		return dnaSequenceLength;
	}

	
	/**
	 * @param targetLength
	 * @return a median string of the required target length
	 */
	public String findMedianString(int targetLength) {
		
		this.globalBestScore = Integer.MAX_VALUE;
		bestMedianString = EMPTY_STRING;
		findMedianStringAtDepth(targetLength, EMPTY_STRING);
		return this.bestMedianString;
		
	}
	
	/**
	 * @param numberOfCharsInRemainingMedianString
	 * @param medianString
	 * recursively find a median string of the required length
	 */
	private void findMedianStringAtDepth(int numberOfCharsInRemainingMedianString, String medianString) {
		
		int currentScore = 0;
		if (numberOfCharsInRemainingMedianString == 0) {
			currentScore = getTotalMinimumHammingDistance(medianString);
			if (currentScore < this.globalBestScore) {
				this.globalBestScore = currentScore;
				this.bestMedianString = medianString;
			}
			return;
		}
		
		//Check for the bound condition
		if (medianString.length() >= BRANCH_AND_BOUND_TEST_START_LENGTH && getTotalMinimumHammingDistance(medianString) > this.globalBestScore) {
			return;
		}
		
		//Append all possible base combinations to current median string and add nodes to the search tree
		for (char base : Nucleotide.validBases) {
			findMedianStringAtDepth(numberOfCharsInRemainingMedianString - 1, (new StringBuffer().append(medianString).append(base)).toString());
		}
		
	}
	
	/**
	 * @param proposedMedianString
	 * @return the minimum possible total hamming distance between the proposed median string and the DNA
	 * sequences by (1) finding the minimum hamming distance for each DNA sequence corresponding to all 
	 * possible starting points (2) finding the sum of all the minimum values found in the step above.
	 */
	private int getTotalMinimumHammingDistance(String proposedMedianString) {
		
		int proposedMedianStringLength = proposedMedianString.length(), maximumMedianStringStartPosition = this.dnaSequenceLength - proposedMedianStringLength, totalMinimumHammingDistance = 0;
		
		//Find the minimum hamming distance between the proposed median string and each DNA sequence
		for (int dnaSequenceCounter = 0; dnaSequenceCounter < this.numberOfDnaSequences; ++dnaSequenceCounter) {
			int minimumHamingDistance = Integer.MAX_VALUE, currentMinimumHamingDistance = 0;
			for (int medianStringStartPosition = 0; medianStringStartPosition < maximumMedianStringStartPosition; ++medianStringStartPosition) {
				
				List<Nucleotide> dnaSubSequence = this.dnaSequences.get(dnaSequenceCounter).subList(medianStringStartPosition, medianStringStartPosition + proposedMedianStringLength);
				try {
					currentMinimumHamingDistance = getHammingDistance(Nucleotide.getDnaSequence(proposedMedianString), dnaSubSequence);
					if (currentMinimumHamingDistance < minimumHamingDistance) {
						minimumHamingDistance = currentMinimumHamingDistance;
					}
				} catch (Exception e) {
					System.err.println(proposedMedianString + " is not a valid nucleotide sequence.");
					System.exit(0);
				}
			
			
			}
			totalMinimumHammingDistance += minimumHamingDistance;
		}
		
		return totalMinimumHammingDistance;
	}
	
	/**
	 * @param nucleotideSequence1
	 * @param nucleotideSequence2
	 * @return hamming distance between the two nucleotide sequences
	 */
	private int getHammingDistance(List<Nucleotide> nucleotideSequence1, List<Nucleotide> nucleotideSequence2) {
		
		int hammingDistance = 0;
		int testLength = nucleotideSequence1.size();
		for (int offsetCounter = 0; offsetCounter < testLength; ++offsetCounter) {
			if (!nucleotideSequence1.get(offsetCounter).equals(nucleotideSequence2.get(offsetCounter))) {
				++hammingDistance;
			}
		}
		
		return hammingDistance;
		
	}
	
	/**
	 * @param dnaSequenceLength
	 * @param numberOfDnaSequences
	 * @param medianStringLength
	 * @param numberOfMedianStringMutations
	 * @param randomNumberGenerator
	 * @return DNA sequences with embedded mutated median strings and original median string
	 */
	public static Map<String, Object> generateDnaSequences(int dnaSequenceLength, int numberOfDnaSequences, int medianStringLength, int numberOfMedianStringMutations, Random randomNumberGenerator) {
		
		Map<String, Object> returnValue = new HashMap<String, Object>();
		
		List<List<Nucleotide>> dnaSequences = new ArrayList<List<Nucleotide>>();
		int numberOfNucleotideTypes = Nucleotide.validBases.length;
			
		//Generate median string
		StringBuffer medianString = new StringBuffer();
		for (int lengthCounter = 0; lengthCounter < medianStringLength; ++lengthCounter) {
			medianString.append(Nucleotide.validBases[randomNumberGenerator.nextInt(numberOfNucleotideTypes)]);
		}

		//Generate DNA sequences
		for (int sequenceCounter = 0; sequenceCounter < numberOfDnaSequences; ++sequenceCounter) {
			
			//Generate nucleotides inside DNA sequence
			StringBuffer nucleotideString = new StringBuffer();
			for (int lengthCounter = 0; lengthCounter < dnaSequenceLength; ++lengthCounter) {
				nucleotideString.append(Nucleotide.validBases[randomNumberGenerator.nextInt(numberOfNucleotideTypes)]);
			}
			
			//Mutate median string
			StringBuffer mutatedMedianString = getMutatedMedianString(new StringBuffer(medianString.toString()), numberOfMedianStringMutations, medianStringLength, randomNumberGenerator);
			
			//Insert median string into the DNA sequence at a random position
			nucleotideString.insert(randomNumberGenerator.nextInt(dnaSequenceLength), mutatedMedianString.toString());
			
			//Add a DNA sequence with mutated median string to the list of sequences
			try {
				dnaSequences.add(Nucleotide.getDnaSequence(nucleotideString.toString()));
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			}
		}

		returnValue.put(MEDIAN_STRING_KEY, medianString.toString());
		returnValue.put(DNA_SEQUENCE_KEY, dnaSequences);
		
		return returnValue;
		
	}
	
	/**
	 * @param medianStringCopy
	 * @param numberOfMedianStringMutations
	 * @param medianStringLength
	 * @param randomNumberGenerator
	 * @return a mutated median string
	 */
	private static StringBuffer getMutatedMedianString(StringBuffer medianStringCopy, int numberOfMedianStringMutations, int medianStringLength, Random randomNumberGenerator) {
		
		Set<Integer> currentMutatedLocations = new HashSet<Integer>();
		int medianStringMutationCounter = 0, proposedMutationLocation = 0;
		
		while (medianStringMutationCounter < numberOfMedianStringMutations) {
			
			proposedMutationLocation = randomNumberGenerator.nextInt(medianStringLength);
			if (!currentMutatedLocations.contains(Integer.valueOf(proposedMutationLocation))) {
				currentMutatedLocations.add(Integer.valueOf(proposedMutationLocation));
				++medianStringMutationCounter;
				medianStringCopy.setCharAt(proposedMutationLocation, getMutatedCharacter(medianStringCopy.charAt(proposedMutationLocation), randomNumberGenerator));
			}
			
		}
		
		return medianStringCopy;
		
	}
	
	/**
	 * @param currentCharacter
	 * @param randomNumberGenerator
	 * @return a mutated character different from the current character
	 */
	private static char getMutatedCharacter(char currentCharacter, Random randomNumberGenerator) {
		
		int numberOfNucleotideTypes = Nucleotide.validBases.length;
		char proposedCharacter = Nucleotide.validBases[randomNumberGenerator.nextInt(numberOfNucleotideTypes)];
		while (proposedCharacter == currentCharacter) {
			proposedCharacter = Nucleotide.validBases[randomNumberGenerator.nextInt(numberOfNucleotideTypes)];
		}
		
		return proposedCharacter;
		
	}
	
}
