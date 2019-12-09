package tiltScriptGenerator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.jena.ontology.OntModelSpec;


@SuppressWarnings("serial")
public class GUI extends JFrame implements ActionListener, TableModelListener {
	
	private JFrame jf;
	private JButton addEventButton;
	private JButton readEventsButton;
	private JButton removeEventsButton;
	private JButton newScriptButton;
	private JButton saveScriptButton;
	private DefaultTableModel eventScriptTableModel = new DefaultTableModel();
	private JTable eventScriptTable;
	private JComboBox<String> addEventTypeComboBox;
	private JTextField addEventValue;
	private JTextField addEventTime;
	private boolean showSaveWarning = false;

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
		
		c.insets = new Insets(10, 20, 10, 20);
		c.ipadx = 75;
		c.ipady = 10;
		
		List<String> eventTypeList = ModelHandler.getEventTypes();
		String[] eventTypeArray = new String[eventTypeList.size()];
		int ix = 0;
		for (String s : eventTypeList) {
			eventTypeArray[ix] = s;
			ix++;
		}
		eventTypeArray = Stream.of(eventTypeArray).sorted().toArray(String[]::new);
		String[] addEventTypes = new String[eventTypeArray.length+1];
		for (int i = 0; i < addEventTypes.length; i++) {
			if (i == 0) {
				addEventTypes[i] = "Select event type";
			} else {
				addEventTypes[i] = eventTypeArray[i-1];
			}
		}
		
		this.addEventTypeComboBox = new JComboBox<String>(addEventTypes);
		addEventPanel.add(addEventTypeComboBox, c);
		
		c.ipadx = 100;
		c.ipady = 15;
		
		this.addEventValue = new JTextField("Enter value");
		addEventPanel.add(addEventValue, c);
		
		this.addEventTime = new JTextField("Enter time");
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
		this.eventScriptTable.getModel().addTableModelListener(this);
		
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(this.eventScriptTable.getModel());
		
		Comparator<String> intComparator = new Comparator<String>() {
		    public int compare(String s1, String s2) {
	            return Integer.parseInt(s1) - Integer.parseInt(s2);
		    }
		};
		
		Comparator<String> floatComparator = new Comparator<String>() {
		    public int compare(String s1, String s2) {
		    	float s1Float = Float.parseFloat(s1);
		    	float s2Float = Float.parseFloat(s2);
	            return (int) ((s1Float * 100) - (s2Float * 100));
		    }
		};
		
		sorter.setComparator(2, intComparator);
		sorter.setComparator(1, floatComparator);
		this.eventScriptTable.setRowSorter(sorter);
		
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
		
		this.newScriptButton = new JButton("New script");
		newScriptButton.setPreferredSize(new Dimension((int) (jf.getSize().width*0.13), (int) (jf.getSize().height*0.05)));
		newScriptButton.addActionListener(this);
		buttonPanel.add(newScriptButton);
		
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
	
	public void removeEvents(int[] selectedEvents) {
		Arrays.sort(selectedEvents);
		
		for (int i = selectedEvents.length - 1; i >= 0; i--) {
			// Make it possible to sort table, and still be able to remove selected events
			selectedEvents[i] = this.eventScriptTable.convertRowIndexToModel(selectedEvents[i]);
			
	        this.eventScriptTableModel.removeRow(selectedEvents[i]);
	    }
	}
	
	//Display unsaved changes warning with selection to proceed or cancel
	//Returns integer corresponding to selected action
	public int saveWarning() {
		if (this.showSaveWarning) {
			Object[] options = {"Cancel", "Discard"};
			return JOptionPane.showOptionDialog(this,
					"You have unsaved changes which will be lost if \nyou proceed to read a new file.\nAre you sure you wish to discard these changes?",
					"Unsaved changes", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
		}
		return 1;
	}
	
	public void readEvents() {
		this.eventScriptTableModel.setRowCount(0);
		
		List<String[]> initialFindings = ModelHandler.getInitialFindings();
		for (String[] triple : initialFindings) {
			this.addEvent(triple);
		}
		List<String[]> eventList = ModelHandler.getEventList();
		for (String[] triple : eventList) {
			this.addEvent(triple);
		}
	}
	
	// Show a FileDialog where the user can choose a patient-file where events will be read from
	public void showFileDialog() {
		FileDialog fd = new FileDialog(jf, "Please choose patient-file:", FileDialog.LOAD);
		
		fd.setDirectory("./patients");
		fd.setFile("*.ttl");
		fd.setVisible(true);
		
		String filename = fd.getFile();
		
		if (filename == null) {
			return;
		} else {
			filename = fd.getDirectory() + filename;
			
			ModelHandler.setInputFileName(filename);
			ModelHandler.setModel(ModelHandler.readFile(OntModelSpec.OWL_DL_MEM));
			readEvents();
		}
	}
	
	public void updateFrameTitle(String patient) {
		this.jf.setTitle("Tilt Script Generator - " + patient);
		jf.revalidate();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		///////////////////////////////////////////////Add event///////////////////////////////////////////////
		
		if (e.getSource() == this.addEventButton) {
			String[] event = new String[3];
			event[0] = (String) addEventTypeComboBox.getSelectedItem();
			event[1] = addEventValue.getText();
			event[2] = addEventTime.getText();
			
			float parsedFloat;
			
			try {
				parsedFloat = Float.parseFloat(event[1]);
			} catch (NumberFormatException e2) {
				JOptionPane.showMessageDialog(this, "Please enter a valid value.", "Warning", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			event[1] = String.valueOf(parsedFloat);
			
			int parsedInt;
			
			try {
				parsedInt = Integer.parseInt(event[2]);
			} catch (NumberFormatException e2) {
				JOptionPane.showMessageDialog(this, "Please enter a valid time value.", "Warning", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			event[2] = String.valueOf(parsedInt);
			
			if (addEventTypeComboBox.getSelectedIndex() != 0) {
				this.addEvent(event);
			} else {
				JOptionPane.showMessageDialog(this, "Please select an event type.", "Warning", JOptionPane.WARNING_MESSAGE);
			}
			
		///////////////////////////////////////////////Read events///////////////////////////////////////////////
			
		} else if (e.getSource() == this.readEventsButton) {
			int unsavedWarningSelection = this.saveWarning();
			// If the user pressed "Discard", show a FileDialog where the user can choose a patient-file where events will be read from
			if (unsavedWarningSelection == 1) {
				showFileDialog();
			}
			this.showSaveWarning = false;
			String patientName = ModelHandler.getPatientName();
			ModelHandler.setPatient(patientName);
			this.updateFrameTitle(patientName);
			ModelHandler.resetModel();
			
		///////////////////////////////////////////////Remove events///////////////////////////////////////////////
			
		} else if (e.getSource() == this.removeEventsButton) {
			int[] selectedEvents = eventScriptTable.getSelectedRows();
			removeEvents(selectedEvents);
			
		///////////////////////////////////////////////New script///////////////////////////////////////////////
			
		} else if (e.getSource() == this.newScriptButton) {
			int unsavedWarningSelection = this.saveWarning();
			if (unsavedWarningSelection == 1) {
				ModelHandler.setPatient("Undefined");
				ModelHandler.setInputFileName("patient.ttl");
				ModelHandler.setModel(ModelHandler.readFile(OntModelSpec.OWL_DL_MEM));
				this.eventScriptTableModel.setRowCount(0);
				this.showSaveWarning = false;
				this.updateFrameTitle("Unsaved script");
			}
			
		///////////////////////////////////////////////Save script///////////////////////////////////////////////
			
		} else if (e.getSource() == this.saveScriptButton) {
			String patientName = JOptionPane.showInputDialog("Enter patient name");
			if (patientName == null) {
				return;
			}
			String patientAddress = JOptionPane.showInputDialog("Enter patient address");
			if (patientAddress == null) {
				return;
			}
			ModelHandler.setPatient(patientName);
			ModelHandler.insertHuman(patientName, patientAddress);
			
			int simulatedInputNumber = 1;
			
			for (int i = 0; i < this.eventScriptTableModel.getRowCount(); i++) {
				String[] event = new String[3];
				for (int j = 0; j < this.eventScriptTableModel.getColumnCount(); j++) {
					event[j] = (String) this.eventScriptTableModel.getValueAt(i, j);
				}
				if (Integer.parseInt(event[2]) == 0) {
					ModelHandler.insertInitialFinding(event[0], event[1]);
				} else {
					ModelHandler.insertSimulatedInput(event[0], event[1], event[2], simulatedInputNumber);
					simulatedInputNumber++;
				}
			}
			ModelHandler.writeFile(patientName);
			this.showSaveWarning = false;
			this.updateFrameTitle(patientName);
			ModelHandler.resetModel();
		}
	}
	
	@Override
	public void tableChanged(TableModelEvent e) {
		if (!this.showSaveWarning) {
			this.showSaveWarning = true;
			this.jf.setTitle("*" + this.jf.getTitle());
		}
	}
}
