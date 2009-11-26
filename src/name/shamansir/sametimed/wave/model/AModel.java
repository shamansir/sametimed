package name.shamansir.sametimed.wave.model;

import name.shamansir.sametimed.wave.model.base.IModelValue;

/**
 * @author shamansir <shaman.sir@gmail.com>
 * 
 * An abstract Model value container, can be converted to JSON format
 *  
 * @param <SourceType> The type that IModelValue instance uses to store value inside (String, for example) 
 * @param <ModelValueType> The IModelValue type that holds the value (in SourceType, internally)
 *
 * @see #extractValue(Object)
 * @see #setValue(IModelValue)
 * @see #asJSON()
 * @see IModelValue
 * @see IModelValue#asJSON()
 *
 */
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
	
	/**
	 * Extracts value from SourceType and converts it to ModelValueType (IModelValue implementer).
	 * Used in constructor to get value from source.
	 * 
	 * @see IModelValue
	 * 
	 * @param source value in the SourceType
	 * @return value in the ModelValueType (IModelValue implementer)
	 */
	public abstract ModelValueType extractValue(SourceType source);

	/**
	 * Creates empty value in ModelValueType (IModelValue implementer).
	 * Used in constructor to get empty value if source is not passed.
	 * 
	 * @see IModelValue
	 * 
	 * @return value in the ModelValueType (IModelValue implementer)
	 */	
	public abstract ModelValueType createEmptyValue();

	/** 
	 * Get model value
	 * 
	 * @return value in the ModelValueType (IModelValue implementer)
	 */
	public ModelValueType getValue() {
		return this.value;
	}
	
	/**
	 * Set model value
	 * 
	 * @param value value to set
	 */
	public void setValue(ModelValueType value) {
		this.value = value;
	}	
	

	/**
	 * Get value in JSON format
	 * 
	 * @param useEscapedQuotes use escaped quotes when converting to JSON 
	 * 				(depends of what type of quotes (single or double)
	 * 				 the value will be wrapped), must be false or true,
	 * 				 corresponding)  
	 * @return value, converted to JSON format
	 */	
	public String asJSON(boolean useEscapedQuotes) {
		return value.asJSON(useEscapedQuotes);
	}
	
	/**
	 * Get value in JSON format
	 * 
	 * @return value, converted to JSON format
	 */
	public String asJSON() {
		return value.asJSON(false);
	}	
	
	/**
	 * Get model type ID 
	 * 
	 * @return model type ID
	 */
	public ModelID getModelID() {
		return modelID;
	}

}
