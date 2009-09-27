package name.shamansir.sametimed.wave.render.html;

import org.dom4j.Element;

import name.shamansir.sametimed.wave.render.ChatModel;
import name.shamansir.sametimed.wave.render.PanelID;
import name.shamansir.sametimed.wave.render.APanelModel;

public class ChatHTMLRenderer extends AHTMLPanelRenderer {
	
	public static final String PANEL_ID_PREFIX = "client-chat-";
	public static final String PANEL_CLASS     = "chat";
	
	public ChatHTMLRenderer(int clientID, ChatModel model) {
		super(clientID, PanelID.CHAT_PANEL, model, PANEL_ID_PREFIX);
		setWrapperClass(PANEL_CLASS);
	}	
	
	public ChatHTMLRenderer(int clientID) {
		this(clientID, null);
	}

	@Override
	protected void addElements(Element wrapper) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void addElements(Element wrapper, APanelModel model) {
		// TODO Auto-generated method stub
		addElements(wrapper);
	}

}
