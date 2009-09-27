package name.shamansir.sametimed.wave.render.html;

import org.dom4j.Element;

import name.shamansir.sametimed.wave.render.EditorModel;
import name.shamansir.sametimed.wave.render.PanelID;
import name.shamansir.sametimed.wave.render.APanelModel;

public class EditorHTMLRenderer extends AHTMLPanelRenderer {
	
	public static final String PANEL_ID_PREFIX = "client-editor-";		
	public static final String PANEL_CLASS     = "editor";	
	
	public EditorHTMLRenderer(int clientID, EditorModel model) {
		super(clientID, PanelID.EDITOR_PANEL, model, PANEL_ID_PREFIX);
		setWrapperClass(PANEL_CLASS);		
	}
	
	public EditorHTMLRenderer(int clientID) {
		this(clientID, null);
	}	

	@Override
	protected void addElements(Element wrapper) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void addElements(Element wrapper, APanelModel model) {
		// TODO Auto-generated method stub		
	}

}
