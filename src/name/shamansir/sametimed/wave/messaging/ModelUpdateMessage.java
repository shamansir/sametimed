package name.shamansir.sametimed.wave.messaging;

import java.util.HashMap;
import java.util.Map;

import name.shamansir.sametimed.wave.model.AModel;
import name.shamansir.sametimed.wave.model.ModelID;

public class ModelUpdateMessage extends UpdateMessage {
	
	private ModelID modelID;
	private AModel<?, ?> model;

	public ModelUpdateMessage(int clientId, ModelID modelID, AModel<?, ?> model) { 
		super(clientId, MessageTypeID.MSG_MODEL_UPDATE, 
				prepareMsgArguments(modelID, model));
	}
	
	private static Map<String, String> 
			prepareMsgArguments(ModelID modelID, AModel<?, ?> model) {
		Map<String, String> arguments = new HashMap<String, String>();
		arguments.put("alias", modelID.getAlias());
		arguments.put("value", model.asJSON(false));
		return arguments;
	}
	
	public ModelID getModelID() {
		return modelID;
	}
	
	public AModel<?, ?> getModel() {
		return model;
	}	

}
