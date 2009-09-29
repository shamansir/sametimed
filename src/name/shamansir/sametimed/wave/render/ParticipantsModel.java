package name.shamansir.sametimed.wave.model;

import java.util.List;

import name.shamansir.sametimed.wave.model.base.Participants;

public class ParticipantsModel extends AModel<List<String>, Participants> {
	
	protected ParticipantsModel(List<String> model) {
		super(ModelID.USERS_LIST_MODEL, model);
	}
	
	protected ParticipantsModel() {
		super(ModelID.USERS_LIST_MODEL);
	}	
	
	public List<String> getParticipants() {
		return getValue().getParticipants();
	}

	@Override
	protected Participants extractValue(List<String> fromList) {
		return new Participants(fromList);
	}

	@Override
	protected Participants createEmptyValue() {
		return new Participants();
	}		

}
