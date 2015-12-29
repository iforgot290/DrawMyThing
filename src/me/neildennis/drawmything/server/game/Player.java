package me.neildennis.drawmything.server.game;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.net.Socket;

import me.neildennis.drawmything.server.DrawServer;
import me.neildennis.drawmything.server.managers.NetworkManager.Accept;
import me.neildennis.drawmything.server.managers.NetworkManager.Send;
import me.neildennis.drawmything.server.packets.Packet;
import me.neildennis.drawmything.server.thread.GameThread;

public class Player {
	
	private GameThread game;
	
	private Socket socket;
	private Accept accept;
	private Send send;

	private String username;
	private BufferedImage pic;

	public Player(String username, Socket socket, Send send, Accept accept){
		this.socket = socket;
		this.username = username;
		this.send = send;
		this.accept = accept;
		game = GameThread.getThread();
	}

	public void setPic(int[] image, int width, int height){
		DataBufferInt buffer = new DataBufferInt(image, image.length);
		int[] bandMasks = {0xFF0000, 0xFF00, 0xFF, 0xFF000000};
		WritableRaster raster = Raster.createPackedRaster(buffer, width, height, width, bandMasks, null);
		ColorModel cm = ColorModel.getRGBdefault();
		pic = new BufferedImage(cm, raster, cm.isAlphaPremultiplied(), null);
	}

	public Accept getAccept(){
		return accept;
	}

	public Send getSend(){
		return send;
	}
	
	public synchronized Socket getSocket(){
		return socket;
	}

	public BufferedImage getPic(){
		return pic;
	}

	public void send(Packet packet){
		send.send(packet);
	}

	public String getName(){
		return username;
	}
	
	//TODO handle disconnects properly
	public void disconnect(String msg){
		DrawServer.log(username + " has disconnected: "+msg);
		game.removePlayer(this);
		accept.kill();
		send.kill();
	}
	
}
