package name.shamansir.sametimed.wave.model.base.atom;

public class TextChunk {
	
	private String text;
	private String style = "none";
	
	public TextChunk(String text, String style) {
		this.text = text;
		this.style = style;
	}
	
	public String getText() {
		return text;
	}
	
	public String getStyle() {
		return style;
	}

}
