package name.shamansir.sametimed.wave.model;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum ModelID {
	
	INFOLINE_MODEL("info"),
	INBOX_MODEL("inbox"),
	USERSLIST_MODEL("users"),	
	CHAT_MODEL("chat"),
	EDITOR_MODEL("document"),	
	CONSOLE_MODEL("console"),
	ERRORBOX_MODEL("errors"),
	FULLWAVE_MODEL("client", true)
    ;

    private static final Map<String, ModelID> lookup 
    		= new HashMap<String, ModelID>();

    static {
         for(ModelID s : EnumSet.allOf(ModelID.class)) {
              lookup.put(s.getAlias(), s);
         }
    }

    private String alias;
    private boolean isOuter;

    private ModelID(String alias) {
         this.alias = alias;
         this.isOuter = false;
    }
    
    private ModelID(String alias, boolean isOuter) {
        this.alias = alias;
        this.isOuter = isOuter;
    }    

    public String getAlias() { return alias; }

    public static ModelID fromAlias(String alias) { 
         return lookup.get(alias); 
    }
    
    // FIXME: must to return array ModelID[]
    public static List<ModelID> allInner() {
    	List<ModelID> allInner = new ArrayList<ModelID>();
    	for (ModelID modelID: ModelID.values()) {
    		if (!modelID.isOuter) allInner.add(modelID);
    	}
    	return allInner;    	
    }

    // FIXME: must to return array ModelID[]    
    public static List<ModelID> allOuter() {
    	List<ModelID> allOuter = new ArrayList<ModelID>();
    	for (ModelID modelID: ModelID.values()) {
    		if (modelID.isOuter) allOuter.add(modelID);
    	}
    	return allOuter;    	
    }
	

}
