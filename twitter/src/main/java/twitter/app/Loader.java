package twitter.app;

import java.awt.EventQueue;

import javax.swing.JFrame;

import twitter4j.TwitterException;



public class Loader {	
	
	public static void main(String[] args){			
		EventQueue.invokeLater(new Runnable(){
			public void run(){				
				CheckAuthorizationProperties cap = new CheckAuthorizationProperties();
				if(cap.check()==false){
				OAuthFrame oaFrame = new OAuthFrame();
				}
				else{
					try {
						MainFrame mf = new MainFrame();
					} catch (TwitterException e) {						
						e.printStackTrace();
					}
				}			 
				
			}
		});
	}
}
				
				
			