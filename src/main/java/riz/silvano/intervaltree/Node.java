package riz.silvano.intervaltree;

import java.util.ArrayList;
import java.util.List;

public class Node {

	private int idx;
	private Node left;
	private Node right;
	private List<Integer> overlappingRanges;
	private List<Integer> reverseOrder;
	
	public Node(int idx, List<Interval> data)
	{
		
		System.out.println("[" + data.get(idx).getMin() + "," + data.get(idx).getMax() + "]");
		
		this.idx = idx;
		overlappingRanges = new ArrayList<Integer>();
		reverseOrder = new ArrayList<Integer>();
	}
	
	public void addOverlappingInterval(int idx)
	{
		overlappingRanges.add(idx);
	}
	
	public List<Integer> getOverlappingRanges() {
		return overlappingRanges;
	}

	public List<Integer> getReverseOrder() {
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
