package name.shamansir.sametimed.wave.render;

import java.util.ArrayList;
import java.util.List;

public class ConsoleModel extends APanelModel {
	
	protected class ConsoleModelValue implements IModelValue {
		
		private List<String> consoleLines;
		
		private ConsoleModelValue(List<String> consoleLines) {
			this.consoleLines = consoleLines; 
		}
		
		private ConsoleModelValue() {
			this.consoleLines = new ArrayList<String>(); 
		}		
		
		public List<String> getConsoleLines() {
			return consoleLines;
		}
		
	}	

	protected ConsoleModel(List<String> model) {
		super(model);
	}
	
	public List<String> getConsoleLines() {
		return ((ConsoleModelValue)getValue()).getConsoleLines();
	}		

	@Override
	protected IModelValue extractModel(List<String> fromList) {
		return new ConsoleModelValue(fromList);
	}

	@Override
	protected IModelValue createEmptyValue() {
		return new ConsoleModelValue();
	}	

}
