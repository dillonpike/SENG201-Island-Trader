package ui.gui;

import core.GameEnvironment;
import seng201.rocketmanager.ui.gui.MainScreen;
import ui.GameUI;

public class GUI implements GameUI {

	private GameEnvironment game;
    private StartScreen screen;
	
	@Override
	public void start(GameEnvironment game) {
		this.game = game;
        screen = new StartScreen(game);
        screen.show();
	}

	@Override
	public void menu() {
		screen.quit();
        //screen = new MainScreen(rocketManager);
        //screen.show();
	}

	@Override
	public void shipInfo() {
		// TODO Auto-generated method stub

	}

	@Override
	public void goods() {
		// TODO Auto-generated method stub

	}

	@Override
	public void islandInfo() {
		// TODO Auto-generated method stub

	}

	@Override
	public void store() {
		// TODO Auto-generated method stub

	}

	@Override
	public void repair() {
		// TODO Auto-generated method stub

	}

	@Override
	public void travel() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pirateEncounter() {
		// TODO Auto-generated method stub

	}

	@Override
	public void weatherEncounter() {
		// TODO Auto-generated method stub

	}

	@Override
	public void sailorsEncounter() {
		// TODO Auto-generated method stub

	}

}