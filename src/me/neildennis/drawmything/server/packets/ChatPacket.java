package me.neildennis.drawmything.server.packets;

public class ChatPacket extends Packet {

	private static final long serialVersionUID = -1325056563259932685L;
	private String name;
	private String msg;
	
	public ChatPacket(String msg, String name){
		super(PacketType.CHAT);
		this.name = name;
		this.msg = msg;
	}
	
	public String getName(){
		return name;
	}
	
	public String getMsg(){
		return msg;
	}
	
}
