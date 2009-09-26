package name.shamansir.sametimed.wave.render.html;

import org.dom4j.Element;

import name.shamansir.sametimed.wave.render.PanelModel;

public class ChatHTMLRenderer extends AHTMLPanelRenderer {
	
	public static final String PANEL_ID_PREFIX = "client-chat-";
	public static final String PANEL_CLASS     = "chat";
	
	public ChatHTMLRenderer(int clientID) {
		super(PANEL_ID_PREFIX, clientID);
		setWrapperClass(PANEL_CLASS);
	}	

	@Override
	protected void addElements(Element wrapper) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void addElements(Element wrapper, PanelModel model) {
		// TODO Auto-generated method stub
		addElements(wrapper);
	}

}
