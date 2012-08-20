package incoming_data_sources;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Uses the Yahoo! Finance API to Retrieve stock data
 * 
 * @author 	Steven Shaw
 * @email 	sps5688@rit.edu
 * 
 * TODO Handle errors better
 * TODO Remove delayed entries that have a real time alternative
 * TODO Format the tag entries when printing them
 */

public class YahooFetcher {

	private final String baseURL = "http://finance.yahoo.com/d/quotes.csv?s=";
	private final String tagURL = "&f=";
	
	@SuppressWarnings("serial")
	private final Map<String, String> tags = new HashMap<String, String>() { {
		put("Ask", "a");
		put("Average Daily Volume", "a2");
		put("Ask Size", "a5");
		put("Bid", "b");
		put("Ask Real Time", "b2");
		put("Bid Real Time", "b3");
		put("Book Value", "b4");
		put("Bid Size", "b6");
		put("Change & Percent Change", "c");
		put("Change", "c1");
		put("Commission", "c3");
		put("Change Real Time", "c6");
		put("After Hours Change Real Time", "c8");
		put("Dividend/Share", "d");
		put("Last Trade Date", "d1");
		put("Trade Date", "d2");
		put("Earnings/Share", "e");
		put("Error Indication", "e1");
		put("EPS Esimate Current Year", "e7");
		put("EPS Estimate Next Year", "e8");
		put("EPS Estimate Next Quarter", "e9");
		put("Float Shares", "f6");
		put("Day's Low", "g");
		put("Day's High", "h");
		put("52-Week Low", "j");
		put("52-Week High", "k");
		put("Holdings Gain Percent", "g1");
		put("Annualized Gain", "g2");
		put("Holdings Gain", "g4");
		put("Holdings Gain Percent", "g5");
		put("Holdings Gain Real Time", "g6");
		put("More Info", "i");
		put("Order Book Real Time", "i5");
		put("Market Capitalization", "j1");
		put("Market Capitalization Real Time", "j3");
		put("EBITDA", "j4");
		put("Change From 52-week Low", "j5");
		put("Percent Change From 52-Week Low", "j6");
		put("Last Trade Real Time With Time", "k1");
		put("Change Percent Real Time", "k2");
		put("Last Trade Size", "k3");
		put("Change From 52-Week High", "k4");
		put("Percent Change From 52-Week High", "k5");
		put("Last Trade With Time", "l");
		put("Last Trade Price Only", "l1");
		put("High Limit", "l2");
		put("Low Limit", "l3");
		put("Days Range", "m");
		put("Days Range Real Time", "m2");
		put("50-Day Moving Average", "m3");
		put("200-Day Moving Average", "m4");
		put("Change From 200-Day Moving Average", "m5");
		put("Percent Change From 200-Day Moving Average", "m6");
		put("Change From 50-Day Moving Average", "m7");
		put("Percent Change From 50-Day Moving Average", "m8");
		put("Name", "n");
		put("Notes", "n4");
		put("Open", "o");
		put("Previous Close", "p");
		put("Price Paid", "p1");
		put("Change in Percent", "p2");
		put("Price/Sales", "p5");
		put("Price/Book", "p6");
		put("Ex-Dividend Date", "q");
		put("P/E Ratio", "r");
		put("Dividend Pay Date", "r1");
		put("P/E Ratio Real Time", "r2");
		put("PEG Ratio", "r5");
		put("Price/EPS Estimate Current Year", "r6");
		put("Price/EPS Estimate Next Year", "r7");
		put("Symbol", "s");
		put("Shares Owned", "s1");
		put("Short Ratio", "s7");
		put("Last Trade Time", "t1");
		put("Trade Links", "t6");
		put("Ticker Trend", "t7");
		put("1 Year Target Price", "t8");
		put("Volume", "v");
		put("Holdings Value", "v1");
		put("Ask", "Holdings Value Real Time");
		put("52-Week Range", "w");
		put("Days Value Change", "w1");
		put("Days Value Change Real Time", "w4");
		put("Stock Exchange", "x");
		put("Dividend Yield", "y");
		}
	};
	
	/**
	 * Prints the available tags to retrieve stock data
	 * 
	 */
	public void printTags(){
		for (Entry<String, String> curTag : tags.entrySet()) {
			System.out.println(curTag.getKey() + ": " +  curTag.getValue());
		}
	}

	/**
	 * Returns the map of tags
	 * @return the tags
	 */
	public Map<String, String> getTagMap(){
		return tags;
	}

	/**
	 * Returns a list of stock data for for a list of symbols
	 * 
	 * @param symbols
	 * @param tags
	 * @return
	 */
	public String getMultipleSymbolData(String[] symbols, String[] tags) {
		URL url = null;
		String urlString;
		String stockData;

		// Constructs URL
		urlString = baseURL;

		for (String symbol : symbols) {
			urlString = urlString + symbol + "+";
		}

		// Strips last + from URL and appends the tag URL
		urlString = urlString.substring(0, urlString.length() - 1);
		urlString = urlString + tagURL;

		// Adds each tag to the URL
		for (String tag : tags) {
			urlString = urlString + tag;
		}

		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves Data
		stockData = processURL(url);

		return stockData;
	}

	/**
	 * Returns a list of stock data for a single symbol
	 * 
	 * @param symbol
	 * @param tags
	 * @return
	 */
	public String getMultipleTagData(String symbol, String[] tags) {
		URL url = null;
		String stockData = null;
		String urlString = baseURL + symbol + tagURL;

		for (String curTag : tags) {
			urlString = urlString + curTag;
		}

		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		stockData = processURL(url);

		return stockData;
	}

	/**
	 * Returns the current ask price of the stock
	 * 
	 * @param symbol the stock to use
	 * @return the ask price
	 */
	public String getAskPrice(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "a");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the current bid price of the stock
	 * 
	 * @param symbol the stock to use
	 * @return the bid price
	 */
	public String getBid(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "b");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return processURL(url);
	}

	/**
	 * Returns the book value of the stock
	 * 
	 * @param symbol the stock to use
	 * @return the book value
	 */
	public String getBookValue(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "b4");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the net change of the stock
	 * 
	 * @param symbol the stock to use
	 * @return the net change
	 */
	public String getChange(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "c1");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the after hours change of the stock
	 * 
	 * @param symbol the stock to use
	 * @return the after hours change
	 */
	public String getAfterHoursChange(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "c8");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the trade date of the stock
	 * 
	 * @param symbol the stock to use
	 * @return the trade date
	 */
	public String getTradeDate(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "d2");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the Earnings per share of the stock
	 * 
	 * @param symbol the stock to use
	 * @return the earnings per share
	 */
	public String getEPSEstimate(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "e7");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the floating shares of the stock
	 * 
	 * @param symbol the stock to use
	 * @return the number of floating shares
	 */
	public String getFloatShares(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "f6");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's 52 week low
	 * 
	 * @param symbol the stock to use
	 * @return the 52 week low price
	 */
	public String get52WeekLow(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "j");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the Annualized Gain of the stock
	 * 
	 * @param symbol the stock to use
	 * @return the Annualized Gain
	 */
	public String getAnnualizedGain(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "g3");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the Holdings Gain of the stock in real time
	 * 
	 * @param symbol the stock to use
	 * @return the real time holdings gain
	 */
	public String getHoldingsGainRealTime(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "g6");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's market cap
	 * 
	 * @param symbol the stock to use
	 * @return the market cap amount
	 */
	public String getMarketCapilization(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "j1");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the change from the stock's 52 week low
	 * 
	 * @param symbol the stock to use
	 * @return the change amount
	 */
	public String getChangeFrom52WeekLow(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "j5");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the percent change of the stock
	 * 
	 * @param symbol the stock to use
	 * @return the percent change
	 */
	public String getChangePercent(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "k2");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the percent change from the stock's 52 week high
	 * 
	 * @param symbol the stock to use
	 * @return the percent change
	 */
	public String getChangePercentFrom52WeekHigh(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "k5");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the high limit of the stock
	 * 
	 * @param symbol the stock to use
	 * @return the high limit
	 */
	public String getHighLimit(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "12");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's day range
	 * 
	 * @param symbol the stock to use
	 * @return the day range
	 */
	public String getDayRange(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "m2");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the change from the 200 day moving average
	 * 
	 * @param symbol the stock to use
	 * @return the change
	 */
	public String getChangeFrom200DayMovingAverage(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "m5");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the change from the 50 day moving average
	 * 
	 * @param symbol the stock to use
	 * @return the change
	 */
	public String getPercentChangeFrom50DayMovingAverage(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "m8");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's opening price
	 * 
	 * @param symbol the stock to use
	 * @return the opening price
	 */
	public String getOpen(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "o");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the after hours change in percent of the stock
	 * 
	 * @param symbol the stock to use
	 * @return the change in percent
	 */
	public String getChangeInPercent(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "p2");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's Ex Dividend Date
	 * 
	 * @param symbol the stock to use
	 * @return the date
	 */
	public String getExDividendDate(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "q");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's price to earnings ratio
	 * 
	 * @param symbol the stock to use
	 * @return the ratio
	 */
	public String getPriceEarningsRatio(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "r2");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's price to earnings estimate for next year
	 * 
	 * @param symbol the stock to use
	 * @return the estimate
	 */
	public String getPriceToEPSEstimateNextYear(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "r7");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's short ratio
	 * 
	 * @param symbol the stock to use
	 * @return the ratio
	 */
	public String getShortRatio(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "s7");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's ticker trend
	 * 
	 * @param symbol the stock to use
	 * @return the trend
	 */
	public String getTickerTrend(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "t7");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's holding value
	 * 
	 * @param symbol the stock to use
	 * @return the value
	 */
	public String getHoldingsValue(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "v1");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's day value change
	 * 
	 * @param symbol the stock to use
	 * @return the value change
	 */
	public String getDaysValueChange(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "w1");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's dividend yield
	 * 
	 * @param symbol the stock to use
	 * @return the yield
	 */
	public String getDividendYield(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "y");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's average daily volume
	 * 
	 * @param symbol the stock to use
	 * @return the estimate
	 */
	public String getAverageDailyVolume(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "a2");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's ask price in real time
	 * 
	 * @param symbol the stock to use
	 * @return the real time ask price
	 */
	public String getAskRealTime(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "b2");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's bid size
	 * 
	 * @param symbol the stock to use
	 * @return the bid size
	 */
	public String getBidSize(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "b6");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's commission
	 * 
	 * @param symbol the stock to use
	 * @return the commission amount
	 */
	public String getCommission(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "c3");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's dividend per share amount
	 * 
	 * @param symbol the stock to use
	 * @return the amount
	 */
	public String getDividendPerShare(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "d");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's earnings per share
	 * 
	 * @param symbol the stock to use
	 * @return the earnings
	 */
	public String getEarningsPerShare(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "e");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's earnings per share next year estimate
	 * 
	 * @param symbol the stock to use
	 * @return the estimate
	 */
	public String getEPSEstimateNextYear(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "e8");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's low of the day
	 * 
	 * @param symbol the stock to use
	 * @return the low price
	 */
	public String getDaysLow(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "g");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's 52 week high
	 * 
	 * @param symbol the stock to use
	 * @return the high amount
	 */
	public String get52WeekHigh(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "k");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's holdings gain
	 * 
	 * @param symbol the stock to use
	 * @return the holdings gain
	 */
	public String getHoldingsGain(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "g4");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the more information about the stock
	 * 
	 * @param symbol the stock to use
	 * @return the information
	 */
	public String getMoreInfo(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "i");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's market cap in real time
	 * 
	 * @param symbol the stock to use
	 * @return the real time market cap
	 */
	public String getMarketCapRealTime(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "j3");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's percent change from it's 52 week low
	 * 
	 * @param symbol the stock to use
	 * @return the percent change
	 */
	public String getPercentChangeFrom52WeekLow(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "j6");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's last trade size
	 * 
	 * @param symbol the stock to use
	 * @return the size
	 */
	public String getLastTradeSize(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "k3");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's last trade amount with the trade time
	 * 
	 * @param symbol the stock to use
	 * @return the trade amount and time it was traded
	 */
	public String getLastTradeWithTime(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "l");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's low limit
	 * 
	 * @param symbol the stock to use
	 * @return the low limit
	 */
	public String getLowLimit(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "l3");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's 50 day moving average
	 * 
	 * @param symbol the stock to use
	 * @return the average price amount
	 */
	public String get50DayMovingAverage(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "m3");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock'spercent change from it's 200 day moving average
	 * 
	 * @param symbol the stock to use
	 * @return the percent change
	 */
	public String getPercentChangeFrom200DayMovingAverage(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "m6");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the company name of the stock
	 * 
	 * @param symbol the stock to use
	 * @return the name
	 */
	public String getName(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "n");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's previous close amount
	 * 
	 * @param symbol the stock to use
	 * @return the close amount
	 */
	public String getPreviousClose(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "p");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's price per sale ratio amount
	 * 
	 * @param symbol the stock to use
	 * @return the amount
	 */
	public String getPricePerSalesRatio(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "p5");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's price per earnings ratio
	 * 
	 * @param symbol the stock to use
	 * @return the ratio
	 */
	public String getPricePerEarningsRatio(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "r");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's PEG ratio
	 * 
	 * @param symbol the stock to use
	 * @return the ratio
	 */
	public String getPEGRatio(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "r5");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	
	/**
	 * Returns the stock's symbol
	 * 
	 * @param symbol the stock to use
	 * @return the symbol
	 */
	public String getSymbol(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "s");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's last trade price with the time
	 * 
	 * @param symbol the stock to use
	 * @return the trade price and time
	 */
	public String getLastTradeTime(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "t1");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's price target
	 * 
	 * @param symbol the stock to use
	 * @return the target price amount
	 */
	public String get1YearTargetPrice(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "t8");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's holdings value in real time
	 * 
	 * @param symbol the stock to use
	 * @return the holdings value
	 */
	public String getHoldingsValueRealTime(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "v7");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's day value change in real time
	 * 
	 * @param symbol the stock to use
	 * @return the day value change
	 */
	public String getDaysValueChangeRealTime(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "w4");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's ask size
	 * 
	 * @param symbol the stock to use
	 * @return the ask size
	 */
	public String getAskSize(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "a5");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's bid price in real time
	 * 
	 * @param symbol the stock to use
	 * @return the bid price
	 */
	public String getBidRealTime(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "b3");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's change and percent change
	 * 
	 * @param symbol the stock to use
	 * @return the change and percent change
	 */
	public String getChangeAndPercentChange(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "c");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's change in real time
	 * 
	 * @param symbol the stock to use
	 * @return the change in real time
	 */
	public String getChangeRealTime(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "c6");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's last trade data
	 * 
	 * @param symbol the stock to use
	 * @return the date
	 */
	public String getLastTradeDate(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "d1");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns an error
	 * 
	 * @param symbol the stock to use
	 * @return the error
	 */
	public String getErrorIndication(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "e1");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's Earnings per share estimate for next quarter
	 * 
	 * @param symbol the stock to use
	 * @return the estimate
	 */
	public String getEPSEstimateNextQuarter(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "e9");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's high of day price
	 * 
	 * @param symbol the stock to use
	 * @return the high of day price
	 */
	public String getDaysHigh(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "h");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's holdings gain percent
	 * 
	 * @param symbol the stock to use
	 * @return the holdings gain percent
	 */
	public String getHoldingsGainPercent(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "g1");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's holdings gain percent in real time
	 * 
	 * @param symbol the stock to use
	 * @return the holdings gain in real time
	 */
	public String getHoldingsGainPercentRealTime(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "g5");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's order book in real time
	 * 
	 * @param symbol the stock to use
	 * @return the order book
	 */
	public String getOrderBookRealTime(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "i5");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's earnings before interest, taxes, depreciation, and amoritization amount
	 * 
	 * @param symbol the stock to use
	 * @return the earnings amount
	 */
	public String getEBITDA(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "j4");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's last trade price in real time
	 * 
	 * @param symbol the stock to use
	 * @return the last trade price in real time
	 */
	public String getLastTradeRealTimeWithTime(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "k1");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's change from it's 52 week high
	 * 
	 * @param symbol the stock to use
	 * @return the change amount
	 */
	public String getChangeFrom52WeekHigh(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "k4");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's last trade price
	 * 
	 * @param symbol the stock to use
	 * @return the last trade price
	 */
	public String getLastTradePriceOnly(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "l1");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's day range
	 * 
	 * @param symbol the stock to use
	 * @return the range
	 */
	public String getDaysRange(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "m");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's 200 day moving average
	 * 
	 * @param symbol the stock to use
	 * @return the 200 day moving average amount
	 */
	public String get200DayMovingAverage(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "m4");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's change from the 50 day moving average
	 * 
	 * @param symbol the stock to use
	 * @return the change amount
	 */
	public String getChangeFrom50DayMovingAverage(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "m7");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's notes
	 * 
	 * @param symbol the stock to use
	 * @return the notes
	 */
	public String getNotes(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "n4");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's price paid
	 * 
	 * @param symbol the stock to use
	 * @return the price amount
	 */
	public String getPricePaid(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "p1");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's price book ratio
	 * 
	 * @param symbol the stock to use
	 * @return the ratio
	 */
	public String getPriceBookRatio(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "p6");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's dividend pay date
	 * 
	 * @param symbol the stock to use
	 * @return the date
	 */
	public String getDividendPayDate(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "r1");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's price earnings per share estimate
	 * current year ratio
	 * 
	 * @param symbol the stock to use
	 * @return the ratio
	 */
	public String getPriceEPSEstimateCurrentYearRatio(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "r6");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the how many shares of the stock are owned
	 * 
	 * @param symbol the stock to use
	 * @return the owned share amount
	 */
	public String getSharesOwned(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "s1");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}
	
	/**
	 * Returns the stock's trade links
	 * 
	 * @param symbol the stock to use
	 * @return the trade links
	 */
	public String getTradeLinks(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "t6");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's current volume
	 * 
	 * @param symbol the stock to use
	 * @return the volume
	 */
	public String getVolume(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "v");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock's 52 week range
	 * 
	 * @param symbol the stock to use
	 * @return the 52 week range
	 */
	public String get52WeekRange(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "w");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Returns the stock exchange that the stock is traded on
	 * 
	 * @param symbol the stock to use
	 * @return the stock exchange
	 */
	public String getStockExchange(String symbol) {
		URL url = null;

		try {
			url = new URL(baseURL + symbol + tagURL + "x");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Retrieves the stock data from the constructed URL
		return processURL(url);
	}

	/**
	 * Processes a URL and constructs a string containing the stock data
	 * 
	 * @param url the URL to be processed
	 * @return the stock data
	 */
	private String processURL(URL url) {
		InputStream response = null;
		String processedData = null;

		try {
			response = url.openStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(response));

			for (String line; (line = reader.readLine()) != null;) {

				if (processedData == null) {
					processedData = line + "\n";
				} else {
					processedData = processedData + line + "\n";
				}
			}

			reader.close();
		} catch (Exception e) {
			System.out.println("Error: Unknown error from Yahoo!\n");
		}

		return processedData;
	}
}