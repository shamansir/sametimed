package name.shamansir.sametimed.wave.model;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author shamansir <shaman.sir@gmail.com>
 * 
 * Enum, representing the models types (and also linking their aliases to the types)
 *
 */
public enum ModelID {
	
	/** information line model (inner)  */
	INFOLINE_MODEL("info"),
	/** waves inbox model (inner)  */	
	INBOX_MODEL("inbox"),
	/** participants list model (inner)  */	
	USERSLIST_MODEL("users"),
	/** chat model (inner, not pure)  */	
	CHAT_MODEL("chat", true, false),
	/** document model (inner, not pure)  */	
	EDITOR_MODEL("document", true, false),
	/** command console model (inner)  */	
	CONSOLE_MODEL("console"),
	/** errors box model (inner) */	
	ERRORBOX_MODEL("errors"),
	/** the whole wave model (outer, not pure) */	
	FULLWAVE_MODEL("client", false, false)
    ;

    private static final Map<String, ModelID> lookup 
    		= new HashMap<String, ModelID>();

    static {
         for(ModelID s : EnumSet.allOf(ModelID.class)) {
              lookup.put(s.getAlias(), s);
         }
    }

    private String alias;
    private boolean isInner; // do not holds other models inside
    private boolean isPure; // accords to "pure" wavelet (no documents)  

    private ModelID(String alias) {
         this.alias = alias;
         this.isInner = true;
         this.isPure = true;
    }
    
    private ModelID(String alias, boolean isInner) {
        this.alias = alias;
        this.isInner = isInner;
        if (this.isInner) this.isPure = true;
    }
    
    private ModelID(String alias, boolean isInner, boolean isPure) {
        this.alias = alias;
        this.isInner = isInner;
        this.isPure = isPure;    	
    }

    /**
     * @return model type alias (name)
     */
    public String getAlias() { return alias; }

    /**
     * returns model type using the passed alias
     * 
     * @param alias alias to look for
     * @return model type
     */     
    public static ModelID fromAlias(String alias) { 
         return lookup.get(alias); 
    }
    
    // FIXME: must to return array ModelID[]
    /**
     * @return all inner-typed models
     */
    public static List<ModelID> allInner() {
    	List<ModelID> allInner = new ArrayList<ModelID>();
    	for (ModelID modelID: ModelID.values()) {
    		if (modelID.isInner) allInner.add(modelID);
    	}
    	return allInner;    	
    }    

    // FIXME: must to return array ModelID[]    
    /**
     * @return all outer-typed models
     */    
    public static List<ModelID> allOuter() {
    	List<ModelID> allOuter = new ArrayList<ModelID>();
    	for (ModelID modelID: ModelID.values()) {
    		if (!modelID.isInner) allOuter.add(modelID);
    	}
    	return allOuter;    	
    }
    
    // FIXME: must to return array ModelID[]
    /**
     * @return all pure-typed models
     */
    public static List<ModelID> allPure() {
    	List<ModelID> allPure = new ArrayList<ModelID>();
    	for (ModelID modelID: ModelID.values()) {
    		if (modelID.isPure) allPure.add(modelID);
    	}
    	return allPure;    	
    }    
	

}
