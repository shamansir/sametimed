package name.shamansir.sametimed.wave.module;

import java.text.ParseException;

import org.waveprotocol.wave.model.wave.ParticipantId;

import name.shamansir.sametimed.wave.doc.AbstractDocumentTag;

public abstract class AbstractVerticalModule<InnerType> extends AbstractMutableModule<InnerType> {

	public AbstractVerticalModule(String moduleID, String documentID) throws ParseException {
		super(moduleID, documentID, false, false);
	}
	
	@Override
	@Deprecated
	public AbstractDocumentTag makeTag(Integer id, ParticipantId author, String text) {
		return makeTag(author, text);
	}
	
	public abstract AbstractDocumentTag makeTag(ParticipantId author, String text);
	
}
