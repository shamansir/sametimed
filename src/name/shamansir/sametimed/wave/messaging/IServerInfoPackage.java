package name.shamansir.sametimed.wave.messaging;

import java.util.Map;

/**
 * 
 * @author shamansir <shaman.sir@gmail.com>
 * 
 * Some package, containing info that can be passed from client to server or back
 * (like command or message) 
 *
 */

public interface IServerInfoPackage {
	
	/** @return id of the client, this package related to */
	public int getClientId();

	/** @return package name or alias */
	public String getID();
	/** @return HashMap of the arguments (name/value) */	
	public Map<String, String> getArguments();
	/**
	 * Get argument value by its name
	 * 
	 * @param name name of the argument
	 * @return argument value
	 */	
	public String getArgument(String name);
	
	/** @return package, encoded in a single line */		
	public String encode();

}
