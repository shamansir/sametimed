package name.shamansir.sametimed.wave.render;

import java.util.ArrayList;
import java.util.List;

public class ChatModel extends APanelModel {
	
	protected class ChatModelValue implements IModelValue {
		
		private List<String> chatLines;
		
		private ChatModelValue(List<String> chatLines) {
			this.chatLines = chatLines; 
		}
		
		private ChatModelValue() {
			this.chatLines = new ArrayList<String>(); 
		}		
		
		public List<String> getChatLines() {
			return chatLines;
		}
		
	}	

	protected ChatModel(List<String> model) {
		super(model);
	}
	
	public List<String> getChatLines() {
		return ((ChatModelValue)getValue()).getChatLines();
	}	

	@Override
	protected IModelValue extractModel(List<String> fromList) {
		return new ChatModelValue(fromList);
	}

	@Override
	protected IModelValue createEmptyValue() {
		return new ChatModelValue();
	}	

}
