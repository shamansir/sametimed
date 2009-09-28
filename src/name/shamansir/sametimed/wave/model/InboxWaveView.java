package name.shamansir.sametimed.wave.model;

import java.util.ArrayList;
import java.util.List;

public class InboxWaveView implements IModelValue {
	
	private List<String> inboxWaves;
	
	public InboxWaveView(List<String> inboxWaves) {
		this.inboxWaves = inboxWaves; 
	}
	
	public InboxWaveView() {
		this.inboxWaves = new ArrayList<String>(); 
	}		
	
	public List<String> getWaves() {
		return inboxWaves;
	}

	public void updateHashedVersions() {
		// FIXME: implement (see InboxWaveView)
		
	}	

}
