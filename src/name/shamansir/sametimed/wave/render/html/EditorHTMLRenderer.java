package name.shamansir.sametimed.wave.render.html;

import org.dom4j.Element;

import name.shamansir.sametimed.wave.render.PanelModel;

public class EditorHTMLRenderer extends AHTMLPanelRenderer {
	
	public static final String PANEL_ID_PREFIX = "client-editor-";		
	public static final String PANEL_CLASS     = "editor";	

	public EditorHTMLRenderer(int clientID) {
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
	}

}
