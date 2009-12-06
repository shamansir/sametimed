package name.shamansir.sametimed.wave.module.mutation;

import name.shamansir.sametimed.wave.module.mutation.proto.AbstractModuleDocumentMutation;
import name.shamansir.sametimed.wave.module.mutation.proto.IMutableModule;
import name.shamansir.sametimed.wave.module.mutation.proto.MutationCompilationException;

import org.apache.commons.lang.StringEscapeUtils;
import org.waveprotocol.wave.model.document.operation.impl.BufferedDocOpImpl.DocOpBuilder;
import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;
import org.waveprotocol.wave.model.wave.ParticipantId;

public class AppendMutation extends AbstractModuleDocumentMutation {
	
	private final ParticipantId author;
	private final String text;
	
	public AppendMutation(ParticipantId author, String text) {
		this.author = author;
		this.text = StringEscapeUtils.unescapeXml(text);
	}

	@Override
	public WaveletDocumentOperation applyTo(IMutableModule module) throws MutationCompilationException {
		int lastID = module.getLastTagPos();		
		DocOpBuilder docOp = alignToTheDocumentEnd(new DocOpBuilder(), module.getSource());
		docOp = (module.makeTag(lastID + 1, author, text)).buildOperation(docOp);		
		return createDocumentOperation(module.getDocumentID(), docOp.finish());		
	}}
