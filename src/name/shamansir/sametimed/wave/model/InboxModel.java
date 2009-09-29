package name.shamansir.sametimed.wave.model;

import java.util.List;

import name.shamansir.sametimed.wave.model.base.InboxWaveView;

public class InboxModel extends AModel<List<String>, InboxWaveView> {
	
	protected InboxModel() {
		super(ModelID.INBOX_MODEL);
	}
	
	protected InboxModel(List<String> source) {
		super(ModelID.INBOX_MODEL, source);
	}	

	@Override
	public InboxWaveView createEmptyValue() {
		return new InboxWaveView();
	}

	@Override
	public InboxWaveView extractValue(List<String> source) {
		return new InboxWaveView(source);
	}	

}
