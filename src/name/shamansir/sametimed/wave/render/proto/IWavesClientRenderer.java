package name.shamansir.sametimed.wave.render.proto;

import java.util.List;

import org.waveprotocol.wave.examples.fedone.waveclient.console.ScrollableWaveView.RenderMode;

import name.shamansir.sametimed.wave.model.AbstractModel;
import name.shamansir.sametimed.wave.model.WaveletModel;

/**
 * 
 * @author shamansir <shaman.sir@gmail.com>
 *
 * The renderer that renders waves client in any format. Uses WavesClientModel 
 * to get info about the current model state or uses the passed AModel 
 * implementors to re-render just part of the client view. All rendering methods
 * (renderAll, renderByModel(s)) are called from WavesClient when it is required
 * to update the view or its part.
 *
 * @see name.shamansir.sametimed.wave.WavesClient
 *
 * @see RenderMode
 * @see WaveletModel
 * @see AbstractModel
 * 
 */

public interface IWavesClientRenderer {
	
	/**
	 * Set current client model
	 * 
	 * @param model current model
	 */
	public void setModel(WaveletModel model);
	
	/**
	 * Render all panels, using the current model
	 */	
	public void renderAll();

	/**
	 * Render according panels, using the passed models
	 * 
	 * @param models models to use for rendering 
	 */		
	public void renderByModels(List<AbstractModel<?, ?>> models);

	/**
	 * Render according panel, using the passed model
	 * 
	 * @param model model to use for rendering  
	 */		
	public void renderByModel(AbstractModel<?, ?> model);

	/**
	 * Set to render using the passed render mode (plain/XML)
	 * 
	 * @see RenderMode 
	 * 
	 * @param mode new rendering mode
	 */
	public void setRenderingMode(RenderMode mode);

	/**
	 * Initialize renderer (called when client is connected)
	 * 
	 * @see name.shamansir.sametimed.wave.WavesClient#connect(String)
	 */
	public void initialize();

	/**
	 * Get ID of the element that holds the view
	 * @return element ID
	 */
	public String getHolderElementId();

	/**
	 * Get full initial view (generated after initialize())
	 * 
	 * @see #initialize()
	 * 
	 * @return initial view as String
	 */
	public String getInitialView();

	/**
	 * Get full error view, called if it is required to show error instead of client view
	 * 
	 * @param error error text
	 * 
	 * @return error view as String
	 */	
	public String getErrorView(String error);
	
}
