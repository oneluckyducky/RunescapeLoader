import java.applet.Applet;
import java.util.HashMap;

public class Variables {

	private static final String LINK = "http://world76.runescape.com/";
	private static final HashMap<String, String> PARAMETERS = new HashMap<String, String>();
	private static Applet applet;

	public static String getLink() {
		return LINK;
	}

	public static HashMap<String, String> getParameters() {
		return PARAMETERS;
	}

	public static void setApplet(Applet a) {
		applet = a;
	}

	public static Applet getApplet() {
		return applet;
	}

}
