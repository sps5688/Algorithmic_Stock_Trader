package algorithms;

import java.util.ArrayList;


import stocks.Portfolio;
import trade_types.Trade;

/**
 * Template to build new algorithms from
 * 
 * @author Steven Shaw
 * @email  sps5688@rit.edu
 *
 */
@SuppressWarnings("unused")
public class AlgorithmTemplate implements Algorithm {

	// Portfolio and watch list for a trader
	private Portfolio myPortfolio;
	private ArrayList<String> watchList;
	
	@Override
	public void setup(Portfolio portfolio) {
		myPortfolio = portfolio;
		watchList = portfolio.getWatchList();
	}
	
	@Override
	public Trade[] evaluate() {
		Trade[] newTrades = null;

		
		// Your Algorithm Goes Here
		
		
		return newTrades;
	}
}