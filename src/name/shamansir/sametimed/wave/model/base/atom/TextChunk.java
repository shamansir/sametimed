package name.shamansir.sametimed.wave.model.base.atom;

/**
 * 
 * @author shamansir <shaman.sir@gmail.com>
 *
 * Text chunk of a document (holds text, author (in future), and style)
 *
 */

public class TextChunk {
	
	private String text;
	private String style = "none";
	private int length = 0;
	
	public TextChunk(String text, String style) {
		this.text = text;
		this.style = style;
		this.length = text.length();
	}
	
	public String getText() {
		return text;
	}
	
	public String getStyle() {
		return style;
	}
	
	public int getLength() {
		return length;
	}

}
