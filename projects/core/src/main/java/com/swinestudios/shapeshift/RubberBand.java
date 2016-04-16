package com.swinestudios.shapeshift;

import org.mini2Dx.core.geom.Circle;
import org.mini2Dx.core.geom.Rectangle;
import org.mini2Dx.core.graphics.Graphics;

import com.badlogic.gdx.InputProcessor;

public class RubberBand implements InputProcessor{

	public final int INITIAL_WIDTH = 300;
	public final int INITIAL_HEIGHT = 200;

	public float x, y;

	public boolean isActive;
	public boolean isResizing;

	public Rectangle hitbox, tempBox;
	public Circle topLeftCorner, topRightCorner, bottomLeftCorner, bottomRightCorner, currentCorner;

	public Gameplay level;
	public String type;

	public RubberBand(float x, float y, Gameplay level){
		this.x = x;
		this.y = y;
		isActive = true;
		isResizing = false;
		this.level = level;
		type = "RubberBand";

		//TODO replace with icons/sprites
		topLeftCorner = new Circle(x, y, 8);
		topRightCorner = new Circle(x + INITIAL_WIDTH, y, 8);
		bottomLeftCorner = new Circle(x, y + INITIAL_HEIGHT, 8);
		bottomRightCorner = new Circle(x + INITIAL_WIDTH, y + INITIAL_HEIGHT, 8);
		currentCorner = null;

		hitbox = new Rectangle(x, y, INITIAL_WIDTH, INITIAL_HEIGHT);
		tempBox = new Rectangle(x, y, INITIAL_WIDTH, INITIAL_HEIGHT);
	}

	public void render(Graphics g){
		if(isActive){
			//g.drawRect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
			//g.drawRect(tempBox.x, tempBox.y, tempBox.width, tempBox.height);
			drawCorners(g);
		}
	}

	public void update(float delta){
		if(isActive){
			hitbox.setX(x);
			hitbox.setY(y);
		}
	}
	
	//TODO switch to sprites for corners later
	public void drawCorners(Graphics g){
		g.drawCircle(topLeftCorner.getX(), topLeftCorner.getY(), (int) topLeftCorner.getRadius());
		g.drawCircle(topRightCorner.getX(), topRightCorner.getY(), (int) topRightCorner.getRadius());
		g.drawCircle(bottomLeftCorner.getX(), bottomLeftCorner.getY(), (int) bottomLeftCorner.getRadius());
		g.drawCircle(bottomRightCorner.getX(), bottomRightCorner.getY(), (int) bottomRightCorner.getRadius());
	}

	//========================================Input Methods==============================================
	@Override
	public boolean keyDown(int keycode){
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
	public boolean touchDown(int mouseX, int mouseY, int pointer, int button){
		if(topLeftCorner.getDistanceTo(mouseX, mouseY) <= topLeftCorner.getRadius()){
			isResizing = true;
			currentCorner = topLeftCorner;
			System.out.println("Clicked");
		}
		else if(topRightCorner.getDistanceTo(mouseX, mouseY) <= topRightCorner.getRadius()){
			isResizing = true;
			currentCorner = topRightCorner;
			System.out.println("Clicked");
		}
		else if(bottomLeftCorner.getDistanceTo(mouseX, mouseY) <= bottomLeftCorner.getRadius()){
			isResizing = true;
			currentCorner = bottomLeftCorner;
			System.out.println("Clicked");
		}
		else if(bottomRightCorner.getDistanceTo(mouseX, mouseY) <= bottomRightCorner.getRadius()){
			isResizing = true;
			currentCorner = bottomRightCorner;
			System.out.println("Clicked");
		}
		return false;
	}

	@Override
	public boolean touchUp(int mouseX, int mouseY, int pointer, int button){
		isResizing = false;
		return false;
	}

	@Override
	public boolean touchDragged(int mouseX, int mouseY, int pointer){
		if(isResizing){
			if(currentCorner == topLeftCorner){
				topLeftCorner.setCenter(mouseX, mouseY);
				bottomLeftCorner.setX(mouseX);
				topRightCorner.setY(mouseY);
			}
			else if(currentCorner == topRightCorner){
				topRightCorner.setCenter(mouseX, mouseY);
				bottomRightCorner.setX(mouseX);
				topLeftCorner.setY(mouseY);
			}
			else if(currentCorner == bottomLeftCorner){
				bottomLeftCorner.setCenter(mouseX, mouseY);
				topLeftCorner.setX(mouseX);
				bottomRightCorner.setY(mouseY);
			}
			else if(currentCorner == bottomRightCorner){
				bottomRightCorner.setCenter(mouseX, mouseY);
				topRightCorner.setX(mouseX);
				bottomLeftCorner.setY(mouseY);
			}
		}
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
