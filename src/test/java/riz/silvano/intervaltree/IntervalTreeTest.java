package riz.silvano.intervaltree;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * 
 * @author mele
 *
 */
public class IntervalTreeTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public IntervalTreeTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( IntervalTreeTest.class );
    }

    /**
     * 
     */
    public void testIntervalTree()
    {
        List<Interval> data = new ArrayList<Interval>();
        data.add(new Interval(0, 9, "0..9"));
        data.add(new Interval(10, 19, "10..19"));
        data.add(new Interval(20, 29, "20..29"));
        data.add(new Interval(30, 39, "30..39"));
        data.add(new Interval(40, 49, "40..49"));
        data.add(new Interval(50, 59, "50..59"));
        data.add(new Interval(50, 59, "45..55"));
        
        IntervalTree tree = new IntervalTree(data);
        
        Assert.assertNotNull(tree);
        Assert.assertEquals(data.size(), tree.getNodeCount());
        
        tree.print();
        
    }
    
    public void testRecursionLimit()
    {
    	int iterations = 100000;
    	
    	List<Interval> data = new ArrayList<Interval>();
    	
    	long min;
    	long max;
    	String info;
    	for (int i=0; i<iterations; i++){
    		min = 0 + (int)(Math.random() * ((10000 - 0) + 1));
    		max = (min+1) + (int)(Math.random() * ((10000 - (min+1)) + 1));
    		info = String.format("%d..%d", min, max);
    		data.add(new Interval(min, max, info));
    	}
    	
    	 IntervalTree tree = new IntervalTree(data);
         
         Assert.assertNotNull(tree);
         Assert.assertEquals(iterations, tree.getNodeCount());
    	
    }
}
