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
	
	// ID (index) of interval file source 
	private int intervalFile;
	
	/**
	 * Constructor.
	 * 
	 * @param min The left point
	 * @param max The right point
	 * @param info The interval info
	 */
	public Interval(long min, long max, String info) {
		super();
		this.min = min;
		this.max = max;
		this.info = info;
	}

	public Interval(long min, long max, String info, int fileId) {
		this(min, max, info);
		this.intervalFile = fileId;
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
		return String.format("[%d..%d] -> %s (%d)", min,max, info, intervalFile);
	}
	
}
