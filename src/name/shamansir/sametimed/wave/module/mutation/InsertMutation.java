package name.shamansir.sametimed.wave.module.mutation;

import name.shamansir.sametimed.wave.doc.AbstractDocumentTag;
import name.shamansir.sametimed.wave.doc.sequencing.DocumentProcessingException;
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

	@Override
	public WaveletDocumentOperation applyTo(AbstractModuleWithDocument<?> module)
			throws MutationCompilationException, DocumentProcessingException {
		module.startOperations();		
		int foundPos = module.scrollToPos(module.findTagStart(insPos));
		int cutPos = insPos - foundPos;
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
