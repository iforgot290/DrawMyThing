package me.neildennis.drawmything;

import me.neildennis.drawmything.thread.GameThread;
import me.neildennis.drawmything.thread.GraphicsThread;

public class Main {
	
	private static Main main;
	
	private GraphicsThread gfxthread;
	private GameThread gamethread;
	
	private Main(){
		main = this;
	}
	
	public static Main getMain(){
		return main;
	}
	
	public static void main(String[] args){
		Main main = new Main();
		main.init();
	}
	
	private void init(){
		gfxthread = new GraphicsThread();
		gamethread = new GameThread();
	}

	public void log(Object str) {
		System.out.println("[" + Thread.currentThread().getName()+"] "+str);
	}

}
