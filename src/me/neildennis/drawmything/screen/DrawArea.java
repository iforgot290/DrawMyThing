package me.neildennis.drawmything.screen;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import me.neildennis.drawmything.Main;

public class DrawArea extends DrawComponent{

	private static final long serialVersionUID = 1L;
	private Main main;
	
	private BufferStrategy bs;
	private Graphics g;
	private int height;
	private int width;

	private BufferedImage image;
	public volatile int[] pixels;
	private int oldx, oldy;
	
	public DrawArea(int height){
		this(height * 16/9, height);
	}
	
	public DrawArea(int width, int height){
		main = Main.getMain();
		
		this.height = height;
		this.width = width;
		this.setSize(new Dimension(width, height));
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
		
		clear();
		registerClicks();
	}
	
	public void render(){
		bs = this.getBufferStrategy();
		if (bs == null){
			this.createBufferStrategy(3);
			return;
		}
		g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, width, height, null);
		g.dispose();
		bs.show();
	}
	
	public void update(){
		
	}
	
	public void clear(){
		for (int i = 0; i < pixels.length; i++){
			pixels[i] = Color.WHITE.hashCode();
		}
	}
	
	private void registerClicks(){
		
		addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				oldx = e.getX();
				oldy = e.getY();

				if (e.getButton()==1)
					main.getGameThread().queueLine(new Line2D.Double(oldx, oldy, oldx, oldy));
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				oldx = e.getX();
				oldy = e.getY();
			}

		});
		
		addMouseMotionListener(new MouseAdapter() {

			@Override
			public void mouseDragged(MouseEvent e) {
				int currentx = e.getX();
				int currenty = e.getY();

				if (currentx > getWidth() || currenty > getWidth() || currentx < 0 || currenty < 0) return;

				main.getGameThread().queueLine(new Line2D.Double(oldx, oldy, currentx, currenty));

				oldx = currentx;
				oldy = currenty;
			}

		});
		
	}

}
