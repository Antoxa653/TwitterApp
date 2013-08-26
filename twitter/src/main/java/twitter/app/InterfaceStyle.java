package twitter.app;

import java.awt.Font;
import java.util.Enumeration;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.FontUIResource;

import org.apache.log4j.Logger;



public class InterfaceStyle {
	private Logger log = Logger.getLogger(getClass().getName());
	private String seaStyle = "com.seaglasslookandfeel.SeaGlassLookAndFeel";

	public void init() {
		try {
			UIManager.setLookAndFeel(seaStyle);
		} catch (ClassNotFoundException e) {
			log.error("Bad class name" + seaStyle, e);
		} catch (InstantiationException e) {
			log.error("Cant initiate class instance of " + seaStyle + " class ", e);
		} catch (IllegalAccessException e) {
			log.error("IllegalAccessException", e);
		} catch (UnsupportedLookAndFeelException e) {
			log.error("L&F them cant been used on this system ", e);
		}
		changeDefaultFontForLF();
		log.debug("Interface style com.seaglasslookandfeel.SeaGlassLookAndFeel used");
	}

	private void changeDefaultFontForLF() {
		FontUIResource f = new FontUIResource(new Font("Verdana", 0, 12));
		Enumeration<Object> keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof FontUIResource) {
				FontUIResource orig = (FontUIResource) value;
				Font font = new Font(f.getFontName(), orig.getStyle(), f.getSize());
				UIManager.put(key, new FontUIResource(font));
			}
		}
	}
}
