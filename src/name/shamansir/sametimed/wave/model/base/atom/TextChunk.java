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
	private String style = "";
	private String authors;
	private boolean reserved;
	private int length = 0;
	
	public TextChunk(String text, String style, String authors, boolean reserved) {
		this.text = text;
		this.style = style;
		this.authors = authors;
		this.reserved = reserved;
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
	
	public String getAuthors() {
		return authors;
	}	
	
	public boolean isReserved() {
		return reserved;
	}	

}
