package riz.silvano.intervaltree;

/**
 * Models an interval
 * 
 * @author mele
 */
public class Interval {

	// The left point
	private long min;
	
	// The right point
	private long max;
	
	// Interval info
	private String info;
	
	/**
	 * Constructor.
	 * 
	 * @param min The left point
	 * @param max The right point
	 * @param info The interval info
	 */
	public Interval(long min, long max, String info) {
		this.min = min;
		this.max = max;
		this.info = info;
	}

	public long getMin() {
		return min;
	}

	public long getMax() {
		return max;
	}

	public String getInfo() {
		return info;
	}
	
	@Override
	public String toString()
	{
		return String.format("[%d..%d] -> %s", min,max, info);
	}
	
}
