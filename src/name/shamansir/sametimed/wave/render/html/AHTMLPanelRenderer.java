package name.shamansir.sametimed.wave.render.html;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import name.shamansir.sametimed.wave.render.IClientPanelRenderer;
import name.shamansir.sametimed.wave.render.PanelID;
import name.shamansir.sametimed.wave.render.APanelModel;
import name.shamansir.sametimed.wave.render.PanelModelFactory;

public abstract class AHTMLPanelRenderer implements IClientPanelRenderer {
	
	private static final String DEFAULT_WRAPPER_TAGNAME = "div";
	
	private final PanelID PANEL_ID;
	
	private String holderElementId;
	private int currentClientId;
	
	private String wrapperTagName = DEFAULT_WRAPPER_TAGNAME;
	private String wrapperClass = null;
	
	private APanelModel model = null;
	
	public AHTMLPanelRenderer(int clientID, PanelID panelID, APanelModel panelModel, String panelIdPrefix) {
		this.PANEL_ID = panelID;
		this.holderElementId = panelIdPrefix + String.valueOf(clientID);
		this.currentClientId = clientID;
		this.model = panelModel != null ? PanelModelFactory.createModel(panelID) : panelModel;
	}	
	
	public AHTMLPanelRenderer(int clientID, PanelID panelID, String panelIdPrefix) {
		this(clientID, panelID, null, panelIdPrefix);
	}
	
	protected void configureWrapper(Element wrapper) { }
	
	protected abstract void addElements(Element wrapper);
	protected abstract void addElements(Element wrapper, APanelModel model);
	
	@Override
	public Element createPanel() {
		Element panelWrapper = createElement(wrapperTagName);
		panelWrapper.addAttribute("id", getHolderId());
		if (wrapperClass != null) panelWrapper.addAttribute("class", wrapperClass);
		configureWrapper(panelWrapper);
		if (model != null) {
			addElements(panelWrapper, model);
		} else {
			addElements(panelWrapper);
		}
		return panelWrapper;
	}
	
	@Override
	public String getHolderId() {
		return holderElementId;
	}
	
	protected void setWrapperTagName(String tagName) {
		this.wrapperTagName = tagName;
	}
	
	protected String getWrapperTagName() {
		return wrapperTagName;
	}	
	
	protected void setWrapperClass(String className) {
		this.wrapperClass = className;
	}
	
	protected String getWrapperClass() {
		return wrapperClass;
	}
	
	protected int getCurrentClientId() {
		return currentClientId;
	}
	
	protected PanelID getPanelID() {
		return PANEL_ID;
	}
	
	public APanelModel getModel() {
		return this.model;
	}
	
	public void setModel(APanelModel model) {
		this.model = model;
	}
	
	protected Element createElement(String name) {
		return DocumentHelper.createElement(name);
	}

}
