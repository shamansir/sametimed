package name.shamansir.sametimed.wave.model.base;

import java.util.ArrayList;
import java.util.List;

public class ErrorsLines implements IModelValue {

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
	public String asJSON() {
		// FIXME: implement
		return null;
	}	
	
	
}
