package name.shamansir.sametimed.wave.render.html;

import java.util.List;

import org.dom4j.Element;

import name.shamansir.sametimed.wave.render.PanelModel;

public class ErrorBoxHTMLRenderer extends AHTMLPanelRenderer {
	
	public static final String PANEL_ID_PREFIX = "client-errorbox-";
	public static final String PANEL_CLASS = "errorbox";

	public ErrorBoxHTMLRenderer(int clientID) {
		super(PANEL_ID_PREFIX, clientID);
		setWrapperClass(PANEL_CLASS);
	}

	@Override
	protected void addElements(Element wrapper) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void addElements(Element wrapper, PanelModel model) {
		for (String errorText: (List<String>)model.get("errors")) {
			Element errorSpan = createElement("span");
			errorSpan.setText(errorText);
			wrapper.add(errorSpan);
		}
	}
	
	

}
