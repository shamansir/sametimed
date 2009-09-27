package name.shamansir.sametimed.wave.render.html;

import java.util.List;

import org.dom4j.Element;

import name.shamansir.sametimed.wave.render.InboxModel;
import name.shamansir.sametimed.wave.render.PanelID;

public class InboxHTMLRenderer extends AHTMLPanelRenderer<InboxModel> {
	
	public static final String PANEL_ID_PREFIX = "client-inbox-";
	public static final String PANEL_CLASS     = "inbox";	

	public InboxHTMLRenderer(int clientID, InboxModel model) {
		super(clientID, PanelID.CHAT_PANEL, model, PANEL_ID_PREFIX);
		setWrapperClass(PANEL_CLASS);		
	}
	
	public InboxHTMLRenderer(int clientID) {
		this(clientID, null);
	}

	@Override
	protected void addElements(Element wrapper) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void addElements(Element wrapper, InboxModel model) {
		List<String> waves = model.getWaves();
		for (String waveName: waves) {
			Element waveNameSpan = createElement("span");
			waveNameSpan.setText(waveName);
			wrapper.add(waveNameSpan);
		}
	}

}
