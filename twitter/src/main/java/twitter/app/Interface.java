package twitter.app;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
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
		Box box1 = Box.createHorizontalBox();
		box1.add(sendButton);
		box1.add(Box.createHorizontalStrut(100));
		box1.add(updateButton);		
		statusArea = new JTextArea(10, 20);
		statusArea.setLineWrap(true);
		statusArea.setWrapStyleWord(true);		
		list = listCreation();
		Box box2 = Box.createHorizontalBox();				
		box2.add(statusArea);
		box2.add(Box.createHorizontalStrut(10));
		box2.add(list);		
		Box mainBox =Box.createVerticalBox();
		mainBox.setBorder(new EmptyBorder(12,12,12,12));
		mainBox.add(box1);
		mainBox.add(Box.createVerticalStrut(10));
		mainBox.add(box2);
		frame.setContentPane(mainBox);		
		frame.pack();
		frame.setResizable(false);		
		sendButton.addActionListener(new ButtonEventListener());
		updateButton.addActionListener(new UpdateButtonListener());
		frame.setVisible(true);
		
	}
	public JList<String> listCreation() throws TwitterException{
		UserTimeLine utl =new UserTimeLine();	
		list =new JList<String>(utl.getTimeLine());
		return list;
	}
	public void updateList() throws TwitterException{				
		//list = listCreation();
		//list.setVisibleRowCount(20);
		//list.setSize(200, 200);
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
