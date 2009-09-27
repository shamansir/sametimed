package name.shamansir.sametimed.wave.render;

import java.util.ArrayList;
import java.util.List;

public class ParticipantsModel extends APanelModel {
	
	protected class ParticipantsModelValue implements IModelValue {
		
		private List<String> participants;
		
		private ParticipantsModelValue(List<String> infoLines) {
			this.participants = infoLines; 
		}
		
		private ParticipantsModelValue() {
			this.participants = new ArrayList<String>(); 
		}		
		
		public List<String> getParticipants() {
			return participants;
		}
		
	}	

	protected ParticipantsModel(List<String> model) {
		super(model);
	}
	
	public List<String> getParticipants() {
		return ((ParticipantsModelValue)getValue()).getParticipants();
	}

	@Override
	protected IModelValue extractModel(List<String> fromList) {
		return new ParticipantsModelValue(fromList);
	}

	@Override
	protected IModelValue createEmptyValue() {
		return new ParticipantsModelValue();
	}		

}
