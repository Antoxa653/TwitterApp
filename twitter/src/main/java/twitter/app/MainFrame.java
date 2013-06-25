package twitter.app;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.ScrollPane;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import twitter4j.TwitterException;

public class MainFrame extends JFrame {
	
		public MainFrame() throws TwitterException{		
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
		setSize(screenWidth/2, screenHeight/2);
		setLocationByPlatform(true);		
		setTitle("TwitterApp");
		setResizable(false);		
				
		JButton tweetButton = new JButton("Tweet");		
		JButton friendListButton = new JButton("Friend List");
		JButton updateButton = new JButton("Update");
		JButton button = new JButton("button");
		JPanel timeLinePanel = new createTimeLinePanel().panel();
		JScrollPane scrollTimeLinePane = new JScrollPane(timeLinePanel);
		scrollTimeLinePane.setPreferredSize(new Dimension(screenWidth/3, screenHeight/4));
		scrollTimeLinePane.setBorder(BorderFactory.createTitledBorder("Time Line"));
		//scrollTimeLinePane.setHorizontalScrollBarPolicy(screenWidth);	
		//JLabel label = new JLabel("Some text");
		JPanel tweetPanel = new JPanel();		
		tweetPanel.setBorder(BorderFactory.createTitledBorder("Tweet text"));
		tweetPanel.setPreferredSize(new Dimension(screenWidth/8, screenHeight/4));
		JTextArea textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		JScrollPane textAreaPane = new  JScrollPane(textArea);
		textAreaPane.setPreferredSize(new Dimension(screenWidth/8, screenHeight/3));				
		tweetPanel.add(textAreaPane);		
		
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
	    layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);		
		
		layout.setHorizontalGroup(layout.createSequentialGroup()				
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
										.addComponent(tweetButton)
										.addComponent(friendListButton))
										//.addComponent(label))
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
										.addComponent(updateButton)
										.addComponent(button))
								)						
						.addComponent(tweetPanel)
						)		
				.addComponent(scrollTimeLinePane)
				);			
		
        layout.linkSize(SwingConstants.HORIZONTAL, tweetButton, friendListButton);
        layout.linkSize(SwingConstants.HORIZONTAL, updateButton, button);

		layout.setVerticalGroup(layout.createSequentialGroup()				
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addGroup(layout.createSequentialGroup()
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(tweetButton)										
										.addComponent(updateButton))
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(friendListButton)
										.addComponent(button))
								//.addComponent(label)
								.addComponent(tweetPanel))
						.addComponent(scrollTimeLinePane))						
				
				);	
		
		setVisible(true);
		
	}
				
}

class createTimeLinePanel{		
	JPanel panel() throws TwitterException{
		JPanel panel = new JPanel();		
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));	
		List<Tweets> friends = new TimeLineUpdater().getUpdatedTimeLine();				
		for(Tweets f : friends){
			JTextArea textArea = new JTextArea();
			textArea.setText("@"+f.getName()+"\n"+f.getText());
			textArea.setEditable(false);
			textArea.setLineWrap(true);			
			textArea.setWrapStyleWord(true);			
			textArea.setBorder(BorderFactory.createLineBorder(Color.black));
			panel.add(textArea);			
		}			
		return panel;
	}	
}