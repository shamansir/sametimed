package name.shamansir.sametimed.wave.model;

import name.shamansir.sametimed.wave.model.base.InfoLine;

public class InfoLineModel extends AModel<String, InfoLine> {	

	public InfoLineModel(String model) {
		super(ModelID.INFOLINE_MODEL, model);
	}
	
	public InfoLineModel() {
		super(ModelID.INFOLINE_MODEL);
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
