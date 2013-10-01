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
public class IntervalTreeTestFiles
{

	private static final Logger log = LoggerFactory.getLogger(IntervalTreeTestFiles.class);

	@Before
	public void setUp()
	{
		// Put here something you want to be executed before the test
	}

	@Test
	public void testQueryTimeUsingFileData() throws InterruptedException 
	{

		Memory before = new Memory();

		List<Interval> data = TestUtils.generateFromFiles();
		Memory after = new Memory();

		log.info("Mem before Data load\n " + before);
		log.info("Mem after Data load\n " + after);

		StatusListener listener = new StatusListener();
		IntervalTree tree = new IntervalTree(data, listener);

		while (!listener.isLoaded()) 
		{
			Thread.sleep(100);
		}
		Assert.assertNotNull(tree);

		after = new Memory();

		log.info("Mem before Tree Load\n " + before);
		log.info("Mem after Tree Load\n " + after);

		// Try a query
		long overallstart = System.currentTimeMillis();
		long query;
		
		long cardNumMin = Long.parseLong("300000000000");
		long cardNumMax = Long.parseLong("600000000000");
		
		for (int i = 0; i < 1000; i++) 
		{
			query = cardNumMin + (long) (Math.random() * (cardNumMax - cardNumMin));
			List<Interval> resultset = tree.query(query);
			
			log.info(TestUtils.printIntervals(resultset));
		}
		long overallend = System.currentTimeMillis();
		
		log.info("Total query time " + (overallend - overallstart) + " ms ");
	}

}
