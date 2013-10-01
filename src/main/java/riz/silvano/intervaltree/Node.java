package riz.silvano.intervaltree;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.List;

/**
 * An index node.
 * Contains the index of an interval and the indexes of all the overlapping intervals
 * The class is on purpose kept very simple (preferred public fields over getters and setters)
 * Moreover, {@link IntArrayList} is used to optimize the memory consumption
 * 
 * @author mele
 */
public class Node {

	// The position in the indexed array
	public int idx;
	
	// The left subtree
	public Node left;
	
	// the right subtree
	public Node right;
	
	// List of positions in the indexed array containing overlapping intervals
	// This is ordered by left end
	public List<Integer> overlappingRanges;
	
	/**
	 * Constructor
	 * 
	 * @param idx The position in the indexed array.
	 */
	public Node(int idx)
	{	
		this.idx = idx;
		
		//overlappingRanges = new ArrayList<Integer>();
		overlappingRanges = new IntArrayList();
	}
	
	
}
