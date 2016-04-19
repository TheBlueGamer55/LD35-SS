package com.swinestudios.shapeshift;

import org.mini2Dx.core.geom.Circle;
import org.mini2Dx.core.geom.Rectangle;
import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.core.graphics.Sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

public class RubberBand implements InputProcessor{
	
	//public static int COUNT = 0;
	//public int ID;

	public final int INITIAL_WIDTH = 96;
	public final int INITIAL_HEIGHT = 96;
	public final int MIN_WIDTH = 48;
	public final int MIN_HEIGHT = 48;
	public static final int TILE_SIZE = 16;
	public final int BORDER_OFFSET = 16;

	public float x, y;

	public boolean isActive;
	public boolean isResizing;
	public boolean containsPlayer;
	public boolean containsBoundingBlock; //Used to make sure window cannot resize past bounding blocks

	public Player containedPlayer;

	public Rectangle hitbox, tempBox;
	public Block topBorder, bottomBorder, leftBorder, rightBorder;
	public Circle topLeftCorner, topRightCorner, bottomLeftCorner, bottomRightCorner, currentCorner;
	
	public Sprite cornerSprite;
	public static Color borderColor = new Color(178/255f, 0/255f, 255/255f, 1);

	public Gameplay level;
	public String type;

	public RubberBand(float x, float y, Gameplay level){
		this.x = x;
		this.y = y;
		isActive = true;
		isResizing = false;
		containsPlayer = false;
		containedPlayer = null;
		containsBoundingBlock = false;
		this.level = level;
		type = "RubberBand";

		topLeftCorner = new Circle(x, y, 8);
		topRightCorner = new Circle(x + INITIAL_WIDTH, y, 8);
		bottomLeftCorner = new Circle(x, y + INITIAL_HEIGHT, 8);
		bottomRightCorner = new Circle(x + INITIAL_WIDTH, y + INITIAL_HEIGHT, 8);
		currentCorner = null;
		
		cornerSprite = new Sprite(new Texture(Gdx.files.internal("window_corner.png")));
		cornerSprite.scale(1);

		hitbox = new Rectangle(x, y, INITIAL_WIDTH, INITIAL_HEIGHT);
		tempBox = new Rectangle(x, y, INITIAL_WIDTH, INITIAL_HEIGHT);
		
		topBorder = new Block(x, y - BORDER_OFFSET, INITIAL_WIDTH, BORDER_OFFSET, level);
		bottomBorder = new Block(x, y + INITIAL_HEIGHT, INITIAL_WIDTH, BORDER_OFFSET, level);
		leftBorder = new Block(x - BORDER_OFFSET, y, BORDER_OFFSET, INITIAL_HEIGHT, level);
		rightBorder = new Block(x + INITIAL_WIDTH, y, BORDER_OFFSET, INITIAL_HEIGHT, level);
		level.solids.add(topBorder);
		level.solids.add(bottomBorder);
		level.solids.add(leftBorder);
		level.solids.add(rightBorder);
	}

	public void render(Graphics g){
		if(isActive){
			//g.drawRect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
			drawBorders(g);
			//g.setColor(borderColor);
			//g.drawRect(tempBox.x, tempBox.y, tempBox.width, tempBox.height);
			drawCorners(g);
		}
	}

	public void update(float delta){
		if(isActive){
			if(containedPlayer != null){
				//System.out.println("Window " + ID + " has player");
				checkPlayerCollision();
			}
			hitbox.setX(x);
			hitbox.setY(y);
		}
	}

	public void checkPlayerCollision(){
		containsPlayer = tempBox.contains(level.player.hitbox);
	}
	
	public void checkBoundsCollision(){
		for(int i = 0; i < level.boundingSolids.size(); i++){
			BoundingBlock block = level.boundingSolids.get(i);
			if(isColliding(tempBox, block)){
				containsBoundingBlock = true;
				break;
			}
		}
	}
	
	private void drawBorders(Graphics g){
		g.setColor(borderColor);
		g.drawRect(tempBox.x, tempBox.y, tempBox.width, 2); //Top
		g.drawRect(bottomLeftCorner.getX(), bottomLeftCorner.getY(), tempBox.width, 2); //Bottom
		g.drawRect(tempBox.x, tempBox.y, 2, tempBox.height); //Left
		g.drawRect(topRightCorner.getX(), tempBox.y, 2, tempBox.height); //Right
	}
	
	private void realignBorders(){
		float x = tempBox.x;
		float y = tempBox.y;
		topBorder.set(x, y - BORDER_OFFSET, tempBox.width, BORDER_OFFSET);
		bottomBorder.set(x, y + tempBox.height, tempBox.width, BORDER_OFFSET);
		leftBorder.set(x - BORDER_OFFSET, y, BORDER_OFFSET, tempBox.height);
		rightBorder.set(x + tempBox.width, y, BORDER_OFFSET, tempBox.height);
	}

	public void drawCorners(Graphics g){
		/*g.drawCircle(topLeftCorner.getX(), topLeftCorner.getY(), (int) topLeftCorner.getRadius());
		g.drawCircle(topRightCorner.getX(), topRightCorner.getY(), (int) topRightCorner.getRadius());
		g.drawCircle(bottomLeftCorner.getX(), bottomLeftCorner.getY(), (int) bottomLeftCorner.getRadius());
		g.drawCircle(bottomRightCorner.getX(), bottomRightCorner.getY(), (int) bottomRightCorner.getRadius());
		*/
		
		float w = cornerSprite.getWidth();
		float h = cornerSprite.getHeight();
		g.drawSprite(cornerSprite, topLeftCorner.getX() - w/2, topLeftCorner.getY() - h/2);
		g.drawSprite(cornerSprite, topRightCorner.getX() - w/2, topRightCorner.getY() - h/2);
		g.drawSprite(cornerSprite, bottomLeftCorner.getX() - w/2, bottomLeftCorner.getY() - h/2);
		g.drawSprite(cornerSprite, bottomRightCorner.getX() - w/2, bottomRightCorner.getY() - h/2);
	}

	private void realignCorners(){
		topLeftCorner.setCenter(tempBox.x, tempBox.y);
		topRightCorner.setCenter(tempBox.x + tempBox.width, tempBox.y);
		bottomLeftCorner.setCenter(tempBox.x, tempBox.y + tempBox.height);
		bottomRightCorner.setCenter(tempBox.x + tempBox.width, tempBox.y + tempBox.height);
	}
	
	/*
	 * Checks if there is a collision with another Rectangle
	 */
	public boolean isColliding(Rectangle r1, Rectangle r2){
		if(r1 == r2){ //Make sure solid isn't stuck on itself
			return false;
		}
		if(r1.x < r2.x + r2.width && r1.x + r1.width > r2.x && r1.y < r2.y + r2.height && r1.y + r1.height > r2.y){
			return true;
		}
		return false;
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
		if(level.paused || level.gameOver || level.gameWin){
			return false;
		}
		mouseX = (mouseX / TILE_SIZE) * TILE_SIZE;
		mouseY = (mouseY / TILE_SIZE) * TILE_SIZE;
		Rectangle prevBox = new Rectangle(tempBox.x, tempBox.y, tempBox.width, tempBox.height);
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
			realignBorders();
			if(containedPlayer != null){
				checkPlayerCollision();
				if(!containsPlayer){
					tempBox.set(prevBox.x, prevBox.y, prevBox.width, prevBox.height);
					realignCorners();
					realignBorders();
				}
			}
			checkBoundsCollision();
			if(containsBoundingBlock){
				tempBox.set(prevBox.x, prevBox.y, prevBox.width, prevBox.height);
				realignCorners();
				realignBorders();
				containsBoundingBlock = false;
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
