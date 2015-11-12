package me.neildennis.drawmything.client;

import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.net.Socket;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import me.neildennis.drawmything.client.managers.Manager;
import me.neildennis.drawmything.client.thread.DrawThread;
import me.neildennis.drawmything.client.thread.GameThread;
import me.neildennis.drawmything.client.thread.GraphicsThread;
import me.neildennis.drawmything.client.thread.NetworkThread;
import me.neildennis.drawmything.server.packets.ConnectPacket;
import me.neildennis.drawmything.server.packets.Packet;
import me.neildennis.drawmything.server.packets.Packet.PacketType;

public class Main{

	private static Main main;

	private GraphicsThread gfxthread;
	private GameThread gamethread;
	private NetworkThread netthread;
	private DrawThread drawthread;

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

			Manager.init();
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
	
	public DrawThread getDrawThread(){
		return drawthread;
	}

	public String getUsername() {
		return username;
	}

}
