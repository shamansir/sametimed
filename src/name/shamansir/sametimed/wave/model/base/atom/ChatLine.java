package name.shamansir.sametimed.wave.model.base.atom;

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
