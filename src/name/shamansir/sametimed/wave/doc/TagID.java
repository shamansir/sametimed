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

public final class TagID {
	
	/* public class TagIDValidationException extends Exception {
		
	} */
	
	/* public class IncorrectTagIDValueException extends Exception {
	
	} */	
	
	private String TAG_DOMAIN_DELIMITER = ".";
	
	/** see {@link #nextPos(char)} and {@link #nextValueFor(String)} methods also if you want to change it */ 
	private static final char[] ALPHABET =
	      "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
	      .toCharArray();
	
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
		if (value != null) {
			int lastDot = value.lastIndexOf(TAG_DOMAIN_DELIMITER);
			if (lastDot < 0) { 
				return new TagID(nextValueFor(value));
			} else {
				String unchangedPart = value.substring(0, lastDot + 1);
				String lastLevelVal = value.substring(lastDot + 1);
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
				if (((charAt >= 'a') && (charAt <= 'z')) ||
					((charAt >= 'A') && (charAt < 'Z')))
					{
					next[pos] = ALPHABET[nextPos(charAt)];
					return String.valueOf(next);
				} else if (charAt == 'Z') {
					next[pos] = 'a';
				} /* else throw new IncorrectTagIDValueException() */;
			} else {
				return new String('a' + String.valueOf(next));
			}
			pos--;
		} while (pos >= -1);
		return null;
	}
	
	private static int nextPos(char charAt) { // TODO: use charVal(charAt) + 1, but not for 'Z'
		int code = (int)charAt;
		if ((charAt >= 'a') && (charAt < 'z')) {
			return (code - (int)'a') + 1;
		} else if (charAt == 'z') {
			return 26; 
		} else if ((charAt >= 'A') && (charAt < 'Z')) {
			return (code - (int)'A') + 27;
		}
		return 0;
	}
	
	private static int charVal(char theChar) {
		int code = (int)theChar;
		if ((theChar >= 'a') && (theChar <= 'z')) {
			return (code - (int)'a');
		} else if ((theChar >= 'A') && (theChar <= 'Z')) {
			return (code - (int)'A') + 26;
		}
		return 0;
	}	

	public TagID makeForFirstChild() {
		return new TagID(this.value + TAG_DOMAIN_DELIMITER + ALPHABET[0]);
	}
	
	public static TagID makeFirstTagID() {
		return new TagID(ALPHABET[0]); 
	}
	
	/** always returned for last domain */
	public int intValue() {
		if (value == null) return -1;
		int lastDot = value.lastIndexOf(TAG_DOMAIN_DELIMITER);
		char[] chars = (lastDot < 0) ? value.toCharArray() : value.substring(lastDot + 1).toCharArray();
		int len = chars.length;
		int highVal = 52;
		int result = 0;
		for (int i = 0; i < (len - 1); i++) {
			result += (charVal(chars[i]) + 1) * Math.pow(highVal, len - i - 1); 
		}
		return result + charVal(chars[len - 1]); 
	}
	
	// TODO: implement methods allowing to get unique integer from any id
	
	/*
	protected static String validate(String value) throws TagIDValidationException {
		// NULL allowed
		// TODO: make check if matches ^[a-zA-Z](\.[a-zA-Z])*$, 
		// but may be not required, if it is always correct
		return value;
	} */
	
	// FIXME: implement intValue (fullIntValue?) and test it (use it when comparing?)
	
	@Override
    public boolean equals(Object other) {
	    if (this == other) return true;
	    if (!(other instanceof TagID)) return false;
	    TagID otherTagID = (TagID) other;
	    return otherTagID.value.equals(value);
	}
	
	@Override
    public int hashCode() {
	    return value.hashCode();
	}
	
}
