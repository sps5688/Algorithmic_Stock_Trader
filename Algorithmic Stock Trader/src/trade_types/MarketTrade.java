package trade_types;

import java.io.Serializable;

import stocks.Stock;

/**
 * Represents a Market Order Stock Transaction
 * 
 * @author Steven Shaw
 * @email  sps5688@rit.edu
 *
 */
@SuppressWarnings("serial")
public class MarketTrade extends Trade implements Serializable {

	private Stock symbol;
	private int shares;
	private String orderType;
	private double price;
	private final String tradeType = "market";

	public MarketTrade(String orderType, Stock symbol, int shares) {
		super(orderType, symbol, shares);
	}

	@Override
	public String getTradeType(){
		return tradeType;
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