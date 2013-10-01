package riz.silvano.intervaltree;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import riz.silvano.intervaltree.IntervalTree.TreeStatusListener;

/**
 * Tests
 * 
 * @author mele
 */
@RunWith(JUnit4.class)
public class IntervalTreeTestFiles {

	private static final Logger log = LoggerFactory.getLogger(IntervalTreeTestFiles.class);

	@Before
	public void setUp() {
		// Put here something you want to be executed before the test
	}

	@Test
	public void testQueryTimeUsingFileData() throws InterruptedException {

		Memory2 before = new Memory2();

		List<Interval> data = TestUtils.generateFromFiles();
		Memory2 after = new Memory2();

		log.info("Mem before Data load\n " + before);
		log.info("Mem after Data load\n " + after);

		StatusListener2 listener = new StatusListener2();
		IntervalTree tree = new IntervalTree(data, listener);

		while (!listener.isLoaded()) {
			Thread.sleep(100);
		}
		Assert.assertNotNull(tree);

		after = new Memory2();

		log.info("Mem before Tree Load\n " + before);
		log.info("Mem after Tree Load\n " + after);

		// Try a query
		long overallstart = System.currentTimeMillis();
		long start;
		long end;
		long query;
		
		long cardNumMin = Long.parseLong("300000000000");
		long cardNumMax = Long.parseLong("600000000000");
		
		for (int i = 0; i < 1000; i++) {
			query = cardNumMin + (long) (Math.random() * (cardNumMax - cardNumMin));
			start = System.currentTimeMillis();
			List<Interval> resultset = tree.query(query);
			end = System.currentTimeMillis();
			log.info(TestUtils.printIntervals(resultset));
		}
		long overallend = System.currentTimeMillis();
		log.info("Total query time " + (overallend - overallstart) + " ms ");
	}

}

class StatusListener2 implements TreeStatusListener {
	private AtomicBoolean loaded = new AtomicBoolean(false);

	public boolean isLoaded() {
		return loaded.get();
	}

	public void loaded() {
		loaded.set(true);
	}

}

class Memory2 {
	private static final int MB = 1024 * 1024;

	private long usedMemory;
	private long freeMemory;
	private long totalMemory;
	private long maxMemory;

	Memory2() {
		Runtime runtime = Runtime.getRuntime();
		usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / MB;
		freeMemory = runtime.freeMemory() / MB;
		totalMemory = runtime.totalMemory() / MB;
		maxMemory = runtime.maxMemory() / MB;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("\n##### Heap utilization statistics [MB] #####");

		sb.append(String.format("\nUsed Memory: %d", usedMemory));
		sb.append(String.format("\nFree Memory: %d", freeMemory));
		sb.append(String.format("\nTotal Memory: %d", totalMemory));
		sb.append(String.format("\nMax Memory: %d", maxMemory));

		sb.append("\n#############################################\n");

		return sb.toString();
	}

}
