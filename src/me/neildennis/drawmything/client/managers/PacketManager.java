package me.neildennis.drawmything.client.managers;

import java.util.concurrent.ConcurrentLinkedQueue;

import me.neildennis.drawmything.client.exeptions.ShutdownException;
import me.neildennis.drawmything.server.packets.Packet;

public class PacketManager extends Manager implements Runnable{
	
	private Thread process;
	
	private ConcurrentLinkedQueue<Packet> queue;
	
	protected PacketManager(){
		queue = new ConcurrentLinkedQueue<Packet>();
		process = new Thread(this, "PacketManager");
		process.start();
	}
	
	public void run(){
		while (!Thread.currentThread().isInterrupted()){
			if (!queue.isEmpty()){
				queue.poll().handle();
			}
		}
	}
	
	public void handle(Packet packet){
		queue.offer(packet);
	}
	
	public void shutdown(){
		process.interrupt();
		try { process.join(2000); } catch (InterruptedException e) { e.printStackTrace(); }
		if (process.isAlive()) throw new ShutdownException("Thread still alive");
	}

}
