package name.shamansir.sametimed.wave.model.base;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import name.shamansir.sametimed.wave.model.base.atom.InboxElement;

public class InboxWaveView implements IModelValue {
	
	private Map<Integer, InboxElement> inboxWaves;
	
	public InboxWaveView(Map<Integer, InboxElement> inboxWaves) {
		this.inboxWaves = inboxWaves; 
	}
	
	public InboxWaveView() {
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
			jsonString += wavesEntry.getKey().toString() + ":" +
						  quot + wavesEntry.getValue().getWaveID() + quot;
			if (iter.hasNext()) jsonString += ","; 
		}
		return jsonString + "}";
	}	
	

}
