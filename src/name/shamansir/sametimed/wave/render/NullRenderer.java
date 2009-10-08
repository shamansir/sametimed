package name.shamansir.sametimed.wave.render;

import java.util.List;

import org.waveprotocol.wave.examples.fedone.waveclient.console.ScrollableWaveView.RenderMode;

import name.shamansir.sametimed.wave.model.AModel;
import name.shamansir.sametimed.wave.model.WavesClientModel;
import name.shamansir.sametimed.wave.render.proto.IWavesClientRenderer;

/**
 * 
 * @author shamansir <shaman.sir@gmail.com>
 *
 * The Renderer that renders to nothing
 *
 * @see IWavesClientRenderer
 * 
 */

public class NullRenderer implements IWavesClientRenderer { 
	
	/**
	 * @param clientID id of the client, renderer attached to
	 */
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
	public void setModel(WavesClientModel model) { }

	@Override
	public void setRenderingMode(RenderMode mode) { }

	@Override
	public String getErrorView(String error) {
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
