package name.shamansir.sametimed.wave.model;

import java.util.List;
import java.util.Map;

import org.waveprotocol.wave.model.wave.ParticipantId;

import name.shamansir.sametimed.wave.model.base.atom.ChatLine;
import name.shamansir.sametimed.wave.model.base.atom.InboxElement;
import name.shamansir.sametimed.wave.model.base.atom.TextChunk;

/**
 * @author shamansir <shaman.sir@gmail.com>
 * 
 * Creates AModel instances from source of any type (must match to
 * the model SourceType)  
 * 
 * @see AModel
 *
 */
public class ModelFactory {
	
	/**
	 * Create model using passed source and type
	 * 
	 * @param <SourceType> type of the source
	 * @param modelID model type ID
	 * @param source the source itself
	 * @return model of the type, corresponding to the ID that was passed
	 */
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
	
	/**
	 * Create empty model using passed model type
	 * 
	 * @param modelID model type ID
	 * @return model of the type, corresponding to the ID that was passed
	 */	
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
