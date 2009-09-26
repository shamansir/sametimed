package name.shamansir.sametimed.wave.render.html;

import org.dom4j.Element;

import name.shamansir.sametimed.wave.render.PanelModel;

public class InfoLineHTMLRenderer extends AHTMLPanelRenderer {

	public static final String PANEL_ID_PREFIX = "client-infoline-";
	public static final String PANEL_CLASS     = "infoline";		
	
	public InfoLineHTMLRenderer(int clientID) {
		super(PANEL_ID_PREFIX, clientID);
		setWrapperTagName("span");
		setWrapperClass(PANEL_CLASS);
	}

	@Override
	protected void addElements(Element wrapper) {
		// TODO Auto-generated method stub
		return;
	}

	@Override
	protected void addElements(Element wrapper, PanelModel model) {
		wrapper.setText((String)model.get("info"));
	}

}
