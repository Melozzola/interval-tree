package riz.silvano.intervaltree;

public class Memory 
{
	
	private static final int MB = 1024 * 1024;

	private long usedMemory;
	private long freeMemory;
	private long totalMemory;
	private long maxMemory;

	public Memory() 
	{
		Runtime runtime = Runtime.getRuntime();
		usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / MB;
		freeMemory = runtime.freeMemory() / MB;
		totalMemory = runtime.totalMemory() / MB;
		maxMemory = runtime.maxMemory() / MB;
	}

	@Override
	public String toString() 
	{
		StringBuilder sb = new StringBuilder("\n##### Heap utilization statistics [MB] #####");

		sb.append(String.format("\nUsed Memory: %d", usedMemory));
		sb.append(String.format("\nFree Memory: %d", freeMemory));
		sb.append(String.format("\nTotal Memory: %d", totalMemory));
		sb.append(String.format("\nMax Memory: %d", maxMemory));

		sb.append("\n#############################################\n");

		return sb.toString();
	}
	
}
