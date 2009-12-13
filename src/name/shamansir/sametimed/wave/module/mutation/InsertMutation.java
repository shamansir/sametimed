package name.shamansir.sametimed.wave.module.mutation;

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
		// FIXME: insert, not append. if inserting in tree - use tagID.makeForFirstChild
		// TagID tagID = module.getTagBeforePos(pos);
		DocOpBuilder docOp = alignToTheDocumentEnd(new DocOpBuilder(), module.getSource());
		// pay attention to number of tag!, be sure to split tags before inserting
		docOp = (module.makeTag(module.nextTagID(/*tagID*/), author, text)).buildOperation(docOp);		
		return createDocumentOperation(module.getDocumentID(), docOp.finish());		
	}

}
