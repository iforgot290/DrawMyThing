package me.neildennis.drawmything.client.managers;

import javax.swing.JFrame;

import me.neildennis.drawmything.client.Main;
import me.neildennis.drawmything.client.exeptions.ShutdownException;
import me.neildennis.drawmything.client.screen.DrawComponent;

public class GraphicsManager extends Manager implements Runnable{
	
	private Main main;
	private Thread process;
	
	private JFrame frame;
	private ScreenManager screen;
	
	protected GraphicsManager(){
		main = Main.getMain();
		screen = Manager.getScreen();
		frame = screen.getFrame();
		process = new Thread(this, "GfxManager");
		process.start();
	}
	
	public void run(){
		main.log("Starting gfx thread...");
		
		long lastTime = System.nanoTime();
		double delta = 0.0;
		double ns = 1000000000.0 / 60.0;
		long timer = System.currentTimeMillis();
		int updates = 0;
		int frames = 0;
		
		while (!process.isInterrupted()){
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
	
	@Override
	public void shutdown(){
		process.interrupt();
		try { process.join(2000); } catch (InterruptedException e) { e.printStackTrace(); }
		if (process.isAlive()) throw new ShutdownException("Thread still alive");
	}

}
