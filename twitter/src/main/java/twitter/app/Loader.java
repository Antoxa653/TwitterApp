package twitter.app;

import java.awt.EventQueue;

import javax.swing.JFrame;

import twitter4j.TwitterException;



public class Loader {	
	
	public static void main(String[] args){	
		EventQueue.invokeLater(new Runnable(){
			public void run(){
				//MainFrame frame;
				try {
					OAuthSetup oa = new OAuthSetup();
					oa.OAuth();
					UserStatus us = new UserStatus();
					us.update("Versuta!!!");					
					FriendList fl = new FriendList();
					fl.getFriendList();
					/*frame = new MainFrame();
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frame.setVisible(true);
					UserDirectMessage u = new UserDirectMessage();
					u.getListOfDirectMessages();*/
				} catch (TwitterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
							

	}

}
