package tiltScriptGenerator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;


@SuppressWarnings("serial")
public class GUI extends JFrame implements ActionListener {
	
	private JFrame jf;
	private JPanel eventScriptBodyType;
	private JPanel eventScriptBodyValue;
	private JPanel eventScriptBodyTime;
	private int timestamp = 0;

	public GUI(String title){
		this.jf = new JFrame();
		jf.setTitle(title);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = (int) screenSize.getWidth();
		int height = (int) screenSize.getHeight();
		jf.setSize((int) (width*0.666666667), (int) (height*0.666666667));
		//jf.setExtendedState(JFrame.MAXIMIZED_BOTH);
		jf.setLocationRelativeTo(null);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setLayout(null);
		
		Border blackBorder = BorderFactory.createLineBorder(Color.BLACK);
		Border emptyBorder = BorderFactory.createEmptyBorder(12, 10, 10, 10);
		//Border compBorder = BorderFactory.createCompoundBorder(emptyBorder, blackBorder);
		
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		
		JLabel addEventLabel = new JLabel("Add event");
		addEventLabel.setBounds((int) (jf.getSize().width*0.05), (int) (jf.getSize().height*0.04), (int) (jf.getSize().width*0.9), (int) (jf.getSize().height*0.03));
		jf.add(addEventLabel);
		
		JPanel addEventPanel = new JPanel();
		addEventPanel.setBounds((int) (jf.getSize().width*0.05), (int) (jf.getSize().height*0.066666667), (int) (jf.getSize().width*0.9), (int) (jf.getSize().height*0.125));
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
		addEventButton.addActionListener(this);
		addEventPanel.add(addEventButton, c);
		
		jf.add(addEventPanel);
		
		JLabel eventScriptLabel = new JLabel("Event script");
		eventScriptLabel.setBounds((int) (jf.getSize().width*0.05), (int) (jf.getSize().height*0.285714286), (int) (jf.getSize().width*0.45), (int) (jf.getSize().height*0.03));
		jf.add(eventScriptLabel);
		
		JPanel eventScriptPanel = new JPanel();
		eventScriptPanel.setBounds((int) (jf.getSize().width*0.05), (int) (jf.getSize().height*0.3125), (int) (jf.getSize().width*0.45), (int) (jf.getSize().height*0.6));
		//eventScriptPanel.setBorder(blackBorder);
		eventScriptPanel.setBackground(Color.WHITE);
		eventScriptPanel.setLayout(null);
		
		int eventScriptHeaderBodySplit = (int) (eventScriptPanel.getSize().height*0.1);
		
		JPanel eventScriptHeaderPanel = new JPanel();
		eventScriptHeaderPanel.setBounds(0, 0, eventScriptPanel.getSize().width, eventScriptHeaderBodySplit);
		eventScriptHeaderPanel.setBorder(blackBorder);
		eventScriptHeaderPanel.setBackground(Color.WHITE);
		eventScriptHeaderPanel.setLayout(new GridLayout(1, 3, 50, 0));
		
		JTextArea eventScriptHeaderType = new JTextArea("Event type");
		eventScriptHeaderType.setEditable(false);
		eventScriptHeaderType.setBorder(emptyBorder);
		eventScriptHeaderPanel.add(eventScriptHeaderType);
		
		JTextArea eventScriptHeaderValue = new JTextArea("Event value");
		eventScriptHeaderValue.setEditable(false);
		eventScriptHeaderValue.setBorder(emptyBorder);
		eventScriptHeaderPanel.add(eventScriptHeaderValue);
		
		JTextArea eventScriptHeaderTime = new JTextArea("Time");
		eventScriptHeaderTime.setEditable(false);
		eventScriptHeaderTime.setBorder(emptyBorder);
		eventScriptHeaderPanel.add(eventScriptHeaderTime);
		
		/*c.insets = new Insets(0, 0, 0, 0);
		c.ipadx = 150;
		c.ipady = 0;
		
		JTextArea eventScriptHeaderType = new JTextArea("Event type");
		eventScriptHeaderType.setEditable(false);
		//eventScriptHeaderType.setBorder(blackBorder);
		eventScriptHeaderPanel.add(eventScriptHeaderType, c);
		
		JTextArea eventScriptHeaderValue = new JTextArea("Event value");
		eventScriptHeaderValue.setEditable(false);
		//eventScriptHeaderValue.setBorder(blackBorder);
		eventScriptHeaderPanel.add(eventScriptHeaderValue, c);
		
		c.insets = new Insets(0, 0, 0, 0);
		c.ipadx = 20;
		
		JTextArea eventScriptHeaderTime = new JTextArea("Time");
		eventScriptHeaderTime.setEditable(false);
		//eventScriptHeaderTime.setBorder(blackBorder);
		eventScriptHeaderPanel.add(eventScriptHeaderTime, c);*/
		
		eventScriptPanel.add(eventScriptHeaderPanel);
		
		JPanel eventScriptBodyPanel = new JPanel();
		eventScriptBodyPanel.setBounds(0, eventScriptHeaderBodySplit-1, eventScriptPanel.getSize().width, eventScriptPanel.getSize().height - (eventScriptHeaderBodySplit-1));
		eventScriptBodyPanel.setPreferredSize(new Dimension(eventScriptPanel.getSize().width, eventScriptPanel.getSize().height - eventScriptHeaderPanel.getSize().height));
		eventScriptBodyPanel.setBorder(blackBorder);
		eventScriptBodyPanel.setBackground(Color.WHITE);
		eventScriptBodyPanel.setLayout(new GridLayout(0, 3, 50, 0));
		
		this.eventScriptBodyType = new JPanel();
		eventScriptBodyType.setBorder(blackBorder);
		eventScriptBodyType.setBackground(Color.WHITE);
		eventScriptBodyPanel.add(eventScriptBodyType);
		
		this.eventScriptBodyValue = new JPanel();
		eventScriptBodyValue.setBorder(blackBorder);
		eventScriptBodyValue.setBackground(Color.WHITE);
		eventScriptBodyPanel.add(eventScriptBodyValue);
		
		this.eventScriptBodyTime = new JPanel();
		eventScriptBodyTime.setBorder(blackBorder);
		eventScriptBodyTime.setBackground(Color.WHITE);
		eventScriptBodyPanel.add(eventScriptBodyTime);
		
		JScrollPane eventScriptScrollPane = new JScrollPane(eventScriptBodyPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		eventScriptScrollPane.setBounds(0, eventScriptHeaderBodySplit-1, eventScriptPanel.getSize().width, eventScriptPanel.getSize().height - (eventScriptHeaderBodySplit-1));
		eventScriptScrollPane.setSize(eventScriptBodyPanel.getSize().width, eventScriptBodyPanel.getSize().height);
		
		eventScriptPanel.add(eventScriptScrollPane);
		
		eventScriptPanel.add(eventScriptBodyPanel);
		
		jf.add(eventScriptPanel);
		
		jf.setVisible(true);
	}
	
	public JTextArea[] createEventScriptEntry() {
		JTextArea[] entry = new JTextArea[3];
		entry[0] = new JTextArea("HansBodyTemperature");
		entry[1] = new JTextArea("0123456789 abcdefghijklmnopqrstuvwxyz");
		entry[2] = new JTextArea(Integer.toString(++this.timestamp));
		return entry;
	}
	
	public void addEvent() {
		JTextArea[] event = this.createEventScriptEntry();
		JPanel[] eventArray = { this.eventScriptBodyType, this.eventScriptBodyValue, this.eventScriptBodyTime };
		for (int i = 0; i < 3; i++) {
			event[i].setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 10, 0, 20), BorderFactory.createLineBorder(Color.BLACK)));
			event[i].setLineWrap(true);
			event[i].setWrapStyleWord(true);
			eventArray[i].add(event[i]);
		}
		this.jf.revalidate();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.addEvent();
	}
}
