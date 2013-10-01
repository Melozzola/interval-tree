package riz.silvano.intervaltree;

import java.util.ArrayList;
import java.util.List;

import riz.silvano.intervaltree.loader.IntervalFileLoader;

/**
 * Some useful utility methods for the unit tests
 * 
 * @author mele
 *
 */
public final class TestUtils {

	private TestUtils()
	{}

	/**
	 * Returns a printable version of the array of intervals
	 * @param intervals The array of intervals
	 * @return printable version of the array of intervals
	 */
	public static String printIntervals(List<Interval> intervals) {

		StringBuilder sb = new StringBuilder();

		if (intervals == null) 
		{
			sb.append("Null\n");
		} 
		else if (intervals.size() == 0) 
		{
			sb.append("Empty\n");
		} 
		else 
		{
			for (int i=0; i< intervals.size(); i ++)
			{
				if (i>0)
				{
					sb.append(" , ");
				}
				sb.append(IntervalUtils.stringify(intervals.get(i)));
			}
			sb.append("\n");
		}

		return sb.toString();
	}

	/**
	 * Generates an hard coded interval set
	 * 
	 * @return The pre-configured set of intervals
	 */
	public static List<Interval> generate()
	{
		List<Interval> data = new ArrayList<Interval>();

		data.add(new Interval(0, 9, "0..9"));
		
		data.add(new Interval(20, 30, "20..30"));
		data.add(new Interval(30, 41, "30..41"));
		
		data.add(new Interval(50, 65, "50..65"));
		data.add(new Interval(60, 80, "60..80"));
		
		data.add(new Interval(100, 200, "100..200"));
		data.add(new Interval(125, 150, "125..150"));

		data.add(new Interval(300, 600, "300..600"));
		data.add(new Interval(350, 400, "350..400"));
		data.add(new Interval(450, 500, "450..500"));
		data.add(new Interval(455, 460, "455..460"));
		data.add(new Interval(480, 490, "480..490"));
		
		return data;
	}

	/**
	 * Generates a random set of intervals
	 * 
	 * @param intervals Number of intervals to generate
	 * @param minBoundary left boundary
	 * @param maxBoundary right boundary
	 * @param minInrervalSize min interval width
	 * @param maxIntervalSize max interval width
	 * @return A random generated set of intervals
	 */
	public static List<Interval> generate(int intervals, int minBoundary, int maxBoundary, int minInrervalSize, int maxIntervalSize) 
	{
		List<Interval> data = new ArrayList<Interval>();

		long min;
		long width;
		String info;
		
		for (int i = 0; i < intervals; i++) 
		{
			min = minBoundary + (int) (Math.random() * ((maxBoundary - maxIntervalSize - minBoundary) + 1));
			width = minInrervalSize + (int) (Math.random() * ((maxIntervalSize - minInrervalSize) + 1));
			info = String.format("%d..%d", min, min + width);
			data.add(new Interval(min, min + width, info));
		}

		return data;

	}

	public static List<Interval> generateFromFiles() {
		IntervalFile file1 = new IntervalFile("BIN021813.dat", "AIB", "2013-02-18");
		IntervalFile file2 = new IntervalFile("ELAVON BIN.ACCTRNG_19022013", "ELAVON", "2013-02-19");

		IntervalFile[] files = { file1, file2 };
		return IntervalFileLoader.loadIntervalsFromFiles(files);
	}

}
