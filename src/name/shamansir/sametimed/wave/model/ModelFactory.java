package name.shamansir.sametimed.wave.model;

import java.util.List;
import java.util.Map;

import org.waveprotocol.wave.model.wave.ParticipantId;

import name.shamansir.sametimed.wave.model.base.atom.ChatLine;
import name.shamansir.sametimed.wave.model.base.atom.InboxElement;
import name.shamansir.sametimed.wave.model.base.atom.TextChunk;

public class ModelFactory {
	
	@SuppressWarnings("unchecked")
	public static <SourceType> AModel<?, ?> createModel(ModelID modelID, SourceType source) {
		switch (modelID) {
			// FIXME: source casting must not be hardcoded
			case INFOLINE_MODEL:  return new InfoLineModel((String)source);
			case INBOX_MODEL:     return new InboxModel((Map<Integer, InboxElement>)source);
			case USERSLIST_MODEL: return new ParticipantsModel((List<ParticipantId>)source);
			case CHAT_MODEL:      return new ChatModel((List<ChatLine>)source);
			case EDITOR_MODEL:    return new DocumentModel((List<TextChunk>)source);
			case CONSOLE_MODEL:   return new ConsoleModel((List<String>)source);
			case ERRORBOX_MODEL:  return new ErrorsModel((List<String>)source);
			default: return null;
		}
	}
	
	public static AModel<?, ?> createModel(ModelID modelID) {
		switch (modelID) {
			case INFOLINE_MODEL:  return new InfoLineModel();
			case INBOX_MODEL:     return new InboxModel();
			case USERSLIST_MODEL: return new ParticipantsModel();
			case CHAT_MODEL:      return new ChatModel();
			case EDITOR_MODEL:    return new DocumentModel();
			case CONSOLE_MODEL:   return new ConsoleModel();
			case ERRORBOX_MODEL:  return new ErrorsModel();
			default: return null;
		}
	}	

}
