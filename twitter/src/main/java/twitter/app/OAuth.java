package twitter.app;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

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
	public OAuth(){
		this.twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(consumerKey, consumerSecret);
	}
	
	public void OAuthSetup(String pin) throws Exception {                     
      try{    	  
           if(pin.length() > 0){
             accessToken = twitter.getOAuthAccessToken(requestToken, pin);
           }          
           else{
             accessToken = twitter.getOAuthAccessToken();
           }
      	} 
      catch (TwitterException te){
          if(401 == te.getStatusCode()){
            System.out.println("Unable to get the access token.");
          }else{
            te.printStackTrace();
          }
       }              
      access = accessToken.getToken();
      accessTokenSecret = accessToken.getTokenSecret();  
      PrintVerifyCredentials pvc = new PrintVerifyCredentials();
      pvc.print(consumerKey, consumerSecret, access, accessTokenSecret);     
           
    }
	
	public String getOAuthAuthorizationURL(){
		try {			
			requestToken = twitter.getOAuthRequestToken();
		} catch (TwitterException e) {			
			e.printStackTrace();
		}		
		return requestToken.getAuthenticationURL();
		
	}
}

class PrintVerifyCredentials{
     public void print(String consumerKey, String consumerSecret, String accessToken, String accessTokenSecret)
     {
         PrintWriter pw = null;
         try
         {
             pw = new PrintWriter(new FileOutputStream("twitter4j.properties"));
         }
        catch(FileNotFoundException e)
        {
            System.out.println("Ошибка открытия файла twitter4j.properties");
            
         }
         pw.println("debug=false");        
         pw.println("oauth.consumerKey="+consumerKey);
         pw.println("oauth.consumerSecret="+consumerSecret);
         pw.println("oauth.accessToken="+accessToken);
         pw.println("oauth.accessTokenSecret="+accessTokenSecret);
         pw.close();
         
        
    }
 }

