package name.shamansir.sametimed.wave.render_;

import java.util.List;

import org.waveprotocol.wave.examples.fedone.waveclient.console.ScrollableWaveView.RenderMode;

import name.shamansir.sametimed.wave.model.AModel;
import name.shamansir.sametimed.wave.model.WaveModel;
import name.shamansir.sametimed.wave.render_.proto_.IWavesClientRenderer;

public class NullRenderer implements IWavesClientRenderer { 
	
	public NullRenderer(int clientID) { }
	
	@Override
	public void initialize() { }	

	@Override
	public void renderAll() { }

	@Override		
	public void renderByModel(AModel<?, ?> model) { }

	@Override
	public void renderByModels(List<AModel<?, ?>> models) { }
	
	@Override
	public void setModel(WaveModel model) { }

	@Override
	public void setRenderingMode(RenderMode normal) { }

	@Override
	public String getErrorView(String format) {
		return null;
	}

	@Override
	public String getHolderElementId() {
		return null;
	}

	@Override
	public String getInitialView() {
		return null;
	}

}
