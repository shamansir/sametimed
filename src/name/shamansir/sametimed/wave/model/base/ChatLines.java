package name.shamansir.sametimed.wave.model.base;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import name.shamansir.sametimed.wave.model.base.atom.ChatLine;


public class ChatLines extends StringBasedValue implements IModelValue {
	
	private List<ChatLine> chatLines;
	
	public ChatLines(List<ChatLine> chatLines) {
		this.chatLines = chatLines; 
	}
	
	public ChatLines() {
		this.chatLines = new ArrayList<ChatLine>(); 
	}		
	
	public List<ChatLine> getChatLines() {
		return chatLines;
	}

	@Override
	public String asJSON(boolean useEscapedQuotes) {
		String jsonString = "[";
		String quot = useEscapedQuotes ? "\\\"" : "\"";
		for (Iterator<ChatLine> iter = chatLines.iterator(); iter.hasNext(); ) {
			ChatLine chatLine = iter.next();
			jsonString += "{" + 
				quot + "author" + quot + ":" + quot + cleanQuotes(chatLine.getAuthor()) + quot + "," +
				quot + "text"   + quot + ":" + quot + cleanQuotes(chatLine.getText()) + quot + "}";
			if (iter.hasNext()) jsonString += ",";
		}
		return jsonString + "]";
	}

	@Override	
	public String asJSON() {
		return asJSON(false);
	}

}
