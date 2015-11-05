package me.neildennis.drawmything.client.utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class DrawUtils {

	public static void stroke(int stroke, int[] pixels, int x, int y, int width, Color color, boolean background){
		if (background){
			for (int i = 0; i < pixels.length; i++){
				pixels[i] = 0x00FFFFFF;
			}
		}

		int c = getIntFromColor(color);

		int w = 2*stroke+1;
		int h = 2*stroke+1;

		int[] pix = new int[w*h];

		int midx = stroke;
		int midy = stroke;

		for (int i = 0; i < 360; i++){
			double rad = Math.toRadians(i) ;
			int cx = (int) Math.round(stroke*Math.cos(rad) + midx);
			int cy = (int) Math.round(stroke*Math.sin(rad) + midy);
			int pos = cx + cy * w;
			if (pos<pix.length) pix[pos] = c;
		}

		for (int iy = 0; iy < h; iy++){
			int minx = -1;
			int maxx = -1;
			for (int ix = 0; ix < w; ix++){
				int testc = pix[ix + iy * w];
				if (hasColor(testc)){
					if (minx == -1) minx = ix;
					if (ix > maxx) maxx = ix;
				}
			}

			for (int ix = 0; ix < w; ix++){
				if (minx>-1&&maxx>-1) {
					if (ix >= minx && ix <= maxx){
						pix[ix + iy * w] = c;
					}
				}
			}
		}

		int offx = x - stroke;
		int offy = y - stroke;

		for (int ix = 0; ix < w; ix++){
			for (int iy = 0; iy < h; iy++){
				int realx = offx + ix;
				int realy = offy + iy;

				int realpos = realx + realy * width;
				int fakepos = ix + iy * w;

				if (pix[fakepos]==c)
					if (realpos < pixels.length && realpos >= 0 && realx < width && realx > -1)
						pixels[realpos] = pix[fakepos];
			}
		}
	}

	public static void circleBorder(int radius, int stroke, int[] pixels, int x, int y, int width, Color color, boolean background){
		if (background){
			for (int i = 0; i < pixels.length; i++){
				pixels[i] = 0x00FFFFFF;
			}
		}

		int c = getIntFromColor(color);

		int w = 2*radius+1;
		int h = 2*radius+1;

		int[] pix = new int[w*h];

		int midx = radius;
		int midy = radius;

		for (int s = 0; s < stroke; s++){
			for (int i = 0; i < 360; i++){
				double rad = Math.toRadians(i) ;
				int cx = (int) Math.round((radius-s)*Math.cos(rad) + midx);
				int cy = (int) Math.round((radius-s)*Math.sin(rad) + midy);
				int pos = cx + cy * w;
				if (pos<pix.length) pix[pos] = c;
			}
		}

		int offx = x - radius;
		int offy = y - radius;

		for (int ix = 0; ix < w; ix++){
			for (int iy = 0; iy < h; iy++){
				int realx = offx + ix;
				int realy = offy + iy;

				int realpos = realx + realy * width;
				int fakepos = ix + iy * w;

				if (pix[fakepos]==c)
					if (realpos < pixels.length && realpos >= 0 && realx < width && realx > -1)
						pixels[realpos] = pix[fakepos];
			}
		}
	}

	public static void antiAlias(int radius, BufferedImage image, int x, int y, Color color, boolean background){
		Graphics2D g = image.createGraphics();

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		if (background){
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, image.getWidth(), image.getHeight());
		}

		g.setColor(color);
		g.fillOval(x - radius, y - radius, radius * 2, radius * 2);

		g.dispose();
	}

	public static void antiAliasBorder(int radius, int stroke, BufferedImage image, int x, int y, Color color, boolean background){
		Graphics2D g = image.createGraphics();

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		if (background){
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, image.getWidth(), image.getHeight());
		}

		g.setColor(color);
		g.setStroke(new BasicStroke(stroke));
		g.drawOval(x - radius, y - radius, radius * 2, radius * 2);

		g.dispose();
	}

	public static boolean hasColor(int i){
		return i != 0;
	}

	public static int getIntFromColor(int red, int green, int blue){
		red = (red << 16) & 0x00FF0000;
		green = (green << 8) & 0x0000FF00;
		blue = blue & 0x000000FF;

		return 0xFF000000 | red | green | blue;
	}

	public static int getIntFromColor(Color color){
		return getIntFromColor(color.getRed(), color.getGreen(), color.getBlue());
	}

}
