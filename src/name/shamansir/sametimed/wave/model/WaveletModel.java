package name.shamansir.sametimed.wave.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import name.shamansir.sametimed.wave.model.base.EmptyModelValue;

/**
 * @author shamansir <shaman.sir@gmail.com>
 * 
 * The whole Waves Client model that contains the inner models (inbox, 
 * chat, users list, document & s.o.) 
 *
 */

public class WaveletModel extends AbstractModel<String, EmptyModelValue> {
	
	private final int currentClientID;
	
	private Map<ModelID, AbstractModel<?, ?>> innerModels = new HashMap<ModelID, AbstractModel<?, ?>>(); 
	
	public WaveletModel(int clientID, List<ModelID> additionalModels) {
		super(ModelID.FULLWAVE_MODEL);
		this.currentClientID = clientID;
		for (ModelID modelID: ModelID.allPure()) { 
			innerModels.put(modelID, ModelFactory.createModel(modelID));
		}
		if (additionalModels != null) {
			prepareAdditionalModels(additionalModels);
		}
	}
	
	private void prepareAdditionalModels(List<ModelID> models) {
		for (ModelID modelID: models) { 
			innerModels.put(modelID, ModelFactory.createModel(modelID));
		}		
	}
	
	public void useModel(ModelID modelID, AbstractModel<?, ?> model) {
		innerModels.put(modelID, model);
	}	
	
	public <SourceType> void useModel(ModelID modelID, SourceType source) {
		innerModels.put(modelID, ModelFactory.createModel(modelID, source));
	}
	
	public AbstractModel<?, ?> getModel(ModelID modelID) {
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
		
		for (ModelID modelID: innerModels.keySet()) {   
			jsonString += quot + modelID.getAlias() + quot + ":" + innerModels.get(modelID).asJSON(useEscapedQuotes) + ",";
		}
		
		jsonString += quot + "clientId" + quot + ":" + Integer.toString(currentClientID);
		
		return jsonString + "}";
	}	

}
