package twitter.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class TimeLineUpdater extends SwingWorker<LinkedList<Tweets>,Object> {
	private Twitter twitter;
	private JPanel timeLinePanel;
	public TimeLineUpdater(JPanel timeLinePanel, Twitter twitter){
		this.twitter = twitter;
		this.timeLinePanel = timeLinePanel;
	}
	
	private LinkedList<Tweets> updateTimeLine() throws TwitterException{
		List<Status> statusList = twitter.getHomeTimeline();
		LinkedList<Tweets> list = new LinkedList<Tweets>();
		for(Status status: statusList){
			Tweets t = new Tweets(status.getId(),status.getUser().getName(),status.getText());				
			list.add(t);	
		}
		return list;
	}

	@Override
	protected LinkedList<Tweets> doInBackground() throws Exception {
		return updateTimeLine();
	}
	@Override
	protected void done(){
		timeLinePanel.setLayout(new BorderLayout());
		JPanel container = new JPanel();
		BoxLayout layout = new BoxLayout(container, BoxLayout.PAGE_AXIS);		
		container.setLayout(layout);
		try {
			for(Tweets t : get()){			
				JPanel panel = new JPanel();				
				panel.setBackground(Color.GRAY);				
				panel.setLayout(new BorderLayout());
				panel.setBorder(BorderFactory.createEtchedBorder());			
				panel.addMouseListener(new MouseListener(){

					public void mouseClicked(MouseEvent arg0) {
						//timeLinePanel.removeAll();					
						//internalPanel();
						//timeLinePanel.repaint();
						//timeLinePanel.revalidate();
					
					}

					public void mouseEntered(MouseEvent arg0) {
						// TODO Auto-generated method stub
						
					}

					public void mouseExited(MouseEvent arg0) {
						// TODO Auto-generated method stub
						
					}

					public void mousePressed(MouseEvent arg0) {
						// TODO Auto-generated method stub
						
					}

					public void mouseReleased(MouseEvent arg0) {
						// TODO Auto-generated method stub
						
					}});
				
				JTextArea textArea = new JTextArea(t.getName()+"\n"+t.getText());
				textArea.setEditable(false);
				textArea.setWrapStyleWord(true);
				textArea.setLineWrap(true);
							
				panel.add(textArea);			
				container.add(panel);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JScrollPane timeLineScrollPane = new JScrollPane(container);
		timeLineScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);		
		timeLinePanel.add(BorderLayout.CENTER, timeLineScrollPane);	
		timeLinePanel.repaint();
		timeLinePanel.revalidate();
		
	}
}



class Tweets{
	private long id;
	private String name;
	private String text;
	Tweets(long id, String name, String text){
		this.id = id;
		this.name = name;
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
	public String getName() {
		return name;
	}
	
	public long getId() {
		return id;
	}	
	
}



