package twitter.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class MainFrame extends JFrame {
	private Toolkit kit;
	private Dimension screenSize;
	private int screenHeight;
	private int screenWidth;	
	private static JPanel mainPanel;
	private static JPanel timeLinePanel;
	private static JPanel optionsPanel;
	private static JPanel buttonPanel;
	private static JPanel friendsAndTweetPanel;
	private static JButton tweetButton;
	private static JButton friendListButton;
	
	public MainFrame(){		
		kit = Toolkit.getDefaultToolkit();
		screenSize = kit.getScreenSize();
		screenHeight = screenSize.height;
		screenWidth = screenSize.width;
		setSize(screenWidth/2, screenHeight/2);
		setLocationByPlatform(true);		
		setTitle("GUI");
		
		mainPanel = new JPanel(new GridLayout());
		mainPanel.setBackground(Color.RED);
		timeLinePanel = new JPanel();
		timeLinePanel.setBackground(Color.DARK_GRAY);
		optionsPanel = new JPanel(new GridLayout(0,1));
		optionsPanel.setBackground(Color.GRAY);
		buttonPanel = new JPanel();
		buttonPanel.setBackground(Color.GREEN);
		friendsAndTweetPanel = new JPanel();
		friendsAndTweetPanel.setBackground(Color.ORANGE);
		setContentPane(mainPanel);
		
		
		mainPanel.add(optionsPanel);
		mainPanel.add(timeLinePanel);
		optionsPanel.add(buttonPanel);
		optionsPanel.add(friendsAndTweetPanel);		
		
		tweetButton = makeButton("Tweet", buttonPanel);		
		friendListButton = makeButton("Friend List", buttonPanel);
		
		makeButton("+",timeLinePanel);
		makeButton("-",timeLinePanel);
		
		makeTextArea(10, 20, " Tweet something", friendsAndTweetPanel);
		
	}
	
	JButton makeButton(String name, JPanel panel){
		JButton button = new JButton(name);
		Controler buttonControler = new Controler();
		button.addActionListener(buttonControler);
		panel.add(button);
		return button;
	}			
	
	void makeTextArea(int height, int widht ,String name, JPanel panel){
		JTextArea textArea = new JTextArea(height,widht);
		textArea.setText(name);
		textArea.setLineWrap(true);		
		panel.add(textArea);
	}
	
	static JButton getTweetButton(){
		return tweetButton;
	}
	static JPanel getTimeLinePanel(){
		return timeLinePanel;
	}
}
	

		
		



