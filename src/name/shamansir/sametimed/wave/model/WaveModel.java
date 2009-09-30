package name.shamansir.sametimed.wave.model;

import java.util.HashMap;
import java.util.Map;

import name.shamansir.sametimed.wave.model.base.EmptyModelValue;

public class WaveModel extends AModel<String, EmptyModelValue> {
	
	private final int currentClientID;
	
	private Map<ModelID, AModel<?, ?>> innerModels = new HashMap<ModelID, AModel<?, ?>>(); 
	
	public WaveModel(int clientID) {
		super(ModelID.FULLWAVE_MODEL);
		this.currentClientID = clientID;
		for (ModelID modelID: ModelID.allInner()) { 
			innerModels.put(modelID, ModelFactory.createModel(modelID));
		}
	}
	
	public void setModel(ModelID modelID, AModel<?, ?> model) {
		innerModels.put(modelID, model);
	}	
	
	public <SourceType> void setModel(ModelID modelID, SourceType source) {
		innerModels.put(modelID, ModelFactory.createModel(modelID, source));
	}
	
	public AModel<?, ?> getModel(ModelID modelID) {
		return innerModels.get(modelID);
	}

	@Override
	public EmptyModelValue createEmptyValue() {
		return new EmptyModelValue();
	}

	@Override
	public EmptyModelValue extractValue(String source) {
		return new EmptyModelValue(source);
	}
	
	@Override
	public String asJSON(boolean useEscapedQuotes) {
		String jsonString = "{";
		String quot = useEscapedQuotes ? "\\\"" : "\"";		
		
		for (ModelID modelID: ModelID.allInner()) {   
			jsonString += quot + modelID.getAlias() + quot + ":" + innerModels.get(modelID).asJSON(useEscapedQuotes) + ",";
		}
		
		jsonString += quot + "clientId" + quot + ":" + Integer.toString(currentClientID);
		
		return jsonString + "}";
	}	

}
