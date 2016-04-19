package com.swinestudios.shapeshift;

import org.mini2Dx.core.game.GameContainer;
import org.mini2Dx.core.geom.Rectangle;
import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.core.graphics.Sprite;
import org.mini2Dx.core.screen.GameScreen;
import org.mini2Dx.core.screen.ScreenManager;
import org.mini2Dx.core.screen.Transition;
import org.mini2Dx.core.screen.transition.FadeInTransition;
import org.mini2Dx.core.screen.transition.FadeOutTransition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

public class MainMenu implements GameScreen{

	public static int ID = 1;

	public static final float SPRITE_SCALE = 2f;

	public Sprite title, start, startPressed, quit, quitPressed;
	public Rectangle startHitbox, quitHitbox;
	public boolean isStartPressed, isQuitPressed;
	
	public static Sound selectSound = Gdx.audio.newSound(Gdx.files.internal("select2.wav"));

	@Override
	public int getId(){
		return ID;
	}

	@Override
	public void initialise(GameContainer gc){
		title = new Sprite(new Texture(Gdx.files.internal("0-0.png")));
		title.setSize(title.getWidth()*2, title.getHeight()*2);

		start = new Sprite(new Texture(Gdx.files.internal("startgame00.png")));
		start.setSize(start.getWidth()*2, start.getHeight()*2);
		startHitbox = new Rectangle(16*SPRITE_SCALE, 120*SPRITE_SCALE, start.getWidth(), start.getHeight());

		startPressed = new Sprite(new Texture(Gdx.files.internal("startgame01.png")));
		startPressed.setSize(startPressed.getWidth()*2, startPressed.getHeight()*2);

		quit = new Sprite(new Texture(Gdx.files.internal("quitgame00.png")));
		quit.setSize(quit.getWidth()*2, quit.getHeight()*2);
		quitHitbox = new Rectangle(16*SPRITE_SCALE, 175*SPRITE_SCALE, quit.getWidth(), quit.getHeight());

		quitPressed = new Sprite(new Texture(Gdx.files.internal("quitgame01.png")));
		quitPressed.setSize(quitPressed.getWidth()*2, quitPressed.getHeight()*2);

		isStartPressed = false;
		isQuitPressed = false;
	}

	@Override
	public void postTransitionIn(Transition t){

	}

	@Override
	public void postTransitionOut(Transition t){
		isStartPressed = false;
		isQuitPressed = false;
	}

	@Override
	public void preTransitionIn(Transition t){

	}

	@Override
	public void preTransitionOut(Transition t){

	}

	@Override
	public void render(GameContainer gc, Graphics g){
		g.drawSprite(title, 123*SPRITE_SCALE, 28*SPRITE_SCALE);
		
		if(isStartPressed){
			g.drawSprite(startPressed, 16*SPRITE_SCALE, 120*SPRITE_SCALE);
		}
		else{
			g.drawSprite(start, 16*SPRITE_SCALE, 120*SPRITE_SCALE);
		}
		
		if(isQuitPressed){
			g.drawSprite(quitPressed, 16*SPRITE_SCALE, 175*SPRITE_SCALE);
		}
		else{
			g.drawSprite(quit, 16*SPRITE_SCALE, 175*SPRITE_SCALE);
		}
	}

	@Override
	public void update(GameContainer gc, ScreenManager<? extends GameScreen> sm, float delta){
		int mx = Gdx.input.getX();
		int my = Gdx.input.getY();
		if(startHitbox.contains(mx, my)){
			isStartPressed = true;
		}
		else{
			isStartPressed = false;
		}
		if(quitHitbox.contains(mx, my)){
			isQuitPressed = true;
		}
		else{
			isQuitPressed = false;
		}
		
		if(Gdx.input.isTouched()){
			if(startHitbox.contains(mx, my)){
				sm.enterGameScreen(Gameplay.ID, new FadeOutTransition(), new FadeInTransition());
				selectSound.play();
			}
			else if(quitHitbox.contains(mx, my)){
				Gdx.app.exit();
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
	public void onResize(int width, int height){
	}

	@Override
	public void onResume() {
	}

}
