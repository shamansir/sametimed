package name.shamansir.sametimed.wave.doc;

/**
 * 
 * @author shaman.sir <shaman.sir@gmail.com>
 * 
 * Tags IDs are generated basing on the level of the tag. 
 * 
 * As an example, here's the sample document structure with 
 * correct tags id-s: 
 * 
 * {@code
 * <tag id="a" />
 * <tag id="b" />
 * <tag id="c" />
 * <tag id="d" >
 * 		<tag id="d.a">
 * 			<tag id="d.a.a" />
 * 			<tag id="d.a.b" />
 * 			<tag id="d.a.c">
 * 				<tag id="d.a.c.a" />
 * 			</tag>
 * 			<tag id="d.a.d" /> 
 * 		</tag>
 * 		<tag id="d.b" />
 * 		<tag id="d.c">
 * 			<tag id="d.c.a" />
 * 		</tag>
 * </tag>
 * <tag id="e" />
 * <tag id="f" />
 * ...
 * <tag id="z" />
 * <tag id="A" />
 * ...
 * <tag id="Z" />
 * <tag id="aa" />
 * ...
 * <tag id="aZ" />
 * <tag id="ba" />
 * ...
 * <tag id="ZZ" />
 * <tag id="aaa" /> 
 * ... 
 * }
 *
 */

public class TagID { // FIXME: Create a factory?
	
	/* public class TagIDValidationException extends Exception {
		
	} */
	
	/* public class IncorrectTagIDValueException extends Exception {
	
	} */	
	
	private String TAG_DOMAIN_DELIMITER = ".";
	
	/** see {@link #nextPos(char)} method also if you want to change it */ 
	private static final char[] ALPHABET =
	      "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
	      .toCharArray();
	private static final int ALPHABET_LEN = ALPHABET.length;
	
	private final String value;
	
	public TagID(char value) /* throws TagIDValidationException */ {
		this.value = String.valueOf(value); // validate(String.valueOf(value));
	}	
	
	public TagID(String value) /* throws TagIDValidationException */ {
		this.value = value; // validate(value);
	}
	
	public String getValue() {
		return value;
	}
	
	public static TagID valueOf(String value) {
		return new TagID(value); // TagID(validate(value));
	}
	
	public TagID makeNext() {
		if (this.value != null) {
			int lastLevelPos = this.value.lastIndexOf(TAG_DOMAIN_DELIMITER);
			if (lastLevelPos < 0) { 
				return new TagID(nextValueFor(this.value));
			} else {
				String unchangedPart = this.value.substring(0, lastLevelPos);
				String lastLevelVal = this.value.substring(lastLevelPos + 1);
				String newLevelVal = nextValueFor(lastLevelVal);
				return new TagID(unchangedPart + newLevelVal);
			}
		} else return TagID.valueOf(String.valueOf(ALPHABET[0]));
	}
	
	private String nextValueFor(final String value) /* throws IncorrectTagIDValueException */ {
		char[] next = value.toCharArray();
		int pos = next.length - 1;
		do {
			if (pos >= 0) {				
				char charAt = value.charAt(pos);
				if ((charAt >= ALPHABET[0]) && (charAt < ALPHABET[ALPHABET_LEN])) {
					// FIXME: ensure it is a letter (now: c >= 'a' && c < 'Z')
					next[pos] = ALPHABET[nextPos(charAt)];
					return next.toString();
				} else if (charAt == ALPHABET[ALPHABET_LEN]) {
					next[pos] = ALPHABET[0];
				} /* else throw new IncorrectTagIDValueException() */;
			} else {
				return new String(ALPHABET[0] + next.toString());
			}
			pos--;
		} while (pos >= -1);
		return null;
	}
	
	private static int nextPos(char charAt) {
		int code = (int)charAt;
		if (code < (int)'z') {
			return (code - (int)'a') + 1;  
		} else if (code == 'z') {
			return 26; 
		} else if (code < 'Z') {
			return (code - (int)'A') + 27;
		}
		return 0;
	}

	public TagID makeForFirstChild() {
		return new TagID(this.value + TAG_DOMAIN_DELIMITER + ALPHABET[0]);
	}
	
	public static TagID makeFirstTagID() {
		return new TagID(ALPHABET[0]); 
	}
	
	/*
	protected static String validate(String value) throws TagIDValidationException {
		// NULL allowed
		// TODO: make check if matches ^[a-zA-Z](\.[a-zA-Z])*$, 
		// but may be not required, if it is always correct
		return value;
	} */
	
	// FIXME: implement compareTo and equalsTo
		
}
