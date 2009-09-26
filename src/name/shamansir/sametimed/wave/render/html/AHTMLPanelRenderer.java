package name.shamansir.sametimed.wave.render.html;

import name.shamansir.sametimed.wave.render.IClientPanelRenderer;
import name.shamansir.sametimed.wave.render.PanelModel;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public abstract class AHTMLPanelRenderer implements IClientPanelRenderer {
	
	private static final String DEFAULT_WRAPPER_TAGNAME = "div";
	
	private String holderElementId;
	private int currentClientId;
	
	private String wrapperTagName = DEFAULT_WRAPPER_TAGNAME;
	private String wrapperClass = null;
	
	public AHTMLPanelRenderer(String panelIdPrefix, int clientID) {
		this.holderElementId = panelIdPrefix + String.valueOf(clientID);
		this.currentClientId = clientID;
	}	
	
	protected void configureWrapper(Element wrapper) { }
	
	protected abstract void addElements(Element wrapper);
	protected abstract void addElements(Element wrapper, PanelModel model);
	
	@Override
	public Element createPanel() {
		return createPanel(null);
	}	
	
	@Override
	public Element createPanel(PanelModel model) {
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
	
	protected Element createElement(String name) {
		return DocumentHelper.createElement(name);
	}

}
