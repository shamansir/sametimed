package name.shamansir.sametimed.wave.messaging;

import java.util.Map;

/**
 * @author shamansir <shaman.sir@gmail.com>
 * 
 * Message, that makes a signal about some update from the server 
 * model update or signal that some command was performed
 *
 */
public class UpdateMessage implements IServerInfoPackage {
	
	// private static final Logger LOG = Logger.getLogger(UpdateMessage.class.getName());	
	
	private final int clientId;
	private final MessageTypeID typeID;
	// private final WaveletOperation baseOperation;
	private final Map<String, String> arguments;

	public UpdateMessage(int clientId, MessageTypeID typeID, Map<String, String> arguments/*, WaveletOperation baseOperation*/) {
		this.clientId = clientId;
		this.typeID = typeID;
		this.arguments = arguments;		
		// this.baseOperation = (baseOperation != null) ? baseOperation : tryToCreate(typeID, arguments);		
	}
	
	@Override
	public String encode() {
		String encodedMsg = getType().getName() + "(";
		encodedMsg += Integer.toString(getClientId()) + " ";
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
	
	public int getClientId() {
		return clientId;
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

