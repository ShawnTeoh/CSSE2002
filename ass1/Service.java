package planner;

import java.util.*;

/**
 * An immutable class for representing a service on a particular route.
 * 
 * Each service has a departure time from the first stop on its route, and a
 * scheduled time for visiting each subsequent stop on the route. A service
 * cannot travel instantaneously between two different stops on a route.
 */
public class Service {

	private Route route; // Route for current service
	private List<Integer> times; // List of service times

	/*
	 * Invariant: route != null && times != null && elements of times.get(i) are
	 * not null && times.size() == route.numStops && no duplicates in times &&
	 * times is ascending
	 */

	/**
	 * Creates a new service for the given route.
	 * 
	 * @param route
	 *            the route for the given service.
	 * @param times
	 *            a strictly ascending list of times for visiting each of the
	 *            stops on the route in order.
	 * 
	 * @throws NullPointerException
	 *             if either parameter is null.
	 * @throws InvalidServiceException
	 *             if service and route are non-null but either: (i) times is
	 *             not a strictly ascending list of times or (ii) times is not
	 *             the same length as the number of stops in the route.
	 */
	public Service(Route route, List<Integer> times) {
		// Verify parameters
		if ((route == null) || (times == null)) {
			// Parameters cannot be null
			throw new NullPointerException("Parameters cannot be null");
		} else if (checkNullElements(times)) {
			// Elements of times cannot be null
			throw new NullPointerException("times cannot contain null elements");
		} else if ((route.numStops() != times.size())) {
			// Wrong number of times
			throw new InvalidServiceException(
					"Number of times is not equal to number of stops");
		} else if (checkDuplicateElements(times)) {
			// Elements of times cannot be repeated
			throw new InvalidServiceException("Cannot have duplicate times");
		} else if (!checkAscendingElements(times)) {
			// Elements of times is not ascending
			throw new InvalidServiceException("times is not ascending");
		} else if (checkNegativeElements(times)) {
			// times cannot contain negative values
			throw new InvalidServiceException("Cannot have negative times");
		}

		// Initialise class variables
		this.route = route;
		this.times = times;
	}

	/**
	 * Returns the route for this service.
	 * 
	 * @return the route for this service.
	 */
	public Route route() {
		// No processing needed, already a Route
		return route;
	}

	/**
	 * If there is a stop number i on this service's route, then return the time
	 * that this service stops at the ith stop, otherwise throw a
	 * NoSuchStopException.
	 * 
	 * @param i
	 *            the stop number to retrieve the time for.
	 * @throws NoSuchStopException
	 *             if there is no ith stop.
	 * @return the time that the service stops at stop i.
	 */
	public int getStopTime(int i) {
		try {
			// Index of times == stop number - 1
			return times.get(i - 1);
		} catch (IndexOutOfBoundsException e) {
			// Index does not exist, stop number does not as well
			throw new NoSuchStopException("Stop number " + i
					+ " does not exist in route " + route.name());
		}
	}

	/**
	 * If this service stops at the given station, return the time of that stop,
	 * otherwise throw a NoSuchStopException.
	 * 
	 * @param station
	 *            the station to find the stop time for
	 * @throws NoSuchStopException
	 *             if the service does not stop at the given station.
	 * @return the time that the service stops at the given station.
	 */
	public int getStopTime(Station station) {
		// Utilise getStopNumber method of Route, exception already handled by
		// that method

		// Reuse getStopTime(int i) after obtaining stop number
		return getStopTime(route.getStopNumber(station));
	}

	/**
	 * Returns true iff s1 and s2 are different stops on this service and the
	 * service can be used to travel from station s1 to s2 by departing s1 at a
	 * time no earlier than parameter t.
	 * 
	 * @param s1
	 *            the station to travel from.
	 * @param s2
	 *            the station to travel to from s1.
	 * @param t
	 *            the earliest time that the service may depart s1
	 * @return true iff s1 and s2 are different stops on this service and the
	 *         service can be used to travel from station s1 to s2 by departing
	 *         s1 at a time no earlier than parameter t.
	 */
	public boolean canTravelFrom(Station s1, Station s2, int t) {
		// Utilise canTravelFrom method of Route to check for trip possibility
		// Utilise getStopTime method to get time and compare with t
		return route.canTravelFrom(s1, s2) && (getStopTime(s1) >= t);
	}

	/**
	 * For a service with N stops, this method returns a string of the form
	 * 
	 * "X: S1 T1, ... , SN TN"
	 * 
	 * where X is the name of the route, Si is the name of the ith station that
	 * the service stops at, and Ti is the time that the service stops there.
	 * 
	 * 
	 */
	@Override
	public String toString() {
		String output = ""; // Empty String
		// Loops through station list of route and append to output
		// Also appends the stop time by getting it through getStopTime method
		// Object are directly used because toString method already declared
		for (int i = 1; i < route.numStops() + 1; i++) {
			output += route.getStop(i);
			output += " " + getStopTime(i);
			if (i != route.numStops()) {
				output += ", "; // Not last stop, append comma
			}
		}
		// Prepend route name to output
		output = route.name() + ": " + output;
		return output;
	}

	/**
	 * Two services are equal if they have equivalent routes and they their stop
	 * times are the same.
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Service)) {
			return false; // Not Service object
		}

		Service s = (Service) obj;
		// True iff same route and same times list
		return route.equals(s.route) && times.equals(s.times);
	}

	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * 79 + route.hashCode();
		hash = hash * 89 + times.hashCode();
		return hash;
	}

	/**
	 * Determines whether this service is internally consistent.
	 * 
	 * @return true if this service is internally consistent, and false
	 *         otherwise
	 */
	public boolean checkInv() {
		if (route == null || times == null) {
			return false; // Null class variables
		} else if (checkNullElements(times)) {
			return false; // Null elements in time list
		} else if (checkDuplicateElements(times)) {
			return false; // Duplicate elements in time list
		} else if (!checkAscendingElements(times)) {
			return false; // Time list not ascending
		} else if (times.size() != route.numStops()) {
			return false; // Length of time list != number of stops in route
		} else if (checkNegativeElements(times)) {
			return false; // Negative elements in time list
		} else {
			return true; // Invariant not violated
		}
	}

	// Service helper methods

	/**
	 * Checks for duplicate times.
	 * 
	 * @param times
	 *            List of times
	 * @return true if there are duplicates, and false otherwise
	 */
	private boolean checkDuplicateElements(List<Integer> times) {
		// Create a hashset to hold station names as looping through the list.
		// Hashset is used because sets do not allow duplicates and returns a
		// false value if duplicate given
		HashSet<Integer> hashset = new HashSet<Integer>();
		for (Integer i : times) {
			if (!hashset.add(i)) {
				return true; // Contains duplicates
			}
		}
		return false; // All stations are unique
	}

	/**
	 * Checks for null elements.
	 * 
	 * @param times
	 *            List of times
	 * @return true if there are null elements, and false otherwise
	 */
	private boolean checkNullElements(List<Integer> times) {
		// Loops through every element
		for (Integer i : times) {
			if (i == null) {
				return true; // Contains null elements
			}
		}
		return false; // No null elements
	}

	/**
	 * Checks if list is ascending.
	 * 
	 * @param times
	 *            List of times
	 * @return true if list is ascending, and false otherwise
	 */
	private boolean checkAscendingElements(List<Integer> times) {
		for (int i = 1; i < times.size(); i++) {
			// Check if current time is greater than time before this
			if (times.get(i) < times.get(i - 1)) {
				return false; // Not ascending
			}
		}
		return true; // Ascending
	}

	/**
	 * Checks for negative elements.
	 * 
	 * @param times
	 *            List of times
	 * @return true if there are negative elements, and false otherwise
	 */
	private boolean checkNegativeElements(List<Integer> times) {
		// Loops through every element
		for (Integer i : times) {
			if (i < 0) {
				return true; // Contains negative elements
			}
		}
		return false; // No negative elements
	}
}
