package me.neildennis.drawmything.server.packets;

import java.io.Serializable;

import me.neildennis.drawmything.server.game.Player;

public abstract class Packet implements Serializable{

	private static final long serialVersionUID = 3359816311617999602L;
	private PacketType type;
	
	public Packet(PacketType type){
		this.type = type;
	}
	
	public abstract void handle();
	public abstract void server(Player player);
	
	public PacketType getType(){
		return type;
	}
	
	public enum PacketType{
		CONNECT,
		PICTURE,
		PLAYERINFO,
		CHAT;
	}
	
}
