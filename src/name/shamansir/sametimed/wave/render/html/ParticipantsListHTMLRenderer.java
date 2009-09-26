package name.shamansir.sametimed.wave.render.html;

import java.util.List;

import name.shamansir.sametimed.wave.render.PanelModel;

import org.dom4j.Element;

public class ParticipantsListHTMLRenderer extends AHTMLPanelRenderer {
	
	public static final String PANEL_ID_PREFIX = "client-userlist-";
	public static final String PANEL_CLASS     = "userlist";		

	public ParticipantsListHTMLRenderer(int clientID) {
		super(PANEL_ID_PREFIX, clientID);
		setWrapperTagName("ul");
		setWrapperClass(PANEL_CLASS);
	}

	@Override
	protected void addElements(Element wrapper) {
		// TODO Auto-generated method stub
		Element liElement = createElement("li");
		wrapper.add(liElement);
	}

	@Override
	protected void addElements(Element wrapper, PanelModel model) {
		// TODO Auto-generated method stub
		for (String participantName: (List<String>)model.get("participants")) {
			Element liElement = createElement("li");
			liElement.setText(participantName);
			wrapper.add(liElement);
		}
	}

}
