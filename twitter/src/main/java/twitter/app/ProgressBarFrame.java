package twitter.app;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JProgressBar;


public class ProgressBarFrame extends JFrame {

	ProgressBarFrame() {
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
		setSize(screenWidth / 4, screenHeight / 4);
		setMinimumSize(new Dimension(screenWidth / 4, screenHeight / 4));
		setLocationRelativeTo(null);	
		setTitle("Progress...");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	

        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
        add(Box.createVerticalGlue());
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);        
        add(progressBar);        
        add(Box.createVerticalGlue());

	}	

}
