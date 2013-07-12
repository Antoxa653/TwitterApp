package twitter.app;


import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import twitter4j.Twitter;
import twitter4j.internal.logging.Logger;


public class OAuthFrame extends JFrame  {
		public static final Logger LOG = Logger.getLogger(OAuthFrame.class);
		private final Twitter twitter;
		private URL url = null;
		private JTextField pinTextField = null;
		private JLabel errorLabel;

		OAuthFrame( Twitter t) {
			this.twitter = t;
			Toolkit kit = Toolkit.getDefaultToolkit();
			Dimension screenSize = kit.getScreenSize();
			int screenHeight = screenSize.height;
			int screenWidth = screenSize.width;
			setPreferredSize(new Dimension(screenWidth / 4, screenHeight / 4));
			setMinimumSize(new Dimension(screenWidth / 4, screenHeight / 4));
			setLocationRelativeTo(null);		
			setTitle("Authorization");
			setResizable(false);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			final OAuth oa = new OAuth(twitter);
			String urlString = oa.getOAuthAuthorizationURL();
			try {
				url = new URL(urlString);
			} catch (MalformedURLException e) {				
				e.printStackTrace();
			}			
			getContentPane().setBackground(Color.WHITE);
			GroupLayout layout = new GroupLayout(getContentPane());
			getContentPane().setLayout(layout);
			layout.setAutoCreateGaps(true);
			layout.setAutoCreateContainerGaps(true);

			JTextArea infoArea = new JTextArea();
			infoArea.setEditable(false);
			infoArea.setWrapStyleWord(true);
			infoArea.setLineWrap(true);		
			infoArea.setText("Follow the url and get PIN code to get access to your twitter accaunt:");

			JTextArea urlArea = new JTextArea();
			Font font = new Font("Verdana", Font.ITALIC, 12);
			urlArea.setEditable(false);
			urlArea.setWrapStyleWord(true);
			urlArea.setLineWrap(true);
			urlArea.setForeground(Color.BLUE);
			urlArea.setFont(font);
			urlArea.setText(urlString);		

			JLabel pinLabel = new JLabel("Enter the received PIN code here:");		
			pinTextField = new JTextField();
			pinTextField.setFont(new Font("SansSerif", Font.CENTER_BASELINE , 28));
			JButton okButton = new JButton("Authorization");

			errorLabel = new JLabel();
			Font errorFont = new Font("Verdana", Font.BOLD, 12);
			errorLabel.setFont(errorFont);
			errorLabel.setForeground(Color.RED);
			errorLabel.setText("Bad format of PIN code");
			errorLabel.setVisible(false);

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

			pinTextField.addMouseListener( new MouseListener() {
				public void mousePressed(MouseEvent e) {
					if (e.isMetaDown()) {
						PopupMenu menu = new PopupMenu();
						menu.show(e.getComponent(), e.getX(), e.getY());
					}
				}

				public void mouseClicked(MouseEvent e) {
					// TODO Auto-generated method stub

				}

				public void mouseEntered(MouseEvent e) {
					// TODO Auto-generated method stub

				}

				public void mouseExited(MouseEvent e) {
					// TODO Auto-generated method stub

				}

				public void mouseReleased(MouseEvent e) {
					// TODO Auto-generated method stub
				}
			}
			);

			okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
						dispose();
						PropertiesExist exist = new PropertiesExist();
						if (oa.spellCheckPIN(pinTextField.getText()) && oa.OAuthSetup(pinTextField.getText()) && exist.isPropertiesExist()) {
							LOG.info("PIN spellcheck passed");
							EventQueue.invokeLater(new Runnable() {
								public void run() {
									ProgressBarFrame pbf = new ProgressBarFrame();					
									Initialization init = new Initialization(pbf, twitter);
									init.execute();
									pbf.setVisible(true);
								}
							});
						}
				}
			});

			urlArea.addMouseListener(new MouseListener() {
				public void mouseClicked(MouseEvent arg0) {
					if (Desktop.isDesktopSupported()) {
						Desktop desktop = Desktop.getDesktop();
						if (desktop.isSupported(Desktop.Action.BROWSE))
							try {
								desktop.browse(url.toURI());
								return;
							}
							catch (Exception exp) {
							}
					}

				}

				public void mouseEntered(MouseEvent arg0) {
					// TODO Auto-generated method stub

				}

				public void mouseExited(MouseEvent arg0) {
					// TODO Auto-generated method stub

				}

				public void mousePressed(MouseEvent arg0) {
					// TODO Auto-generated method stub

				}

				public void mouseReleased(MouseEvent arg0) {
					// TODO Auto-generated method stub

				} });
		}

		class PopupMenu extends JPopupMenu {
		private JMenuItem paste;
		private String pin;
		PopupMenu() {
			paste = new JMenuItem("Paste");
			add(paste);
			pin = new ClipBoardText().getClipboardText();
			paste.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {

					pinTextField.setText(pin);
					LOG.info("PIN entered");

				}

			});

		}
		public String getPin() {
			return pin;
		}

		}
		class ClipBoardText {
		private String clipboardText;
		ClipBoardText() {		
			try {
				Transferable trans = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
				if (trans != null & trans.isDataFlavorSupported(DataFlavor.stringFlavor)) {					 
					clipboardText = (String) trans.getTransferData(DataFlavor.stringFlavor);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}			
		}
		public String getClipboardText() {
			return clipboardText;
		}

	}
}