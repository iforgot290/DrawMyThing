package me.neildennis.drawmything.client.managers;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import me.neildennis.drawmything.client.exeptions.DrawException;

public abstract class Manager {

	private static PacketManager packetman;
	private static NetworkManager networkman;
	private static GameManager gameman;
	private static ScreenManager screenman;
	private static GraphicsManager gfxman;

	public abstract void shutdown() throws DrawException;

	public static PacketManager getPacketManager(){
		return packetman;
	}
	
	public static NetworkManager getNetworkManager(){
		return networkman;
	}
	
	public static GameManager getGameManager(){
		return gameman;
	}
	
	public static ScreenManager getScreen(){
		return screenman;
	}
	
	public static GraphicsManager getGfx(){
		return gfxman;
	}

	public static void init() throws UnknownHostException, ClassNotFoundException, IOException{
		packetman = new PacketManager();
		networkman = new NetworkManager(InetAddress.getByName("127.0.0.1"), 8080);
		gameman = new GameManager();
		screenman = new ScreenManager();
		gfxman = new GraphicsManager();
	}

}
