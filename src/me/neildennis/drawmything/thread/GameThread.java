package me.neildennis.drawmything.thread;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Iterator;

import me.neildennis.drawmything.Main;
import me.neildennis.drawmything.screen.DrawArea;
import me.neildennis.drawmything.screen.ScreenManager;

public class GameThread extends Thread{

	private Main main;

	@SuppressWarnings("unused")
	private int oldx, oldy;

	public GameThread(){
		main = Main.getMain();
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

		ScreenManager.getDrawArea().addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				oldx = e.getX();
				oldy = e.getY();

				DrawArea drawarea = ScreenManager.getDrawArea();
				drawarea.pixels[e.getX() + e.getY() * drawarea.getWidth()] = 0xff00ff;
			}

		});

		ScreenManager.getDrawArea().addMouseMotionListener(new MouseAdapter() {

			@Override
			public void mouseDragged(MouseEvent e) {
				int currentx = e.getX();
				int currenty = e.getY();
				
				DrawArea drawarea = ScreenManager.getDrawArea();

				Line2D line = new Line2D.Float(oldx, oldy, currentx, currenty);
				
				oldx = currentx;
				oldy = currenty;
			}

			public void stroke(int stroke, int[] pixels, int x, int y, int width){
				int pos = x + y * width;
				if (pos < pixels.length){
					pixels[pos] = 0xff00ff;
				}
			}

		});
	}

}
