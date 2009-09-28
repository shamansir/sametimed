package name.shamansir.sametimed.wave;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

public class Command {
	
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
	
	public static Command fromXMLString(String XMLString, int clientId) throws DocumentException {
		try {
			Document commandDoc = DocumentHelper.parseText(XMLString);
			Element commandElement = commandDoc.getRootElement();
			
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
		// FIXME: implement		
		return "";
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

}
