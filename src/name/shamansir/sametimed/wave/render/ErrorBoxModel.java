package name.shamansir.sametimed.wave.render;

import java.util.ArrayList;
import java.util.List;

public class ErrorBoxModel extends APanelModel {
	
	protected class ErrorsModelValue implements IModelValue {
		
		private List<String> errors;
		
		private ErrorsModelValue(List<String> errors) {
			this.errors = errors; 
		}
		
		private ErrorsModelValue() {
			this.errors = new ArrayList<String>(); 
		}		
		
		public List<String> getErrors() {
			return errors;
		}
		
	}	

	protected ErrorBoxModel(List<String> model) {
		super(model);
	}
	
	public List<String> getErrors() {
		return ((ErrorsModelValue)getValue()).getErrors();
	}

	@Override
	protected IModelValue extractModel(List<String> fromList) {
		return new ErrorsModelValue(fromList);
	}

	@Override
	protected IModelValue createEmptyValue() {
		return new ErrorsModelValue();
	}		
	
}
