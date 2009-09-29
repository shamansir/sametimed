package name.shamansir.sametimed.wave.render_;

import java.util.List;

import org.waveprotocol.wave.examples.fedone.waveclient.console.ScrollableWaveView.RenderMode;

import name.shamansir.sametimed.wave.model.AModel;
import name.shamansir.sametimed.wave.model.WaveModel;

public interface IWavesClientRenderer {
	
	public void setModel(WaveModel model);
	
	public void renderAll();
	
	public void renderByModels(List<AModel <?, ?>> models);
	
	public void renderByModel(AModel<?, ?> model);

	public void setRenderingMode(RenderMode normal);

	public void initialize();

	public String getHolderElementId();

	public String getInitialView();

	public String getErrorView(String format);
	
}
