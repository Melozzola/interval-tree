package riz.silvano.intervaltree;

/**
 * Models an interval.
 * The class is on purpose kept very simple (preferred public fields over getters and setters).
 * 
 * @author mele
 */
public class Interval {

	// The left point
	public long min;
	
	// The right point
	public long max;
	
	// Interval info
	public String info;
	
	// ID (index) of interval file source 
	public int intervalFile;
	
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

	public Interval(long min, long max, String info, int fileId) {
		this(min, max, info);
		this.intervalFile = fileId;
	}	
}
