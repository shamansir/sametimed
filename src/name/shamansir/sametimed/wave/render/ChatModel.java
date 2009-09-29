package name.shamansir.sametimed.wave.model;

import java.util.List;

import name.shamansir.sametimed.wave.model.base.ChatLines;

public class ChatModel extends AModel<List<String>, ChatLines> {
	
	protected ChatModel(List<String> model) {
		super(ModelID.CHAT_MODEL, model);
	}
	
	protected ChatModel() {
		super(ModelID.CHAT_MODEL);
	}	
	
	public List<String> getChatLines() {
		return getValue().getChatLines();
	}

	@Override
	protected ChatLines createEmptyValue() {
		return new ChatLines();
	}

	@Override
	protected ChatLines extractValue(List<String> fromSource) {
		return new ChatLines(fromSource);
	}

}
