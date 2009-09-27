package name.shamansir.sametimed.wave.render;

import java.util.List;

// TODO: make a possibility to create model in the way like 
//       InfoLineModel.create(<T>), where T is a type of model value,
//		 also in PanelModelFactory

public class PanelModelFactory {
	
	public static final APanelModel createModel(PanelID forPanel, List<String> modelData) {
		switch (forPanel) {
			case CHAT_PANEL:       return new ChatModel(modelData);
			case CONSOLE_PANEL:    return new ConsoleModel(modelData);
			case EDITOR_PANEL:     return new EditorModel(modelData);
			case ERROR_BOX_PANEL:  return new ErrorBoxModel(modelData);
			case INBOX_PANEL:      return new InboxModel(modelData);
			case INFOLINE_PANEL:   return new InfoLineModel(modelData);
			case USERS_LIST_PANEL: return new ParticipantsModel(modelData);
			default: return null;
		}
	}
	
	public static final APanelModel createModel(PanelID forPanel) {
		return createModel(forPanel, null);
	}

}
