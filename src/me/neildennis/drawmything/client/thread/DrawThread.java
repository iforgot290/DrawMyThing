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

import me.neildennis.drawmything.client.Main;

public class DrawThread{

	private boolean running = true;
	private GameThread game;

	private DatagramSocket socket;
	private Accept accept;
	private Send send;

	public DrawThread(DatagramSocket socket){
		this.socket = socket;
		accept = new Accept();
		send = new Send();
		game = Main.getMain().getGameThread();
	}

	public void send(Line2D line, Color color, int stroke){
		String strline = line.getX1()+":"+line.getY1()+":"+line.getX2()+":"+line.getY2()+":"+stroke+":"+color.hashCode();
		try {
			send.queue.offer(new DatagramPacket(strline.getBytes(), strline.getBytes().length, InetAddress.getByName("localhost"), 8080));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public void kill(){
		running = false;
		socket.close();
		try {
			accept.join(2000);
			send.join(2000);
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	private class Accept extends Thread{

		public Accept(){
			start();
		}

		public void run(){
			byte[] buffer;
			DatagramPacket packet;
			while (running){
				buffer = new byte[100];
				packet = new DatagramPacket(buffer, buffer.length);
				try {
					socket.receive(packet);
					String[] args = packet.getData().toString().split(":");
					Line2D line = new Line2D.Double(Double.parseDouble(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]));
					game.setStroke(Integer.valueOf(args[4]));
					game.setDrawColor(new Color(Integer.valueOf(args[5])));
					game.queueLine(line);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
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
