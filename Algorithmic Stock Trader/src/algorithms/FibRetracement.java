package algorithms;

import java.util.ArrayList;
import java.util.List;


import stocks.Portfolio;
import stocks.Stock;
import trade_types.MarketTrade;
import trade_types.Trade;
import utilities.AlgorithmToolKit;
import utilities.GeneralToolKit;


/**
 * 
 * Fibonacci Retracement Algorithm:
 *  An overview of Fibonacci Retracements can be found here:
 *  	http://www.investopedia.com/ask/answers/05/FibonacciRetracement.asp#axzz2425Rzunp
 * 
 * 	This algorithm uses the days high and low as the levels. I have found that 
 *  using the days high and low does not yield profits when day trading.
 *  To increase profits, use other values as the high and low. Be aware that as the the numerical 
 *  distance between the high and low levels increases, the frequency of trades decreases.
 * 
 * This Algorithm performs the following actions for each stock in the traders watchlist:
 * 
 * Step 1:
 * 	Calculates the Fibonacci Retracement levels using either the stock's high and low of the day
 * 	or, if the high and low of the day are too close, it uses either the 52 week high as the high and
 *  the 50 day moving average as the low or the 50 day moving average as the high and the 52 week
 *  low as the low depending on the stock's current price.
 *  
 * Step 2:
 *  It then determines if the stock's current price is within the threshold of each fibonacci level.
 * 	If it is, it approximates the stock's end of day volume level.
 * 
 * Step 3:
 *  If the stock is approaching a resistance level with heavy volume, it returns a buy action.
 *  If the stock is approaching a support level with heavy volume, it returns a sell action.
 *  If the stock is approaching a resistance level with light volume, it returns a sell action.
 *  If the stock is approaching a support level with light volume, it returns a buy action.
 *  Else, it returns null indicating no action.
 *  
 * Step 4:
 *  For each buy action, it dynamically determines the amount of shares to buy based on the
 *  allotted money amount for the specific group that the stock is a part of.
 *  The three groups are: speculative stocks, small cap stocks, and large cap stocks.
 *  Each group has a specific number of stocks that it can contain.
 *  
 *  For each sell action, it retrieves the number of shares that were bought and the original
 *  price paid. It then sells the stock and computes the result of the trade.
 * 
 * Step 5:
 * 	It then updates the trader's positions list, net worth (if a sell occurred), 
 *  and available money (if a buy occurred).
 * 
 * 
 * @author Steven Shaw
 * @email  sps5688@rit.edu
 *
 */
public class FibRetracement implements Algorithm {

	// Portfolio and watch list for a trader
	private Portfolio myPortfolio;
	private ArrayList<String> watchList;

	// Number of stocks in each group
	private final int specStockNumber = 5;
	private final int smallStockNumber = 10;
	private final int largeStockNumber = 10;
	
	// Indicates the dollar amount for each stock group
	private final double specStockAmount = 350000000;
	private final double smallStockAmount = 2000000000;
	
	// Allocated money for each stock group
	private double allowedSpecMoneyAmount;
	private double allowedSmallMoneyAmount;
	private double allowedLargeMoneyAmount;
	
	@Override
	public void setup(Portfolio portfolio){
		// Sets up portfolio and watch list
		myPortfolio = portfolio;
		watchList = portfolio.getWatchList();

		// Sets up the alloted money amount for each group of stocks
		double moneyAmount = myPortfolio.getMoneyAmount();
		int totalStocks = specStockNumber + smallStockNumber + largeStockNumber;
		
		allowedSpecMoneyAmount = moneyAmount * ((float) specStockNumber / totalStocks);
		allowedSmallMoneyAmount = moneyAmount * ((float) smallStockNumber / totalStocks);
		allowedLargeMoneyAmount = moneyAmount * ((float) largeStockNumber / totalStocks);
	}

	@Override
	public Trade[] evaluate() {
		Trade[] toReturn = new Trade[watchList.size()];

		// Makes move for each stock in watch list
		int counter = 0;
		for (String stock : watchList) {
			toReturn[counter] = determineFibonacciMove(stock);
			counter++;
		}

		return toReturn;
	}
	
	/**
	 * Determines a move for a single stock. 
	 * 
	 * @param symbol the symbol of the stock
	 * @return the trade (null if no move).
	 */
	private Trade determineFibonacciMove(String symbol) {
		System.out.println("\nMaking move for " + symbol);
		Trade move = null;

		// Verification that the Stock Market is open
		if (GeneralToolKit.isMarketOpen()) {
			double daysHigh = Double.parseDouble(GeneralToolKit.fetcher.getDaysHigh(symbol));
			double daysLow = Double.parseDouble(GeneralToolKit.fetcher.getDaysLow(symbol));
			double curPrice = Double.parseDouble(GeneralToolKit.fetcher.getLastTradePriceOnly(symbol));
			
			// Computes Values to be used as the high and low for the Fibonacci Retracement calculation
			// If the days high and low are close, use the averages instead
			String[] fibonacciCompVals;
			if ((daysHigh - daysLow) <= (curPrice * .007)) {
				fibonacciCompVals = AlgorithmToolKit.calculateHighAndLow_HLAVG(symbol);
			} else {
				fibonacciCompVals = AlgorithmToolKit.calculateHighAndLow_HDAILY(symbol);
			}

			double low = Double.parseDouble(fibonacciCompVals[0]);
			double high = Double.parseDouble(fibonacciCompVals[1]);
			double threshold = Double.parseDouble(fibonacciCompVals[2]);

			int sharesToSell = 0;
			int sharesToBuy = 0;
			
			// Calculates Fibonacci Retracement values
			ArrayList<Double> levels = AlgorithmToolKit.calcFibRetrace(high, low);
			double approximateVolume = AlgorithmToolKit.approximateVolume(symbol);
			
			// Retrieves the Stock's average daily volume over a period of 10 days
			double tenDayVolAvg = Double.parseDouble(GeneralToolKit.fetcher.getAverageDailyVolume(symbol));

			System.out.println("Current Price: " + curPrice);
			System.out.println("Threshold of " + threshold);

			// For each level, determine if the Stock's current price is within the threshold.
			// If it is, it then determines if it has heavy or light volume by the time the market is closed.
			// It then determines a move based on the volume.
			for (int i = 0; i < levels.size(); i++) {
				// Support Case
				if (curPrice >= levels.get(i)) {

					if (curPrice <= (levels.get(i) + threshold)) {
						System.out.println("Potential Support at: " + levels.get(i) + " within threshold");

						if (approximateVolume >= tenDayVolAvg) {
							System.out.println("Heavy Volume. Should sell.");
							sharesToSell = determineShares(symbol);
						} else {
							System.out.println("Light Volume. Should buy.");
							sharesToBuy = determineShares(symbol);
						}
					} else {
						System.out.println("Potential Support at: " + levels.get(i) + " not within threshold");
						System.out.println("Not doing anythng.");
					}
					// Resistance Case
				} else if (curPrice <= levels.get(i)) {

					if (curPrice >= (levels.get(i) - threshold)) {
						System.out.println("Potential Resistance at: " + levels.get(i) + " within threshold");

						if (approximateVolume >= tenDayVolAvg) {
							System.out.println("Heavy volume. Should buy.");
							sharesToBuy = determineShares(symbol);
						} else {
							System.out.println("Light volume. Should sell.");
							sharesToSell = determineShares(symbol);
						}
					} else {
						System.out.println("Potential Resistance at: " + levels.get(i) + " not within threshold");
						System.out.println("Not doing anything.");
					}
				} else {
					System.out.println("Unknown Level: " + levels.get(i));
				}
			}

			// If there is a trade to be made, construct Trade object
			if (sharesToBuy != 0) {
				move = new MarketTrade("Buy", Stock.get(symbol), sharesToBuy);
			} else if (sharesToSell != 0) {
				move = new MarketTrade("Sell", Stock.get(symbol), sharesToSell);
			}
		}

		// Returns null if there is no move for the Stock
		return move;
	}

	/**
	 * Dynamically determines number of shares to buy based on which
	 * group the stock is in as well as how much money is allocated to that group.
	 * 
	 * @param symbol the stock
	 * @return the number of shares
	 */
	private int determineShares(String symbol) {
		String group = getGroup(symbol);
		List<Trade> stocks = new ArrayList<Trade>();
		int currentNumberInGroup;
		int shareAmount = 0;

		// If there is room for another stock of that type in the portfolio
		if (group.equals("spec")) {
			stocks = getSpecStocks();
			currentNumberInGroup = stocks.size();

			if (currentNumberInGroup < specStockNumber) {
				double allowedMoneyPerStock = allowedSpecMoneyAmount / specStockNumber;
				double curPrice = Double.parseDouble(GeneralToolKit.fetcher.getLastTradePriceOnly(symbol));
				
				shareAmount = (int) (allowedMoneyPerStock / curPrice);
			}
		} else if (group.equals("small")) {
			stocks = getSmallCapStocks();
			currentNumberInGroup = stocks.size();

			if (currentNumberInGroup < smallStockNumber) {
				double allowedMoneyPerStock = allowedSmallMoneyAmount / smallStockNumber;
				double curPrice = Double.parseDouble(GeneralToolKit.fetcher.getLastTradePriceOnly(symbol));

				shareAmount = (int) (allowedMoneyPerStock / curPrice);
			}
		} else {
			stocks = getLargeCapStocks();
			currentNumberInGroup = stocks.size();

			if (currentNumberInGroup < largeStockNumber) {
				double allowedMoneyPerStock = allowedLargeMoneyAmount / largeStockNumber;
				double curPrice = Double.parseDouble(GeneralToolKit.fetcher.getLastTradePriceOnly(symbol));

				shareAmount = (int) (allowedMoneyPerStock / curPrice);
			}
		}
		return shareAmount;
	}

	/**
	 * Gets the group of a stock.
	 * The three groups are: Speculative, Small Cap, and Large Cap
	 * 
	 * @param symbol
	 * @return
	 */
	private String getGroup(String symbol) {
		double marketcap = AlgorithmToolKit.getMarketCapAmount(symbol);

		if (marketcap <= specStockAmount) {
			return "spec";
		} else if (marketcap <= smallStockAmount) {
			return "small";
		} else {
			return "large";
		}
	}

	/**
	 * Gets all of the Stocks that are in the Speculative group
	 * 
	 * @return a list of Speculative Stocks
	 */
	private List<Trade> getSpecStocks() {
		List<Trade> stocks = myPortfolio.getStocks();
		List<Trade> specStocks = new ArrayList<Trade>();

		for (Trade curTrade : stocks) {
			String symbol = curTrade.getSymbol().symbol;
			String group = getGroup(symbol);

			if (group.equals("spec")) {
				specStocks.add(curTrade);
			}
		}
		return specStocks;
	}

	/**
	 * Gets all of the Stocks that are in the Small Cap group
	 * 
	 * @return a list of Small Cap Stocks
	 */
	private List<Trade> getSmallCapStocks() {
		List<Trade> stocks = myPortfolio.getStocks();
		List<Trade> smallCapStocks = new ArrayList<Trade>();

		for (Trade curTrade : stocks) {
			String symbol = curTrade.getSymbol().symbol;
			String group = getGroup(symbol);

			if (group.equals("small")) {
				smallCapStocks.add(curTrade);
			}
		}
		return smallCapStocks;
	}

	/**
	 * Gets all of the Stocks that are in the Large Cap group
	 * 
	 * @return a list of Large Cap Stocks
	 */
	private List<Trade> getLargeCapStocks() {
		List<Trade> stocks = myPortfolio.getStocks();
		List<Trade> largeCapStocks = new ArrayList<Trade>();

		for (Trade curTrade : stocks) {
			String symbol = curTrade.getSymbol().symbol;
			String group = getGroup(symbol);

			if (group.equals("large")) {
				largeCapStocks.add(curTrade);
			}
		}
		return largeCapStocks;
	}
}