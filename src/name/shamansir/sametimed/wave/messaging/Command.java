package name.shamansir.sametimed.wave.messaging;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import name.shamansir.sametimed.wave.WavesClient;

import org.apache.commons.lang.StringEscapeUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 * 
 * @author shamansir <shaman.sir@gmail.com>
 * 
 * Command, representing the changes called from the client (adding user, saying
 * something, creating wave, opening it, modifying document & s.o.). It is passed from 
 * the client to the server in XML format through CommandExecutorServlet, 
 * servlet loads arguments inside and calls execute method  
 * 
 * @see #fromXMLString(String)
 * @see #execute()
 * @see CommandsReceiverServlet
 * @see CommandTypeID
 *
 * @see IServerInfoPackage
 *  
 */

public class Command implements IServerInfoPackage {
	
	private static final Logger LOG = Logger.getLogger(Command.class.getName());	
	
	private final int clientId;
	private final CommandTypeID typeID;
	private final String relatedDocumentID;
	// private final WaveletOperation baseOperation;
	private final Map<String, String> arguments;

	public Command(int clientId, CommandTypeID typeID, Map<String, String> arguments/*, WaveletOperation baseOperation*/) {
		this.clientId = clientId;
		this.typeID = typeID;
		this.arguments = arguments;
		this.relatedDocumentID = null;		
		// this.baseOperation = (baseOperation != null) ? baseOperation : tryToCreate(typeID, arguments);		
	}
	
	public Command(int clientId, CommandTypeID typeID, String relatedDocumentID, Map<String, String> arguments) {
		this.clientId = clientId;
		this.typeID = typeID;
		this.relatedDocumentID = relatedDocumentID;		
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
			
			String relatedDocument = null;
			if (commandType.isDocumentRelated()) {
				Node documentIDNode = commandElement.selectSingleNode("./for-document");
				if ((documentIDNode == null) || "".equals(documentIDNode.getText())) {
					throw new DocumentException("Document-related command must specify ID of the related document ('for-document' tag)");
				}
				relatedDocument = documentIDNode.getText();
			}
			
			List<Element> argumentsNodes = (List<Element>)commandElement.selectNodes("./argument");
			Map<String, String> arguments = new HashMap<String, String>();
			for (Element argument: argumentsNodes) {
				arguments.put(argument.valueOf("@name"), StringEscapeUtils.unescapeXml(argument.getText()));
			}
			
			return new Command(clientId, commandType, relatedDocument, arguments);
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
		xmlCmdString += "<for-document>" + command.getRelatedDocumentID() + "</for-document>";
		for (Map.Entry<String, String> argPair: command.getArguments().entrySet()) {
			xmlCmdString += 
				"<argument name=\"" + argPair.getKey() + "\">" + 
					StringEscapeUtils.escapeXml(argPair.getValue()) + "</argument>";			
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
	
	public String getRelatedDocumentID() {
		return relatedDocumentID;
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
