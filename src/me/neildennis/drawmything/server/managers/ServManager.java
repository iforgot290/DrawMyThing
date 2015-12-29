package me.neildennis.drawmything.server.managers;

import java.io.IOException;

public abstract class ServManager {
	
	private static NetworkManager netman;
	
	public abstract void shutdown();
	
	public static void init() throws IOException{
		netman = new NetworkManager(8080);
	}
	
	public static NetworkManager getNetwork(){
		return netman;
	}

}
