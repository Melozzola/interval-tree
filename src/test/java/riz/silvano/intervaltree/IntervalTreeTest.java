package riz.silvano.intervaltree;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests
 * 
 * @author mele
 */
@RunWith(JUnit4.class)
public class IntervalTreeTest 
{

	private static final Logger log = LoggerFactory.getLogger(IntervalTreeTest.class);

	@Before
	public void setUp() 
	{
		// Put here something you want to be executed before the test
	}

	@Test
	public void testIntervalTreeConstruction() throws InterruptedException 
	{

		List<Interval> data = TestUtils.generate(20, 0, 100, 10, 40);

		StatusListener listener = new StatusListener();
		IntervalTree tree = new IntervalTree(data, listener);

		while (!listener.isLoaded()) 
		{
			Thread.sleep(100);
		}

		Assert.assertNotNull(tree);
		Assert.assertEquals(data.size(), tree.getNodeCount());

		log.info("Tree:\n" + tree);
	}

	@Test
	public void testRecursionLimit() throws InterruptedException
	{

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

		while (!listener.isLoaded()) 
		{
			Thread.sleep(100);
		}

		Assert.assertNotNull(tree);
		Assert.assertEquals(data.size(), tree.getNodeCount());

		int depth = tree.maxHeight();
		log.info("Depth : {}", depth);
		
		Memory after = new Memory();

		log.info("Mem before\n " + before);
		log.info("Mem after\n " + after);

	}

	@Test
	public void testQueryTime() throws InterruptedException 
	{

		Memory before = new Memory();

		List<Interval> data = TestUtils.generate(100000, 0, 10000000, 50, 10000);

		StatusListener listener = new StatusListener();
		IntervalTree tree = new IntervalTree(data, listener);

		while (!listener.isLoaded()) 
		{
			Thread.sleep(100);
		}

		Assert.assertNotNull(tree);
		Assert.assertEquals(data.size(), tree.getNodeCount());

		int depth = tree.maxHeight();
		log.info("Depth : {}", depth);
		
		Memory after = new Memory();

		log.info("Mem before\n " + before);
		log.info("Mem after\n " + after);

		// Try a few queries
		long query;
		long overallstart = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) 
		{
			query = 0 + (int) (Math.random() * ((1000000 - 0) + 1));
			List<Interval> resultset = tree.query(query);

			log.info(TestUtils.printIntervals(resultset));
		}
		long overallend = System.currentTimeMillis();

		log.info("Total query time " + (overallend - overallstart) + " ms ");
	}

	@Test
	public void testQuery() throws InterruptedException 
	{
		List<Interval> data = TestUtils.generate();

		StatusListener listener = new StatusListener();
		IntervalTree tree = new IntervalTree(data, listener);

		while (!listener.isLoaded()) 
		{
			Thread.sleep(100);
		}

		int depth = tree.maxHeight();
		log.info("Depth : {}", depth);
		
		log.info("Tree : \n", tree);

		List<Interval> resultset = tree.query(0);
		log.info(TestUtils.printIntervals(resultset));
		Assert.assertTrue(1 == resultset.size());
		Assert.assertTrue(resultset.get(0).min == 0);
		Assert.assertTrue(resultset.get(0).max == 9);
		
		resultset = tree.query(5);
		log.info(TestUtils.printIntervals(resultset));
		Assert.assertTrue(1 == resultset.size());
		Assert.assertTrue(resultset.get(0).min == 0);
		Assert.assertTrue(resultset.get(0).max == 9);
		
		resultset = tree.query(9);
		log.info(TestUtils.printIntervals(resultset));
		Assert.assertTrue(1 == resultset.size());
		Assert.assertTrue(resultset.get(0).min == 0);
		Assert.assertTrue(resultset.get(0).max == 9);
		
		resultset = tree.query(10);
		log.info(TestUtils.printIntervals(resultset));
		Assert.assertTrue(0 == resultset.size());
		
		resultset = tree.query(25);
		log.info(TestUtils.printIntervals(resultset));
		Assert.assertTrue(1 == resultset.size());
		Assert.assertTrue(resultset.get(0).min == 20);
		Assert.assertTrue(resultset.get(0).max == 30);

		resultset = tree.query(30);
		log.info(TestUtils.printIntervals(resultset));
		Assert.assertTrue(2 == resultset.size());
		Assert.assertTrue(resultset.get(0).min == 20);
		Assert.assertTrue(resultset.get(0).max == 30);
		Assert.assertTrue(resultset.get(1).min == 30);
		Assert.assertTrue(resultset.get(1).max == 41);
		
		resultset = tree.query(35);
		log.info(TestUtils.printIntervals(resultset));
		Assert.assertTrue(1 == resultset.size());
		Assert.assertTrue(resultset.get(0).min == 30);
		Assert.assertTrue(resultset.get(0).max == 41);
		
		resultset = tree.query(63);
		log.info(TestUtils.printIntervals(resultset));
		Assert.assertTrue(2 == resultset.size());
		Assert.assertTrue(resultset.get(0).min == 50);
		Assert.assertTrue(resultset.get(0).max == 65);
		Assert.assertTrue(resultset.get(1).min == 60);
		Assert.assertTrue(resultset.get(1).max == 80);
	
		resultset = tree.query(110);
		log.info(TestUtils.printIntervals(resultset));
		Assert.assertTrue(1 == resultset.size());
		Assert.assertTrue(resultset.get(0).min == 100);
		Assert.assertTrue(resultset.get(0).max == 200);
		
		resultset = tree.query(125);
		log.info(TestUtils.printIntervals(resultset));
		Assert.assertTrue(2 == resultset.size());
		Assert.assertTrue(resultset.get(0).min == 125);
		Assert.assertTrue(resultset.get(0).max == 150);
		Assert.assertTrue(resultset.get(1).min == 100);
		Assert.assertTrue(resultset.get(1).max == 200);
		
		resultset = tree.query(130);
		log.info(TestUtils.printIntervals(resultset));
		Assert.assertTrue(2 == resultset.size());
		Assert.assertTrue(resultset.get(0).min == 125);
		Assert.assertTrue(resultset.get(0).max == 150);
		Assert.assertTrue(resultset.get(1).min == 100);
		Assert.assertTrue(resultset.get(1).max == 200);
		
		resultset = tree.query(325);
		log.info(TestUtils.printIntervals(resultset));
		Assert.assertTrue(1 == resultset.size());
		Assert.assertTrue(resultset.get(0).min == 300);
		Assert.assertTrue(resultset.get(0).max == 600);
		
		resultset = tree.query(370);
		log.info(TestUtils.printIntervals(resultset));
		Assert.assertTrue(2 == resultset.size());
		Assert.assertTrue(resultset.get(0).min == 350);
		Assert.assertTrue(resultset.get(0).max == 400);
		Assert.assertTrue(resultset.get(1).min == 300);
		Assert.assertTrue(resultset.get(1).max == 600);
		
		resultset = tree.query(456);
		log.info(TestUtils.printIntervals(resultset));
		Assert.assertTrue(3 == resultset.size());
		Assert.assertTrue(resultset.get(0).min == 455);
		Assert.assertTrue(resultset.get(0).max == 460);
		Assert.assertTrue(resultset.get(1).min == 450);
		Assert.assertTrue(resultset.get(1).max == 500);
		Assert.assertTrue(resultset.get(2).min == 300);
		Assert.assertTrue(resultset.get(2).max == 600);
		
	}	

}

