package name.shamansir.sametimed.wave.render.html;

import java.util.List;

import org.dom4j.Element;

import name.shamansir.sametimed.wave.render.ErrorBoxModel;
import name.shamansir.sametimed.wave.render.PanelID;

public class ErrorBoxHTMLRenderer extends AHTMLPanelRenderer<ErrorBoxModel> {
	
	public static final String PANEL_ID_PREFIX = "client-errorbox-";
	public static final String PANEL_CLASS = "errorbox";
	
	public ErrorBoxHTMLRenderer(int clientID, ErrorBoxModel model) {
		super(clientID, PanelID.ERROR_BOX_PANEL, model, PANEL_ID_PREFIX);
		setWrapperClass(PANEL_CLASS);		
	}
	
	public ErrorBoxHTMLRenderer(int clientID) {
		this(clientID, null);
	}

	@Override
	protected void addElements(Element wrapper) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void addElements(Element wrapper, ErrorBoxModel model) {
		List<String> errorsList = model.getErrors();
		for (String errorText: errorsList) {
			Element errorSpan = createElement("span");
			errorSpan.setText(errorText);
			wrapper.add(errorSpan);
		}
	}
	
	

}
