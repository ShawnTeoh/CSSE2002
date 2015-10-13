package planner;

import java.io.*;
import java.util.*;

/**
 * Provides a method to read a timetable from a text file.
 */
public class TimetableReader {

	/**
	 * Reads a text file called fileName that contains zero or more descriptions
	 * of a route and its services, and returns the timetable containing each of
	 * those routes and their corresponding services.
	 * 
	 * Here a timetable is simply a mapping from routes occurring in the file to
	 * a non-null -- but possibly empty -- list of services for that route, in
	 * the order in which they occur in the file.
	 * 
	 * Each description of a route and its services consists of (1) a route name
	 * on a line of its own, followed by (2) a line containing a comma-separated
	 * list of station names, followed by (3) zero or more lines each denoting a
	 * service for that route, followed by (4) an empty line. A route name is
	 * simply an unformatted non-empty string that doesn't contain any
	 * whitespace characters. There may be leading and trailing whitespace on
	 * the line containing the route name, but no other information. Station
	 * names are non-empty strings that may contain spaces, but they are
	 * separated by a comma and a single space character (i.e. ", "). A route
	 * must have at least two stops, and it may not stop at the same station
	 * twice. Each line denoting a service consists of exactly one integer for
	 * every stop on the route, where the integers are separated by whitespace
	 * characters. The integers for a service must be strictly ascending.
	 * 
	 * The same route should not occur twice in the one file.
	 * 
	 * @param fileName
	 *            the file to read from.
	 * @return the timetable that was read from the file.
	 * @throws IOException
	 *             if there is an error reading from the input file.
	 * @throws FormatException
	 *             if there is an error with the input format (e.g. the same
	 *             route occurs twice, or the file format is not as specified
	 *             above in any other way.)
	 */
	public static Map<Route, List<Service>> read(String fileName)
			throws IOException, FormatException {
		// Initialise an empty Map (Route as key, List of Service as value)
		Map<Route, List<Service>> output = new HashMap<Route, List<Service>>();
		// Initialise a File object using fileName
		File file = new File(fileName);

		// Check if last line ends with an empty line
		if (!checkLastLine(file)) {
			throw new FormatException("Last line of " + fileName
					+ " is not empty.");
		}
		// Initialise a new Scanner object to read through the file
		Scanner reader = new Scanner(file);

		// Parse the file and map routes to corresponding list of services
		// Continue looping if not last line of file
		while (reader.hasNextLine()) {
			// Create the Route
			Route route = createRoute(reader);
			// Check if the same route already exist in the map
			if (output.keySet().contains(route)) {
				reader.close(); // Close Scanner before throwing exception
				throw new FormatException(route
						+ " cannot occur more than once.");
			}
			// Put the Route as the key and the list of Service as value
			// into the Map
			output.put(route, createServiceList(reader, route));
		}
		reader.close(); // Close the Scanner after done reading

		return output; // Return the Map
	}

	/**
	 * Check if the last line of an input file is an empty line or not. Returns
	 * true if yes.
	 * 
	 * @param file
	 *            a File object of the input file
	 * @return true iff the last line of the file is an empty line.
	 * @throws IOException
	 *             if there is error reading the input file.
	 * @require file must exist and be readable.
	 * @ensure returns true or false after checking the last line.
	 */
	private static boolean checkLastLine(File file) throws IOException {
		// Initialise a Scanner to read file
		Scanner reader = new Scanner(file);
		// Initialise an empty String
		String out = "";

		// Loops until last line of file
		while (reader.hasNextLine()) {
			// Overwrite out in each loop
			// Eventually will contain String value of last line
			out = reader.nextLine();
		}
		reader.close(); // Close the Scanner

		// Return true if last line is empty, else false
		if (out.equals("")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Creates and returns a Route object by parsing the input file.
	 * 
	 * @param reader
	 *            a Scanner object used to read through the file.
	 * @return a Route object initialised by parsing info from the file.
	 * @throws FormatException
	 *             if the route name is empty or has spaces in between, error
	 *             when initialising Route object.
	 * @require reader to be at the line containing route name.
	 * @ensure returns an initialised Route object with info parsed from file.
	 */
	private static Route createRoute(Scanner reader) throws FormatException {
		Route output; // Declare a Route object

		// Strip leading and tailing whitespace from the line returned by reader
		String name = reader.nextLine().trim(); // Name of route

		// Throw FormatException if name is empty or has whitespace in between
		if (name.split("\\s+").length != 1) {
			// Attempt to split name by whitespace characters, if contains
			// whitespace, size of array will be more than 1
			reader.close(); // Close Scanner before throwing exception
			throw new FormatException(
					"Route name cannot contain whitespace in between.");
		} else if (name.equals("")) {
			reader.close(); // Close Scanner before throwing exception
			throw new FormatException("Route name cannot be empty.");
		}

		// List of stations
		ArrayList<Station> stationList = createStationList(reader);

		try {
			// Attempt to initialise the Route object
			output = new Route(name, stationList);
		} catch (InvalidRouteException e) {
			reader.close(); // Close Scanner before throwing exception
			throw new FormatException(e.getMessage());
		}

		return output; // Return Route object
	}

	/**
	 * Initialise Service objects with info parsed from file and returns them as
	 * a list.
	 * 
	 * @param reader
	 *            a Scanner object used to read through the file.
	 * @param route
	 * @return a list of Service objects or an empty list.
	 * @throws FormatException
	 *             if error when initialising Service objects.
	 * @require reader to be at the line containing service times and has an
	 *          empty line to signal an end.
	 * @ensure returns a list with initialised Service objects with info parsed
	 *         from file, if there are no service time given, returns an empty
	 *         list instead.
	 */
	private static ArrayList<Service> createServiceList(Scanner reader,
			Route route) throws FormatException {
		// Initialise an empty list of services
		ArrayList<Service> output = new ArrayList<Service>();

		// Keep on parsing file to create Service objects and add into list
		// until empty line is detected
		while (reader.hasNextLine()) {
			// Record content of next line into times
			String times = reader.nextLine();

			if (times.equals("")) {
				// Stop looping if empty line is found
				break;
			}
			// Create a list of times
			ArrayList<Integer> timeList = createTimeList(times, reader);

			try {
				// Attempt to create a new Service object
				Service service = new Service(route, timeList);
				// Add to list of services
				output.add(service);
			} catch (InvalidServiceException e) {
				// Error when initialising Service
				reader.close(); // Close Scanner before throwing exception
				throw new FormatException(e.getMessage());
			}
		}

		return output; // Returns the list of services
	}

	/**
	 * Initialise Station objects by parsing the input file to get station names
	 * and returns a list of the Station objects created.
	 * 
	 * @param reader
	 *            a Scanner object used to read through the file.
	 * @return a list of stations.
	 * @throws FormatException
	 *             if there are no stations or empty station name.
	 * @require reader to be after the line containing route name, station names
	 *          separated by ", "
	 * @ensure returns a list of initialised stations using info parsed from
	 *         input file.
	 */
	private static ArrayList<Station> createStationList(Scanner reader)
			throws FormatException {
		// Initialise an empty list of stations
		ArrayList<Station> stationList = new ArrayList<Station>();

		// Read the next line and split String using ", " as delimiter
		// out contains the String array after splitting
		String[] out = reader.nextLine().split(", ");
		// Loop through every station name in out array
		for (String i : out) {
			// Check if station name is empty
			if (i.equals("")) {
				reader.close(); // Close Scanner before throwing exception
				throw new FormatException("Station name cannot be empty.");
			}
			// Initialise Station object and add to list
			stationList.add(new Station(i));
		}

		return stationList; // Returns the list of stations
	}

	/**
	 * Parse a string representation of a list of times and returns a list of
	 * time integers.
	 * 
	 * @param times
	 *            string representation of time list.
	 * @param reader
	 *            a Scanner object used to read through the file.
	 * @return a list of times.
	 * @throws FormatException
	 *             if contains strings that do not represent integers.
	 * @require times to use any number of whitespace char to separate each
	 *          element.
	 * @ensure returns a list of times that only consists of integers.
	 */
	private static ArrayList<Integer> createTimeList(String times,
			Scanner reader) throws FormatException {
		// Initialise an empty list of times
		ArrayList<Integer> timeList = new ArrayList<Integer>();

		// Read the next line and split String using "\\s+" (any number of
		// whitespace char) as delimiter
		// out contains the String array after splitting
		String[] out = times.split("\\s+");
		// Loop through every number string in out array
		for (String i : out) {
			try {
				// Attempt to parse number string into integer
				timeList.add(Integer.parseInt(i));
			} catch (NumberFormatException e) {
				// Cannot parse string into number
				reader.close(); // Close Scanner before throwing exception
				throw new FormatException("'" + i + "'" + " is not a number.");
			}
		}

		return timeList; // Returns the list of times
	}
}
