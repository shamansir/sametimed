package name.shamansir.sametimed.wave.messaging;

import java.text.ParseException;

import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import name.shamansir.sametimed.wave.WavesClient;

// TODO: change to CommandsRegistrar

/**
 * 
 * @author shamansir <shaman.sir@gmail.com>
 * 
 * Command, representing the changes called from the client (adding user, saying
 * something, creating wave, opening it, modifying document & s.o.). It is passed from 
 * the client to the server in XML format through CommandExecutorServlet, 
 * servlet loads arguments inside and calls execute method  
 * 
 * @see #decode(String)
 * @see #execute()
 * @see CommandsReceiverServlet
 * @see CommandTypeID
 *
 * @see IServerInfoPackage
 *  
 */

public class Command implements IServerInfoPackage {
	
	private static final Logger LOG = Logger.getLogger(Command.class.getName());
	
	// Commands format: cmd(32[ chat][ attr1("val1") attr2("val2") ... ])
	// Example of correct command: cmd(32 chat arg1("val1") arg2("val2") )
	// `cmd` is command name, `32` is client ID it belongs to, `chat` is optional 
	// document ID this command related to, and then a list of attributes
	//
	// - full RE:
	//                   cmd  ( 32         chat         text ( "funny txt" )      )  
	// non-escaped RE: ^(\w+)\((\d+)(?:\s+(\w+))?\s+(?:(\w+)\(\"([^\"]*)\"\)\s*)*\)$
	//         groups:  ( 1 )  ( 2 )      ( 3 )        (4,6)    (5,7,..)
	//
	// - just command RE (capturing all args in one group):
	//                   cmd  ( 32         chat     args )
	// non-escaped RE: ^(\w+)\((\d+)(?:\s+(\w+))?\s+(.*)\)$
	//         groups:  ( 1 )  ( 2 )      ( 3 )     ( 4)   
	//
	// - just arguments RE (matching all arguments):
	//                      text ( "funny txt" )
	// non-escaped RE: ^(?:(\w+)\(\"([^\"]*)\"\)\s*)*$
	//         groups:     ( 1 )    ( 2    )	
	//
	// - one argument RE (matching arguments one by one):
	//                  text ( "funny txt" )
	// non-escaped RE: (\w+)\(\"([^\"]*)\"\)
	//         groups: ( 1 )    ( 2    )	
	protected static final String CMD_RE = "^" + // from the beginning of the line
								           "(\\w+)\\(" + // a word (command name, group 1), and opening bracket symbol then;
								           "(\\d+)"    + // some digits (client id, group 2) following;
								           "(?:\\s+(\\w+))?\\s+" + // an optional word after spaces (document id, group 3) 
								           					       // and some spaces next 
								           "(.*)" + // capture all arguments in single group 4
								           "\\)$"; // a closing bracket for command content and the end of line
	protected static final String ARG_RE = "(\\w+)\\(" + // some word (argument name, group 1), and opening bracket then; 
										   "\\\"([^\\\"]*)\\\"" + // double quotes, any text without double quotes 
											                            // (argument value, group 2), and closing double quotes
										   "\\)"; // a closing bracket 
	private static final Pattern CMD_RE_PATTERN = Pattern.compile(CMD_RE);
	private static final Pattern ARG_RE_PATTERN = Pattern.compile(ARG_RE);
	
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

	public Command(int clientId, CommandTypeID commandType,
			String relatedDocument) {
		this(clientId, commandType, relatedDocument, new HashMap<String, String>());
	}
	
	public static synchronized Command decode(String encodedCmd) throws ParseException {
		Matcher cmdMatcher = CMD_RE_PATTERN.matcher(encodedCmd);
		if (cmdMatcher.matches()) {
			try {
				CommandTypeID commandType = CommandTypeID.fromName(cmdMatcher.group(1));
				if (commandType == null) throw new ParseException("No matching type found for command with name \"" + cmdMatcher.group(0) + "\"", 0);
				int clientId = Integer.parseInt(cmdMatcher.group(2));
				String relatedDocument = null;
				if (commandType.isDocumentRelated()) {
					relatedDocument = cmdMatcher.group(3);
					if ((relatedDocument == null) || "".equals(relatedDocument)) {
						throw new ParseException("Document-related command must specify ID of the related document (see command format)", 0);
					}
				}
				String argsLine = cmdMatcher.group(4);
				Matcher argsMatcher = ARG_RE_PATTERN.matcher(argsLine);
				Map<String, String> arguments = new HashMap<String, String>();
				while (argsMatcher.find()) {
					arguments.put(argsMatcher.group(1), unescapeThings(argsMatcher.group(2)));					
				}				
				return new Command(clientId, commandType, relatedDocument, arguments);
			} catch (NumberFormatException nfe) {
				LOG.severe("Exception thrown while decoding/parsing command text: \"" + encodedCmd + "\"; " + nfe.getMessage());
				throw new ParseException(nfe.getMessage(), 0);
			} catch (ParseException pe) {
				LOG.severe("Exception thrown while decoding/parsing command text: \"" + encodedCmd + "\"; " + pe.getMessage());
				throw pe;
			}
		} else throw new ParseException("Command [" + encodedCmd + "] can not be parsed because it does not matches regular expression " + CMD_RE, 0);
	}
	
	@Override
	public String encode() {
		String encodedCmd = getType().getName() + "(";
		encodedCmd += Integer.toString(getClientId()) + " ";
		if (getType().isDocumentRelated()) {
			encodedCmd += getRelatedDocumentID() + " ";
		}
		for (Map.Entry<String, String> argPair: getArguments().entrySet()) {
			encodedCmd += argPair.getKey() + "(\"" + escapeThings(argPair.getValue()) + "\") ";			
		}
		return encodedCmd + ")";
	}
	
	// TODO: extract in some utils class?
	protected static String escapeThings(String strToEscape) {
		return strToEscape.replaceAll("\"", "&_qt;");
	}	
	
	// TODO: extract in some utils class?
	protected static String unescapeThings(String escapedStr) {
		return escapedStr.replaceAll("&_qt;", "\"");
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
	
	@SuppressWarnings("unchecked")
	public boolean execute() {
		WavesClient client = WavesClient.get(clientId); 
		return (client != null) ? client.doCommand(this) : false;
	}
	
	@Override
	public String toString() {
		return encode(); 
	}

}
