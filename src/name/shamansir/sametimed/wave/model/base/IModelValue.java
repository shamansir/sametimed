package name.shamansir.sametimed.wave.model.base;

/**
 * @author shamansir <shaman.sir@gmail.com>
 * 
 * A model value, that is used by models to hold their inner value
 * Must be able to be converted to JSON format 
 *
 * @see #asJSON()
 *
 */
public interface IModelValue {
	
	
	/**
	 * Get value in JSON format
	 * 
	 * must use only double quotes in resulting string
	 * (JSON fails to parse single quotes)
	 * <code>String quot = "\\\"";</code> may be used for this issue if the resulting
	 * 		string will be also wrapped in double quotes (useEscapedQuotes == true)
	 * <code>String quot = "\"";</code> may be used for this issue if the resulting
	 * 		string will be also wrapped in single quotes (useEscapedQuotes == false)
	 * the best way:
	 * <code>String quot = useEscapedQuotes ? "\\\"" : "\"";</code>
	 * 
	 * @param useEscapedQuotes use escaped quotes when converting to JSON 
	 * 				(depends of what type of quotes (single or double)
	 * 				 the value will be wrapped), must be false or true,
	 * 				 corresponding)  
	 * @return value, converted to JSON format
	 */		
	public String asJSON(boolean useEscapedQuotes);
	
	/**
	 * Get value in JSON format (pass the default quotes escaping parameter to asJSON)
	 * 
	 * @see #asJSON(boolean)
	 * 
	 * @return value, converted to JSON format
	 */	
	public String asJSON();

}
