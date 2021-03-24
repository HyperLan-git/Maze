package fr.hyper.labyrinth;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class Jeu extends JPanel {
	public static final int WIDTH = 90, HEIGHT = 47; 
	private Case[][] cases = new Case[WIDTH][HEIGHT];
	private Player p;
	private List<Point2D> playerPos = new ArrayList<>();
	public Jeu() {
		reset();
	}
	
	public void reset() {
		for(int x = 0; x < WIDTH; x++)
			for(int y = 0; y < HEIGHT; y++)
				cases[x][y] = new Case(x*20, y*20, (y==0 && x==0), (y == HEIGHT-1 && x == WIDTH-1));
		Case currentCase = cases[0][0];
		while(currentCase != null) {
			List<Case> possibleCases = new ArrayList<Case>();
			for(Case c : getCasesAround(currentCase))
				if(c != null && !c.isVisited())
					possibleCases.add(c);
			Case newCase = (possibleCases.size()>0)?possibleCases.get((int)(Math.random()*possibleCases.size())):null;
			currentCase.setVisited(true);
			if(newCase == null)
				currentCase = currentCase.getLastCaseVisited();
			else {
				if(currentCase.x > newCase.x) {
					newCase.rightBlocked = false;
					currentCase.leftBlocked = false;
				} else if(currentCase.x < newCase.x) {
					newCase.leftBlocked = false;
					currentCase.rightBlocked = false;
				} else if(currentCase.y > newCase.y) {
					currentCase.upBlocked = false;
					newCase.downBlocked = false;
				} else if(currentCase.y < newCase.y) {
					currentCase.downBlocked = false;
					newCase.upBlocked = false;
				}
				newCase.setLastCaseVisited(currentCase);
				currentCase = newCase;
			}
		}
		p = new Player(cases);
	}

	public Case[] getCasesAround(Case c) {
		Case[] result = new Case[4];
		if(c.x/20<WIDTH-1)
			result[0] = cases[c.x/20+1][c.y/20];
		if(c.x/20>0)
			result[1] = cases[c.x/20-1][c.y/20];
		if(c.y/20<HEIGHT-1)
			result[2] = cases[c.x/20][c.y/20+1];
		if(c.y/20>0)
			result[3] = cases[c.x/20][c.y/20-1];
		return result;
	}
	
	public void update() {
		p.update();
		playerPos.add(new Point((int)p.getX(), (int)p.getY()));
	}

	@Override
	public void paint(Graphics graphics) {
		super.paint(graphics);
		for(Case[] ligne : cases)
			for(Case c : ligne)
				c.paint(graphics);
		p.paint(graphics);
		graphics.setColor(Color.black);
		for(int i = 0; i < playerPos.size()-1; i++) {
			Point2D p1 = playerPos.get(i),
					p2 = playerPos.get(i+1);
			graphics.drawLine((int)p1.getX(), (int)p1.getY(), (int)p2.getX(), (int)p2.getY());
		}
	}

	public void registerKeyEvent(KeyEvent e) {
		p.onKeyEvent(e);
	}
	
	public Player getPlayer() {
		return p;
	}
}