package name.shamansir.sametimed.wave.model.base;

/**
 * @author shamansir <shaman.sir@gmail.com>
 * 
 * @see IModelValue
 * @see JSONiableValue
 */

public class InfoLine extends JSONiableValue implements IModelValue {
	
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
		return quot + escapeJSONString(infoLine) + quot;
	}	
	
	@Override	
	public String asJSON() {
		return asJSON(false);
	}	
	

}
