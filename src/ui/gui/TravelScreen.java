package ui.gui;

import javax.swing.JFrame;

import core.GameEnvironment;
import core.Route;
import ui.GameUI;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.ListSelectionModel;

/**
 * Displays the available routes on the current island and allows the player to choose one to take.
 * @author Dillon Pike, Daniel Pallesen
 * @version 25 May 2021
 */
public class TravelScreen extends Screen {
	
	/**
	 * Frame that holds all GUI elements.
	 */
	private JFrame frame;
	
	/**
	 * Table of all the routes the player can take from the current island.
	 */
	private JTable routeTable;
	
	/**
	 * Displays an error message if an error occurs. 
	 * E.g. if there aren't enough days yet to take the chosen route.
	 */
	private JLabel errorLbl;

	/**
	 * Stores the game instance then creates and sets up the frame.
	 * @param game game instance
	 */
	public TravelScreen(GameEnvironment game) {
		super(game);
		frame = new JFrame();
		initialiseFrame();
		configureFrame();
	}

	@Override
	JFrame getFrame() {
		return frame;
	}
	
	/**
	 * Travels to the island chosen in the island table if its possible. If not, an error message is displayed.
	 * Has route-specific chance to trigger a random event.
	 */
	private void travel() {
		if (routeTable.getSelectedRowCount() == 0) {
			// Displays error message if player hasn't selected a route
			errorLbl.setText("You must select a route.");
		} else {
			Route route = getGame().getIsland().getRoutes().get(routeTable.getSelectedRow());
			if (!getGame().isTimeForRoute(route)) {
				// Displays error message if there's not enough days left for the route
				errorLbl.setText(GameUI.TRAVEL_DAYS_ERROR);
			} else if (getGame().getShip().getHealth() != getGame().getShip().getMaxHealth()) {
				// Displays error message if player's ship is not at max health
				errorLbl.setText(GameUI.TRAVEL_SHIP_ERROR);
			} else if (!getGame().canAffordRoute(route)) {
				// Displays error message if player doesn't have enough gold to afford the route
				errorLbl.setText(GameUI.TRAVEL_GOLD_ERROR);
			} else {
				getGame().travelRoute(route);
				// Has a chance of calling random event screen
				if (route.encounterPirates()) {
					getGame().getUI().pirateEncounter(route);
				} else if (route.encounterWeatherEvent()) {
					getGame().getUI().weatherEncounter(route);
				} else if (route.encounterLostSailors()) {
					getGame().getUI().sailorEncounter(route);
				} else {
					getGame().getUI().menu();
				}
			}
		}
	}
	
	/**
	 * Initialise the contents of the frame.
	 */
	private void initialiseFrame() {
		frame.setBounds(100, 100, 620, 350);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JButton backBtn = new JButton("Back");
		backBtn.addActionListener(e -> getGame().getUI().menu());
		backBtn.setFont(new Font("Tahoma", Font.PLAIN, 15));
		backBtn.setFocusable(false);
		backBtn.setBackground(Color.LIGHT_GRAY);
		
		JScrollPane routeScrollPane = new JScrollPane();
		
		JLabel titleLbl = new JLabel("Travel to Another Island");
		titleLbl.setFont(new Font("Tahoma", Font.PLAIN, 17));
		titleLbl.setHorizontalAlignment(SwingConstants.CENTER);
		
		JLabel routeLbl = new JLabel("Routes");
		routeLbl.setFont(new Font("Tahoma", Font.PLAIN, 15));
		
		JButton confirmBtn = new JButton("Confirm");
		confirmBtn.addActionListener(e -> travel());
		confirmBtn.setFont(new Font("Tahoma", Font.PLAIN, 15));
		confirmBtn.setFocusable(false);
		confirmBtn.setBackground(Color.LIGHT_GRAY);
		
		errorLbl = new JLabel("");
		errorLbl.setFont(new Font("Tahoma", Font.PLAIN, 15));
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(backBtn, GroupLayout.PREFERRED_SIZE, 105, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, 454, Short.MAX_VALUE)
							.addComponent(confirmBtn, GroupLayout.PREFERRED_SIZE, 105, GroupLayout.PREFERRED_SIZE))
						.addComponent(titleLbl, GroupLayout.DEFAULT_SIZE, 664, Short.MAX_VALUE)
						.addComponent(routeLbl)
						.addComponent(routeScrollPane, GroupLayout.DEFAULT_SIZE, 664, Short.MAX_VALUE)
						.addComponent(errorLbl))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(titleLbl)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(routeLbl)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(routeScrollPane, GroupLayout.PREFERRED_SIZE, 87, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(errorLbl)
					.addPreferredGap(ComponentPlacement.RELATED, 98, Short.MAX_VALUE)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(backBtn, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
						.addComponent(confirmBtn, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		
		routeTable = new JTable();
		routeTable.setShowGrid(false);
		routeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		ArrayList<Route> routes = getGame().getIsland().getRoutes();
		Object routesTable[][] = new Object[routes.size()][];
		int i = 0;
		String islandName;
		for (Route route : routes) {
			if (route.getIslands()[0].getName() == getGame().getIsland().getName()) {
				islandName = route.getIslands()[1].getName();
			} else {
				islandName = route.getIslands()[0].getName();
			}
			Object routeRow[] = {islandName, route.getDays(getGame().getShip().getSpeed()), route.getPirateDanger(), route.getWeatherDanger(), route.getSailorsOdds()};
			routesTable[i] = routeRow;
			i++;
		}
		String[] columnText = {"Island", "Days", "Pirate Danger", "Weather Danger", "Lost Sailors Chance"};
		routeTable.setModel(new DefaultTableModel(routesTable, columnText) {
			private static final long serialVersionUID = -156423262431076534L;

			public boolean isCellEditable(int row, int column) {
				return false;
			}
		});
		routeScrollPane.setViewportView(routeTable);
		frame.getContentPane().setLayout(groupLayout);
	}

}
