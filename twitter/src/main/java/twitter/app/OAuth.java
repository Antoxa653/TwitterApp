package twitter.app;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import org.apache.log4j.Logger;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class OAuth{
	private String consumerKey = "qYTN6Qa7Ml3dIEesbBeQ";
	private String consumerSecret = "K3bzZh8ZiuWyxzt7nGmBIMzjYXOVxeTiymhtkgqlkcs";
	private String access;
	private String accessTokenSecret;
	private RequestToken requestToken;
	private AccessToken accessToken = null;
	private Twitter twitter;
	public static final Logger LOG=Logger.getLogger(OAuth.class);
	public OAuth(Twitter twitter){
		this.twitter = twitter;
		twitter.setOAuthConsumer(consumerKey, consumerSecret);
	}
	
	public String getOAuthAuthorizationURL(){
		try {			
			requestToken = twitter.getOAuthRequestToken();
		} 
		catch (TwitterException e) {
			LOG.warn("failed to receive a request token");
			e.printStackTrace();			
		}
		LOG.info(requestToken.getAuthenticationURL());		
		return requestToken.getAuthenticationURL();
	}
	
	public boolean OAuthSetup(String pin){
		boolean complit = true;
		try{
			if(pin.length() > 0){
            accessToken = twitter.getOAuthAccessToken(requestToken, pin);
          	}          
          	else{
        	  LOG.warn("no PIN code entered");
        	  complit = false;
          	}
			access = accessToken.getToken();
			accessTokenSecret = accessToken.getTokenSecret();
			print(consumerKey, consumerSecret, access, accessTokenSecret);
      	
		}
		catch (TwitterException te){
			if(401 == te.getStatusCode()){
				LOG.warn("Unable to get the access token.");
				complit = false;
			}
			else{
				te.printStackTrace();
				complit = false;
				LOG.info("Unhandled exception");
			}	
		}
		catch(FileNotFoundException e){	
			e.printStackTrace();
			complit = false;
			LOG.warn("Ошибка cоздания файла twitter4j.properties");
		}		
      	return complit;
    }
	
	public boolean spellCheckPIN(String pin){
		boolean complit = true;
		try{
		int num = Integer.parseInt(pin);
		}
		catch(NumberFormatException e){
			LOG.warn("Bad number format");
			complit = false;
		}
		return complit;
	}
	
	public void print(String consumerKey, String consumerSecret, String accessToken, String accessTokenSecret) throws FileNotFoundException
	    {
	         PrintWriter pw = null;
	         pw = new PrintWriter(new FileOutputStream("twitter4j.properties"));
	         pw.println("debug=false");        
	         pw.println("oauth.consumerKey="+consumerKey);
	         pw.println("oauth.consumerSecret="+consumerSecret);
	         pw.println("oauth.accessToken="+accessToken);
	         pw.println("oauth.accessTokenSecret="+accessTokenSecret);
	         pw.close();
	     }

	
    
   
    
 
}
