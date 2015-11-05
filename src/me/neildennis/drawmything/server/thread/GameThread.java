package me.neildennis.drawmything.server.thread;

import java.util.ArrayList;

import me.neildennis.drawmything.server.game.Player;

public class GameThread {
	
	private static GameThread game;
	
	private boolean running;
	
	private volatile ArrayList<Player> players;
	
	public GameThread(){
		players = new ArrayList<Player>();
		running = false;
		
		game = this;
	}
	
	public static GameThread getThread(){
		return game;
	}
	
	public boolean isGameRunning(){
		return running;
	}
	
	/**
	 * Returns if username is available or not
	 * @param str username
	 * @return true if the username is available
	 */
	public boolean checkUsername(String str){
		for (Player p : players)
			if (p.getName().equalsIgnoreCase(str))
				return false;
		return true;
	}
	
	public void addPlayer(Player player){
		players.add(player);
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public void removePlayer(Player player) {
		players.remove(player);
	}

}
