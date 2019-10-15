package tiltScriptGenerator;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;


@SuppressWarnings("serial")
public class GUI extends JFrame {

	public GUI(String title){
		JFrame jf = new JFrame();
		jf.setTitle(title);
		int width = 700; 
		int height = 700; 
		jf.setSize(width, height);
		jf.setLocationRelativeTo(null);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setLayout(null);
		
		JLabel eventLabel = new JLabel("Add event");
		eventLabel.setVerticalTextPosition(JLabel.TOP);
		eventLabel.setHorizontalTextPosition(JLabel.LEFT);
		eventLabel.setBounds(50, 50, 600, 100);
		jf.add(eventLabel);
		JButton button = new JButton("button 1");
		button.setBounds(250, 330, 200, 40);
		jf.add(button);
		
		jf.setVisible(true);
	}
}
