package name.shamansir.sametimed.wave.render;

import java.util.ArrayList;
import java.util.List;

public class InboxModel extends APanelModel {

	protected class InboxModelValue implements IModelValue {
		
		private List<String> inboxWaves;
		
		private InboxModelValue(List<String> inboxWaves) {
			this.inboxWaves = inboxWaves; 
		}
		
		private InboxModelValue() {
			this.inboxWaves = new ArrayList<String>(); 
		}		
		
		public List<String> getWaves() {
			return inboxWaves;
		}
		
	}	

	protected InboxModel(List<String> model) {
		super(model);
	}
	
	public List<String> getWaves() {
		return ((InboxModelValue)getValue()).getWaves();
	}

	@Override
	protected IModelValue extractModel(List<String> fromList) {
		return new InboxModelValue(fromList);
	}

	@Override
	protected IModelValue createEmptyValue() {
		return new InboxModelValue();
	}	
	
}
