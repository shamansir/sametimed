package name.shamansir.sametimed.wave.render.html;

import java.util.List;

import name.shamansir.sametimed.wave.render.PanelID;
import name.shamansir.sametimed.wave.render.ParticipantsModel;

import org.dom4j.Element;

public class ParticipantsListHTMLRenderer extends AHTMLPanelRenderer<ParticipantsModel> {
	
	public static final String PANEL_ID_PREFIX = "client-userlist-";
	public static final String PANEL_CLASS     = "userlist";
	
	public ParticipantsListHTMLRenderer(int clientID, ParticipantsModel model) {
		super(clientID, PanelID.USERS_LIST_PANEL, model, PANEL_ID_PREFIX);
		setWrapperTagName("ul");
		setWrapperClass(PANEL_CLASS);	
	}
	
	public ParticipantsListHTMLRenderer(int clientID) {
		this(clientID, null);
	}	

	@Override
	protected void addElements(Element wrapper) {
		Element liElement = createElement("li");
		wrapper.add(liElement);
	}

	@Override
	protected void addElements(Element wrapper, ParticipantsModel model) {
		List<String> participants = model.getParticipants();
		for (String participantName: participants) {
			Element liElement = createElement("li");
			liElement.setText(participantName);
			wrapper.add(liElement);
		}
	}

}
