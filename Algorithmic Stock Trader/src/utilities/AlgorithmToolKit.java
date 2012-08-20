package utilities;

import java.util.ArrayList;

import stocks.Portfolio;
import stocks.Stock;
import trade_types.Trade;

/**
 * Contains many useful methods to construct algorithms.
 * 
 * @author Steven Shaw
 * @email  sps5688@rit.edu
 *
 */
public class AlgorithmToolKit {

	/**
	 * Processes a Trade array and updates the the Trader's finances
	 * 
	 * @param myPortfolio the Trader's portfolio
	 * @param newTrades   the list of Trades to be processed
	 */
	public static void processTradeResults(Portfolio myPortfolio, Trade[] newTrades) {
		if (newTrades != null) {
			
			// Process each trade
			for (Trade curTrade : newTrades) {
				double availableFunds = myPortfolio.getAvailableFunds();

				// If there is a transaction to make
				if (curTrade != null) {
					double transactionCost;
					double result = 0;
					double pricePaid = myPortfolio.getPriceAmount(curTrade.getSymbol());
					double curPrice = Double.parseDouble(GeneralToolKit.fetcher.getLastTradePriceOnly(curTrade.getSymbol().symbol));

					transactionCost = curPrice * (curTrade.getShares());
					String symbol = curTrade.getSymbol().symbol;
					int originalShares = myPortfolio.getShareAmount(Stock.get(symbol));

					if (curTrade.getOrderType().equals("Buy")) {

						// If the Stock is already owned
						if (pricePaid != 0) {
							// If its within 10%, don't buy again
							double threshold = pricePaid * .10;
							
							// If the Stock is not within the 10% threshold
							if ( (pricePaid > curPrice && (pricePaid - curPrice) >= threshold) || (pricePaid < curPrice && (curPrice - pricePaid) >= threshold) ) {
								if ((availableFunds - transactionCost) > 0) {
									availableFunds -= transactionCost;
									
									System.out.println("Buying " + curTrade.getShares() + " shares of " + curTrade.getSymbol().symbol + " at " + curPrice);
									System.out.println("Trade costs $" + transactionCost);
									
									curTrade.setPrice(curPrice);
									myPortfolio.setAvailableFunds(availableFunds);
									myPortfolio.addStockTransaction(curTrade);
									
									System.out.println("Funds after buying: $" + availableFunds);
								}
							} 
						} else {
							// Stock not already owned
							if ((availableFunds - transactionCost) > 0) {
								availableFunds -= transactionCost;
								
								System.out.println("Buying " + curTrade.getShares() + " shares of " + curTrade.getSymbol().symbol + " at " + curPrice);
								System.out.println("Trade costs $" + transactionCost);
								
								curTrade.setPrice(curPrice);
								myPortfolio.setAvailableFunds(availableFunds);
								myPortfolio.addStockTransaction(curTrade);
								
								System.out.println("Funds after buying: $" + availableFunds);
							}
						}
					} else if (curTrade.getOrderType().equals("Sell")) {
						pricePaid = myPortfolio.getPriceAmount(Stock.get(symbol));

						// Verification that the stock is owned
						if (pricePaid != 0) {
							// Determines the result of the trade
							if (pricePaid > curPrice) {
								result = (pricePaid * originalShares) - (curPrice * originalShares);
								
								System.out.println("Selling " + curTrade.getShares() + " shares of " + curTrade.getSymbol().symbol + " at $" + curPrice);
								System.out.println("Result of trade: Loss of $" + result);
								
								result *= -1; // Makes it negative
							} else if (pricePaid < curPrice) {
								result = (curPrice * originalShares) - (pricePaid * originalShares);
								System.out.println("Result of trade: Gain of $" + result);
							} else {
								System.out.println("Result of trade: Even");
							}

							result -= 20.0; // Buy and Sell Commission
							availableFunds += result;

							double moneyAmountToSet = (pricePaid * originalShares);
							availableFunds += moneyAmountToSet;

							System.out.println("Funds after selling: $" + availableFunds);
							myPortfolio.addStockTransaction(curTrade);

							// Updates Trader's finances
							myPortfolio.updateNetWorth(result);
							myPortfolio.setAvailableFunds(availableFunds);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Computes the Fibonacci Retracement levels of a stock given a high and low price amounts.
	 * 
	 * @param  high the high price number
	 * @param  low the low price number
	 * @return the computed levels
	 */
	public static ArrayList<Double> calcFibRetrace(double high, double low) {
		double difference = high - low;
		ArrayList<Double> levels = new ArrayList<Double>();

		// Calculates the fibonacci retracement levels
		// 23.6%, 38.2%, 50%, 61.8% and 100%.
		levels.add(low);
		levels.add(low + (difference * .236));
		levels.add(low + (difference * .382));
		levels.add(low + (difference * .50));
		levels.add(low + (difference * .618));
		levels.add(high);

		return levels;
	}

	/**
	 * Calculates the high, low, and the threshold values
	 * that will be used by in Fibonacci computation
	 * 
	 * Uses the Stock's 52 week high and low and its 50 day moving average
	 * in computing the values.
	 * 
	 * @param symbol the Stock
	 * @return the High, Low, and Threshold amounts
	 */
	public static String[] calculateHighAndLow_HLAVG(String symbol) {
		String low, high = null;
		String[] values = new String[3];
		double curPrice = Double.parseDouble(GeneralToolKit.fetcher.getLastTradePriceOnly(symbol));
		double fiftyday = Double.parseDouble(GeneralToolKit.fetcher.get50DayMovingAverage(symbol));

		// If the current price is high than the 50 day
		// Use the 52 week high as the high and the 50 day as the low
		// Otherwise, use the 50 day as the high and the 52 week low as the low
		if (curPrice > fiftyday) {
			low = Double.toString(fiftyday);
			high = GeneralToolKit.fetcher.get52WeekHigh(symbol);
		} else {
			low = GeneralToolKit.fetcher.get52WeekLow(symbol);
			high = Double.toString(fiftyday);
		}

		double lowAmount = Double.parseDouble(low);
		double highAmount = Double.parseDouble(high);
		
		// Computes a relative threshold to allow for stocks
		// approaching the Fib Levels to trigger trades
		double threshold = ((highAmount - lowAmount) / 50);
		
		values[0] = low;
		values[1] = high;
		values[2] = Double.toString(threshold);

		return values;
	}

	/**
	 * Calculates the high, low, and the threshold values
	 * that will be used by in Fibonacci computation
	 * 
	 * Uses the Stock's high and low of the day as the values
	 * 
	 * @param symbol the Stock
	 * @return the High, Low, and Threshold amounts
	 */
	public static String[] calculateHighAndLow_HDAILY(String symbol) {
		String[] values = new String[3];
		
		// Threshold needs to be extremely small because the values are closer togather
		double threshold = Double.parseDouble(GeneralToolKit.fetcher.getLastTradePriceOnly(symbol)) * .0009;
		
		values[0] = GeneralToolKit.fetcher.getDaysLow(symbol);
		values[1] = GeneralToolKit.fetcher.getDaysHigh(symbol);
		values[2] = Double.toString(threshold);

		return values;
	}

	/**
	 * Calculates the high, low, and the threshold values
	 * that will be used by in Fibonacci computation
	 * 
	 * Uses the Stock's 52 week high and low as the values
	 * 
	 * @param symbol the Stock
	 * @return the High, Low, and Threshold amounts
	 */
	public static String[] calculateHighAndLow_H52WEEK(String symbol) {
		String[] values = new String[3];

		values[0] = GeneralToolKit.fetcher.get52WeekLow(symbol);
		values[1] = GeneralToolKit.fetcher.get52WeekHigh(symbol);

		double lowAmount = Double.parseDouble(values[0]);
		double highAmount = Double.parseDouble(values[1]);
		
		// Computes a relative threshold to allow for stocks
		// approaching the Fib Levels to trigger trades
		double threshold = ((highAmount - lowAmount) / 50);
		values[2] = Double.toString(threshold);

		return values;
	}

	/**
	 * Approximates a Stock's end of day volume
	 * 
	 * @param symbol
	 * @return
	 */
	public static double approximateVolume(String symbol) {
		int endOfDay = 16;
		String time = GeneralToolKit.fetcher.getLastTradeWithTime(symbol).split(" ")[0];
		time = time.replace("\"", "");

		int hour = Integer.parseInt(time.split(":")[0]);

		// Not using min yet in implementation
		// int min = Integer.parseInt(time.split(":")[1]);

		switch (hour) {
		case 1:
			hour = 13;
			break;
		case 2:
			hour = 14;
			break;
		case 3:
			hour = 15;
			break;
		default:
			break;
		}

		int hoursLeft = endOfDay - hour;
		double curVolume = Float.parseFloat(GeneralToolKit.fetcher.getVolume(symbol));
		double approxVolume = curVolume + (curVolume * ((hoursLeft / 6.5)));

		return approxVolume;
	}

	/**
	 * Retrieves how much a company is worth
	 * 
	 * @param symbol Companies symbol
	 * @return how much the company is worth
	 */
	public static double getMarketCapAmount(String symbol) {
		String marketcapString = GeneralToolKit.fetcher.getMarketCapilization(symbol);
		String[] marketcapParts = new String[2];

		// Parse the market cap string returned
		String marketcapAltered = "";
		String amount = "";

		for (int i = 0; i < marketcapString.length() - 1; i++) {
			String s = Character.toString(marketcapString.charAt(i));
			try {
				Integer.parseInt(s);
				marketcapAltered += s;
			} catch (NumberFormatException ex) {
				if (s.equals(".")) {
					marketcapAltered += s;
				} else {
					amount = s;
				}
			}
		}

		marketcapParts[0] = marketcapAltered;
		marketcapParts[1] = amount;

		String capAmount = marketcapParts[0];
		String capMagnitude = marketcapParts[1];

		double marketcap = 0;
		marketcap = Double.parseDouble(capAmount);

		// Need to multiply the market cap by the appropriate factor
		if (capMagnitude.equals("T")) {
			marketcap *= 1000000000000L;
		} else if (capMagnitude.equals("B")) {
			marketcap *= 1000000000;
		} else if (capMagnitude.equals("M")) {
			marketcap *= 1000000;
		}

		return marketcap;
	}
}