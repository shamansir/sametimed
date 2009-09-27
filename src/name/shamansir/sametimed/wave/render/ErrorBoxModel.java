package name.shamansir.sametimed.wave.render;

import java.util.List;

import name.shamansir.sametimed.wave.model.ErrorsLines;
import name.shamansir.sametimed.wave.render.proto.APanelModel;

public class ErrorBoxModel extends APanelModel<List<String>, ErrorsLines> {

	protected ErrorBoxModel(List<String> model) {
		super(model);
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
