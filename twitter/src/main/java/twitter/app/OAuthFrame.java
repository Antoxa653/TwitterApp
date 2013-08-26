package twitter.app;

import java.awt.Color;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import twitter4j.Twitter;

public class OAuthFrame extends JFrame {
	private final Twitter twitter;
	private Logger log = Logger.getLogger(getClass().getName());
	private JTextField pinTextField;
	private OAuth oa;
	private URL url;
	private JLabel errorLabel;

	OAuthFrame(Twitter tTwitter) {
		this.twitter = tTwitter;

		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
		setSize(new Dimension(screenWidth / 4, screenHeight / 4 + 100));
		setLocationRelativeTo(null);
		setTitle("Authorization");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		oa = new OAuth(twitter);
		url = getOAuthAuthorizationURL();

		JTextArea infoArea = new JTextArea();
		infoArea.setEditable(false);
		infoArea.setFocusable(false);
		infoArea.setWrapStyleWord(true);
		infoArea.setLineWrap(true);
		infoArea.setText("Follow the url below and use PIN code to get access to your twitter accaunt:");

		JTextArea urlArea = new JTextArea();
		urlArea.setEditable(false);
		urlArea.setFocusable(false);
		urlArea.setWrapStyleWord(true);
		urlArea.setLineWrap(true);
		urlArea.setForeground(Color.BLUE);
		urlArea.setFont(new Font("Verdana", Font.ITALIC, 12));
		urlArea.setText(url.toString());

		JLabel pinLabel = new JLabel("Enter the received PIN code here:");
		pinTextField = new JTextField();
		pinTextField.setFont(new Font("SansSerif", Font.CENTER_BASELINE, 28));
		JButton okButton = new JButton("Login");

		errorLabel = new JLabel();
		errorLabel.setVisible(false);

		Container container = getContentPane();
		container.setBackground(Color.WHITE);
		GroupLayout layout = new GroupLayout(container);
		container.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(infoArea)
						.addComponent(urlArea)
						.addGroup(layout.createSequentialGroup()
								.addComponent(pinLabel)
								.addComponent(pinTextField))
						.addComponent(okButton)
						.addComponent(errorLabel)));

		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(infoArea)
				.addComponent(urlArea)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(pinLabel)
						.addComponent(pinTextField))
				.addComponent(okButton)
				.addComponent(errorLabel)
				);

		pinTextField.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isMetaDown()) {
					PopupMenu menu = new PopupMenu();
					menu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});

		okButton.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (spellCheckPIN() && oa.OAuthSetup(pinTextField.getText())) {
					log.debug("PIN spellcheck passed");
					EventQueue.invokeLater(new Runnable() {
						public void run() {
							dispose();
							ProgressBarFrame pbf = new ProgressBarFrame();
							TwitterResourcesInitialization init = new TwitterResourcesInitialization(pbf, twitter);
							init.execute();
							pbf.setVisible(true);
						}
					});
				}
				else {
					errorLabel.setForeground(Color.RED);
					errorLabel.setText("Bad format of PIN code");
					errorLabel.setVisible(true);
				}
			}
		});

		urlArea.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent arg0) {
				if (Desktop.isDesktopSupported()) {
					Desktop desktop = Desktop.getDesktop();
					if (desktop.isSupported(Desktop.Action.BROWSE))
						try {
							desktop.browse(url.toURI());
						} catch (URISyntaxException e) {
							log.error("URISyntaxException " + url.toString(), e);
						} catch (IOException e) {
							log.error("IOException trying to open desktop browser", e);
						}
				}
			}

		});
	}

	private URL getOAuthAuthorizationURL() {
		URL url = null;
		String urlString = oa.getOAuthAuthorizationURL();
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			log.error("Incorrect form of URL", e);
		}
		return url;
	}

	private boolean spellCheckPIN() {
		boolean complit = true;
		try {
			int num = Integer.parseInt(pinTextField.getText());
		} catch (NumberFormatException e) {
			log.error("Bad number format of PIN code");
			complit = false;
		}
		return complit;
	}

	private class PopupMenu extends JPopupMenu {
		private JMenuItem paste;
		private String pin;

		PopupMenu() {
			paste = new JMenuItem("Paste");
			add(paste);
			pin = new ClipboardText().getClipboardText();
			paste.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					pinTextField.setText(pin);
					log.debug("PIN entered");
				}
			});
		}
	}

	private class ClipboardText {
		private String clipboardText;

		ClipboardText() {
			String clipBoardText = initClipBoardData();
			if (clipBoardText != null) {
				this.clipboardText = initClipBoardData();
			}
			else {
				errorLabel.setForeground(Color.RED);
				errorLabel.setText("Bad format or clipBoardText is not exist");
				errorLabel.setVisible(true);
			}
		}

		private String initClipBoardData() {
			Transferable trans = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
			String text = null;
			if (trans != null & trans.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				try {
					text = (String) trans.getTransferData(DataFlavor.stringFlavor);
				} catch (UnsupportedFlavorException e) {
					log.error("Exception appears while getting text from the clipboard", e);
				} catch (IOException e) {
					log.error("IOException while getting text from the clipboard :", e);
				}
			}
			return text;
		}

		public String getClipboardText() {
			return clipboardText;
		}
	}
}