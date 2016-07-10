import java.util.ArrayList;
import java.util.List;


public class MedianStringFinder {

	public static final String EMPTY_STRING = "";
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
		
		int proposedMedianStringLength = proposedMedianString.length(), maximumMedianStringStartPosition = this.dnaSequenceLength - proposedMedianStringLength;
		List<Integer> minimumHammingDistances = new ArrayList<Integer>(this.numberOfDnaSequences);
		
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
			minimumHammingDistances.add(Integer.valueOf(minimumHamingDistance));
		}
		
		//Return the sum of all the minimum hamming distances
		int totalMinimumHammingDistance = 0;
		for (Integer minimumHammingDistance : minimumHammingDistances) {
			totalMinimumHammingDistance += minimumHammingDistance.intValue();
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
	
}
