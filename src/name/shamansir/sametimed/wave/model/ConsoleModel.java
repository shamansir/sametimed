package name.shamansir.sametimed.wave.model;

import java.util.List;

import name.shamansir.sametimed.wave.model.base.ConsoleLines;

/**
 * @author shamansir <shaman.sir@gmail.com>
 * 
 * @see ConsoleLines
 * 
 */
public class ConsoleModel extends AModel<List<String>, ConsoleLines> {
	
	protected ConsoleModel() {
		super(ModelID.CONSOLE_MODEL);
	}
	
	protected ConsoleModel(List<String> source) {
		super(ModelID.CONSOLE_MODEL, source);
	}	

	@Override
	public ConsoleLines createEmptyValue() {
		return new ConsoleLines();
	}

	@Override
	public ConsoleLines extractValue(List<String> source) {
		return new ConsoleLines(source);
	}	

}
