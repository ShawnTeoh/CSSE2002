package planner.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * The view for the Journey Planner.
 */
@SuppressWarnings("serial")
public class PlannerView extends JFrame {

	// the model of the Journey Planner
	private PlannerModel model;
	// input field for starting station (text field)
	private JTextField startStation;
	// input field for ending station (text field)
	private JTextField endStation;
	// input field for departing time (spinner)
	private JSpinner time;
	// button to execute search
	private JButton search;
	// list of available interchange stations (combo box)
	private JComboBox<String> modifyStation;
	// input field to modify departing time of interchange station (spinner)
	private JSpinner modifyTime;
	// button to execute modification
	private JButton modify;
	// output area (text area)
	private JTextArea output;

	/**
	 * Creates a new Journey Planner window.
	 */
	public PlannerView(PlannerModel model) {
		this.model = model;
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		// Set the window title
		setTitle("Journey Planner");
		// Position and set initial size of window
		setBounds(400, 200, 600, 300);
		// Get frame
		Container c = getContentPane();
		// Use GridLayout for frame
		c.setLayout(new GridLayout(1, 2));
		// Add in controls
		addControls(c);
		// Disable "Search" button, no input yet
		toggleSearch(false);
		// No journey found yet, disable extra controls for modifying journey
		toggleSubControls(false);
		// Add output area
		addOutput(c);
	}

	/**
	 * Helper to add controls for user interaction.
	 * 
	 * @param c
	 *            container to add items
	 */
	private void addControls(Container c) {
		// Initialise a new panel
		JPanel p = new JPanel();
		// Set layout of panel to GridBagLayout (more flexible)
		p.setLayout(new GridBagLayout());
		// Make the panel stand out
		p.setBorder(BorderFactory.createBevelBorder(0));
		// Initialise constants for positioning items in GridBagLayout
		GridBagConstraints gbc = new GridBagConstraints();
		// We want items to take up as much space as possible
		gbc.fill = GridBagConstraints.BOTH;
		// Ensure items do not pack in the center, expand as far as possible
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		// Add some padding between items
		gbc.insets = new Insets(5, 0, 5, 0);

		// Primary controls

		// Initialise a new text field for starting station
		startStation = new JTextField();
		// Initialise a new text field for ending station
		endStation = new JTextField();
		// Initialise a new spinner for departure time
		// By default, spinner only accepts integers, saves non-integer check
		time = new JSpinner();
		// Set minimum input as 0, no negative values are accepted
		((SpinnerNumberModel) time.getModel()).setMinimum(0);
		// Initialise a new button to perform search action
		search = new JButton("Search");

		// Extra controls

		// Initialise a new text field for interchange stations
		modifyStation = new JComboBox<String>();
		// Initialise a new spinner for interchange station departure time
		modifyTime = new JSpinner();
		// Initialise a new button to perform modify action
		modify = new JButton("Modify");

		// Add in primary controls to panel

		// Add "Start Station" label
		gbc.gridx = 0;
		gbc.gridy = 0;
		p.add(new JLabel("Start Station"), gbc);
		// Add "Start Station" input field
		gbc.gridx = 1;
		p.add(startStation, gbc);
		// Add "End Station" label
		gbc.gridx = 0;
		gbc.gridy = 1;
		p.add(new JLabel("End Station"), gbc);
		// Add "End Station" input field
		gbc.gridx = 1;
		p.add(endStation, gbc);
		// Add "Travel After" label
		gbc.gridx = 0;
		gbc.gridy = 2;
		p.add(new JLabel("Travel after"), gbc);
		// Add "Travel After" spinner
		gbc.gridx = 1;
		p.add(time, gbc);
		// Add "Search" button
		gbc.gridx = 0;
		gbc.gridy = 3;
		// Position button in middle of panel
		gbc.insets = new Insets(10, 50, 10, 50);
		gbc.gridwidth = 2;
		p.add(search, gbc);

		// Add in extra controls to panel

		// Add "Interchange Station" label
		gbc.gridy = 4;
		gbc.insets = new Insets(5, 0, 5, 0);
		gbc.gridwidth = 1;
		p.add(new JLabel("Interchange Station"), gbc);
		// Add "Interchange Station" combo box
		gbc.gridx = 1;
		p.add(modifyStation, gbc);
		// Add "Departing Time" label
		gbc.gridx = 0;
		gbc.gridy = 5;
		p.add(new JLabel("Departing Time"), gbc);
		// Add "Departing Time" spinner
		gbc.gridx = 1;
		p.add(modifyTime, gbc);
		// Add "Modify" button
		gbc.gridx = 0;
		gbc.gridy = 6;
		// Position button in middle of panel
		gbc.insets = new Insets(10, 50, 10, 50);
		gbc.gridwidth = 2;
		p.add(modify, gbc);

		// Add panel to container
		c.add(p);
	}

	/**
	 * Helper to add area to display results.
	 * 
	 * @param c
	 *            container to add items
	 */
	private void addOutput(Container c) {
		// Initialise text area with instructions
		// '\n' is hard-coded because it is used by the text system internally
		// to represent newlines, so it is OS independent
		output = new JTextArea(
				"Please fill in the fields.\n\nButton will be enabled after the"
						+ "\nrequired fields are filled in.");
		// Text area is only used for displaying messages
		output.setEditable(false);

		// Initialise a new scrollable panel, with text area in it
		JScrollPane p = new JScrollPane(output);

		// Add panel to container
		c.add(p);
	}

	/**
	 * Enables/disables extra controls for modifying a journey
	 * 
	 * @param toggle
	 *            state of controls
	 */
	public void toggleSubControls(boolean toggle) {
		// true to enable, false to disable
		modifyStation.setEnabled(toggle);
		modifyTime.setEnabled(toggle);
		modify.setEnabled(toggle);
	}

	/**
	 * Disables all controls that accept user input.
	 */
	public void disableAllControls() {
		// Disable all primary controls
		startStation.setEnabled(false);
		endStation.setEnabled(false);
		time.setEnabled(false);
		search.setEnabled(false);
		// Disable extra controls
		toggleSubControls(false);
	}

	/**
	 * Enables/Disables the "Search" button.
	 * 
	 * @param toggle
	 *            state of search button
	 */
	public void toggleSearch(boolean toggle) {
		// true to enable, false to disable
		search.setEnabled(toggle);
	}

	/**
	 * Returns the starting station given by user.
	 * 
	 * @return starting station
	 */
	public String getStartStation() {
		// Get input from text field
		return startStation.getText();
	}

	/**
	 * Returns the ending station given by user.
	 * 
	 * @return ending station
	 */
	public String getEndStation() {
		// Get input from text field
		return endStation.getText();
	}

	/**
	 * Returns the departure time given by user.
	 * 
	 * @return departure time
	 */
	public int getTime() {
		// Get time as integer
		return (int) time.getValue();
	}

	/**
	 * Returns the interchange station (selected item in combo box) selected by
	 * user.
	 * 
	 * @return index of selected item
	 */
	public int getModifyStationIndex() {
		// Get index of selected item
		return modifyStation.getSelectedIndex();
	}

	/**
	 * Returns the interchange station departure time given by user.
	 * 
	 * @return the interchange station departure time as integer
	 */
	public int getModifyTime() {
		// Get time as integer
		return (int) modifyTime.getValue();
	}

	/**
	 * Set the minimum time of the interchange station departure time spinner.
	 * 
	 * @param time
	 *            the minimum time
	 */
	public void setModifyTimeLimit(int time) {
		// Set the minimum to time + 1, no point of setting at time as will
		// return same result
		((SpinnerNumberModel) modifyTime.getModel()).setMinimum(time + 1);
		// Initialise the value at time + 1, this avoids previous inputs which
		// might be invalid
		((SpinnerNumberModel) modifyTime.getModel()).setValue(time + 1);
	}

	/**
	 * Update the interchange station combo box entries.
	 * 
	 * @param stations
	 *            the array of interchange station names
	 */
	public void setModifyStationList(String[] stations) {
		// Clear the old entries
		modifyStation.removeAllItems();
		// Add in new entries
		for (String s : stations) {
			modifyStation.addItem(s);
		}
	}

	/**
	 * Displays a normal output message in text area.
	 * 
	 * @param s
	 *            message to display
	 */
	public void setOutput(String s) {
		// Default output, show in black
		output.setForeground(Color.BLACK);
		// Display the message
		output.setText(s);
	}

	/**
	 * Displays an error message in text area.
	 * 
	 * @param s
	 *            message to display
	 */
	public void setErrorOutput(String s) {
		// Error output, highlight in red
		output.setForeground(Color.RED);
		// Display the message
		output.setText(s);
	}

	/**
	 * Binds "Search" button to an action listener.
	 * 
	 * @param pl
	 *            the action listener
	 */
	public void addSearchListener(ActionListener pl) {
		// Register listener to "Search" button
		search.addActionListener(pl);
	}

	/**
	 * Binds "Modify" button to an action listener.
	 * 
	 * @param pl
	 *            the action listener
	 */
	public void addModifyListener(ActionListener pl) {
		// Register listener to "Modify" button
		modify.addActionListener(pl);
	}

	/**
	 * Binds "Start Station" field to a key listener.
	 * 
	 * @param pl
	 *            the key listener
	 */
	public void addStartStationListener(KeyListener pl) {
		// Register listener to "Start Station" field
		startStation.addKeyListener(pl);
	}

	/**
	 * Binds "End Station" field to a key listener.
	 * 
	 * @param pl
	 *            the key listener
	 */
	public void addEndStationListener(KeyListener pl) {
		// Register listener to "End Station" field
		endStation.addKeyListener(pl);
	}

	/**
	 * Binds "Interchange Station" combo box to an item listener.
	 * 
	 * @param pl
	 *            the item listener
	 */
	public void addModifyStationListener(ItemListener pl) {
		// Register listener to "Interchange Station" combo box
		modifyStation.addItemListener(pl);
	}

}
