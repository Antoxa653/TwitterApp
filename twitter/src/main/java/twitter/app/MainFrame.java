package twitter.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
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
import twitter.app.HomeTimeLine.Tweets;
import twitter.app.UserDirectMessage.Conversation;
import twitter4j.internal.logging.Logger;

public class MainFrame extends JFrame {
	private final String imageLocation = "/image.jpg";
	private Logger log = Logger.getLogger(getClass());
	private int screenWidth;
	private int screenHeight;
	private JPanel firstPanel;
	private JPanel secondPanel;
	private JPanel thirdPanel;
	private FriendList friendList;
	private UserDirectMessage userDirectMessage;
	private UserStatus userStatus;
	private HomeTimeLine timeLine;
	private MainFrameDataUpdateTimer dataUpdateTimer;
	private Map<Panels, PanelsViews> currentViewMap;

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
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setBackground(Color.WHITE);

		firstPanel = createPreferredSizePanel(new Dimension(screenWidth / 4, screenHeight / 16));
		firstPanel.setBorder(BorderFactory.createEtchedBorder());
		firstPanel.setLayout(new BorderLayout());
		secondPanel = createPreferredSizePanel(new Dimension(screenWidth / 4, 7 * screenHeight / 16));
		secondPanel.setBorder(BorderFactory.createEtchedBorder());
		secondPanel.setLayout(new BorderLayout());
		thirdPanel = createPreferredSizePanel(new Dimension(screenWidth / 4, screenHeight / 2));
		thirdPanel.setBorder(BorderFactory.createEtchedBorder());
		thirdPanel.setLayout(new BorderLayout());

		//Initialize Map<Panels, PanelsViews> currentViewMap;
		createCurrentView();

		createMenuBar();
		firstPanel.add(BorderLayout.CENTER, createButtonPanel());
		secondPanel.add(BorderLayout.CENTER, createLogoPanel());

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(firstPanel)
						.addComponent(secondPanel))
				.addComponent(thirdPanel));

		layout.linkSize(SwingConstants.HORIZONTAL, firstPanel, secondPanel);

		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addGroup(layout.createSequentialGroup()
								.addComponent(firstPanel)
								.addComponent(secondPanel))
						.addComponent(thirdPanel)));
	}

	private String getCurrentNameForInternalConversationsPanel() {
		return currentName;
	}

	private void setCurrentNameForInternalConversationPanel(String name) {
		currentName = name;
	}

	private void createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		menuBar.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				secondPanel.removeAll();
				secondPanel.add(BorderLayout.CENTER, createLogoPanel());
				secondPanel.revalidate();
				secondPanel.repaint();
			}
		});
		JMenu menu = new JMenu("Menu");
		JMenuItem logoutItem = new JMenuItem("Logout");
		logoutItem.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				dispose();
				LogOut logout = new LogOut();
				logout.doLogout();
			}

		});

		JMenuItem exit = new JMenuItem("Exit");
		exit.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				dispose();
				if (dataUpdateTimer.isWorking) {
					while (!dataUpdateTimer.isWorking)
						;
					System.exit(0);
				}
				System.exit(0);
			}
		});

		menuBar.add(menu);
		menu.add(logoutItem);
		menu.add(exit);
		setJMenuBar(menuBar);

	}

	private JPanel createButtonPanel() {
		currentViewMap.put(Panels.FIRSTPANEL, PanelsViews.BUTTONPANEL);

		JButton tweet = new JButton("Tweet");
		tweet.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				secondPanel.removeAll();
				secondPanel.add(BorderLayout.CENTER, createTweetPanel());
				secondPanel.revalidate();
				secondPanel.repaint();
			}
		});

		JButton friendList = new JButton("Friend List");
		friendList.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				secondPanel.removeAll();
				secondPanel.add(BorderLayout.CENTER, createFriendPanel());
				secondPanel.revalidate();
				secondPanel.repaint();
			}
		});

		JButton directMessages = new JButton("DirectMessages");
		directMessages.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				secondPanel.removeAll();
				secondPanel.add(BorderLayout.CENTER, createConversationsListPanel());
				secondPanel.revalidate();
				secondPanel.repaint();
			}
		});

		JPanel buttonPanel = new JPanel();
		BoxLayout layout = new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS);
		buttonPanel.add(Box.createVerticalStrut(40));
		buttonPanel.setLayout(layout);
		buttonPanel.add(tweet);
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(friendList);
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(directMessages);
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.setBackground(Color.WHITE);

		return buttonPanel;

	}

	private JPanel createLogoPanel() {
		currentViewMap.put(Panels.SECONDPANEL, PanelsViews.LOGOPANEL);

		JPanel panelWithLogo = new JPanel();
		panelWithLogo.setLayout(new BorderLayout());
		ImageIcon image = new ImageIcon(this.getClass().getResource(imageLocation));
		JLabel label = new JLabel();
		label.setIcon(image);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		panelWithLogo.add(BorderLayout.CENTER, label);
		panelWithLogo.setBackground(Color.WHITE);
		return panelWithLogo;
	}

	private JPanel createTweetPanel() {
		currentViewMap.put(Panels.SECONDPANEL, PanelsViews.TWEETPANEL);

		JPanel tweetPanel = new JPanel();
		final JTextArea textArea = new JTextArea();
		JScrollPane scrollPane = new JScrollPane(textArea);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		AbstractDocument pDoc = (AbstractDocument) textArea.getDocument();
		pDoc.setDocumentFilter(new DocumentSizeFilter(140));
		final JLabel label = new JLabel();
		label.setVisible(false);
		JButton sent = new JButton("Update your status!");
		sent.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				boolean updated = userStatus.update(textArea.getText());
				if (updated) {
					log.debug("Twitter status update correctly");
					label.setText("Status updated!!!");
					label.setVisible(true);
				}
				else {
					log.debug("Twitter status has not been updated ");
					label.setText("An erros has occurred, try again");
					label.setVisible(true);
				}
			}
		});

		GroupLayout layout = new GroupLayout(tweetPanel);
		tweetPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(scrollPane)
						.addComponent(sent)
						.addComponent(label))
				);

		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(scrollPane))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(sent))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(label))
				);
		return tweetPanel;
	}

	private JPanel createDirectMessageToPanel(final String name) {
		currentViewMap.put(Panels.SECONDPANEL, PanelsViews.DIRECTMESSAGETOPANEL);

		JPanel directMessageToPanel = new JPanel();

		final JTextArea textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		AbstractDocument pDoc = (AbstractDocument) textArea.getDocument();
		pDoc.setDocumentFilter(new DocumentSizeFilter(140));
		JScrollPane scrollPane = new JScrollPane(textArea);
		final JLabel sentStatus = new JLabel();
		sentStatus.setVisible(false);
		JButton sent = new JButton("Send Direct Message");
		sent.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				boolean isSent = userDirectMessage.sentDirectMessageTo(
						name.substring(name.indexOf("@") + 1, name.length()),
						textArea.getText());
				if (isSent) {
					log.debug("Direct Message sent");
					sentStatus.setText("Message sent");
					sentStatus.setVisible(true);
				}
				else {
					log.debug("Direct Message was not sent");
					sentStatus.setText("Message was not sent");
					sentStatus.setVisible(true);
				}
			}
		});

		GroupLayout layout = new GroupLayout(directMessageToPanel);
		directMessageToPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(scrollPane)
						.addComponent(sent)
						.addComponent(sentStatus))
				);

		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(scrollPane))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(sent))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(sentStatus))
				);
		return directMessageToPanel;
	}

	private JPanel createFriendPanel() {
		currentViewMap.put(Panels.SECONDPANEL, PanelsViews.FRIENDPANEL);

		JPanel friendPanel = new JPanel();

		Set<Friend> listOfFriends = friendList.getFriendList();

		DefaultListModel<String> dlm = new DefaultListModel<String>();
		for (Friend f : listOfFriends) {
			dlm.addElement(f.getName() + " " + "@" + f.getScreenName());
		}
		final JList<String> list = new JList<String>(dlm);

		JScrollPane scrollPane = new JScrollPane(list);
		scrollPane.setBorder(BorderFactory.createEtchedBorder());

		JLabel lable = new JLabel("Enter friend twitter accaunt @:");
		final JLabel errorLabel = new JLabel();
		errorLabel.setVisible(false);
		final JTextField textField = new JTextField(1);
		textField.setMaximumSize(new Dimension(120, 10));

		JButton directMessage = new JButton("Direct Message");
		directMessage.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				secondPanel.removeAll();
				secondPanel.add(BorderLayout.CENTER, createDirectMessageToPanel(list.getSelectedValue()));
				secondPanel.revalidate();
				secondPanel.repaint();

			}
		});

		JButton deleteFriend = new JButton("Remove the friend");
		deleteFriend.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				friendList.deleteFriend(list.getSelectedValue());
				errorLabel.setText("Friend was deleted!");
				errorLabel.setVisible(true);

				secondPanel.removeAll();
				secondPanel.add(BorderLayout.CENTER, createFriendPanel());
				secondPanel.revalidate();
				secondPanel.repaint();

			}
		});

		JButton addFriend = new JButton("Add a friend");
		addFriend.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				friendList.addFriend(textField.getText().trim());
				errorLabel.setText("Friend is added!");
				errorLabel.setVisible(true);

				secondPanel.removeAll();
				secondPanel.add(BorderLayout.CENTER, createFriendPanel());
				secondPanel.revalidate();
				secondPanel.repaint();

			}
		});

		GroupLayout layout = new GroupLayout(friendPanel);
		friendPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addGroup(layout.createSequentialGroup()
								.addComponent(scrollPane)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
										.addComponent(lable)
										.addComponent(textField)
										.addComponent(addFriend)
										.addComponent(directMessage)
										.addComponent(deleteFriend)))

						.addComponent(errorLabel))
				);

		layout.linkSize(SwingConstants.HORIZONTAL, directMessage, deleteFriend, addFriend);

		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(scrollPane)
						.addGroup(layout.createSequentialGroup()
								.addComponent(lable)
								.addComponent(textField)
								.addComponent(addFriend)
								.addComponent(directMessage)
								.addComponent(deleteFriend)))
				.addComponent(errorLabel)
				);

		return friendPanel;

	}

	private JPanel createConversationsListPanel() {
		currentViewMap.put(Panels.SECONDPANEL, PanelsViews.CONVERSATIONSLISTPANEL);

		JPanel conversationsListPanel = new JPanel();

		DefaultListModel<String> dlm = new DefaultListModel<String>();
		for (String s : userDirectMessage.conversationsList()) {
			dlm.addElement(s);
		}
		final JList<String> nameList = new JList<String>(dlm);
		nameList.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					secondPanel.removeAll();
					secondPanel.add(BorderLayout.CENTER, createInternalConversationPanel(nameList.getSelectedValue()));
					secondPanel.revalidate();
					secondPanel.repaint();
				}
			}
		});

		JScrollPane scrollPane = new JScrollPane(nameList);
		scrollPane.setBorder(BorderFactory.createEtchedBorder());

		GroupLayout layout = new GroupLayout(conversationsListPanel);
		conversationsListPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addComponent(scrollPane));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(scrollPane));

		return conversationsListPanel;

	}

	private JPanel createInternalConversationPanel(String n) {
		currentViewMap.put(Panels.SECONDPANEL, PanelsViews.INTERNALCONVERSATIONPANEL);

		final String name = n;
		setCurrentNameForInternalConversationPanel(name);

		JPanel internalConversationPanel = new JPanel();

		JPanel container = new JPanel();
		BoxLayout containerLayout = new BoxLayout(container, BoxLayout.PAGE_AXIS);
		container.setLayout(containerLayout);
		List<Conversation> conv = userDirectMessage.setConversationMessages(name);
		for (Conversation c : conv) {
			container.add(conversationMessagesPanel(c));
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

		JButton messageButton = new JButton("Sent Message");
		messageButton.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
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
		backButton.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				secondPanel.removeAll();
				secondPanel.add(BorderLayout.CENTER, createConversationsListPanel());
				secondPanel.revalidate();
				secondPanel.repaint();
			}
		});

		GroupLayout layout = new GroupLayout(internalConversationPanel);
		internalConversationPanel.setLayout(layout);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
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
		return internalConversationPanel;
	}

	private JPanel conversationMessagesPanel(Conversation c) {
		JPanel conversationMessagesPanel = new JPanel();
		conversationMessagesPanel.setBorder(BorderFactory.createEtchedBorder());
		JLabel label = new JLabel();
		JTextArea textArea = new JTextArea();
		BorderLayout conversationMessagesPanelLayout = new BorderLayout();
		conversationMessagesPanel.setLayout(conversationMessagesPanelLayout);
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String dateTime = dateFormat.format(c.getDate());
		label.setText(dateTime);
		textArea.setBorder(BorderFactory.createEtchedBorder());
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		textArea.setEditable(false);
		textArea.setText(c.getText());
		conversationMessagesPanel.add(textArea, BorderLayout.WEST);
		conversationMessagesPanel.add(label, BorderLayout.CENTER);
		if (c.isSent()) {
			textArea.setBackground(Color.YELLOW);
		}
		else {
			textArea.setBackground(Color.LIGHT_GRAY);
		}
		return conversationMessagesPanel;

	}

	private JPanel createHomeTimeLinePanel() {
		currentViewMap.put(Panels.THIRDPANEL, PanelsViews.HOMETIMELINEPANEL);

		JPanel homeTimeLinePanel = new JPanel();
		homeTimeLinePanel.setLayout(new BorderLayout());
		final JPanel container = new JPanel();
		BoxLayout layout = new BoxLayout(container, BoxLayout.PAGE_AXIS);
		container.setLayout(layout);
		List<Tweets> homeTileLineTweets = timeLine.getTimeLineList();
		for (Tweets t : homeTileLineTweets) {
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

			textArea.addMouseListener(new UrlFromMessages(textArea));

			panel.add(textArea, BorderLayout.CENTER);
			panel.add(label, BorderLayout.NORTH);
			container.add(panel);
		}

		JScrollPane timeLineScrollPane = new JScrollPane(container);
		timeLineScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		timeLineScrollPane.getVerticalScrollBar().setUnitIncrement(16);
		homeTimeLinePanel.add(BorderLayout.CENTER, timeLineScrollPane);
		return homeTimeLinePanel;

	}

	private JPanel createInternaHomeTimeLinelPanel(String text) {
		currentViewMap.put(Panels.THIRDPANEL, PanelsViews.INTERNALHOMETIMELINEPANEL);
		String tweetText = text;

		JPanel internaHomeTimeLinelPanel = new JPanel();

		JButton backButton = new JButton("Back");
		backButton.addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent e) {
				thirdPanel.removeAll();
				thirdPanel.add(BorderLayout.CENTER, createHomeTimeLinePanel());
				thirdPanel.revalidate();
				thirdPanel.repaint();
			}

		});

		final JTextArea textArea = new JTextArea();
		textArea.setText(tweetText);
		textArea.setEditable(false);
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		textArea.setBorder(BorderFactory.createEtchedBorder());

		GroupLayout layout = new GroupLayout(internaHomeTimeLinelPanel);
		internaHomeTimeLinelPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(backButton)
						.addComponent(textArea)
				));

		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(backButton)
				.addComponent(textArea));

		textArea.addMouseListener(new UrlFromMessages(textArea));
		return internaHomeTimeLinelPanel;
	}

	private JPanel createPreferredSizePanel(Dimension dimension) {
		JPanel panel = new JPanel();
		panel.setPreferredSize(dimension);
		return panel;
	}

	public void init() {
		dataUpdateTimer = new MainFrameDataUpdateTimer();
		dataUpdateTimer.timerInit();
	}

	private void createCurrentView() {
		currentViewMap = new HashMap<Panels, PanelsViews>();
		currentViewMap.put(Panels.FIRSTPANEL, PanelsViews.BUTTONPANEL);
		currentViewMap.put(Panels.SECONDPANEL, PanelsViews.LOGOPANEL);
		currentViewMap.put(Panels.THIRDPANEL, PanelsViews.HOMETIMELINEPANEL);
	}

	private enum Panels {
		FIRSTPANEL(), SECONDPANEL(), THIRDPANEL();

	}

	private enum PanelsViews {
		BUTTONPANEL, LOGOPANEL, TWEETPANEL, DIRECTMESSAGETOPANEL, FRIENDPANEL, CONVERSATIONSLISTPANEL, INTERNALCONVERSATIONPANEL, HOMETIMELINEPANEL, INTERNALHOMETIMELINEPANEL

	}

	private class MainFrameDataUpdateTimer {
		private Logger log = Logger.getLogger(getClass());
		private int updateTimeDelay = 180000;
		private Timer timer;
		private boolean isWorking = false;

		MainFrameDataUpdateTimer() {
			timer = new Timer(true);
		}

		public void timerInit() {
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					log.debug("Time Line Updating...");
					AutoUpdate autoUpdate = new AutoUpdate();
					autoUpdate.execute();
				}
			}, 0, updateTimeDelay);
		}

		private class AutoUpdate extends SwingWorker<Object, Object> {

			@Override
			protected Object doInBackground() throws Exception {
				isWorking = true;
				timeLine.setTimeLineList();
				log.debug("Time Line has been updated");
				userDirectMessage.setSent();
				userDirectMessage.setRecieved();
				return null;
			}

			@Override
			protected void done() {
				isWorking = false;
				if (currentViewMap.get(Panels.THIRDPANEL) == PanelsViews.HOMETIMELINEPANEL) {
					thirdPanel.removeAll();
					thirdPanel.add(BorderLayout.CENTER, createHomeTimeLinePanel());
					thirdPanel.revalidate();
					thirdPanel.repaint();
				}
				if (currentViewMap.get(Panels.SECONDPANEL) == PanelsViews.CONVERSATIONSLISTPANEL) {
					secondPanel.removeAll();
					secondPanel.add(BorderLayout.CENTER, createConversationsListPanel());
					secondPanel.revalidate();
					secondPanel.repaint();
				}
				if (currentViewMap.get(Panels.SECONDPANEL) == PanelsViews.INTERNALCONVERSATIONPANEL) {
					secondPanel.removeAll();
					secondPanel.add(BorderLayout.CENTER,
							createInternalConversationPanel(getCurrentNameForInternalConversationsPanel()));
					secondPanel.revalidate();
					secondPanel.repaint();

				}

			}
		}
	}

	private final class UrlFromMessages extends MouseAdapter {
		private JTextArea textArea;

		private UrlFromMessages(JTextArea ta) {
			this.textArea = ta;
		}

		public void mouseClicked(MouseEvent me) {
			boolean goInto = true;
			int x = me.getX();
			int y = me.getY();
			String text = textArea.getText();
			String regexUrl = "(http{1}s?://)((\\w\\.?\\-?)+\\/?)+([\\s]*)(\\W*)";
			String regexWord = "(\\s{1})|(\\).)|”";
			int startOffset = textArea.viewToModel(new Point(x, y));
			String[] array = text.split(regexWord);
			for (String s : array) {
				if (s.matches(regexUrl)) {
					goInto = false;
					int start = text.indexOf(s);
					int finish = start + s.length();
					if (start <= startOffset && startOffset <= finish) {
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
					else if (currentViewMap.get(Panels.THIRDPANEL) == PanelsViews.HOMETIMELINEPANEL) {
						thirdPanel.removeAll();
						thirdPanel.add(BorderLayout.CENTER, createInternaHomeTimeLinelPanel(textArea.getText()));
						thirdPanel.revalidate();
						thirdPanel.repaint();
					}
				}
			}
			if (goInto) {
				thirdPanel.removeAll();
				thirdPanel.add(BorderLayout.CENTER, createInternaHomeTimeLinelPanel(textArea.getText()));
				thirdPanel.revalidate();
				thirdPanel.repaint();
			}

		}

	}

}