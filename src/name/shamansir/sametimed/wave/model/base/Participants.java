package name.shamansir.sametimed.wave.model.base;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.waveprotocol.wave.model.wave.ParticipantId;

/**
 * @author shamansir <shaman.sir@gmail.com>
 * 
 * @see ParticipantId
 * 
 * @see IModelValue
 * @see JSONiableValue
 */

public class Participants extends JSONiableValue implements IModelValue {

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
	public String asJSON(boolean useEscapedQuotes) {		
		String jsonString = "[";
		String quot = useEscapedQuotes ? "\\\"" : "\"";		
		for (Iterator<ParticipantId> iter = participants.iterator(); iter.hasNext(); ) {
			jsonString += quot + escapeJSONString(iter.next().getAddress()) + quot;
			if (iter.hasNext()) jsonString += ",";
		}
		return jsonString + "]";
	}	
	
	@Override	
	public String asJSON() {
		return asJSON(false);
	}	
	
}
