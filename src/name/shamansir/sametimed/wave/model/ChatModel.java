package name.shamansir.sametimed.wave.model;

import java.util.List;

import name.shamansir.sametimed.wave.model.base.ChatLines;
import name.shamansir.sametimed.wave.model.base.atom.ChatLine;

/**
 * @author shamansir <shaman.sir@gmail.com>
 * 
 * @see ChatLines
 * @see ChatLine
 * 
 */

public class ChatModel extends AModel<List<ChatLine>, ChatLines> {
	
	protected ChatModel() {
		super(ModelID.CHAT_MODEL);
	}
	
	protected ChatModel(List<ChatLine> source) {
		super(ModelID.CHAT_MODEL, source);
	}	

	@Override
	public ChatLines createEmptyValue() {
		return new ChatLines();
	}

	@Override
	public ChatLines extractValue(List<ChatLine> source) {
		return new ChatLines(source);
	}	

}