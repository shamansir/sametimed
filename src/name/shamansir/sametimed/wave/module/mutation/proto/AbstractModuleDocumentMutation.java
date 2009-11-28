package name.shamansir.sametimed.wave.module.mutation.proto;

import org.waveprotocol.wave.examples.fedone.waveclient.common.ClientUtils;
import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.document.operation.impl.BufferedDocOpImpl.DocOpBuilder;
import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;

public abstract class AbstractModuleDocumentMutation implements IModuleMutation {
	
	protected static WaveletDocumentOperation createDocumentOperation(String documentID, BufferedDocOp operation) {
		return new WaveletDocumentOperation(documentID, operation);
	}
	
	protected static DocOpBuilder alignToTheDocumentEnd(DocOpBuilder docOp, BufferedDocOp srcDoc) {
		int docSize = (srcDoc == null) ? 0 : ClientUtils.findDocumentSize(srcDoc);		
		if (docSize > 0) {
			docOp.retain(docSize);
		}
		return docOp;
	}	
	
	
}
