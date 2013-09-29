package riz.silvano.intervaltree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.apache.commons.threadpool.DefaultThreadPool;
import org.apache.commons.threadpool.ThreadPool;

/**
 * 
 * @author mele
 *
 */
public class IntervalTree 
{

	private int nodeCount = 0;
	private Node root;
	private List<Interval> data;
	ThreadPool pool;

	public IntervalTree(List<Interval> data)
	{

		if (data == null || data.size() == 0)
		{
			throw new IllegalArgumentException("data cannot be null or empty");
		}

		this.data = data;

		Collections.sort(data, new Comparator<Interval>(){

			public int compare(Interval o1, Interval o2) {
				if (o1.getMin() < o2.getMin())
				{
					return -1;
				}
				else if (o1.getMin() == o2.getMin())
				{
					return 0;
				}else{
					return 1;
				}
			}

		});

		root = buildIndex(0, data.size() -1);
		
		pool = new DefaultThreadPool(5);
		
		iterativePostOrderTraversal(root, new FindOverlaps());
		
	}

	private Node buildIndex(int lowerIdx, int upperIdx)
	{

		int middleIdx = (lowerIdx + upperIdx) / 2;

		System.out.println("buildIndex(" + lowerIdx + "," + upperIdx +") | middle = " + middleIdx);

		Node n = new Node(middleIdx, data);
		nodeCount ++;
		if (lowerIdx != upperIdx){
			if (lowerIdx < middleIdx)
			{
				n.setLeft(buildIndex(lowerIdx, middleIdx -1));
			}
			if (middleIdx < upperIdx)
			{
				n.setRight(buildIndex(middleIdx + 1, upperIdx));
			}
		}

		return n;
	}

	public int getNodeCount(){
		return nodeCount;
	}

	public void print()
	{
		iterativePostOrderTraversal(root, new VisitProcessor() {
			
			public void process(Node node) {
				int idx = node.getIdx();
				Interval i = data.get(idx);
				System.out.println(String.format("%d : [%d..%d]", idx, i.getMin(), i.getMax()));
			}
		});
	}

	public static void iterativePostOrderTraversal(Node root, VisitProcessor processor){
		
		Node cur = root;
		Node pre = root;
		
		Stack<Node> s = new Stack<Node>();
		
		if(root!=null)
		{
			s.push(root);
		}
		
		while(!s.isEmpty())
		{
			cur = s.peek();
			if(cur.equals(pre) || cur.equals(pre.getLeft()) || cur.equals(pre.getRight()))
			{
				// we are traversing down the tree
				if(cur.getLeft()!=null){
					s.push(cur.getLeft());
				}
				else if(cur.getRight()!=null)
				{
					s.push(cur.getRight());
				}
				if(cur.getLeft()==null && cur.getRight()==null)
				{
					//System.out.println(s.pop().getIdx());
					processor.process(s.pop());
				}
			}
			else if(pre.equals(cur.getLeft()))
			{
				// we are traversing up the tree from the left
				if(cur.getRight()!=null)
				{
					s.push(cur.getRight());
				}
				else if(cur.getRight()==null)
				{
					processor.process(s.pop());
				}
			}
			else if(pre.equals(cur.getRight()))
			{
				// we are traversing up the tree from the right
				processor.process(s.pop());
			}
			pre=cur;
		}
	}

	public List<Interval> query(long query)
	{
		List<Interval> resultSet = new ArrayList<Interval>();

		if (root == null)
			return resultSet;

		Node current = root;

		do
		{
			if (data.get(current.getIdx()).getMin() <= query && data.get(current.getIdx()).getMax() >= query)
			{
				// Found node. Scan the ranges
				for (Integer idx : current.getOverlappingRanges()) {
					if (data.get(idx).getMin() >= query && data.get(idx).getMax() <= query)
					{
						resultSet.add(data.get(idx));
					}
				}

				current = null;
			}
			else
			{
				long middle = (data.get(current.getIdx()).getMin() + data.get(current.getIdx()).getMax()) / 2;
				if (middle > query)
				{
					current = current.getLeft();
				}
				else
				{
					current  = current.getRight();
				}
			}


		}while(current !=null);

		Collections.sort(resultSet, new Comparator<Interval>() {

			public int compare(Interval o1, Interval o2) {
				long size1 = (o1.getMax() - o1.getMin()) /2;
				long size2 = (o2.getMax() - o2.getMin()) /2;
				if (size1 > size2)
				{
					return 1;
				}
				else if (size1 == size2)
				{
					return 0;
				}
				else
				{
					return -1;
				}
			}

		});

		return resultSet;

	}
	
	private class FindOverlaps implements VisitProcessor{

		public void process(Node node) 
		{
			pool.invokeLater(new FindOverlapsJob(node));
		}
		
	}
	
	private class FindOverlapsJob implements Runnable
	{

		private Node n;
		private FindOverlapsJob(Node n)
		{
			this.n = n;
		}
		
		public void run() {
			
			System.out.println("Thread:" + Thread.currentThread().getId());
			
			Interval current = data.get(n.getIdx());
			Interval next;
			
			for (int i=0; i<data.size(); i++ )
			{
				if (i==n.getIdx())
				{
					continue;
				}
				next = data.get(i);
				if (next.getMin() < current.getMax() && current.getMin() < next.getMax())
				{
					System.out.println("Found overlap");
					n.addOverlappingInterval(i);
				}
				else
				{
					System.out.println("Not an overlap");
				}
			}
			
		}
		
	}
	
}