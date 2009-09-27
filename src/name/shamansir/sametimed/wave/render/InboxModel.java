package name.shamansir.sametimed.wave.render;

import java.util.List;

import name.shamansir.sametimed.wave.model.InboxWaveView;
import name.shamansir.sametimed.wave.render.proto.APanelModel;

public class InboxModel extends APanelModel<List<String>, InboxWaveView> {

	protected InboxModel(List<String> model) {
		super(model);
	}
	
	public List<String> getWaves() {
		return getValue().getWaves();
	}

	@Override
	protected InboxWaveView createEmptyValue() {
		return new InboxWaveView();
	}

	@Override
	protected InboxWaveView extractValue(List<String> fromSource) {
		return new InboxWaveView(fromSource);
	}
	
}
