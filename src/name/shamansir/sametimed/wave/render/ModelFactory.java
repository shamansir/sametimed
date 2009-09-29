package name.shamansir.sametimed.wave.model;

import java.util.List;

public class ModelFactory {

	@SuppressWarnings("unchecked")
	public static <SourceType> AModel createModel(ModelID modelType, SourceType source) {
		switch (modelType) {
			case INFOLINE_MODEL:   return new InfoLineModel((String)source);
			case INBOX_MODEL:      return new InboxModel((List<String>)source);
			case USERS_LIST_MODEL: return new ParticipantsModel((List<String>)source);
			case CHAT_MODEL:       return new ChatModel((List<String>)source);
			case EDITOR_MODEL:     return new EditorModel((List<String>)source);
			case CONSOLE_MODEL:    return new ConsoleModel((List<String>)source);
			case ERRORBOX_MODEL:   return new ErrorBoxModel((List<String>)source);
			default: return null;
		}
	}	
	
	@SuppressWarnings("unchecked")
	public static AModel createModel(ModelID modelType) {
		switch (modelType) {
			case INFOLINE_MODEL:   return new InfoLineModel();
			case INBOX_MODEL:      return new InboxModel();
			case USERS_LIST_MODEL: return new ParticipantsModel();
			case CHAT_MODEL:       return new ChatModel();
			case EDITOR_MODEL:     return new EditorModel();
			case CONSOLE_MODEL:    return new ConsoleModel();
			case ERRORBOX_MODEL:   return new ErrorBoxModel();
			default: return null;
		}
	}	
	
}
