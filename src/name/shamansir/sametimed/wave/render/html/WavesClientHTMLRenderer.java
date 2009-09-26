package name.shamansir.sametimed.wave.render.html;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import name.shamansir.sametimed.client.proto.AWavesClientRedrawEventsHandler;
import name.shamansir.sametimed.wave.render.IClientPanelRenderer;
import name.shamansir.sametimed.wave.render.IWavesClientRenderer;
import name.shamansir.sametimed.wave.render.PanelID;
import name.shamansir.sametimed.wave.render.PanelModel;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.HTMLWriter;
import org.dom4j.io.OutputFormat;

public class WavesClientHTMLRenderer implements IWavesClientRenderer {
	
	private static final Logger LOG = Logger.getLogger(WavesClientHTMLRenderer.class.getName());
	
	private final static String VIEWS_ID_PREFIX = "client-view-";	
	private final static String VIEW_CSS_CLASS = "wave-client";
	private final static String ERROR_VIEW_CSS_CLASS = "wave-error";

	private final int currentViewId;
	private final String holderElementId;
	
	private Map<PanelID, IClientPanelRenderer> panelsRenderers = new HashMap<PanelID, IClientPanelRenderer>();	
	private Map<PanelID, Element> panelsElements = new HashMap<PanelID, Element>();
	
	private Element wrapperDiv = null;
	
	private static HTMLWriter HTMLWriter = null; {		
		try {			
			OutputFormat outputFormat = OutputFormat.createPrettyPrint();
			outputFormat.setExpandEmptyElements(true);
			HTMLWriter = new HTMLWriter(outputFormat);
		} catch (UnsupportedEncodingException use) {
			LOG.severe("Failed HTML Writer initilization. Output can be unexpected or empty. " + use.getMessage());
		}
	}
	
	AWavesClientRedrawEventsHandler redrawEventsHandler = null;
	
	public WavesClientHTMLRenderer(int viewId, AWavesClientRedrawEventsHandler redrawEventsHandler) {
		this.currentViewId = viewId;
		this.holderElementId = VIEWS_ID_PREFIX + String.valueOf(viewId);
		
		panelsRenderers.put(PanelID.INFOLINE_PANEL,   new InfoLineHTMLRenderer(this.currentViewId));
		panelsRenderers.put(PanelID.INBOX_PANEL,      new InboxHTMLRenderer(this.currentViewId));
		panelsRenderers.put(PanelID.USERS_LIST_PANEL, new ParticipantsListHTMLRenderer(this.currentViewId));
		panelsRenderers.put(PanelID.CHAT_PANEL,       new ChatHTMLRenderer(this.currentViewId));
		panelsRenderers.put(PanelID.EDITOR_PANEL,     new EditorHTMLRenderer(this.currentViewId));
		panelsRenderers.put(PanelID.CONSOLE_PANEL,    new ConsoleHTMLRenderer(this.currentViewId));
		panelsRenderers.put(PanelID.ERROR_BOX_PANEL,  new ErrorBoxHTMLRenderer(this.currentViewId));
		
		this.redrawEventsHandler = redrawEventsHandler;
	}
	
	public void initialize(String userAtDomain, String waveServer) {
		wrapperDiv = DocumentHelper.createElement("div");
		wrapperDiv.addAttribute("id", this.holderElementId);	
		wrapperDiv.addAttribute("class", VIEW_CSS_CLASS);
		
		for (PanelID panelID: PanelID.values()) {
			if (panelID != PanelID.INFOLINE_PANEL) { 
				panelsElements.put(panelID, panelsRenderers.get(panelID).createPanel());
			} else {
				PanelModel infoPanelModel = new PanelModel();
				infoPanelModel.put("info", "connected as " + userAtDomain + "; waveserver: " + waveServer);
				panelsElements.put(panelID, panelsRenderers.get(panelID).createPanel(infoPanelModel));
			}
			wrapperDiv.add(panelsElements.get(panelID));
		}
				
	}
	
	public String getCompleteView() {
		return getElementAsString(wrapperDiv);
	}

	public String getErrorView(String errorText) {
		Element errorDiv = DocumentHelper.createElement("div");
		errorDiv.addAttribute("class", ERROR_VIEW_CSS_CLASS);
		errorDiv.setText(errorText);
		return getElementAsString(errorDiv);
	}

	@Override
	public String getHolderElementId() {
		return holderElementId;
	}
	
	@Override
	public int getViewId() {
		return currentViewId;
	}
	
	private static boolean writerInitialized() {
		return HTMLWriter != null;
	}
	
	private static String getElementAsString(Element element) {
		if (writerInitialized()) {
			StringWriter out = new StringWriter();			
			try {				
				HTMLWriter.setWriter(out);
				HTMLWriter.write(element);
			} catch (IOException e) {
				LOG.warning("HTML writer failed to generate element " + element.toString() + ": " + e.getMessage());
			}			
			return out.toString();
		} else {
			return "";
		}		
	}

	@Override
	public String getPanelContent(PanelID panelID) {
		return getElementAsString(
				panelsRenderers.get(panelID).createPanel());
	}

	@Override
	public String getPanelContent(PanelID panelID, PanelModel model) {
		return getElementAsString(
				panelsRenderers.get(panelID).createPanel(model));
	}
	
	public void updatePanel(PanelID panelID, PanelModel model) {
		IClientPanelRenderer panelRenderer = panelsRenderers.get(panelID);
		redrawEventsHandler.redrawClientPanel(panelRenderer.getHolderId(),
				getElementAsString(panelRenderer.createPanel(model)));
	}

	@Override
	public String getPanelElementId(PanelID panelID) {
		return panelsRenderers.get(panelID).getHolderId();
	}

	@Override
	public AWavesClientRedrawEventsHandler getRedrawEventsHandler() {
		return redrawEventsHandler;
	}	
	
}
