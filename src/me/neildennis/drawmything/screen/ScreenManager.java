package me.neildennis.drawmything.screen;

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
	
	private static int height = 900;
	private static int width = height * 16/9;
	
	private ScreenManager(){}
	
	public static void init(JFrame frame){
		layout = new SpringLayout();
		frame.setLayout(layout);
		
		frame.setSize(new Dimension(width, height));
		
		components = new ArrayList<DrawComponent>();
		
		int drawbounds = (int) (width * 0.75);
		
		components.add(drawarea = new DrawArea(drawbounds-50, 630));
		components.add(colorsel = new ColorSelector(drawarea.getHeight()));
		
		layout.putConstraint(SpringLayout.WEST, drawarea, 0, SpringLayout.EAST, colorsel);
		
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
