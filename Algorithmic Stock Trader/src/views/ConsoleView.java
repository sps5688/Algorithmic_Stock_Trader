package views;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


import algorithms.Algorithm;
import algorithms.FibRetracement;

import stocks.Portfolio;
import stocks.Stock;
import trade_types.MarketTrade;
import trade_types.Trade;
import utilities.AlgorithmToolKit;
import utilities.GeneralToolKit;


/**
 * Console view that controls the flow of the application
 * 
 * @author Steven Shaw
 * @email  sps5688@rit.edu
 *
 */
public class ConsoleView {

	private Portfolio currentPortfolio;
	private Scanner in = new Scanner(System.in);
	private List<Class<? extends Algorithm>> availableAlgorithms = new ArrayList<Class<? extends Algorithm>>();

	public ConsoleView(Portfolio portfolioToUse) {
		currentPortfolio = portfolioToUse;
		
		// List of Algorithms to use
		availableAlgorithms.add(FibRetracement.class);
	}

	/**
	 * Menu for Buying and Selling Stocks
	 */
	private void tradeMenu() {
		String menuChoice;

		System.out.println();

		System.out.println(currentPortfolio.getAccountName() + " " + currentPortfolio.getPercentChange());
		System.out.println("===================================");
		System.out.println("Enter 1 to Buy");
		System.out.println("Enter 2 to Sell");
		System.out.println("Enter 3 to Sell All");
		System.out.println("Enter 4 to Sell Short");
		System.out.println("Enter 5 to Buy To Cover");
		System.out.println("===================================");

		System.out.print("Choice: ");
		menuChoice = in.nextLine();

		if (menuChoice.equals("1")) {
			buyMenu();
		} else if (menuChoice.equals("2")) {
			sellMenu();
		} else if (menuChoice.equals("3")) {
			GeneralToolKit.sellAllStocks(currentPortfolio);
		} else if(menuChoice.equals("4")){
			sellShortMenu();
		} else if(menuChoice.equals("5")){
			buyToCoverMenu();
		} else {
			System.out.println("Incorrect choice");
		}

		mainMenu();
	}

	/**
	 * Main Menu that controls the application
	 */
	private void mainMenu() {
		boolean isValid = true;
		String menuChoice = null;

		System.out.println();

		do {
			System.out.println(currentPortfolio.getAccountName() + " " + currentPortfolio.getPercentChange());
			System.out.println("===================================");
			System.out.println("Enter 1 to get stock data");
			System.out.println("Enter 2 to make a trade");
			System.out.println("Enter 3 to get funds");
			System.out.println("Enter 4 to get trade history");
			System.out.println("Enter 5 to get portfolio");
			System.out.println("Enter 6 to manage watchlist");
			System.out.println("Enter 7 to start simulation");
			System.out.println("Enter 8 to quit");
			System.out.println("===================================");

			System.out.print("Choice: ");
			menuChoice = in.nextLine();

			if (menuChoice.equals("1")) {
				stockDataMenu();
			} else if (menuChoice.equals("2")) {
				tradeMenu();
			} else if (menuChoice.equals("3")) {
				balanceMenu();
			} else if (menuChoice.equals("4")) {
				printTransactionHistory();
			} else if (menuChoice.equals("5")) {
				currentPortfolio.printPortfolio();
				mainMenu();
			} else if (menuChoice.equals("6")) {
				watchListMenu();
			} else if (menuChoice.equals("7")) {
				simulationMenu();
			} else if (menuChoice.equals("8")) {
				serializeData(currentPortfolio);
				System.exit(0);
			} else {
				System.out.println("Invalid choice");
				isValid = false;
			}

			isValid = true;
		} while (isValid == false);
	}

	/**
	 * Menu to look up data about Stocks
	 */
	private void stockDataMenu() {
		String symbol, tags = null;
		String[] tagArray = null;

		System.out.println();

		System.out.println(currentPortfolio.getAccountName() + " " + currentPortfolio.getPercentChange());
		System.out.println("===================================");

		System.out.print("Enter in symbol: ");
		symbol = in.nextLine();

		if(GeneralToolKit.verifySymbol(symbol)){
			System.out.println("\nTag List: ");
			System.out.println("----------");
			GeneralToolKit.fetcher.printTags();

			System.out.print("\nEnter in tags separated by a ',': ");
			tags = in.nextLine();
			tagArray = tags.split(",");

			// Verify each tag
			Map<String, String> tagMap = GeneralToolKit.fetcher.getTagMap();
			for(int i = 0; i < tagArray.length; i++){
				if( !(tagMap.containsValue(tagArray[i])) ){
					System.out.println("Invalid tag");
					mainMenu();
				}
			}
			
			System.out.println(symbol + " data: " + GeneralToolKit.fetcher.getMultipleTagData(symbol, tagArray));
		}

		System.out.println("===================================");
		mainMenu();
	}

	/**
	 * Menu to view the account balance
	 */
	private void balanceMenu() {
		System.out.println();
		System.out.println(currentPortfolio.getAccountName() + " " + currentPortfolio.getPercentChange());
		System.out.println("===================================");
		System.out.println("Available Funds: $" + currentPortfolio.getAvailableFunds());
		System.out.println("===================================");

		mainMenu();
	}

	/**
	 * Menu to manage the account's watch list
	 */
	private void watchListMenu() {
		System.out.println();
		System.out.println(currentPortfolio.getAccountName() + " " + currentPortfolio.getPercentChange());
		System.out.println("===================================");

		// Print watchList
		System.out.print("Watch List: ");
		currentPortfolio.printWatchList();

		System.out.println("Enter 1 to add to watch list");
		System.out.println("Enter 2 to remove from watch list");
		System.out.print("Choice: ");
		String choice = in.nextLine();

		// Add and Remove from watchList
		if (choice.equals("1")) {
			System.out.print("Symbol: ");
			String symbol = in.nextLine();
			currentPortfolio.addToWatchList(symbol);
		} else if (choice.equals("2")) {
			System.out.print("Symbol: ");
			String symbol = in.nextLine();
			currentPortfolio.removeFromWatchList(symbol);
		}else{
			System.out.println("Invalid choice");
		}

		mainMenu();
	}

	/**
	 * Menu to make a Stock purchase
	 */
	private void buyMenu() {
		System.out.println();
		System.out.println(currentPortfolio.getAccountName() + " " + currentPortfolio.getPercentChange());
		System.out.println("===================================");

		if (GeneralToolKit.isMarketOpen()) {
			System.out.print("Enter in symbol: ");
			String whatToBuy = in.nextLine();
			
			if(GeneralToolKit.verifySymbol(whatToBuy)){
				String curPrice = GeneralToolKit.fetcher.getLastTradePriceOnly(whatToBuy);

				System.out.println("Current price is: " + curPrice);
				System.out.print("Enter amount of shares to buy: ");
				String shareAmount = in.nextLine();
				
				// Verifies entered share amount
				int shareNum = 0;
				try{
					shareNum = Integer.parseInt(shareAmount);
					
					if(shareNum <= 0){
						System.out.println("Invalid number of shares");
						mainMenu();
					}
				}catch(NumberFormatException e){
					System.out.println("Invalid number of shares");
					mainMenu();
				}
				
				double costToPurchase = Double.parseDouble(GeneralToolKit.fetcher.getLastTradePriceOnly(whatToBuy)) * (Double.parseDouble(shareAmount));
				double availableFunds = currentPortfolio.getAvailableFunds();
				
				// Verifying Trader has enough funds to make purchase
				if (costToPurchase > availableFunds) {
					System.out.println("Not enough funds to make purchase");
					mainMenu();
				}

				System.out.println("Price types: ");
				
				String[] priceTypes = Trade.getPriceTypes();
				for(int i = 0; i < priceTypes.length; i++){
					System.out.println("  " + priceTypes[i]);
				}

				// Prompt for price type
				System.out.print("\nEnter in price type: ");
				String priceType = in.nextLine();
				
				// Verifies entered price type
				boolean found = false;
				for(int i = 0; i < priceTypes.length; i++){
					if(priceType.equals(priceTypes[i])){
						found = true;
						break;
					}
				}
				
				if(!found){
					System.out.println("Invalid price type");
					System.out.println("===================================");
					mainMenu();
				}
				
				if(!priceType.equals("market")){
					System.out.println("Only market orders are implemented, using market order.");	
				}

				// Data verified
				System.out.println("Trade will cost: $" + costToPurchase);
				System.out.print("Execute trade (Y/N): ");
				String execute = in.nextLine();

				if (execute.toLowerCase().equals("y")) {
					availableFunds -= costToPurchase;
					System.out.println("You have $" + availableFunds + " left in your account.");
					
					// Currently only Market orders are implemented
					Trade newTrade = null;
					if(priceType.equals("market")){
						newTrade = new MarketTrade("Buy", Stock.get(whatToBuy), shareNum);	
					}else if(priceType.equals("market on close")){
						newTrade = new MarketTrade("Buy", Stock.get(whatToBuy), shareNum);
					}else if(priceType.equals("limit")){
						newTrade = new MarketTrade("Buy", Stock.get(whatToBuy), shareNum);
					}else if(priceType.equals("stop limit")){
						newTrade = new MarketTrade("Buy", Stock.get(whatToBuy), shareNum);
					}else if(priceType.equals("stop")){
						newTrade = new MarketTrade("Buy", Stock.get(whatToBuy), shareNum);
					}else if(priceType.equals("trailing stop dollar")){
						newTrade = new MarketTrade("Buy", Stock.get(whatToBuy), shareNum);
					}else if(priceType.equals("trailing stop percent")){
						newTrade = new MarketTrade("Buy", Stock.get(whatToBuy), shareNum);
					}
					
					currentPortfolio.addStockTransaction(newTrade);
					currentPortfolio.setAvailableFunds(availableFunds);
				}else{
					System.out.println("No action taken");
				}
			}
		}else{
			System.out.println("Invalid Symbol");
		}

		System.out.println("===================================");
		mainMenu();
	}

	/**
	 * Menu to sell a Stock
	 */
	private void sellMenu() {
		System.out.println();
		System.out.println(currentPortfolio.getAccountName() + " " + currentPortfolio.getPercentChange());
		System.out.println("===================================");

		if (GeneralToolKit.isMarketOpen()) {
			System.out.print("Enter in symbol: ");
			String whatToSell = in.nextLine();
			
			if(GeneralToolKit.verifySymbol(whatToSell)){
				String curPrice = GeneralToolKit.fetcher.getLastTradePriceOnly(whatToSell);

				// Need to retrieve average price paid
				double pricePaid = 0.0;
				pricePaid = currentPortfolio.getPriceAmount(Stock.get(whatToSell));

				if (pricePaid == 0) {
					System.out.println(whatToSell + " not found in portfolio");
					mainMenu();
				}
				
				System.out.println("Current price is: " + curPrice);
				System.out.print("Enter amount of shares to sell: ");
				String shareAmount = in.nextLine();
				
				// Verifies entered share amount
				int shareNum = 0;
				try{
					shareNum = Integer.parseInt(shareAmount);
					
					if(shareNum <= 0){
						System.out.println("Invalid number of shares");
						mainMenu();
					}
				}catch(NumberFormatException e){
					System.out.println("Invalid share amount");
					mainMenu();
				}
				
				// Verifies that trader owns entered amount of shares
				int originalShares = currentPortfolio.getShareAmount(Stock.get(whatToSell));
				if (shareNum > originalShares) {
						System.out.println("You do not own that many shares of " + whatToSell);
						mainMenu();
				} 
				
				System.out.println("Price types: ");
				
				String[] priceTypes = Trade.getPriceTypes();
				for(int i = 0; i < priceTypes.length; i++){
					System.out.println("  " + priceTypes[i]);
				}
				
				// Prompt for price type
				System.out.print("\nEnter in price type: ");
				String priceType = in.nextLine();
				
				// Verifies entered price type
				boolean found = false;
				for(int i = 0; i < priceTypes.length; i++){
					if(priceType.equals(priceTypes[i])){
						found = true;
						break;
					}
				}
				
				if(!found){
					System.out.println("Invalid price type");
					System.out.println("===================================");
					mainMenu();
				}
				
				if(!priceType.equals("market")){
					System.out.println("Only market orders are implemented, using market order.");	
				}
				
				// Data is verified, selling
				double result = 0;
				// Computes result of trade
				if (pricePaid != Double.parseDouble(curPrice)) {
					if (pricePaid > Double.parseDouble(curPrice)) {
						result = (pricePaid * originalShares) - (Double.parseDouble(curPrice) * shareNum);
						System.out.println("Result of trade: Loss of $" + result);
						result *= -1; // Negates result
					} else if (pricePaid < Double.parseDouble(curPrice)) {
						result = (Double.parseDouble(curPrice) * shareNum) - (pricePaid * originalShares);
						System.out.println("Result of trade: Gain of $" + result);
					}
				} else {
					System.out.println("Result of trade: Even");
				}

				result -= 20.0; // Buying and Selling Commission

				System.out.print("Execute trade (Y/N): ");
				String execute = in.nextLine();

				double availableFunds = currentPortfolio.getAvailableFunds();

				// Executes Trade
				if (execute.toLowerCase().equals("y")) {
					availableFunds += result;
					System.out.println("You have $" + availableFunds + " left in your account");
					
					// Currently only Market orders are implemented
					Trade newTrade = null;
					if(priceType.equals("market")){
						newTrade = new MarketTrade("Sell", Stock.get(whatToSell), Integer.parseInt(shareAmount));
					}else if(priceType.equals("market on close")){
						newTrade = new MarketTrade("Sell", Stock.get(whatToSell), Integer.parseInt(shareAmount));
					}else if(priceType.equals("limit")){
						newTrade = new MarketTrade("Sell", Stock.get(whatToSell), Integer.parseInt(shareAmount));
					}else if(priceType.equals("stop limit")){
						newTrade = new MarketTrade("Sell", Stock.get(whatToSell), Integer.parseInt(shareAmount));
					}else if(priceType.equals("stop")){
						newTrade = new MarketTrade("Sell", Stock.get(whatToSell), Integer.parseInt(shareAmount));
					}else if(priceType.equals("trailing stop dollar")){
						newTrade = new MarketTrade("Sell", Stock.get(whatToSell), Integer.parseInt(shareAmount));
					}else if(priceType.equals("trailing stop percent")){
						newTrade = new MarketTrade("Sell", Stock.get(whatToSell), Integer.parseInt(shareAmount));
					}
					
					currentPortfolio.addStockTransaction(newTrade);
					currentPortfolio.updateNetWorth(result);
					currentPortfolio.setAvailableFunds(availableFunds);
				}else{
					System.out.println("No action taken");
				}
			}	
		}else{
			System.out.println("Invalid Symbol");
		}
		
		System.out.println("===================================");
		mainMenu();
	}

	private void sellShortMenu(){
		System.out.println("===================================");
		System.out.println("Not implemented yet.");
		System.out.println("===================================");
		mainMenu();
	}
	
	private void buyToCoverMenu(){
		System.out.println("===================================");
		System.out.println("Not implemented yet.");
		System.out.println("===================================");
		mainMenu();
	}
	
	/**
	 * Transaction history Menu
	 */
	private void printTransactionHistory() {
		System.out.println();
		System.out.println(currentPortfolio.getAccountName() + " " + currentPortfolio.getPercentChange());
		System.out.println("===================================");
		System.out.println("Transaction History:");
		currentPortfolio.printTradeHistory();
		System.out.println("===================================");

		mainMenu();
	}

	/**
	 * Menu to simulate trading strategies
	 */
	@SuppressWarnings("rawtypes")
	private void simulationMenu() {
		float duration = 0;
		float elapsedTimeHour;
		long elapsedTimeMillis;
		boolean notOver = true;
		long start;
		Portfolio portfolioToUse = null;
		String choice = null;

		System.out.println();
		System.out.println(currentPortfolio.getAccountName() + " " + currentPortfolio.getPercentChange());
		System.out.println("===================================");

		if (GeneralToolKit.isMarketOpen()) {
			// Choose Portfolio:
			System.out.println("Enter 1 to choose your current portfolio");
			System.out.println("Enter 2 to construct a portfolio (money amount and stock)");
			System.out.println("Enter 3 to construct a portfolio (money and no stocks)");
			System.out.print("Choice: ");
			choice = in.nextLine();

			if (choice.equals("1")) {
				portfolioToUse = currentPortfolio;
			} else if (choice.equals("2")) {
				System.out.println("Not implemented yet, using current portfolio");
				portfolioToUse = currentPortfolio;
			} else if (choice.equals("3")) {
				System.out.println("Not implemented yet, using current portfolio");
				portfolioToUse = currentPortfolio;
			} else {
				System.out.println("Incorrect choice");
				System.out.println("===================================");
				mainMenu();
			}

			// Choose Simulation duration
			System.out.print("\nEnter in simulation duration (hours): ");
			choice = in.nextLine();
			
			try{
				duration = Float.parseFloat(choice);
			}catch(NumberFormatException e){
				System.out.println("Invalid amount");
			}

			// Choose Algorithm
			System.out.println("\nSelect an Algorithm:");
			System.out.println("---------------------");
			int i;
			for (i = 0; i < availableAlgorithms.size(); i++) {
				System.out.println("Enter " + i + ": " + availableAlgorithms.get(i).getSimpleName());
			}
			System.out.print("Choice: ");
			choice = in.nextLine();

			int choiceNumber = 0;
			try{
				choiceNumber = Integer.parseInt(choice);
			}catch(NumberFormatException e){
				System.out.println("Invalid choice.");
				mainMenu();
			}
			
			if(choiceNumber >= 0 && choiceNumber <= i){
				// Initializes algorithm that will be used
				Class<? extends Algorithm> algorithm = availableAlgorithms.get(choiceNumber);;
				Algorithm algorithmToExecute = null;
				
				// Uses Reflection to initialize the correct Algorithm class
				try{
					Class<?> someClass = Class.forName(algorithm.getCanonicalName());
					//Constructor constructor = someClass.getConstructor(new Class[] { Portfolio.class });
					//algorithmToExecute = (Algorithm) constructor.newInstance(portfolioToUse);
					Constructor constructor = someClass.getConstructor(new Class[] { });
					algorithmToExecute = (Algorithm) constructor.newInstance();
				}catch(Exception e){
					e.printStackTrace();
				}
				
				// Performs algorithm until simulation is over
				Trade[] newTrades;
				start = System.currentTimeMillis();
				System.out.println("\nBeginning Simulation");
				algorithmToExecute.setup(portfolioToUse);
				
				while (notOver) {
					System.out.println("Executing Algorithm");
					newTrades = algorithmToExecute.evaluate();
					
					System.out.println("\nProcessing results");
					AlgorithmToolKit.processTradeResults(currentPortfolio, newTrades);

					// Updates time
					elapsedTimeMillis = System.currentTimeMillis() - start;
					elapsedTimeHour = elapsedTimeMillis / (60 * 60 * 1000F);

					// Determines if simulation is over
					if (elapsedTimeHour >= duration) {
						break;
					}

					// Sleeps to ensure connections are not lost
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				System.out.println("\nEnding Simulation");
				
				System.out.println("===================================");
				mainMenu();
			}
			}else{
				System.out.println("===================================");
				mainMenu();
			}
	}

	/**
	 * Saves the Trader's portfolio for for future use
	 * 
	 * @param toSerialize the portfolio to save
	 */
	private void serializeData(Portfolio toSerialize) {
		try {
			FileOutputStream fileOut = new FileOutputStream("portfolio.ser");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			
			out.writeObject(toSerialize);
			
			out.close();
			fileOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		ConsoleView application;
		Scanner in = new Scanner(System.in);
		
		String accountName;
		String moneyAmount;
		String[] watchList;
		
		FileInputStream f_in;
		ObjectInputStream obj_in;
		Object obj = null;
		
		System.out.println("---------------------------------------------------");
		System.out.println("Welcome to the Algorithmic Stock Trading Simulator!");
		System.out.println("---------------------------------------------------\n");
		
		// Determines if there is already a portfolio to use
		try {
			f_in = new FileInputStream("portfolio.ser");
			obj_in = new ObjectInputStream(f_in);
			obj = obj_in.readObject();
		}catch (Exception e) { } // Can ignore

		// If a portfolio is found, use it. Else, construct a new one
		if (obj instanceof Portfolio) {
			Portfolio portfolio = (Portfolio) obj;
			
			// Starts program
			application = new ConsoleView(portfolio);
			application.mainMenu();
		} else {
			System.out.print("Enter account name: ");
			accountName = in.nextLine();
			
			boolean valid = false;
			double money = 0;
			
			do{
				System.out.print("Enter money amount: $");
				moneyAmount = in.nextLine();
				try{
					money = Double.parseDouble(moneyAmount);
					
					if(money >= 0){
						valid = true;	
					}else{
						System.out.println("Invalid money amount");
					}
				}catch(NumberFormatException e){
					System.out.println("Invalid money amount");
				}
			}while(!valid);
			
			System.out.print("Enter watch list separed by commas ex) msft,fb,mrge: ");
			String list = in.nextLine();
			watchList = list.split(",");

			// Creates Portfolio
			Portfolio portfolio = new Portfolio(accountName, money);

			// Adds stocks to watchlist
			for (String curStock : watchList) {
				portfolio.addToWatchList(curStock);
			}

			// Starts Program
			application = new ConsoleView(portfolio);
			application.mainMenu();
		}
	}
}