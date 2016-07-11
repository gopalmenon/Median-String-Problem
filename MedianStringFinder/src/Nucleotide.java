import java.util.ArrayList;
import java.util.List;


public class Nucleotide {

	public static final char[] validBases = {'A', 'C', 'G', 'T'};
	
	private char base;
	
	/**
	 * @param base
	 * @throws Exception
	 * Create a nucleotide only if the base is valid
	 */
	public Nucleotide(char base) throws Exception {

		
		boolean isValidBase = false;
		char baseUpperCase = Character.toUpperCase(base);
		for (char validBase : validBases) {
			if (baseUpperCase == validBase) {
				isValidBase = true;
				this.base = baseUpperCase;
				break;
			}
		}
		
		if (!isValidBase) {
			throw new Exception(base + " is not a valid base.");
		}
		
	}
	
	/**
	 * @return the value of the base
	 */
	public char getBase() {
		return this.base;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 * Return a string representation of the base
	 */
	@Override
	public String toString() {
		StringBuffer base = new StringBuffer();
		base.append(this.base);
		return base.toString();
	}
	
	/**
	 * @param nucleotideString
	 * @return a nucleotide list
	 * @throws Exception 
	 */
	public static List<Nucleotide> getDnaSequence(String nucleotideString) throws Exception {
		
		int nucleotideStringLength = nucleotideString.length();
		List<Nucleotide> dnaSequence = new ArrayList<Nucleotide>(nucleotideStringLength);
		for (int nucleotideCounter = 0; nucleotideCounter < nucleotideStringLength; ++nucleotideCounter) {
			dnaSequence.add(new Nucleotide(nucleotideString.charAt(nucleotideCounter)));
		}
		return dnaSequence;
		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object otherNucleotide) {
		
		if (!(otherNucleotide instanceof Nucleotide)) {
			return false;
		}
		
		if (((Nucleotide) otherNucleotide).getBase() == this.base) {
			return true;
		} else {
			return false;
		}
	}
	
}
