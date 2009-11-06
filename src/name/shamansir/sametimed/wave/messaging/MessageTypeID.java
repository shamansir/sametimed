package name.shamansir.sametimed.wave.messaging;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * @author shamansir <shaman.sir@gmail.com>
 * 
 * Enum, representing the messages types (and also linking their aliases to the types)
 *
 */
public enum MessageTypeID {
    
	/** message, making signal about update */
    MSG_MODEL_UPDATE("mupdate"),	
	/** message to acknowledge from the server the previously passed command */
	MSG_COMMAND_ACKNWLDG("cmdacknowledge")
	/*
	MSG_ADD_USER("add"),
	MSG_REMOVE_USER("remove"),
    MSG_SAY("say"),	 
    MSG_UNDO_OP("undo"), */ // encapsulate Command for these 
    ;

    private static final Map<String,MessageTypeID> lookup 
    		= new HashMap<String,MessageTypeID>();

    static {
         for(MessageTypeID s : EnumSet.allOf(MessageTypeID.class)) {
              lookup.put(s.getName(), s);
         }
    }

    private String name;

    private MessageTypeID(String name) {
         this.name = name;
    }

    /**
     * @return message alias (name)
     */    
    public String getName() { return name; }

    /**
     * returns message type using the passed alias
     * 
     * @param name alias to look for
     * @return message type
     */    
    public static MessageTypeID fromName(String name) { 
         return lookup.get(name); 
    }
}
