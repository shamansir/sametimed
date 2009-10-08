package name.shamansir.sametimed.wave.messaging;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * @author shamansir <shaman.sir@gmail.com>
 * 
 * Enum, representing the commands types (and also linking their aliases to the types)
 *
 */
public enum CommandTypeID {
     
     /** connection command (user,server,port) */
     CMD_CONNECT("connect"),
     /** wave opening command (entry) */
	 CMD_OPEN_WAVE("open"),
	 /** wave creation command () */
	 CMD_NEW_WAVE("new"),
	 /** adding participant command (user) */
	 CMD_ADD_USER("add"),
	 /** removing participant command (user) */
	 CMD_REMOVE_USER("remove"),
	 /** saying something command (text) */
     CMD_SAY("say"),
     /** undo last action command (user?) */
     CMD_UNDO_OP("undo"),
     /** mark all waves as read command () */
     CMD_MARK_READ("read"),
     /** view mode changing command (mode) */
     CMD_CHANGE_VIEW("view"),
     /** quitting form client command () */
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

     /**
      * @return command alias (name)
      */
     public String getName() { return name; }

     /**
      * returns command type using the passed alias
      * 
      * @param name alias to look for
      * @return command type
      */
     public static CommandTypeID fromName(String name) { 
          return lookup.get(name); 
     }
}
