package me.neildennis.drawmything.client.managers;

import me.neildennis.drawmything.client.exeptions.DrawException;

public abstract class Manager {
	
	private static PacketManager packetman;
	
	public abstract void shutdown() throws DrawException;
	
	public static PacketManager getPacketManager(){
		return packetman;
	}
	
	public static void init(){
		packetman = new PacketManager();
	}
	
}
