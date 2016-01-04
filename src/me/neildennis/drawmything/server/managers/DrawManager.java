package me.neildennis.drawmything.server.managers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DrawManager extends ServManager{

	private ExecutorService service;
	private DatagramSocket socket;
	
	private UdpSend send;
	private HashMap<InetAddress, Integer> addrs;
	
	protected DrawManager(int port) throws SocketException{
		socket = new DatagramSocket(port);
		service = Executors.newCachedThreadPool();
		
		addrs = new HashMap<InetAddress, Integer>();
		
		service.execute(send = new UdpSend());
		service.execute(new UdpAccept());
	}
	
	@Override
	public void shutdown() {
		service.shutdownNow();
	}
	
	private class UdpAccept implements Runnable{
		
		public void run(){
			DatagramPacket packet;
			byte[] buffer;
			
			while(!Thread.currentThread().isInterrupted()){
				buffer = new byte[100];
				packet = new DatagramPacket(buffer, buffer.length);
				try {
					socket.receive(packet);
					handle(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		private void handle(DatagramPacket packet){
			String data = new String(packet.getData(), StandardCharsets.UTF_8);
			if (data.startsWith("hi")){
				addrs.put(packet.getAddress(), packet.getPort());
			} else if (data.startsWith("bye")){
				addrs.remove(packet.getAddress());
			} else {
				send.queue.offer(packet);
			}
		}
		
	}
	
	private class UdpSend implements Runnable{
		
		private ConcurrentLinkedQueue<DatagramPacket> queue;
		
		private UdpSend(){
			queue = new ConcurrentLinkedQueue<DatagramPacket>();
		}
		
		public void run(){
			while(!Thread.currentThread().isInterrupted()){
				if (!queue.isEmpty()){
					DatagramPacket packet = queue.poll();
					
					for (InetAddress addr : addrs.keySet()){
						packet.setAddress(addr);
						packet.setPort(addrs.get(addr));
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

}
