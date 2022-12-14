package ui.gui;

import javax.swing.JFrame;

import core.GameEnvironment;
import core.Item;
import core.Ship;
import core.Weapon;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.SwingConstants;
import javax.swing.JButton;
import java.awt.Color;
import javax.swing.UIManager;
import javax.swing.JTable;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;

/**
 * Displays the properties of the player's ship, what's in the cargo, and which cargo items are weapons.
 * @author Dillon Pike, Daniel Pallesen
 * @version 25 May 2021
 */
public class ShipInfoScreen extends Screen {

	/**
	 * Frame that holds all GUI elements.
	 */
	private JFrame frame;
	
	/**
	 * Table to show the contents of the ship's cargo.
	 */
	private JTable cargoTable;
	
	/**
	 * Table to show the player's weapons.
	 */
	private JTable weaponTable;

	/**
	 * Stores the game instance then creates and sets up the frame.
	 * @param game game instance
	 */
	public ShipInfoScreen(GameEnvironment game) {
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
	 * Returns 2D array from items where each row has a name, price, size, and description.
	 * @param items an ArrayList of items
	 * @return 2D array of item info
	 */
	private Object[][] makeItemArray(ArrayList<Item> items) {
		Object itemArray[][] = new Object[items.size()][];
		int i = 0;
		for (Item item : items) {
			Object itemRow[] = {item.getName(), item.getPrice(), item.getSize(), item.getDesc()};
			itemArray[i] = itemRow;
			i++;
		}
		return itemArray;
	}
	
	/**
	 * Returns 2D array from weapons where each row has a name, price, size, and description.
	 * @param items an ArrayList of items
	 * @return 2D array of item info
	 */
	private Object[][] makeWeaponArray(ArrayList<Item> weapons) {
		Object itemArray[][] = new Object[weapons.size()][];
		int i = 0;
		for (Item item : weapons) {
			Weapon weapon = (Weapon) item;
			Object itemRow[] = {weapon.getName(), weapon.getPrice(), weapon.getSize(), weapon.shots(), weapon.damage(), weapon.getDesc()};
			itemArray[i] = itemRow;
			i++;
		}
		return itemArray;
	}
	

	/**
	 * Initialise the contents of the frame.
	 */
	private void initialiseFrame() {
		frame.setBounds(100, 100, 700, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JLabel titleLbl = new JLabel(getGame().getName() + "'s Ship");
		titleLbl.setHorizontalAlignment(SwingConstants.CENTER);
		titleLbl.setFont(new Font("Tahoma", Font.PLAIN, 17));
		
		JButton backBtn = new JButton("Back");
		backBtn.addActionListener(e -> getGame().getUI().menu());
		backBtn.setFont(new Font("Tahoma", Font.PLAIN, 15));
		backBtn.setFocusable(false);
		backBtn.setBackground(Color.LIGHT_GRAY);
		
		Ship ship = getGame().getShip();
		JLabel lblNewLabel = new JLabel("Ship Type: " + ship.getName());
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 13));
		
		JLabel lblHealth = new JLabel("Current Health: " + ship.getHealth() + "/" + ship.getMaxHealth());
		lblHealth.setFont(new Font("Tahoma", Font.PLAIN, 13));
		
		JLabel lblCargoSpace = new JLabel("Cargo Space: " + ship.getSpaceLeft() + "/" + ship.getMaxSpace());
		lblCargoSpace.setFont(new Font("Tahoma", Font.PLAIN, 13));
		
		JLabel lblSpeed = new JLabel("Speed: " + ship.getSpeed());
		lblSpeed.setFont(new Font("Tahoma", Font.PLAIN, 13));
		
		JLabel lblEndurance = new JLabel("Endurance: " + ship.getEndurance());
		lblEndurance.setFont(new Font("Tahoma", Font.PLAIN, 13));
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setForeground(new Color(0, 128, 0));
		progressBar.setBackground(UIManager.getColor("Button.background"));
		if (ship.getHealth() < ship.getMaxHealth()/4) {
			progressBar.setForeground(new Color(128, 0, 0)); // Red
		}
		else if (ship.getHealth() < ship.getMaxHealth()/2) {
			progressBar.setForeground(new Color(255, 140, 0)); // Orange
		}
		else {
			progressBar.setForeground(new Color(0, 210, 0)); // Green
		}
		
		progressBar.setMaximum(ship.getMaxHealth());
		progressBar.setValue(ship.getHealth());
		
		JScrollPane scrollPane = new JScrollPane();
		
		JLabel lblCurrentWeapons = new JLabel("Current Weapons");
		lblCurrentWeapons.setFont(new Font("Tahoma", Font.PLAIN, 13));
		
		JScrollPane scrollPane_1 = new JScrollPane();
		
		
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(lblHealth)
							.addGap(18)
							.addComponent(progressBar, GroupLayout.DEFAULT_SIZE, 532, Short.MAX_VALUE))
						.addComponent(titleLbl, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 670, Short.MAX_VALUE))
					.addGap(25))
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(backBtn, GroupLayout.PREFERRED_SIZE, 105, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(580, Short.MAX_VALUE))
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 674, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(11, Short.MAX_VALUE))
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblSpeed, GroupLayout.PREFERRED_SIZE, 675, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblEndurance, GroupLayout.PREFERRED_SIZE, 675, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblCargoSpace, GroupLayout.PREFERRED_SIZE, 675, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblCurrentWeapons, GroupLayout.PREFERRED_SIZE, 675, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 655, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(21, Short.MAX_VALUE))
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 657, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(19, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(titleLbl)
					.addGap(12)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblHealth)
						.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE)
					.addGap(2)
					.addComponent(lblSpeed)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblEndurance)
					.addGap(30)
					.addComponent(lblCargoSpace)
					.addGap(12)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 126, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(lblCurrentWeapons, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(backBtn, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		
		
		String[] weaponColumnText = {"Name", "Price", "Size", "Shots","Damage", "Description"};
		weaponTable = new JTable(makeWeaponArray(ship.getWeapons()), weaponColumnText);
		weaponTable.setEnabled(false);
		weaponTable.setFillsViewportHeight(true);
		weaponTable.setShowGrid(false);
		scrollPane_1.setViewportView(weaponTable);
		
		String[] cargoColumnText = {"Name", "Price", "Size", "Description"};
		cargoTable = new JTable(makeItemArray(ship.getCargo()), cargoColumnText);
		cargoTable.setEnabled(false);
		cargoTable.setFillsViewportHeight(true);
		cargoTable.setShowGrid(false);
		scrollPane.setViewportView(cargoTable);
		frame.getContentPane().setLayout(groupLayout);
	}
}
