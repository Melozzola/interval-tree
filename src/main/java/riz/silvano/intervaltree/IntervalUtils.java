package riz.silvano.intervaltree;

/**
 * Utility class exposing methods for the Intervals
 * 
 * @author mele
 *
 */
public final class IntervalUtils {

	private IntervalUtils(){}
	
	/**
	 * Returns a pretty printable string describing the interval
	 * 
	 * @param interval The interval
	 * @return The string representing the interval
	 */
	public static String stringify(Interval interval)
	{
		return String.format("[%d..%d] -> %s", interval.min, interval.max, interval.info);
	}
	
	/**
	 * Returns if the 2 intervals overlaps
	 * 
	 * @param i1 The first interval
	 * @param i2 The second interval
	 * @return if the two intervals overlap
	 */
	public static boolean overlaps(Interval i1, Interval i2)
	{
		return ((i2.min <= i1.max) && (i1.min <= i2.max));
	}
	
	/**
	 * Returns if the interval contains the point
	 * 
	 * @param interval The interval
	 * @param point The point
	 * @return if the interval contains the point
	 */
	public static boolean contains(Interval interval, long point)
	{
		return (point >= interval.min && point <= interval.max);
	}	
}
