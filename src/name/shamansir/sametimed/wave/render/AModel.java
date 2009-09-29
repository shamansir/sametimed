package name.shamansir.sametimed.wave.model;

import name.shamansir.sametimed.wave.model.base.IModelValue;

public abstract class AModel<SourceType, ValueType extends IModelValue> {
	
	private ValueType value;
	protected final ModelID MODEL_ID; 
	
	protected AModel(ModelID id, SourceType source) {
		this.MODEL_ID = id;		
		this.value = (source != null) ? extractValue(source) : createEmptyValue();
	}
	
	protected AModel(ModelID id) {
		this(id, null);
	}
	
	public ValueType getValue() {
		return value;
	}
	
	public ModelID getModelID() {
		return MODEL_ID;
	}
	
	/* 
	protected void setValue(ValueType value) {
		this.value = value;
	}*/

	protected abstract ValueType extractValue(SourceType fromSource);
	protected abstract ValueType createEmptyValue();
	
	public String toJSON() {
		return value.toJSON();
	}
	
}
