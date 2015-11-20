package me.neildennis.drawmything.server;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;

import me.neildennis.drawmything.server.game.Player;
import me.neildennis.drawmything.server.packets.Packet;
import me.neildennis.drawmything.server.thread.ConnectThread;
import me.neildennis.drawmything.server.thread.DrawBroadcastThread;
import me.neildennis.drawmything.server.thread.GameThread;

public class DrawServer {
	
	private static DrawServer main;
	
	private GameThread game;
	private ConnectThread connect;
	private DrawBroadcastThread udp;
	
	private ServerSocket serversock;
	
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
			serversock = new ServerSocket(8080);
			log("Starting server on port 8080");
			
			game = new GameThread();
			connect = new ConnectThread(serversock);
			udp = new DrawBroadcastThread(new DatagramSocket(8080));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void log(Object str){
		System.out.println("[" + Thread.currentThread().getName() + "] "+str);
	}
	
	public ServerSocket getServerSock(){
		return serversock;
	}
	
	public ConnectThread getConnect(){
		return connect;
	}
	
	public void broadcast(Packet packet){
		for (Player player : game.getPlayers())
			player.sendPacket(packet);
	}
	
	public void broadcast(Packet packet, Player exclude){
		for (Player player : game.getPlayers())
			if (player != exclude)
				player.sendPacket(packet);
	}

}
