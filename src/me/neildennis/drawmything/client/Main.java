package me.neildennis.drawmything.client;

import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import me.neildennis.drawmything.client.managers.Manager;
import me.neildennis.drawmything.client.thread.GameThread;
import me.neildennis.drawmything.client.thread.GraphicsThread;

public class Main{

	private static Main main;

	private GraphicsThread gfxthread;
	private GameThread gamethread;

	private String username;

	private Main(){
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		File config = new File(System.getenv("APPDATA")+"/DrawMyThing/config.txt");

		if (config.exists()){

		} else {

		}

		username = JOptionPane.showInputDialog(null, "Enter an username", "test");
		//username = "Neil";
		if (username == null || username.equals("")){
			main = null;
			JOptionPane.showMessageDialog(null, "You need an username nigga", "Error", JOptionPane.ERROR_MESSAGE);
		} else {
			main = this;
			init();
		}
	}

	public static Main getMain(){
		return main;
	}

	public static void main(String[] args){
		new Main();
	}

	private void init(){
		try {
			Manager.init();
			gfxthread = new GraphicsThread();
			gamethread = new GameThread();
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}

	public void log(Object str){
		System.out.println("[" + Thread.currentThread().getName()+"] "+str);
	}

	public GraphicsThread getGfxThread(){
		return gfxthread;
	}

	public GameThread getGameThread(){
		return gamethread;
	}

	public String getUsername() {
		return username;
	}

}
