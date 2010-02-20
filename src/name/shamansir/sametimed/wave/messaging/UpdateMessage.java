package name.shamansir.sametimed.wave.messaging;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author shamansir <shaman.sir@gmail.com>
 * 
 * Message, that makes a signal about some update from the server 
 * model update or signal that some command was performed
 *
 */
public class UpdateMessage implements IServerInfoPackage {
	
	private static final Log LOG = LogFactory.getLog(UpdateMessage.class.getName());	
	
	private final int clientID;
	private final MessageTypeID typeID;
	// private final WaveletOperation baseOperation;
	private final Map<String, String> arguments;

	public UpdateMessage(int clientID, MessageTypeID typeID, Map<String, String> arguments/*, WaveletOperation baseOperation*/) {
		this.clientID = clientID;
		this.typeID = typeID;
		this.arguments = arguments;		
		// this.baseOperation = (baseOperation != null) ? baseOperation : tryToCreate(typeID, arguments);
		
		LOG.debug("created update message for client " + clientID + " with type \'" + typeID.getName() + "\'");
	}
	
	@Override
	public String encode() {
		String encodedMsg = getType().getName() + "(";
		encodedMsg += Integer.toString(getClientID()) + " ";
		for (Map.Entry<String, String> argPair: getArguments().entrySet()) {
			encodedMsg += argPair.getKey() + "(\"" + escapeThings(argPair.getValue()) + "\") ";			
		}
		return encodedMsg + ")";
	}
	
	// TODO: extract in some utils class?
	protected static String escapeThings(String strToEscape) {
		return strToEscape.replaceAll("\"", "&_qt;");
	}
	
	/* private static WaveletOperation tryToCreate(MessageTypeID typeID,
			Map<String, String> arguments) {
		return null;
	} */
	
	public int getClientID() {
		return clientID;
	}
	
	public String getID() {
		return typeID.getName();
	}

	public MessageTypeID getType() {
		return typeID;
	}

	/* public WaveletOperation getBaseOperation() {
		return baseOperation;
	} */

	public Map<String, String> getArguments() {
		return arguments;
	}	
	
	public String getArgument(String name) {
		return arguments.get(name);
	}
	
	@Override
	public String toString() {
		return this.encode();
	}

}

