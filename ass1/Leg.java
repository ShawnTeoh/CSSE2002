package planner;

/**
 * An immutable class representing part of a journey that is taken from a start
 * station to an end station using a single service for a route.
 */
public class Leg {

	private Station startStation; // Starting stations
	private Station endStation; // Ending stations
	private Service service; // Current service

	/*
	 * Invariant: startStation != null && endStation != null && service != null
	 * && service.route() != null && traveling from startStation to endStation
	 * is valid
	 */

	/**
	 * Creates a new leg of a journey from the given start to end stations,
	 * using the specified service.
	 * 
	 * @param startStation
	 *            the start station of the leg.
	 * @param endStation
	 *            the end station of the leg.
	 * @param service
	 *            the service that is taken from start to end.
	 * 
	 * @throws NullPointerException
	 *             if any of the parameters are null.
	 * @throws InvalidJourneyException
	 *             if the parameters are not null, but the route taken by the
	 *             given service cannot be used to travel from the startStation
	 *             to the endStation (that includes the case where the stations
	 *             are equivalent).
	 */
	public Leg(Station startStation, Station endStation, Service service) {
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
		this.startStation = startStation;
		this.endStation = endStation;
		this.service = service;
	}

	/**
	 * Returns the start station of the journey leg.
	 * 
	 * @return the start station for the journey leg.
	 */
	public Station startStation() {
		// No processing needed, already a Station
		return startStation;
	}

	/**
	 * Returns the end station of the journey leg.
	 * 
	 * @return the end station for the journey leg.
	 */
	public Station endStation() {
		// No processing needed, already a Station
		return endStation;
	}

	/**
	 * Returns time of departure from the start station.
	 * 
	 * @return the departure time for the journey leg.
	 */
	public int startTime() {
		// Utilise the getStopTime method of Service
		// to get starting station time
		return service.getStopTime(startStation);
	}

	/**
	 * Returns the arrival time for the journey leg.
	 * 
	 * @return the arrival time for the journey leg.
	 */
	public int endTime() {
		// Utilise the getStopTime method of Service
		// to get ending station time
		return service.getStopTime(endStation);
	}

	/**
	 * Returns the service taken on the journey leg to get from the start to the
	 * end station.
	 * 
	 * @return the service taken on the journey leg.
	 */
	public Service service() {
		// No processing needed, already a Service
		return service;
	}

	/**
	 * Two Legs are equal if and only if they start and end at equivalent
	 * stations and their services are equivalent.
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Leg)) {
			return false; // Not Leg object
		}

		Leg l = (Leg) o;
		// True iff same starting and ending station and same service
		return startStation.equals(l.startStation)
				&& endStation.equals(l.endStation) && service.equals(l.service);
	}

	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * 2 + startStation.hashCode();
		hash = hash * 3 + endStation.hashCode();
		hash = hash * 5 + service.hashCode();
		return hash;
	}

	/**
	 * Returns a string of the form:
	 * 
	 * "DT - AT: catch route R from S1 to S2"
	 * 
	 * where DT and AT are the start and end times of the leg, respectively; R
	 * is the name of the service's route, and S1 and S2 are the start and end
	 * stations.
	 */
	@Override
	public String toString() {
		String output = ""; // Empty String
		// Append starting time to output
		// Object are directly used because toString method already declared
		output += service.getStopTime(startStation) + " - ";
		// Append ending time to output
		output += service.getStopTime(endStation) + ": catch route ";
		output += service.route().name() + " from ";
		// Append starting and ending station to output
		output += startStation + " to " + endStation;
		return output;
	}

	/**
	 * Determines whether this instance of the class is internally consistent.
	 * 
	 * @return true if this instance is internally consistent, and false
	 *         otherwise
	 */
	public boolean checkInv() {
		if (startStation == null || endStation == null || service == null) {
			return false; // Null class variables
		} else if (service.route() == null) {
			return false; // route cannot be null
		} else if (!service.route().canTravelFrom(startStation, endStation)) {
			// Unable to travel to selected stations using current service
			return false;
		} else {
			return true; // Invariant not violated
		}
	}
}
