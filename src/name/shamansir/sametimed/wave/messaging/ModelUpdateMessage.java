package name.shamansir.sametimed.wave.messaging;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
	
    private static final Log LOG = LogFactory.getLog(ModelUpdateMessage.class);
    
	private ModelID modelID;
	private AbstractModel<?, ?> model;
	
	public static final String ALIAS_PARAM_NAME = "alias";
	public static final String VALUE_PARAM_NAME = "value";
	
	// FIXME: make update messages contain both module ID and document ID

	public ModelUpdateMessage(int clientID, ModelID modelID, AbstractModel<?, ?> model) {
		super(clientID, MessageTypeID.MSG_MODEL_UPDATE, 
				prepareMsgArguments(modelID, model));
        LOG.debug("(created model update message for client " + clientID + " and model \'" + modelID.getAlias() + "\')");		
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
		encodedMsg += Integer.toString(getClientID()) + " ";
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
