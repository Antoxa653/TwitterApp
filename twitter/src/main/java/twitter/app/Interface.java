package twitter.app;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;

import twitter4j.Status;
import twitter4j.TwitterException;

public class Interface extends JFrame {
	
	OAuth oa = new OAuth();
	UpdateTimeLine utl = new UpdateTimeLine();
	private JButton button = new JButton("Send");
	private JButton updateButton =new JButton("Update");
	private JTextArea textArea = new JTextArea();	
	private DefaultListModel<String> listModel = new DefaultListModel<String>();
	private JList<String> list = new JList<String>(utl.getTimeLine(listModel));
	private JScrollPane listScroll = new JScrollPane(list);
	
	public Interface() throws TwitterException{
		super("Twitter Application");		
		this.setBounds(400, 400, 700, 400);		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container container = this.getContentPane();
		container.setLayout(new GridLayout());
		button.addActionListener(new  ButtonEventListener());
		updateButton.addActionListener(new UpdateButtonListener());
		container.add(button);
		container.add(updateButton);
		container.add(textArea);		
        list.setLayoutOrientation(JList.VERTICAL_WRAP);
		list.setVisibleRowCount(-1);			
		container.add(list);
		
	}
	
	class ButtonEventListener implements ActionListener{
		public void actionPerformed(ActionEvent e){			
			if(textArea.getText() != null){				
				try {
					oa.authorization(textArea.getText());
					JOptionPane.showMessageDialog(null,"Sended", "Output", JOptionPane.PLAIN_MESSAGE);
					textArea.setText("");
				} catch (TwitterException e1) {
					JOptionPane.showMessageDialog(null,"This status is already set", "Output", JOptionPane.PLAIN_MESSAGE);
				}
			}
			
			
		}
		
	}
	class UpdateButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			
			
		}
	}
	
		
		
	
	
	

}
