package name.shamansir.sametimed.wave.model;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum ModelID {
	
	INFOLINE_MODEL("info"),
	INBOX_MODEL("inbox"),
	USERSLIST_MODEL("users"),	
	CHAT_MODEL("chat"),
	EDITOR_MODEL("document"),	
	CONSOLE_MODEL("console"),
	ERRORBOX_MODEL("errors"),
	FULLWAVE_MODEL("client")
    ;

    private static final Map<String,ModelID> lookup 
    		= new HashMap<String,ModelID>();

    static {
         for(ModelID s : EnumSet.allOf(ModelID.class)) {
              lookup.put(s.getAlias(), s);
         }
    }

    private String alias;

    private ModelID(String alias) {
         this.alias = alias;
    }

    public String getAlias() { return alias; }

    public static ModelID fromAlias(String alias) { 
         return lookup.get(alias); 
    }
	

}
