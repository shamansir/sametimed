package name.shamansir.sametimed.wave.render.html;

import org.dom4j.Element;

import name.shamansir.sametimed.wave.render.InfoLineModel;
import name.shamansir.sametimed.wave.render.PanelID;
import name.shamansir.sametimed.wave.render.APanelModel;

public class InfoLineHTMLRenderer extends AHTMLPanelRenderer {

	public static final String PANEL_ID_PREFIX = "client-infoline-";
	public static final String PANEL_CLASS     = "infoline";		
	
	public InfoLineHTMLRenderer(int clientID, InfoLineModel model) {
		super(clientID, PanelID.INFOLINE_PANEL, model, PANEL_ID_PREFIX);
		setWrapperTagName("span");
		setWrapperClass(PANEL_CLASS);	
	}
	
	public InfoLineHTMLRenderer(int clientID) {
		this(clientID, null);
	}	

	@Override
	protected void addElements(Element wrapper) {
		// TODO Auto-generated method stub
		return;
	}

	@Override
	protected void addElements(Element wrapper, APanelModel model) {		
		wrapper.setText(((InfoLineModel)model).getInfoLine());
	}

}
