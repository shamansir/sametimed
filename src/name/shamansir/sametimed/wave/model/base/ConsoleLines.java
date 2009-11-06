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

public class ConsoleLines extends JSONiableValue implements IModelValue {
		
	private List<String> consoleLines;
		
	public ConsoleLines(List<String> consoleLines) {
		this.consoleLines = consoleLines; 
	}
		
	public ConsoleLines() {
		this.consoleLines = new ArrayList<String>(); 
	}		
		
	public List<String> getConsoleLines() {
		return consoleLines;
	}	

	@Override
	public String asJSON(boolean useEscapedQuotes) {
		String jsonString = "[";
		String quot = useEscapedQuotes ? "\\\"" : "\"";		
		for (Iterator<String> iter = consoleLines.iterator(); iter.hasNext(); ) {
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
