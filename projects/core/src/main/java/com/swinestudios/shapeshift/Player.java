package com.swinestudios.shapeshift;

import org.mini2Dx.core.geom.Rectangle;
import org.mini2Dx.core.graphics.Graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;

public class Player implements InputProcessor{ 

	public float x, y;
	public float velX, velY;
	//public float accelX, accelY;

	//public final float frictionX = 0.6f;
	//public final float frictionY = 0.6f;

	public final float moveSpeedX = 2.0f;
	public final float moveSpeedY = 2.0f;

	//public final float maxSpeedX = 2.0f;
	//public final float maxSpeedY = 2.0f;

	public boolean isActive;

	public Rectangle hitbox;
	public Gameplay level;
	public String type;
	public TrailParticles trail;
	
	public static Sound teleportSound = Gdx.audio.newSound(Gdx.files.internal("teleport.wav"));

	//Controls/key bindings
	public final int LEFT = Keys.A;
	public final int RIGHT = Keys.D;
	public final int UP = Keys.W;
	public final int DOWN = Keys.S;

	public Player(float x, float y, Gameplay level){
		this.x = x;
		this.y = y;
		velX = 0;
		velY = 0;
		isActive = false;
		this.level = level;
		type = "Player";
		trail = new TrailParticles(new Color(Color.GREEN), level);
		hitbox = new Rectangle(x, y, 20, 20); 
	}

	public void render(Graphics g){
		trail.render(g);
		g.setColor(Color.GREEN);
		g.fillRect(x, y, hitbox.width, hitbox.height);
	}

	public void update(float delta){
		trail.update(delta);
		playerMovement();

		//Stop x-movement if not pressing LEFT nor RIGHT
		if(!Gdx.input.isKeyPressed(this.LEFT) && !Gdx.input.isKeyPressed(this.RIGHT)){
			velX = 0;
		}
		//Stop y-movement if not pressing UP nor DOWN
		if(!Gdx.input.isKeyPressed(this.UP) && !Gdx.input.isKeyPressed(this.DOWN)){
			velY = 0;
		}

		checkPortalCollision();

		hitbox.setX(this.x);
		hitbox.setY(this.y);
	}

	public void checkPortalCollision(){
		for(int i = 0; i < level.portalSys.portals.size(); i++){
			Portal portal = level.portalSys.portals.get(i);
			if(this.isColliding(portal.hitbox, x, y)){
				if(portal.isActive() && portal.link != null && portal.canTeleport){
					//Teleport player
					this.x = portal.getLink().xPos;
					this.y = portal.getLink().yPos;
					teleportSound.play(0.8f);
					//Update the new window the player is in
					level.attachPlayerToWindow();
					//Start cooldown for both portals
					portal.canTeleport = false;
					portal.getLink().canTeleport = false;
				}
			}
		}
	}

	public void playerMovement(){
		//Move Left
		if(Gdx.input.isKeyPressed(this.LEFT)){
			velX = -moveSpeedX;
		}
		//Move Right
		if(Gdx.input.isKeyPressed(this.RIGHT)){
			velX = moveSpeedX;
		}
		//Move Up
		if(Gdx.input.isKeyPressed(this.UP)){
			velY = -moveSpeedY;
		}
		//Move Down
		if(Gdx.input.isKeyPressed(this.DOWN)){
			velY = moveSpeedY;
		}
		move();
	}

	/*
	 * Checks if there is a collision if the player was at the given position.
	 */
	public boolean isColliding(Rectangle other, float x, float y){
		if(other == this.hitbox){ //Make sure solid isn't stuck on itself
			return false;
		}
		if(x < other.x + other.width && x + hitbox.width > other.x && y < other.y + other.height && y + hitbox.height > other.y){
			return true;
		}
		return false;
	}

	/*
	 * Helper method for checking whether there is a collision if the player moves at the given position
	 */
	public boolean collisionExistsAt(float x, float y){
		for(int i = 0; i < level.solids.size(); i++){
			Rectangle solid = level.solids.get(i);
			if(isColliding(solid, x, y)){
				return true;
			}
		}
		return false;
	}

	public void move(){
		moveX();
		moveY();
	}

	public float distanceTo(Rectangle target){
		return ((float)Math.pow(Math.pow((target.y - this.y), 2.0) + Math.pow((target.x - this.x), 2.0), 0.5));
	}

	/*
	 * Move horizontally in the direction of the x-velocity vector. If there is a collision in
	 * this direction, step pixel by pixel up until the player hits the solid.
	 */
	public void moveX(){
		for(int i = 0; i < level.solids.size(); i++){
			Rectangle solid = level.solids.get(i);
			if(isColliding(solid, x + velX, y)){
				while(!isColliding(solid, x + Math.signum(velX), y)){
					x += Math.signum(velX);
				}
				velX = 0;
			}
		}
		x += velX;
	}

	/*
	 * Move vertically in the direction of the y-velocity vector. If there is a collision in
	 * this direction, step pixel by pixel up until the player hits the solid.
	 */
	public void moveY(){
		for(int i = 0; i < level.solids.size(); i++){
			Rectangle solid = level.solids.get(i);
			if(isColliding(solid, x, y + velY)){
				while(!isColliding(solid, x, y + Math.signum(velY))){
					y += Math.signum(velY);
				}
				velY = 0;
			}
		}
		y += velY;
	}

	//========================================Input Methods==============================================

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

}