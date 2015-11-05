package me.neildennis.drawmything.client.screen;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.SpringLayout;

import me.neildennis.drawmything.client.Main;

public class ScreenManager {
	
	private ArrayList<DrawComponent> components;
	private static ScreenManager manager;
	
	private SpringLayout layout;
	
	private DrawArea drawarea;
	private ColorSelector colorsel;
	private ChatArea chatarea;
	private PlayerInfo playerinfo;
	
	private static int height = 900;
	private static int width = height * 16/9;
	
	private ScreenManager(){}
	
	public static void init(JFrame frame){
		new ScreenManager().privinit(frame);
	}
	
	private void privinit(JFrame frame){
		@SuppressWarnings("unused")
		Main main = Main.getMain();
		
		layout = new SpringLayout();
		
		frame.setSize(new Dimension(width, height));
		
		frame.setLayout(layout);
		
		components = new ArrayList<DrawComponent>();
		
		int drawbounds = (int) (width * 0.75);
		
		components.add(drawarea = new DrawArea(drawbounds-50, 630));
		components.add(colorsel = new ColorSelector(drawarea.getHeight()));
		
		for (Component c : components) frame.add(c);
		
		layout.putConstraint(SpringLayout.WEST, drawarea, 0, SpringLayout.EAST, colorsel);
		
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		components.add(chatarea = new ChatArea(drawbounds, frame.getContentPane().getHeight() - drawarea.getHeight()));
		layout.putConstraint(SpringLayout.NORTH, chatarea, 0, SpringLayout.SOUTH, drawarea);
		frame.add(chatarea);
		
		components.add(playerinfo = new PlayerInfo(frame.getContentPane().getWidth() - drawbounds, frame.getContentPane().getHeight()));
		layout.putConstraint(SpringLayout.WEST, playerinfo, 0, SpringLayout.EAST, drawarea);
		frame.add(playerinfo);
		
		manager = this;
	}
	
	public ArrayList<DrawComponent> getComponents(){
		return components;
	}
	
	public DrawArea getDrawArea(){
		return drawarea;
	}

	public boolean isEnabled() {
		return manager != null;
	}

	public ChatArea getChat() {
		return chatarea;
	}
	
	public PlayerInfo getPlayerInfo(){
		return playerinfo;
	}
	
	public static ScreenManager getManager(){
		while (manager == null){
			try {
				Thread.sleep(1L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		return manager;
	}

}
