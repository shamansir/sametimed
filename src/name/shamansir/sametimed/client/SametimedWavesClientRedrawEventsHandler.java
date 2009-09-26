package name.shamansir.sametimed.client;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;

import name.shamansir.sametimed.client.proto.AWavesClientRedrawEventsHandler;

class SametimedWavesClientRedrawEventsHandler extends AWavesClientRedrawEventsHandler implements IsSerializable {
	
	private static final long serialVersionUID = -8652109648642876118L;

	/* Empty constructor required for Serializable classes */
	public SametimedWavesClientRedrawEventsHandler() { }

	public void redrawClientPanel(String holderId, String withContent) {
		RootPanel panelElement = RootPanel.get(holderId);
		if (panelElement != null) {
			// FIXME: remove this element from parent and add it again
			// RootPanel element = viewsContainer.get(clientIdToHolder.get(clientId)).get(elementId);
			panelElement.clear();
			panelElement.getElement().setInnerHTML("");
			panelElement.getElement().setInnerText("");			
			panelElement.add(new HTML(withContent));
		}
	}
	
}
