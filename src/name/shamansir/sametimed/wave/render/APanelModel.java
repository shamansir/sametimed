package name.shamansir.sametimed.wave.render;

import java.util.List;

public abstract class APanelModel {
	
	// required to identify model values
	public interface IModelValue {
		
	}
	
	private IModelValue value;
	
	// PanelModelFactory is intended to instantiate models
	protected APanelModel(List<String> model) {
		this.value = (model != null) ? extractModel(model) : createEmptyValue();
	}
	
	protected APanelModel() {
		this(null);
	}
	
	public IModelValue getValue() {
		return value;
	}	
	
	protected void setValue(IModelValue value) {
		this.value = value;
	}
	
	protected abstract IModelValue createEmptyValue();
	
	protected abstract IModelValue extractModel(List<String> fromList);

}
