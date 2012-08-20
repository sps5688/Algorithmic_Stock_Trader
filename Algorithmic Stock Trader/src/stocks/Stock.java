package stocks;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Represents a Stock
 * 
 * @author Steven Shaw
 * @email  sps5688@rit.edu
 * 
 * @author Andrew Schmitz
 * @email  Andy@ARSchmitz.com
 *
 */
@SuppressWarnings("serial")
public class Stock implements Serializable {

	public String symbol;
	private static HashMap<String, Stock> singleton = new HashMap<String, Stock>();

	private Stock(String stockSymbol) {
		symbol = stockSymbol;
		Stock.singleton.put(stockSymbol, this);
	}
	
	/**
	 * Gets the Stock object
	 * 
	 * @param stockSymbol
	 * @return
	 */
	public static Stock get(String stockSymbol) {
		stockSymbol = stockSymbol.toLowerCase();
		
		// If symbol is already in Map, return it
		// Otherwise, create new Stock and put in Map
		if (Stock.singleton.containsKey(stockSymbol)) {
			return Stock.singleton.get(stockSymbol);
		} else {
			return new Stock(stockSymbol);
		}
	}
}