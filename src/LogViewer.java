import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class LogViewer {
	 protected  JTextArea  field = new JTextArea(); //Declare it here for Data Passing
	 
	public LogViewer() {
		JFrame mainframe = new JFrame("Log Viewer");
		JPanel mainpanel = new JPanel();
		field.setPreferredSize(new Dimension(550,3000));
		JScrollPane js = new JScrollPane(field);
		mainpanel.setPreferredSize(new Dimension(600,300));
		js.setPreferredSize(new Dimension(550,300));
		 
		mainpanel.add(js);
		
		mainframe.setContentPane(mainpanel);
		mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		mainframe.pack();
		mainframe.setVisible(true);
		
	}
	
	
	public void SetString(String s){
		field.append(s);
	} 
}
