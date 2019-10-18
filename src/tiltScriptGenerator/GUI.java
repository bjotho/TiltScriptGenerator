package tiltScriptGenerator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;


@SuppressWarnings("serial")
public class GUI extends JFrame {

	public GUI(String title){
		JFrame jf = new JFrame();
		jf.setTitle(title);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = (int) screenSize.getWidth();
		int height = (int) screenSize.getHeight();
		jf.setSize((int) (width/1.5), (int) (height/1.5));
		//jf.setExtendedState(JFrame.MAXIMIZED_BOTH);
		jf.setLocationRelativeTo(null);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setLayout(null);
		jf.setVisible(true);
		Dimension jfSize = jf.getContentPane().getSize();
		
		Border blackBorder = BorderFactory.createLineBorder(Color.BLACK);
		
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		
		JLabel addEventLabel = new JLabel("Add event");
		addEventLabel.setBounds((int) (jfSize.width/20), (int) (jfSize.height/25), (int) (jfSize.width*0.9), (int) (jfSize.height*0.03));
		jf.add(addEventLabel);
		
		JPanel addEventPanel = new JPanel();
		addEventPanel.setBounds((int) (jfSize.width/20), (int) (jfSize.height/15), (int) (jfSize.width*0.9), (int) (jfSize.height*0.125));
		addEventPanel.setBorder(blackBorder);
		addEventPanel.setBackground(new Color(200, 225, 225));
		addEventPanel.setLayout(gbl);
		
		c.insets = new Insets(10, 30, 10, 30);
		c.ipadx = 75;
		c.ipady = 10;
		
		String[] addEventTypes = {"Select event type", "Choice 1", "Choice 2", "Choice 3"};
		JComboBox<String> addEventTypeComboBox = new JComboBox<String>(addEventTypes);
		addEventPanel.add(addEventTypeComboBox, c);
		
		c.ipadx = 100;
		c.ipady = 15;
		
		JTextField addEventValue = new JTextField("Enter value");
		addEventPanel.add(addEventValue, c);
		
		JTextField addEventTime = new JTextField("Enter time");
		addEventPanel.add(addEventTime, c);
		
		c.ipadx = 75;
		c.ipady = 10;
		
		JButton addEventButton = new JButton("Add event");
		addEventPanel.add(addEventButton, c);
		
		jf.add(addEventPanel);
		
		JLabel eventScriptLabel = new JLabel("Event script");
		eventScriptLabel.setBounds((int) (jfSize.width/20), (int) (jfSize.height/3.5), (int) (jfSize.width*0.45), (int) (jfSize.height*0.03));
		jf.add(eventScriptLabel);
		
		JPanel eventScriptPanel = new JPanel();
		eventScriptPanel.setBounds((int) (jfSize.width/20), (int) (jfSize.height/3.2), (int) (jfSize.width*0.45), (int) (jfSize.height*0.6));
		eventScriptPanel.setBorder(blackBorder);
		//eventScriptPanel.setBackground(Color.WHITE);
		eventScriptPanel.setLayout(null);
		
		/*c.insets = new Insets(0, 0, 0, 0);
		c.fill = GridBagConstraints.BOTH;
		c.ipadx = 0;
		c.ipady = 0;
		
		JPanel eventScriptHeaderPanel = new JPanel();
		eventScriptHeaderPanel.setBounds(0, 0, 600, 100);
		eventScriptHeaderPanel.setBorder(blackBorder);
		eventScriptHeaderPanel.setBackground(Color.WHITE);
		eventScriptHeaderPanel.setLayout(gbl);
		eventScriptPanel.add(eventScriptHeaderPanel);
		
		jf.add(eventScriptPanel);*/
		
		SwingUtilities.updateComponentTreeUI(jf);
	}
}
