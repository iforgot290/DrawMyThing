package me.neildennis.drawmything.server.managers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DrawManager extends ServManager{

	private ExecutorService service;
	private DatagramSocket socket;
	
	private UdpSend send;
	private ArrayList<Client> addrs;
	
	protected DrawManager(int port) throws SocketException{
		socket = new DatagramSocket(port);
		service = Executors.newCachedThreadPool();
		
		addrs = new ArrayList<Client>();
		
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
				addrs.add(new Client(packet.getAddress(), packet.getPort()));
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
					
					for (Client client : addrs){
						packet.setAddress(client.addr);
						packet.setPort(client.port);
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
	
	private class Client{
		public InetAddress addr;
		public int port;
		
		public Client(InetAddress addr, int port){
			this.addr = addr;
			this.port = port;
		}
	}

}
