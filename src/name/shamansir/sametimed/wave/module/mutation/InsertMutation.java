package name.shamansir.sametimed.wave.module.mutation;

import name.shamansir.sametimed.wave.doc.AbstractDocumentTag;
import name.shamansir.sametimed.wave.doc.TagID;
import name.shamansir.sametimed.wave.module.mutation.proto.AbstractModuleDocumentMutation;
import name.shamansir.sametimed.wave.module.mutation.proto.IMutableModule;
import name.shamansir.sametimed.wave.module.mutation.proto.MutationCompilationException;

import org.apache.commons.lang.StringEscapeUtils;
import org.waveprotocol.wave.model.document.operation.impl.BufferedDocOpImpl.DocOpBuilder;
import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;
import org.waveprotocol.wave.model.wave.ParticipantId;

// TODO: some modules may not want to accept/implement insert mutations,
//       create a mechanism to ask module if it accepts it

public class InsertMutation extends AbstractModuleDocumentMutation {
	
	private final ParticipantId author;
	private final String text;
	private final Integer pos;

	public InsertMutation(ParticipantId author, String text,
			Integer pos) {
		this.author = author;
		this.text = StringEscapeUtils.unescapeXml(text);
		this.pos = pos;
	}

	@Override
	public WaveletDocumentOperation applyTo(IMutableModule module) throws MutationCompilationException {
		// FIXME: this three operations are too complex - they can be merged in
		//        one DocOp, need to find the way to optimize. all these
		//        mutations are performing cursors several times over document
		//		  when it can be done in only one sequence of steps
		//		  may be use chaining or something - let the document control
		//		  the last DocOp applied
		// TODO: determine if we can just insert (between tags), not always delete
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
		return createDocumentOperation(module.getDocumentID(), docOp.finish());
	}

}
