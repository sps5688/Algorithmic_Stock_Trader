package algorithms;

import stocks.Portfolio;
import trade_types.Trade;

/**
 * Interface that defines the layout for custom Algorithms
 * Each algorithm class that is created must use this interface
 * 
 * @author Steven Shaw
 * @email  sps5688@rit.edu
 *
 */
public interface Algorithm {
	
	/**
	 * Sets up the algorithm that will be executed
	 * 
	 * @param portfolio
	 */
	public void setup(Portfolio portfolio);
	
	/**
	 * Creates a set of Trades based on an algorithm
	 * 
	 * @return trades that were computed from an algorithm 
	 */
	public Trade[] evaluate();
	
}