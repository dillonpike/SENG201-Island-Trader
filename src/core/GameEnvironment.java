package core;
import java.util.ArrayList;
import java.util.Random;

import ui.GameUI;
import ui.TextUI;

/**
 * This class handles the main game logic and keeps track of the state.
 * @author Dillon Pike, Daniel Pallesen
 * @version 25 May 2021
 */
public class GameEnvironment {
	
	// Constants for the game
	private final double WAGE_MODIFIER = 0.5;
	private final int MIN_REWARD = 20;
	private final int MAX_REWARD = 60;
	private final int MIN_WEATHER_DAMAGE = 10;
	private final int MAX_WEATHER_DAMAGE = 40;
	private final int PIRATE_CARGO_THRESHOLD = 50;

	
	private ArrayList<Ship> ships;
	private ArrayList<Item> items;
	private ArrayList<Item> weapons;
	private ArrayList<Island> islands;
	private ArrayList<Route> routes;
	private ArrayList<Item> goods;
	
	private GameUI ui;
	private Island island;
	private int gold;
	private String name;
	private int days;
	private int startDays;
	private Ship ship;
	
	public GameEnvironment(GameUI ui, int islandIndex, int gold) {
		initArrayLists();
		this.ui = ui;
		this.island = islands.get(islandIndex);
		this.gold = gold;
		ui.start(this);
	}
	
	/**
	 * Initialises all the ArrayLists needed for the game.
	 */
	private void initArrayLists() {
		ships = ObjectsListGenerator.generateShip();
		items = ObjectsListGenerator.generateItem();
		weapons = ObjectsListGenerator.generateWeapon();
		islands = ObjectsListGenerator.generateIsland();
		routes = ObjectsListGenerator.generateRoute(islands);
		goods = new ArrayList<Item>();
	}
	
	public void finishSetup(String name, int days, Ship ship) {
		this.name = name;
		this.startDays = days;
		this.days = days;
		this.ship = ship;
		ui.menu();
	}
	
	public GameUI getUI() {
		return ui;
	}
	
	
	public ArrayList<Ship> getShips() {
		return ships;
	}
	
	/**
	 * Returns an Array list of in game islands
	 * @return islands
	 */
	public ArrayList<Island> getIslands() {
		return islands;
	}
	
	public Ship getShip() {
		return ship;
	}
	
	public String getName() {
		return name;
	}
	
	/** 
	 * Returns the number of days left.
	 * @return number of days left
	 */
	public int getDays() {
		return days;
	}
	
	/** 
	 * Returns the number of days started with.
	 * @return number of days started with
	 */
	public int getStartDays() {
		return startDays;
	}
	
	/** 
	 * Sets the remaining number of days left to 0 ending the game.
	 */
	public void endGame() {
		days = 0;
	}
	
	/**
	 * Returns the current island.
	 * @return current island
	 */
	public Island getIsland() {
		return island;
	}
	
	/**
	 * Returns the store on the current island.
	 * @return current store
	 */
	public Store getStore() {
		return island.getStore();
	}
	
	/**
	 * Returns the player's gold.
	 * @return the gold
	 */
	public int getGold() {
		return gold;
	}
	
	/**
	 * Sets the player's gold to 0.
	 */
	public void loseGold() {
		gold = 0;
	}
	
	/**
	 * Sets the players gold
	 * @param gold
	 */
	public void setGold(int gold) {
		this.gold = gold;
	}
	
	/**
	 * Returns all the goods the player has bought during the game.
	 * @return goods the player has bought
	 */
	public ArrayList<Item> getGoods() {
		return goods;
	}
	
	public boolean buyItem(Item item) {
		boolean bought = false;
		if (gold >= item.getPrice() && ship.addCargo(item)) {
			bought = true;
			gold -= item.getPrice();
			
			// Adds a copy of item to goods with the necessary constructor variables
			// Note: price is passed in as basePrice since basePrice not needed
			goods.add(new Item(item.getName(), item.getDesc(), item.getSize(), item.getPrice(), item.isWeapon()));
		}
		return bought;
	}

	public boolean sellItem(Item item) {
		boolean sold = false;
		if (ship.removeCargo(item)) {
			sold = true;
			gold += item.getPrice();
			for (Item goodsItem : goods) {
				
				// Sets the first unsold occurrence of this item in goods to sold and stores the
				// sell price and location
				if (goodsItem.getName().equals(item.getName()) && goodsItem.getIsSold() == false) {
					goodsItem.setIsSold(true);
					goodsItem.setSoldAt(island.getName());
					goodsItem.setSoldFor(item.getPrice());
					break;
				}
			}
		}
		return sold;
	}
	
	/** 
	 * Attempts to repair ship, but fails if the player doesn't have enough gold.
	 * @return true if successful, otherwise false
	 */
	public boolean repairShip() {
		boolean repaired = false;
		int cost = ship.getRepairCost();
		if (gold > cost) {
			gold -= cost;
			ship.setHealth(ship.getMaxHealth());
			repaired = true;
		}
		return repaired;
	}
	
	/**
	 * Returns true if the player can afford to travel along route, otherwise false.
	 * @param route the route to travel on
	 * @return true if possible, otherwise false
	 */
	public boolean canAffordRoute(Route route) {
		boolean can = false;
		int daysTaken = route.getDays(ship.getSpeed());
		if (gold >= daysTaken * ship.getCrew() * WAGE_MODIFIER) {
			can = true;
		}
		return can;
	}
	
	/**
	 * Returns true if there are enough days left for the player to travel along route, otherwise false.
	 * @param route the route to travel on
	 * @return true if possible, otherwise false
	 */
	public boolean isTimeForRoute(Route route) {
		boolean isTime = false;
		int daysTaken = route.getDays(ship.getSpeed());
		if (days >= daysTaken) {
			isTime = true;
		}
		return isTime;
	}

	/** 
	 * Decreases the number of days by the days of the route, decreases gold by the crew's wages,
	 * possibly encounters random events, and changes island to the destination of the route.
	 * @param route the route to travel on
	 */
	public void travelRoute(Route route) {
		if (ui instanceof TextUI) {
			if (route.encounterPirates()) {
				ui.pirateEncounter(route);
			}
			if (route.encounterWeatherEvent()) {
				ui.weatherEncounter(route);
			}
			if (route.encounterLostSailors()) {
				ui.sailorEncounter(route);
			}
		}
		int daysTaken = route.getDays(ship.getSpeed());
		this.days -= daysTaken;
		this.gold -= daysTaken * ship.getCrew() * WAGE_MODIFIER;
		Island[] islands = route.getIslands();
		if (islands[0] != island) {
			island = islands[0];
		} else {
			island = islands[1];
		}
	}
	
	/**
	 * Empties the player's cargo and returns true if they are satisfied with the loot, otherwise false.
	 * @return true if pirate's are satisfied, otherwise false.
	 */
	public boolean pirateLossOutcome() {
		int totalValue = 0;
		for (Item item : ship.getCargo()) {
			totalValue += item.getBasePrice();
		}
		ship.emptyCargo(); // Pirates steal all goods
		return totalValue > PIRATE_CARGO_THRESHOLD;
	}
	
	public int pirateEvent() {
		Random randomGenerator = new Random();
		// Generate enemy ship
		ArrayList<Ship> ships = ObjectsListGenerator.generateShip();
		int shipInt = randomGenerator.nextInt(ships.size());
		Ship pirateShip = ships.get(shipInt);
		Ship playerShip = ship;
		int initialHealth = playerShip.getHealth();
		
		// Player and pirate take turns rolling dice
		while (playerShip.getHealth() > 0 && pirateShip.getHealth() > 0) {
			// Player Turn
			for (Item item : playerShip.getWeapons()) {
				Weapon weapon = (Weapon) item; 
				for (int i = 0; i < weapon.shots(); i++) {
					int damage = randomGenerator.nextInt(weapon.damage());
					if (damage > 0) {
						int resisted = randomGenerator.nextInt(pirateShip.getEndurance());
						resisted = Math.min(resisted, damage);
						pirateShip.setHealth(pirateShip.getHealth() - damage + resisted);
					}
				}				
			}
			if (pirateShip.getHealth() <= 0){
				break;
			}
			// Pirate Turn
			for (Item item : pirateShip.getWeapons()) {
				Weapon weapon = (Weapon) item; 
				for (int i = 0; i < weapon.shots(); i++) {
					int damage = randomGenerator.nextInt(weapon.damage());
					if (damage > 0) {
						int resisted = randomGenerator.nextInt(playerShip.getEndurance());
						resisted = Math.min(resisted, damage);
						playerShip.setHealth(playerShip.getHealth() - damage + resisted);
					}
				}				
			}
		}
		return initialHealth - playerShip.getHealth();
	}
	
	public int weatherEvent() {
		Random randomGenerator = new Random();
		// Deal random damage between a range
		int damage = randomGenerator.nextInt(MAX_WEATHER_DAMAGE - MIN_WEATHER_DAMAGE) + MIN_WEATHER_DAMAGE;
		ship.setHealth(ship.getHealth() - damage);
		return damage;
	}
	
	public int sailorEvent() {
		Random randomGenerator = new Random();
		// Give monetary reward between a range
		int reward = randomGenerator.nextInt(MAX_REWARD - MIN_REWARD) + MIN_REWARD;
		setGold(gold + reward);
		return reward;
	}
}
