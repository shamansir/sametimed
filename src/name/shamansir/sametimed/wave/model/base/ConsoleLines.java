package name.shamansir.sametimed.wave.model.base;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class ConsoleLines implements IModelValue {
		
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
	public String asJSON() {
		String jsonString = "[";
		for (Iterator<String> iter = consoleLines.iterator(); iter.hasNext(); ) {
			jsonString += "'" + iter.next() + "'";
			if (iter.hasNext()) jsonString += ",";
		}
		return jsonString + "]";
	}	
	
	
}
