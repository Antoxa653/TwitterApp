package twitter.app;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import twitter4j.TwitterException;

public class MainFrame extends JFrame {		
	
	
	public MainFrame() throws TwitterException{		
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
		setSize(screenWidth/2, screenHeight/2);
		setLocationByPlatform(true);		
		setTitle("GUI");		
		setLayout(new GridBagLayout());
		
		JPanel timeLinePanel = new JPanel();	
		JPanel buttonPanel = new JPanel();				
		timeLinePanel.setBackground(Color.GRAY);
		
		add(buttonPanel, new GBC(0,0,1,1,100,100).setFill(GBC.BOTH));
		add(timeLinePanel, new GBC(0,1,2,2,100,100).setFill(GBC.BOTH));
		
		
		JButton tweetButton = new JButton("Tweet");
		JButton updateButton = new JButton("Update");		
		buttonPanel.add(tweetButton);
		buttonPanel.add(updateButton);
		
		JTabbedPane listPane = new JTabbedPane();
		timeLinePanel.add(listPane);
		
		
		listPane.addTab("title", new Tab().createTimeLine());		
		
	}
				
}

class GBC extends GridBagConstraints{
	GBC(int gridx, int gridy, int gridwidth, int gridheight, int weightx, int weighty){
		this.gridx = gridx;
		this.gridy = gridy;
		this.gridwidth = gridwidth;
		this.gridheight = gridheight;
		this.weightx = weightx;
		this.weighty = weighty;		
	}
	
	public GBC setFill(int fill){
		this.fill = fill;
		return this;
	}
	
	public GBC setAnchor(int anchor){
		this.anchor = anchor;
		return this;
	}
	
	public GBC setInsets(int top, int bottom, int left, int right){
		this.insets = new Insets(top,bottom,left,right);
		return this;
	}
	public GBC setIpad(int ipadx, int ipady){
		this.ipadx = ipadx;
		this.ipady = ipady;
		return this;
	}
}


class Tab{		
	public JScrollPane createTimeLine() throws TwitterException{
		TimeLineUpdater updater = new TimeLineUpdater();				
		JList<String> list = new JList<String>(updater.getUpdatedTimeLine());
		JScrollPane pane = new JScrollPane(list);
		return pane;
	}
}
		
		



