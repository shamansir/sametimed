package name.shamansir.sametimed.wave.model;

import java.util.List;

import name.shamansir.sametimed.wave.model.base.Participants;

public class ParticipantsModel extends AModel<List<String>, Participants> {
	
	protected ParticipantsModel() {
		super(ModelID.USERSLIST_MODEL);
	}
	
	protected ParticipantsModel(List<String> source) {
		super(ModelID.USERSLIST_MODEL, source);
	}	

	@Override
	public Participants createEmptyValue() {
		return new Participants();
	}

	@Override
	public Participants extractValue(List<String> source) {
		return new Participants(source);
	}	

}
