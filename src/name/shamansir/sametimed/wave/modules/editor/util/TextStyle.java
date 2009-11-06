package name.shamansir.sametimed.wave.modules.editor.util;

public class TextStyle {
	
	private static final String STYLE_STR_RE = "[biu]{0,3}";

	private boolean isBold;
	private boolean isItalic;
	private boolean isUnderlined;
	
	public TextStyle() {
		isBold = false;
		isItalic = false;
		isUnderlined = false;
	}
	
	public TextStyle(String style) {
		loadFromString(style);
	}
		
	public boolean isBold() {
		return isBold;
	}
	public boolean isItalic() {
		return isItalic;
	}
	public boolean isUnderlined() {
		return isUnderlined;
	}
	
	public static TextStyle fromString(String styleString) {
		TextStyle instance = new TextStyle();
		instance.loadFromString(styleString);
		return instance;
	}
	
	private void loadFromString(String styleString) {
		if (styleString == null) return;
		if (styleString.matches(STYLE_STR_RE)) {
			if (styleString.contains("b")) isBold = true;
			if (styleString.contains("i")) isItalic = true;
			if (styleString.contains("u")) isUnderlined = true;
		} else throw new IllegalArgumentException("Style string \"" + styleString + "\" is not matches internal regular expression: " + STYLE_STR_RE);
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		if (isBold) result.append('b');
		if (isItalic) result.append('i');
		if (isUnderlined) result.append('u');
		return result.toString();
	}
	
	
}
