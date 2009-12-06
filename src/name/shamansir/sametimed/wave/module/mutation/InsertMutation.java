package name.shamansir.sametimed.wave.module.mutation;

import name.shamansir.sametimed.wave.module.mutation.proto.AbstractModuleDocumentMutation;
import name.shamansir.sametimed.wave.module.mutation.proto.IMutableModule;
import name.shamansir.sametimed.wave.module.mutation.proto.MutationCompilationException;

import org.apache.commons.lang.StringEscapeUtils;
import org.waveprotocol.wave.model.document.operation.impl.BufferedDocOpImpl.DocOpBuilder;
import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;
import org.waveprotocol.wave.model.wave.ParticipantId;

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
		// FIXME: insert, not append
		// Integer insertBefore = module.getTagBeforePos(pos);
		Integer lastID = module.getLastTagPos();
			// module.applyCursor(sourceDoc, new DocumentLastChunkIDCursor());
		DocOpBuilder docOp = alignToTheDocumentEnd(new DocOpBuilder(), module.getSource());
		// pay attention to number of tag!
		docOp = (module.makeTag(lastID + 1, author, text)).buildOperation(docOp);		
		return createDocumentOperation(module.getDocumentID(), docOp.finish());		
	}

}
