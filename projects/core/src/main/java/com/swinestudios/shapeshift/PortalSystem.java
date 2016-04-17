package com.swinestudios.shapeshift;

import java.util.ArrayList;
import org.mini2Dx.core.graphics.Graphics;

public class PortalSystem {

	public ArrayList<Portal> portals;
	public Gameplay level;
	
	public Portal waiting;
	
	public PortalSystem( Gameplay level ){
		portals = new ArrayList<Portal>();
		this.level=level;
	}
	
	public void update(float delta){
		waiting = null;
		for(Portal p : portals){
			p.update(delta);
		}
		for(Portal p : portals){
			
			
			if(p.isActive() && p.getLink()==null){
				if(waiting==null){
					waiting = p;
				}
				else{
					//System.out.println("LINKED");
					waiting.setLink(p);
					p.setLink(waiting);
					
					//Don't think this code matters anymore
					waiting.drawing = true;//This way only one of them draws
					p.drawing = false;
					
					waiting = null;
				}
			}
		}
	}
	
	public void render( Graphics g ){
		for(Portal p : portals){
			p.render(g);
		}
	}
	
	public void addPortal( Portal p ){
		portals.add(p);
	}
	
	
}
