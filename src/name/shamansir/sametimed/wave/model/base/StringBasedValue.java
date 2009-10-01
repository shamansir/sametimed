package name.shamansir.sametimed.wave.model.base;

public class StringBasedValue {
	
	public static String cleanQuotes(String source) {
		// FIXME: sometimes it replaces quotes twice and so on
		return source.replace("\"", "\\\\\""); 		
	}

}
