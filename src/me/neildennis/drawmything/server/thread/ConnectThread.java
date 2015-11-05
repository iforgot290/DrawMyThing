package me.neildennis.drawmything.server.thread;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.neildennis.drawmything.server.DrawServer;
import me.neildennis.drawmything.server.game.Player;
import me.neildennis.drawmything.server.packets.ChatPacket;
import me.neildennis.drawmything.server.packets.ConnectPacket;
import me.neildennis.drawmything.server.packets.PlayerInfoPacket;

public class ConnectThread extends Thread{
	
	private DrawServer server;
	private GameThread game;
	
	private ServerSocket sock;
	private ExecutorService execute;
	
	private boolean running = true;
	
	public ConnectThread(ServerSocket sock){
		this.sock = sock;
		start();
	}
	
	public void run(){
		execute = Executors.newFixedThreadPool(3);
		
		while(GameThread.getThread() == null){
			try {
				Thread.sleep(1L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		server = DrawServer.getServer();
		game = GameThread.getThread();
		
		while (running){
			try {
				Socket socket = sock.accept();
				if (game.isGameRunning())
					execute.execute(new AcceptThread(socket, false));
				else
					execute.execute(new AcceptThread(socket, true));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class AcceptThread implements Runnable{
		
		private Socket socket;
		private boolean accept;
		
		public AcceptThread(Socket socket, boolean accept){
			AcceptThread.this.socket = socket;
			this.accept = accept;
		}
		
		public void run(){
			try {
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				ConnectPacket packet = (ConnectPacket) ois.readObject();
				
				if (!accept){
					ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
					oos.writeObject(new ConnectPacket(false, "Game already started"));
					socket.close();
					return;
				}
				
				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
				
				if (game.checkUsername(packet.getData())){
					oos.writeObject(new ConnectPacket(true, ""));
					Player player = new Player(packet.getData(), socket);
					
					/*ois = new ObjectInputStream(socket.getInputStream());
					PicturePacket picpack = (PicturePacket)ois.readObject();
					player.setPic(picpack.getImage(), picpack.width, picpack.height);*/
					
					game.addPlayer(player);
					
					for (Player p : game.getPlayers()){
						if (p != player){
							PlayerInfoPacket pinfo = new PlayerInfoPacket(p);
							oos = new ObjectOutputStream(socket.getOutputStream());
							oos.writeObject(pinfo);
						}
					}
					
					oos = new ObjectOutputStream(socket.getOutputStream());
					oos.writeObject(new ConnectPacket(true, ""));
					
					player.init();
					server.broadcast(new ChatPacket(player.getName() + " has joined the server", null));
				}
				
				else {
					oos.writeObject(new ConnectPacket(false, "Username already in use"));
					socket.close();
					return;
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
			DrawServer.getServer().log("end");
		}
		
	}

}
