package name.shamansir.sametimed.wave.model;

import java.util.List;

import name.shamansir.sametimed.wave.model.base.ErrorsLines;

public class ErrorBoxModel extends AModel<List<String>, ErrorsLines> {

	protected ErrorBoxModel(List<String> model) {
		super(ModelID.ERRORBOX_MODEL, model);
	}
	
	protected ErrorBoxModel() {
		super(ModelID.ERRORBOX_MODEL);
	}	
	
	public List<String> getErrors() {
		return getValue().getErrors();
	}

	@Override
	protected ErrorsLines createEmptyValue() {
		return new ErrorsLines();
	}

	@Override
	protected ErrorsLines extractValue(List<String> fromSource) {
		// TODO Auto-generated method stub
		return new ErrorsLines(fromSource);
	}		
	
}
