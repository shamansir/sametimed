package name.shamansir.sametimed.wave.render.html;

import java.util.List;

import org.dom4j.Element;

import name.shamansir.sametimed.wave.render.PanelModel;

public class InboxHTMLRenderer extends AHTMLPanelRenderer {
	
	public static final String PANEL_ID_PREFIX = "client-inbox-";
	public static final String PANEL_CLASS     = "inbox";	

	public InboxHTMLRenderer(int clientID) {
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
		for (String waveName: (List<String>)model.get("waves")) {
			Element waveNameSpan = createElement("span");
			waveNameSpan.setText(waveName);
			wrapper.add(waveNameSpan);
		}
	}

}
