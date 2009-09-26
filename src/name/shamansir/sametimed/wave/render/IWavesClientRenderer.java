package name.shamansir.sametimed.wave.render;

import name.shamansir.sametimed.client.proto.AWavesClientRedrawEventsHandler;

public interface IWavesClientRenderer {
	
	public void initialize(String userAtDomain, String waveServer);	
	
	public String getHolderElementId();
	public String getPanelElementId(PanelID panelID);	
	public int getViewId();	

	public String getCompleteView();
	public String getErrorView(String errorString);	
	public String getPanelContent(PanelID panelID);
	public String getPanelContent(PanelID panelID, PanelModel model);
	public void updatePanel(PanelID panelID, PanelModel model);

	public AWavesClientRedrawEventsHandler getRedrawEventsHandler();	
	
}
