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

import org.waveprotocol.wave.model.operation.wave.WaveletOperation;

public class Command {
	
	private static final Logger LOG = Logger.getLogger(Command.class.getName());	
	
	private final CommandTypeID typeID;
	private final WaveletOperation baseOperation;
	private final Map<String, String> arguments;

	public Command(CommandTypeID typeID, Map<String, String> arguments, WaveletOperation baseOperation) {
		this.typeID = typeID;
		this.arguments = arguments;		
		this.baseOperation = (baseOperation != null) ? baseOperation : tryToCreate(typeID, arguments);		
	}

	public Command(CommandTypeID typeID, Map<String, String> arguments) {
		this(typeID, arguments, null);
	}
	
	public static Command fromXMLString(String XMLString) throws DocumentException {
		try {
			Document commandDoc = DocumentHelper.parseText(XMLString);
			Element commandElement = commandDoc.getRootElement();
			
			Node commandNameNode = commandElement.selectSingleNode("/name");
			CommandTypeID commandType = CommandTypeID.fromName(commandNameNode.getText());
			
			if (commandType == null) {
				throw new DocumentException("No matching type found for command with name " + commandNameNode.getText());
			}
			
			List<Element> argumentsNodes = commandElement.selectNodes("/argument");
			Map<String, String> arguments = new HashMap<String, String>();
			for (Element argument: argumentsNodes) {
				arguments.put(argument.valueOf("@name"), argument.getText());
			}
			
			return new Command(commandType, arguments);
		} catch (DocumentException e) {
			LOG.severe("Exception thrown while parsing XML command text: " + XMLString + "; " + e.getMessage());
			throw e;
		}
	}
	
	public static String toXMLString(Command command) {
		// FIXME: implement		
		return "";
	}
	
	private static WaveletOperation tryToCreate(CommandTypeID typeID,
			Map<String, String> arguments) {
		// FIXME: implement
		return null;
	}

	public CommandTypeID getType() {
		return typeID;
	}

	public WaveletOperation getBaseOperation() {
		return baseOperation;
	}

	public Map<String, String> getArguments() {
		return arguments;
	}	

}
