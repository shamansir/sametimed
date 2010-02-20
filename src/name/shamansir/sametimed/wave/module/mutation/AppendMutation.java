package name.shamansir.sametimed.wave.module.mutation;

import name.shamansir.sametimed.wave.doc.sequencing.DocumentProcessingException;
import name.shamansir.sametimed.wave.module.AbstractModuleWithDocument;
import name.shamansir.sametimed.wave.module.mutation.proto.IModuleDocumentMutation;
import name.shamansir.sametimed.wave.module.mutation.proto.MutationCompilationException;

import org.apache.commons.lang.StringEscapeUtils;

import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;
import org.waveprotocol.wave.model.wave.ParticipantId;

public class AppendMutation implements IModuleDocumentMutation {
	
    // private static final Log LOG = LogFactory.getLog(AppendMutation.class);
	
	private final ParticipantId author;
	private final String text;
	
	public AppendMutation(ParticipantId author, String text) {
		this.author = author;
		this.text = StringEscapeUtils.unescapeXml(text);
	}

	@Override
	public WaveletDocumentOperation applyTo(AbstractModuleWithDocument<?> module)
			throws MutationCompilationException, DocumentProcessingException {
		module.startOperations();
		module.alignDocToEnd();
		module.addTag(module.makeTag(author, text));
		return module.finishOperations();
		/*
		DocOpBuilder docOp = alignToTheDocumentEnd(new DocOpBuilder(), module.getSource());
		docOp = (module.makeTag(module.nextTagID(), author, text)).build(docOp);
		//LOG.info("created tag for module " + module.getModuleID() + ", document " + module.getDocumentID() + ", id: " + module.nextTagID());
		return createDocumentOperation(module.getDocumentID(), docOp.build()); */				
	}}
