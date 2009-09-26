package name.shamansir.sametimed.wave.render;

import org.dom4j.Element;

public interface IClientPanelRenderer {
	
	public String getHolderId();

	public Element createPanel();
	public Element createPanel(PanelModel model);
	
}
