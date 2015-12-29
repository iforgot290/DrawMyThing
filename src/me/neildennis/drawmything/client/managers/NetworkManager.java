package me.neildennis.drawmything.client.managers;

import java.awt.Color;
import java.awt.geom.Line2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.neildennis.drawmything.client.Main;
import me.neildennis.drawmything.client.exeptions.DrawException;
import me.neildennis.drawmything.server.packets.ConnectPacket;
import me.neildennis.drawmything.server.packets.Packet;
import me.neildennis.drawmything.server.packets.Packet.PacketType;

public class NetworkManager extends Manager{

	private ExecutorService service;

	private Socket tcpsocket;
	private DatagramSocket udpsocket;
	private PacketManager pman;
	private GameManager game;

	private InetAddress host;
	private int port;

	private TcpSend tcp;
	private UdpSend udp;

	protected NetworkManager(InetAddress host, int port) throws UnknownHostException, IOException, ClassNotFoundException{
		this.host = host;
		this.port = port;
		tcpsocket = new Socket(host, port);
		udpsocket = new DatagramSocket();
		pman = Manager.getPacketManager();

		connect();

		service = Executors.newFixedThreadPool(4);
		service.execute(new TcpAccept());
		service.execute(tcp = new TcpSend());
		service.execute(new UdpAccept());
		service.execute(udp = new UdpSend());
		
		String hi = "hi";
		udp.queue.offer(new DatagramPacket(hi.getBytes(), hi.getBytes().length, host, port));
	}

	private void connect() throws IOException, ClassNotFoundException{
		ObjectOutputStream oos = new ObjectOutputStream(tcpsocket.getOutputStream());
		oos.writeObject(new ConnectPacket(true, Main.getMain().getUsername()));

		ObjectInputStream ois = new ObjectInputStream(tcpsocket.getInputStream());
		ConnectPacket cp = (ConnectPacket)ois.readObject();

		if (cp.isConnecting()){

			boolean connecting = true;

			while (connecting){
				ois = new ObjectInputStream(tcpsocket.getInputStream());
				Packet packet = (Packet) ois.readObject();

				if (packet.getType()==PacketType.PLAYERINFO) packet.handle();
				else connecting = false;
			}
		}
	}

	@Override
	public void shutdown() throws DrawException {
		udp.queue.offer(new DatagramPacket("bye".getBytes(), "bye".getBytes().length, host, port));
		service.shutdownNow();
	}

	public void send(Packet packet){
		tcp.queue.offer(packet);
	}

	public void send(Line2D line, Color color, int stroke){
		String strline = line.getX1()+":"+line.getY1()+":"+line.getX2()+":"+line.getY2()+":"+stroke+":"+color.getRGB()+":";
		udp.queue.offer(new DatagramPacket(strline.getBytes(), strline.getBytes().length, host, port));
	}

	private class TcpAccept implements Runnable{

		private ObjectInputStream ois;

		public void run(){
			Thread.currentThread().setName("TcpAccept");

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

	private class TcpSend implements Runnable{

		private ConcurrentLinkedQueue<Packet> queue;
		private ObjectOutputStream oos;

		private TcpSend(){
			queue = new ConcurrentLinkedQueue<Packet>();
		}

		public void run(){
			Thread.currentThread().setName("TcpSend");

			while (!Thread.currentThread().isInterrupted()){
				if (!queue.isEmpty()){
					try {
						oos = new ObjectOutputStream(tcpsocket.getOutputStream());
						oos.writeObject(queue.poll());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private class UdpAccept implements Runnable{

		private byte[] buffer;
		private DatagramPacket packet;

		public void run(){
			Thread.currentThread().setName("UdpAccept");

			while (!Thread.currentThread().isInterrupted()){
				buffer = new byte[100];
				packet = new DatagramPacket(buffer, buffer.length);
				if (game != null){
					try {
						udpsocket.receive(packet);
						String data = new String(packet.getData(), StandardCharsets.UTF_8);
						String[] args = data.split(":");
						Line2D line = new Line2D.Double(Double.parseDouble(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]));
						game.setStroke(Integer.valueOf(args[4]));
						try {
							game.setDrawColor(new Color(Integer.valueOf(args[5])));
						} catch (Exception e){
							e.printStackTrace();
						}
						game.queueLine(line);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					game = Manager.getGameManager();
					try {
						Thread.sleep(5L);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private class UdpSend implements Runnable{

		private ConcurrentLinkedQueue<DatagramPacket> queue;

		private UdpSend(){
			queue = new ConcurrentLinkedQueue<DatagramPacket>();
		}

		public void run(){
			Thread.currentThread().setName("UdpSend");

			while (!Thread.currentThread().isInterrupted()){
				if (!queue.isEmpty()){
					try {
						udpsocket.send(queue.poll());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
			while (!queue.isEmpty()){
				try {
					udpsocket.send(queue.poll());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
