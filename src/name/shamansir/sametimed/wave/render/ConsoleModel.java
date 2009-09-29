package name.shamansir.sametimed.wave.model;

import java.util.List;

import name.shamansir.sametimed.wave.model.base.ConsoleLines;

public class ConsoleModel extends AModel<List<String>, ConsoleLines> {	

	protected ConsoleModel(List<String> model) {
		super(ModelID.CONSOLE_MODEL, model);
	}
	
	protected ConsoleModel() {
		super(ModelID.CONSOLE_MODEL);
	}	
	
	public List<String> getConsoleLines() {
		return ((ConsoleLines)getValue()).getConsoleLines();
	}

	@Override
	protected ConsoleLines createEmptyValue() {
		return new ConsoleLines();
	}

	@Override
	protected ConsoleLines extractValue(List<String> fromSource) {
		return new ConsoleLines(fromSource);
	}

}
