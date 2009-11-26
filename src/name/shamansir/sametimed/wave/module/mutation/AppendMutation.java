package name.shamansir.sametimed.wave.module.mutation;

import name.shamansir.sametimed.wave.module.IMutableModule;
import name.shamansir.sametimed.wave.module.mutation.proto.IMutation;
import name.shamansir.sametimed.wave.module.mutation.proto.MutationCompilationException;

import org.apache.commons.lang.StringEscapeUtils;
import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;
import org.waveprotocol.wave.model.wave.ParticipantId;

public class AppendMutation implements IMutation {
	
	private final ParticipantId author;
	private final String text;
	
	public AppendMutation(ParticipantId author, String text) {
		this.author = author;
		this.text = StringEscapeUtils.unescapeXml(text);
	}

	@Override
	public WaveletDocumentOperation compile(BufferedDocOp sourceDoc,
			IMutableModule targetModule) throws MutationCompilationException {
		return targetModule.getAppendOp(sourceDoc, author, text);
	}

}
