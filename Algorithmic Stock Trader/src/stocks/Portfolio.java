package stocks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


import outgoing_data_sources.DummyService;
import trade_types.MarketTrade;
import trade_types.Trade;
import utilities.GeneralToolKit;

/**
 * Represents a Trader's Portfolio
 * 
 * @author Steven Shaw
 * @email  sps5688@rit.edu
 *
 */
@SuppressWarnings("serial")
public class Portfolio implements Serializable {

	private List<Trade> positions = new ArrayList<Trade>();
	private List<Trade> tradeHistory = new ArrayList<Trade>();
	private ArrayList<String> watchList = new ArrayList<String>();
	private double moneyAmount;
	private double availableFunds;
	private double netWorth;
	private String accountName;

	public Portfolio(String accountName, double funds) {
		moneyAmount = funds;
		availableFunds = funds;
		netWorth = funds;
		this.accountName = accountName;
	}

	/**
	 * Retrieves the trader's currently owned stocks
	 * 
	 * @return List of Trades
	 */
	public List<Trade> getStocks() {
		return positions;
	}

	/**
	 * Processes the new Trade
	 * 
	 * @param newTrade the trade to be processed
	 */
	public void addStockTransaction(Trade newTrade) {
		// Sends trade through outgoing service and updates stock price
		DummyService service = new DummyService();
		newTrade = service.executeOrder(newTrade);

		// Searches positions list
		boolean found = false;
		for (int i = 0; i < positions.size(); i++) {
			// If stock is already owned
			if (positions.get(i).getSymbol().symbol.equals(newTrade.getSymbol().symbol)) {
				
				// Creates a new object so trade history does not have the same object address
				// Currently only Market Trades are implemented
				Trade existingTrade = null;
				if(newTrade.getTradeType().equals("market")){
					existingTrade = new MarketTrade(newTrade.getOrderType(), newTrade.getSymbol(), newTrade.getShares());
				}else if(newTrade.getTradeType().equals("market on close")){
					existingTrade = new MarketTrade(newTrade.getOrderType(), newTrade.getSymbol(), newTrade.getShares());
				}else if(newTrade.getTradeType().equals("limit")){
					existingTrade = new MarketTrade(newTrade.getOrderType(), newTrade.getSymbol(), newTrade.getShares());
				}else if(newTrade.getTradeType().equals("stop limit")){
					existingTrade = new MarketTrade(newTrade.getOrderType(), newTrade.getSymbol(), newTrade.getShares());
				}else if(newTrade.getTradeType().equals("stop")){
					existingTrade = new MarketTrade(newTrade.getOrderType(), newTrade.getSymbol(), newTrade.getShares());
				}else if(newTrade.getTradeType().equals("trailing stop dolar")){
					existingTrade = new MarketTrade(newTrade.getOrderType(), newTrade.getSymbol(), newTrade.getShares());
				}else if(newTrade.getTradeType().equals("trailing stop percent")){
					existingTrade = new MarketTrade(newTrade.getOrderType(), newTrade.getSymbol(), newTrade.getShares());
				}
				
				existingTrade.setPrice(newTrade.getPrice());
				tradeHistory.add(existingTrade);
				
				// Updates share amount if already owned
				updateShareAmount(existingTrade.getSymbol(), existingTrade.getShares(), existingTrade.getOrderType());
				
				found = true;
				break;
			}
		}

		// If not owned, add to positions list
		if (found == false) {
			tradeHistory.add(newTrade);
			positions.add(newTrade);
		}
	}

	/**
	 * Removes a stock from a Trader's position list
	 * 
	 * @param toRemove Stock that will be removed
	 */
	public void removeStock(Stock toRemove) {
		for (int i = 0; i < positions.size(); i++) {
			if (positions.get(i).getSymbol() == toRemove) {
				positions.remove(positions.get(i));
			}
		}
	}

	/**
	 * Returns how many stocks the Trader owns
	 * 
	 * @return
	 */
	public int getPositionsSize() {
		return positions.size();
	}

	/**
	 * Sets the money amount for the Trader
	 * 
	 * @param amount
	 */
	public void setMoneyAmount(double amount) {
		moneyAmount = amount;
	}

	/**
	 * Gets the money amount for the Trader
	 * 
	 * @return
	 */
	public double getMoneyAmount() {
		return moneyAmount;
	}

	/**
	 * Sets the Trader's account name
	 * 
	 * @param name
	 */
	public void setAccountName(String name) {
		accountName = name;
	}
	
	/**
	 * Gets the Trader's account name
	 * 
	 * @return
	 */
	public String getAccountName() {
		return accountName;
	}

	/**
	 * Sets the Trader's available funds
	 * 
	 * @param amount
	 */
	public void setAvailableFunds(double amount) {
		availableFunds = amount;
	}

	/**
	 * Gets the Trader's available funds
	 * 
	 * @return
	 */
	public double getAvailableFunds() {
		return availableFunds;
	}

	/**
	 * Updates how much money the Trader has left to spend
	 * 
	 * @param amount
	 */
	public void updateAvailableFunds(double amount) {
		availableFunds += amount;
	}

	/**
	 * Prints out the Trader's currently owned stocks
	 * 
	 */
	public void printPortfolio() {
		Stock curStock;
		int curShares;
		double curPrice;
		Trade curTrade;

		for (int i = 0; i < positions.size(); i++) {
			curTrade = positions.get(i);
			curShares = curTrade.getShares();
			curStock = curTrade.getSymbol();
			curPrice = curTrade.getPrice();

			System.out.println(curStock.symbol + " " + curShares + " "
					+ curPrice);
		}
	}

	/**
	 * Prints out the Trader's watch list
	 * 
	 */
	public void printWatchList() {
		for (String curStock : watchList) {
			System.out.print(curStock + " ");
		}
		System.out.println();
	}

	/**
	 * Adds a symbol to the Trader's watch list
	 * 
	 * @param symbol
	 */
	public void addToWatchList(String symbol) {
		if(GeneralToolKit.verifySymbol(symbol)){
			watchList.add(symbol);	
		}
	}

	/**
	 * Gets the number of shares that the Trader owns of a stock
	 * 
	 * @param symbol the stock for lookup
	 * @return number of shares
	 */
	public int getShareAmount(Stock symbol) {
		Trade curTrade;
		int shareTotal = 0;

		for (int i = 0; i < positions.size(); i++) {
			curTrade = positions.get(i);

			if (curTrade.getSymbol() == symbol) {
				shareTotal += curTrade.getShares();
			}
		}
		return shareTotal;
	}

	/**
	 * Returns the average priced paid for a stock
	 * 
	 * @param symbol the stock for lookup
	 * @return average priced paid
	 */
	public double getPriceAmount(Stock symbol) {
		Trade curTrade;
		double prices = 0.0;
		int count = 0;

		// Adds up all the prices and counts how many times it was bought
		for (int i = 0; i < positions.size(); i++) {
			curTrade = positions.get(i);

			if (curTrade.getSymbol().symbol.equals(symbol.symbol)) {
				prices += curTrade.getPrice();
				count++;
			}
		}

		// Computes average price
		if (count != 0) {
			return (prices / count);
		} else {
			return 0;
		}
	}

	/**
	 * Prints the Trader's transactions
	 */
	public void printTradeHistory() {
		Trade curTrade;
		int curShares;
		Stock curStock;
		double curPrice;
		String curOrderType;

		// Iterates through tradeHistory list and prints out information
		for (int i = 0; i < tradeHistory.size(); i++) {
			curTrade = tradeHistory.get(i);
			curOrderType = curTrade.getOrderType();
			curShares = curTrade.getShares();
			curStock = curTrade.getSymbol();
			curPrice = curTrade.getPrice();

			System.out.println(curOrderType + " " + curStock.symbol + " " + curShares + " " + curPrice);
		}
	}

	/**
	 * Updates share amount for an already owned stock
	 * 
	 * @param toUpdate
	 * @param shareAmount
	 * @param orderType
	 */
	public void updateShareAmount(Stock toUpdate, int shareAmount, String orderType) {
		int shares = 0;
		int newShares = 0;

		// Searches for stock to update
		for (int i = 0; i < positions.size(); i++) {
			if (positions.get(i).getSymbol() == toUpdate) {
				shares = positions.get(i).getShares();

				if (orderType.equals("Buy")) {
					newShares = shares + shareAmount;
					
					// Updates share amount
					positions.get(i).setShares(newShares);
				} else {
					newShares = shares - shareAmount;

					// If no shares are left, remove from positions list
					// Otherwise, update share amount
					if (newShares == 0) {
						removeStock(toUpdate);
					} else {
						positions.get(i).setShares(newShares);
					}
				}
			}
		}
	}

	/**
	 * Returns the Trader's watch list
	 * 
	 * @return
	 */
	public ArrayList<String> getWatchList() {
		return watchList;
	}

	/**
	 * Removes a stock from the Trader's watch list
	 * 
	 * @param symbol
	 */
	public void removeFromWatchList(String symbol) {
		if(GeneralToolKit.verifySymbol(symbol)){
			for (int i = 0; i < watchList.size(); i++) {
				if (watchList.get(i).equals(symbol)) {
					watchList.remove(watchList.get(i));
				}
			}	
		}
	}

	/**
	 * Updates the Trader's net worth
	 * 
	 * @param amount
	 */
	public void updateNetWorth(double amount) {
		netWorth += amount;
	}

	/**
	 * Returns the trader's net worth
	 * 
	 * @return
	 */
	public double getNetWorth() {
		return netWorth;
	}

	/**
	 * Returns the percent change from when the Trader started
	 * 
	 * @return
	 */
	public String getPercentChange() {
		StringBuilder result = new StringBuilder();
		double difference = netWorth - moneyAmount;

		if (difference < 0) {
			result.append("-");
		} else if (difference > 0) {
			result.append("+");
		} else {
			result.append("");
		}

		double percentChange = ((moneyAmount - netWorth) / netWorth) * 100;
		result.append(Double.toString(percentChange) + "%");

		return result.toString();
	}
}