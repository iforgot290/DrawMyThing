package me.neildennis.drawmything.screen;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class DrawArea extends DrawComponent{

	private static final long serialVersionUID = 1L;
	
	private BufferStrategy bs;
	private Graphics g;
	private int height = 300;
	private int width = height * 16/9;
	

	private BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	public volatile int[] pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
	
	public DrawArea(){
		this.setPreferredSize(new Dimension(width, height));
	}
	
	public void render(){
		bs = this.getBufferStrategy();
		if (bs == null){
			this.createBufferStrategy(3);
			return;
		}
		g = bs.getDrawGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);
		g.drawImage(image, 0, 0, width, height, null);
		g.dispose();
		bs.show();
	}
	
	public void update(){
		
	}

}
