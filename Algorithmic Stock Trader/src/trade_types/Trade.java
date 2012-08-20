package trade_types;

import java.io.Serializable;

import stocks.Stock;

/**
 * Represents a Stock Transaction
 * 
 * @author Steven Shaw
 * @email  sps5688@rit.edu
 *
 */
@SuppressWarnings("serial")
public abstract class Trade implements Serializable {

	private Stock symbol;
	private int shares;
	private double price;
	private String orderType;
	private String tradeType;
	private static final String[] orderTypes = {"Buy", "Sell", "Sell Short", "Buy to Cover" };
	private static final String[] priceTypes = { "market", "market on close", "limit", "stop limit",
												 "trailing stop dollar", "trailing stop percent", "stop"};
	
	public Trade(String buyOrSell, Stock symbol, int shares) {
		setShares(shares);
		setSymbol(symbol);
		setOrderType(buyOrSell);
	}
	
	/**
	 * Returns the types of trades
	 * 
	 * @return the trade types
	 */
	public String getTradeType(){
		return tradeType;
	}
	
	/**
	 * Returns the types of orders
	 * 
	 * @return the order types
	 */
	public String[] getOrderTypes(){
		return orderTypes;
	}
	
	/**
	 * Returns a list of available trade types
	 * 
	 * @return the list
	 */
	public static String[] getPriceTypes(){
		return priceTypes;
	}

	/**
	 * Sets the symbol for the trade
	 * 
	 * @param symbol
	 */
	public void setSymbol(Stock symbol) {
		this.symbol = symbol;
	}
	
	/**
	 * Sets the share amount for the trade
	 * 
	 * @param shares
	 */
	public void setShares(int shares) {
		this.shares = shares;
	}
	
	/**
	 * Sets the price for the trade
	 * 
	 * @param price
	 */
	public void setPrice(double price) {
		this.price = price;
	}
	
	/**
	 * Sets the order type for the trade
	 * 
	 * @param orderType
	 */
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	/**
	 * Gets the symbol of the trade
	 * 
	 * @return
	 */
	public Stock getSymbol() {
		return symbol;
	}

	/**
	 * Gets the share amount of the trade
	 * 
	 * @return
	 */
	public int getShares() {
		return shares;
	}

	/**
	 * Gets the price of the trade
	 * 
	 * @return
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * Gets the order type of the trade
	 * 
	 * @return
	 */
	public String getOrderType() {
		return orderType;
	}

}