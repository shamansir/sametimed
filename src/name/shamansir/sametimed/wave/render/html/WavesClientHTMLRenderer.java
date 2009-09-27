package name.shamansir.sametimed.wave.render.html;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.HTMLWriter;
import org.dom4j.io.OutputFormat;

import name.shamansir.sametimed.client.proto.AWavesClientRedrawEventsHandler;
import name.shamansir.sametimed.wave.Command;
import name.shamansir.sametimed.wave.render.InfoLineModel;
import name.shamansir.sametimed.wave.render.PanelID;
import name.shamansir.sametimed.wave.render.PanelModelFactory;
import name.shamansir.sametimed.wave.render.proto.IClientPanelRenderer;
import name.shamansir.sametimed.wave.render.proto.IWavesClientRenderer;

public class WavesClientHTMLRenderer implements IWavesClientRenderer {
	
	private static final Logger LOG = Logger.getLogger(WavesClientHTMLRenderer.class.getName());
	
	private final static String VIEWS_ID_PREFIX = "client-view-";	
	private final static String VIEW_CSS_CLASS = "wave-client";
	private final static String ERROR_VIEW_CSS_CLASS = "wave-error";

	private final int currentViewId;
	private final String holderElementId;
	
	private final static String SEND_BTN_ONCLICK_JSFUNC_NAME = "sendButtonOnClick";
	private final static String CMD_BTN_ONCLICK_JSFUNC_NAME = "cmdButtonOnClick";
	 	
	@SuppressWarnings("unchecked")
	private Map<PanelID, IClientPanelRenderer> panelsRenderers = new HashMap<PanelID, IClientPanelRenderer>() {

		private static final long serialVersionUID = 1108763838937694456L;

		public <PanelModelType> IClientPanelRenderer<PanelModelType> get(PanelID panelID) {
			return get(panelID);
		};
	};
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
		
		this.redrawEventsHandler = redrawEventsHandler;		
		
		initPanelsRenderers();
	}
	
	public void initialize(String userAtDomain, String waveServer) {
		wrapperDiv = DocumentHelper.createElement("div");
		wrapperDiv.addAttribute("id", this.holderElementId);	
		wrapperDiv.addAttribute("class", VIEW_CSS_CLASS);
		
		addPanelsElements(wrapperDiv, userAtDomain, waveServer);
	}	
	
	protected void initPanelsRenderers() {	// static?
		panelsRenderers.put(PanelID.INFOLINE_PANEL,   new InfoLineHTMLRenderer(this.currentViewId));
		panelsRenderers.put(PanelID.INBOX_PANEL,      new InboxHTMLRenderer(this.currentViewId));
		panelsRenderers.put(PanelID.USERS_LIST_PANEL, new ParticipantsListHTMLRenderer(this.currentViewId));
		panelsRenderers.put(PanelID.CHAT_PANEL,       new ChatHTMLRenderer(this.currentViewId));
		panelsRenderers.put(PanelID.EDITOR_PANEL,     new EditorHTMLRenderer(this.currentViewId));
		panelsRenderers.put(PanelID.CONSOLE_PANEL,    new ConsoleHTMLRenderer(this.currentViewId));
		panelsRenderers.put(PanelID.ERROR_BOX_PANEL,  new ErrorBoxHTMLRenderer(this.currentViewId));		
	}	
	
	protected void addPanelsElements(Element wrapper, String userAtDomain, String waveServer) {		
		for (PanelID panelID: PanelID.values()) {
			if (panelID != PanelID.INFOLINE_PANEL) { 
				panelsElements.put(panelID, panelsRenderers.get(panelID).createPanel());
			} else {
				InfoLineModel lineModel = PanelModelFactory.createModel(panelID, "connected as " + userAtDomain + "; waveserver: " + waveServer);
				((InfoLineHTMLRenderer)panelsRenderers.get(panelID)).setModel(lineModel);
				panelsElements.put(panelID, panelsRenderers.get(panelID).createPanel());
			}
			wrapper.add(panelsElements.get(panelID));
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
	@SuppressWarnings("unchecked")	
	public <SourceType> void setPanelModel(PanelID panelID, SourceType modelData) {
		panelsRenderers.get(panelID).setModel(
				PanelModelFactory.createModel(panelID, modelData));
		
	}	
	
	@SuppressWarnings("unchecked")
	public void updatePanel(PanelID panelID) {
		IClientPanelRenderer panelRenderer = panelsRenderers.get(panelID);
		redrawEventsHandler.redrawClientPanel(
				panelRenderer.getHolderId(),
				getElementAsString(panelRenderer.createPanel()));
	}
	
	public void updatePanel(PanelID panelID, List<String> withModel) {
		setPanelModel(panelID, withModel);
		updatePanel(panelID);
	}	

	@Override
	public String getPanelElementId(PanelID panelID) {
		return panelsRenderers.get(panelID).getHolderId();
	}

	@Override
	public AWavesClientRedrawEventsHandler getRedrawEventsHandler() {
		return redrawEventsHandler;
	}
	
	public static final String getSendButtonOnClickJS(int clientId, String holderElementId, String commandLineSourceId) {
		return SEND_BTN_ONCLICK_JSFUNC_NAME + "('" + holderElementId + "'," 
					+ String.valueOf(clientId) + ",'" 
					+ commandLineSourceId + "');";	
	}
	
	public static final String getCmdButtonOnClickJS(int clientId, String holderElementId, Command command) {
		return CMD_BTN_ONCLICK_JSFUNC_NAME + "('" + holderElementId + "'," 
					+ String.valueOf(clientId) + ",'" 
					+ Command.toXMLString(command) + "');";		
	}	
	
}
