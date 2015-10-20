package me.neildennis.drawmything.thread;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
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
		queueLine(new Line2D.Double(100, 100, 110, 200));

		drawarea.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				oldx = e.getX();
				oldy = e.getY();

				main.log(oldx+","+oldy);

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

				main.log("drawing line: "+oldx+","+oldy+" "+currentx+","+currenty);

				if (oldx - currentx == 0) {
					if (oldy <= currenty)
						for (int y = oldy; y <= currenty; y++){
							stroke(2, drawarea.pixels, oldx, y, drawarea.getWidth());
						}
					else
						for (int y = currenty; y <= oldy; y++)
							stroke(2, drawarea.pixels, oldx, y, drawarea.getWidth());
					continue;
				}

				float slope = (oldy - currenty) / (oldx - currentx);
				int intercept = Math.round(-1*(slope*oldx-oldy));

				if (oldx <= currentx){
					for (int x = oldx; x <= currentx; x++){
						int y = Math.round(slope * x + intercept);
						stroke(2, drawarea.pixels, x, y, drawarea.getWidth());
					}
					
					if (slope == 0) continue;
					
					if (oldy < currenty)
						for (int y = oldy; y < currenty; y++){
							int x = Math.round((y-intercept)/slope);
							stroke(2, drawarea.pixels, x, y, drawarea.getWidth());
						}
					else if (oldy > currenty)
						for (int y = currenty; y <= oldy; y++){
							int x = Math.round((y-intercept)/slope);
							stroke(2, drawarea.pixels, x, y, drawarea.getWidth());
						}
				} else {
					for (int x = currentx; x <= oldx; x++){
						int y = Math.round(slope * x + intercept);
						stroke(2, drawarea.pixels, x, y, drawarea.getWidth());
					}
					
					if (slope == 0) continue;
					
					if (oldy < currenty)
						for (int y = oldy; y < currenty; y++){
							int x = Math.round((y-intercept)/slope);
							stroke(2, drawarea.pixels, x, y, drawarea.getWidth());
						}
					else if (oldy > currenty)
						for (int y = currenty; y <= oldy; y++){
							int x = Math.round((y-intercept)/slope);
							stroke(2, drawarea.pixels, x, y, drawarea.getWidth());
						}
				}
			}
		}
	}

	public synchronized void queueLine(Line2D line){
		lines.offer(line);
	}

	public void stroke(int stroke, int[] pixels, int x, int y, int width){
		int pos = (x + 0) + (y + 0) * width;
		if (pos < pixels.length) pixels[pos] = 0xff00ff;

		pos = (x + 0) + (y + 1) * width;
		if (pos < pixels.length) pixels[pos] = 0xff00ff;

		pos = (x + 0) + (y - 1) * width;
		if (pos < pixels.length) pixels[pos] = 0xff00ff;

		pos = (x + 1) + (y + 0) * width;
		if (pos < pixels.length) pixels[pos] = 0xff00ff;

		pos = (x - 1) + (y + 0) * width;
		if (pos < pixels.length) pixels[pos] = 0xff00ff;
	}

}
