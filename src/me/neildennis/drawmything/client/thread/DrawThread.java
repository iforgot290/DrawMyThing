package me.neildennis.drawmything.client.thread;

import java.awt.Color;
import java.awt.geom.Line2D;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DrawThread{
	
	private boolean running = true;
	
	private DatagramSocket socket;
	private Accept accept;
	private Send send;
	
	public DrawThread(DatagramSocket socket){
		this.socket = socket;
		accept = new Accept();
		send = new Send();
	}
	
	public void send(Line2D line, Color color, int stroke){
		String strline = line.getX1()+":"+line.getY1()+":"+line.getX2()+":"+line.getY2()+":"+stroke+":"+color.hashCode();
		try {
			send.queue.offer(new DatagramPacket(strline.getBytes(), strline.getBytes().length, InetAddress.getByName("localhost"), 8080));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	private class Accept extends Thread{
		
		public Accept(){
			
		}
		
	}
	
	private class Send extends Thread{
		
		private ConcurrentLinkedQueue<DatagramPacket> queue;
		
		public Send(){
			queue = new ConcurrentLinkedQueue<DatagramPacket>();
			start();
		}
		
		public void run(){
			while (running){
				if (!queue.isEmpty()){
					try {
						socket.send(queue.poll());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
	}

}
