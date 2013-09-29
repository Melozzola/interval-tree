package riz.silvano.intervaltree;

public class Interval {

	private long min;
	private long max;
	private String info;
	
	public Interval(long min, long max, String info) {
		super();
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
	
	
	
}
