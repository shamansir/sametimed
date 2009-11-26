package name.shamansir.sametimed.wave.module.mutation;

import name.shamansir.sametimed.wave.module.IMutableModule;
import name.shamansir.sametimed.wave.module.mutation.proto.IMutation;
import name.shamansir.sametimed.wave.module.mutation.proto.MutationCompilationException;

import org.apache.commons.lang.StringEscapeUtils;
import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;
import org.waveprotocol.wave.model.wave.ParticipantId;

public class InsertMutation implements IMutation {
	
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
	public WaveletDocumentOperation compile(BufferedDocOp sourceDoc,
			IMutableModule targetModule) throws MutationCompilationException {
		// FIXME: insert, not append
		return targetModule.getAppendOp(sourceDoc, author, text);
	}

}
