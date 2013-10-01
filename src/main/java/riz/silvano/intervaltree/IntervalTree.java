package riz.silvano.intervaltree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.threadpool.DefaultThreadPool;
import org.apache.commons.threadpool.ThreadPool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of an interval tree that allows to query all the intervals that contain a particular point.
 *  
 * @author mele
 *
 */
public class IntervalTree {
	
	private static Logger log = LoggerFactory.getLogger(IntervalTree.class);
	
	//The root node of the index. The data will be kept in an array
	private Node root;

	// The node count
	private int nodeCount = 0;

	// Loading percentage
	private AtomicInteger initializationPercent;
	
	// Interval data.
	private List<Interval> data;

	// Pool of thread used to initialize the nodes.
	private ThreadPool pool;

	// Counter used to verify when the tree has been initialized.
	private AtomicInteger processedNodes;

	// Listener to notify when the tree has been initialized
	private TreeStatusListener listener;

	/**
	 * Default thread pool size for the initialization process
	 */
	public static final int DEFAUT_THREAD_POOL_SIZE = 4;

	/**
	 * Listener called when the initialization phase is complete
	 */
	public interface TreeStatusListener 
	{
		/**
		 * Method called when the tree is ready
		 */
		public void loaded();
	}

	/**
	 * Returns the node count
	 * 
	 * @return the node count
	 */
	public int getNodeCount() 
	{
		return nodeCount;
	}
	
	/**
	 * Constructor.
	 * The initialization of each node is carried out in separate threads to improve performances.
	 * 
	 * @param data The list of intervals
	 * @param listener The {@link TreeStatusListener} to be notified when the tree is complete
	 * @param threadPoolSize the thread pool size for the initialization process.
	 */
	public IntervalTree(List<Interval> data, TreeStatusListener listener, int threadPoolSize) 
	{

		if ((data == null) || (data.size() == 0)) 
		{
			throw new IllegalArgumentException("data cannot be null or empty");
		}

		log.info("Creating interval tree. Data size is {}", data.size() );
		
		pool = new DefaultThreadPool(threadPoolSize);
		this.listener = listener;
		processedNodes = new AtomicInteger(0);
		initializationPercent = new AtomicInteger(0);
		this.data = data;

		Collections.sort(data, new Comparator<Interval>() {

			public int compare(Interval o1, Interval o2) 
			{
				if (o1.min < o2.min) 
				{
					return -1;
				} 
				else if (o1.min == o2.min) 
				{
					return 0;
				} 
				else 
				{
					return 1;
				}
			}

		});

		root = buildIndex(0, data.size() - 1);
		
		log.info("Index created");
		
	}

	/**
	 * Constructor.
	 * The initialization of each node is carried out in separate threads to improve performances.
	 * It uses the default thread pool size for the initialization process
	 * 
	 * @param data The list of intervals
	 * @param listener The {@link TreeStatusListener} to be notified when the tree is complete
	 */
	public IntervalTree(List<Interval> data, TreeStatusListener listener) 
	{
		this(data, listener, DEFAUT_THREAD_POOL_SIZE);
	}
	
	/**
	 * Builds the index. This method is using recursion over the data array.
	 * 
	 * @param lowerIdx The lower index of the data array.
	 * @param upperIdx The upper index of the data array.
	 * @return the index node.
	 */
	private Node buildIndex(int lowerIdx, int upperIdx) {

		int middleIdx = (lowerIdx + upperIdx) / 2;

		if (log.isDebugEnabled())
		{
			log.debug(String.format("buildIndex(%d, %d) | middle = %d", lowerIdx, upperIdx, middleIdx));
		}
		
		Node n = new Node(middleIdx);
		nodeCount++;
		pool.invokeLater(new FindOverlapsJob(n));

		if (lowerIdx != upperIdx)
		{
			
			if (lowerIdx < middleIdx) 
			{
				n.left=(buildIndex(lowerIdx, middleIdx - 1));
			}
			if (middleIdx < upperIdx) 
			{
				n.right=(buildIndex(middleIdx + 1, upperIdx));
			}
		}

		return n;
	}

	/**
	 * Method traversing the tree in post order.
	 * The method is currently used internally to print the tree in the toString and to initialize the nodes.
	 * 
	 * @param root The tree root
	 * @param processor A {@link VisitProcessor} that will be called for each node.
	 */
	public static void iterativePostOrderTraversal(Node root, VisitProcessor processor) 
	{

		Node cur = root;
		Node pre = root;

		Stack<Node> s = new Stack<Node>();

		if (root != null) 
		{
			s.push(root);
		}

		while (!s.isEmpty()) 
		{
			
			cur = s.peek();
			
			if (cur.idx == pre.idx || (pre.left != null && cur.idx == pre.left.idx) || (pre.right != null && cur.idx == pre.right.idx)) 
			{
				// we are traversing down the tree
				if (cur.left != null) 
				{
					s.push(cur.left);
				} 
				else if (cur.right != null) 
				{
					s.push(cur.right);
				}
				if ((cur.left == null) && (cur.right == null)) 
				{
					processor.process(s.pop());
				}
			} 
			else if (cur.left != null && pre.idx == cur.left.idx) 
			{
				// we are traversing up the tree from the left
				if (cur.right != null) 
				{
					s.push(cur.right);
				} 
				else if (cur.right == null) 
				{
					processor.process(s.pop());
				}
			} 
			else if (cur.right != null && pre.idx == cur.right.idx) 
			{
				// we are traversing up the tree from the right
				processor.process(s.pop());
			}
			
			pre = cur;
		}
	}

	/**
	 * Method to recursively compute the max height of the tree.
	 * 
	 * @param node The current root
	 * @return The max height of the tree (or subtree)
	 */
	private int maxHeight(Node node)
	{
		if (node == null)
		{
			return 0;
		}
		int leftHeight = maxHeight(node.left);
		int rightHeight = maxHeight(node.right);
		
		return (leftHeight > rightHeight) ? leftHeight + 1 : rightHeight + 1;
		
	}
	
	/**
	 * Returns the max height of the tree
	 * 
	 * @return the max height of the tree
	 */
	public int maxHeight() 
	{
		return maxHeight(root);
	}
	
	/**
	 * Returns all the intervals containing the specific point
	 * 
	 * @param query The point
	 * @return All the intervals containing the specific point
	 */
	public List<Interval> query(long query) 
	{

		log.info("Query {}", query);
		
		long startTime = System.currentTimeMillis();
		List<Interval> resultSet = new ArrayList<Interval>();

		if (root == null)
		{
			return resultSet;
		}
		
		Node current = root;
		int hops = 0;

		do 
		{

			if (IntervalUtils.contains(data.get(current.idx), query)) 
			{
				
				// Found the interval, add it to the result set
				resultSet.add(data.get(current.idx));

				// Verify all the overlapping intervals
				// TODO -  This can be improved
				for (Integer idx : current.overlappingRanges) 
				{
					if (IntervalUtils.contains(data.get(idx), query)) 
					{
						resultSet.add(data.get(idx));
					}
				}

				current = null;
			} 
			else 
			{
				// Continue the query on one of the subtrees
				long pivot = data.get(current.idx).min;
				if (pivot > query) 
				{
					current = current.left;
				}
				else 
				{
					current = current.right;
				}
			}
			
			hops++;

		} 
		while (current != null);
		
		// Sort the result set by interval width
		Collections.sort(resultSet, new Comparator<Interval>() {

			public int compare(Interval o1, Interval o2) 
			{
				long size1 = (o1.max - o1.min);
				long size2 = (o2.max - o2.min);
				
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

		long endTime = System.currentTimeMillis();
		log.info("Found {} results | iterations = {} | query time = {} ms ", resultSet.size(), hops, (endTime-startTime));
		
		return resultSet;

	}

	@Override
	public String toString() 
	{
		final StringBuilder sb = new StringBuilder();

		iterativePostOrderTraversal(root, new VisitProcessor() 
		{

			public void process(Node node) 
			{

				Interval i = data.get(node.idx);
				sb.append(String.format("%d : %s", node.idx, IntervalUtils.stringify(i)));

				if ((node.overlappingRanges != null) && (node.overlappingRanges.size() > 0)) 
				{
					sb.append(" | Overlaps: ");
					for (Integer overlapping : node.overlappingRanges) 
					{
						sb.append(IntervalUtils.stringify(data.get(overlapping)));
					}
				}
			}
		});

		return sb.toString();
	}

	/**
	 * The {@link Runnable} job that finds the overlapping intervals
	 */
	private class FindOverlapsJob implements Runnable 
	{

		private Node n;

		private FindOverlapsJob(Node n) 
		{
			this.n = n;
		}

		public void run() 
		{

			Interval current = data.get(n.idx);
			Interval next;
			int idx = 0;
			
			// Iterate over all the data searching for overlappings
			do 
			{

				next = data.get(idx);
				
				if ((idx != n.idx) && IntervalUtils.overlaps(current, next)) 
				{
					n.overlappingRanges.add(idx);
				}
				idx++;

			} 
			while ((idx < data.size()) && (next.min < current.max));

			int res = processedNodes.addAndGet(1);
			
			log.debug("Node {} ready | {} overlappings intervals", n.idx, n.overlappingRanges.size());
			
			// Log some progress, without making too noise
			int loaded = (100 * res) / data.size();
			
			if (loaded == 25 && initializationPercent.compareAndSet(0, 25))
			{
				log.info("Initialization 25% complete");
			}
			else if (loaded == 50 && initializationPercent.compareAndSet(25, 50)) 
			{
				log.info("Initialization 50% complete");
			}
			else if (loaded == 75 && initializationPercent.compareAndSet(50, 75)) 
			{
				log.info("Initialization 75% complete");
			}
			
			// Notify the listener if we finished
			if ((res == data.size()) && (listener != null)) 
			{
				log.info("Initialization 100% complete");
				listener.loaded();
			}

		}

	}

}
