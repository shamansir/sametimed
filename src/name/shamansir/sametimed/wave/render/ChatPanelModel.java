package name.shamansir.sametimed.wave.render;

import java.util.List;

import name.shamansir.sametimed.wave.model.ChatLines;
import name.shamansir.sametimed.wave.render.proto.APanelModel;

public class ChatPanelModel extends APanelModel<List<String>, ChatLines> {
	
	protected ChatPanelModel(List<String> model) {
		super(model);
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
