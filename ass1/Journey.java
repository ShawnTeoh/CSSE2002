package planner;

import java.util.*;

/**
 * A mutable class representing a journey.
 * 
 * A journey has a non-empty sequence of journey legs. Each successive leg of a
 * journey must depart from the same station that its predecessor arrives at, at
 * a time no earlier than its arrival time at that station.
 * 
 * No two adjacent legs of the journey should be from the same service (since
 * they would be considered to be part of the same leg).
 */

public class Journey implements Iterable<Leg> {

	private ArrayList<Leg> legs = new ArrayList<Leg>();
	private int totalTravelTime;

	/*
	 * Invariant: legs != null && totalTravelTime > 0 && transfers() > 0 &&
	 * elements of legs.get(i) are not null && legs.get(i).startStation() is not
	 * null && legs.get(i).service() is not null && legs.get(i).endStation() is
	 * not null && traveling from legs.get(i).startStation() to
	 * legs.get(i).endStation() is valid
	 */

	/**
	 * Creates a new journey consisting of a single leg from startStation to
	 * endStation using the given service.
	 * 
	 * @param startStation
	 *            the start station of the first leg of the journey.
	 * @param endStation
	 *            the end station of the first leg of the journey.
	 * @param service
	 *            the service that is taken from start to end in the first leg.
	 * 
	 * @throws NullPointerException
	 *             if any of the parameters are null.
	 * @throws InvalidJourneyException
	 *             if the parameters are not null, but the route taken by the
	 *             given service cannot be used to travel from the startStation
	 *             to the endStation (that includes the case where the stations
	 *             are equivalent).
	 */
	public Journey(Station startStation, Station endStation, Service service) {
		// Verify parameters
		if ((startStation == null) || (endStation == null) || service == null) {
			// Parameters cannot be null
			throw new NullPointerException("Parameters cannot be null");
		} else if (!service.route().canTravelFrom(startStation, endStation)) {
			// Invalid attempt to travel from startStation to endStation
			throw new InvalidJourneyException("Cannot travel from "
					+ startStation.toString() + " to " + endStation.toString()
					+ " using route " + service.route().name());
		}
		// Initialise class variables
		// Add first element to legs
		legs.add(new Leg(startStation, endStation, service));
		updateTotalTravelTime(); // Update total travel time
	}

	/**
	 * Returns the departure time of the first leg of the journey.
	 * 
	 * @return the time that the journey starts.
	 */
	public int startTime() {
		// Utilise startTime method of Leg to get starting time of first leg
		return legs.get(0).startTime();
	}

	/**
	 * Returns the arrival time of the last leg of the journey.
	 * 
	 * @return the time that the journey ends.
	 */
	public int endTime() {
		// Utilise endTime method of Leg to get ending time of last leg
		return legs.get(legs.size() - 1).endTime();
	}

	/**
	 * Returns the station that the first leg of the journey departs from.
	 * 
	 * @return the station that the journey starts at.
	 */
	public Station startStation() {
		// Utilise startStation method of Leg to get starting station
		return legs.get(0).startStation();
	}

	/**
	 * Returns the station that the last leg of the journey arrives at.
	 * 
	 * @return the station that the journey ends at.
	 */
	public Station endStation() {
		// Utilise endStation method of Leg to get ending station
		return legs.get(legs.size() - 1).endStation();
	}

	/**
	 * Returns the number of transfers in the journey from one service to
	 * another.
	 * 
	 * @return the number of transfers in the journey.
	 */
	public int transfers() {
		// Number of transfers equals length of legs minus 1
		return legs.size() - 1;
	}

	/**
	 * Returns the total travel time for the journey. That is, the difference
	 * between the start and end time of the journey.
	 * 
	 * @return the total travel time.
	 */
	public int totalTravelTime() {
		// No processing needed, already an int
		return totalTravelTime;
	}

	/**
	 * Extends the journey by catching the given service from the last station
	 * currently in the journey to nextStation.
	 * 
	 * If the last leg of the journey was also on the same service (or a service
	 * that is equivalent using the equals method), then that leg of the journey
	 * should be replaced by one that extends that last leg to travel further to
	 * the new destination, nextStation.
	 * 
	 * If the last leg of the journey was on a different service, then a new leg
	 * should be added to the journey that uses service to get from the last
	 * station currently in the journey to nextStation.
	 * 
	 * @param service
	 *            the service to take
	 * @param nextStation
	 * @throws NullPointerException
	 *             if either parameter is null
	 * @throws InvalidJourneyException
	 *             if the the service cannot be used to travel from the last
	 *             station currently in the journey to the station next, by
	 *             departing at a time no earlier than the current end time of
	 *             the journey; or if the nextStation is equal to the last
	 *             station currently in the journey.
	 */
	public void extendJourney(Service service, Station nextStation) {
		// Verify parameters
		if (service == null || nextStation == null) {
			// Parameters cannot be null
			throw new NullPointerException("Parameters cannot be null");
		} else if (!service.canTravelFrom(endStation(), nextStation,
				legs.get(legs.size() - 1).endTime())) {
			// Cannot use service to travel to nextStation
			throw new InvalidJourneyException("Cannot use route "
					+ service.route().name() + " to travel to "
					+ nextStation.toString());
		} else if (endStation().equals(nextStation)) {
			// nextStation cannot be the same as last station of last leg
			throw new InvalidJourneyException(
					"nextStation is the same as current last station");
		}

		// Check if service equals to service of last leg
		if (legs.get(legs.size() - 1).service().equals(service)) {
			// Same service, will remove current last leg and
			// replace with new one
			Station startStation = legs.get(legs.size() - 1).startStation();
			// Remove last leg
			legs.remove(legs.size() - 1);
			// Append new leg to legs
			legs.add(new Leg(startStation, nextStation, service));
			// Update travel time
			updateTotalTravelTime();
		} else {
			// Different service, so append new leg to legs
			legs.add(new Leg(legs.get(legs.size() - 1).endStation(),
					nextStation, service));
			// Update travel time
			updateTotalTravelTime();
		}
	}

	/**
	 * For a journey with N legs, this method returns a string:
	 * 
	 * "Total travel time: TT" + "\t" + "Transfers: TR" + LS + LEG1 + LS + ... +
	 * LEGN + LS
	 * 
	 * where TT is the total travel time for this journey; TR is the number of
	 * transfers; LEGi is the string representation of the ith Leg of the
	 * journey, and LS is the line separator string retrieved in a
	 * machine-independent way by calling the function
	 * System.getProperty("line.separator").
	 */
	@Override
	public String toString() {
		// Machine independent newline character
		String LS = System.getProperty("line.separator");
		String output = ""; // Emptry String
		// Append total travel time and number of transfers to output
		// Object are directly used because toString method already declared
		output += "Total travel time: " + totalTravelTime + "\t"
				+ "Transfers: " + transfers() + LS;
		// Loop through leg list and append description of legs to output
		for (Leg i : this) {
			output += i + LS;
		}
		return output;
	}

	/**
	 * Returns an iterator over the legs in the journey.
	 */
	@Override
	public Iterator<Leg> iterator() {
		// Return iterator method of a read-only leg list
		// This is to avoid modification of legs
		return Collections.unmodifiableList(legs).iterator();
	}

	/**
	 * Determines whether this instance of the class is internally consistent.
	 * 
	 * @return true if this instance is internally consistent, and false
	 *         otherwise
	 */
	public boolean checkInv() {
		if (legs == null) {
			return false; // legs cannot be null
		} else if (totalTravelTime < 0) {
			return false; // Total travel time cannot be less than zero
		} else if (transfers() < 0) {
			return false; // Total transfers cannot be less than zero
		}

		// Loops through every element in legs
		for (int i = 0; i < legs.size(); i++) {
			if (legs.get(i) == null) {
				return false; // Elements of legs cannot be null
			} else if (legs.get(i).startStation() == null
					|| legs.get(i).endStation() == null
					|| legs.get(i).service() == null) {
				return false; // Methods of Legs cannot return null
			} else if (!checkPossibleTrip(i)) {
				return false; // Trip not possible
			}
		}

		return true; // Invariant not violated
	}

	// Journey helper methods

	/**
	 * Updates total travel time.
	 */
	private void updateTotalTravelTime() {
		// Total travel time is endTime() of last legs element
		// minus startTime() of first legs element
		totalTravelTime = legs.get(legs.size() - 1).endTime()
				- legs.get(0).startTime();
	}

	/**
	 * Checks if possible to travel from legs.get(i).startStation() to
	 * legs.get(i).endStation()
	 * 
	 * @param i
	 *            the index for leg list
	 * @return true if trip is possible
	 */
	private boolean checkPossibleTrip(int i) {
		// Current service
		Service service = legs.get(i).service();
		// Starting station
		Station startStation = legs.get(i).startStation();
		// Ending station
		Station endStation = legs.get(i).endStation();
		if (i > 0) {
			// Not first leg, can check for continuation of
			// service using time
			// Return true iff possible to travel from i.startStation() to
			// i.endStation()
			int time = legs.get(i - 1).endTime();
			return service.canTravelFrom(startStation, endStation, time);
		} else {
			// First leg, check for continuation of
			// service without using time
			// Return true iff possible to travel from i.startStation() to
			// i.endStation()
			return service.route().canTravelFrom(startStation, endStation);
		}
	}
}
