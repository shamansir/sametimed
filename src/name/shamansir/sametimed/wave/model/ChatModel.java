package name.shamansir.sametimed.wave.model;

import java.util.List;

import name.shamansir.sametimed.wave.model.base.ChatLines;

public class ChatModel extends AModel<List<String>, ChatLines> {
	
	protected ChatModel() {
		super(ModelID.CHAT_MODEL);
	}
	
	protected ChatModel(List<String> source) {
		super(ModelID.CHAT_MODEL, source);
	}	

	@Override
	public ChatLines createEmptyValue() {
		return new ChatLines();
	}

	@Override
	public ChatLines extractValue(List<String> source) {
		return new ChatLines(source);
	}	

}