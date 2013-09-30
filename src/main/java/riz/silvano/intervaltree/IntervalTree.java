package riz.silvano.intervaltree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.threadpool.DefaultThreadPool;
import org.apache.commons.threadpool.ThreadPool;

/**
 * Implementation of an interval tree that allows to query all the intervals that contain a particular point
 *  
 * @author mele
 *
 */
public class IntervalTree {
	//The root node of the index. The data will be kept in an array
	private Node root;

	// The node count
	private int nodeCount = 0;

	// Interval data.
	private List<Interval> data;

	// Pool of thread used to initialize the nodes.
	private ThreadPool pool;

	// Counter used to verify when the tree has been initialized.
	private AtomicInteger processedNodes;

	// Listener to notify when the tree has been initialized
	private TreeStatusListener listener;

	// The size of the threadPool
	private int threadPoolSize = 4;

	/**
	 * Listener called when the initialization phase is complete
	 */
	public interface TreeStatusListener {
		/**
		 * Method called when the tree is ready
		 */
		public void loaded();
	}

	/**
	 * Returns the node count
	 * @return the node count
	 */
	public int getNodeCount() {
		return nodeCount;
	}

	/**
	 * Constructor.
	 * The initialization of each node is carried out in separate threads to improve performances.
	 * 
	 * @param data The list of intervals
	 * @param listener The {@link TreeStatusListener} to be notified when the tree is complete
	 */
	public IntervalTree(List<Interval> data, TreeStatusListener listener) {

		if ((data == null) || (data.size() == 0)) {
			throw new IllegalArgumentException("data cannot be null or empty");
		}

		pool = new DefaultThreadPool(threadPoolSize);
		this.listener = listener;
		processedNodes = new AtomicInteger(0);
		this.data = data;

		Collections.sort(data, new Comparator<Interval>() {

			public int compare(Interval o1, Interval o2) {
				if (o1.getMin() < o2.getMin()) {
					return -1;
				} else if (o1.getMin() == o2.getMin()) {
					return 0;
				} else {
					return 1;
				}
			}

		});

		root = buildIndex(0, data.size() - 1);

		//System.out.println("Init nodes");
		//iterativePostOrderTraversal(root, new FindOverlaps());
		//listener.loaded();
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

		System.out.println("buildIndex(" + lowerIdx + "," + upperIdx + ") | middle = " + middleIdx);

		Node n = new Node(middleIdx);
		nodeCount++;
		pool.invokeLater(new FindOverlapsJob(n));
		//findOverlaps(n);
		System.out.println("|overlappings| = " + n.getOverlappingRanges().size());

		if (lowerIdx != upperIdx) {
			if (lowerIdx < middleIdx) {
				n.setLeft(buildIndex(lowerIdx, middleIdx - 1));
			}
			if (middleIdx < upperIdx) {
				n.setRight(buildIndex(middleIdx + 1, upperIdx));
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
	public static void iterativePostOrderTraversal(Node root, VisitProcessor processor) {

		Node cur = root;
		Node pre = root;

		Stack<Node> s = new Stack<Node>();

		if (root != null) {
			s.push(root);
		}

		while (!s.isEmpty()) {
			cur = s.peek();
			if (cur.equals(pre) || cur.equals(pre.getLeft()) || cur.equals(pre.getRight())) {
				// we are traversing down the tree
				if (cur.getLeft() != null) {
					s.push(cur.getLeft());
				} else if (cur.getRight() != null) {
					s.push(cur.getRight());
				}
				if ((cur.getLeft() == null) && (cur.getRight() == null)) {
					processor.process(s.pop());
				}
			} else if (pre.equals(cur.getLeft())) {
				// we are traversing up the tree from the left
				if (cur.getRight() != null) {
					s.push(cur.getRight());
				} else if (cur.getRight() == null) {
					processor.process(s.pop());
				}
			} else if (pre.equals(cur.getRight())) {
				// we are traversing up the tree from the right
				processor.process(s.pop());
			}
			pre = cur;
		}
	}

	/**
	 * Returns all the intervals containing the specific point
	 * 
	 * @param query The point
	 * @return All the intervals containing the specific point
	 */
	public List<Interval> query(long query) {

		List<Interval> resultSet = new ArrayList<Interval>();

		if (root == null)
			return resultSet;

		Node current = root;

		do {

			if ((data.get(current.getIdx()).getMin() <= query) && (data.get(current.getIdx()).getMax() >= query)) {
				// Found the interval, add it to the result set
				resultSet.add(data.get(current.getIdx()));

				// Verify all the overlapping intervals
				// TODO -  This can be improved
				for (Integer idx : current.getOverlappingRanges()) {
					if ((data.get(idx).getMin() <= query) && (data.get(idx).getMax() >= query)) {
						resultSet.add(data.get(idx));
					}
				}

				current = null;
			} else {
				// Continue the query on one of the subtrees
				long pivot = data.get(current.getIdx()).getMin();
				if (pivot > query) {
					current = current.getLeft();
				} else {
					current = current.getRight();
				}
			}

		} while (current != null);

		// Sort the result set by interval width
		Collections.sort(resultSet, new Comparator<Interval>() {

			public int compare(Interval o1, Interval o2) {
				long size1 = (o1.getMax() - o1.getMin()) / 2;
				long size2 = (o2.getMax() - o2.getMin()) / 2;
				if (size1 > size2) {
					return 1;
				} else if (size1 == size2) {
					return 0;
				} else {
					return -1;
				}
			}

		});

		return resultSet;

	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();

		iterativePostOrderTraversal(root, new VisitProcessor() {

			public void process(Node node) {

				int idx = node.getIdx();
				Interval i = data.get(idx);

				sb.append(String.format("%d : [%d..%d]", idx, i.getMin(), i.getMax()));

				if ((node.getOverlappingRanges() != null) && (node.getOverlappingRanges().size() > 0)) {
					sb.append(" | Overlaps: ");
					for (Integer overlapping : node.getOverlappingRanges()) {
						sb.append(String.format("[%d..%d]", data.get(overlapping).getMin(), data.get(overlapping).getMax()));
					}
				}
			}
		});

		return sb.toString();
	}

	private void findOverlaps(Node node) {

		System.out.println("Fining overlaps of " + data.get(node.getIdx()));

		Interval current = data.get(node.getIdx());
		Interval next;
		int idx = 0;

		do {

			next = data.get(idx);
			if ((idx != node.getIdx()) && (next.getMin() < current.getMax()) && (current.getMin() < next.getMax())) {

				//System.out.println("Fount overlap " + );

				node.addOverlappingInterval(idx);
			}
			idx++;

		} while ((idx < data.size()) && (next.getMin() < current.getMax()));

	}

	/**
	 * Implementation of {@link VisitProcessor} that will find the overlapping intervals
	 * for a given node.
	 * The process is execute in a thread taken from the thread pool.
	 */
	private class FindOverlaps implements VisitProcessor {

		public void process(Node node) {
			pool.invokeLater(new FindOverlapsJob(node));
		}

	}

	/**
	 * The {@link Runnable} job that finds the overlapping intervals
	 */
	private class FindOverlapsJob implements Runnable {

		private Node n;

		private FindOverlapsJob(Node n) {
			this.n = n;
		}

		public void run() {

			Interval current = data.get(n.getIdx());
			Interval next;
			int idx = 0;
			// Iterate over all the data searching for overlappings
			// TODO - It can be improved
			do {

				next = data.get(idx);
				if ((idx != n.getIdx()) && (next.getMin() < current.getMax()) && (current.getMin() < next.getMax())) {

					//System.out.println("Fount overlap " + );

					n.addOverlappingInterval(idx);
				}
				idx++;

			} while ((idx < data.size()) && (next.getMin() < current.getMax()));

			int res = processedNodes.addAndGet(1);

			System.out.println(String.format("%d / %d", res, data.size()));

			// Notify the listener if we finished
			if ((res == data.size()) && (listener != null)) {
				listener.loaded();
			}

		}

	}

}
