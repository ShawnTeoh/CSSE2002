package planner;

import java.util.*;

/**
 * A class that provides a method for finding a journey between two stations.
 */
public class JourneyFinder {

	/**
	 * @require startStation != null && endStation!=null &&
	 *          !startStation.equal(endStation) &&
	 * 
	 *          timetable != null && !timetable.keySet().contains(null) &&
	 *          !timetable.values().contains(null)
	 * 
	 *          for any route in timetable.keySet(),
	 * 
	 *          - timetable.get(route) does not contain duplicate services; and
	 * 
	 *          - for any two different services s1 and s2 in
	 *          timetable.get(route), s1 and s2 do not depart the first stop for
	 *          the route at the same time; and
	 * 
	 *          - any two services in timetable.get(route) are not allowed to
	 *          "overtake" one another. That is, if a given service s1 departs
	 *          the first stop before s2, then for each station x on the route,
	 *          s1 will be scheduled to visit x before s2 does. (This means that
	 *          if you want to get from station x to y as soon as you can using
	 *          a given route, you are best to take the first service for that
	 *          route that comes along.)
	 * 
	 * @ensure Using the given timetable, this method returns a journey from
	 *         startStation to endStation that departs startStation no earlier
	 *         than time, and arrives at endStation no later than any other
	 *         journey from startStation to endStation that departs the start
	 *         station no earlier than time. If there is no such journey, then
	 *         the method returns the value null.
	 */
	public static Journey findJourney(Station startStation, Station endStation,
			int time, Map<Route, List<Service>> timetable) {
		// Initialise a Map with Station objects as keys and Node objects as
		// values
		Map<Station, Node> nodes = createNodeMap(timetable);
		// Make startStation the special case where the fastestKnownJourney is
		// defined
		nodes.get(startStation).defined = true;
		// Initialise the currenVar object which acts as a container for
		// important variables
		CurrentVar currentVar = new CurrentVar();

		// Keep on looping as long there are nodes which require processing
		// (read the comments in checkStationStatus for more details)
		while (checkStationStatus(nodes)) {
			// Initialise current station (read comments in
			// getStationWithFastestJourney for more details)
			currentVar.current = getStationWithFastestJourney(nodes);
			if (currentVar.current.equals(startStation)) {
				// Special handling for startStation

				// null is given as no Journey that starts and end at the same
				// station can exist
				currentVar.currentJourney = null;
				// currentTime is the desired starting travel time
				currentVar.currentTime = time;
			} else {
				// Handles other cases

				// currentJourney is the fastestKnownJourney of current node
				currentVar.currentJourney = nodes.get(currentVar.current)
						.fastestKnownJourney;
				// currentTime is the ending time of currentJourney
				currentVar.currentTime = currentVar.currentJourney.endTime();
			}

			// Mark current node as finalised
			nodes.get(currentVar.current).finalised = true;

			if (currentVar.current.equals(endStation)) {
				// If current station is endStation, return the current journey
				// as the journey is found
				return currentVar.currentJourney;
			}

			// Loop for every key (Route) in timetable
			for (Route r : timetable.keySet()) {
				// Only continue if current route (r) being tested stops at
				// current station and current station is not the final stop of
				// r
				if (r.stopsAt(currentVar.current)
						&& !checkLastStop(r, currentVar.current)) {
					// Attempt to get the earliest possible Service which is no
					// later than currentTime
					Service service = getEarliestService(timetable.get(r),
							currentVar);
					// Only continue if a Service is found
					if (service != null) {
						// Initialise adjacent as the next stop to current in r
						Station adjacent = r.getNextStop(currentVar.current);
						// Perform several checks to decide whether to update
						// fastestKnownJourney (refer updateJourney for more
						// details)
						updateJourney(nodes, service, currentVar, adjacent);
					}
				}
			}
		}

		return null; // Return null as no journey is found
	}

	/**
	 * Returns a map with Route objects as keys and Node objects as values by
	 * getting each station in every route.
	 * 
	 * @param timetable
	 *            a Map with Route as keys and list of Service as values.
	 * @return a Map with Station objects as keys and Node objects as values.
	 * @require timetable != null
	 * @ensure returns a map with no duplicate or null keys and corresponding
	 *         Node initialised
	 */
	private static Map<Station, Node> createNodeMap(
			Map<Route, List<Service>> timetable) {
		// Initialise an empty Map
		Map<Station, Node> nodes = new HashMap<Station, Node>();
		// Loop for every key (Route) in timetable
		for (Route r : timetable.keySet()) {
			// Loop for every Station in route
			for (int i = 1; i <= r.numStops(); i++) {
				// Put Station as key and initialise a new Node object as value
				// Duplicate stations will only be recorded once as Map cannot
				// have duplicate keys, overwriting corresponding value will not
				// affect as they are all same at this stage
				nodes.put(r.getStop(i), new Node());
			}
		}

		return nodes; // Returns the map
	}

	/**
	 * Checks if any nodes are not finalised and has defined
	 * fastestKnownJourney.
	 * 
	 * @param nodes
	 *            a Map with Station objects as keys and Node objects as values.
	 * @return true if a node that is not finalised and has a defined
	 *         fastestKnownJourney exists.
	 * @require nodes != null.
	 * @ensure return true iff at least one node with specified conditions is
	 *         found.
	 */
	private static boolean checkStationStatus(Map<Station, Node> nodes) {
		// Loop for every key (Station)
		for (Station s : nodes.keySet()) {
			// Check if node is not finalised and has a defined
			// fastestKnownJourney
			if (!nodes.get(s).finalised && nodes.get(s).defined) {
				return true; // Such node exist
			}
		}

		return false; // No such node found
	}

	/**
	 * Searches and returns the Station that can be reached earlier compared to
	 * other stations.
	 * 
	 * @param nodes
	 *            a Map with Station objects as keys and Node objects as values.
	 * @return Unfinalised Station with defined fastestKnownJourney that can be
	 *         reached earlier by its corresponding fastestKnownJourney compared
	 *         to other stations.
	 * @require nodes != null.
	 * @ensure returned Station must be unfinalised with defined
	 *         fastestKnownJourney.
	 */
	private static Station getStationWithFastestJourney(
			Map<Station, Node> nodes) {
		Station fastest = null; // Initialise fastest Station
		// Loop for evey key (Station) in nodes
		for (Station s : nodes.keySet()) {
			// Check if node is unfinalised and with defined fastestKnownJourney
			if (!nodes.get(s).finalised && nodes.get(s).defined
					&& fastest == null) {
				// Handle the first case where fastest is still null
				fastest = s;
			} else if (!nodes.get(s).finalised && nodes.get(s).defined) {
				// Compare the nodes and overwrite fastest if current node is
				// faster
				if (nodes.get(s).compareTo(nodes.get(fastest)) < 0) {
					fastest = s;
				}
			}
		}

		return fastest; // Returns the fastest station
	}

	/**
	 * Checks if a given Station is the last stop of a given Route.
	 * 
	 * @param route
	 *            a Route object that stops at current.
	 * @param current
	 *            the Station object to be checked.
	 * @return true if current is not the last stop of route.
	 * @require route must stop at current.
	 * @ensure returns true iff current is not the last stop of route.
	 */
	private static boolean checkLastStop(Route route, Station current) {
		// Check if current has the same stop number as the total number of
		// stops in route
		if (route.getStopNumber(current) != route.numStops()) {
			return false; // current is the last stop
		} else {
			return true; // current is not the last stop
		}
	}

	/**
	 * Compares and return the earliest service that is >= currentTime.
	 * 
	 * @param services
	 *            list of services.
	 * @param currentVar
	 *            container class that holds important variables about current
	 *            Station.
	 * @return the earliest service, null if no such service is found.
	 * @require services contain at least one element, currentVar != null,
	 *          services stop at current Station.
	 * @ensure the earliest service is >= currentTime.
	 */
	private static Service getEarliestService(List<Service> services,
			CurrentVar currentVar) {
		Service earliest = null; // Initialise earliest Service
		// Loop through every Service in services
		for (Service i : services) {
			// Check if service (i) time for current is on or after
			// currentTime
			if (i.getStopTime(currentVar.current) >= currentVar.currentTime) {
				if (earliest == null) {
					// Handles the case when earliest is not defined
					earliest = i;
				} else if (i.getStopTime(currentVar.current) < earliest
						.getStopTime(currentVar.current)) {
					// Overwrite earliest when i is faster
					earliest = i;
				}
			}
		}

		return earliest; // Returns the earliest service
	}

	/**
	 * Compares and update fastestKnownJourney if fastestKnownJourney to
	 * adjacent is undefined or if adjacent has not been finalised and
	 * currentJourney extended by taking service to adjacent would arrive before
	 * the fastestKnownJourney to adjacent.
	 * 
	 * @param nodes
	 *            a Map with Station objects as keys and Node objects as values.
	 * @param service
	 *            the Service to be used for Journey extension.
	 * @param currentVar
	 *            container class that holds important variables about current
	 *            Station.
	 * @param adjacent
	 *            the next stop after current.
	 * @require nodes does not contain null keys or values, service != null,
	 *          currenVar != null, adjacent != null
	 * @ensure only update fastestKnownJourney iff one of the conditions above
	 *         is fulfilled
	 */
	private static void updateJourney(Map<Station, Node> nodes,
			Service service, CurrentVar currentVar, Station adjacent) {
		Journey tmpJourney; // Declare a Journey object to be used in testing
		// Check if station has a defined fastestKnownJourney
		if (!nodes.get(adjacent).defined) {
			if (currentVar.currentJourney == null) {
				// Handles special currentJourney of startStation
				// Create a new Journey as cannot extend a null Journey
				tmpJourney = new Journey(currentVar.current, adjacent, service);
			} else {
				// Make a copy of currentJourney to avoid errors caused by
				// referencing of variables
				tmpJourney = new Journey(currentVar.currentJourney);
				// Extend the Journey from current to adjacent using service
				tmpJourney.extendJourney(service, adjacent);
			}
			// Update fastestKnownJoureny (read Node.updateJourney for more
			// details)
			nodes.get(adjacent).updateJourney(tmpJourney);
		} else if (!nodes.get(adjacent).finalised) {
			// The other case where adjacent is unfinalised
			if (currentVar.currentJourney == null) {
				// Handles special currentJourney of startStation
				// Create a new Journey as cannot extend a null Journey
				tmpJourney = new Journey(currentVar.current, adjacent, service);
				// Check if tmpJourney is faster than fastestKnownJourney
				// to adjacent
				if (tmpJourney.endTime() < nodes.get(adjacent)
						.fastestKnownJourney
						.endTime()) {
					// Update fastestKnownJoureny (read Node.updateJourney for
					// more details)
					nodes.get(adjacent).updateJourney(tmpJourney);
				}
			} else {
				// Make a copy of currentJourney to avoid errors caused by
				// referencing of variables
				tmpJourney = new Journey(currentVar.currentJourney);
				// Extend the Journey from current to adjacent using service
				tmpJourney.extendJourney(service, adjacent);
				// Check if extended journey is faster than fastestKnownJourney
				// to adjacent
				if (tmpJourney.endTime() < nodes.get(adjacent)
						.fastestKnownJourney
						.endTime()) {
					// Update fastestKnownJoureny (read Node.updateJourney for
					// more details)
					nodes.get(adjacent).updateJourney(tmpJourney);
				}
			}
		}
	}

	/**
	 * A private class to hold different types of data typed related to a
	 * station.
	 */
	private static class Node implements Comparable<Node> {

		// status of station (true = finalised, false = unfinalised)
		private boolean finalised;
		// status of fastestKnownJourney (true = defined, false = undefined)
		private boolean defined;
		// the fastest journey that can be used to travel to current station
		private Journey fastestKnownJourney;

		/**
		 * Creates a Node with all default values
		 */
		private Node() {
			// All stations are unfinalised at first
			this.finalised = false;
			// All stations do not have a defined fastestKnownJourney at first
			this.defined = false;
			// All stations do not have a fastestKnownJourney at first
			this.fastestKnownJourney = null;
		}

		/**
		 * Updates fastestKnownJourney with another and change status as
		 * defined.
		 * 
		 * @param currentJourney
		 *            target journey to update fastestKnownJourney.
		 * @require currentJourney != null.
		 * @ensure fastestKnownJourney != null and status is defined.
		 */
		private void updateJourney(Journey currentJourney) {
			// Initialse a new Journey object and update fastestKnownJourney
			// with it to avoid errors caused by referencing of variables
			fastestKnownJourney = new Journey(currentJourney);
			defined = true; // fastestKnownJourney is now defined
		}

		/**
		 * A Node is greater than another if the ending time of
		 * fastestKnownJourney of this Node is later than the other Node.
		 * 
		 * @param o target Node for comparison
		 * @return integer representing state of current Node when compared to
		 *         another Node
		 * @require o != null
		 * @ensure positive integer if current > other, negative integer if
		 *         current < other, 0 if equal
		 */
		@Override
		public int compareTo(Node o) {
			if (fastestKnownJourney == null && o.fastestKnownJourney == null) {
				// Both fastestKnownJourney are null, so they are equal
				return 0;
			} else if (fastestKnownJourney == null) {
				// fastestKnownJourney is null but not the fastestKnownJourney
				// of the other node, so current node is smaller
				return -1;
			} else if (o.fastestKnownJourney == null) {
				// fastestKnownJourney of the other node is null but not the
				// fastestKnownJourney of current node, so current node is
				// greater
				return 1;
			} else {
				// the Journey that has the earlier end time will be smaller
				return Integer.compare(fastestKnownJourney.endTime(),
						o.fastestKnownJourney.endTime());
			}
		}
	}

	/**
	 * A private class to act as a container for variables related to current
	 * node.
	 */
	private static class CurrentVar {

		private Station current; // current station
		private int currentTime; // current journey end time
		private Journey currentJourney; // current journey
	}
}
