package planner;

import java.util.*;

/**
 * An immutable class for representing a route (such as a bus route).
 * 
 * A route has a name and a sequence of at least two stations, that it stops at
 * in sequence order. A route may not stop at the same station more than once.
 */
public class Route {

	private String name; // Name of route
	private List<Station> stations; // List of stations available in route

	/*
	 * Invariant: name != null && stations != null && elements of
	 * stations.get(i) are not null && stations.size() >= 2 && no duplicates in
	 * stations
	 */

	/**
	 * Creates a route with the given name, that stops at each station in the
	 * given list of stations, in the order in which they appear in that list.
	 * 
	 * @param name
	 *            the name of the route.
	 * @param stations
	 *            a list of stations that the route will stop at.
	 * @throws NullPointerException
	 *             if either name or stations is null or any of the stations are
	 *             null.
	 * @throws InvalidRouteException
	 *             if stations is not null and none of its stations are null,
	 *             but it contains the same station more than once or consists
	 *             of less than two stations
	 */
	public Route(String name, List<Station> stations) {
		// Verify parameters
		if ((name == null) || (stations == null)) {
			// Parameters cannot be null
			throw new NullPointerException("Parameters cannot be null");
		} else if (checkNullElements(stations)) {
			// Elements of stations cannot be null
			throw new NullPointerException(
					"stations cannot contain null elements");
		} else if ((stations.size() < 2)) {
			// Number of stations cannot be less than 2
			throw new InvalidRouteException(
					"Number of stations cannot be less than 2");
		} else if (checkDuplicateElements(stations)) {
			// Elements of stations cannot be repeated
			throw new InvalidRouteException("Cannot have duplicate stations");
		}

		// Initialise class variables
		this.name = name;
		this.stations = stations;
	}

	/**
	 * Returns the name of the route.
	 * 
	 * @return the name of the route.
	 */
	public String name() {
		// No processing needed, already a String
		return name;
	}

	/**
	 * Returns the number of stops made on this route.
	 * 
	 * @return the number of stops made on this route.
	 */
	public int numStops() {
		// Number of elements in stations == number of stops
		return stations.size();
	}

	/**
	 * Returns true if this route stops at the given station (i.e a Station
	 * object equal to the given parameter) and false otherwise.
	 * 
	 * @param station
	 *            the station to look for in the route.
	 * @return true iff this route stops at station.
	 */
	public boolean stopsAt(Station station) {
		// The contains method of List checks if an element exists and returns
		// true if it does, vice versa
		return stations.contains(station);
	}

	/**
	 * If the route stops at station, this method returns the stop number of
	 * station in this route, otherwise throws a NoSuchStopException. (Stop
	 * numbers are counted from 1.)
	 * 
	 * @param station
	 *            the station whose stop number will be returned.
	 * @return the stop number of this route.
	 * @throws NoSuchStopException
	 *             if this route does not stop at the given station.
	 */
	public int getStopNumber(Station station) {
		if (!stopsAt(station)) {
			throw new NoSuchStopException(station.toString()
					+ " is not in route " + name); // Station does not exist
		}
		// Must add 1 to index because starts at 0, stop number starts at 1
		return stations.indexOf(station) + 1;
	}

	/**
	 * If there is a stop number i on this route (i.e. if 1<=i<= numStops()),
	 * then this method returns the station that is visited on the ith stop of
	 * this route, otherwise it throws a NoSuchStopException. (Note that the
	 * first stop is referred to as stop number 1, not stop number 0.)
	 * 
	 * @param i
	 *            the stop number to retrieve the station for
	 * @return the station that is visited on the ith stop of this route.
	 * @throws NoSuchStopException
	 *             if there is no ith stop on this route.
	 */
	public Station getStop(int i) {
		try {
			// Attempt to get station using stop number
			// Minus 1 to be used as List index
			return stations.get(i - 1);
		} catch (IndexOutOfBoundsException e) {
			// Index does not exist, stop number does not as well
			throw new NoSuchStopException("Stop number " + i
					+ "does not exist in route " + name);
		}
	}

	/**
	 * Returns true iff s1 and s2 are different stops on this route and the
	 * route can be used to travel from station s1 to s2.
	 * 
	 * @param s1
	 *            the station to travel from.
	 * @param s2
	 *            the station to travel to from s1.
	 * @return true iff s1 and s2 are different stops on this route and the
	 *         route can be used to travel from station s1 to s2.
	 */
	public boolean canTravelFrom(Station s1, Station s2) {
		try {
			if (s1.equals(s2)) {
				return false; // Cannot travel to and from the same station
			} else if (getStopNumber(s1) < getStopNumber(s2)) {
				return true; // Stations in ascending order, possible trip
			}
		} catch (NoSuchStopException e) {
			// Attempted to travel from non existent station, false
			return false;
		}
		return false;
	}

	/**
	 * For a Route with name X and N stops, this method returns a string of the
	 * form
	 * 
	 * "X: S1, ... , SN"
	 * 
	 * where Si is the station that is visited in the ith stop of the route, and
	 * "S1, ... , SN" is short-hand for a comma-separated list of all of those
	 * stations.
	 * 
	 * (Note the use of a single space character after the colon, and after each
	 * comma in the comma-separated list of stops.)
	 */
	@Override
	public String toString() {
		String output = ""; // Empty String
		// Loops through station list and append to output
		// Object are directly used because toString method already declared
		for (Station i : stations) {
			output += i;
			if (stations.indexOf(i) != numStops() - 1) {
				output += ", "; // Not last stop, append comma
			}
		}
		// Prepend route name to output
		output = name + ": " + output;
		return output;
	}

	/**
	 * Two routes are equal if they have equivalent names and they visit the
	 * same sequence of stations.
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Route)) {
			return false; // Not Route object
		}

		Route r = (Route) obj;
		// True iff same name and same list of stations
		return name.equals(r.name) && stations.equals(r.stations);
	}

	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * 37 + name.hashCode();
		hash = hash * 47 + stations.hashCode();
		return hash;
	}

	/**
	 * Determines whether this route is internally consistent.
	 * 
	 * @return true if this route is internally consistent, and false otherwise
	 */
	public boolean checkInv() {
		if (name == null || stations == null) {
			return false; // Null class variables
		} else if (checkNullElements(stations)) {
			return false; // Null elements in station list
		} else if (checkDuplicateElements(stations)) {
			return false; // Duplicate elements in station list
		} else if (stations.size() < 2) {
			return false; // Less than 2 stations
		} else {
			return true; // Invariant not violated
		}
	}

	// Route helper methods

	/**
	 * Checks for duplicate stations.
	 * 
	 * @param stations
	 *            List of stations
	 * @return true if there are duplicates, and false otherwise
	 */
	private boolean checkDuplicateElements(List<Station> stations) {
		// Creates a hashset to hold station names as looping through the list.
		// Hashset is used because sets do not allow duplicates and returns a
		// false value if duplicate given
		HashSet<Station> hashset = new HashSet<Station>();
		for (Station i : stations) {
			if (!hashset.add(i)) {
				return true; // Contains duplicates
			}
		}
		return false; // All stations are unique
	}

	/**
	 * Checks for null elements.
	 * 
	 * @param stations
	 *            List of stations
	 * @return true if there are null elements, and false otherwise
	 */
	private boolean checkNullElements(List<Station> stations) {
		// Loops through every element
		for (Station i : stations) {
			if (i == null) {
				return true; // Contains null elements
			}
		}
		return false; // No null elements
	}
}
