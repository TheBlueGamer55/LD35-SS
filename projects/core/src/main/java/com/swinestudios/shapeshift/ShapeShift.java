package com.swinestudios.shapeshift;

import org.mini2Dx.core.game.ScreenBasedGame;

public class ShapeShift extends ScreenBasedGame {
	
	public static final String GAME_IDENTIFIER = "com.swinestudios.shapeshift";

	@Override
	public void initialise() {
		this.addScreen(new MainMenu());
		this.addScreen(new Gameplay());
	}	

	@Override
	public int getInitialScreenId() {
		return MainMenu.ID;
	}
	
}
