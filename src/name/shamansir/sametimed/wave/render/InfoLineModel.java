package name.shamansir.sametimed.wave.render;

import name.shamansir.sametimed.wave.model.InfoLine;
import name.shamansir.sametimed.wave.render.proto.APanelModel;

public class InfoLineModel extends APanelModel<String, InfoLine> {	

	public InfoLineModel(String model) {
		super(model);
	}
	
	public String getInfoLine() {
		return getValue().getLine();
	}

	@Override
	protected InfoLine createEmptyValue() {
		return new InfoLine();
	}

	@Override
	protected InfoLine extractValue(String fromSource) {
		return new InfoLine(fromSource);
	}

	
	
}
