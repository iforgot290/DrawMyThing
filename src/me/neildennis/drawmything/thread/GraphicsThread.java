package me.neildennis.drawmything.thread;

import javax.swing.JFrame;

import me.neildennis.drawmything.Main;
import me.neildennis.drawmything.screen.DrawComponent;
import me.neildennis.drawmything.screen.ScreenManager;

public class GraphicsThread extends Thread{
	
	private Main main;
	private boolean running = true;
	
	private JFrame frame;
	
	public GraphicsThread(){
		main = Main.getMain();
		start();
	}
	
	public void run(){
		main.log("Starting gfx thread");
		
		frame = new JFrame();
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("DrawMyThing");
		
		ScreenManager.init();
		
		for (DrawComponent c : ScreenManager.getComponents()){
			frame.add(c);
		}
		
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		while (running){
			for (DrawComponent c : ScreenManager.getComponents()) c.update();
			for (DrawComponent c : ScreenManager.getComponents()) c.render();
			/*try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}*/
		}
		
		main.log("Stopping gfx thread");
	}
	
	public void kill(){
		running = false;
	}

}
