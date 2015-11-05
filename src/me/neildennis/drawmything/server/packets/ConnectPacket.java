package me.neildennis.drawmything.server.packets;

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

}
