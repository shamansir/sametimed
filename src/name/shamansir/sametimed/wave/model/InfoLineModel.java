package name.shamansir.sametimed.wave.model;

import name.shamansir.sametimed.wave.model.base.InfoLine;

/**
 * @author shamansir <shaman.sir@gmail.com>
 *
 * @see InfoLine
 */
public class InfoLineModel extends AModel<String, InfoLine> {

	protected InfoLineModel() {
		super(ModelID.INFOLINE_MODEL);
	}
	
	protected InfoLineModel(String source) {
		super(ModelID.INFOLINE_MODEL, source);
	}	

	@Override
	public InfoLine createEmptyValue() {
		return new InfoLine();
	}

	@Override
	public InfoLine extractValue(String source) {
		return new InfoLine(source);
	}
	
}
