package com.swinestudios.shapeshift;

import java.util.ArrayList;
import java.util.List;

import org.mini2Dx.core.game.GameContainer;
import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.core.screen.GameScreen;
import org.mini2Dx.core.screen.ScreenManager;
import org.mini2Dx.core.screen.Transition;
import org.mini2Dx.core.screen.transition.FadeInTransition;
import org.mini2Dx.core.screen.transition.FadeOutTransition;
import org.mini2Dx.tiled.TiledMap;
import org.mini2Dx.tiled.TiledObject;
import org.mini2Dx.tiled.exception.TiledException;

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
	
	public Player player;
	public GoalBlock goal;
	public PortalSystem portalSys;
	
	//public RubberBand virtualWindow;
	//public RubberBand virtualWindow2;
	//public Portal p1, p2, p3, p4;
	
	private TiledMap map0;
	
	private InputMultiplexer multiplexer;

	@Override
	public int getId(){
		return ID;
	}

	@Override
	public void initialise(GameContainer gc){
		solids = new ArrayList<Block>();
		windows = new ArrayList<RubberBand>();
		boundingSolids = new ArrayList<BoundingBlock>();
		multiplexer = new InputMultiplexer();
		
		try{
			map0 = new TiledMap(Gdx.files.internal("test_map0.tmx"));
		} catch (TiledException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void postTransitionIn(Transition t){

	}

	@Override
	public void postTransitionOut(Transition t){
		gameOver = false;
		paused = false;
		
		//TODO adjust resetting as necessary
		solids.clear();
		windows.clear();
		boundingSolids.clear();
		portalSys.portals.clear();
	}

	@Override
	public void preTransitionIn(Transition t){
		gameOver = false;
		paused = false;
		portalSys = new PortalSystem(this);
		goal = new GoalBlock(-20, -20, this);
		
		if(map0 != null){
			generateSolids(map0);
			generateBoundingSolids(map0);
			generatePortals(map0);
			generateGoal(map0);
			generateWindows(map0);
		}
		
		//virtualWindow = new RubberBand(0, 0, this);
		//virtualWindow2 = new RubberBand(320, 0, this);
		
		//windows.add(virtualWindow);
		//windows.add(virtualWindow2);
		player = new Player(16, 480 - 40, this);
		attachPlayerToWindow();
		
		/*p1 = new Portal(0, 0, this);
		p2 = new Portal(100, 100, this);
		p3 = new Portal(300, 0, this);
		p4 = new Portal(400, 200, this);
		portalSys.addPortal(p1);
		portalSys.addPortal(p2);
		portalSys.addPortal(p3);
		portalSys.addPortal(p4);*/

		//Input handling
		//multiplexer.addProcessor(virtualWindow);
		//multiplexer.addProcessor(virtualWindow2);
		multiplexer.addProcessor(player);
		Gdx.input.setInputProcessor(multiplexer);
	}

	@Override
	public void preTransitionOut(Transition t){

	}

	@Override
	public void render(GameContainer gc, Graphics g){
		//g.drawString("This is the gameplay screen", 20, 24);
		map0.draw(g, 0, 0);
		
		player.render(g);
		renderWindows(g);
		portalSys.render(g);
		goal.render(g);

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
			
			//testing portal system
			/*if(Gdx.input.isKeyJustPressed(Keys.Q)){ //Activate a portal
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
			}*/

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
	
	/* 
	 * Generates all solids based on a given tile map's object layer and adds them to the game. 
	 */
	public void generateSolids(TiledMap map){
		List<TiledObject> objects = map.getObjectGroup("Solids").getObjects();
		if(objects != null){ //if the given object layer exists
			for(int i = 0; i < objects.size(); i++){
				TiledObject temp = objects.get(i);
				Block block = new Block(temp.getX(), temp.getY(), temp.getWidth(), temp.getHeight(), this);
				if(solids != null){
					solids.add(block);
				}
				else{
					System.out.println("ArrayList solids does not exist."); //error message
				}
			}
		}
	}
	
	/* 
	 * Generates all bounding solids based on a given tile map's object layer and adds them to the game. 
	 */
	public void generateBoundingSolids(TiledMap map){
		List<TiledObject> objects = map.getObjectGroup("BoundingSolids").getObjects();
		if(objects != null){ //if the given object layer exists
			for(int i = 0; i < objects.size(); i++){
				TiledObject temp = objects.get(i);
				BoundingBlock block = new BoundingBlock(temp.getX(), temp.getY(), temp.getWidth(), temp.getHeight(), this);
				if(boundingSolids != null){
					boundingSolids.add(block);
				}
				else{
					System.out.println("ArrayList boundingSolids does not exist."); //error message
				}
			}
		}
	}
	
	/* 
	 * Generates all portals based on a given tile map's object layer and adds them to the game. 
	 */
	public void generatePortals(TiledMap map){
		List<TiledObject> objects = map.getObjectGroup("Portals").getObjects();
		if(objects != null){ //if the given object layer exists
			for(int i = 0; i < objects.size(); i++){
				TiledObject temp = objects.get(i);
				Portal portal = new Portal(temp.getX(), temp.getY(), this);
				if(portalSys != null){
					portalSys.addPortal(portal);
				}
				else{
					System.out.println("portalSys does not exist."); //error message
				}
			}
		}
	}
	
	/* 
	 * "Generates" the goal specific to the given tile map simply by moving it to its corresponding position
	 */
	public void generateGoal(TiledMap map){
		List<TiledObject> objects = map.getObjectGroup("Goal").getObjects();
		if(objects != null){ //if the given object layer exists
			for(int i = 0; i < objects.size(); i++){
				TiledObject temp = objects.get(i);
				if(goal != null){
					goal.x = temp.getX();
					goal.y = temp.getY();
				}
				else{
					System.out.println("GoalBlock is null");
				}
			}
		}
	}
	
	/* 
	 * Generates all windows based on a given tile map's object layer and adds them to the game. 
	 */
	public void generateWindows(TiledMap map){
		List<TiledObject> objects = map.getObjectGroup("Windows").getObjects();
		if(objects != null){ //if the given object layer exists
			for(int i = 0; i < objects.size(); i++){
				TiledObject temp = objects.get(i);
				RubberBand window = new RubberBand(temp.getX(), temp.getY(), this);
				if(windows != null){
					windows.add(window);
					multiplexer.addProcessor(window);
				}
				else{
					System.out.println("ArrayList windows does not exist."); //error message
				}
			}
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
