package com.swinestudios.shapeshift;

import org.mini2Dx.core.geom.Circle;
import org.mini2Dx.core.geom.Rectangle;
import org.mini2Dx.core.graphics.Graphics;

import com.badlogic.gdx.InputProcessor;

public class RubberBand implements InputProcessor{

	public final int INITIAL_WIDTH = 300;
	public final int INITIAL_HEIGHT = 200;
	public final int MIN_WIDTH = 32;
	public final int MIN_HEIGHT = 32;

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
			g.drawRect(tempBox.x, tempBox.y, tempBox.width, tempBox.height);
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
	
	private void realignCorners(){
		topLeftCorner.setCenter(tempBox.x, tempBox.y);
		topRightCorner.setCenter(tempBox.x + tempBox.width, tempBox.y);
		bottomLeftCorner.setCenter(tempBox.x, tempBox.y + tempBox.height);
		bottomRightCorner.setCenter(tempBox.x + tempBox.width, tempBox.y + tempBox.height);
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
		}
		else if(topRightCorner.getDistanceTo(mouseX, mouseY) <= topRightCorner.getRadius()){
			isResizing = true;
			currentCorner = topRightCorner;
		}
		else if(bottomLeftCorner.getDistanceTo(mouseX, mouseY) <= bottomLeftCorner.getRadius()){
			isResizing = true;
			currentCorner = bottomLeftCorner;
		}
		else if(bottomRightCorner.getDistanceTo(mouseX, mouseY) <= bottomRightCorner.getRadius()){
			isResizing = true;
			currentCorner = bottomRightCorner;
		}
		return false;
	}

	@Override
	public boolean touchUp(int mouseX, int mouseY, int pointer, int button){
		isResizing = false;
		realignCorners();
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
			float nextX = topLeftCorner.getX();
			float nextY = topLeftCorner.getY();
			float nextWidth = topRightCorner.getX() - topLeftCorner.getX();
			float nextHeight = bottomLeftCorner.getY() - topLeftCorner.getY();
			if(nextWidth >= MIN_WIDTH && nextHeight >= MIN_HEIGHT){
				tempBox.set(nextX, nextY, nextWidth, nextHeight);
			}
			else{ 
				if(nextWidth < MIN_WIDTH && nextHeight < MIN_HEIGHT){
					if(currentCorner == topLeftCorner){
						tempBox.set(bottomRightCorner.getX() - MIN_WIDTH, bottomRightCorner.getY() - MIN_HEIGHT, MIN_WIDTH, MIN_HEIGHT);
					}
					else if(currentCorner == topRightCorner){
						tempBox.set(bottomLeftCorner.getX(), bottomLeftCorner.getY() - MIN_HEIGHT, MIN_WIDTH, MIN_HEIGHT);
					}
					else if(currentCorner == bottomLeftCorner){
						tempBox.set(topRightCorner.getX() - MIN_WIDTH, topRightCorner.getY(), MIN_WIDTH, MIN_HEIGHT);
					}
					else if(currentCorner == bottomRightCorner){
						tempBox.set(nextX, nextY, MIN_WIDTH, MIN_HEIGHT);
					}
				}
				else if(nextWidth < MIN_WIDTH){
					if(currentCorner == topRightCorner || currentCorner == bottomRightCorner){
						tempBox.set(tempBox.x, nextY, MIN_WIDTH, nextHeight);
					}
					else if(currentCorner == topLeftCorner || currentCorner == bottomLeftCorner){
						tempBox.set(topRightCorner.getX() - MIN_WIDTH, nextY, MIN_WIDTH, nextHeight);
					}
				}
				else if(nextHeight < MIN_HEIGHT){
					if(currentCorner == bottomLeftCorner || currentCorner == bottomRightCorner){
						tempBox.set(nextX, tempBox.y, nextWidth, MIN_HEIGHT);
					}
					else if(currentCorner == topLeftCorner || currentCorner == topRightCorner){
						tempBox.set(nextX, bottomLeftCorner.getY() - MIN_HEIGHT, nextWidth, MIN_HEIGHT);
					}
				}
			}
			realignCorners();
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
