package me.neildennis.drawmything.client.thread;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

import me.neildennis.drawmything.client.Main;
import me.neildennis.drawmything.client.utils.ChatUtils;
import me.neildennis.drawmything.server.packets.ChatPacket;
import me.neildennis.drawmything.server.packets.Packet;

public class NetworkThread{

	private Socket socket;

	private Accept accept;
	private Send send;

	private boolean running = true;

	public NetworkThread(Socket socket){
		this.socket = socket;
		accept = new Accept();
		send = new Send();
	}

	public void send(Packet packet){
		send.send(packet);
	}

	public class Accept extends Thread{

		private Accept(){
			start();
		}
		
		public void run(){
			ObjectInputStream ois;
			
			while (running){
				try {
					ois = new ObjectInputStream(socket.getInputStream());
					handlePacket((Packet) ois.readObject());
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		
		private void handlePacket(Packet packet){
			switch(packet.getType()){
			
			case CONNECT:
				break;
				
			case CHAT:
				ChatPacket cp = (ChatPacket)packet;
				ChatUtils.chat(cp.getMsg(), cp.getName());
				break;
			
			default:
				break;
			
			}
		}

	}

	public class Send extends Thread{

		private ConcurrentLinkedQueue<Packet> queue;

		private Send(){
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
						Main.getMain().log("sent a packet");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		private void send(Packet packet){
			queue.offer(packet);
			Main.getMain().log("offering the queue a packet");
		}

	}

}
