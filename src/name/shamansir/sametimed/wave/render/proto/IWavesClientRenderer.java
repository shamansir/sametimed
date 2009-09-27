package name.shamansir.sametimed.wave.render.proto;

import java.util.List;

import name.shamansir.sametimed.client.proto.AWavesClientRedrawEventsHandler;
import name.shamansir.sametimed.wave.render.PanelID;

public interface IWavesClientRenderer {
	
	public void initialize(String userAtDomain, String waveServer);	
	
	public String getHolderElementId();
	public String getPanelElementId(PanelID panelID);	
	public int getViewId();	

	public String getCompleteView();
	public String getErrorView(String errorString);	
	public String getPanelContent(PanelID panelID);
	public void updatePanel(PanelID panelID, List<String> withModel); 
	// FIXME: panels must to update their models on events and just rerender 
	//        when updatePanel is called
	public void updatePanel(PanelID panelID); 
	
	public void setPanelModel(PanelID panelID, List<String> modelData);

	public AWavesClientRedrawEventsHandler getRedrawEventsHandler();	
	
}
