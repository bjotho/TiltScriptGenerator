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
import java.util.ArrayList;
import java.util.Arrays;
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
import javax.swing.RowSorter;
import javax.swing.SortOrder;
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
	private JButton editEventButton;
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
		
		TableRowSorter<TableModel> sorter = new TableRowSorter<>(this.eventScriptTable.getModel());
		this.eventScriptTable.setRowSorter(sorter);
		List<RowSorter.SortKey> sortKeys = new ArrayList<>();
		int columnIndexToSort = 2;
		sortKeys.add(new RowSorter.SortKey(columnIndexToSort, SortOrder.ASCENDING));
		sorter.setSortKeys(sortKeys);
		sorter.sort();
		
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
	
	public void removeEvents(int[] selectedEvents) {
		Arrays.sort(selectedEvents);
		
		for (int i = selectedEvents.length - 1; i >= 0; i--) {
	        this.eventScriptTableModel.removeRow(selectedEvents[i]);
	        this.showSaveWarning = true;
	    }
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.addEventButton) {
			System.out.println("Add event");
			String[] event = new String[3];
			event[0] = (String) addEventTypeComboBox.getSelectedItem();
			event[1] = addEventValue.getText();
			event[2] = addEventTime.getText();
			if (addEventTypeComboBox.getSelectedIndex() != 0) {
				this.addEvent(event);
				this.showSaveWarning = true;
			}
			
		} else if (e.getSource() == this.readEventsButton) {
			System.out.println("Read events");
			int unsavedWarningSelection = 1;
			if (showSaveWarning) {
				//Display unsaved changes warning with selection to proceed or cancel
				Object[] options = {"Cancel", "Discard"};
				unsavedWarningSelection = JOptionPane.showOptionDialog(this,
						"You have unsaved changes which will be lost if \nyou proceed to read a new file.\nAre you sure you wish to discard these changes?",
						"Unsaved changes", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
			}
			// If the user pressed "Discard", show a FileDialog where the user can choose a patient-file where events will be read from
			if (unsavedWarningSelection == 1) {
				showFileDialog();
			}
			
		} else if (e.getSource() == this.removeEventsButton) {
			System.out.println("Remove events");
			
			int[] selectedEvents = eventScriptTable.getSelectedRows();
			removeEvents(selectedEvents);
			//showSaveWarning = true;
			
		} else if (e.getSource() == this.editEventButton) {
			System.out.println("Edit event");
			List<String[]> triples = ModelHandler.getEventList();
			boolean printOutput = true;
			if (printOutput) {
				for (String[] triple : triples) {
					String output = "";
					int formatSize = 40;
					for (String s : triple) {
						output += String.format("%-" + formatSize + "s", s);
						formatSize = 10;
					}
					System.out.println(output);
				}
			}
			
		} else if (e.getSource() == this.saveScriptButton) {
			System.out.println("Save script");
			this.showSaveWarning = false;
		}
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
		
		this.showSaveWarning = false;
	}
	
	// Show a FileDialog where the user can choose a patient-file where events will be read from
	public void showFileDialog() {
		FileDialog fd = new FileDialog(jf, "Please choose patient-file:", FileDialog.LOAD);
		
		fd.setDirectory("C:\\");
		fd.setFile("*.ttl");
		fd.setVisible(true);
		
		String filename = fd.getFile();
		
		if (filename == null) {
			System.out.println("You cancelled the choice");
			return;
		} else {
			System.out.println("You chose " + filename);
			
			ModelHandler.setInputFileName(filename);
			ModelHandler.setModel(ModelHandler.readFile(OntModelSpec.OWL_DL_MEM));
			readEvents();
		}
	}
	
	@Override
	public void tableChanged(TableModelEvent e) {
		this.showSaveWarning = true;
	}
}
