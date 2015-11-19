package me.neildennis.drawmything.client.managers;

import static me.neildennis.drawmything.client.utils.DrawUtils.stroke;

import java.awt.Color;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import me.neildennis.drawmything.client.Main;
import me.neildennis.drawmything.client.exeptions.ShutdownException;
import me.neildennis.drawmything.client.game.Player;
import me.neildennis.drawmything.client.screen.DrawArea;

public class GameManager extends Manager implements Runnable{

	private Main main;
	private DrawArea drawarea;
	private Thread process;

	private ConcurrentLinkedQueue<Line2D> lines;

	private volatile Color color = Color.BLACK;
	private volatile int stroke = 2;
	private volatile boolean candraw = true;

	private volatile ArrayList<Player> players;

	public GameManager(){
		main = Main.getMain();
		main.log("Game Manager starting...");
		lines = new ConcurrentLinkedQueue<Line2D>();
		players = new ArrayList<Player>();
		process = new Thread(this, "GameManager");
		process.start();
	}

	@Override
	public void shutdown(){
		process.interrupt();
		try { process.join(2000); } catch (InterruptedException e) { e.printStackTrace(); }
		if (process.isAlive()) throw new ShutdownException("Thread still alive");
	}

	@Override
	public void run(){
		while (!process.isInterrupted()){
			if (Manager.getScreen()!=null && drawarea != null){
				handleLines();
			} else {
				try {
					drawarea = Manager.getScreen().getDrawArea();
					try {
						Thread.sleep(5L);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} catch (Exception e) {}
			}
		}
	}

	public ArrayList<Player> getPlayers(){
		return players;
	}

	public void queueLine(Line2D line){
		lines.offer(line);
	}

	public void setDrawColor(Color color) {
		this.color = color;
	}

	public void setStroke(int stroke){
		this.stroke = stroke;
	}

	public int getStroke() {
		return stroke;
	}

	public Color getColor() {
		return color;
	}

	public boolean canDraw(){
		return candraw;
	}

	private void handleLines(){
		Line2D line;
		if ((line = lines.poll())!=null){
			int oldx = (int) line.getX1();
			int oldy = (int) line.getY1();
			int currentx = (int) line.getX2();
			int currenty = (int) line.getY2();

			if (oldx - currentx == 0) {
				if (oldy <= currenty)
					for (int y = oldy; y <= currenty; y++){
						stroke(stroke, drawarea.pixels, oldx, y, drawarea.getWidth(), color, false);
					}
				else
					for (int y = currenty; y <= oldy; y++)
						stroke(stroke, drawarea.pixels, oldx, y, drawarea.getWidth(), color, false);
				return;
			}

			float yvals = oldy - currenty;
			float xvals = oldx - currentx;
			float slope = yvals / xvals;
			int intercept = Math.round(-1*(slope*oldx-oldy));

			if (oldx <= currentx){
				for (int x = oldx; x <= currentx; x++){
					int y = Math.round(slope * x + intercept);
					stroke(stroke, drawarea.pixels, x, y, drawarea.getWidth(), color, false);
				}
			} else {
				for (int x = currentx; x <= oldx; x++){
					int y = Math.round(slope * x + intercept);
					stroke(stroke, drawarea.pixels, x, y, drawarea.getWidth(), color, false);
				}
			}

			if (slope == 0) return;

			if (oldy < currenty)
				for (int y = oldy; y < currenty; y++){
					int x = Math.round((y-intercept)/slope);
					stroke(stroke, drawarea.pixels, x, y, drawarea.getWidth(), color, false);
				}
			else if (oldy > currenty)
				for (int y = currenty; y <= oldy; y++){
					int x = Math.round((y-intercept)/slope);
					stroke(stroke, drawarea.pixels, x, y, drawarea.getWidth(), color, false);
				}

		}
	}

}
