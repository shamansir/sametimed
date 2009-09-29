package name.shamansir.sametimed.wave.model.base;

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
	
	@Override
	public String asJSON() {
		// FIXME: implement
		return null;
	}	
	
	
}
