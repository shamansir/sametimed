package name.shamansir.sametimed.wave.render.html;

import org.dom4j.Element;

import name.shamansir.sametimed.wave.WavesClient;
import name.shamansir.sametimed.wave.render.ConsoleModel;
import name.shamansir.sametimed.wave.render.PanelID;
import name.shamansir.sametimed.wave.render.APanelModel;

public class ConsoleHTMLRenderer extends AHTMLPanelRenderer {
	
	public static final String PANEL_ID_PREFIX = "client-console-";	
	public static final String PANEL_CLASS     = "console";
	
	public ConsoleHTMLRenderer(int clientID, ConsoleModel model) {
		super(clientID, PanelID.CONSOLE_PANEL, model, PANEL_ID_PREFIX);
		setWrapperTagName("form");
		setWrapperClass(PANEL_CLASS);		
	}
	
	public ConsoleHTMLRenderer(int clientID) {
		this(clientID, null);
	}
	
	@Override
	protected void configureWrapper(Element wrapper) {
		wrapper.addAttribute("action", "");
	}	

	@Override
	protected void addElements(Element wrapper) {
		Element inputText = createElement("input");
		inputText.addAttribute("type", "text");
		inputText.addAttribute("class", "gwt-TextBox");		
		wrapper.add(inputText);
		
		Element executeButton = createElement("input");
		executeButton.addAttribute("type", "button");
		executeButton.addAttribute("title", "Send");
		executeButton.addAttribute("value", "Send");
		executeButton.addAttribute("class", "gwt-Button");
		executeButton.addAttribute("onclick", 
				WavesClient.generateCmdExecutionJavascript(
						getCurrentClientId(), getHolderId(), null));
		// executeButton.setText("Send");
		wrapper.add(executeButton);		
	}

	@Override
	protected void addElements(Element wrapper, APanelModel model) {
		// TODO Auto-generated method stub
		addElements(wrapper);
	}


}
