package me.neildennis.drawmything.server.packets;

import java.awt.image.BufferedImage;

import me.neildennis.drawmything.server.game.Player;

public class PlayerInfoPacket extends Packet{

	private static final long serialVersionUID = -1982651109455931498L;
	private BufferedImage image;
	private String name;
	
	public PlayerInfoPacket(Player player){
		super(PacketType.PLAYERINFO);
		image = player.getPic();
		name = player.getName();
	}
	
	public BufferedImage getImage(){
		return image;
	}
	
	public String getName(){
		return name;
	}
	
	public void handle(){
		
	}
	
	public void server(Player player){

	}

}
