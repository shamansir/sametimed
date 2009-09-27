package name.shamansir.sametimed.wave.model;

public class InfoLine implements IModelValue {
	
	private String infoLine;
	
	public InfoLine(String infoLine) {
		this.infoLine = infoLine;
	}
	
	public InfoLine() {
		this.infoLine = "";
	}
	
	public String getLine() {
		return infoLine;
	}

}
