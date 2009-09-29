package name.shamansir.sametimed.wave.model;

import java.util.Map;

import name.shamansir.sametimed.wave.model.base.InboxWaveView;
import name.shamansir.sametimed.wave.model.base.atom.InboxElement;

public class InboxModel extends AModel<Map<Integer, InboxElement>, InboxWaveView> {
	
	protected InboxModel() {
		super(ModelID.INBOX_MODEL);
	}
	
	protected InboxModel(Map<Integer, InboxElement> source) {
		super(ModelID.INBOX_MODEL, source);
	}	

	@Override
	public InboxWaveView createEmptyValue() {
		return new InboxWaveView();
	}

	@Override
	public InboxWaveView extractValue(Map<Integer, InboxElement> source) {
		return new InboxWaveView(source);
	}
	

}
