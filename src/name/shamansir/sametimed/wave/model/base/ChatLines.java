package name.shamansir.sametimed.wave.model.base;

import java.util.ArrayList;
import java.util.List;


public class ChatLines implements IModelValue {
	
	private List<String> chatLines;
	
	public ChatLines(List<String> chatLines) {
		this.chatLines = chatLines; 
	}
	
	public ChatLines() {
		this.chatLines = new ArrayList<String>(); 
	}		
	
	public List<String> getChatLines() {
		return chatLines;
	}

	@Override
	public String asJSON() {
		// FIXME: implement
		return null;
	}	

}
