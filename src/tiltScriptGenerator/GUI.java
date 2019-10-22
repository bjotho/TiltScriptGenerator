package tiltScriptGenerator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;


@SuppressWarnings("serial")
public class GUI extends JFrame implements ActionListener {
	
	private JFrame jf;
	private JButton addEventButton;
	private JButton readEventsButton;
	private JButton removeEventsButton;
	private JButton editEventButton;
	private JButton saveScriptButton;
	private DefaultTableModel eventScriptTableModel = new DefaultTableModel();
	private JTable eventScriptTable;
	private ModelHandler modelHandler;

	public GUI(String title) {
		this.jf = new JFrame();
		jf.setTitle(title);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = (int) screenSize.getWidth();
		int height = (int) screenSize.getHeight();
		jf.setSize((int) (width*0.666666667), (int) (height*0.666666667));
		jf.setLocationRelativeTo(null);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setLayout(null);
		
		Border blackBorder = BorderFactory.createLineBorder(Color.BLACK);
		
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
		
		this.addEventButton = new JButton("Add event");
		addEventButton.addActionListener(this);
		addEventPanel.add(addEventButton, c);
		
		jf.add(addEventPanel);
		
		JLabel eventScriptLabel = new JLabel("Event script");
		eventScriptLabel.setBounds((int) (jf.getSize().width*0.05), (int) (jf.getSize().height*0.285714286), (int) (jf.getSize().width*0.45), (int) (jf.getSize().height*0.03));
		jf.add(eventScriptLabel);
		
		JPanel eventScriptPanel = new JPanel();
		eventScriptPanel.setBounds((int) (jf.getSize().width*0.05), (int) (jf.getSize().height*0.3125), (int) (jf.getSize().width*0.45), (int) (jf.getSize().height*0.6));
		
		this.eventScriptTableModel.addColumn("Event type");
		this.eventScriptTableModel.addColumn("Event value");
		this.eventScriptTableModel.addColumn("Time");
		this.eventScriptTable = new JTable(this.eventScriptTableModel);
		
		JScrollPane tableScroller = new JScrollPane(eventScriptTable);
		tableScroller.setPreferredSize(new Dimension(eventScriptPanel.getSize().width, eventScriptPanel.getSize().height));
		
		eventScriptPanel.add(tableScroller);
		
		jf.add(eventScriptPanel);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBounds((int) (jf.getSize().width*0.82), (int) (jf.getSize().height*0.3125), (int) (jf.getSize().width*0.13), (int) (jf.getSize().height*0.6));
		
		this.readEventsButton = new JButton("Read events");
		readEventsButton.setPreferredSize(new Dimension((int) (jf.getSize().width*0.13), (int) (jf.getSize().height*0.05)));
		readEventsButton.addActionListener(this);
		buttonPanel.add(readEventsButton);
		
		this.removeEventsButton = new JButton("Remove events");
		removeEventsButton.setPreferredSize(new Dimension((int) (jf.getSize().width*0.13), (int) (jf.getSize().height*0.05)));
		removeEventsButton.addActionListener(this);
		buttonPanel.add(removeEventsButton);
		
		this.editEventButton = new JButton("Edit event");
		editEventButton.setPreferredSize(new Dimension((int) (jf.getSize().width*0.13), (int) (jf.getSize().height*0.05)));
		editEventButton.addActionListener(this);
		buttonPanel.add(editEventButton);
		
		this.saveScriptButton = new JButton("Save script");
		saveScriptButton.setPreferredSize(new Dimension((int) (jf.getSize().width*0.13), (int) (jf.getSize().height*0.05)));
		saveScriptButton.addActionListener(this);
		buttonPanel.add(saveScriptButton);
		
		jf.add(buttonPanel);
		
		jf.setVisible(true);
	}
	
	public void addEvent(String[] event) {
		this.eventScriptTableModel.addRow(event);
		this.jf.revalidate();
	}
	
	public void setModelHandler(ModelHandler modelHandler) {
		this.modelHandler = modelHandler;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.addEventButton) {
			System.out.println("Add event");
		} else if (e.getSource() == this.readEventsButton) {
			System.out.println("Read events");
			
			this.eventScriptTableModel.setRowCount(0);
			
			List<String[]> initialBodyTempReadings = this.modelHandler.getInitialBodyTempReadings();
			
			for (String[] triple : initialBodyTempReadings) {
				this.addEvent(triple);
			}
			
		} else if (e.getSource() == this.removeEventsButton) {
			System.out.println("Remove events");
		} else if (e.getSource() == this.editEventButton) {
			System.out.println("Edit event");
		} else if (e.getSource() == this.saveScriptButton) {
			System.out.println("Save script");
		}
	}
}
