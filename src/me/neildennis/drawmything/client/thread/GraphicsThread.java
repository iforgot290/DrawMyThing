package me.neildennis.drawmything.client.thread;

import javax.swing.JFrame;

import me.neildennis.drawmything.client.Main;
import me.neildennis.drawmything.client.managers.Manager;
import me.neildennis.drawmything.client.managers.ScreenManager;
import me.neildennis.drawmything.client.screen.DrawComponent;

public class GraphicsThread extends Thread{
	
	private Main main;
	private boolean running = true;
	
	private JFrame frame;
	private ScreenManager screen;
	
	public GraphicsThread(){
		main = Main.getMain();
		screen = Manager.getScreen();
		frame = screen.getFrame();
		start();
	}
	
	public void run(){
		main.log("Starting gfx thread");
		
		long lastTime = System.nanoTime();
		double delta = 0.0;
		double ns = 1000000000.0 / 60.0;
		long timer = System.currentTimeMillis();
		int updates = 0;
		int frames = 0;
		
		while (running){
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			
			if (delta >= 1.0) {
				for (DrawComponent c : screen.getComponents()) c.tick();
				updates++;
				delta--;
			}
			
			for (DrawComponent c : screen.getComponents()) c.render();
			
			frames++;
			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				frame.setTitle("DrawMyThing | "+frames+" fps "+updates+" ups");
				updates = 0;
				frames = 0;
			}
		}
		
		main.log("Stopping gfx thread");
	}
	
	public void kill(){
		running = false;
	}

}
