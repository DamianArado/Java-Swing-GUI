//STUDENT RECORD MANAGER GUI using Java Swing Toolkit

import java.util.Scanner;
import java.util.Vector;
import java.util.HashSet;
import java.util.Iterator;
import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.border.*;

//General Section
class PhoneInfo implements Serializable {
	String name;
	String phoneNumber;
	
	public PhoneInfo(String name, String num) {
		this.name = name;
		phoneNumber = num;
	}
	
	public void showPhoneInfo() {
		System.out.println("Name : " + name);
		System.out.println("Phone : " + phoneNumber);
	}
	
	public String toString() {
		return "Name : " + name +'\n' + "Phone : " + phoneNumber + '\n';
	}
	
	public int hashCode() {
		return name.hashCode();
	}
	
	public boolean equals(Object obj) {
		PhoneInfo cmp = (PhoneInfo)obj;
		if(name.compareTo(cmp.name) == 0)
			return true;
		else
			return false;
	}
}

//University Section
class PhoneUnivInfo extends PhoneInfo {
	String faculty;
	String enrolment;	
	
	public PhoneUnivInfo(String name, String number, String faculty, String enrolment) {
		super(name, number);
		this.faculty = faculty;
		this.enrolment = enrolment;
	}
	
	public void showPhoneInfo() {
		super.showPhoneInfo();
		System.out.println("Faculty no. : " + faculty);
		System.out.println("Enrolment no. : " + enrolment);
	}
	
	public String toString() {
		return super.toString() + "Faculty no. : " + faculty + '\n' + "Enrolment no. : " + enrolment + '\n';
	}
}

//Hostel Section
class PhoneHostelInfo extends PhoneInfo {
	String hostel;
	
	public PhoneHostelInfo(String name, String number, String hostel) {
		super(name, number);
		this.hostel = hostel;
	}
	
	public void showPhoneInfo() {
		super.showPhoneInfo();
		System.out.println("Hostel : "+ hostel);
	}
	
	public String toString() {
		return super.toString() + "Hostel : "+ hostel + '\n';
	}
}

//Storing records in hashset, working with file(used as a database here)
class StudentRecordManager {
	private final File dataFile = new File("StudentRecord.dat");
	HashSet<PhoneInfo> infoStorage = new HashSet<PhoneInfo>();
	
	static StudentRecordManager inst = null;
	public static StudentRecordManager createManagerInst() {
		if(inst == null)
			inst = new StudentRecordManager();
		return inst;
	}
	
	private StudentRecordManager() {
		readFromFile();
	}

	public String searchData(String name) {
		PhoneInfo info = search(name);
		if(info == null)
			return null;
		else
			return info.toString();
	}
	
	public boolean deleteData(String name) {	
		Iterator<PhoneInfo> itr = infoStorage.iterator();
		while(itr.hasNext()) {
			PhoneInfo currentInfo = itr.next();
			if(name.compareTo(currentInfo.name) == 0) {
				itr.remove();
				return true;
			}
		}
		return false;
	}
	
	private PhoneInfo search(String name) {
		Iterator<PhoneInfo> itr = infoStorage.iterator();
		while(itr.hasNext()) {
			PhoneInfo currentInfo = itr.next();
			if(name.compareTo(currentInfo.name) == 0)
				return currentInfo;
		}
		return null;
	}
	
	public void storeToFile() {
		try {
			FileOutputStream file = new FileOutputStream(dataFile);		
			ObjectOutputStream out = new ObjectOutputStream(file);
			Iterator<PhoneInfo> itr = infoStorage.iterator();
			while(itr.hasNext()) 
				out.writeObject(itr.next());			
			out.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void readFromFile() {
		if(dataFile.exists() == false)
			return;
		try {
			FileInputStream file = new FileInputStream(dataFile);		
			ObjectInputStream in = new ObjectInputStream(file);
			while(true) {
				PhoneInfo info = (PhoneInfo)in.readObject();
				if(info == null)
					break;
				infoStorage.add(info);
			}
			in.close();
		}
		catch(IOException e) {
			return;
		}
		catch(ClassNotFoundException e) {
			return;
		}
	}
}


//Searches for the name entered as a query string, shows all data under it, if exists.
class SearchEventHandler implements ActionListener {
	JTextField searchField;
	JTextArea textArea;
	
	public SearchEventHandler(JTextField field, JTextArea area) {
		searchField = field;
		textArea = area;
	}
	
	public void actionPerformed(ActionEvent e) {
		String name = searchField.getText();
		StudentRecordManager manager = StudentRecordManager.createManagerInst();
		String searchResult = manager.searchData(name);
		if(searchResult == null) {
			textArea.append("Search Failed : Info does not exist.\n");
		}
		else {
			textArea.append("Search Completed.\n");
			textArea.append(searchResult);
			textArea.append("\n");
		}
	}
}

//Adding event handler to various different fields
class AddEventHandler implements ActionListener {
	JTextField name;
	JTextField phone;
	JTextField faculty;
	JTextField enrolment;
	JTextField hostel;
	JTextArea text;
	Vector<String> inputList = new Vector<String>();
	
	boolean isAdded;
	
	PhoneInfo info;
	public AddEventHandler(JTextField nameField, JTextField phoneField, JTextField facultyField, JTextField enrolmentField, JTextArea textArea) {
		name = nameField;
		phone = phoneField;
		faculty = facultyField;
		enrolment = enrolmentField;
		text = textArea;
	}
	
	
	//Updating data(Update, here means adding data) according to the 3 sections viz. General, University and Hostel
	//If data already exists, it will not add that again
	public void actionPerformed(ActionEvent e) {
		StudentRecordManager manager = StudentRecordManager.createManagerInst();
		if(faculty.getText().equals("") == false && enrolment.getText().equals("") == true) {
			hostel = faculty;
			info = new PhoneHostelInfo(name.getText(), phone.getText(), hostel.getText());
			isAdded = manager.infoStorage.add(info);
		}		
		else if(faculty.getText().equals("") == false && enrolment.getText().equals("") == false) {
			info = new PhoneUnivInfo(name.getText(), phone.getText(), faculty.getText(), enrolment.getText());
			isAdded = manager.infoStorage.add(info);
		}
		else {
			info = new PhoneInfo(name.getText(), phone.getText());
			isAdded = manager.infoStorage.add(info);
		}
		
		if(isAdded) {
			text.append("Update Completed.\n");
		}
		else {
			text.append("Update Failed: Info already exists.\n");
		}
	}
}

//Deleting data only if it exists
class DeleteEventHandler implements ActionListener {
	JTextField deleteField;
	JTextArea textArea;
	
	public DeleteEventHandler(JTextField field, JTextArea area) {
		deleteField = field;
		textArea = area;
	}
	
	public void actionPerformed(ActionEvent e) {
		String name = deleteField.getText();
		StudentRecordManager manager = StudentRecordManager.createManagerInst();
		boolean isDeleted = manager.deleteData(name);
		if(isDeleted)
			textArea.append("Delete Completed.\n");
		else
			textArea.append("Delete Failed : Info never existed.\n");
	}
}

//Constructing the main frame
class MainFrame<setBackground> extends JFrame {
	JTextField searchField = new JTextField(15);
	JButton searchButton = new JButton("SEARCH");
	
	//Adding the relevant buttons with labels
	JButton addButton = new JButton("ADD");
	JRadioButton radioBtn1 = new JRadioButton("General");
	JRadioButton radioBtn2 = new JRadioButton("University");
	JRadioButton radioBtn3 = new JRadioButton("Hostel");
	ButtonGroup buttonGroup = new ButtonGroup();
	
	JLabel nameLabel = new JLabel("NAME");
	JTextField nameField = new JTextField(15);
	JLabel phoneLabel = new JLabel("PHONE NUMBER");
	JTextField phoneField = new JTextField(15);
	JLabel facultyLabel = new JLabel("FACULTY");
	JTextField facultyField = new JTextField(15);
	JLabel enrolmentLabel = new JLabel("ENROLMENT");
	JTextField enrolmentField = new JTextField(15);
	
	JTextField deleteField = new JTextField(15);
	JButton deleteButton = new JButton("DELETE");
	
	JTextArea textArea = new JTextArea(10, 25);

	public MainFrame(String title) {
		super(title);
		
		//Creating the layout
		setBounds(100, 200, 330, 450);
		setSize(730,350);
		setLayout(new GridLayout(0,2,0,0));
		Border border = BorderFactory.createEtchedBorder();
		
		//Search section
		Border searchBorder = BorderFactory.createTitledBorder(border, "Search by Name");
		//Creating layout, adding components
		JPanel searchPanel = new JPanel();
		searchPanel.setBorder(searchBorder);
		searchPanel.setBackground(Color.white);
		searchPanel.setLayout(new FlowLayout());
		
		//Adding fields and buttons
		searchPanel.add(searchField);
		searchPanel.add(searchButton);
		
		//Add section
		Border addBorder = BorderFactory.createTitledBorder(border, "Add Record");
		//Creating layout, adding components
		JPanel addPanel = new JPanel();
		addPanel.setBorder(addBorder);
		addPanel.setBackground(Color.white);
		addPanel.setLayout(new FlowLayout());
		
		JPanel addInputPanel = new JPanel();
		addInputPanel.setLayout(new GridLayout(0,2,5,5));
		
		buttonGroup.add(radioBtn1);
		buttonGroup.add(radioBtn2);
		buttonGroup.add(radioBtn3);
		
		addPanel.add(radioBtn1);
		addPanel.add(radioBtn2);
		addPanel.add(radioBtn3);
		addPanel.add(addButton);
		
		addInputPanel.add(nameLabel);
		addInputPanel.add(nameField);
		addInputPanel.add(phoneLabel);
		addInputPanel.add(phoneField);
		addInputPanel.add(facultyLabel);
		addInputPanel.add(facultyField);
		addInputPanel.add(enrolmentLabel);
		addInputPanel.add(enrolmentField);
		
		facultyLabel.setVisible(false);
		facultyField.setVisible(false);
		enrolmentLabel.setVisible(false);
		enrolmentField.setVisible(false);
		
		radioBtn1.setSelected(true);
		addPanel.add(addInputPanel);
		
		//Visibility only according to the particular section
		radioBtn1.addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent e) {
						if(e.getStateChange() == ItemEvent.SELECTED) {
							facultyLabel.setVisible(false);
							facultyField.setVisible(false);
							enrolmentLabel.setVisible(false);
							enrolmentField.setVisible(false);			
							facultyField.setText("");
							enrolmentField.setText("");
						}
					}
				}
		);
		
		radioBtn2.addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent e) {
						if(e.getStateChange() == ItemEvent.SELECTED) {
							facultyLabel.setVisible(true);
							facultyLabel.setText("FACULTY");
							facultyField.setVisible(true);
							enrolmentLabel.setVisible(true);
							enrolmentField.setVisible(true);
						}
					}
				}
		);
		
		radioBtn3.addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent e) {
						if(e.getStateChange() == ItemEvent.SELECTED) {
							facultyLabel.setVisible(true);
							facultyLabel.setText("HOSTEL");
							facultyField.setVisible(true);
							enrolmentLabel.setVisible(false);
							enrolmentField.setVisible(false);
							enrolmentField.setText("");
						}
					}
				}
		);
		
		//Delete section
		Border deleteBorder = BorderFactory.createTitledBorder(border, "Delete by Name");
		//Creating layout, adding components
		JPanel deletePanel = new JPanel();
		deletePanel.setBorder(deleteBorder);
		deletePanel.setLayout(new FlowLayout());
		deletePanel.add(deleteField);
		deletePanel.add(deleteButton);
		deletePanel.setBackground(Color.white);
		
		JScrollPane scrollTextArea = new JScrollPane(textArea);	
		
		//INFORMATION BOARD section
		Border textBorder = BorderFactory.createTitledBorder(border, "INFORMATION BOARD");
		scrollTextArea.setBorder(textBorder);
		scrollTextArea.setBackground(Color.white);
		
		//Creating layout, adding components
		JPanel actionPanel = new JPanel();
		actionPanel.setLayout(new BorderLayout());
		actionPanel.add(searchPanel, BorderLayout.NORTH);
		actionPanel.add(addPanel, BorderLayout.CENTER);
		actionPanel.add(deletePanel, BorderLayout.SOUTH);
		
		add(actionPanel);
		add(scrollTextArea);
		
		//Attaching event listeners to buttons
		searchButton.addActionListener(new SearchEventHandler(searchField, textArea));
		addButton.addActionListener(new AddEventHandler(nameField, phoneField, facultyField, enrolmentField, textArea));
		deleteButton.addActionListener(new DeleteEventHandler(deleteField, textArea));
		
		setVisible(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);	
		
	}
}

class StudentRecord {	
	public static void main(String[] args) {
		StudentRecordManager manager = StudentRecordManager.createManagerInst();
		MainFrame winFrame = new MainFrame("STUDENT RECORD MANAGER by Humayun");
	}
}