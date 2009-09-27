package name.shamansir.sametimed.wave.render;

import java.util.List;

public class InfoLineModel extends APanelModel {

	protected class InfoLineModelValue implements IModelValue {
		
		private String infoLine;
		
		private InfoLineModelValue(String infoLine) {
			this.infoLine = infoLine; 
		}
		
		private InfoLineModelValue() {
			this.infoLine = ""; 
		}		
		
		public String getInfoLine() {
			return infoLine;
		}
		
	}	

	protected InfoLineModel(List<String> model) {
		super(model);
	}
	
	public String getInfoLine() {
		return ((InfoLineModelValue)getValue()).getInfoLine();
	}

	@Override
	protected IModelValue extractModel(List<String> fromList) {
		return new InfoLineModelValue(fromList.get(0));
	}

	@Override
	protected IModelValue createEmptyValue() {
		return new InfoLineModelValue();
	}		
	
}
