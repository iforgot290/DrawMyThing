package me.neildennis.drawmything.server.thread;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ConcurrentLinkedQueue;

import me.neildennis.drawmything.server.game.Player;

public class DrawBroadcastThread {
	
	private boolean running = true;
	private GameThread game;
	
	private DatagramSocket socket;
	private Accept accept;
	private Send send;
	
	public DrawBroadcastThread(DatagramSocket socket){
		game = GameThread.getThread();
		this.socket = socket;
		send = new Send();
		accept = new Accept();
	}
	
	public void kill(){
		running = false;
		socket.close();
		try {
			accept.join(2000);
			send.join(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void send(DatagramPacket packet){
		send.queue.offer(packet);
	}
	
	private class Accept extends Thread {
		
		public Accept(){
			start();
		}
		
		public void run(){
			DatagramPacket packet;
			byte[] buffer;
			while (running){
				buffer = new byte[100];
				packet = new DatagramPacket(buffer, buffer.length);
				try {
					socket.receive(packet);
					send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	private class Send extends Thread {
		
		private ConcurrentLinkedQueue<DatagramPacket> queue;
		
		private Send(){
			queue = new ConcurrentLinkedQueue<DatagramPacket>();
			start();
		}
		
		public void run(){
			DatagramPacket packet;
			while (running){
				for (Player player : game.getPlayers()){
					packet = queue.poll();
					packet.setAddress(player.getSocket().getInetAddress());
					packet.setPort(8080);
					try {
						socket.send(packet);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
	}

}
