package name.shamansir.sametimed.wave.model;

import java.util.List;

import name.shamansir.sametimed.wave.model.base.InboxWaveView;

public class InboxModel extends AModel<List<String>, InboxWaveView> {

	protected InboxModel(List<String> model) {
		super(ModelID.INBOX_MODEL, model);
	}
	
	protected InboxModel() {
		super(ModelID.INBOX_MODEL);
	}	
	
	public List<String> getWaves() {
		return getValue().getWaves();
	}

	@Override
	protected InboxWaveView createEmptyValue() {
		return new InboxWaveView();
	}

	@Override
	protected InboxWaveView extractValue(List<String> fromSource) {
		return new InboxWaveView(fromSource);
	}
	
}
