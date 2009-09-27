package name.shamansir.sametimed.wave.render;

import org.dom4j.Element;

public interface IClientPanelRenderer {
	
	public String getHolderId();

	public Element createPanel();
	
	public APanelModel getModel();
	public void setModel(APanelModel model);	
	
}
