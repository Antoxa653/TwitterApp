package twitter.app;

import java.awt.Toolkit;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import org.apache.log4j.Logger;

public class DocumentSizeFilter extends DocumentFilter {
	private Logger log = Logger.getLogger(getClass().getName());
	private int maxCharacters;

	public DocumentSizeFilter(int maxChars) {
		maxCharacters = maxChars;
	}

	public void insertString(FilterBypass fb, int offs, String str, AttributeSet a) {
		try {
			if ((fb.getDocument().getLength() + str.length()) <= maxCharacters) {
				super.insertString(fb, offs, str, a);
			}
			else {
				Toolkit.getDefaultToolkit().beep();
			}
		} catch (BadLocationException e) {
			log.error("Bad location within document model :", e);			
		}

	}

	public void replace(FilterBypass fb, int offs, int length, String str, AttributeSet a) {
		try {
			if ((fb.getDocument().getLength() + str.length() - length) <= maxCharacters) {
				super.replace(fb, offs, length, str, a);
			}
			else {
				Toolkit.getDefaultToolkit().beep();
			}
		} catch (BadLocationException e) {
			log.error("Bad location within document model :", e);			
		}
	}
}
