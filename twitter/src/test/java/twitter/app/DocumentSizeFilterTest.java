package twitter.app;

import static org.junit.Assert.assertEquals;

import javax.swing.JTextArea;
import javax.swing.text.AbstractDocument;

import org.junit.Test;

public class DocumentSizeFilterTest {

	@Test
	public void testInsert() {
		StringBuilder sb = new StringBuilder();
		int firstLenght = 120;
		for (int i = 0; i < firstLenght; i++) {
			sb.append("a");
		}
		String first = sb.toString();
		sb.setLength(0);
		int secondLenght = 10;
		for (int i = 0; i < secondLenght; i++) {
			sb.append("b");
		}
		String second = sb.toString();
		sb.setLength(0);
		int result = (first + second).length();
		JTextArea ta = new JTextArea();
		ta.setText(first);
		AbstractDocument ad = (AbstractDocument) ta.getDocument();
		ad.setDocumentFilter(new DocumentSizeFilter(140));
		ta.append(second);
		assertEquals(result, ta.getText().length());
	}

	@Test
	public void testReplace() {
		StringBuilder sb = new StringBuilder();
		int firstLenght = 130;
		for (int i = 0; i < firstLenght; i++) {
			sb.append("a");
		}
		String first = sb.toString();
		sb.setLength(0);
		int secondLenght = 10;
		for (int i = 0; i < secondLenght; i++) {
			sb.append("b");
		}
		String second = sb.toString();
		sb.setLength(0);
		int result = (first + second).length();
		JTextArea ta = new JTextArea();
		ta.setText(first);
		AbstractDocument ad = (AbstractDocument) ta.getDocument();
		ad.setDocumentFilter(new DocumentSizeFilter(140));
		ta.append(second);
		assertEquals(result, ta.getText().length());
	}

}
