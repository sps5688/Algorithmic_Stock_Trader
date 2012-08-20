package outgoing_data_sources;

import trade_types.Trade;
import utilities.GeneralToolKit;

/**
 * Represents the transmission of outgoing stock transactions to a service such as a brokerage.
 * 
 * @author Steven Shaw
 * @email  sps5688@rit.edu
 *
 */
public class DummyService {
	
	public Trade executeOrder(Trade curTrade) {
		// Retrieves up to date stock price and updates Trade
		String price = GeneralToolKit.fetcher.getLastTradePriceOnly(curTrade.getSymbol().symbol);
		curTrade.setPrice(Double.parseDouble(price));

		// Will eventually send to outside broker service
		
		return curTrade;
	}

}