package name.shamansir.sametimed.wave.model;

import java.util.Map;

import name.shamansir.sametimed.wave.model.base.InboxModelValue;
import name.shamansir.sametimed.wave.model.base.atom.InboxElement;

public class InboxModel extends AModel<Map<Integer, InboxElement>, InboxModelValue> {
	
	protected InboxModel() {
		super(ModelID.INBOX_MODEL);
	}
	
	protected InboxModel(Map<Integer, InboxElement> source) {
		super(ModelID.INBOX_MODEL, source);
	}	

	@Override
	public InboxModelValue createEmptyValue() {
		return new InboxModelValue();
	}

	@Override
	public InboxModelValue extractValue(Map<Integer, InboxElement> source) {
		return new InboxModelValue(source);
	}
	

}
