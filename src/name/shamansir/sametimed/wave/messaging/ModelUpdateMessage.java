package name.shamansir.sametimed.wave.messaging;

import java.util.HashMap;
import java.util.Map;

import name.shamansir.sametimed.wave.model.AbstractModel;
import name.shamansir.sametimed.wave.model.ModelID;

/**
 * @author shamansir <shaman.sir@gmail.com>
 * 
 * Message, that makes a signal about some model update from the server 
 * (participant added, chat message added, new wave created, document changed & s.o.)
 *
 * @see UpdateMessage
 *
 */
public class ModelUpdateMessage extends UpdateMessage {
	
	private ModelID modelID;
	private AbstractModel<?, ?> model;
	
	public static final String ALIAS_PARAM_NAME = "alias";
	public static final String VALUE_PARAM_NAME = "value";

	public ModelUpdateMessage(int clientId, ModelID modelID, AbstractModel<?, ?> model) { 
		super(clientId, MessageTypeID.MSG_MODEL_UPDATE, 
				prepareMsgArguments(modelID, model));
	}
	
	private static Map<String, String> 
			prepareMsgArguments(ModelID modelID, AbstractModel<?, ?> model) {
		Map<String, String> arguments = new HashMap<String, String>();
		arguments.put(ALIAS_PARAM_NAME, modelID.getAlias());
		arguments.put(VALUE_PARAM_NAME, model.asJSON(false));
		return arguments;
	}
	
	@Override
	public String encode() {
		String encodedMsg = getType().getName() + "(";
		encodedMsg += Integer.toString(getClientId()) + " ";
		encodedMsg += getArgument(ALIAS_PARAM_NAME) + " ";  
		encodedMsg += VALUE_PARAM_NAME + "(\"" + escapeThings(getArgument(VALUE_PARAM_NAME)) + "\")";
		return encodedMsg + ")";
	}	
	
	public ModelID getModelID() {
		return modelID;
	}
	
	public AbstractModel<?, ?> getModel() {
		return model;
	}	

}
