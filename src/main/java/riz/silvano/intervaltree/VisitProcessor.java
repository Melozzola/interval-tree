package riz.silvano.intervaltree;

/**
 * Processor called when a node is visited during the tree traversal.
 * 
 * @author mele
 *
 */
public interface VisitProcessor {

	/**
	 * Process the node
	 * 
	 * @param node the visited node
	 */
	void process(Node node);
	
}
