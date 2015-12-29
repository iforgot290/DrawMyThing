package me.neildennis.drawmything.server;

import java.io.IOException;

import me.neildennis.drawmything.server.game.Player;
import me.neildennis.drawmything.server.managers.ServManager;
import me.neildennis.drawmything.server.packets.Packet;
import me.neildennis.drawmything.server.thread.GameThread;

public class DrawServer {
	
	private static DrawServer main;
	
	private GameThread game;
	
	public DrawServer(){
		main = this;
		init();
	}
	
	public static void main(String[] args){
		new DrawServer();
	}
	
	public static DrawServer getServer(){
		return main;
	}
	
	public void init(){
		try {
			game = new GameThread();
			ServManager.init();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void log(Object str){
		System.out.println("[" + Thread.currentThread().getName() + "] "+str);
	}
	
	public void broadcast(Packet packet){
		for (Player player : game.getPlayers())
			player.send(packet);
	}
	
	public void broadcast(Packet packet, Player exclude){
		for (Player player : game.getPlayers())
			if (player != exclude)
				player.send(packet);
	}

}
