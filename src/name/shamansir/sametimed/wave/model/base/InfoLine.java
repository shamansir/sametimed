package name.shamansir.sametimed.wave.model.base;

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
	
	@Override
	public String asJSON(boolean useEscapedQuotes) {
		String quot = useEscapedQuotes ? "\\\"" : "\"";		
		return quot + infoLine + quot;
	}	
	
	@Override	
	public String asJSON() {
		return asJSON(false);
	}	
	

}
