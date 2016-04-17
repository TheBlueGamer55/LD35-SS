package com.swinestudios.shapeshift;

import java.util.ArrayList;

import org.mini2Dx.core.game.GameContainer;
import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.core.screen.GameScreen;
import org.mini2Dx.core.screen.ScreenManager;
import org.mini2Dx.core.screen.Transition;
import org.mini2Dx.core.screen.transition.FadeInTransition;
import org.mini2Dx.core.screen.transition.FadeOutTransition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;

public class Gameplay implements GameScreen{

	public static int ID = 2;

	public boolean paused = false;
	public boolean gameOver = false;

	public ArrayList<Block> solids;
	public ArrayList<BoundingBlock> boundingSolids;

	public ArrayList<RubberBand> windows;
	public RubberBand virtualWindow;
	public RubberBand virtualWindow2;
	public Player player;
	public PortalSystem portalSys;
	public Portal p1, p2, p3, p4;

	@Override
	public int getId(){
		return ID;
	}

	@Override
	public void initialise(GameContainer gc){

	}

	@Override
	public void postTransitionIn(Transition t){

	}

	@Override
	public void postTransitionOut(Transition t){
		gameOver = false;
		paused = false;
	}

	@Override
	public void preTransitionIn(Transition t){
		gameOver = false;
		paused = false;

		solids = new ArrayList<Block>();
		windows = new ArrayList<RubberBand>();
		boundingSolids = new ArrayList<BoundingBlock>();
		
		virtualWindow = new RubberBand(0, 0, this);
		virtualWindow2 = new RubberBand(320, 0, this);
		
		windows.add(virtualWindow);
		windows.add(virtualWindow2);
		player = new Player(virtualWindow.x + 16, virtualWindow.y + 16, this);
		attachPlayerToWindow();

		//TODO test remove later
		solids.add(new Block(330, 330, 21, 49, this));
		solids.add(new Block(330, 330, 49, 21, this));
		boundingSolids.add(new BoundingBlock(320, 240, 32, 32, this));
		
		portalSys = new PortalSystem(this);
		p1 = new Portal(0, 0, this);
		p2 = new Portal(100, 100, this);
		p3 = new Portal(300, 0, this);
		p4 = new Portal(400, 200, this);
		portalSys.addPortal(p1);
		portalSys.addPortal(p2);
		portalSys.addPortal(p3);
		portalSys.addPortal(p4);

		//Input handling
		InputMultiplexer multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(virtualWindow);
		multiplexer.addProcessor(virtualWindow2);
		multiplexer.addProcessor(player);
		Gdx.input.setInputProcessor(multiplexer);
	}

	@Override
	public void preTransitionOut(Transition t){

	}

	@Override
	public void render(GameContainer gc, Graphics g){
		//g.drawString("This is the gameplay screen", 20, 24);

		player.render(g);
		portalSys.render(g);
		renderWindows(g);

		//TODO remove solids rendering later
		for(int i = 0; i < solids.size(); i++){
			solids.get(i).render(g);
		}
		for(int i = 0; i < boundingSolids.size(); i++){
			boundingSolids.get(i).render(g);
		}

		if(gameOver){
			g.setColor(Color.RED);
			g.drawString("You died! Press Escape to go back to the main menu", 160, 240);
		}
		if(paused){
			g.setColor(Color.RED);
			g.drawString("Are you sure you want to quit? Y or N", 220, 240);
		}
	}

	@Override
	public void update(GameContainer gc, ScreenManager<? extends GameScreen> sm, float delta){
		if(!paused && !gameOver){
			player.update(delta);
			updateWindows(delta);
			portalSys.update(delta);
			
			//TODO Test teleportation, remove later
			if(Gdx.input.isKeyJustPressed(Keys.T)){
				player.x = Gdx.input.getX();
				player.y = Gdx.input.getY();
				attachPlayerToWindow();
			}
			//TODO testing portal system
			if(Gdx.input.isKeyJustPressed(Keys.Q)){ //Activate a portal
				for(int i = 0; i < portalSys.portals.size(); i++){
					Portal port = portalSys.portals.get(i);
					if(port.hitbox.contains(Gdx.input.getX(), Gdx.input.getY())){
						port.activate();
					}
				}
			}
			if(Gdx.input.isKeyJustPressed(Keys.E)){ //Deactivate a portal
				for(int i = 0; i < portalSys.portals.size(); i++){
					Portal port = portalSys.portals.get(i);
					if(port.hitbox.contains(Gdx.input.getX(), Gdx.input.getY())){
						port.setActive(false);
					}
				}
			}

			if(Gdx.input.isKeyPressed(Keys.ESCAPE)){
				paused = true;
			}
		}
		else{
			if(gameOver){
				if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
					sm.enterGameScreen(MainMenu.ID, new FadeOutTransition(), new FadeInTransition());
				}
			}
			else if(paused){
				if(Gdx.input.isKeyJustPressed(Keys.Y)){
					sm.enterGameScreen(MainMenu.ID, new FadeOutTransition(), new FadeInTransition());
				}
				if(Gdx.input.isKeyJustPressed(Keys.N)){
					paused = false;
				}
			}
		}
	}
	
	/*
	 * Searches for the window that the player is contained in.
	 * precondition - windows cannot overlap, player cannot move outside of its current window
	 * postcondition - only one window will contain a reference to the player
	 */
	public void attachPlayerToWindow(){
		for(int i = 0; i < windows.size(); i++){
			RubberBand window = windows.get(i);
			//If window contains player, set the variables that indicate so
			if(window.tempBox.contains(player.x, player.y)){
				window.containedPlayer = player;
				window.containsPlayer = true;
			}
			//Any other windows will not have the player, so set the variables to indicate so
			else{
				window.containedPlayer = null;
				window.containsPlayer = false;
			}
		}
	}
	
	public void renderWindows(Graphics g){
		for(int i = 0; i < windows.size(); i++){
			windows.get(i).render(g);
		}
	}
	
	public void updateWindows(float delta){
		for(int i = 0; i < windows.size(); i++){
			windows.get(i).update(delta);
		}
	}

	@Override
	public void interpolate(GameContainer gc, float delta){
	}

	@Override
	public void onPause() {
	}

	@Override
	public void onResize(int width, int height) {
	}

	@Override
	public void onResume() {
	}

}
