package name.shamansir.sametimed.wave.model;

import java.util.ArrayList;
import java.util.List;

public class Participants implements IModelValue {

	private List<String> participants;
	
	public Participants(List<String> participants) {
		this.participants = participants; 
	}
	
	public Participants() {
		this.participants = new ArrayList<String>(); 
	}		
	
	public List<String> getParticipants() {
		return participants;
	}		
	
}
