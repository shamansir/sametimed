package name.shamansir.sametimed.wave.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.waveprotocol.wave.examples.fedone.waveclient.common.ClientUtils;
import org.waveprotocol.wave.examples.fedone.waveclient.common.ClientWaveView;
import org.waveprotocol.wave.examples.fedone.waveclient.console.ConsoleUtils;
import org.waveprotocol.wave.examples.fedone.waveclient.console.ScrollableWaveView.RenderMode;
import org.waveprotocol.wave.model.document.operation.AnnotationBoundaryMap;
import org.waveprotocol.wave.model.document.operation.Attributes;
import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.document.operation.DocInitializationCursor;
import org.waveprotocol.wave.model.document.operation.impl.AttributesImpl;
import org.waveprotocol.wave.model.document.operation.impl.InitializationCursorAdapter;
import org.waveprotocol.wave.model.document.operation.impl.BufferedDocOpImpl.DocOpBuilder;
import org.waveprotocol.wave.model.operation.wave.WaveletDocumentOperation;
import org.waveprotocol.wave.model.wave.ParticipantId;

import com.google.common.collect.ImmutableMap;

import name.shamansir.sametimed.wave.ADocumentsWavelet;
import name.shamansir.sametimed.wave.model.ModelID;
import name.shamansir.sametimed.wave.render.proto.IWavesClientRenderer;

/**
 * @see #addUpdatesListener(IUpdatesListener)
 * 
 * @see IWavesClientRenderer
 * @see IUpdatesListener
 * @see WavesClientInfoProvider
 * 
 * @see WaveletModel
 * @see ClientWaveView 
 * @see InboxWaves
 * @see Chat 
 * 
 */


public class WaveletWithChat extends ADocumentsWavelet {
	
	private static final String CHAT_DOCUMENT_ID = "main";		
	
	/* models */
	private Chat chatView = null;	

	public WaveletWithChat(int clientID, IWavesClientRenderer renderer) {
		super(clientID, renderer);		
	}
	
	public WaveletWithChat(int clientID) {
		super(clientID);
	}
	
	@Override
	protected void clear() {
		super.clear();
		chatView = null;
	}	
	
	@Override	
	protected void prepareDocuments(ClientWaveView currentWave) {
		chatView = new Chat(currentWave);		
	}
	
	@Override	
	protected void updateDocumentsModels() {
		if (isChatReady()) {
			updateModel(ModelID.CHAT_MODEL, chatView.getChatLines());
		}		
	}	
	
	@Override
	protected List<ModelID> getDocumentsModelsTypes() {
		List<ModelID> modelsTypes = new ArrayList<ModelID>();
		modelsTypes.add(ModelID.CHAT_MODEL);
		return modelsTypes;
	}	
	
	@Override	
	protected void resetDocuments() {
		chatView = null;
	}
	
	@Override
	protected void changeDocumentsOutputMode(RenderMode mode) { 
		chatView.setOutputMode(mode);
	}	
	
	private boolean isChatReady() {
		return chatView != null;
	}	
	
	private BufferedDocOp getChatDocument() {
		return getDocument(CHAT_DOCUMENT_ID);
	}
	
	@Override
	protected boolean documentPerformUndo(String userId) {
		if (getChatDocument() == null) {
			registerError("Error: document is empty");
			return false;
		}
		
		final String userId_ = userId;

		// Find the last line written by the participant given by userId (by
		// counting the number of
		// <line></line> elements, and comparing to their authors).
		final AtomicInteger totalLines = new AtomicInteger(0);
		final AtomicInteger lastLine = new AtomicInteger(-1);

		// FIXME: it works for console, implement it for HTMLView (using models)
		getChatDocument().apply(
				new InitializationCursorAdapter(new DocInitializationCursor() {
					@Override
					public void elementStart(String type, Attributes attrs) {
						if (type.equals(ConsoleUtils.LINE)) {
							totalLines.incrementAndGet();

							if (userId_.equals(attrs
									.get(ConsoleUtils.LINE_AUTHOR))) {
								lastLine.set(totalLines.get() - 1);
							}
						}
					}

					@Override
					public void characters(String s) {
					}

					@Override
					public void annotationBoundary(AnnotationBoundaryMap map) {
					}

					@Override
					public void elementEnd() {
					}
				}));

		// Delete the line
		if (lastLine.get() >= 0) {
			WaveletDocumentOperation undoOp = new WaveletDocumentOperation(
					CHAT_DOCUMENT_ID, ConsoleUtils.createLineDeletion(
							getChatDocument(), lastLine.get()));
			getOperationsSender().sendWaveletOperation(getOpenWavelet().getWaveletName(),
					undoOp);
			return true;
		} else {
			registerError("Error: " + userId + " hasn't written anything yet");
			return false;
		}
	}
	
	@Override
	protected void documentPerformAppend(String text, ParticipantId author) {
		BufferedDocOp openDoc = getChatDocument();
		int docSize = (openDoc == null) ? 0 : ClientUtils
				.findDocumentSize(openDoc);
		DocOpBuilder docOp = new DocOpBuilder();

		if (docSize > 0) {
			docOp.retain(docSize);
		}

		docOp.elementStart(ConsoleUtils.LINE, new AttributesImpl(
				ImmutableMap.of(ConsoleUtils.LINE_AUTHOR, author.getAddress())));
		docOp.characters(text);
		docOp.elementEnd();
		
		// FIXME:
		// ATTENTION: this differs from console, first characters, then - elementEnd
		//            console does end before characters 

		getOperationsSender().sendWaveletOperation(getOpenWavelet().getWaveletName(),
				new WaveletDocumentOperation(CHAT_DOCUMENT_ID, docOp
						.finish()));
	}

}
