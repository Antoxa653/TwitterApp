package twitter.app;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Controler implements ActionListener {

	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		if(source == MainFrame.getTweetButton()){
			MainFrame.getTimeLinePanel().setBackground(Color.RED);
		}

	}

}
