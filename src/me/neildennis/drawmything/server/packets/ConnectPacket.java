package me.neildennis.drawmything.server.packets;

import me.neildennis.drawmything.server.game.Player;

public class ConnectPacket extends Packet {

	private static final long serialVersionUID = 3812843008614396635L;
	private String data;
	private boolean connecting;
	
	public ConnectPacket(boolean connecting, String data) {
		super(PacketType.CONNECT);
		this.data = data;
		this.connecting = connecting;
	}
	
	public boolean isConnecting(){
		return connecting;
	}
	
	public String getData(){
		return data;
	}
	
	public void handle(){
		
	}
	
	public void server(Player player){
		
	}

}
