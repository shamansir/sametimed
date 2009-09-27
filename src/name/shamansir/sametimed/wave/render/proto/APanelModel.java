package name.shamansir.sametimed.wave.render.proto;

import name.shamansir.sametimed.wave.model.IModelValue;

public abstract class APanelModel<SourceType, ValueType extends IModelValue> {
	
	private ValueType value;
	
	protected APanelModel(SourceType source) {
		value = (source != null) ? extractValue(source) : createEmptyValue();
	}
	
	protected APanelModel() {
		this(null);
	}
	
	public ValueType getValue() {
		return value;
	}
	
	/* 
	protected void setValue(ValueType value) {
		this.value = value;
	}*/

	protected abstract ValueType extractValue(SourceType fromSource);
	protected abstract ValueType createEmptyValue();
	
}
