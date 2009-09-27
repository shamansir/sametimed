package name.shamansir.sametimed.wave.render;

import java.util.List;

import name.shamansir.sametimed.wave.model.ConsoleLines;
import name.shamansir.sametimed.wave.render.proto.APanelModel;

public class ConsolePanelModel extends APanelModel<List<String>, ConsoleLines> {	

	protected ConsolePanelModel(List<String> model) {
		super(model);
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
