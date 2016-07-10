import java.util.List;


public class MedianStringFinder {

	private int numberOfDnaSequences;
	private int dnaSequenceLength;
	private int targetMedianStringLength;
	private List<List<Nucleotide>> dnaSequences;
	
	/**
	 * Constructor
	 * @param DnaSequences - list of DNA sequences. Each DNA sequence needs to be of the same length.
	 * @throws Exception 
	 */
	public MedianStringFinder(List<List<Nucleotide>> dnaSequences, int targetMedianStringLength) throws Exception {
		
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
		
		//Median string length must be more than 0
		if (targetMedianStringLength > 0) {
			this.targetMedianStringLength = targetMedianStringLength;
		} else {
			throw new Exception("Target median string length must be more than 0");
		}
		
	}

	public int getNumberOfDnaSequences() {
		return numberOfDnaSequences;
	}

	public int getDnaSequenceLength() {
		return dnaSequenceLength;
	}

	public int getTargetMedianStringLength() {
		return targetMedianStringLength;
	}
	
}
