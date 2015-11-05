package me.neildennis.drawmything.client.screen;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import me.neildennis.drawmything.client.Main;
import me.neildennis.drawmything.client.shapes.Circle;
import me.neildennis.drawmything.client.utils.FileUtils;

public class ColorSelector extends DrawComponent{

	private static final long serialVersionUID = 1L;
	private Main main;
	
	private int width, height;
	private int btnoffset = 0;
	
	private BufferStrategy bs;
	private Graphics2D g;
	
	private BufferedImage clearbtn;
	private Circle strokebig;
	private Circle strokemed;
	private Circle strokesmall;
	
	private Circle[] colorbtns = new Circle[]{
			new Circle(18, 50, Color.BLACK),
			new Circle(18, 50, Color.GRAY),
			new Circle(18, 50, Color.WHITE),
			new Circle(18, 50, new Color(0xba00c1)), //purple
			new Circle(18, 50, new Color(0xfb83ff)), //pink
			new Circle(18, 50, Color.BLUE),
			//new Circle(18, 50, new Color(0x0090f5)), //light blue
			new Circle(18, 50, new Color(0x00a71c)), //green
			new Circle(18, 50, Color.RED),
			new Circle(18, 50, Color.ORANGE),
			new Circle(18, 50, Color.YELLOW),
			new Circle(18, 50, new Color(0x8B2500)) //brown
	};

	public ColorSelector(int height){
		this(50, height);
	}

	public ColorSelector(int width, int height){
		main = Main.getMain();
		
		this.width = width;
		this.height = height;
		this.setSize(new Dimension(width, height));
		
		clearbtn = FileUtils.loadImage("/res/clearbtn.png");
		
		strokebig = new Circle(14, 50, Color.BLACK);
		strokemed = new Circle(10, 50, Color.BLACK);
		strokesmall = new Circle(5, 50, Color.BLACK);
		
		strokesmall.reset();
		strokebig.outline();
		strokemed.outline();
		
		registerClicks();
	}
	
	public void render(){
		bs = this.getBufferStrategy();
		if (bs == null){
			this.createBufferStrategy(3);
			return;
		}
		g = (Graphics2D) bs.getDrawGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g.drawImage(clearbtn, 0, getHeight()-50, null);
		g.drawImage(strokebig.getImage(), 0, getHeight()-110, null);
		g.drawImage(strokemed.getImage(), 0, getHeight()-137, null);
		g.drawImage(strokesmall.getImage(), 0, getHeight()-156, null);
		
		for (int c = 0; c < colorbtns.length; c++){
			g.drawImage(colorbtns[c].getImage(), 0, colorbtns[c].getHeight() * c + btnoffset, null);
		}
		
		g.dispose();
		bs.show();
	}
	
	public void update(){
		
	}
	
	public void registerClicks(){
		this.addMouseListener(new MouseAdapter(){
			
			@Override
			public void mousePressed(MouseEvent e){
				int x = e.getX();
				int y = e.getY();
				
				if (isClearButton(x, y)){
					ScreenManager.getManager().getDrawArea().clear();
					return;
				}
				
				Color color = getColorClicked(x, y);
				if (color != null){
					main.getGameThread().setDrawColor(color);
					return;
				}
				
				int stroke = getStrokeClicked(x, y);
				if (stroke > 0) {
					main.getGameThread().setStroke(stroke);
					return;
				}
			}
			
		});
	}
	
	public boolean isClearButton(int x, int y){
		return y < getHeight() && y > getHeight()-50;
	}
	
	public Color getColorClicked(int x, int y){
		
		if (colorbtns.length>0 && y<=colorbtns[0].getHeight()*colorbtns.length+btnoffset && y>btnoffset){
			int area = (y-btnoffset) / colorbtns[0].getHeight();
			
			try {
				Circle circle = colorbtns[area];
				clearColorSelected();
				circle.overlay();
				return circle.getColor();
			} catch (IndexOutOfBoundsException e){
				return null;
			}
		}
		
		return null;
	}
	
	public void clearColorSelected(){
		for (Circle c : colorbtns) c.reset();
	}
	
	public int getStrokeClicked(int x, int y){
		
		if (y > getHeight() - 110 && y < (getHeight() - 110) + strokebig.getHeight()){
			strokebig.reset();
			strokemed.outline();
			strokesmall.outline();
			return 12;
		}
		
		if (y > getHeight() - 137 && y < (getHeight() - 137) + strokemed.getHeight()){
			strokemed.reset();
			strokebig.outline();
			strokesmall.outline();
			return 6;
		}
		
		if (y > getHeight() - 156 && y < (getHeight() - 156) + strokesmall.getHeight()){
			strokesmall.reset();
			strokebig.outline();
			strokemed.outline();
			return 2;
		}
		
		return 0;
	}

}
