package name.shamansir.sametimed.wave.model.base;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.waveprotocol.wave.model.wave.ParticipantId;

public class Participants implements IModelValue {

	private List<ParticipantId> participants;
	
	public Participants(List<ParticipantId> participants) {
		this.participants = participants; 
	}
	
	public Participants() {
		this.participants = new ArrayList<ParticipantId>(); 
	}		
	
	public List<ParticipantId> getParticipants() {
		return participants;
	}		
	
	@Override
	public String asJSON() {
		String jsonString = "[";
		for (Iterator<ParticipantId> iter = participants.iterator(); iter.hasNext(); ) {
			jsonString += "'" + iter.next().getAddress() + "'";
			if (iter.hasNext()) jsonString += ",";
		}
		return jsonString + "]";
	}	
	
	
}
