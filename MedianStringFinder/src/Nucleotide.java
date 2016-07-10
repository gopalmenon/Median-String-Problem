
public class Nucleotide {

	public static final String[] validBases = {"A", "C", "G", "T"};
	
	private String base;
	
	/**
	 * @param base
	 * @throws Exception
	 * Create a nucleotide only if the base is valid
	 */
	public Nucleotide(String base) throws Exception {
		
		boolean isValidBase = false;
		for (String validBase : validBases) {
			if (validBase.equals(base)) {
				isValidBase = true;
				this.base = base;
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
	public String getBase() {
		return this.base;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 * Return a string representation of the base
	 */
	@Override
	public String toString() {
		return this.base;
	}
	
}
