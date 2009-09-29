package name.shamansir.sametimed.wave.model.base;

public class EmptyModelValue implements IModelValue {
	
	public EmptyModelValue() {};
	
	public EmptyModelValue(String source) {};
	
	@Override
	public String asJSON() { return null; }		

}
