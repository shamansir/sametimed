package name.shamansir.sametimed.wave;

import java.util.List;

import name.shamansir.sametimed.wave.model.ModelID;
import name.shamansir.sametimed.wave.render.proto.IWavesClientRenderer;

public class SimpleWavelet extends AUpdatingWavelet {

	public SimpleWavelet(int clientID, IWavesClientRenderer renderer) {
		super(clientID, renderer);
	}

	@Override
	protected List<ModelID> getAdditionalModels() {
		return null;
	}

}
