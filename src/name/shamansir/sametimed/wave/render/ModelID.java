package name.shamansir.sametimed.wave.model;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum ModelID {
	
    INFOLINE_MODEL("info"),	
	INBOX_MODEL("inbox"),
	USERS_LIST_MODEL("users"),	
	CHAT_MODEL("chat"),
	EDITOR_MODEL("document"),
    CONSOLE_MODEL("console"),	 
    ERRORBOX_MODEL("errors"),
    
    FULL_WAVE_MODEL("wave")
    ;

    private static final Map<String,ModelID> lookup 
    		= new HashMap<String,ModelID>();

    static {
         for(ModelID s : EnumSet.allOf(ModelID.class)) {
              lookup.put(s.getID(), s);
         }
    }

    private String id;

    private ModelID(String id) {
         this.id = id;
    }

    public String getID() { return id; }

    public static ModelID fromID(String id) { 
         return lookup.get(id); 
    }	
    
    public String toString() {
    	return id;
    }
}
