package testing;

import stocks.Stock;
import trade_types.MarketTrade;
import trade_types.Trade;

/**
 * Tester Class to test functionality
 * 
 * @author Steven Shaw
 * @email  sps5688@rit.edu
 *
 */
public class TesterClass {

	public static void main(String[] args) {
		
		Trade t = new MarketTrade("Buy", Stock.get("msft"), 5);
		System.out.println(t.getTradeType());
	}

}