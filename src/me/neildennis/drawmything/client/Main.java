package me.neildennis.drawmything.client;

import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import me.neildennis.drawmything.client.managers.Manager;

public class Main{

	private static Main main;

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
			return;
		}
		
		main = this;

		try {
			Manager.init();
		}  catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}

	public static Main getMain(){
		return main;
	}

	public static void main(String[] args){
		new Main();
	}

	public void log(Object str){
		System.out.println("[" + Thread.currentThread().getName()+"] "+str);
	}

	public String getUsername() {
		return username;
	}

}
