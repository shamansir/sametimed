package name.shamansir.sametimed.wave.model.base;

public interface IModelValue {
	
	// must use only double quotes in resulting string 
	// (JSON fails to parse single quotes)
	// String quot = "\\\""; // may be used for this issue if the resulting 
	//					     // string will be also wrapped in double quotes 
	//						 // (useEscapedQuotes == true)
	// String quot = "\""; // may be used for this issue if the resulting 
	//					   // string will be wrapped in single quotes 
	//					   // (useEscapedQuotes == false)
	// the best way:
	// String quot = useEscapedQuotes ? "\\\"" : "\"";
	public String asJSON(boolean useEscapedQuotes);	
	public String asJSON();

}
