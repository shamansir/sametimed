package name.shamansir.sametimed.wave.model.base;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import name.shamansir.sametimed.wave.model.base.atom.InboxElement;

/**
 * @author shamansir <shaman.sir@gmail.com>
 * 
 * @see InboxElement
 * 
 * @see IModelValue
 * @see JSONiableValue
 */

public class InboxModelValue extends JSONiableValue implements IModelValue {
	
	private Map<Integer, InboxElement> inboxWaves;
	
	public InboxModelValue(Map<Integer, InboxElement> inboxWaves) {
		this.inboxWaves = inboxWaves; 
	}		
	
	public InboxModelValue() {
		this.inboxWaves = new TreeMap<Integer, InboxElement>();
	}

	public Map<Integer, InboxElement> getWaves() {
		return inboxWaves;
	}

	public void updateHashedVersions() {
		// FIXME: implement (see InboxWaveView)
		
	}	
	
	@Override
	public String asJSON(boolean useEscapedQuotes) {
		String jsonString = "{";
		String quot = useEscapedQuotes ? "\\\"" : "\"";		
		Iterator<Map.Entry<Integer, InboxElement>> iter = inboxWaves.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<Integer, InboxElement> wavesEntry = iter.next();
			Integer entryID = wavesEntry.getKey();
			InboxElement inboxEntry = wavesEntry.getValue();
			jsonString += quot + entryID.toString() + quot + ":" +
						  "{" + 
						  	quot + "id" + quot + ":" 
						  		 + quot + escapeJSONString(inboxEntry.getWaveID()) + quot + "," +
						  	quot + "unread" + quot + ":" 
						  		 + Boolean.toString(inboxEntry.isNew()) + "," +
						  	quot + "current" + quot + ":" 
						  		 + Boolean.toString(inboxEntry.isOpened()) + "," +
						  	quot + "digest" + quot + ":" 
						  	     + quot + escapeJSONString(inboxEntry.getDigest()) + quot +
						  "}";
			if (iter.hasNext()) jsonString += ","; 
		}
		return jsonString + "}";
	}	
	
	@Override	
	public String asJSON() {
		return asJSON(false);
	}

}
