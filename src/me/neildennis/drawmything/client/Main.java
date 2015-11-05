package me.neildennis.drawmything.client;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import me.neildennis.drawmything.client.thread.GameThread;
import me.neildennis.drawmything.client.thread.GraphicsThread;
import me.neildennis.drawmything.client.thread.NetworkThread;
import me.neildennis.drawmything.client.utils.FileUtils;
import me.neildennis.drawmything.server.packets.ConnectPacket;
import me.neildennis.drawmything.server.packets.Packet;
import me.neildennis.drawmything.server.packets.Packet.PacketType;
import me.neildennis.drawmything.server.packets.PicturePacket;

public class Main{

	private static Main main;

	private GraphicsThread gfxthread;
	private GameThread gamethread;
	private NetworkThread netthread;

	private String username;
	private Socket socket;

	private Main(){
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		File config = new File(System.getenv("APPDATA")+"/DrawMyThing/config.txt");
		
		if (config.exists()){
			
		} else {
			
		}
		
		username = JOptionPane.showInputDialog(null, "Enter an username", "test");
		//username = "Neil";
		if (username == null || username.equals("")){
			main = null;
			JOptionPane.showMessageDialog(null, "You need an username nigga", "Error", JOptionPane.ERROR_MESSAGE);
		} else {
			main = this;
			init();
		}
	}

	public static Main getMain(){
		return main;
	}

	public static void main(String[] args){
		new Main();
	}

	private void init(){
		try {
			gfxthread = new GraphicsThread();
			gamethread = new GameThread();
			
			socket = new Socket("127.0.0.1", 8080);
			
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(new ConnectPacket(true, username));
			
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			ConnectPacket cp = (ConnectPacket)ois.readObject();
			
			if (cp.isConnecting()){
				/*oos = new ObjectOutputStream(socket.getOutputStream());
				BufferedImage image = ImageIO.read(new File(FileUtils.loadSkypePic()));
				PicturePacket pp = new PicturePacket(((DataBufferInt)image.getRaster().getDataBuffer()).getData(), image.getWidth(), image.getHeight());
				oos.writeObject(pp);*/
				
				boolean connecting = true;
				
				while (connecting){
					ois = new ObjectInputStream(socket.getInputStream());
					Packet packet = (Packet) ois.readObject();
					
					if (packet.getType()==PacketType.PLAYERINFO){
						//handle
					} else {
						netthread = new NetworkThread(socket);
						connecting = false;
					}
				}
			} else {
				JOptionPane.showMessageDialog(null, cp.getData(), "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}

	public void log(Object str) {
		System.out.println("[" + Thread.currentThread().getName()+"] "+str);
	}

	public GraphicsThread getGfxThread(){
		return gfxthread;
	}

	public GameThread getGameThread(){
		return gamethread;
	}
	
	public NetworkThread getNetworkThread(){
		return netthread;
	}

	public String getUsername() {
		return username;
	}

}
