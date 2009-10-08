package name.shamansir.sametimed.wave.model;

import java.util.List;

import name.shamansir.sametimed.wave.model.base.ErrorsLines;

/**
 * @author shamansir <shaman.sir@gmail.com>
 * 
 * @see ErrorsLines 
 */

public class ErrorsModel extends AModel<List<String>, ErrorsLines> {
	
	protected ErrorsModel() {
		super(ModelID.ERRORBOX_MODEL);
	}
	
	protected ErrorsModel(List<String> source) {
		super(ModelID.ERRORBOX_MODEL, source);
	}	

	@Override
	public ErrorsLines createEmptyValue() {
		return new ErrorsLines();
	}

	@Override
	public ErrorsLines extractValue(List<String> source) {
		return new ErrorsLines(source);
	}	

}
