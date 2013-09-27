package riz.silvano.intervaltree;

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
     * Rigourous Test :-)
     */
    public void testIntervalTree()
    {
        assertTrue( true );
    }
}
