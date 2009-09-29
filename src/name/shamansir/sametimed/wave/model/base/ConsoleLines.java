package name.shamansir.sametimed.wave.model.base;

import java.util.ArrayList;
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
		// FIXME: implement
		return null;
	}	
	
	
}
