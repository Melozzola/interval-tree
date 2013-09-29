package riz.silvano.intervaltree;

import java.util.ArrayList;
import java.util.List;

/**
 * An index node.
 * Contains the index of an interval and the indexes of all the overlapping intervals
 * 
 * @author mele
 */
public class Node {

	// The position in the indexed array
	private int idx;
	
	// The left subtree
	private Node left;
	
	// the right subtree
	private Node right;
	
	// List of positions in the indexed array containing overlapping intervals
	// This is ordered by left end
	private List<Integer> overlappingRanges;
	
	// Right end order of the overlappings.
	private int[] reverseOrder;
	
	/**
	 * Constructor
	 * 
	 * @param idx The position in the indexed array.
	 */
	public Node(int idx)
	{	
		this.idx = idx;
		overlappingRanges = new ArrayList<Integer>();
	}
	
	/**
	 * Adds the index of an overlapping interval
	 * 
	 * @param idx The index of the overlapping interval
	 */
	public void addOverlappingInterval(int idx)
	{
		overlappingRanges.add(idx);
	}
	
	public List<Integer> getOverlappingRanges() {
		return overlappingRanges;
	}

	public int[] getReverseOrder() {
		if (reverseOrder==null){
			reverseOrder = new int[overlappingRanges.size()];
			// TODO - sort in reverse order
		}
		return reverseOrder;	
	}

	public int getIdx() {
		return idx;
	}

	public Node getLeft() {
		return left;
	}

	public void setLeft(Node left) {
		this.left = left;
	}

	public Node getRight() {
		return right;
	}

	public void setRight(Node right) {
		this.right = right;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + idx;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (idx != other.idx)
			return false;
		return true;
	}

	
	
}
