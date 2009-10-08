package name.shamansir.sametimed.wave.model.base;

/**
 * @author shamansir <shaman.sir@gmail.com>
 * 
 * An empty model value (used as a stub in models that don't use their value)
 * 
 * @see IModelValue
 */

public class EmptyModelValue /* extends JSONiableValue */ implements IModelValue {
	
	public EmptyModelValue() {};
	
	public EmptyModelValue(String source) {};
	
	@Override
	public String asJSON(boolean useEscapedQuotes) { return "null"; }
	
	@Override
	public String asJSON() { return "null"; }	

}
