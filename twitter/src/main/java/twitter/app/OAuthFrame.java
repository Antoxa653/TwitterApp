package twitter.app;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import twitter4j.TwitterException;

public class OAuthFrame extends JFrame  {
	private JPanel mainPanel;
	private JTextField urlLabel;
	private JTextField pinField;
	private JButton authorization;
	private final OAuth oa = new OAuth();
	public OAuthFrame(){
		setSize(800, 200);
		setLocationByPlatform(true);		
		setTitle("OAuthFrame");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new FlowLayout());
		add(mainPanel);		
		
		urlLabel = new JTextField();
		
		urlLabel.setText(oa.getOAuthAuthorizationURL());
		urlLabel.setEditable(false);		
		mainPanel.add(urlLabel);
		
		pinField = new JTextField("Enter Pin");
		mainPanel.add(pinField);
		authorization = new JButton("authorization");	
		mainPanel.add(authorization);
		authorization.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {				
				try {
					oa.OAuthSetup(getPin().getText());
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}});
		
		setVisible(true);
	}
	
	JTextField getPin(){
		return pinField;
	}
	
	
}
