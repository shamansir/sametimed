package name.shamansir.sametimed.wave.model;

import name.shamansir.sametimed.wave.model.base.IModelValue;

public abstract class AModel<SourceType, ModelValueType extends IModelValue> {
	
	private ModelValueType value;
	private final ModelID modelID;
	
	protected AModel(ModelID modelID) {
		this(modelID, null);
	}
	
	protected AModel(ModelID modelID, SourceType source) {
		this.modelID = modelID;
		this.value = (source != null) ? extractValue(source) : createEmptyValue(); 
	}
	
	public abstract ModelValueType extractValue(SourceType source);
	
	public abstract ModelValueType createEmptyValue();
	
	public ModelValueType getValue() {
		return this.value;
	}
	
	public void setValue(ModelValueType value) {
		this.value = value;
	}	
	
	public String asJSON(boolean useEscapedQuotes) {
		return value.asJSON(useEscapedQuotes);
	}
	
	public ModelID getModelID() {
		return modelID;
	}

}
