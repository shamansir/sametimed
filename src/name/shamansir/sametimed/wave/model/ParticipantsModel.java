package name.shamansir.sametimed.wave.model;

import java.util.List;

import org.waveprotocol.wave.model.wave.ParticipantId;

import name.shamansir.sametimed.wave.model.base.Participants;

public class ParticipantsModel extends AModel<List<ParticipantId>, Participants> {
	
	protected ParticipantsModel() {
		super(ModelID.USERSLIST_MODEL);
	}
	
	protected ParticipantsModel(List<ParticipantId> source) {
		super(ModelID.USERSLIST_MODEL, source);
	}	

	@Override
	public Participants createEmptyValue() {
		return new Participants();
	}

	@Override
	public Participants extractValue(List<ParticipantId> source) {
		return new Participants(source);
	}	

}
