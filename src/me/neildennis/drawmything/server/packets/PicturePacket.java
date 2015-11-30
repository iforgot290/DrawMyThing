package me.neildennis.drawmything.server.packets;

import me.neildennis.drawmything.server.game.Player;

public class PicturePacket extends Packet{
	
	private static final long serialVersionUID = -3422730640291234015L;
	private int[] image;
	public int width;
	public int height;
	
	public PicturePacket(int[] image, int width, int height){
		super(PacketType.PICTURE);
		this.image = image;
		this.width = width;
		this.height = height;
	}
	
	public int[] getImage(){
		return image;
	}
	
	public void handle(){
		
	}
	
	public void server(Player player){

	}

}
