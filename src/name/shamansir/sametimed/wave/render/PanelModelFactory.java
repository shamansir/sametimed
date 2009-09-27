package name.shamansir.sametimed.wave.render;

import java.util.List;

import name.shamansir.sametimed.wave.render.proto.APanelModel;

public class PanelModelFactory {
	
	@SuppressWarnings("unchecked")
	public static <PanelModelType extends APanelModel, SourceType> PanelModelType createModel(PanelID panelID, SourceType model) {
		switch (panelID) {
			case CHAT_PANEL:       return (PanelModelType) new ChatPanelModel((List<String>)model);
			case CONSOLE_PANEL:    return (PanelModelType) new ConsolePanelModel((List<String>) model);
			case EDITOR_PANEL:     return (PanelModelType) new EditorPanelModel((List<String>) model);
			case ERROR_BOX_PANEL:  return (PanelModelType) new ErrorBoxModel((List<String>) model);
			case INBOX_PANEL:      return (PanelModelType) new InboxModel((List<String>) model);
			case INFOLINE_PANEL:   return (PanelModelType) new InfoLineModel((String)model);
			case USERS_LIST_PANEL: return (PanelModelType) new ParticipantsModel((List<String>) model);
			default: return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <PanelModelType extends APanelModel> PanelModelType createModel(PanelID panelID) {
		return createModel(panelID, null);
	}
	
}
