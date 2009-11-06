package name.shamansir.sametimed.wave.model.base;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author shamansir <shaman.sir@gmail.com>
 * 
 * @see IModelValue
 * @see JSONiableValue
 */

public class ErrorsLines extends JSONiableValue implements IModelValue {

	private List<String> errors;
		
	public ErrorsLines(List<String> errors) {
		this.errors = errors; 
	}
		
	public ErrorsLines() {
		this.errors = new ArrayList<String>(); 
	}		
		
	public List<String> getErrors() {
		return errors;
	}
	
	@Override
	public String asJSON(boolean useEscapedQuotes) {
		String jsonString = "[";
		String quot = useEscapedQuotes ? "\\\"" : "\"";
		for (Iterator<String> iter = errors.iterator(); iter.hasNext(); ) {
			jsonString += quot + escapeJSONString(iter.next()) + quot;
			if (iter.hasNext()) jsonString += ",";
		}
		return jsonString + "]";
	}	
	
	@Override	
	public String asJSON() {
		return asJSON(false);
	}	
	
}
