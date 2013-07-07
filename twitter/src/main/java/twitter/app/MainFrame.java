package twitter.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;
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
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.text.Caret;
import javax.swing.text.DefaultCaret;

import twitter4j.Twitter;
import twitter4j.internal.logging.Logger;

public class MainFrame extends JFrame{	
	
	private int screenWidth;
	private int screenHeight;
	private JPanel buttonPanel;		
	private JPanel panelTwo;
	private JPanel timeLinePanel;	
	public static final Logger LOG = Logger.getLogger(MainFrame.class);
	private Twitter twitter;
	private TwitterInit twitterInit;
	private FriendList fl;	
	private UserDirectMessage udm;
	private UserStatus us;	
	private TimeLine tlu;
	MainFrame(TwitterInit ti, Twitter twitter){		
		this.twitterInit = ti;
		this.twitter = twitter;
		this.fl = ti.getFl();		
		this.udm = ti.getUdm();
		this.us = ti.getUs();
		this.tlu = ti.getTlu();
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		screenHeight = screenSize.height;
		screenWidth = screenSize.width;
		setSize(screenWidth/2, screenHeight/2);
		setMinimumSize(new Dimension(screenWidth/2, screenHeight/2));
		setLocationRelativeTo(null);		
		setTitle("Twitter Application");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setBackground(Color.WHITE);
		
		buttonPanel = createPreferredSizePanel(Color.WHITE,  new Dimension(screenWidth/4, screenHeight/16));		
		panelTwo = createPreferredSizePanel(Color.WHITE,  new Dimension(screenWidth/4, 7*screenHeight/16));		
		timeLinePanel = createPreferredSizePanel(Color.WHITE, new Dimension(screenWidth/4, screenHeight/2));
						
		buttonPanel.setBorder(BorderFactory.createEtchedBorder());
		panelTwo.setBorder(BorderFactory.createEtchedBorder());
		timeLinePanel.setBorder(BorderFactory.createEtchedBorder());
		
		createButtonPanel();
		createEmptyPanel();
		createTimeLinePanel();	
		
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(buttonPanel)
						.addComponent(panelTwo))
				.addComponent(timeLinePanel));				
		
		layout.linkSize(SwingConstants.HORIZONTAL,buttonPanel, panelTwo);
		
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addGroup(layout.createSequentialGroup()
						.addComponent(buttonPanel)
						.addComponent(panelTwo))
				.addComponent(timeLinePanel)));
	}

	public void createButtonPanel(){
		GroupLayout layout = new GroupLayout(buttonPanel);
		buttonPanel.setLayout(layout);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		JButton tweet = new JButton("Tweet");
		JButton friendList = new JButton("Friend List");
		final JButton update = new JButton("Update");
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
						.addComponent(directMessages))
				);
	
		
		tweet.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				panelTwo.removeAll();
				createTweetPanel();
			}	
		});
		
		friendList.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				panelTwo.removeAll();
				createFriendPanel();
			}
		});
		/*
		update.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				TimeLineUpdater updater = new TimeLineUpdater(timeLinePanel);		
				updater.execute();
				update.setEnabled(false);
				Timer t = new Timer();
				t.schedule(new TimerTask(){
					@Override
					public void run() {
						update.setEnabled(true);						
					}}, 60000);
			}	
		});
		*/
		directMessages.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				panelTwo.removeAll();
				getDirectMessages();
				
			}
			
		});
	}
	
	public void createEmptyPanel(){
		panelTwo.setLayout(new BorderLayout());
		final ImageIcon icon = new ImageIcon("F:\\git\\TwitterApp\\twitter\\images.jpg");		
		JLabel label = new JLabel();
		label.setIcon(icon);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		panelTwo.add(BorderLayout.CENTER,label);		
	}
	
	public void createTweetPanel(){
		GroupLayout layout = new GroupLayout(panelTwo);		
		panelTwo.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		final JTextArea textArea = new JTextArea();
		JScrollPane scrollPane = new JScrollPane(textArea);			
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		final JLabel label = new JLabel();
		label.setVisible(false);
		JButton send = new JButton("Update your status!");
		send.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {				
				boolean updated =us.update(textArea.getText());
				if(updated){
					LOG.info("Twitter status update correctly");
					label.setText("Status updated!!!");
					label.setVisible(true);
				}
				if(!updated){
					LOG.info("Twitter status has not been updated ");
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
	
	public void sentDirectMessage(final String name){
		GroupLayout layout = new GroupLayout(panelTwo);		
		panelTwo.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		final JTextArea textArea = new JTextArea();
		JScrollPane scrollPane = new JScrollPane(textArea);			
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		final JLabel sendStatus = new JLabel();
		sendStatus.setVisible(false);
		JButton send = new JButton("Send Direct Message");		
		send.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {				
				boolean sended = udm.sentDirectMessageTo(name, textArea.getText());
				if(sended){
					LOG.info("Direct Message sended");
					sendStatus.setText("Message sended");					
					sendStatus.setVisible(true);
				}
				if(!sended){
					LOG.info("Direct Message not sended");
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
	
	public void createFriendPanel(){		
		GroupLayout layout = new GroupLayout(panelTwo);
		panelTwo.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		LinkedHashSet<Friend> friendList = fl.getFriendList();
		DefaultListModel<String> dlm = new DefaultListModel<String>();
		for(Friend f : friendList){
			dlm.addElement(f.getName().trim());
		}
		final JList<String> list = new JList<String>(dlm);
		
		JScrollPane scrollPane = new JScrollPane(list);		
		scrollPane.setBorder(BorderFactory.createEtchedBorder());		
		
		JButton directMassage = new JButton("Direct Massege");
		JButton deleteFriend = new JButton("Dell Friend");
		JButton addFriend = new JButton("Add Friend");
		JLabel lable = new JLabel("Find new friends");
		final JTextField textField = new JTextField(1);
		textField.setMaximumSize(new Dimension(120,10));
		
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addComponent(scrollPane)					
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(lable)
							.addComponent(textField)
							.addComponent(addFriend)
							.addComponent(directMassage)
							.addComponent(deleteFriend))				
				);
				
				
		layout.linkSize(SwingConstants.HORIZONTAL,directMassage, deleteFriend, addFriend );		
		
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(scrollPane)
						.addGroup(layout.createSequentialGroup()
								.addComponent(lable)
								.addComponent(textField)
								.addComponent(addFriend)
								.addComponent(directMassage)
								.addComponent(deleteFriend)))				
				);
		
		directMassage.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				panelTwo.removeAll();
				sentDirectMessage(list.getSelectedValue());
				
			}
		});
		
		deleteFriend.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				fl.deleteFriend(list.getSelectedValue());
				panelTwo.removeAll();
				createFriendPanel();
			}
		});
		
		addFriend.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				fl.addFriend(textField.getText());
				panelTwo.removeAll();
				createFriendPanel();
			}
		});
	}
	
	public void getDirectMessages(){
		GroupLayout layout = new GroupLayout(panelTwo);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		panelTwo.setLayout(layout);
		DefaultListModel<String> dlm = new DefaultListModel<String>();
		for(String s : udm.conversationsList()){
			dlm.addElement(s);
		}
		final JList<String> nameList = new JList<String>(dlm);
		JScrollPane scrollPane = new JScrollPane(nameList);		
		scrollPane.setBorder(BorderFactory.createEtchedBorder());		
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addComponent(scrollPane));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(scrollPane));
		nameList.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent arg0) {
				panelTwo.removeAll();
				internalConversation(nameList.getSelectedValue());
				
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}});
		
	}
	
	public void internalConversation(String name){
		GroupLayout layout = new GroupLayout(panelTwo);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		panelTwo.setLayout(layout);
		
		JPanel container = new JPanel();
		BoxLayout containerLayout = new BoxLayout(container, BoxLayout.PAGE_AXIS);
		container.setLayout(containerLayout);
		LinkedList<Conversation> conv = udm.getConversationMessages(name);		
		for(Conversation c : conv){			
			JPanel panel = new JPanel();
			panel.setBorder(BorderFactory.createEtchedBorder());
			JLabel label = new JLabel();
			JTextArea textArea = new JTextArea();
			BorderLayout panelLayout = new BorderLayout();
			panel.setLayout(panelLayout);	
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");				
			String dateTime = dateFormat.format(c.getDate());
			if(c.isSent()){				
				label.setText(dateTime);
				textArea.setBorder(BorderFactory.createEtchedBorder());
				textArea.setBackground(Color.YELLOW);
				textArea.setWrapStyleWord(true);
				textArea.setLineWrap(true);
				textArea.setEditable(false);
				textArea.setText(c.getText());				
				panel.add(textArea, BorderLayout.WEST);
				panel.add(label, BorderLayout.CENTER);
				
			}
			if(!c.isSent()){							
				label.setText(dateTime);
				textArea.setBorder(BorderFactory.createEtchedBorder());
				textArea.setBackground(Color.LIGHT_GRAY);
				textArea.setWrapStyleWord(true);
				textArea.setLineWrap(true);
				textArea.setEditable(false);
				textArea.setText(c.getText());
				panel.add(textArea, BorderLayout.WEST);
				panel.add(label, BorderLayout.CENTER);
				
			}
			container.add(panel);
		}
		
		JScrollPane scrollPane = new JScrollPane(container);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		JButton backButton = new JButton("Back");
		backButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				panelTwo.removeAll();
				getDirectMessages();
				
			}
			
		});
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(backButton)
						.addComponent(scrollPane)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(backButton)
				.addComponent(scrollPane)
				);
	}
	
	public void createTimeLinePanel(){		
		timeLinePanel.setLayout(new BorderLayout());
		JPanel container = new JPanel();
		BoxLayout layout = new BoxLayout(container, BoxLayout.PAGE_AXIS);		
		container.setLayout(layout);		
			for(Tweets t : tlu.getTimeLineList()){			
				JPanel panel = new JPanel();
				BorderLayout panelLayout = new BorderLayout();
				panel.setBackground(Color.GRAY);				
				panel.setLayout(panelLayout);
				panel.setBorder(BorderFactory.createEtchedBorder());	
				
				final JTextArea textArea = new JTextArea();
				DefaultCaret caret = (DefaultCaret) textArea.getCaret();
				caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
				textArea.append(t.getName()+"\n"+t.getText());
				textArea.setEditable(false);
				textArea.setWrapStyleWord(true);
				textArea.setLineWrap(true);
				
				textArea.addMouseListener(new MouseListener(){
					public void mouseClicked(MouseEvent arg0) {
						timeLinePanel.removeAll();					
						internalPanel(textArea.getText());
						timeLinePanel.repaint();
						timeLinePanel.revalidate();
					
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
						
					}});
				
				
							
				panel.add(textArea, BorderLayout.CENTER);			
				container.add(panel);			
			}
			
		JScrollPane timeLineScrollPane = new JScrollPane(container);
		timeLineScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);		
		timeLinePanel.add(BorderLayout.CENTER, timeLineScrollPane);
				
	}
	
	public void internalPanel(String text){		
		timeLinePanel.setLayout(new BorderLayout());
		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		panel.setBackground(Color.WHITE);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		JButton backButton = new JButton("back");		
		JTextArea textArea = new JTextArea(text);			
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
		
		timeLinePanel.add(BorderLayout.CENTER, panel);
		
		backButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				timeLinePanel.removeAll();				
				createTimeLinePanel();
				timeLinePanel.repaint();
				timeLinePanel.revalidate();
			}
			
		});
	}
			
	public JPanel createPreferredSizePanel(Color color, Dimension dimension) {
		JPanel panel = new JPanel();
		panel.setBackground(color);
		panel.setPreferredSize(dimension);
		return panel;		
	}
	
}