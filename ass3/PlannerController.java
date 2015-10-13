package planner.gui;

import java.awt.event.*;
import java.io.IOException;
import planner.*;

/**
 * The controller for the Journey Planner.
 */
public class PlannerController {

	// the model that is being controlled
	private PlannerModel model;
	// the view that is being controlled
	private PlannerView view;
	// variable to determine whether to fire ItemListener
	// true = do not fire, false = fire
	private boolean start;

	/**
	 * Initialises the Controller for the Journey Planner.
	 */
	public PlannerController(PlannerModel model, PlannerView view) {
		this.model = model;
		this.view = view;
		// Initialise signal (true as no combo box item, so should not update
		// spinner)
		this.start = true;
		try {
			// Attempt to read timetable.txt
			model.readTimetable();
		} catch (IOException e) {
			// Error when reading file, disable controls and display error
			// message
			view.disableAllControls();
			view.setErrorOutput(e.getMessage());
		} catch (FormatException e) {
			// timetable.txt is malformed, disable controls and display error
			// message
			view.disableAllControls();
			view.setErrorOutput(e.getMessage());
		}
		// Listener for search button
		view.addSearchListener(new SearchActionListener());
		// Listener for interchange station combo box
		view.addModifyListener(new ModifyActionListener());
		// Listener for start station text field
		view.addStartStationListener(new StartStationActionListener());
		// Listener for end station text field
		view.addEndStationListener(new EndStationActionListener());
		// Listener for interchange station time spinner
		view.addModifyStationListener(new ModifyStationActionListener());
	}

	/**
	 * "Search" button listener.
	 * 
	 * @param e
	 *            the event when clicked
	 */
	private class SearchActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// Attempt to search for a journey
			model.findJourney(view.getStartStation(), view.getEndStation(),
					view.getTime());
			if (model.getJourney() != null) {
				// A journey is found, output to screen
				view.setOutput(model.getJourney().toString());

				if (model.getJourney().transfers() > 0) {
					// Has at least one interchange station
					// Signal ModifyStationActionListener not to fire when
					// updating
					start = true;
					// Update combo box with interchange stations
					view.setModifyStationList(model.getInterchangeStations());
					// Remove the signal
					start = false;
					// Set the minimum time of interchange station time spinner
					setTimeLimit();
					// Enable extra controls for modifying journey
					view.toggleSubControls(true);
				} else {
					// No interchange stations
					// Do not enable extra controls for modifying journey
					view.toggleSubControls(false);
				}
			} else {
				// No journey is found, output error message
				view.setErrorOutput("No journey found");
				// Do not enable extra controls for modifying journey
				view.toggleSubControls(false);
			}
		}
	}

	/**
	 * "Modify" button listener.
	 * 
	 * @param e
	 *            the event when clicked
	 */
	private class ModifyActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// Attempt to modify journey
			if (model.modifyJourney(view.getModifyStationIndex(),
					view.getModifyTime()) != null) {
				// Modification possible
				// Signal ModifyStationActionListener not to fire when
				// updating
				start = true;
				// Update combo box with interchange stations
				view.setModifyStationList(model.getInterchangeStations());
				// Remove the signal
				start = false;
				// Set the minimum time of interchange station time spinner
				setTimeLimit();
				// Output to screen
				view.setOutput(model.getJourney().toString());
			} else {
				// Modification impossible, output error message
				view.setErrorOutput("No journey found, please do not\n"
						+ "modify the original journey");
			}
		}
	}

	/**
	 * "Start Station" text field listener.
	 * 
	 * @param e
	 *            the event when keyboard key is pressed/released
	 */
	private class StartStationActionListener implements KeyListener {
		/**
		 * Listener for pressed keyboard keys
		 * 
		 * @param e
		 *            the event when keyboard key is pressed
		 */
		@Override
		public void keyPressed(KeyEvent e) {
			// Check if input fields are filled in
			toggleSearchButton();
		}

		/**
		 * Listener for released keyboard keys.
		 * 
		 * @param e
		 *            the event when keyboard key is released
		 */
		@Override
		public void keyReleased(KeyEvent e) {
			// Check if input fields are filled in
			toggleSearchButton();
		}

		/**
		 * Listener for typed keys.
		 * 
		 * @param e
		 *            the event when user typed something
		 */
		@Override
		public void keyTyped(KeyEvent e) {
			// Check if input fields are filled in
			toggleSearchButton();
		}

	}

	/**
	 * "End Station" text field listener.
	 * 
	 * @param e
	 *            the event when keyboard key is pressed/released
	 */
	private class EndStationActionListener implements KeyListener {
		/**
		 * Listener for pressed keyboard keys.
		 * 
		 * @param e
		 *            the event when keyboard key is pressed
		 */
		@Override
		public void keyPressed(KeyEvent e) {
			// Check if input fields are filled in
			toggleSearchButton();
		}

		/**
		 * Listener for released keyboard keys.
		 * 
		 * @param e
		 *            the event when keyboard key is released
		 */
		@Override
		public void keyReleased(KeyEvent e) {
			// Check if input fields are filled in
			toggleSearchButton();
		}

		/**
		 * Listener for typed keys.
		 * 
		 * @param e
		 *            the event when user typed something
		 */
		@Override
		public void keyTyped(KeyEvent e) {
			// Check if input fields are filled in
			toggleSearchButton();

		}
	}

	/**
	 * "Interchange Station" combo box listener.
	 * 
	 * @param e
	 *            the event when a combo box item is selected
	 * 
	 */
	private class ModifyStationActionListener implements ItemListener {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (!start) {
				// No signal to pause action
				// Update minimum time of "Departing Time" spinner
				setTimeLimit();
			}
		}
	}

	/**
	 * Method to check input fields and enable/disable "Search" button.
	 */
	private void toggleSearchButton() {
		// Enable "Search" button when both input fields are filled in
		view.toggleSearch(!view.getStartStation().equals("")
				&& !view.getEndStation().equals(""));
	}

	/**
	 * Method to set minimum value of "Departing Time" spinner.
	 */
	private void setTimeLimit() {
		// Set the minimum time for "Departing Time" spinner
		// based on current selected combo box item
		view.setModifyTimeLimit(model.getDepartTime(view
				.getModifyStationIndex()));
	}
}
