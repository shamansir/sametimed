package name.shamansir.sametimed.wave.module.mutation;

import name.shamansir.sametimed.wave.module.mutation.proto.AbstractModuleDocumentMutation;
import name.shamansir.sametimed.wave.module.mutation.proto.IMutableModule;
import name.shamansir.sametimed.wave.module.mutation.proto.MutationCompilationException;

import org.apache.commons.lang.StringEscapeUtils;
import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
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
	public WaveletDocumentOperation applyTo(IMutableModule module,
			BufferedDocOp sourceDoc) throws MutationCompilationException {
		Integer lastID = null;
		if (module.enumerateTags()) lastID = module.getLastTagPos(sourceDoc);
			// module.applyCursor(sourceDoc, new DocumentLastChunkIDCursor());
		DocOpBuilder docOp = alignToTheDocumentEnd(new DocOpBuilder(), sourceDoc);
		docOp = (module.makeTag(module.enumerateTags() ? lastID + 1 : null,
					author, text)).buildOperation(docOp);		
		return createDocumentOperation(module.getDocumentID(), docOp.finish());		
	}}
