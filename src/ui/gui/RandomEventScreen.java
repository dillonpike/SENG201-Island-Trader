package ui.gui;

import javax.swing.JFrame;

import core.GameEnvironment;
import core.Route;
import ui.GameUI;
import ui.gui.GUI.RandomEvent;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JButton;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.JPanel;

/**
 * Screen that displays a random event to the player.
 * @author Dillon Pike, Daniel Pallesen
 * @version 25 May 2021
 */
public class RandomEventScreen extends Screen {
	
	/**
	 * Frame that holds all GUI elements.
	 */
	private JFrame frame;
	
	/**
	 * Current route being traveled along.
	 */
	private Route route;
	
	/**
	 * Result value of the random event. E.g. the amount of damage taken during the weather event.
	 */
	private int resultValue;
	
	/**
	 * The type of random event occurring.
	 */
	private RandomEvent event;
	
	/**
	 * Whether or not the pirates are satisfied with the player's goods.
	 */
	private boolean piratesUnsatisfied;

	/**
	 * Stores the game instance, current route being traveled along, the result value of the event, and the type of event occurring,
	 * then creates and sets up the frame.
	 * @param game game instance
	 * @param route route being traveled along
	 * @param resultValue result value of the event
	 * @param event random event occurring
	 */
	public RandomEventScreen(GameEnvironment game, Route route, int resultValue, RandomEvent event) {
		super(game);
		this.route = route;
		this.resultValue = resultValue;
		this.event = event;
		frame = new JFrame();
		initialiseFrame();
		configureFrame();
	}
	
	/**
	 * Sets the given label's text to the specific random event's title.
	 * @param titleLbl label to set the text of
	 */
	private void setTitleText(JLabel titleLbl) {
		switch (event) {
			case PIRATES: titleLbl.setText(GameUI.PIRATE_ENCOUNTER);
						  break;
			case WEATHER: titleLbl.setText(GameUI.WEATHER_ENCOUNTER);
						  break;
			case SAILORS: titleLbl.setText(GameUI.SAILOR_ENCOUNTER);
						  break;
		}
	}
	
	/**
	 * Sets the given text pane's text to the specific random event's message.
	 * @param messageTextPane text pane to set the text of
	 */
	private void setMessageText(JTextPane messageTextPane) {
		switch (event) {
			case PIRATES:
				if (getGame().getShip().getHealth() > 0) {
					messageTextPane.setText("You defeated them! Your ship has taken " + resultValue + " damage.");
				} else {
					if (!getGame().pirateLossOutcome()) {
						piratesUnsatisfied = true;
						getGame().loseGold();
						messageTextPane.setText(GameUI.PIRATE_UNSATISFIED);
						
					}
					else {
						messageTextPane.setText(GameUI.PIRATE_SATISFIED);
					}
				}
				break;
			case WEATHER: 
				if (getGame().getShip().getHealth() > 0) {
					messageTextPane.setText("Your ship has taken " + resultValue + " damage.");
				} else {
					messageTextPane.setText("Your ship has been destroyed in the storm.");
				}
				break;
			case SAILORS: 
				messageTextPane.setText("The sailors give you " + resultValue + " gold as a reward for rescuing them.");
				break;
		}
	}
	
	/**
	 * Continues the game by ending the game if the player lost to pirates, 
	 * allowing any subsequent random events to occur, or opening the menu screen.
	 */
	private void continueGame() {
		switch (event) {
			case PIRATES:
				if (piratesUnsatisfied) {
					getGame().endGame("Killed by Pirates");
					break;
				} else if (route.encounterWeatherEvent()) {
					getGame().getUI().weatherEncounter(route);
					break;
				}
			case WEATHER: 
				if (getGame().getShip().getHealth() <= 0) {
					getGame().loseGold();
					getGame().getShip().emptyCargo();
					getGame().endGame("Ship destroyed in storm");
				}
				if (route.encounterLostSailors()) {
					getGame().getUI().sailorEncounter(route);
					break;
				}
			case SAILORS: 
				getGame().getUI().menu();
				break;
		}
	}

	@Override
	JFrame getFrame() {
		return frame;
	}
		
	/**
	 * Initialise the contents of the frame.
	 */
	private void initialiseFrame() {
		frame.setBounds(100, 100, 525, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JLabel titleLbl = new JLabel();
		setTitleText(titleLbl);
		titleLbl.setHorizontalAlignment(SwingConstants.CENTER);
		titleLbl.setFont(new Font("Tahoma", Font.PLAIN, 17));
		
		JButton continueBtn = new JButton("Continue");
		continueBtn.addActionListener(e -> continueGame());
		continueBtn.setFont(new Font("Tahoma", Font.PLAIN, 15));
		continueBtn.setFocusable(false);
		continueBtn.setBackground(Color.LIGHT_GRAY);
		
		JPanel panel = new JPanel();
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(panel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE)
						.addComponent(titleLbl, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE)
						.addComponent(continueBtn, GroupLayout.PREFERRED_SIZE, 105, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(titleLbl)
					.addGap(18)
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 162, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
					.addComponent(continueBtn, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		
		JTextPane outcomeTextPane = new JTextPane();
		outcomeTextPane.setDisabledTextColor(Color.BLACK);
		outcomeTextPane.setEnabled(false);
		outcomeTextPane.setEditable(false);
		panel.add(outcomeTextPane, BorderLayout.CENTER);
		outcomeTextPane.setFont(new Font("Tahoma", Font.PLAIN, 13));
		setMessageText(outcomeTextPane);
		outcomeTextPane.setBackground(UIManager.getColor("menu"));
		frame.getContentPane().setLayout(groupLayout);
	}
}
