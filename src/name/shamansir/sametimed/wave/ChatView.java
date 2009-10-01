package name.shamansir.sametimed.wave;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.waveprotocol.wave.examples.fedone.waveclient.common.ClientUtils;
import org.waveprotocol.wave.examples.fedone.waveclient.common.ClientWaveView;
import org.waveprotocol.wave.examples.fedone.waveclient.console.ConsoleUtils;
import org.waveprotocol.wave.examples.fedone.waveclient.console.ScrollableWaveView.RenderMode;
import org.waveprotocol.wave.model.document.operation.AnnotationBoundaryMap;
import org.waveprotocol.wave.model.document.operation.Attributes;
import org.waveprotocol.wave.model.document.operation.BufferedDocOp;
import org.waveprotocol.wave.model.document.operation.DocInitializationCursor;
import org.waveprotocol.wave.model.document.operation.impl.InitializationCursorAdapter;

import name.shamansir.sametimed.wave.model.base.atom.ChatLine;

// based on ScrollableWaveView
public class ChatView {
	
	private Logger LOG = Logger.getLogger(ChatView.class.getName()); 
	
	private ClientWaveView sourceWave;
	private RenderMode outputMode = RenderMode.NORMAL;
	
	public ChatView(ClientWaveView sourceWave) {
		this.sourceWave = sourceWave;
	}
	
	public List<ChatLine> getChatLines() {		
		final List<ChatLine> chatLines = new ArrayList<ChatLine>();
				
	    final StringBuffer currentLine = new StringBuffer();
	    final StringBuffer currentAuthor = new StringBuffer();
	    
	    final StringBuffer xmlLine = new StringBuffer();
	    
	    final Deque<String> elemStack = new LinkedList<String>();
	    
		for (BufferedDocOp document : ClientUtils.getConversationRoot(
				sourceWave).getDocuments().values()) { 
			
			document.apply(new InitializationCursorAdapter(
					
			new DocInitializationCursor() {

				@Override
				public void characters(String s) {
					xmlLine.append(s);
					if (currentLine.length() > 0) {
						currentLine.delete(0, currentLine.length());
					}
					currentLine.append(ConsoleUtils.renderNice(s));
				}

				@Override
				public void elementStart(String type, Attributes attrs) {
					elemStack.push(type);

					if (outputMode.equals(RenderMode.NORMAL)) {
						if (type.equals(ConsoleUtils.LINE)) {
							if (!attrs
									.containsKey(ConsoleUtils.LINE_AUTHOR)) {
								throw new IllegalArgumentException(
										"Line element must have author");
							}							
							
							if (currentAuthor.length() > 0) {
								currentAuthor.delete(0, currentAuthor.length());
							}
							currentAuthor.append(attrs.get(ConsoleUtils.LINE_AUTHOR));

						} else {
							throw new IllegalArgumentException(
									"Unsupported element type " + type);
						}
					} else if (outputMode.equals(RenderMode.XML)) {
						if (xmlLine.length() > 0) {
							xmlLine.delete(0, xmlLine.length());
						}
						if (attrs.isEmpty()) {
							xmlLine.append("<" + type + ">");
						} else {
							xmlLine.append("<" + type + ">");
							for (String key : attrs.keySet()) {
								xmlLine.append(key + "=\""
										+ attrs.get(key) + "\"");
							}
							xmlLine.append(">");
						}
					}
				}

				@Override
				public void elementEnd() {
					String type = elemStack.pop();

					if (outputMode.equals(RenderMode.XML)) {
						xmlLine.append("</" + type + ">");
						chatLines.add(new ChatLine(currentAuthor.toString(), 
								xmlLine.toString()));
					} else {
						chatLines.add(new ChatLine(currentAuthor.toString(), 
								currentLine.toString()));
					}						
				}

				@Override
				public void annotationBoundary(AnnotationBoundaryMap map) {
				}
			}
			
			));
		}

		return chatLines;
	}

	public void setOutputMode(RenderMode outputMode) {
		this.outputMode  = outputMode;		
	}

}
