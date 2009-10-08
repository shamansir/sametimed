package name.shamansir.sametimed.wave.model.base;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * @author shamansir <shaman.sir@gmail.com>
 * 
 * Value, that can be converted to JSON
 * 
 */

public class JSONiableValue {
	
	/**
	 * Escape string to pass it in JavaScript
	 * 
	 * @param sourceString source string
	 * @return string with escaped special symbols and quotes
	 */
	public static String escapeJSONString(String sourceString) {
		return StringEscapeUtils.escapeJavaScript(sourceString).replace("\\\"", "\\\\\"");
	}

}
