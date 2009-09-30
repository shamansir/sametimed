package name.shamansir.sametimed.wave.messaging;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum MessageTypeID {
    
    MSG_MODEL_UPDATE("mupdate"),	
	MSG_COMMAND_ACKNWLDG("cmdacknowlege")
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

    public String getName() { return name; }

    public static MessageTypeID fromName(String name) { 
         return lookup.get(name); 
    }
}
