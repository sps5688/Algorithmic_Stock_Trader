package utilities;

import incoming_data_sources.YahooFetcher;

import java.util.Date;
import java.util.List;


import stocks.Portfolio;
import stocks.Stock;
import trade_types.MarketTrade;
import trade_types.Trade;

/**
 * Contains useful methods.
 * 
 * @author Steven Shaw
 * @email  sps5688@rit.edu
 *
 */
public class GeneralToolKit {

	public static final YahooFetcher fetcher = new YahooFetcher();

	/**
	 * Sells all of the Stocks in the Trader's portfolio
	 * 
	 * @param portfolio the Trader's portfolio
	 */
	public static void sellAllStocks(Portfolio portfolio) {
		List<Trade> positions = portfolio.getStocks();
		Trade[] sellTrades = new Trade[positions.size()];

		// For each stock in the portfolio, construct a new Sell Trade
		for (int i = 0; i < positions.size(); i++) {
			Trade curTrade = positions.get(i);
			Trade sellTrade = new MarketTrade("Sell", Stock.get(curTrade.getSymbol().symbol), curTrade.getShares());

			sellTrades[i] = sellTrade;
		}

		// Processes new trades and updates the Trader's finances
		AlgorithmToolKit.processTradeResults(portfolio, sellTrades);
	}
	
	/**
	 * Verifies that a stock is valid
	 * 
	 * @param symbol the stock to verify
	 * @return is the symbol is valid or not
	 */
	public static boolean verifySymbol(String symbol){
		String[] invalidChars = { "!", "@", "$", "%", "^", "&", "*", "(", ")", "-", "_",
								  "+", "=", "{", "}", "[", "]", "|", "\\", ":", ";", "\"",
								  "'", ",", "<", ".", ">", "/", "?", "~", "`" };
		boolean validSymbol = true;
		
		// Check invalid characters
		for(int i = 0; i < invalidChars.length; i++){
			if(symbol.equals(invalidChars[i])){
				validSymbol = false;
			}
		}
		
		// Check for null, "", a space, and numbers
		if(validSymbol){
			if(symbol != null && !symbol.equals("") && !symbol.equals(" ")){
				try{
					if(Double.parseDouble(fetcher.getLastTradePriceOnly(symbol)) != 0){
						validSymbol = true;		
					}else{
						validSymbol = false;
					}
				}catch(NumberFormatException e) {
					validSymbol = false;
				}catch(NullPointerException e){
					validSymbol = false;
				}		
			}else{
				validSymbol = false;
			}
		}
		
		return validSymbol;
	}
	
	/**
	 * Determines if the Stock Market is open or not
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static boolean isMarketOpen() {
		Date dt = new Date();
		int currentHour = dt.getHours();
		int currentMin = dt.getMinutes();
		int currentDay = dt.getDay();

		if (currentDay == 0 || currentDay == 6) {
			System.out.println("Weekend, Market Closed");
			return false;
		} else if (currentHour >= 16) { // After 4pm
			System.out.println("After 4pm, Market Closed");
			return false;
		} else if (currentHour < 9) { // Before 9am
			System.out.println("Before 9:30am, Market Closed");
			return false;
		} else if (currentHour == 9 && currentMin < 30) { // Before 9:30am
			System.out.println("Before 9:30am, Market Closed");
			return false;
		}
		return true;
	}

	/**
	 * Determines if the current time is greater than or equal to 3:50 pm
	 * 
	 * @return boolean var indicating if the time is past 3:50 pm
	 */
	@SuppressWarnings("deprecation")
	public static boolean isPastThreefifty() {
		Date dt = new Date();
		int currentHour = dt.getHours();
		int currentMin = dt.getMinutes();

		if (currentHour == 15 && currentMin >= 50) {
			return true;
		}

		return false;
	}
}