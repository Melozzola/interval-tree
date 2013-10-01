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
public class IntervalTreeTest {

	private static final Logger log = LoggerFactory.getLogger(IntervalTreeTest.class);

	@Before
	public void setUp() {
		// Put here something you want to be executed before the test
	}

	@Test
	public void testIntervalTreeConstruction() throws InterruptedException {

		List<Interval> data = TestUtils.generate(20, 0, 100, 10, 40);

		StatusListener listener = new StatusListener();
		IntervalTree tree = new IntervalTree(data, listener);

		while (!listener.isLoaded()) {
			Thread.sleep(100);
		}

		Assert.assertNotNull(tree);
		Assert.assertEquals(data.size(), tree.getNodeCount());

		log.info("Tree:\n" + tree);
	}

	@Test
	public void testRecursionLimit() throws InterruptedException {

		Memory before = new Memory();

		// Used -Xmx1048m
		// TestUtils.generate(1000000, 0, 10000000, 50, 1000) is too much
		// TestUtils.generate(100000, 0, 10000000, 50, 1000) is ok:
		//
		// ##### Heap utilization statistics [MB] #####
		// Used Memory: 2
		// Free Memory: 12
		// Total Memory: 15
		// Max Memory: 1484
		// #############################################
		//
		// [main] INFO riz.silvano.intervaltree.IntervalTreeTest - Mem after
		// 
		// ##### Heap utilization statistics [MB] #####
		// Used Memory: 78
		// Free Memory: 82
		// Total Memory: 161
		// Max Memory: 1484
		// #############################################
		//
		// The error when there are too many overlaps is: java.lang.OutOfMemoryError: Java heap space
		List<Interval> data = TestUtils.generate(100000, 0, 10000000, 50, 10000);

		StatusListener listener = new StatusListener();
		IntervalTree tree = new IntervalTree(data, listener);

		while (!listener.isLoaded()) {
			Thread.sleep(100);
		}

		Assert.assertNotNull(tree);
		Assert.assertEquals(data.size(), tree.getNodeCount());

		Memory after = new Memory();

		log.info("Mem before\n " + before);
		log.info("Mem after\n " + after);

	}

	@Test
	public void testQueryTime() throws InterruptedException {

		Memory before = new Memory();

		List<Interval> data = TestUtils.generate(100000, 0, 10000000, 50, 10000);

		StatusListener listener = new StatusListener();
		IntervalTree tree = new IntervalTree(data, listener);

		while (!listener.isLoaded()) {
			Thread.sleep(100);
		}

		Assert.assertNotNull(tree);
		Assert.assertEquals(data.size(), tree.getNodeCount());

		Memory after = new Memory();

		log.info("Mem before\n " + before);
		log.info("Mem after\n " + after);

		// Try a few queries
		long query;

		long overallstart = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			query = 0 + (int) (Math.random() * ((1000000 - 0) + 1));
			List<Interval> resultset = tree.query(query);

			log.info(TestUtils.printIntervals(resultset));
		}
		long overallend = System.currentTimeMillis();

		log.info("Total query time " + (overallend - overallstart) + " ms ");
	}

	@Test
	public void testQuery() throws InterruptedException {
		List<Interval> data = TestUtils.generate();

		StatusListener listener = new StatusListener();
		IntervalTree tree = new IntervalTree(data, listener);

		while (!listener.isLoaded()) {
			Thread.sleep(100);
		}

		log.info("Tree : \n", tree);

		List<Interval> resultset = tree.query(5);
		log.info(TestUtils.printIntervals(resultset));

		resultset = tree.query(15);
		log.info(TestUtils.printIntervals(resultset));

		resultset = tree.query(25);
		log.info(TestUtils.printIntervals(resultset));

		resultset = tree.query(35);
		log.info(TestUtils.printIntervals(resultset));

		resultset = tree.query(45);
		log.info(TestUtils.printIntervals(resultset));

		resultset = tree.query(53);
		log.info(TestUtils.printIntervals(resultset));
	}

}

class StatusListener implements TreeStatusListener {
	private AtomicBoolean loaded = new AtomicBoolean(false);

	public boolean isLoaded() {
		return loaded.get();
	}

	public void loaded() {
		loaded.set(true);
	}

}

class Memory {
	private static final int MB = 1024 * 1024;

	private long usedMemory;
	private long freeMemory;
	private long totalMemory;
	private long maxMemory;

	Memory() {
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
