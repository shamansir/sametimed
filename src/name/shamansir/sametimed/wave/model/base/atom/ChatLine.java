package name.shamansir.sametimed.wave.model.base.atom;

/**
 * 
 * @author shamansir <shaman.sir@gmail.com>
 *
 * Any line of the chat (with author and text)
 *
 */

public class ChatLine {
	
	private String author;
	private String text;
	//private int timestamp;
	
	public ChatLine(String author, String text) {
		this.author = author;
		this.text = text;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public String getText() {
		return text;
	}	

}
