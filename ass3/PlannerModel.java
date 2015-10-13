package planner.gui;

import java.io.IOException;
import java.util.*;
import planner.*;

/**
 * The model for the Journey Planner.
 */
public class PlannerModel {

	// the found journey
	private Journey foundJourney;
	// map representation of timetable.txt
	private Map<Route, List<Service>> timetable;
	// array of legs in foundJourney
	private Leg[] legs;

	/**
	 * Initialises the model for the Journey Planner.
	 */
	public PlannerModel() {
		// No journey found yet
		foundJourney = null;
	}

	/**
	 * Read timetable.txt and populates timetable map.
	 * 
	 * @throws IOException
	 *             if error faced when reading timetable.txt
	 * @throws FormatException
	 *             if timetable.txt is malformed
	 */
	public void readTimetable() throws IOException, FormatException {
		timetable = TimetableReader.read("timetable.txt");
	}

	/**
	 * Searches for a journey.
	 * 
	 * @param startStation
	 *            departing station
	 * @param endStation
	 *            arriving station
	 * @param time
	 *            departure time
	 */
	public void findJourney(String startStation, String endStation, int time) {
		// Attempt to search for a journey, foundJourney will be updated with a
		// journey if one is found, or else it remains null
		foundJourney = JourneyFinder.findJourney(new Station(startStation),
				new Station(endStation), time, timetable);
	}

	/**
	 * Returns the found journey.
	 * 
	 * @return the found journey
	 */
	public Journey getJourney() {
		return foundJourney;
	}

	/**
	 * Modifies foundJourney based on manipulation of a specified interchange
	 * station's departure time.
	 * 
	 * @param num
	 *            index of last unmodified leg
	 * @param time
	 *            the new departure time of specified interchange station
	 * @return the modified journey (null if cannot be modified)
	 */
	public Journey modifyJourney(int num, int time) {
		// Extract the unmodified part of foundJourney into a new Journey
		// Initialise the new journey
		Journey modifiedJourney = new Journey(legs[0].startStation(),
				legs[0].endStation(), legs[0].service());
		// Loop through every leg in foundJourney
		for (int i = 1; i < legs.length; i++) {
			if (i == num + 1) {
				// Stop if past index of last unmodified leg
				break;
			}
			// Add unmodified leg to modifiedJourney
			modifiedJourney.extendJourney(legs[i].service(),
					legs[i].endStation());
		}
		// Attempt to search for a new journey starting from interchange station
		// specified (but still same arriving station) and new departure time of
		// interchange station

		// Interchage station = startStation of modified leg
		Journey attemptJourney = JourneyFinder.findJourney(
				legs[num].endStation(), legs[legs.length - 1].endStation(),
				time, timetable);

		if (attemptJourney != null) {
			// A journey is found, able to modify the journey
			// Loop through every leg in attempted journey and add to
			// modifiedJourney
			for (Leg l : attemptJourney) {
				modifiedJourney.extendJourney(l.service(), l.endStation());
			}
			// Update foundJourney with modifiedJourney
			foundJourney = modifiedJourney;
			// Returns the modified journey
			return modifiedJourney;
		} else {
			// No journey found, cannot modify journey
			return null;
		}
	}

	/**
	 * Creates and return an array of interchange stations in foundJourney.
	 * 
	 * @return array of all interchange stations
	 */
	public String[] getInterchangeStations() {
		// Make sure legs is polulated
		createLegArray();
		// Initialise the output array
		String[] output = new String[foundJourney.transfers()];
		// Loop through every leg in legs
		int i = 0;
		for (Leg l : legs) {
			if (i == foundJourney.transfers()) {
				// Skip the last leg
				continue;
			}
			// Interchange station = end station of each leg
			output[i] = l.endStation().toString();
			i++;
		}
		// Returns the array
		return output;
	}

	/**
	 * Returns the departure time of the specified interchange station.
	 * 
	 * @param num
	 *            index of leg
	 * @return departure time of interchange station
	 */
	public int getDepartTime(int num) {
		return legs[num + 1].startTime();
	}

	/**
	 * Populates legs with those in foundJourney.
	 */
	private void createLegArray() {
		// Number of legs = number of transfers + 1
		legs = new Leg[foundJourney.transfers() + 1];
		// Loop through every leg in foundJourney and add to legs
		int i = 0;
		for (Leg l : foundJourney) {
			legs[i] = l;
			i++;
		}
	}

}
