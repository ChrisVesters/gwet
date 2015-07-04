package framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * This class is a data class meant to store all checked values during a test.
 * This makes it easier to compare values and check them later.
 * 
 * @author cvesters
 */
public class Diff
{
	// A list containing the names of the entries.
	private final ArrayList<String> fNames = new ArrayList<String>();
	// The expected values of the entries.
	private final ArrayList<Object> fExpected = new ArrayList<Object>();
	// The actual values of the entries.
	private final ArrayList<Object> fActual = new ArrayList<Object>();
	
	
	/**
	 * 
	 * Insert a new entry in the diff.
	 * Note the objects stored in the diff should implement the {@link #equals(Object)} and {@link #toString()} methods.
	 * 
	 * @param name A name for the entry.
	 * @param expected The expected value.
	 * @param actual The actual value.
	 * 
	 **/
	public void add(final String name, final Object expected, final Object actual)
	{
		assert (name != null) && (!name.isEmpty()) : "Use a non-empty name to identify the values.";
		
		fNames.add(name);
		fExpected.add(expected);
		fActual.add(actual);
	}
	
	
	/**
	 * 
	 * Returns all the information about the differences.
	 * This is equivalent as calling {@link #getDiffInformation(boolean)} with verbose set to false.
	 * 
	 * @return A map containing the information about all the differences.
	 * 
	 **/
	public Map<String, String[]> getDiffInformation()
	{
		return getDiffInformation(false);
	}
	
	
	/**
	 * 
	 * Returns all the information about the differences.
	 * 
	 * @param verbose Indicates if we want information about all entries (true) or just the differences (false).
	 * @return A map containing the information about the differences.
	 * 
	 **/
	public Map<String, String[]> getDiffInformation(final boolean verbose)
	{
		final HashMap<String, String[]> info = new HashMap<String, String[]>();
		for (int i = 0; i < fNames.size(); ++i)
		{
			final Object expected = fExpected.get(i);
			final Object actual = fActual.get(i);
			if (verbose || !expected.equals(actual))
			{
				info.put(fNames.get(i), new String[] {expected.toString(), actual.toString()});
			}
		}
		return info;
	}
	
	
	/**
	 * 
	 * Indicates if there are any elements where the expected does not match the actual value.
	 * 
	 * @return True if there are differences, false otherwise.
	 * 
	 **/
	public boolean isEmpty()
	{
		for (int i = 0; i < fExpected.size(); ++i)
		{
			if (!fExpected.get(i).equals(fActual.get(i)))
			{
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 **/
	@Override
	public String toString()
	{
		return "Diff of " + fNames.size() + " entries.";
	}
}
