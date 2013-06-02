package twitter.app;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import twitter4j.TwitterException;

public class Interface extends JFrame {
	

	JButton sendButton;
	JButton updateButton;
	JTextArea statusArea;
	JList<String> list;
	JFrame frame;
	public Interface() {
		frame = new JFrame("Twitter Application");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1000, 400);				
	}
	
	public void mainInterface() throws TwitterException{	
		sendButton = new JButton("Send");
		updateButton = new JButton("Update");
		JPanel panel1 = new JPanel();
		JPanel panel2 = new JPanel();
		JPanel mainPanel = new JPanel();
		//panel1.setLayout(new BorderLayout());
		//panel2.setLayout(new BorderLayout());
		sendButton.setPreferredSize(new Dimension(100,50));
		updateButton.setPreferredSize(new Dimension(100,50));
		panel1.add(sendButton);
		sendButton.setLocation(100, 100);
		statusArea = new JTextArea();
		statusArea.setLineWrap(true);
		statusArea.setWrapStyleWord(true);
		panel1.add(statusArea);		
		panel2.add(updateButton);		
		list = listCreation();						
		panel2.add(list);		
		mainPanel.setLayout(new FlowLayout());
		mainPanel.add(panel1);
		mainPanel.add(panel2);
		//mainBox.setBorder(new EmptyBorder(10,10,10,10));
		//mainBox.add(box1);
		//mainBox.add(Box.createVerticalStrut(10));
		//mainBox.add(box2);
		frame.setContentPane(mainPanel);		
		frame.setResizable(false);		
		sendButton.addActionListener(new ButtonEventListener());
		updateButton.addActionListener(new UpdateButtonListener());
		frame.pack();	
		frame.setVisible(true);
		frame.setResizable(false);
	
		
	}
	public JList<String> listCreation() throws TwitterException{
		UserTimeLine utl =new UserTimeLine();	
		list =new JList<String>(utl.getTimeLine());
		return list;
	}
	public void updateList() throws TwitterException{				
		mainInterface();						
	}	
	
	class ButtonEventListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
		try{		
			if(statusArea.getText() != null){	
					UserStatus uus = new UserStatus();	
					uus.update(statusArea.getText());   
					JOptionPane.showMessageDialog(null,"Sended", null , JOptionPane.PLAIN_MESSAGE);
					statusArea.setText(null);
				} 								
			}				
			catch (TwitterException e1) {
			JOptionPane.showMessageDialog(null,"This status is already set", null, JOptionPane.PLAIN_MESSAGE);
			
			}
		}
	}
		
	
	class UpdateButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {				
					try {
						//list = listCreation();
						updateList();
					} catch (TwitterException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}	
			
		}
	}
	
		
		
	
	
	

}
