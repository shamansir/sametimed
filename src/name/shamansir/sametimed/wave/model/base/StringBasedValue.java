package name.shamansir.sametimed.wave.model.base;

public class StringBasedValue {
	
	public static String cleanQuotes(String source) {
		return source.replace("\"", "\\\\\"");
	}

}
