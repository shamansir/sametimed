package name.shamansir.sametimed.wave.model.base;

import org.apache.commons.lang.StringEscapeUtils;

public class JSONiableValue {
	
	public static String escapeJSONString(String sourceString) {
		return StringEscapeUtils.escapeJavaScript(sourceString).replace("\\\"", "\\\\\"");
	}

}
