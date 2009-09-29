package name.shamansir.sametimed.wave.render;

import org.dom4j.Element;

public interface IClientPanelRenderer<PanelModelType> {
	
	public String getHolderId();

	public Element createPanel();
	
	public PanelModelType getModel();
	public void setModel(PanelModelType model);	
	
}
