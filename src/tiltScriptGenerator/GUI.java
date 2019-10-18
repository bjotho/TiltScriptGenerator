package tiltScriptGenerator;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;


@SuppressWarnings("serial")
public class GUI extends JFrame {

	public GUI(String title){
		JFrame jf = new JFrame();
		jf.setTitle(title);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenRes = Toolkit.getDefaultToolkit().getScreenResolution();
		double width = screenSize.getWidth();
		double height = screenSize.getHeight();
		jf.setSize((int) width, (int) height);
		System.out.println("ScreenSize: " + screenSize + ", screenRes: " + screenRes + ", width: " + width + ", height: " + height);
		jf.setExtendedState(JFrame.MAXIMIZED_BOTH);
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
