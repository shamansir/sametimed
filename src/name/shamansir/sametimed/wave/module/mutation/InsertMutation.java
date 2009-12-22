package name.shamansir.sametimed.wave.module.mutation;

import name.shamansir.sametimed.wave.doc.AbstractDocumentTag;
import name.shamansir.sametimed.wave.doc.DocumentProcessingException;
import name.shamansir.sametimed.wave.module.AbstractModuleWithDocument;
import name.shamansir.sametimed.wave.module.mutation.proto.IModuleDocumentMutation;
import name.shamansir.sametimed.wave.module.mutation.proto.MutationCompilationException;

import org.apache.commons.lang.StringEscapeUtils;
import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;
import org.waveprotocol.wave.model.wave.ParticipantId;

// TODO: some modules may not want to accept/implement insert mutations,
//       create a mechanism to ask module if it accepts it

public class InsertMutation implements IModuleDocumentMutation {
	
	private final ParticipantId author;
	private final String text;
	private final Integer insPos;

	public InsertMutation(ParticipantId author, String text,
			Integer pos) {
		this.author = author;
		this.text = StringEscapeUtils.unescapeXml(text);
		this.insPos = pos;
	}

	/*
	@Override
	public WaveletDocumentOperation applyTo(IMutableModule module) throws MutationCompilationException {
		DocOpBuilder docOp = null;
		Integer tagFoundAt = -1;
		AbstractDocumentTag removedTag = null;
		AbstractDocumentTag newBeforeTag = null;
		TagID tagToRemove = module.getTagAtPosition(pos);
		boolean needToRemove = (tagToRemove != null) && (tagToRemove.getValue() != null); 
		if (needToRemove) {
			tagFoundAt = module.findTagStartPosition(tagToRemove);
			if (tagFoundAt > -1) {
				removedTag = module.deleteTagAndGet(tagToRemove);
				newBeforeTag = module.makeTag(module.nextTagID(), 
								removedTag.getAuthor(), removedTag.getContent().substring(0, pos - tagFoundAt));
			}
		}
		AbstractDocumentTag insertingTag = 
								module.makeTag(module.nextTagID(), author, text);
		if (needToRemove) {
			AbstractDocumentTag newAfterTag = module.makeTag(module.nextTagID(), 
								removedTag.getAuthor(), removedTag.getContent().substring(pos - tagFoundAt));
			docOp = newAfterTag.build(
									 insertingTag.build(
									 newBeforeTag.build(new DocOpBuilder())));
		} else {
			docOp =  insertingTag.build(new DocOpBuilder());
		}
		return createDocumentOperation(module.getDocumentID(), docOp.build());
	} */

	@Override
	public WaveletDocumentOperation applyTo(AbstractModuleWithDocument<?> module)
			throws MutationCompilationException, DocumentProcessingException {
		// TODO Auto-generated method stub
		module.startOperations();		
		int foundPos = module.scrollToPos(module.findTagStart(insPos));
		int cutPos = insPos - foundPos; // FIXME may differ with actual value because of elementStart/elementEnd
		if (foundPos < insPos) {
			AbstractDocumentTag removedTag = module.deleteTagAndGet(foundPos);
			module.addTag(module.makeTag(author, removedTag.getContent().substring(foundPos, cutPos)));
			module.addTag(module.makeTag(author, text));
			module.addTag(module.makeTag(author, removedTag.getContent().substring(cutPos)));
		} else {
			module.addTag(module.makeTag(author, text));
		}		
		return module.finishOperations();
	}

}
