package me.neildennis.drawmything.client.shapes;

import java.awt.Color;
import java.awt.image.BufferedImage;

import me.neildennis.drawmything.client.utils.DrawUtils;

public class Circle {

	private BufferedImage image;
	
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
		
		if (color == Color.WHITE)
			DrawUtils.antiAliasBorder(radius, 1, image, (int) (width/2), (int) (height/2), Color.BLACK, true);
		else
			DrawUtils.antiAlias(radius, image, (int) (width/2), (int) (height/2), color, true);
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
		DrawUtils.antiAliasBorder(radius, 2, image, (int) (width / 2), (int) (height / 2), Color.BLACK, false);
	}

	public void reset(){
		if (color == Color.WHITE)
			DrawUtils.antiAliasBorder(radius, 1, image, (int) (width/2), (int) (height/2), Color.BLACK, true);
		else
			DrawUtils.antiAlias(radius, image, (int) (width/2), (int) (height/2), color, true);
	}
	
	public void outline(){
		DrawUtils.antiAliasBorder(radius, 1, image, (int) (width / 2), (int) (height / 2), color, true);
	}

}
