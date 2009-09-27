package name.shamansir.sametimed.wave.render;

import java.util.List;

import name.shamansir.sametimed.wave.model.Participants;
import name.shamansir.sametimed.wave.render.proto.APanelModel;

public class ParticipantsModel extends APanelModel<List<String>, Participants> {
	
	protected ParticipantsModel(List<String> model) {
		super(model);
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
