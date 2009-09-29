package name.shamansir.sametimed.wave;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum CommandTypeID {
     
     CMD_CONNECT("connect"),	
	 CMD_OPEN_WAVE("open"),
	 CMD_NEW_WAVE("new"),	
	 CMD_ADD_USER("add"),
	 CMD_REMOVE_USER("remove"),
     CMD_SAY("say"),	 
     CMD_UNDO_OP("undo"),
     CMD_MARK_READ("read"),
     CMD_CHANGE_VIEW("view"),
     CMD_QUIT("quit")
     // TODO: scroll, log, dumplog, clearlog
     ;

     private static final Map<String,CommandTypeID> lookup 
     		= new HashMap<String,CommandTypeID>();

     static {
          for(CommandTypeID s : EnumSet.allOf(CommandTypeID.class)) {
               lookup.put(s.getName(), s);
          }
     }

     private String name;

     private CommandTypeID(String name) {
          this.name = name;
     }

     public String getName() { return name; }

     public static CommandTypeID fromName(String name) { 
          return lookup.get(name); 
     }
}
