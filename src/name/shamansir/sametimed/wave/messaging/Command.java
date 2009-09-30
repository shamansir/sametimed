package name.shamansir.sametimed.wave.messaging;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import name.shamansir.sametimed.wave.WavesClient;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

public class Command implements IServerInfoPackage {
	
	private static final Logger LOG = Logger.getLogger(Command.class.getName());	
	
	private final int clientId;
	private final CommandTypeID typeID;
	// private final WaveletOperation baseOperation;
	private final Map<String, String> arguments;

	public Command(int clientId, CommandTypeID typeID, Map<String, String> arguments/*, WaveletOperation baseOperation*/) {
		this.clientId = clientId;
		this.typeID = typeID;
		this.arguments = arguments;		
		// this.baseOperation = (baseOperation != null) ? baseOperation : tryToCreate(typeID, arguments);		
	}

	/* public Command(int clientId, CommandTypeID typeID, Map<String, String> arguments) {
		this(clientId, typeID, arguments, null);
	} */
	
	public static Command fromXMLString(String XMLString) throws DocumentException {
		try {
			Document commandDoc = DocumentHelper.parseText(XMLString);
			Element commandElement = commandDoc.getRootElement();
			
			Node commandOwnerNode = commandElement.selectSingleNode("./owner-id");
			int clientId = Integer.parseInt(commandOwnerNode.getText());
			
			Node commandNameNode = commandElement.selectSingleNode("./name");
			CommandTypeID commandType = CommandTypeID.fromName(commandNameNode.getText());
			
			if (commandType == null) {
				throw new DocumentException("No matching type found for command with name " + commandNameNode.getText());
			}
			
			List<Element> argumentsNodes = commandElement.selectNodes("./argument");
			Map<String, String> arguments = new HashMap<String, String>();
			for (Element argument: argumentsNodes) {
				arguments.put(argument.valueOf("@name"), argument.getText());
			}
			
			return new Command(clientId, commandType, arguments);
		} catch (DocumentException e) {
			LOG.severe("Exception thrown while parsing XML command text: " + XMLString + "; " + e.getMessage());
			throw e;
		}
	}
	
	public static String toXMLString(Command command) {
		// FIXME: implement	as dom4j
		String xmlCmdString = "<command>";
		xmlCmdString += "<name>" + command.getType().getName() + "</name>";
		xmlCmdString += "<owner-id>" + Integer.toString(command.getClientId()) + "</owner-id>";
		for (Map.Entry<String, String> argPair: command.getArguments().entrySet()) {
			xmlCmdString += 
				"<argument name=\"" + argPair.getKey() + "\">" + 
					argPair.getValue() + "</argument>";			
		}		
		return xmlCmdString + "</command>";
	}
	
	public String toXMLString() {
		return Command.toXMLString(this);
	}
	
	/* private static WaveletOperation tryToCreate(CommandTypeID typeID,
			Map<String, String> arguments) {
		return null;
	} */
	
	public int getClientId() {
		return clientId;
	}

	public CommandTypeID getType() {
		return typeID;
	}
	
	public String getID() {
		return typeID.getName();
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
	
	public boolean execute() {
		WavesClient client = WavesClient.get(clientId); 
		return (client != null) ? client.doCommand(this) : false;
	}
	
	@Override
	public String toString() {
		String cmdString = "<" + this.getType().name() + "(";
		for (Map.Entry<String, String> argPair: this.arguments.entrySet()) {
			cmdString += argPair.getKey() + ":" + argPair.getValue() + ";";			
		}
		return cmdString + ") [" + this.clientId + "]>"; 
	}

}
