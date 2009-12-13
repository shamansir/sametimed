package name.shamansir.sametimed.wave.model.base.atom;

/**
 * 
 * @author shamansir <shaman.sir@gmail.com>
 *
 * Text chunk of a document (holds text, author (in future), and style)
 *
 */

public class TextChunk {
	
	private String id;
	private String text;
	private String style = "";
	private String author;
	private boolean reserved;
	private int length = 0;
	
	public TextChunk(String id, String text, String style, String author, boolean reserved) {
		this.id = id;
		this.text = text;
		this.style = style;
		this.author = author;
		this.reserved = reserved;
		this.length = text.length();
	}
	
	public String getID() {
		return id;
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
	
	public String getAuthor() {
		return author;
	}	
	
	public boolean isReserved() {
		return reserved;
	}	
	
	public static TextChunk justWithContent(String content) {
		return new TextChunk("", content, "", "-", false);
	}

}
