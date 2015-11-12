package me.neildennis.drawmything.server.game;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentLinkedQueue;

import me.neildennis.drawmything.server.DrawServer;
import me.neildennis.drawmything.server.packets.ChatPacket;
import me.neildennis.drawmything.server.packets.Packet;
import me.neildennis.drawmything.server.thread.GameThread;

public class Player {
	
	private GameThread game;
	private DrawServer server;

	private Socket socket;
	private Accept accept;
	private Send send;
	private boolean running = true;

	private String username;
	private BufferedImage pic;

	public Player(String username, Socket socket){
		this.socket = socket;
		this.username = username;
		server = DrawServer.getServer();
		game = GameThread.getThread();
	}

	public void setPic(int[] image, int width, int height){
		DataBufferInt buffer = new DataBufferInt(image, image.length);
		int[] bandMasks = {0xFF0000, 0xFF00, 0xFF, 0xFF000000};
		WritableRaster raster = Raster.createPackedRaster(buffer, width, height, width, bandMasks, null);
		ColorModel cm = ColorModel.getRGBdefault();
		pic = new BufferedImage(cm, raster, cm.isAlphaPremultiplied(), null);
	}

	public void init(){
		accept = new Accept();
		send = new Send();
	}

	public Accept getAccept(){
		return accept;
	}

	public Send getSend(){
		return send;
	}
	
	public synchronized Socket getSocket(){
		return socket;
	}

	public BufferedImage getPic(){
		return pic;
	}

	public void sendPacket(Packet packet){
		send.send(packet);
	}

	public String getName(){
		return username;
	}
	
	public void disconnect(String msg){
		DrawServer.getServer().log(username + " has disconnected: "+msg);
		running = false;
		game.removePlayer(this);
	}

	private class Accept extends Thread{

		public Accept(){
			start();
		}

		public void run(){
			ObjectInputStream ois;
			
			while (running){
				try {
					ois = new ObjectInputStream(socket.getInputStream());
					handlePacket((Packet) ois.readObject());
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}  catch (SocketException e){
					disconnect(e.getMessage());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		private void handlePacket(Packet packet){
			switch (packet.getType()){
			
			case CONNECT:
				break;
				
			case CHAT:
				ChatPacket cp = (ChatPacket)packet;
				server.broadcast(new ChatPacket(cp.getMsg(), cp.getName()), Player.this);
				break;
			
			default: break;
			
			}
		}

	}

	private class Send extends Thread{

		private ConcurrentLinkedQueue<Packet> queue;

		public Send(){
			queue = new ConcurrentLinkedQueue<Packet>();
			start();
		}

		public void run(){
			ObjectOutputStream oos;

			while (running){
				try {
					if (!queue.isEmpty()){
						oos = new ObjectOutputStream(socket.getOutputStream());
						oos.writeObject(queue.poll());
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				try {
					Thread.sleep(1L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		public void send(Packet packet){
			queue.offer(packet);
		}

	}

}
