package name.shamansir.sametimed.wave.messaging;

import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;

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

	/* public UpdateMessage(int clientId, MessageTypeID typeID, Map<String, String> arguments) {
		this(clientId, typeID, arguments, null);
	} */
	
	public static String toXMLString(UpdateMessage message) {
		// FIXME: implement	as dom4j
		String xmlCmdString = "<message>";
		xmlCmdString += "<name>" + message.getType().getName() + "</name>";
		xmlCmdString += "<owner-id>" + Integer.toString(message.getClientId()) + "</owner-id>";
		for (Map.Entry<String, String> argPair: message.getArguments().entrySet()) {
			xmlCmdString += 
				"<argument name=\"" + argPair.getKey() + "\">" + 
					StringEscapeUtils.escapeXml(argPair.getValue()) + "</argument>";			
		}		
		return xmlCmdString + "</message>";
	}
	
	public String toXMLString() {
		return UpdateMessage.toXMLString(this);
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
		String msgString = "<" + this.getType().name() + "(";
		for (Map.Entry<String, String> argPair: this.arguments.entrySet()) {
			msgString += argPair.getKey() + ":" + argPair.getValue() + ";";			
		}
		return msgString + ") [" + this.clientId + "]>"; 
	}

}

