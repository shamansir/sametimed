package name.shamansir.sametimed.wave.model;

import java.util.HashMap;
import java.util.Map;

import name.shamansir.sametimed.wave.model.base.EmptyModelValue;

// FIXME: must encapsulate WaveModel though
public class WaveModel extends AModel<String, EmptyModelValue> {
	
	private final int clientID;
		
	private final Map<String, AModel<?, ?>> models = new HashMap<String, AModel<?, ?>>(); {
		for (ModelID modelID: ModelID.values()) {
			models.put(modelID.toString(), ModelFactory.createModel(modelID));
		}
	}
	
	public WaveModel(int clientID) {
		super(ModelID.FULL_WAVE_MODEL);
		this.clientID = clientID; 
	}
		
	public <SourceType> void setModel(ModelID modelID, SourceType source) {
		models.put(modelID.toString(), ModelFactory.createModel(modelID, source));
	}

	public void setModel(ModelID modelID, AModel<?, ?> model) {
		models.put(modelID.toString(), model);
	}	
	
	public AModel<?, ?> getModel(ModelID modelID) {
		return models.get(modelID.toString());
	}
	
	@Override
	protected EmptyModelValue createEmptyValue() {
		return new EmptyModelValue();
	}
	
	@Override
	protected EmptyModelValue extractValue(String fromSource) {
		return new EmptyModelValue();
	}

	@Override
	public String toJSON() {
		// FIXME: implement
		return null;
	}
	
	public int getClientID() {
		return clientID;
	}

}
