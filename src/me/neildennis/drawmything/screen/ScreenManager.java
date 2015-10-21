package me.neildennis.drawmything.screen;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.SpringLayout;

public class ScreenManager {
	
	private static ArrayList<DrawComponent> components;
	private static boolean enabled = false;
	
	private static SpringLayout layout;
	
	private static DrawArea drawarea;
	private static ColorSelector colorsel;
	private static ChatArea chatarea;
	
	private static int height = 900;
	private static int width = height * 16/9;
	
	private ScreenManager(){}
	
	public static void init(JFrame frame){
		layout = new SpringLayout();
		
		frame.setSize(new Dimension(width, height));
		
		frame.setLayout(layout);
		
		components = new ArrayList<DrawComponent>();
		
		int drawbounds = (int) (width * 0.75);
		
		components.add(drawarea = new DrawArea(drawbounds-50, 630));
		components.add(colorsel = new ColorSelector(drawarea.getHeight()));
		
		layout.putConstraint(SpringLayout.WEST, drawarea, 0, SpringLayout.EAST, colorsel);
		
		for (Component c : components) frame.add(c);
		
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		components.add(chatarea = new ChatArea(drawbounds, frame.getContentPane().getHeight() - drawarea.getHeight()));
		layout.putConstraint(SpringLayout.NORTH, chatarea, 0, SpringLayout.SOUTH, drawarea);
		frame.add(chatarea);
		
		enabled = true;
	}
	
	public static ArrayList<DrawComponent> getComponents(){
		return components;
	}
	
	public static DrawArea getDrawArea(){
		return drawarea;
	}

	public static boolean isEnabled() {
		return enabled;
	}

}
