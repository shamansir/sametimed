package name.shamansir.sametimed.wave.chat.cursor;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import name.shamansir.sametimed.wave.chat.Chat;
import name.shamansir.sametimed.wave.model.base.atom.ChatLine;

import org.waveprotocol.wave.examples.fedone.waveclient.console.ConsoleUtils;
import org.waveprotocol.wave.examples.fedone.waveclient.console.ScrollableWaveView.RenderMode;
import org.waveprotocol.wave.model.document.operation.AnnotationBoundaryMap;
import org.waveprotocol.wave.model.document.operation.Attributes;
import org.waveprotocol.wave.model.document.operation.DocInitializationCursor;

public class LinesExtractionCursor implements DocInitializationCursor {
	
	private final List<ChatLine> chatLines;	
	
    private final StringBuffer currentLine;
    private final StringBuffer currentAuthor;
    
    private final StringBuffer xmlLine;
    
    private final Deque<String> elemStack;
	
    private final RenderMode outputMode;
	
	public LinesExtractionCursor(RenderMode outputMode) {
		this.chatLines = new ArrayList<ChatLine>();
		
		this.currentLine = new StringBuffer();
		this.currentAuthor = new StringBuffer();
		this.xmlLine = new StringBuffer();
		
		this.elemStack = new LinkedList<String>();
		
		this.outputMode = outputMode;
	}
	
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
			if (type.equals(Chat.LINE_TAG_NAME)) {
				if (!attrs
						.containsKey(Chat.AUTHOR_ATTR_NAME)) {
					throw new IllegalArgumentException(
							"Line element must have author");
				}							
				
				if (currentAuthor.length() > 0) {
					currentAuthor.delete(0, currentAuthor.length());
				}
				currentAuthor.append(attrs.get(Chat.AUTHOR_ATTR_NAME));

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
	
	public List<ChatLine> getExtractedLines() {
		return chatLines;
	}

}
