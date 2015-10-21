package me.neildennis.drawmything.thread;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.concurrent.ConcurrentLinkedQueue;

import me.neildennis.drawmything.Main;
import me.neildennis.drawmything.screen.DrawArea;
import me.neildennis.drawmything.screen.ScreenManager;

public class GameThread extends Thread{

	private Main main;

	private int oldx, oldy;
	private ConcurrentLinkedQueue<Line2D> lines;
	private boolean running = true;

	public GameThread(){
		main = Main.getMain();
		lines = new ConcurrentLinkedQueue<Line2D>();
		start();
	}

	public void run(){

		while (!ScreenManager.isEnabled()) {
			try {
				Thread.sleep(1L);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}

		main.log("Starting game thread");

		final DrawArea drawarea = ScreenManager.getDrawArea();

		while (drawarea.getWidth() == 0){
			try {
				Thread.sleep(1L);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}

		//stroke(50, drawarea.pixels, drawarea.getWidth() / 2, drawarea.getHeight() / 2, drawarea.getWidth(), Color.GREEN);

		drawarea.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				oldx = e.getX();
				oldy = e.getY();

				queueLine(new Line2D.Double(oldx, oldy, oldx, oldy));
			}

		});

		drawarea.addMouseMotionListener(new MouseAdapter() {

			@Override
			public void mouseDragged(MouseEvent e) {
				int currentx = e.getX();
				int currenty = e.getY();

				if (currentx > drawarea.getWidth() || currenty > drawarea.getWidth() || currentx < 0 || currenty < 0) return;

				queueLine(new Line2D.Double(oldx, oldy, currentx, currenty));

				oldx = currentx;
				oldy = currenty;
			}

		});

		while (running){
			Line2D line;
			if ((line = lines.poll())!=null){
				int oldx = (int) line.getX1();
				int oldy = (int) line.getY1();
				int currentx = (int) line.getX2();
				int currenty = (int) line.getY2();

				Color color = Color.CYAN;


				//main.log("drawing line: "+oldx+","+oldy+" "+currentx+","+currenty+" color: "+colors.get(color));

				if (oldx - currentx == 0) {
					if (oldy <= currenty)
						for (int y = oldy; y <= currenty; y++){
							stroke(2, drawarea.pixels, oldx, y, drawarea.getWidth(), color);
						}
					else
						for (int y = currenty; y <= oldy; y++)
							stroke(2, drawarea.pixels, oldx, y, drawarea.getWidth(), color);
					continue;
				}

				float yvals = oldy - currenty;
				float xvals = oldx - currentx;
				float slope = yvals / xvals;
				int intercept = Math.round(-1*(slope*oldx-oldy));

				if (oldx <= currentx){
					for (int x = oldx; x <= currentx; x++){
						int y = Math.round(slope * x + intercept);
						stroke(2, drawarea.pixels, x, y, drawarea.getWidth(), color);
					}
				} else {
					for (int x = currentx; x <= oldx; x++){
						int y = Math.round(slope * x + intercept);
						stroke(2, drawarea.pixels, x, y, drawarea.getWidth(), color);
					}
				}

				if (slope == 0) continue;

				if (oldy < currenty)
					for (int y = oldy; y < currenty; y++){
						int x = Math.round((y-intercept)/slope);
						stroke(2, drawarea.pixels, x, y, drawarea.getWidth(), color);
					}
				else if (oldy > currenty)
					for (int y = currenty; y <= oldy; y++){
						int x = Math.round((y-intercept)/slope);
						stroke(2, drawarea.pixels, x, y, drawarea.getWidth(), color);
					}
			}
		}
	}

	public synchronized void queueLine(Line2D line){
		lines.offer(line);
	}

	public void stroke(int stroke, int[] pixels, int x, int y, int width, Color color){
		int c = getIntFromColor(color.getRed(), color.getGreen(), color.getBlue());

		int w = 2*stroke+2;
		int h = 2*stroke+2;

		int[] pix = new int[w*h];

		int midx = stroke + 1;
		int midy = stroke + 1;

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
				if (pix[fakepos]==c){
					if (realpos < pixels.length)
						pixels[realpos] = pix[fakepos];
				}
			}
		}
	}

	public int getIntFromColor(int Red, int Green, int Blue){
		Red = (Red << 16) & 0x00FF0000;
		Green = (Green << 8) & 0x0000FF00;
		Blue = Blue & 0x000000FF;

		return 0xFF000000 | Red | Green | Blue;
	}

	public boolean hasColor(int i){
		return i != 0;
	}

}
