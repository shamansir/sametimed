package name.shamansir.sametimed.wave.messaging;

import java.util.Map;

public interface IServerInfoPackage {
	
	public int getClientId();
	
	public String getID();
	public Map<String, String> getArguments();
	public String getArgument(String name);	
	
	public String toXMLString();

}
