package twitter.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DefaultCaret;

import twitter.app.FriendList.Friend;
import twitter.app.TimeLine.Tweets;
import twitter.app.UserDirectMessage.Conversation;

import twitter4j.internal.logging.Logger;

public class MainFrame extends JFrame {
	private final String imageLocation = "target/classes/image.jpg";
	private Logger log = Logger.getLogger(getClass());
	private int screenWidth;
	private int screenHeight;
	private JPanel buttonPanel;
	private JPanel panelTwo;
	private JPanel timeLinePanel;
	private FriendList friendList;
	private UserDirectMessage userDirectMessage;
	private UserStatus userStatus;
	private TimeLine timeLine;
	private AutoUpdate autoUpdate;
	private String currentName;

	MainFrame(TwitterInitialization ti) {
		this.friendList = ti.getFl();
		this.userDirectMessage = ti.getUdm();
		this.userStatus = ti.getUs();
		this.timeLine = ti.getTlu();

		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		screenHeight = screenSize.height;
		screenWidth = screenSize.width;
		setSize(screenWidth / 2, screenHeight / 2);
		setMinimumSize(new Dimension(screenWidth / 2, screenHeight / 2));
		setLocationRelativeTo(null);
		setTitle("Twitter Application");
		setResizable(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setBackground(Color.WHITE);

		buttonPanel = createPreferredSizePanel(Color.WHITE, new Dimension(screenWidth / 4, screenHeight / 16));
		panelTwo = createPreferredSizePanel(Color.WHITE, new Dimension(screenWidth / 4, 7 * screenHeight / 16));
		timeLinePanel = createPreferredSizePanel(Color.WHITE, new Dimension(screenWidth / 4, screenHeight / 2));

		buttonPanel.setBorder(BorderFactory.createEtchedBorder());
		panelTwo.setBorder(BorderFactory.createEtchedBorder());
		timeLinePanel.setBorder(BorderFactory.createEtchedBorder());
		timeLinePanel.setName("null");

		createMenuBar();
		createButtonPanel();
		createPanelWithLogo();

		Timer t = new Timer(true);
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				log.debug("Time Line Updating...");
				autoUpdate = new AutoUpdate();
				autoUpdate.execute();
			}
		}, 0, 180000);

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(buttonPanel)
						.addComponent(panelTwo))
				.addComponent(timeLinePanel));

		layout.linkSize(SwingConstants.HORIZONTAL, buttonPanel, panelTwo);

		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addGroup(layout.createSequentialGroup()
								.addComponent(buttonPanel)
								.addComponent(panelTwo))
						.addComponent(timeLinePanel)));
	}

	private void createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Menu");
		JMenuItem logoutItem = new JMenuItem("Logout");
		JMenuItem exit = new JMenuItem("Exit");
		menuBar.add(menu);
		menu.add(logoutItem);
		menu.add(exit);
		setJMenuBar(menuBar);

		logoutItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				dispose();
				LogOut logout = new LogOut();
				logout.doLogout();
			}
		});
		exit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				dispose();
				if (autoUpdate.getState() == SwingWorker.StateValue.STARTED) {
					do {

					}
					while (autoUpdate.getState() == SwingWorker.StateValue.DONE);
				}
				System.exit(0);

			}
		});
	}

	private void createButtonPanel() {
		buttonPanel.setName("buttonPanel");
		log.debug(buttonPanel.getName());
		GroupLayout layout = new GroupLayout(buttonPanel);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		buttonPanel.setLayout(layout);

		JButton tweet = new JButton("Tweet");
		JButton friendList = new JButton("Friend List");
		JButton directMessages = new JButton("DirectMessages");

		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addComponent(tweet)
				.addComponent(friendList)
				.addComponent(directMessages)

				);

		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(tweet)
						.addComponent(friendList)
						.addComponent(directMessages)
				)
				);

		tweet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panelTwo.removeAll();
				createTweetPanel();
			}
		});

		friendList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panelTwo.removeAll();
				createFriendPanel();
			}
		});

		directMessages.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				panelTwo.removeAll();
				createConversationsListPanel();
			}
		});
	}

	private void createPanelWithLogo() {
		panelTwo.setName("emptyPanel");
		log.debug(panelTwo.getName());
		panelTwo.setLayout(new BorderLayout());
		ImageIcon icon = new ImageIcon(imageLocation);
		JLabel label = new JLabel();
		label.setIcon(icon);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		panelTwo.add(BorderLayout.CENTER, label);
	}

	private void createTweetPanel() {
		panelTwo.setName("tweetPanel");
		log.debug(panelTwo.getName());
		GroupLayout layout = new GroupLayout(panelTwo);
		panelTwo.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		final JTextArea textArea = new JTextArea();
		JScrollPane scrollPane = new JScrollPane(textArea);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		AbstractDocument pDoc = (AbstractDocument) textArea.getDocument();
		pDoc.setDocumentFilter(new DocumentSizeFilter(140));

		final JLabel label = new JLabel();
		label.setVisible(false);
		JButton send = new JButton("Update your status!");
		send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				boolean updated = userStatus.update(textArea.getText());
				if (updated) {
					log.debug("Twitter status update correctly");
					label.setText("Status updated!!!");
					label.setVisible(true);
				}
				if (!updated) {
					log.debug("Twitter status has not been updated ");
					label.setText("An erros has occurred");
					label.setVisible(true);
				}
			}
		});

		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(scrollPane)
						.addComponent(send)
						.addComponent(label))
				);

		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(scrollPane))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(send))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(label))
				);
	}

	private void createDirectMessageToPanel(final String name) {
		panelTwo.setName("DirectMessageTo");
		log.debug(panelTwo.getName());
		GroupLayout layout = new GroupLayout(panelTwo);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		panelTwo.setLayout(layout);
		final JTextArea textArea = new JTextArea();
		JScrollPane scrollPane = new JScrollPane(textArea);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		AbstractDocument pDoc = (AbstractDocument) textArea.getDocument();
		pDoc.setDocumentFilter(new DocumentSizeFilter(140));
		final JLabel sendStatus = new JLabel();
		sendStatus.setVisible(false);
		JButton send = new JButton("Send Direct Message");
		send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				boolean sended = userDirectMessage.sentDirectMessageTo(
						name.substring(name.indexOf("@") + 1, name.length()),
						textArea.getText());
				if (sended) {
					log.debug("Direct Message sended");
					sendStatus.setText("Message sended");
					sendStatus.setVisible(true);
				}
				if (!sended) {
					log.debug("Direct Message not sended");
					sendStatus.setText("Message not sended");
					sendStatus.setVisible(true);
				}
			}

		});

		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(scrollPane)
						.addComponent(send)
						.addComponent(sendStatus))
				);

		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(scrollPane))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(send))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(sendStatus))
				);
	}

	private void createFriendPanel() {
		panelTwo.setName("listOfFriends");
		log.debug(panelTwo.getName());
		GroupLayout layout = new GroupLayout(panelTwo);
		panelTwo.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		LinkedHashSet<Friend> listOfFriends = friendList.getFriendList();
		DefaultListModel<String> dlm = new DefaultListModel<String>();
		for (Friend f : listOfFriends) {
			dlm.addElement(f.getName() + " " + "@" + f.getScreenName());
		}
		final JList<String> list = new JList<String>(dlm);

		JScrollPane scrollPane = new JScrollPane(list);
		scrollPane.setBorder(BorderFactory.createEtchedBorder());

		JButton directMassage = new JButton("Direct Massege");
		JButton deleteFriend = new JButton("Dell Friend");
		JButton addFriend = new JButton("Add Friend");
		JLabel lable = new JLabel("Enter friend twitter accaunt @:");
		final JLabel errorLabel = new JLabel();
		errorLabel.setVisible(false);
		final JTextField textField = new JTextField(1);
		textField.setMaximumSize(new Dimension(120, 10));

		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addGroup(layout.createSequentialGroup()
								.addComponent(scrollPane)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
										.addComponent(lable)
										.addComponent(textField)
										.addComponent(addFriend)
										.addComponent(directMassage)
										.addComponent(deleteFriend)))

						.addComponent(errorLabel))
				);

		layout.linkSize(SwingConstants.HORIZONTAL, directMassage, deleteFriend, addFriend);

		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(scrollPane)
						.addGroup(layout.createSequentialGroup()
								.addComponent(lable)
								.addComponent(textField)
								.addComponent(addFriend)
								.addComponent(directMassage)
								.addComponent(deleteFriend)))
				.addComponent(errorLabel)
				);

		directMassage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panelTwo.removeAll();
				createDirectMessageToPanel(list.getSelectedValue());

			}
		});

		deleteFriend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				friendList.deleteFriend(list.getSelectedValue());
				panelTwo.removeAll();
				createFriendPanel();
			}
		});

		addFriend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				friendList.addFriend(textField.getText().trim());
				panelTwo.removeAll();
				createFriendPanel();
				errorLabel.setText("Friend is added!");
				errorLabel.setVisible(true);

			}
		});
	}

	private void createConversationsListPanel() {
		panelTwo.setName("conversationsList");
		log.debug(panelTwo.getName());
		GroupLayout layout = new GroupLayout(panelTwo);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		panelTwo.setLayout(layout);
		DefaultListModel<String> dlm = new DefaultListModel<String>();
		for (String s : userDirectMessage.conversationsList()) {
			dlm.addElement(s);
		}
		final JList<String> nameList = new JList<String>(dlm);
		JScrollPane scrollPane = new JScrollPane(nameList);
		scrollPane.setBorder(BorderFactory.createEtchedBorder());

		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addComponent(scrollPane));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(scrollPane));
		nameList.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent arg0) {
				if (arg0.getButton() == MouseEvent.BUTTON1) {
					panelTwo.removeAll();
					createInternalConversationPanel(nameList.getSelectedValue());
				}

			}

			public void mouseEntered(MouseEvent arg0) {
				//no code

			}

			public void mouseExited(MouseEvent arg0) {
				//no code

			}

			public void mousePressed(MouseEvent arg0) {
				//no code

			}

			public void mouseReleased(MouseEvent arg0) {
				//no code

			}
		});
	}

	private String createInternalConversationPanel(String n) {
		final String name = n;
		setCurrentName(name);
		panelTwo.setName("internalConversation");
		log.debug(panelTwo.getName());
		GroupLayout layout = new GroupLayout(panelTwo);
		panelTwo.setLayout(layout);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);

		JPanel container = new JPanel();
		BoxLayout containerLayout = new BoxLayout(container, BoxLayout.PAGE_AXIS);
		container.setLayout(containerLayout);
		LinkedList<Conversation> conv = userDirectMessage.setConversationMessages(name);
		for (Conversation c : conv) {
			JPanel panel = new JPanel();
			panel.setBorder(BorderFactory.createEtchedBorder());
			JLabel label = new JLabel();
			JTextArea textArea = new JTextArea();
			BorderLayout panelLayout = new BorderLayout();
			panel.setLayout(panelLayout);
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			String dateTime = dateFormat.format(c.getDate());
			label.setText(dateTime);
			textArea.setBorder(BorderFactory.createEtchedBorder());
			textArea.setWrapStyleWord(true);
			textArea.setLineWrap(true);
			textArea.setEditable(false);
			textArea.setText(c.getText());
			panel.add(textArea, BorderLayout.WEST);
			panel.add(label, BorderLayout.CENTER);
			if (c.isSent()) {
				textArea.setBackground(Color.YELLOW);
			}
			else {
				textArea.setBackground(Color.LIGHT_GRAY);
			}
			container.add(panel);
		}

		JScrollPane containerScrollPane = new JScrollPane(container);
		containerScrollPane.setPreferredSize(new Dimension(screenWidth / 4, 2 * screenHeight / 16));
		containerScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		containerScrollPane.setBorder(BorderFactory.createEtchedBorder());

		JPanel messagePanel = new JPanel();
		BorderLayout messagePanelLayout = new BorderLayout();
		messagePanel.setLayout(messagePanelLayout);
		messagePanel.setBorder(BorderFactory.createEtchedBorder());

		JScrollPane messagePanelScrollPane = new JScrollPane(messagePanel);
		messagePanelScrollPane.setPreferredSize(new Dimension(screenWidth / 4, 1 * screenHeight / 16));
		messagePanelScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		messagePanelScrollPane.setBorder(BorderFactory.createEtchedBorder());

		final JTextArea messageArea = new JTextArea();
		messageArea.setWrapStyleWord(true);
		messageArea.setLineWrap(true);
		messageArea.setEditable(true);
		AbstractDocument pDoc = (AbstractDocument) messageArea.getDocument();
		pDoc.setDocumentFilter(new DocumentSizeFilter(140));

		messagePanel.add(BorderLayout.CENTER, messageArea);

		final JLabel sendStatus = new JLabel();
		sendStatus.setVisible(false);

		JButton messageButton = new JButton("Send Message");
		messageButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				boolean sended = userDirectMessage.sentDirectMessageTo(name, messageArea.getText());
				if (sended) {
					log.debug("Direct Message sended");
					sendStatus.setText("Message sended");
					sendStatus.setVisible(true);
				}
				if (!sended) {
					log.debug("Direct Message not sended");
					sendStatus.setText("Message not sended");
					sendStatus.setVisible(true);
				}
			}
		});

		JButton backButton = new JButton("Back");
		backButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				panelTwo.removeAll();
				createConversationsListPanel();
			}
		});

		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(backButton)
						.addComponent(containerScrollPane)
						.addComponent(messagePanelScrollPane)
						.addComponent(messageButton)
						.addComponent(sendStatus)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(backButton)
				.addComponent(containerScrollPane)
				.addComponent(messagePanelScrollPane)
				.addComponent(messageButton)
				.addComponent(sendStatus)
				);
		return name;
	}

	private void createHomeTimeLinePanel() {
		timeLinePanel.setName("homeTimeLine");
		log.debug(timeLinePanel.getName());
		timeLinePanel.setLayout(new BorderLayout());
		JPanel container = new JPanel();
		BoxLayout layout = new BoxLayout(container, BoxLayout.PAGE_AXIS);
		container.setLayout(layout);
		for (Tweets t : timeLine.getTimeLineList()) {
			JPanel panel = new JPanel();
			BorderLayout panelLayout = new BorderLayout();
			panel.setBackground(Color.GRAY);
			panel.setLayout(panelLayout);
			panel.setBorder(BorderFactory.createEtchedBorder());

			final JTextArea textArea = new JTextArea();
			JLabel label = new JLabel();
			label.setText(t.getName());
			DefaultCaret caret = (DefaultCaret) textArea.getCaret();
			caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
			textArea.append(t.getText() + " ");
			textArea.setEditable(false);
			textArea.setWrapStyleWord(true);
			textArea.setLineWrap(true);

			textArea.addMouseListener(new ShowUrlFromTimeLine(textArea));

			panel.add(textArea, BorderLayout.CENTER);
			panel.add(label, BorderLayout.NORTH);
			container.add(panel);
		}

		JScrollPane timeLineScrollPane = new JScrollPane(container);
		timeLineScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		timeLinePanel.add(BorderLayout.CENTER, timeLineScrollPane);

	}

	private void createInternaHomeTimeLinelPanel(String text) {
		String tweetText = text;
		timeLinePanel.setName("internalHomeTimeLine");
		log.debug(timeLinePanel.getName());
		GroupLayout layout = new GroupLayout(timeLinePanel);
		timeLinePanel.setLayout(layout);
		timeLinePanel.setBackground(Color.WHITE);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		JButton backButton = new JButton("back");
		final JTextArea textArea = new JTextArea();
		textArea.setText(tweetText);
		textArea.setEditable(false);
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		textArea.setBorder(BorderFactory.createEtchedBorder());

		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(backButton)
						.addComponent(textArea)
				));

		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(backButton)
				.addComponent(textArea));

		backButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				timeLinePanel.removeAll();
				createHomeTimeLinePanel();
				timeLinePanel.repaint();
				timeLinePanel.revalidate();
			}

		});

		textArea.addMouseListener(new ShowUrl(textArea));
	}

	private String getCurrentName() {
		return currentName;
	}

	private void setCurrentName(String currentName) {
		this.currentName = currentName;
	}

	private JPanel createPreferredSizePanel(Color color, Dimension dimension) {
		JPanel panel = new JPanel();
		panel.setBackground(color);
		panel.setPreferredSize(dimension);
		return panel;
	}

	private class AutoUpdate extends SwingWorker<Object, Object> {

		@Override
		protected Object doInBackground() throws Exception {
			timeLine.setTimeLineList();
			log.debug("Time Line has been updated");
			userDirectMessage.setSent();
			userDirectMessage.setRecieved();
			return null;
		}

		@Override
		protected void done() {
			if ("null".equals(timeLinePanel.getName()) | "homeTimeLine".equals(timeLinePanel.getName())) {
				timeLinePanel.removeAll();
				createHomeTimeLinePanel();
				timeLinePanel.repaint();
				timeLinePanel.revalidate();
			}
			if ("conversationsList".equals(panelTwo.getName())) {
				panelTwo.removeAll();
				createConversationsListPanel();
				panelTwo.repaint();
				panelTwo.revalidate();
			}
			if ("internalConversation".equals(panelTwo.getName())) {
				panelTwo.removeAll();
				createInternalConversationPanel(getCurrentName());
				panelTwo.repaint();
				panelTwo.revalidate();
			}
		}
	}

	private final class ShowUrl implements MouseListener {
		private JTextArea textArea;

		private ShowUrl(JTextArea ta) {
			this.textArea = ta;
		}

		public void mouseClicked(MouseEvent me) {
			int x = me.getX();
			int y = me.getY();
			String text = textArea.getText();
			String regexUrl = "(http{1}s?://)((\\w\\.?\\-?)+\\/?)+([\\s]*)(\\W*)";
			String regexWord = "(\\s{1})|(\\).)";
			int startOffset = textArea.viewToModel(new Point(x, y));
			String[] array = text.split(regexWord);
			for (String s : array) {
				if (s.matches(regexUrl)) {
					int start = text.indexOf(s);
					int finish = start + s.length();
					if (start <= startOffset & startOffset <= finish) {
						Desktop desktop = Desktop.getDesktop();
						if (desktop.isSupported(Desktop.Action.BROWSE)) {
							try {
								String urlString = s;
								URL url = new URL(urlString);
								desktop.browse(url.toURI());
								break;
							} catch (IOException e) {
								log.error("IOException", e);
								e.printStackTrace();
							} catch (URISyntaxException e) {
								log.error("URISyntaxExceptin :", e);
							}
						}
					}
				}
			}
		}

		public void mouseEntered(MouseEvent arg0) {
			//no code

		}

		public void mouseExited(MouseEvent arg0) {
			//no code

		}

		public void mousePressed(MouseEvent arg0) {
			//no code

		}

		public void mouseReleased(MouseEvent arg0) {
			//no code

		}

	}

	private final class ShowUrlFromTimeLine implements MouseListener {
		private JTextArea textArea;

		private ShowUrlFromTimeLine(JTextArea ta) {
			this.textArea = ta;

		}

		public void mouseClicked(MouseEvent me) {
			boolean goInto = true;
			int x = me.getX();
			int y = me.getY();
			String text = textArea.getText();
			String regexUrl = "(http{1}s?://)((\\w\\.?\\-?)+\\/?)+([\\s]*)(\\W*)";
			String regexWord = "(\\s{1})|(\\).)";
			int startOffset = textArea.viewToModel(new Point(x, y));
			String[] array = text.split(regexWord);
			for (String s : array) {
				if (s.matches(regexUrl)) {
					goInto = false;
					int start = text.indexOf(s);
					int finish = start + s.length();
					if (start <= startOffset & startOffset <= finish) {
						Desktop desktop = Desktop.getDesktop();
						if (desktop.isSupported(Desktop.Action.BROWSE)) {
							try {
								String urlString = s;
								URL url = new URL(urlString);
								desktop.browse(url.toURI());
								break;
							} catch (IOException e) {
								log.error("IOException", e);
							} catch (URISyntaxException e) {
								log.error("URISyntaxExceptin :", e);
							}
						}
					}
					else {
						timeLinePanel.removeAll();
						createInternaHomeTimeLinelPanel(textArea.getText());
						timeLinePanel.revalidate();
						timeLinePanel.repaint();
					}

				}
			}
			if (goInto) {
				timeLinePanel.removeAll();
				createInternaHomeTimeLinelPanel(textArea.getText());
				timeLinePanel.revalidate();
				timeLinePanel.repaint();
			}
		}

		public void mouseEntered(MouseEvent e) {
			//no code

		}

		public void mouseExited(MouseEvent e) {
			//no code

		}

		public void mousePressed(MouseEvent e) {
			//no code

		}

		public void mouseReleased(MouseEvent e) {
			//no code

		}
	}

}