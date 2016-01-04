package me.neildennis.drawmything.server.packets;

import me.neildennis.drawmything.server.game.Player;

public class PlayerInfoPacket extends Packet{

	private static final long serialVersionUID = -1982651109455931498L;
	private String name;
	
	public PlayerInfoPacket(Player player){
		super(PacketType.PLAYERINFO);
		name = player.getName();
	}
	
	public String getName(){
		return name;
	}
	
	public void handle(){
		
	}
	
	public void server(Player player){

	}

}
