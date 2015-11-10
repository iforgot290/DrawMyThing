package me.neildennis.drawmything.client.managers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.neildennis.drawmything.client.exeptions.DrawException;
import me.neildennis.drawmything.server.packets.Packet;

public class NetworkManager extends Manager{
	
	private ExecutorService service;
	
	private Socket tcpsocket;
	private PacketManager pman;
	
	protected NetworkManager(String host, int port) throws UnknownHostException, IOException{
		tcpsocket = new Socket(host, port);
		pman = Manager.getPacketManager();
		
		service = Executors.newFixedThreadPool(4);
		service.execute(new TcpAccept());
	}
	
	@Override
	public void shutdown() throws DrawException {
		service.shutdownNow();
	}
	
	private class TcpAccept implements Runnable{
		
		private ObjectInputStream ois;
		
		public void run(){
			while (!Thread.currentThread().isInterrupted()){
				try {
					ois = new ObjectInputStream(tcpsocket.getInputStream());
					pman.handle((Packet) ois.readObject());
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

}
