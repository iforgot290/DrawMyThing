package me.neildennis.drawmything.shapes;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import me.neildennis.drawmything.utils.DrawUtils;

public class Circle {

	private BufferedImage image;
	private int[] pixels;
	
	private Color color;
	private int width, height;
	private int radius;

	public Circle(int radius, Color color){
		this(radius, radius * 2 + 5, radius * 2 + 5, color);
	}

	public Circle(int radius, int width, Color color){
		this(radius, width, radius * 2 + 5, color);
	}

	public Circle(int radius, int width, int height, Color color){
		this.width = width;
		this.height = height;
		this.color = color;
		this.radius = radius;
		
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
		
		if (color == Color.WHITE)
			DrawUtils.circleBorder(radius, 1, pixels, (int) (width/2), (int) (height/2), width, Color.BLACK, true);
		else
			DrawUtils.stroke(radius, pixels, (int) (width/2), (int) (height/2), width, color, true);
	}

	public BufferedImage getImage() {
		return image;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public Color getColor() {
		return color;
	}

	public void overlay(){
		DrawUtils.circleBorder(radius, 2, pixels, (int) (width / 2), (int) (height / 2), width, Color.BLACK, false);
	}

	public void reset(){
		for (int i = 0; i < pixels.length; i++) pixels[i] = 0xffffff;
		
		if (color == Color.WHITE)
			DrawUtils.circleBorder(radius, 1, pixels, (int) (width/2), (int) (height/2), width, Color.BLACK, true);
		else
			DrawUtils.stroke(radius, pixels, (int) (width/2), (int) (height/2), width, color, true);
	}
	
	public void outline(){
		for (int i = 0; i < pixels.length; i++) pixels[i] = 0xffffff;
		
		DrawUtils.circleBorder(radius, 1, pixels, (int) (width/2), (int) (height/2), width, color, true);
	}

}
